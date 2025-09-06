import { useEffect, useState } from 'react'
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa'
import { Link } from 'react-router-dom';
import "./vacunas.css"

interface vacuna{
  id: number;
  nombre: String;
  stock: number;
  estado: String;
}

function Vacunas() {
  const [minimizado, setMinimizado] = useState(false);
  const [busqueda, setBusqueda] = useState("");
  const [vacunas, setVacunas] = useState<vacuna[]>([]);
  const [filtrados, setFiltrados] = useState<vacuna[]>([]);

  useEffect(() => {
  const datos = [
      { id: 1, nombre: "vacuna 1", stock: 2, estado: "Disponible" },
      { id: 2, nombre: "vacuna 2", stock: 3, estado: "En Espera" },
      { id: 3, nombre: "vacuna 3", stock: 0, estado: "Agotado" },
      { id: 10, nombre: "vacuna 4", stock: 2, estado: "Disponible" },
    ];
    setVacunas(datos);
    setFiltrados(datos);
  }, []);

  useEffect(() => {
        const palabrasBusqueda = busqueda.toLowerCase().split(" ").filter(Boolean);
  
        const resultado = vacunas.filter((vacuna) =>{
          const texto = `${vacuna.nombre} ${vacuna.id}`.toLowerCase();
          return palabrasBusqueda.every(palabra => texto.includes(palabra));
        });
        setFiltrados(resultado);
  }, [busqueda, vacunas]);

  return (
    <>
      <div id='vacunas'>
        <Br_administrativa  onMinimizeChange={setMinimizado} />
        <main className={minimizado ? 'minimize' : ''}>
          <div id='lista_vacunas'>
            <div id='buscador'>
              <div id='br_buscador'>
                <input type="text" placeholder='Nombre del cliente.....' value={busqueda}
                  onChange={(e) => setBusqueda(e.target.value)}/>
              </div>
              <button><Link to="/administracion/cliente/registro">➕AÑADIR</Link></button>
            </div>
            <div>
              <table className='tabla-productos'>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Stock</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  {filtrados.map((vacuna) => (
                    <tr key={vacuna.id}>
                      <td>{vacuna.id}</td>
                      <td>{vacuna.nombre}</td>
                      <td>{vacuna.stock}</td>
                      <td style={{color: vacuna.estado === 'Disponible'? 'green' : 
                          vacuna.estado === 'En Espera'? 'yellow' : 'Red'
                      }}>
                        {vacuna.estado}
                      </td>
                      <td></td>
                    </tr>
                  ))}
                </tbody>
              </table>
              
            </div>
          </div>
        </main>
      </div>
    </>
  )
}

export default Vacunas