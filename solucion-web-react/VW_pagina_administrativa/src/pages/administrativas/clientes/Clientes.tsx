import { useEffect, useState } from 'react';
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa';
import './clientes.css';
import IST from '../../../components/proteccion/IST';
import { Link, useNavigate } from 'react-router-dom';
import type { ClienteResponse, MascotaResponse } from '../../../components/interfaces/interfaces';

type Mascotaextendido = MascotaResponse & { nombre_raza?: string; nombre_especie?: string;
   nombre_estado?: string};

function Lst_clientes() {
  const [minimizado, setMinimizado] = useState(false);
  const [busqueda, setBusqueda] = useState("");
  const [filtrados, setFiltrados] = useState<ClienteResponse[]>([]);
  const [mascota, setMascota] = useState<Mascotaextendido[]>([]);
  const [clientes, setClientes] = useState<ClienteResponse[]>([]);
  const [clienteSeleccionado, setClienteSeleccionado] = useState<ClienteResponse | null>(null);
  const navigate = useNavigate();
  
  useEffect(() => {
    IST.get("/clientes")
      .then(res => {
        console.log("clientes:", res.data);
        const lista = res.data.data;

        const activos = lista.filter((cliente: ClienteResponse) =>cliente.activo === true);

        setClientes(activos);
        setFiltrados(activos);
      })
      .catch(err => {
        console.error("Error en la carga de datos", err);

        if (err.response && err.response.status === 401) {
          alert("Tu sesión ha expirado. Inicia sesión nuevamente.");
          localStorage.clear();
          navigate("/login")
        }
      });
  }, []);

  useEffect(() => {
    IST
      .get<{ data: MascotaResponse[] }>("/mascotas")
      .then(async (res) => {
        const lista = res.data.data;

        const mascotasConExtras = await Promise.all(
          lista.map(async (m: MascotaResponse) => {
            try {
              const [razaRes, especieRes, estadoRes] = await Promise.all([
                IST.get(`/razas/${m.idRaza}`),
                IST.get(`/especies/${m.idEspecie}`),
                IST.get(`/estado-mascota/${m.idEstado}`)
              ]);

              return {
                ...m,
                nombre_raza: razaRes.data.nombre,
                nombre_especie: especieRes.data.nombre,
                nombre_estado: estadoRes.data.nombre,
              };
            } catch (error) {
              console.error("Error al obtener datos", error);
              return {
                ...m,
                nombre_raza: "Desconocido",
                nombre_especie: "Desconocido",
                nombre_estado: "Desconocido",
              };
            }
          })
        );

        setMascota(mascotasConExtras);
      })
      .catch((err) => console.error("Error en la carga de mascotas", err));
  }, []);


  const mascotaDueño = clienteSeleccionado? mascota.filter(masc => masc.idCliente == clienteSeleccionado.id): [];

  const handleDelete = (id?: number) => {
    if (id === undefined) return; 

    if (!window.confirm("¿Seguro que deseas eliminar este cliente?")) return;

    IST.delete(`/clientes/eliminar/${id}`)
      .then(() => {
        const actualizados = clientes.filter(e => e.id !== id);
        setClientes(actualizados);
        setFiltrados(actualizados);
      })
      .catch(err => {
        console.error("Error al eliminar este cliente", err);
      });
  };

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
                  <table>
                    <thead>
                      <tr>
                        <th>Nombre</th>
                        <th>N. Documento</th>
                        <th>Correo</th>
                        <th>Estado</th>
                        <th className='accion_lst_cliente' colSpan={2}></th>
                      </tr>
                    </thead>
                    <tbody>
                      {filtrados.map((cliente) =>(
                        <tr key={cliente.id}>
                          <td className='cliente_dato_vd' onClick={() => setClienteSeleccionado(cliente)}>{cliente.nombre}</td>
                          <td className='cliente_dato_vd' onClick={() => setClienteSeleccionado(cliente)}>{cliente.documento}</td>
                          <td className='cliente_dato_vd' onClick={() => setClienteSeleccionado(cliente)}>{cliente.correo}</td>
                          <td>{cliente.activo? "✅" : "❌"}</td>
                          <td>
                            <Link to="/administracion/cliente/registro" state={{ cliente }}>✏️</Link>
                          </td>
                          <td>
                            <i onClick={() => {handleDelete(cliente.id)}}>🗑️</i>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
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
                      {clienteSeleccionado.idTipoPersonaJuridica == 2  && (
                        <tr>
                          <td><strong>representante:</strong></td>
                          <td>{clienteSeleccionado.representante}</td>
                        </tr>
                      )}
                      <tr>
                        <td><strong>Dirección:</strong></td>
                        <td>{clienteSeleccionado.direccion}</td>
                      </tr>
                    </table>
                    <div className='VDCliente_mis_mascotas'>
                      {mascotaDueño.length === 0 ? (
                        <p>NO HAY MASCOTAS A SU NOMBRE</p>
                      ):(
                        mascotaDueño.map((masc) => (
                          <Link to="/administracion/mascotas/lista" state={{ idMascota: masc.id }}>
                            <div className='masc_dueño'>
                                <div className='masc_dueño_img'>
                                  <img src={masc.foto} alt="" />
                                </div>
                                <div className='masc_dueño_dataS masc_superior'>
                                  <p>{masc.nombre}</p>
                                  <span>{masc.nombre_estado}</span>
                                </div>
                                <div className='masc_dueño_dataS masc_inferior'>
                                  <p>Especie: {masc.nombre_especie}</p>
                                  <span>Raza: {masc.nombre_raza}</span>
                                </div>
                            </div>
                          </Link>
                        ))
                      )}
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

export default Lst_clientes