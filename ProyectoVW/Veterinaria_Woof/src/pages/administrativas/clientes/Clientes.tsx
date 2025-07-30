import { useRef, useEffect, useState } from 'react';
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa';
import './clientes.css';
import axios from 'axios';
import { Link } from 'react-router-dom';

{/*interface dueño {
  id: number;
  apellido: string;
  correo: string;
  direccion: string
  nombre: string;
  num_doc: string;
  telefono: string
  tipo_documento: number;
} */}

interface Cliente {
  id: number;
  nombre: string;
  apellido: string;
}

function Lst_clientes() {
  const [minimizado, setMinimizado] = useState(false);
  const [busqueda, setBusqueda] = useState("");
  const [filtrados, setFiltrados] = useState<Cliente[]>([]);
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [clienteSeleccionado, setClienteSeleccionado] = useState<Cliente | null>(null);
  const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
  const menuRef = useRef<HTMLDivElement | null>(null);

  /*const [dueños, setDueños] = useState<dueño[]>([]);*
  
  /*useEffect(() => {
    axios.get("http://localhost:8080/clientes")
    .then(res => {
      setDueños(res.data);
    })
    .catch(err => {
      console.error("Error en la carga de datos", err)
    });
  }, [])*/

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
  // borralo una vez tengas la base de datos, no te olvide pendejo xd
  const datos = [
      { id: 1, nombre: "Juan", apellido: "Pérez rosendo" },
      { id: 2, nombre: "Ana", apellido: "Torres quiñones" },
      { id: 3, nombre: "Carlos", apellido: "Ramírez manfredi" },
      { id: 10, nombre: "Marlos", apellido: "Ramírez quiñones" },
    ];
    setClientes(datos);
    setFiltrados(datos);
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
      const texto = `${cliente.nombre} ${cliente.apellido}`.toLowerCase();
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
                            <span className='nombre_cliente'>{cliente.nombre} {cliente.apellido}</span>
                            <span className='dni_dueño'>DNI: #########</span>
                          </div>
                          <div className='info_cliente'>
                            <span className='correo_dueño'>CORREO: ##########################</span>
                          </div>
                        </div>
                        <div className="lst_opciones_container">
                          <div className='lst_opciones' onClick={() => setMenuActivoId(cliente.id)}>
                            <i className="fa-solid fa-ellipsis-vertical" />
                          </div>
                          {menuActivoId === cliente.id && (
                            <div ref={menuRef} className="menu-opciones">
                              <a href={`/clientes/editar/${cliente.id}`} onClick={() => setMenuActivoId(null)}>Editar</a>
                              <a href={`/clientes/eliminar/${cliente.id}` } onClick={() => setMenuActivoId(null)}>Eliminar</a>
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
                        <td>12345678</td>
                      </tr>
                      <tr>
                        <td><strong>Nombre:</strong></td>
                        <td>{clienteSeleccionado.nombre}</td>
                      </tr>
                      <tr>
                        <td><strong>Apellidos:</strong></td>
                        <td>{clienteSeleccionado.apellido}</td>
                      </tr>
                      <tr>
                        <td><strong>Correo:</strong></td>
                        <td>###################</td>
                      </tr>
                      <tr>
                        <td><strong>Telefono:</strong></td>
                        <td>123456789</td>
                      </tr>
                      <tr>
                        <td><strong>Dirección:</strong></td>
                        <td>###############</td>
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