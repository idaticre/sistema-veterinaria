import { useEffect, useState } from "react";
import Br_administrativa from "../../../components/barra_administrativa/Br_administrativa";
import { Link, useLocation } from "react-router-dom";
import "./lst_mascotas.css";
import type { MascotaResponse } from "../../../components/interfaces/interfaces";
import IST from "../../../components/proteccion/IST";
import Swal from 'sweetalert2';

type Mascotaextendido = MascotaResponse;

function Lst_mascotas() {
  const [minimizado, setMinimizado] = useState(false);
  const [busqueda, setBusqueda] = useState("");
  const [filtrados, setFiltrados] = useState<Mascotaextendido[]>([]);
  const [mascotas, setMascotas] = useState<Mascotaextendido[]>([]);
  const [mascotaSeleccionado, setMascotaSeleccionado] =
    useState<Mascotaextendido | null>(null);
  const location = useLocation();
  const idMDCS = location.state?.idMascota ?? null;

  useEffect(() => {
    IST.get<{ data: MascotaResponse[] }>("/mascotas")
      .then((res) => {
        const lista = res.data.data;
        setMascotas(lista);
        setFiltrados(lista);

        if (idMDCS) {
          const encontrada = lista.find((m) => m.id === idMDCS);
          if (encontrada) setMascotaSeleccionado(encontrada);
        }
      })
      .catch(() => Swal.fire({
        title: "Error...",
        text: "al cargar las mascotas",
        icon: "error"
      }));
  }, []);

  const handleDelete = (id?: number) => {
    if (id === undefined) return;

    if (!window.confirm("¿Seguro que deseas eliminar esta mascota?")) return;

    IST.delete(`/mascotas/${id}`)
      .then(() => {
        const actualizados = mascotas.filter((e) => e.id !== id);
        setMascotas(actualizados);
        setFiltrados(actualizados);
        setMascotaSeleccionado(null);
      })
      .catch((err) => {
          Swal.fire({
          title: "Error...",
          text: "al eliminar esta mascota",
          icon: "error"
        });
      });
  };

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

    const resultado = mascotas.filter((mascota) => {
      const texto = `${mascota.nombre} ${mascota.cliente?.nombre ?? ""}`.toLowerCase();
      return palabrasBusqueda.every((palabra) => texto.includes(palabra));
    });
    setFiltrados(resultado);
  }, [busqueda, mascotas]);

  return (
    <>
      <div id="mascotas">
        <Br_administrativa onMinimizeChange={setMinimizado} />
        <main className={minimizado ? "minimize" : ""}>
          <section id="lst_mascotas">
            <div id="encabezado">
              <h2>Lista de mascotas</h2>
            </div>
            <div id="buscador">
              <div id="br_buscador">
                <input
                  type="text"
                  placeholder="Ingrese el nombre de la mascota"
                  value={busqueda}
                  onChange={(e) => setBusqueda(e.target.value)}
                />
              </div>
              <Link to="/administracion/mascotas/registro">
                <button className="anadir-goated">➕AÑADIR</button>
              </Link>
            </div>
            <section className="tabla_registosM">
              <div id="lista_mascotas">
                {filtrados
                .filter((mascota) => mascota.estado?.id !== 9)
                .length > 0 ? (
                  filtrados
                  .filter((mascota) => mascota.estado?.id !== 9)
                  .map((mascota) => (
                    <div
                      className={`registro_mascota ${mascotaSeleccionado?.id === mascota.id ? "seleccionado" : ""}`}
                      onClick={() => setMascotaSeleccionado(mascota)}
                      key={mascota.id}
                    >
                      <div className="icono_mascota">
                        {mascota.nombre.charAt(0).toUpperCase()}
                      </div>
                      <div className="datosB_mascota">
                        <p>{mascota.nombre}</p>
                        <p>
                          Dueño: {mascota.cliente?.nombre} <br />{" "}
                          {mascota.especie?.nombre} | {mascota.raza?.nombre}{" "}
                        </p>
                      </div>
                      <div className="estado_mascota">
                        <p>{mascota.estado?.nombre}</p>
                      </div>
                    </div>
                  ))
                ) : (
                  <p>❌ Mascota no encontrada </p>
                )}
              </div>
              <div className="Datos_mascotaR">
                <div className="registro_mascotaR">
                  {mascotaSeleccionado ? (
                    <div className="datos_mascotaR">
                      <div className="DmascotaR_contenido">
                        <div className="DmascotaR_cabecera">
                          <button
                            className="DmascotaR_cierre"
                            onClick={() => setMascotaSeleccionado(null)}
                          >
                            ❌
                          </button>
                          <h2>Información de {mascotaSeleccionado.nombre}</h2>
                        </div>
                        <div className="DmascotaR_contenido_info">
                          <section className="DmascotaR_contenido_superior">
                            <table>
                              <tbody>
                                <tr>
                                  <td>
                                    <strong>Codigo:</strong>
                                  </td>
                                  <td colSpan={3}>
                                    {mascotaSeleccionado.codigo}
                                  </td>
                                </tr>
                                <tr>
                                  <td>
                                    <strong>Dueño:</strong>
                                  </td>
                                  <td colSpan={3}>
                                    {mascotaSeleccionado.cliente?.nombre}
                                  </td>
                                </tr>
                                <tr>
                                  <td>
                                    <strong>Especie:</strong>
                                  </td>
                                  <td>{mascotaSeleccionado.especie?.nombre}</td>
                                  <td>
                                    <strong>Raza:</strong>
                                  </td>
                                  <td>{mascotaSeleccionado.raza?.nombre}</td>
                                </tr>
                                <tr>
                                  <td>
                                    <strong>Sexo:</strong>
                                  </td>
                                  <td>
                                    {mascotaSeleccionado.sexo == "M"
                                      ? "Macho"
                                      : "Hembra"}
                                  </td>
                                  <td>
                                    <strong>Etapa:</strong>
                                  </td>
                                  <td>{mascotaSeleccionado.etapa?.nombre}</td>
                                </tr>
                                <tr>
                                  <td>
                                    <strong>Tamaño:</strong>
                                  </td>
                                  <td colSpan={3}>
                                    {mascotaSeleccionado.tamano?.nombre}
                                  </td>
                                </tr>
                              </tbody>
                            </table>
                            {/* mascotaSeleccionado.foto && <img src={mascotaSeleccionado.foto} alt="" /> */}
                          </section>
                          <table>
                            <tbody>
                              <tr>
                                <td>
                                  <strong>Fecha de nacimiento:</strong>
                                </td>
                                <td>{mascotaSeleccionado.fechaNacimiento}</td>
                              </tr>
                            </tbody>
                          </table>
                          <table>
                            <tbody>
                              <tr>
                                <td>Pelaje: {mascotaSeleccionado.pelaje}</td>
                                <td>
                                  Alergias: {mascotaSeleccionado.alergias}
                                </td>
                                <td>
                                  estado: {mascotaSeleccionado.estado?.nombre}
                                </td>
                              </tr>
                            </tbody>
                          </table>
                          <div>
                            <span>
                              <strong>Castrado/a: </strong>
                              {mascotaSeleccionado.esterilizado ? "✅" : "❌"}
                            </span>
                            <span>
                              <strong>Chip: </strong>
                              {mascotaSeleccionado.chip ? "✅" : "❌"}
                            </span>
                            <span>
                              <strong>Factor Dea: </strong>
                              {mascotaSeleccionado.factorDea ? "✅" : "❌"}
                            </span>
                            <span>
                              <strong>Pedigree: </strong>
                              {mascotaSeleccionado.pedigree ? "✅" : "❌"}
                            </span>
                            <span>
                              <strong>Agresivo: </strong>
                              {mascotaSeleccionado.agresividad ? "✅" : "❌"}
                            </span>
                          </div>
                          <Link
                            to={`/administracion/mascotas/registro/${mascotaSeleccionado.id}`}
                          >
                            <button>Editar</button>
                          </Link>
                          <Link
                            to= {`/administracion/historia-clinica/${mascotaSeleccionado.id}`}
                          >
                            <button>Historia Clinica</button>
                          </Link>
                          <button
                            onClick={() => {
                              handleDelete(mascotaSeleccionado.id);
                            }}
                          >
                            Eliminar
                          </button>
                        </div>
                      </div>
                    </div>
                  ) : (
                    <div className="SinSeleccion">
                      <h2>
                        Por favor seleccione una mascota <br />
                        📚
                      </h2>
                    </div>
                  )}
                </div>
              </div>
            </section>
          </section>
        </main>
      </div>
    </>
  );
}

export default Lst_mascotas;
