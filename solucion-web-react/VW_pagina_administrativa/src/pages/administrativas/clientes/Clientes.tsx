import { useEffect, useState } from "react";
import Br_administrativa from "../../../components/barra_administrativa/Br_administrativa";
import "./clientes.css";
import IST from "../../../components/proteccion/IST";
import { Link, useNavigate } from "react-router-dom";
import type {
  ClienteResponse,
  MascotaResponse,
} from "../../../components/interfaces/interfaces";
import Swal from 'sweetalert2';

type Mascotaextendido = MascotaResponse;

function Lst_clientes() {
  const [minimizado, setMinimizado] = useState(false);
  const [busqueda, setBusqueda] = useState("");
  const [filtrados, setFiltrados] = useState<ClienteResponse[]>([]);
  const [mascota, setMascota] = useState<Mascotaextendido[]>([]);
  const [clientes, setClientes] = useState<ClienteResponse[]>([]);
  const [clienteSeleccionado, setClienteSeleccionado] =
    useState<ClienteResponse | null>(null);

  useEffect(() => {
    IST.get("/clientes")
      .then((res) => {
        const lista = res.data.data;

        const activos = lista.filter(
          (cliente: ClienteResponse) => cliente.activo === true,
        );

        setClientes(activos);
        setFiltrados(activos);
      })
      .catch((err) => {
        Swal.fire({
          title: "Error desconocido",
          text: "Por favor refresque la página",
          icon: "error"
        });
      });
  }, []);

  useEffect(() => {
    IST.get<{ data: MascotaResponse[] }>("/mascotas")
      .then((res) => setMascota(res.data.data))
      .catch(() => Swal.fire({
        title: "Error desconocido",
        text: "Por favor refresque la página",
        icon: "error"
      }));
  }, []);

  const mascotaDueño = clienteSeleccionado
    ? mascota.filter((masc) => masc.cliente?.id == clienteSeleccionado.id)
    : [];

  const handleDelete = (id?: number) => {
    if (id === undefined) return;

    if (!window.confirm("¿Seguro que deseas eliminar este cliente?")) return;

    IST.delete(`/clientes/${id}`)
      .then(() => {
        const actualizados = clientes.filter((e) => e.id !== id);
        setClientes(actualizados);
        setFiltrados(actualizados);
      })
      .catch((err) => {
        Swal.fire({
          title: "Error...",
          text: "al eliminar este cliente",
          icon: "error"
        });
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

    const resultado = clientes.filter((cliente) => {
      const texto = `${cliente.nombre} ${cliente.documento}`.toLowerCase();
      return palabrasBusqueda.every((palabra) => texto.includes(palabra));
    });
    setFiltrados(resultado);
  }, [busqueda, clientes]);

  return (
    <>
      <div id="clientes">
        <Br_administrativa onMinimizeChange={setMinimizado} />
        <main className={minimizado ? "minimize" : ""}>
          <section id="lst_clientes">
            <div id="encabezado">
              <h2>Lista de clientes</h2>
            </div>
            <div id="buscador">
              <div id="br_buscador">
                <input
                  type="text"
                  placeholder="Nombre del cliente....."
                  value={busqueda}
                  onChange={(e) => setBusqueda(e.target.value)}
                />
              </div>
              <Link to="/administracion/cliente/registro">
                <button>➕AÑADIR</button>
              </Link>
            </div>
            <div id="lista_clientes">
              <table>
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>N. Documento</th>
                    <th>Correo</th>
                    <th>Estado</th>
                    <th className="accion_lst_cliente" colSpan={2}></th>
                  </tr>
                </thead>
                <tbody>
                  {filtrados.map((cliente) => (
                    <tr key={cliente.id}>
                      <td
                        className="cliente_dato_vd"
                        onClick={() => setClienteSeleccionado(cliente)}
                      >
                        {cliente.nombre}
                      </td>
                      <td
                        className="cliente_dato_vd"
                        onClick={() => setClienteSeleccionado(cliente)}
                      >
                        {cliente.documento}
                      </td>
                      <td
                        className="cliente_dato_vd"
                        onClick={() => setClienteSeleccionado(cliente)}
                      >
                        {cliente.correo}
                      </td>
                      <td>{cliente.activo ? "✅" : "❌"}</td>
                      <td>
                        <Link
                          to="/administracion/cliente/registro"
                          state={{ cliente }}
                        >
                          ✏️
                        </Link>
                      </td>
                      <td>
                        <i
                          onClick={() => {
                            handleDelete(cliente.id);
                          }}
                        >
                          🗑️
                        </i>
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
                <button
                  className="VDCliente_cierre"
                  onClick={() => setClienteSeleccionado(null)}
                >
                  ❌
                </button>
                <h2>Información del Cliente</h2>
                <div className="VDCliente_contenido_info">
                  <table>
                    <tbody>
                      <tr>
                        <td>
                          <strong>ID:</strong>
                        </td>
                        <td>{clienteSeleccionado.id}</td>
                      </tr>
                      <tr>
                        <td>
                          <strong>DNI:</strong>
                        </td>
                        <td>{clienteSeleccionado.documento}</td>
                      </tr>
                      <tr>
                        <td>
                          <strong>Nombre:</strong>
                        </td>
                        <td>{clienteSeleccionado.nombre}</td>
                      </tr>
                      {clienteSeleccionado.representante && (
                        <tr>
                          <td>
                            <strong>Representante:</strong>
                          </td>
                          <td>{clienteSeleccionado.representante}</td>
                        </tr>
                      )}
                      <tr>
                        <td>
                          <strong>Ciudad:</strong>
                        </td>
                        <td>{clienteSeleccionado.ciudad}</td>
                      </tr>
                      <tr>
                        <td>
                          <strong>Correo:</strong>
                        </td>
                        <td>{clienteSeleccionado.correo}</td>
                      </tr>
                      <tr>
                        <td>
                          <strong>Telefono:</strong>
                        </td>
                        <td>{clienteSeleccionado.telefono}</td>
                      </tr>
                      {clienteSeleccionado.idTipoPersonaJuridica == 2 && (
                        <tr>
                          <td>
                            <strong>representante:</strong>
                          </td>
                          <td>{clienteSeleccionado.representante}</td>
                        </tr>
                      )}
                      <tr>
                        <td>
                          <strong>Dirección:</strong>
                        </td>
                        <td>{clienteSeleccionado.direccion}</td>
                      </tr>
                    </tbody>
                  </table>
                  <div className="VDCliente_mis_mascotas">
                    {mascotaDueño.length === 0 ? (
                      <p>NO HAY MASCOTAS A SU NOMBRE</p>
                    ) : (
                      mascotaDueño.map((masc) => (
                        <Link
                          to="/administracion/mascotas/lista"
                          state={{ idMascota: masc.id }}
                        >
                          <div className="masc_dueño">
                            <div className="masc_dueño_img">
                              <img src={masc.foto} alt="" />
                            </div>
                            <div className="masc_dueño_dataS masc_superior">
                              <p>{masc.nombre}</p>
                              <span>{masc.estado?.nombre}</span>
                            </div>
                            <div className="masc_dueño_dataS masc_inferior">
                              <p>Especie: {masc.especie?.nombre}</p>
                              <span>Raza: {masc.raza?.nombre}</span>
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
  );
}

export default Lst_clientes;
