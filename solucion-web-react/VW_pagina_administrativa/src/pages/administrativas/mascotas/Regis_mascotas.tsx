import { useEffect, useState } from "react";
import Br_administrativa from "../../../components/barra_administrativa/Br_administrativa";
import "./regis_mascotas.css";
import type {
  Razas,
  Especie,
  MascotaRequest,
  ClienteResponse,
  Estado_Mascota,
  Tamaño_Mascota,
  Etapa_Mascota,
  MascotaResponse,
} from "../../../components/interfaces/interfaces";
import IST from "../../../components/proteccion/IST";
import { Link, useNavigate, useParams } from "react-router-dom";
import Swal from 'sweetalert2';

type Mascotaextendido = MascotaResponse & { nombre_dueño?: string };

function Regis_mascotas() {
  const [minimizado, setMinimizado] = useState(false);
  const [imagenMascota, setImagenMascota] = useState<string | null>(null);
  const [dueños, setDueños] = useState<ClienteResponse[]>([]);
  const [razas, setRazas] = useState<Razas[]>([]);
  const [especies, setEspecies] = useState<Especie[]>([]);
  const [estadoMascota, setEstadoMascota] = useState<Estado_Mascota[]>([]);
  const [tamañosMascota, setTamañosMascota] = useState<Tamaño_Mascota[]>([]);
  const [etapaMascota, setEtapaMascota] = useState<Etapa_Mascota[]>([]);

  const [formMascota, setFormMascota] = useState<MascotaRequest>({
    nombre: "",
    sexo: "M",
    idCliente: 0,
    idRaza: 0,
    idEspecie: 0,
    idEstado: 1,
    fechaNacimiento: "",
    pelaje: "",
    idTamano: 0,
    idEtapa: 0,
    esterilizado: false,
    alergias: "",
    peso: undefined,
    chip: false,
    pedigree: false,
    factorDea: false,
    agresividad: false,
    foto: ""
  });
  const [mascotaSelecc, setMascotaSelecc] = useState<MascotaResponse | null>(null);
  const [fotoFile, setFotoFile] = useState<File | null>(null);

  const [busqueda, setBusqueda] = useState("");
  const [resultados, setResultados] = useState<ClienteResponse[]>([]);
  const { id } = useParams();
  const navigate = useNavigate();


  useEffect(() => {
    if (!id) return;

    IST.get(`/mascotas/${id}`)
      .then(res => {
        const data: MascotaResponse = res.data.data;

        setMascotaSelecc(data); 

        setFormMascota({
          id: data.id,
          nombre: data.nombre,
          sexo: data.sexo,
          idCliente: data.idCliente,
          idRaza: data.idRaza,
          idEspecie: data.idEspecie,
          idEstado: data.idEstado,
          fechaNacimiento: data.fechaNacimiento,
          pelaje: data.pelaje,
          idTamano: data.idTamano,
          idEtapa: data.idEtapa,
          esterilizado: data.esterilizado,
          alergias: data.alergias,
          peso: data.peso,
          chip: data.chip,
          pedigree: data.pedigree,
          factorDea: data.factorDea,
          agresividad: data.agresividad,
          foto: data.foto
        });

        setImagenMascota(data.foto);
      });
  }, [id]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [
          resRazas,
          resEspecies,
          resTamaños,
          resEtapasV,
          resEstadosM,
          resDueños,
        ] = await Promise.all([
          IST.get("/razas"),
          IST.get("/especies"),
          IST.get("/tamanos"),
          IST.get("/etapasVida"),
          IST.get("/estado-mascota"),
          IST.get("/clientes"), 
        ]);

        setRazas(resRazas.data);
        setEspecies(resEspecies.data);
        setTamañosMascota(resTamaños.data);
        setEtapaMascota(resEtapasV.data);
        setEstadoMascota(resEstadosM.data);
        setDueños(resDueños.data.data);
      } catch (error) {
        Swal.fire({
          title: "Error desconocido",
          text: "Por favor refresque la página",
          icon: "error"
        });
      }
    };

    fetchData();
  }, []);

  useEffect(() => {
    if (!formMascota.idCliente || dueños.length === 0) return;

    const dueño = dueños.find(d => d.id === formMascota.idCliente);
    if (dueño) {
      setBusqueda(dueño.nombre);
    }
  }, [formMascota.idCliente, dueños]);

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    // Guardamos el archivo para subirlo después
    setFotoFile(file);

    // Vista previa
    const reader = new FileReader();
    reader.onloadend = () => setImagenMascota(reader.result as string);
    reader.readAsDataURL(file);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    
    if (!formMascota.idCliente) {
      Swal.fire({
        title: "Alerta",
        text: "debe seleccionar un dueño",
        icon: "warning"
      });
      return;
    }
    
    e.preventDefault();

    try {
      let fotoURL = formMascota.foto;

      // Si se seleccionó una imagen, la subimos
      if (fotoFile) {
        const formData = new FormData();
        formData.append("file", fotoFile);

        const nombreArchivoExistente = mascotaSelecc?.foto?.split("/").pop();
        if (nombreArchivoExistente) {
          formData.append("nombreExistente", nombreArchivoExistente);
        } else {
          formData.append("nombreMascota", formMascota.nombre || "mascota");
        }

        const res = await IST.post("/archivos/subir", formData);
        fotoURL = res.data;
      }

      const nuevaMascota: MascotaRequest = {
        ...formMascota,
        foto: fotoURL,
      };

      if (id) {
        IST.put(`/mascotas/${id}`, nuevaMascota)
          .then((res) => {
            Swal.fire({
              title: "Éxito",
              text: "Operación exitosa",
              icon: "success"
            });
            navigate("/administracion/mascotas/lista");
          })
          .catch((err) => {
            Swal.fire({
              title: "Error",
              text: "al actualizar mascota",
              icon: "error"
            });
          });
      } else {
        IST.post("/mascotas", nuevaMascota).then(async (res) => {
          Swal.fire({
              title: "Éxito",
              text: "Operación exitosa",
              icon: "success"
            });

          const idHistoriaMascota = res.data.data.id;

          if (idHistoriaMascota) {
            const historia_clinica = {
              idMascota: idHistoriaMascota,
            };

            try {
              await IST.post("/historia-clinica", historia_clinica);
            } catch (error) {
              Swal.fire({
                title: "Error",
                text: "al crear lista clínica",
                icon: "error"
              });
            }
          }

          navigate("/administracion/mascotas/lista");
        });
      }
    } catch (err) {
      Swal.fire({
        title: "Error",
        text: "al registrar la mascota",
        icon: "error"
      });
    }
  };

  const handleBusqueda = (valor: string) => {
    setBusqueda(valor);

    if (valor.trim() === "") {
      setResultados([]);
      return;
    }
    const filtrados = dueños.filter((d) => {
      const nombreCoincide = d.nombre
        .toLowerCase()
        .includes(valor.toLowerCase());
      const documentoCoincide = d.documento.toString().includes(valor);
      return nombreCoincide || documentoCoincide;
    });
    setResultados(filtrados);
  };

  const eliminarFoto = async () => {
    const nombreArchivo = mascotaSelecc?.foto?.split("/").pop();

    const esImagenBD = nombreArchivo && imagenMascota?.includes(nombreArchivo);

    if (esImagenBD) {
      try {
        const formData = new FormData();
        formData.append("nombreArchivo", nombreArchivo);

        await IST.post("/archivos/eliminar", formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });


        setImagenMascota(null);
        setFotoFile(null);
        setFormMascota(prev => ({ ...prev, foto: "" }));

        Swal.fire({
          title: "Éxito",
          text: "Operación exitosa",
          icon: "success"
        });

        return;
      } catch (error) {
        Swal.fire({
          title: "Error",
          text: "al eliminar la imagen",
          icon: "error"
        });
        return;
      }
    }

    setImagenMascota(null);
    setFotoFile(null);
    setFormMascota(prev => ({ ...prev, foto: "" }));
  };

  return (
    <>
      <div id="regis_mascotas">
        <Br_administrativa onMinimizeChange={setMinimizado} />
        <main className={minimizado ? "minimize" : ""}>
          <div className="content-section hidden" id="nueva-mascota-section">
            <Link className="boton_retorno" to="/administracion/mascotas/lista">
              <i className="fa-solid fa-backward"></i>
            </Link>
            <div className="form-header">
              <h2>Información de la Mascota</h2>
            </div>
            <div className="form-content">
              <div className="form-main">
                <div className="form-fields">
                  <form id="pet-form" onSubmit={handleSubmit}>
                    {mascotaSelecc && (
                      <div className="form-row">
                        <div className="form-group">
                          <label>ID</label>
                          <input
                            type="text"
                            value={mascotaSelecc.codigo}
                            disabled
                            readOnly
                          />
                        </div>
                        <div className="form-group">
                          <label>Fecha de resgitro</label>
                          <input
                            type="text"
                            value={
                              mascotaSelecc.fechaModificacion
                                ? `${mascotaSelecc.fechaModificacion.slice(11, 16)}  del  ${mascotaSelecc.fechaModificacion.split("T")[0]}`
                                : ""
                            }
                            disabled
                            readOnly
                          />
                        </div>
                      </div>
                    )}
                    <div className="form-grid">
                      <section className="formulario_superior">
                        <div className="form_super_datos">
                          <div className="form-group">
                            <label htmlFor="pet-name">
                              Nombre de la Mascota *
                            </label>
                            <input
                              type="text"
                              id="pet-name"
                              value={formMascota.nombre}
                              onChange={(e) => setFormMascota(prev => ({ ...prev, nombre: e.target.value }))}
                              required
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor="birth-date">
                              Fecha de Nacimiento
                            </label>
                            <input
                              type="date"
                              id="birth-date"
                              name="birth-date"
                              value={formMascota.fechaNacimiento}
                              onChange={(e) =>
                                setFormMascota(prev => ({ ...prev, fechaNacimiento: e.target.value}))
                              }
                            />
                          </div>

                          <div className="form-group">
                            <label>Especie *</label>
                            <select
                              value={formMascota.idEspecie}
                              onChange={(e) =>
                                setFormMascota(prev => ({
                                  ...prev,
                                  idEspecie: Number(e.target.value),
                                  idRaza: 0
                                }))
                              }
                              required
                            >
                              <option value="">Seleccionar especie</option>
                              {especies.map((esp) => (
                                <option key={esp.id} value={esp.id}>
                                  {esp.nombre}
                                </option>
                              ))}
                            </select>
                          </div>
                          <div className="form-group">
                            <label>Raza *</label>
                            <select
                              value={formMascota.idRaza}
                              onChange={(e) =>
                                setFormMascota(prev => ({
                                  ...prev,
                                  idRaza: Number(e.target.value),
                                }))
                              }
                              required
                              disabled={!formMascota.idEspecie}
                            >
                              <option value="">Seleccionar raza</option>
                              {razas
                                .filter((r) => r.idEspecie == formMascota.idEspecie)
                                .map((r) => (
                                  <option key={r.id} value={r.id}>
                                    {r.nombre}
                                  </option>
                                ))}
                            </select>
                          </div>
                          <div className="form-group">
                            <label htmlFor="weight">Peso (kg)</label>
                            <input
                              type="number"
                              id="weight"
                              step="0.1"
                              min="0"
                              placeholder="Ej: 5.5"
                              value={formMascota.peso ?? ""}
                              onChange={(e) =>
                                setFormMascota(prev => ({
                                  ...prev,
                                  peso: e.target.value ? Number(e.target.value) : undefined,
                                }))
                              }
                              className="pet-weight"
                            />
                          </div>

                          <div className="form-group">
                            <label>Sexo *</label>
                            <div className="gender-group">
                              <div className="radio-group">
                                <input
                                  type="radio"
                                  id="male"
                                  name="sex"
                                  value="M"
                                  checked={formMascota.sexo === "M"}
                                  onChange={() =>
                                    setFormMascota(prev => ({
                                      ...prev,
                                      sexo: "M",
                                    }))
                                  }
                                />
                                <label htmlFor="male">Macho</label>
                              </div>
                              <div className="radio-group">
                                <input
                                  type="radio"
                                  id="female"
                                  name="sex"
                                  value="H"
                                  checked={formMascota.sexo === "H"}
                                  onChange={() =>
                                    setFormMascota(prev => ({
                                      ...prev,
                                      sexo: "H",
                                    }))
                                  }
                                />
                                <label htmlFor="female">Hembra</label>
                              </div>
                            </div>
                          </div>
                        </div>
                        <div className="photo-section-form">
                          <h4
                            style={{
                              color: "#666",
                              margin: "0 0 10px 0",
                              fontSize: 16,
                              fontWeight: 500,
                            }}
                          >
                            📷 Foto de la Mascota
                          </h4>

                          <label
                            htmlFor="foto-mascota"
                            className="photo-upload-text"
                          >
                            Seleccionar foto
                          </label>

                          <input
                            type="file"
                            id="foto-mascota"
                            accept="image/*"
                            onChange={handleImageChange}
                            style={{ display: "none" }}
                          />

                          <div className="profile-photo" id="pet-photo">
                            {imagenMascota ? (
                              <img
                                src={imagenMascota}
                                alt="Foto de la mascota"
                                style={{
                                  width: "100%",
                                  height: "100%",
                                  objectFit: "cover",
                                  borderRadius: "50%",
                                }}
                              />
                            ) : (
                              "🐕"
                            )}
                          </div>
                          {imagenMascota && (
                            <button
                              type="button"
                              onClick={eliminarFoto}
                              className="btn_quitar_foto"
                            >
                              Quitar imagen ❌
                            </button>
                          )}
                        </div>
                      </section>

                      <div className="form-group">
                        <label>¿Está castrado/a?</label>
                        <div className="gender-group">
                          <div className="radio-group">
                            <input
                              type="radio"
                              id="neutered-yes"
                              name="neutered"
                              checked={formMascota.esterilizado === true}
                              onChange={() =>
                                setFormMascota(prev => ({ ...prev, esterilizado: true }))
                              }
                            />
                            <label htmlFor="neutered-yes">Sí</label>
                          </div>
                          <div className="radio-group">
                            <input
                              type="radio"
                              id="neutered-no"
                              name="neutered"
                              checked={formMascota.esterilizado === false}
                              onChange={() =>
                                setFormMascota(prev => ({ ...prev, esterilizado: false }))
                              }
                            />
                            <label htmlFor="neutered-no">No</label>
                          </div>
                        </div>
                      </div>  

                      <div className="form-group">
                        <label htmlFor="estado">Estado *</label>
                        <select
                          id="estado"
                          value={formMascota.idEstado}
                          onChange={(e) =>
                            setFormMascota(prev => ({
                              ...prev,
                              idEstado: Number(e.target.value)
                            }))
                          }
                          required
                        >
                          <option value="">-- Seleccione estado --</option>
                          {estadoMascota.map((estad) => (
                            <option key={estad.id} value={estad.id}>
                              {estad.nombre}
                            </option>
                          ))}
                        </select>
                      </div>

                      <div className="form-group">
                        <label htmlFor="pelaje">Pelaje *</label>
                        <input
                          type="text"
                          id="pelaje"
                          value={formMascota.pelaje}
                          onChange={(e) =>
                            setFormMascota(prev => ({
                              ...prev,
                              pelaje: e.target.value
                            }))
                          }
                          placeholder="Ej: Corto, largo"
                          required
                        />
                      </div>
                      <div className="form-group">
                        <label htmlFor="tamano">Tamaño *</label>
                        <select
                          id="tamano"
                          value={formMascota.idTamano}
                          onChange={(e) =>
                            setFormMascota(prev => ({
                              ...prev,
                              idTamano: Number(e.target.value)
                            }))
                          }
                          required
                        >
                          <option value="">-- Seleccione tamaño --</option>
                          {tamañosMascota.map((tam) => (
                            <option key={tam.id} value={tam.id}>
                              {tam.descripcion}
                            </option>
                          ))}
                        </select>
                      </div>

                      <div className="form-group">
                        <label htmlFor="etapa-de-vida">Etapa de vida *</label>
                        <select
                          id="etapa-de-vida"
                          value={formMascota.idEtapa}
                          onChange={(e) =>
                            setFormMascota(prev => ({
                              ...prev,
                              idEtapa: Number(e.target.value)
                            }))
                          }
                          required
                        >
                          <option value="">-- Seleccione etapa --</option>
                          {etapaMascota.map((etm) => (
                            <option key={etm.id} value={etm.id}>
                              {etm.descripcion}
                            </option>
                          ))}
                        </select>
                      </div>

                      <div className="form-group">
                        <label htmlFor="alergias">Alergias</label>
                        <input
                          type="text"
                          id="alergias"
                          value={formMascota.alergias}
                          onChange={(e) =>
                            setFormMascota(prev => ({
                              ...prev,
                              alergias: e.target.value
                            }))
                          }
                          placeholder="Ej: Alimentarias o ambientales"
                        />
                      </div>
                      <div className="form-group">
                        <label htmlFor="chip">Chip</label>
                        <input
                          type="checkbox"
                          id="chip"
                          checked={formMascota.chip}
                          onChange={(e) =>
                            setFormMascota(prev => ({
                              ...prev,
                              chip: e.target.checked
                            }))
                          }
                        />

                        <label htmlFor="pedigree">Pedigree</label>
                        <input
                          type="checkbox"
                          id="pedigree"
                          checked={formMascota.pedigree}
                          onChange={(e) =>
                            setFormMascota(prev => ({
                              ...prev,
                              pedigree: e.target.checked
                            }))
                          }
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="factor-dea">Factor DEA</label>
                        <input
                          type="checkbox"
                          id="factor-dea"
                          checked={formMascota.factorDea}
                          onChange={(e) =>
                            setFormMascota(prev => ({
                              ...prev,
                              factorDea: e.target.checked
                            }))
                          }
                        />
                        <label htmlFor="agresividad">Agresivo</label>
                        <input
                          type="checkbox"
                          id="agresividad"
                          checked={formMascota.agresividad}
                          onChange={(e) =>
                            setFormMascota(prev => ({
                              ...prev,
                              agresividad: e.target.checked
                            }))
                          }
                        />
                      </div>
                    </div>
                    <div className="owner-info">
                      <h4>Dueño</h4>
                      <div className="owner-search">
                        <input
                          type="text"
                          placeholder="Buscar cliente por nombre"
                          value={busqueda}
                          onChange={(e) => handleBusqueda(e.target.value)}
                        />
                        <input type="hidden" value={formMascota.idCliente} />
                      </div>

                      {resultados.length > 0 && (
                        <ul className="suggestions-list">
                          {resultados.map((cliente) => (
                            <li
                              key={cliente.id}
                              onClick={() => {
                                setBusqueda(cliente.nombre);

                                setFormMascota(prev => ({
                                  ...prev,
                                  idCliente: cliente.id
                                }));

                                setResultados([]);
                              }}
                            >
                              {cliente.nombre} — {cliente.documento}
                            </li>
                          ))}
                        </ul>
                      )}
                    </div>

                    <button type="submit" className="submit-btn">
                      {mascotaSelecc ? "Actualizar" : "Guardar"}
                    </button>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </>
  );
}

export default Regis_mascotas;
