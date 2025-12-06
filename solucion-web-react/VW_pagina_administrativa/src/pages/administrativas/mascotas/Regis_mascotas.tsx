import { useEffect, useState } from 'react';
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa';
import './regis_mascotas.css';
import type { Razas, Especie, MascotaRequest, ClienteResponse, Estado_Mascota, Tamaño_Mascota, Etapa_Mascota, MascotaResponse } from '../../../components/interfaces/interfaces';
import IST from '../../../components/proteccion/IST';
import { Link, useLocation, useNavigate } from 'react-router-dom';

type Mascotaextendido = MascotaResponse & { nombre_dueño?: string;};

function Regis_mascotas() {
    const [minimizado, setMinimizado] = useState(false);
    const [imagenMascota, setImagenMascota] = useState<string | null>(null);
    const [dueños, setDueños] = useState<ClienteResponse[]>([]);
    const [razas, setRazas] = useState<Razas[]>([]);
    const [especies, setEspecies] = useState<Especie[]>([]);
    const [estadoMascota, setEstadoMascota] = useState<Estado_Mascota[]>([]);
    const [tamañosMascota, setTamañosMascota] = useState<Tamaño_Mascota[]>([]);
    const [etapaMascota, setEtapaMascota] = useState<Etapa_Mascota[]>([]);

    const [nombre, setNombre] = useState("");
    const [sexo, setSexo] = useState<"M" | "H" | undefined>(undefined);
    const [idCliente, setIdCliente] = useState<number>(0);
    const [idRaza, setIdRaza] = useState<number>(0);
    const [idEspecie, setIdEspecie] = useState<number>(0);
    const [idEstado, setIdEstado] = useState<number>(1);
    const [fechaNacimiento, setFechaNacimiento] = useState("");
    const [pelaje, setPelaje] = useState("");
    const [idTamano, setIdTamaño] = useState<number>(0);
    const [idEtapa, setIdEtapa] = useState<number>(0);
    const [esterilizado, setEsterilizado] = useState<boolean>(false);
    const [alergias, setAlergias] = useState("");
    const [peso, setPeso] = useState<number | undefined >(undefined);
    const [chip, setChip] = useState(false);
    const [pedigree, setPedigree] = useState(false);
    const [factorDea, setFactorDea] = useState(false);
    const [agresividad, setAgresividad] = useState(false);
    const [foto, setFoto] = useState("");
    const [fotoFile, setFotoFile] = useState<File | null>(null);

    const [busqueda, setBusqueda] = useState(""); 
    const [resultados, setResultados] = useState<ClienteResponse[]>([]);

    const location = useLocation();
    const mascotaSelecc = location.state?.mascotaSeleccionado as Mascotaextendido | undefined;

    const navigate = useNavigate();

    useEffect(() => {
        const fetchData = async () => {
        try {
            const [resRazas, resEspecies, resTamaños, resEtapasV, resEstadosM, resDueños] = await Promise.all([
            IST.get("/razas"),
            IST.get("/especies"),
            IST.get("/tamanos"),
            IST.get("/etapasVida"),
            IST.get("/estado-mascota"),
            IST.get("/clientes"), // esto se cambiara a futuro
            ]);

            setRazas(resRazas.data);
            setEspecies(resEspecies.data);
            setTamañosMascota(resTamaños.data);
            setEtapaMascota(resEtapasV.data);
            setEstadoMascota(resEstadosM.data);
            setDueños(resDueños.data.data);
        } catch (error) {
            console.error("Error al obtener datos:", error);
        }
        };

        fetchData();
    }, []);

    useEffect(() => {
        if (mascotaSelecc) {
            setNombre(mascotaSelecc.nombre || "");
            setSexo(mascotaSelecc.sexo as "M" | "H" | undefined);
            setIdCliente(mascotaSelecc.idCliente || 0);
            setIdRaza(mascotaSelecc.idRaza || 0);
            setIdEspecie(mascotaSelecc.idEspecie || 0);
            setIdEstado(mascotaSelecc.idEstado || 0);
            setFechaNacimiento(mascotaSelecc.fechaNacimiento || "");
            setPelaje(mascotaSelecc.pelaje || "");
            setIdTamaño(mascotaSelecc.idTamano || 0);
            setIdEtapa(mascotaSelecc.idEtapa || 0);
            setEsterilizado(mascotaSelecc.esterilizado || false);
            setAlergias(mascotaSelecc.alergias || "");
            setPeso(mascotaSelecc.peso || undefined);
            setChip(mascotaSelecc.chip || false);
            setPedigree(mascotaSelecc.pedigree || false);
            setFactorDea(mascotaSelecc.factorDea || false);
            setAgresividad(mascotaSelecc.agresividad || false);
            setFoto(mascotaSelecc.foto || "");
            setImagenMascota(mascotaSelecc.foto || null);
        }
    }, [mascotaSelecc]);

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
        e.preventDefault();

        try {
            let fotoURL = foto;

            // Si se seleccionó una imagen, la subimos
            if (fotoFile) {
                const formData = new FormData();
                formData.append("file", fotoFile);
                
                const nombreArchivoExistente = mascotaSelecc?.foto?.split("/").pop();
                if (nombreArchivoExistente) {
                    formData.append("nombreExistente", nombreArchivoExistente);
                } else {
                    formData.append("nombreMascota", nombre || "mascota");
                }

                const res = await IST.post("/archivos/subir", formData);
                fotoURL = res.data;
            }

            const nuevaMascota: MascotaRequest = {
            nombre,
            sexo,
            idCliente,
            idRaza,
            idEspecie,
            idEstado,
            fechaNacimiento,
            pelaje,
            idTamano,
            idEtapa,
            esterilizado,
            alergias,
            peso,
            chip,
            pedigree,
            factorDea,
            agresividad,
            foto: fotoURL,
            };

            if(mascotaSelecc){
                IST.put(`/mascotas/actualizar/${mascotaSelecc.id}`, nuevaMascota)
                .then(res => {
                    console.log("cliente actualizado:", res.data);
                    alert("Mascota actualizada correctamente ✅");
                    navigate("/administracion/mascotas/lista"); 
                })
                .catch(err => {
                    console.error("Error al actualizar mascota", err);
                    alert("Error al actualizar mascota ❌");
                });
            }else{
                IST.post("/mascotas/crear", nuevaMascota)
                .then(async res => {
                    console.log("Respuesta del servidor:", res.data);
                    alert("Mascota registrada correctamente ✅");

                    const idHistoriaMascota = res.data.data.id;

                    if (idHistoriaMascota) {
                        
                        const historia_clinica = {
                            idMascota: idHistoriaMascota
                        };

                        try{ 
                            await IST.post("/historia-clinica/crear", historia_clinica);
                        } catch (error) {
                            console.error("Error al crear historia clínica", error);
                        }
                    };

                    navigate("/administracion/mascotas/lista"); 
                })
            }

        }catch(err) {
            console.error("Error al registrar mascota:", err);
            alert("Error al registrar mascota ❌");
        };
        
    };

    const handleBusqueda = (valor: string) => {
        setBusqueda(valor);
        console.log("Buscando:", valor);
        
        if (valor.trim() === "") {
            setResultados([]);
            return;
        }
        const filtrados = dueños.filter((d) =>{
            const nombreCoincide = d.nombre.toLowerCase().includes(valor.toLowerCase());
            const documentoCoincide = d.documento.toString().includes(valor);
            return nombreCoincide || documentoCoincide;
        });
        console.log("Coincidencias encontradas:", filtrados);
        setResultados(filtrados);
    };
    
    const eliminarFoto = async () => {
        const nombreArchivo = mascotaSelecc?.foto?.split("/").pop();

        const esImagenBD =
            nombreArchivo && imagenMascota?.includes(nombreArchivo);

        if (esImagenBD) {
            try {
                const formData = new FormData();
                formData.append("nombreArchivo", nombreArchivo);

                await IST.post("/archivos/eliminar", formData, {
                    headers: { "Content-Type": "multipart/form-data" },
                });

                console.log("Imagen eliminada del servidor ✔");

                setImagenMascota(null);
                setFotoFile(null);
                setFoto("");

                alert("Imagen eliminada ✔");

                return;
            } catch (error) {
                console.error("Error eliminando archivo:", error);
                alert("Error eliminando la imagen ❌");
                return;
            }
        }

        setImagenMascota(null);
        setFotoFile(null);
        setFoto("");
    };

    return (
        <>
            <div id="regis_mascotas">
                <Br_administrativa onMinimizeChange={setMinimizado} />
                <main className={minimizado ? 'minimize' : ''}>
                    <div className="content-section hidden" id="nueva-mascota-section">
                            <Link className='boton_retorno' to="/administracion/mascotas/lista"><i className="fa-solid fa-backward"></i></Link>
                            <div className="form-header">
                                <h2>Información de la Mascota</h2>
                            </div>
                            <div className="form-content">
                                <div className="form-main">
                                    <div className="form-fields">
                                        <form id="pet-form" onSubmit={handleSubmit} >
                                            {mascotaSelecc && (
                                                <div className="form-row">
                                                    <div className="form-group">
                                                        <label>ID</label>
                                                        <input type="text" value={mascotaSelecc.codigo} disabled readOnly/>
                                                    </div>
                                                    <div className='form-group'>
                                                        <label>Fecha de resgitro</label>
                                                        <input type="text" value={
                                                        mascotaSelecc.fechaModificacion ? 
                                                        `${mascotaSelecc.fechaModificacion.slice(11, 16)}  del  ${mascotaSelecc.fechaModificacion.split('T')[0]}` : '' } disabled readOnly/>
                                                    </div>
                                                </div>
                                            )}
                                            <div className="form-grid">
                                                <section className='formulario_superior'>
                                                    <div className='form_super_datos'>
                                                        <div className="form-group">
                                                            <label htmlFor="pet-name">Nombre de la Mascota *</label>
                                                            <input type="text" id="pet-name" value={nombre} onChange={(e) => setNombre(e.target.value)} required />
                                                        </div>
                                                            
                                                        <div className="form-group">
                                                            <label htmlFor="birth-date">Fecha de Nacimiento</label>
                                                            <input type="date" id="birth-date" name="birth-date"  value={fechaNacimiento} onChange={(e) => setFechaNacimiento(e.target.value)} />
                                                        </div>
                                                            
                                                        <div className="form-group">
                                                            <label>Especie *</label>
                                                            <select value={idEspecie} onChange={(e) => {
                                                                    setIdEspecie(Number(e.target.value)); 
                                                                    setIdRaza(0); }} required>
                                                                <option value="">Seleccionar especie</option>
                                                                {especies.map((esp) => (
                                                                    <option key={esp.id} value={esp.id}>{esp.nombre}</option>
                                                                ))}
                                                            </select>
                                                        </div>
                                                                
                                                        <div className="form-group">
                                                            <label>Raza *</label>
                                                            <select value={idRaza} onChange={(e) => setIdRaza(Number(e.target.value))} required disabled ={!idEspecie}>
                                                                <option value="">Seleccionar raza</option>
                                                                {razas.filter((r) => r.idEspecie == idEspecie)
                                                                    .map((r)=>(
                                                                        <option key={r.id} value={r.id}>{r.nombre}</option>
                                                                    )) 
                                                                }
                                                            </select>
                                                        </div>
                                                                
                                                        <div className="form-group">
                                                            <label htmlFor="weight">Peso (kg)</label>
                                                            <input type="number" id="weight" name="weight" 
                                                                    step="0.1" min="0" placeholder="Ej: 5.5"
                                                                    value={peso} onChange={(e) => setPeso(Number(e.target.value))} className="pet-weight"/>
                                                        </div>
                                                                
                                                        <div className="form-group">
                                                            <label>Sexo *</label>
                                                            <div className="gender-group">
                                                                <div className="radio-group">
                                                                    <input type="radio" id="male" name="sex" value="M" checked={sexo === "M"} onChange={() => setSexo("M")} />
                                                                    <label htmlFor="male">Macho</label>
                                                                </div>
                                                                <div className="radio-group">
                                                                    <input type="radio" id="female" name="sex" value="H" checked={sexo === "H"} onChange={() => setSexo("H")} />
                                                                    <label htmlFor="female">Hembra</label>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    
                                                    <div className="photo-section-form">
                                                        <h4 style={{ color: "#666", margin: "0 0 10px 0", fontSize: 16, fontWeight: 500, }}>
                                                            📷 Foto de la Mascota
                                                        </h4>

                                                        <label htmlFor="foto-mascota" className="photo-upload-text">
                                                            Seleccionar foto
                                                        </label>

                                                        <input type="file" id="foto-mascota" accept="image/*" onChange={handleImageChange} style={{ display: "none" }} />

                                                        <div className="profile-photo" id="pet-photo">
                                                            {imagenMascota ? (
                                                            <img src={imagenMascota} alt="Foto de la mascota" style={{
                                                                width: "100%", height: "100%", objectFit: "cover", borderRadius: "50%", }} />
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
                                                            <input type="radio" id="neutered-yes" name="neutered" value="true" checked={esterilizado === true} onChange={() => setEsterilizado(true)}/>
                                                            <label htmlFor="neutered-yes">Sí</label>
                                                        </div>
                                                        <div className="radio-group">
                                                            <input type="radio" id="neutered-no" name="neutered" value="false" checked={esterilizado === false} onChange={() => setEsterilizado(false)} />
                                                            <label htmlFor="neutered-no">No</label>
                                                        </div>
                                                    </div>
                                                </div>
                                               
                                                <div className="form-group">
                                                    <label htmlFor="estado">Estado *</label>
                                                    <select id="estado" name="estado" value={idEstado} onChange={(e) => setIdEstado(Number(e.target.value))} required>
                                                        <option selected>-- Seleccione estado --</option>
                                                        {estadoMascota.map((estad) =>(
                                                            <option key={estad.id} value={estad.id}>{estad.nombre}</option>
                                                        ))}
                                                    </select>
                                                </div>
                                                
                                                {/*<div className="form-group">
                                                    <label htmlFor="veterinario">Veterinario a cargo *</label>
                                                    <select id="veterinario" name="veterinario" required>
                                                        <option selected>-- FALTA DESARROLLAR --</option>
                                                    </select>
                                                </div> */}

                                                <div className="form-group">
                                                    <label htmlFor="pelaje">Pelaje *</label>
                                                    <input type="text" id="pelaje" name="pelaje" value={pelaje} onChange={(e) => setPelaje(e.target.value)} placeholder="Ej: Corto, largo" required />
                                                </div>

                                                <div className="form-group">
                                                    <label htmlFor="tamano">Tamaño *</label>
                                                    <select id="tamano" name="tamano" value={idTamano} onChange={(e) => setIdTamaño(Number(e.target.value))} required>
                                                        <option selected>-- Seleccione tamaño --</option>
                                                        {tamañosMascota.map((tam) => (
                                                            <option key={tam.id} value={tam.id}>{tam.descripcion}</option>
                                                        ))}
                                                    </select>
                                                </div>

                                                <div className="form-group">
                                                    <label htmlFor="etapa-de-vida">Etapa de vida *</label>
                                                    <select id="etapa-de-vida" name="etapa-de-vida" value={idEtapa} onChange={(e) => setIdEtapa(Number(e.target.value))} required>
                                                        <option selected>-- Seleccione etapa --</option>
                                                        {etapaMascota.map((etm) => (
                                                            <option key={etm.id} value={etm.id}>{etm.descripcion}</option>
                                                        ))}
                                                    </select>
                                                </div>

                                                <div className="form-group">
                                                    <label htmlFor="alergias">Alergias </label>
                                                    <input type="text" id="alergias" name="alergias" value={alergias} onChange={(e) => setAlergias(e.target.value)} placeholder="Ej: Alimentarias o ambientales"/>
                                                </div>

                                                <div className="form-group">
                                                    <label htmlFor="chip">Chip</label>
                                                    <input type="checkbox" id="chip" checked={chip} onChange={(e) => setChip(e.target.checked)} name="chip"/>

                                                    <label htmlFor="pedigree">Pedigree</label>
                                                    <input type="checkbox" id="pedigree" name="pedigree" checked={pedigree} onChange={(e) => setPedigree(e.target.checked)} />
                                                </div>

                                                <div className="form-group">
                                                    <label htmlFor="factor-dea">Factor DEA</label>
                                                    <input type="checkbox" id="factor-dea" name="factor-dea" checked={factorDea} onChange={(e) => setFactorDea(e.target.checked)}/>

                                                    <label htmlFor="agresividad">Agresivo</label>
                                                    <input type="checkbox" id="agresividad" name="agresividad" checked={agresividad} onChange={(e) => setAgresividad(e.target.checked)}/>
                                                </div>
                                            </div>
                                            <div className="owner-info">
                                                <h4>Dueño</h4>
                                                <div className="owner-search">
                                                    <input
                                                    type="text"
                                                    placeholder= {mascotaSelecc? mascotaSelecc.nombre_dueño : "Buscar cliente por nombre"}
                                                    value={busqueda}
                                                    onChange={(e) => handleBusqueda(e.target.value)}
                                                    />
                                                    <input type="hidden" name="idCliente" value={idCliente} />
                                                </div>

                                                {/* Lista de coincidencias */}
                                                {resultados.length > 0 && (
                                                    <ul className="suggestions-list">
                                                        {resultados.map((cliente) => (
                                                        <li
                                                            key={cliente.id}
                                                            onClick={() => {
                                                            setBusqueda(cliente.nombre);
                                                            setIdCliente(cliente.id);
                                                            setResultados([]);
                                                            }}
                                                        >
                                                            {cliente.nombre} — {cliente.documento}
                                                        </li>
                                                        ))}
                                                    </ul>
                                                )}
                                            </div>
                                                    
                                            <button type="submit" className="submit-btn">{mascotaSelecc? "Actualizar" : "Guardar"}</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                    </div>
                </main>    
            </div>
        </>
    )
}

export default Regis_mascotas