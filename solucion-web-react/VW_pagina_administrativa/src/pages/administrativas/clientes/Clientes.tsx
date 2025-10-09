import { useRef, useEffect, useState } from 'react';
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa';
import './clientes.css';
import axios from 'axios';
import { Link } from 'react-router-dom';
import type { ClienteResponse } from '../../../components/interfaces/interfaces';


function Lst_clientes() {
  const [minimizado, setMinimizado] = useState(false);
  const [busqueda, setBusqueda] = useState("");
  const [filtrados, setFiltrados] = useState<ClienteResponse[]>([]);
  const [clientes, setClientes] = useState<ClienteResponse[]>([]);
  const [clienteSeleccionado, setClienteSeleccionado] = useState<ClienteResponse | null>(null);
  const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
  const menuRef = useRef<HTMLDivElement | null>(null);
  
  useEffect(() => {
    axios.get("http://localhost:8088/api/clientes")
      .then(res => {
        console.log("DATA CLIENTES:", res.data);
        const lista = res.data.data;
        setClientes(lista);
        setFiltrados(lista);
      })
      .catch(err => {
        console.error("Error en la carga de datos", err);
      });
  }, []);

  /*const handleDelete = (idEntidad?: number) => {
    if (idEntidad === undefined) return;

    if (!window.confirm("¿Seguro que deseas eliminar este cliente")) {
      return;
    }

    axios.delete(`http://localhost:8088/api/entidades/${idEntidad}`)
      .then(() => {
        setClientes(prev => prev.filter(c => c.idEntidad !== idEntidad));
        setFiltrados(prev => prev.filter(c => c.idEntidad !== idEntidad));
        alert("✅ Entidad eliminada correctamente");
      })
      .catch(err => {
        console.error("Error al eliminar la entidad:", err);
        alert("❌ No se pudo eliminar la entidad.");
      });
  };*/

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
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        setClienteSeleccionado(null);
      }
    };

    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, []);

  useEffect(() => {
    const palabrasBusqueda = busqueda.toLowerCase().split(" ").filter(Boolean);

    const resultado = clientes.filter((cliente) =>{
      const texto = `${cliente.nombre} ${cliente.documento}`.toLowerCase();
      return palabrasBusqueda.every(palabra => texto.includes(palabra));
    });
    setFiltrados(resultado);
  }, [busqueda, clientes]);

  return (
    <>
        <div id="clientes">
          <Br_administrativa onMinimizeChange={setMinimizado}/>
          <main className={minimizado ? 'minimize' : ''}>
            <section id="lst_clientes">
                <div id='encabezado'>
                    <h2>Lista de clientes</h2>
                </div>
                <div id='buscador'>
                  <div id='br_buscador'>
                    <input type="text" placeholder='Nombre del cliente.....' value={busqueda}
                      onChange={(e) => setBusqueda(e.target.value)}/>
                  </div>
                  <Link to="/administracion/cliente/registro"><button>➕AÑADIR</button></Link>
                </div>
                <div id='lista_clientes'>
                    {filtrados.map((cliente) =>(
                      <div className='registro_cliente'>
                        <div className='identificador_client' >
                          <span id='identificador'>{cliente.id}</span>
                        </div>
                        <div className='data_client' onClick={() => setClienteSeleccionado(cliente)} key={cliente.id}>
                          <div className='info_cliente'>
                            <span className='nombre_cliente'>{cliente.nombre}</span>
                            <span className='dni_dueño'>Numero: {cliente.documento}</span>
                          </div>
                          <div className='info_cliente'>
                            <span className='correo_dueño'>CORREO: {cliente.correo}</span>
                          </div>
                        </div>
                        <div className="lst_opciones_container">
                          <div className='lst_opciones' onClick={() => setMenuActivoId(cliente.id)}>
                            <i className="fa-solid fa-ellipsis-vertical" />
                          </div>
                          {menuActivoId === cliente.id && (
                            <div ref={menuRef} className="menu-opciones">
                              <Link to="/administracion/cliente/registro" state={{ cliente }} onClick={() => setMenuActivoId(null)}>
                                Editar
                              </Link>
                              {/*<button onClick={() => {handleDelete(cliente.idEntidad);setMenuActivoId(null);}}
                              >
                                Eliminar
                              </button>*/}
                            </div>
                          )}
                        </div>
                      </div>
                    ))}
                </div>
            </section>
            {clienteSeleccionado && (
              <div className="VDCliente">
                <div className="VDCliente_contenido">
                  <button className="VDCliente_cierre" onClick={() => setClienteSeleccionado(null)}>❌</button>
                  <h2>Información del Cliente</h2>
                  <div className='VDCliente_contenido_info'>
                    <table >
                      <tr>
                        <td><strong>ID:</strong></td>
                        <td>{clienteSeleccionado.id}</td>
                      </tr>
                      <tr>
                        <td><strong>DNI:</strong></td>
                        <td>{clienteSeleccionado.documento}</td>
                      </tr>
                      <tr>
                        <td><strong>Nombre:</strong></td>
                        <td>{clienteSeleccionado.nombre}</td>
                      </tr>
                      <tr>
                        <td><strong>Apellidos:</strong></td>
                        <td>{clienteSeleccionado.ciudad}</td>
                      </tr>
                      <tr>
                        <td><strong>Correo:</strong></td>
                        <td>{clienteSeleccionado.correo}</td>
                      </tr>
                      <tr>
                        <td><strong>Telefono:</strong></td>
                        <td>{clienteSeleccionado.telefono}</td>
                      </tr>
                      <tr>
                        <td><strong>Dirección:</strong></td>
                        <td>{clienteSeleccionado.direccion}</td>
                      </tr>
                    </table>
                    <div className='VDCliente_foto'>
                      <img src="/kayn.jpg" alt="" />
                    </div>
                  </div>
                  <div className='VDCliente_mis_mascotas'>
                    <p>Diseño Pendiente</p>
                  </div>
                </div>
              </div>
            )}
          </main>
        </div>
    </>
  )
}

export default Lst_clientes