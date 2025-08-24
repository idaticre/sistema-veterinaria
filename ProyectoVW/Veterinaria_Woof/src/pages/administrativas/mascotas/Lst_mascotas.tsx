import { useEffect, useRef, useState } from 'react'
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa'
import { Link } from 'react-router-dom';
import './lst_mascotas.css'

interface Mascota {
  id: number;
  nombre: string;
  id_cliente: number;
}

function Lst_mascotas() {
  const [minimizado, setMinimizado] = useState(false);
  const [busqueda, setBusqueda] = useState("");
  const [filtrados, setFiltrados] = useState<Mascota[]>([]);
  const [mascotas, setMascotas] = useState<Mascota[]>([]);
  const [mascotaSeleccionado, setMascotaSeleccionado] = useState<Mascota | null>(null);
  const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
  const menuRef = useRef<HTMLDivElement | null>(null);
  
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setMenuActivoId(null); 
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  useEffect(() => {
    // esto tambien xd
    const datos = [
      { id: 1, nombre: "Pancho", id_cliente: 2 },
      { id: 2, nombre: "yuko", id_cliente: 3 },
      { id: 3, nombre: "chocolate", id_cliente: 1 },
      { id: 10, nombre: "mostaza", id_cliente: 2 },
    ];
    setMascotas(datos);
    setFiltrados(datos);
  }, []);

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        setMascotaSeleccionado(null);
      }
    };

    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, []);

  useEffect(() => {
        const palabrasBusqueda = busqueda.toLowerCase().split(" ").filter(Boolean);
  
        const resultado = mascotas.filter((mascota) =>{
          const texto = `${mascota.nombre} ${mascota.id_cliente}`.toLowerCase();
          return palabrasBusqueda.every(palabra => texto.includes(palabra));
        });
        setFiltrados(resultado);
  }, [busqueda, mascotas]);
  
  return (
    <>
      <div id='mascotas'>
        <Br_administrativa onMinimizeChange={setMinimizado}/>
        <main className={minimizado ? 'minimize' : ''}>
          <section id="lst_mascotas">
                <div id='encabezado'>
                    <h2>Lista de mascotas</h2>
                </div>
                <div id='buscador'>
                  <div id='br_buscador'>
                    <input type="text" placeholder='Nombre del cliente.....' value={busqueda}
                      onChange={(e) => setBusqueda(e.target.value)}/>
                  </div>
                  <button><Link to="/administracion/cliente/registro">➕AÑADIR</Link></button>
                </div>
                <div id='lista_mascotas'>
                    {filtrados.map((mascota) =>(
                      <div className='registro_mascota'>
                        <div className='foto_mascota' onClick={() => setMascotaSeleccionado(mascota)} key={mascota.id}>
                          <img src="/yuko.jpeg" alt="" />
                        </div>
                        <div className='base_mascota'>
                          <div className='data_mascota' onClick={() => setMascotaSeleccionado(mascota)} key={mascota.id}>
                              <span className='nombre_mascota'>Nombre: {mascota.nombre}</span>
                              <span className='dni_dueño'>Dueño: {mascota.id_cliente}</span>
                          </div>
                          <div className="lst_opciones_container">
                            <div className='lst_opciones' onClick={() => setMenuActivoId(mascota.id)}>
                              <i className="fa-solid fa-ellipsis-vertical" />
                            </div>
                            {menuActivoId === mascota.id && (
                              <div ref={menuRef} className="menu-opciones">
                                <a href={`/mascotas/editar/${mascota.id}`} onClick={() => setMenuActivoId(null)}>Editar</a>
                                <a href={`/mascotas/eliminar/${mascota.id}` } onClick={() => setMenuActivoId(null)}>Eliminar</a>
                              </div>
                            )}
                          </div>
                        </div>
                      </div>
                    ))}
                </div>
          </section>
          {mascotaSeleccionado && (
              <div className="VDMascota">
                <div className="VDMascota_contenido">
                  <button className="VDMascota_cierre" onClick={() => setMascotaSeleccionado(null)}>❌</button>
                  <h2>Información de la mascota</h2>
                  <div className='VDMascota_contenido_info'>
                    <table>
                      <tr>
                        <td><strong>ID:</strong></td>
                        <td>{mascotaSeleccionado.id}</td>
                      </tr>
                      <tr>
                        <td><strong>Nombre:</strong></td>
                        <td>{mascotaSeleccionado.nombre}</td>
                      </tr>
                      <tr>
                        <td><strong>Nacimiento:</strong></td>
                        <td>00/00/0000</td>
                      </tr>
                      <tr>
                        <td><strong>Dueño:</strong></td>
                        <td>#########</td>
                      </tr>
                      <tr>
                        <td><strong>Especie:</strong></td>
                        <td>###############</td>
                      </tr>
                      <tr>
                        <td><strong>Raza:</strong></td>
                        <td>###########</td>
                      </tr>
                      <tr>
                        <td><strong>Pelaje:</strong></td>
                        <td></td>
                      </tr>
                      <tr>
                        <td><strong>Peso:</strong></td>
                        <td>17KG</td>
                      </tr>
                      <tr>
                        <td><strong>Sexo:</strong></td>
                        <td>Hembra</td>
                      </tr>
                      <tr>
                        <td><strong>Castrado/a:</strong></td>
                        <td>✅</td>
                      </tr>
                    </table>
                    <div className='VDMascota_foto'>
                      <img src="/yuko.jpeg" alt="" />
                    </div>
                  </div>  
                </div>
              </div>
            )}
        </main>
      </div>
    </>
  )
}

export default Lst_mascotas