import { useEffect, useState } from 'react'
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa'
import { Link } from 'react-router-dom';
import './lst_mascotas.css'
import type { MascotaResponse } from '../../../components/interfaces/interfaces';
import IST from '../../../components/proteccion_momentanea/IST';

type Mascotaextendido = MascotaResponse & { nombre_dueño?: string; nombre_raza?: string; nombre_especie?: string;
   nombre_estado?: string; nombre_tamaño?: string; nombre_etapa?: string };

function Lst_mascotas() {
  const [minimizado, setMinimizado] = useState(false);
  const [busqueda, setBusqueda] = useState("");
  const [filtrados, setFiltrados] = useState<Mascotaextendido[]>([]);
  const [mascotas, setMascotas] = useState<Mascotaextendido[]>([]);
  const [mascotaSeleccionado, setMascotaSeleccionado] = useState<Mascotaextendido | null>(null);
  useEffect(() => {
    IST
      .get<{ data: MascotaResponse[] }>("/mascotas")
      .then(async (res) => {
        const lista = res.data.data;

        const mascotasConExtras = await Promise.all(
          lista.map(async (m: MascotaResponse) => {
            try {
              const [dueñoRes, razaRes, especieRes, estadoRes, tamanoRes, etapaRes] = await Promise.all([
                IST.get(`/clientes/${m.idCliente}`),
                IST.get(`/razas/${m.idRaza}`),
                IST.get(`/especies/${m.idEspecie}`),
                IST.get(`/estado-mascota/${m.idEstado}`),
                IST.get(`/tamanos/${m.idTamano}`),
                IST.get(`/etapasVida/${m.idEtapa}`)
              ]);

              return {
                ...m,
                nombre_dueño: dueñoRes.data.data.nombre,
                nombre_raza: razaRes.data.nombre,
                nombre_especie: especieRes.data.nombre,
                nombre_estado: estadoRes.data.nombre,
                nombre_tamaño: tamanoRes.data.descripcion,
                nombre_etapa: etapaRes.data.descripcion,
              };
            } catch (error) {
              console.error("Error al obtener datos", error);
              return {
                ...m,
                nombre_dueño: "Desconocido",
                nombre_raza: "Desconocido",
                nombre_especie: "Desconocido",
                nombre_estado: "Desconocido",
                nombre_tamaño: "Desconocido",
                nombre_etapa: "Desconocido",
              };
            }
          })
        );

        setMascotas(mascotasConExtras);
        setFiltrados(mascotasConExtras);
      })
      .catch((err) => console.error("Error en la carga de mascotas", err));
  }, []);

  const handleDelete = (id?: number) => {
    if (id === undefined) return; 

    if (!window.confirm("¿Seguro que deseas eliminar esta mascota?")) return;

    IST.delete(`/mascotas/eliminar/${id}`)
      .then(() => {
        const actualizados = mascotas.filter(e => e.id !== id);
        setMascotas(actualizados);
        setFiltrados(actualizados);
        setMascotaSeleccionado(null);
      })
      .catch(err => {
        console.error("Error al eliminar esta mascota", err);
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
  
        const resultado = mascotas.filter((mascota) =>{
          const texto = `${mascota.nombre} ${mascota.idCliente}`.toLowerCase();
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
                <div id='encabezado'><h2>Lista de mascotas</h2></div>
                <div id='buscador'>
                  <div id='br_buscador'>
                    <input type="text" placeholder='Ingrese el nombre del cliente que desea buscar' value={busqueda}
                      onChange={(e) => setBusqueda(e.target.value)}/>
                  </div>
                  <Link to="/administracion/mascotas/registro"><button className="anadir-goated">➕AÑADIR</button></Link>
                </div>
                <section className='tabla_registosM'>
                  <div id='lista_mascotas'>
                      {filtrados.map((mascota) =>(
                        <div className={`registro_mascota ${mascotaSeleccionado?.id === mascota.id? "seleccionado":""}`} onClick={() => setMascotaSeleccionado(mascota)} key={mascota.id}>
                          <div className='icono_mascota'>
                            {mascota.nombre.charAt(0).toUpperCase()}
                          </div>
                          <div className='datosB_mascota'>
                            <p>{mascota.nombre}</p>
                            <p>Dueño: {mascota.nombre_dueño} <br /> {mascota.nombre_especie} | {mascota.nombre_raza} </p>
                          </div>
                          <div className='estado_mascota'>
                            <p>{mascota.nombre_estado}</p>
                          </div>
                        </div>
                      ))}
                  </div>
                  <div className='Datos_mascotaR'>
                      <div className='registro_mascotaR'>
                        {mascotaSeleccionado ? (
                          <div className="datos_mascotaR">
                            <div className="DmascotaR_contenido">
                              <div className='DmascotaR_cabecera'>
                                <button className="DmascotaR_cierre" onClick={() => setMascotaSeleccionado(null)}>❌</button>
                                <h2>Información de {mascotaSeleccionado.nombre}</h2>
                              </div>
                              <div className='DmascotaR_contenido_info'>
                                <section className='DmascotaR_contenido_superior'>
                                  <table>
                                    <tr>
                                      <td><strong>Codigo:</strong></td>
                                      <td colSpan={3}>{mascotaSeleccionado.codigo}</td>
                                    </tr>
                                    <tr>
                                      <td><strong>Dueño:</strong></td>
                                      <td colSpan={3}>{mascotaSeleccionado.nombre_dueño}</td>
                                    </tr>
                                    <tr>
                                      <td><strong>Especie:</strong></td>
                                      <td>{mascotaSeleccionado.nombre_especie}</td>
                                      <td><strong>Raza:</strong></td>
                                      <td>{mascotaSeleccionado.nombre_raza}</td>
                                    </tr>
                                    <tr>
                                      <td><strong>Sexo:</strong></td>
                                      <td>{mascotaSeleccionado.sexo == "M"? "Macho":"Hembra"}</td>
                                      <td><strong>Etapa:</strong></td>
                                      <td>{mascotaSeleccionado.nombre_etapa}</td>
                                    </tr>
                                    <tr>
                                      <td><strong>Tamaño:</strong></td>
                                      <td colSpan={3}>{mascotaSeleccionado.nombre_tamaño}</td>
                                    </tr>
                                  </table>
                                  <img src={mascotaSeleccionado.foto} alt="" />
                                </section>
                                <table>
                                  <tr>
                                    <td><strong>Fecha de nacimiento:</strong></td>
                                    <td>{mascotaSeleccionado.fechaNacimiento}</td>
                                  </tr>
                                </table>
                                <table>
                                  <tr>
                                    <td>Pelaje: {mascotaSeleccionado.pelaje}</td>
                                    <td>Alergias: {mascotaSeleccionado.alergias}</td>
                                    <td>estado: {mascotaSeleccionado.nombre_estado}</td>
                                  </tr>
                                </table>
                                <div>
                                  <span><strong>Castrado/a: </strong>{mascotaSeleccionado.esterilizado? "✅":"❌"}</span>
                                  <span><strong>Chip: </strong>{mascotaSeleccionado.chip? "✅":"❌"}</span>
                                  <span><strong>Factor Dea: </strong>{mascotaSeleccionado.factorDea? "✅":"❌"}</span>
                                  <span><strong>Pedigree: </strong>{mascotaSeleccionado.pedigree? "✅":"❌"}</span>
                                  <span><strong>Agresivo: </strong>{mascotaSeleccionado.agresividad? "✅":"❌"}</span>
                                </div>
                                <Link to="/administracion/mascotas/registro" state={{ mascotaSeleccionado }}><button>Editar</button></Link>
                                <button onClick={() => {handleDelete(mascotaSeleccionado.id)}}>Eliminar</button>
                              </div>  
                            </div>
                          </div>
                        ):
                        (
                          <div className='SinSeleccion'>
                            <h2>Por favor seleccione una mascota <br />📚</h2>
                          </div>
                        )}
                      </div>
                  </div>
                </section>
          </section>
          
        </main>
      </div>
    </>
  )
}

export default Lst_mascotas