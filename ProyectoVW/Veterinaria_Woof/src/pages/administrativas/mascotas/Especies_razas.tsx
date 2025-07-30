import { useState } from 'react'
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa'
import "./especies_razas.css"
import { Link } from 'react-router-dom';

function Especies_razas() {
  const [minimizado, setMinimizado] = useState(false);
  const [especieActivaId, setEspecieActivaId] = useState<number | null>(null);

  const especiesMock = [
    {
      id: 1,
      nombreEspecie: 'Canino',
      razas: [
        { id: 1, nombre: 'Labrador' },
        { id: 2, nombre: 'Bulldog' },
        { id: 3, nombre: 'Poodle' }
      ]
    },
    {
      id: 2,
      nombreEspecie: 'Felino',
      razas: [
        { id: 4, nombre: 'Persa' },
        { id: 5, nombre: 'Siamés' },
        { id: 6, nombre: 'Bengala' }
      ]
    },
    {
      id: 3,
      nombreEspecie: 'Aves',
      razas: [
        { id: 7, nombre: 'Canario' },
        { id: 8, nombre: 'Perico' }
      ]
    }
  ];

  const toggleTabla = (id: number) => {
      setEspecieActivaId((prev) => (prev === id ? null : id));
  };


  return (
    <>
      <div id='especies_razas'>
        <Br_administrativa  onMinimizeChange={setMinimizado}/>
        <main className={minimizado ? 'minimize' : ''}>
          <div className="contenedor_especie">
            <h2>Especies registradas</h2>
            <Link to="/" id='añadir_ER'>Añadir ➕</Link>
            <div className='lst_ER'>
              {especiesMock.map((especie) => (
                <div key={especie.id} className="especie">
                  <div className="despliegue_razas">
                    <div className='data_ER' onClick={() => toggleTabla(especie.id)}>
                      <span className='id_ER'>{especie.id})</span>
                      <span className='name_ER'>{especie.nombreEspecie}</span>
                    </div>
                    <div className='opciones_ER'>
                      <Link to="#" className='delete_ER'><i className="fa-solid fa-trash"/></Link>
                      <Link to="#" className='edit_ER'><i className="fa-solid fa-pen-to-square"/></Link>
                      <Link to="#" className='add_ER'><i className="fa-solid fa-plus"/></Link>
                    </div>
                  </div>
                  {especieActivaId === especie.id && (
                    <table className="tabla-razas">
                      <thead>
                        <tr>
                          <th>ID</th>
                          <th>Nombre de raza</th>
                          <th colSpan={2}>Acciones</th>
                        </tr>
                      </thead>
                      <tbody>
                        {especie.razas.map((raza) => (
                          <tr key={raza.id}>
                            <td>{raza.id}</td>
                            <td>{raza.nombre}</td>
                            <td className='delete_ER_raza'><i className="fa-solid fa-trash"></i></td>
                            <td className='edit_ER_raza'><i className="fa-solid fa-pen-to-square"></i></td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  )}
                </div>
              ))}
            </div>
          </div>
        </main>
      </div>
    </>
  )
}

export default Especies_razas