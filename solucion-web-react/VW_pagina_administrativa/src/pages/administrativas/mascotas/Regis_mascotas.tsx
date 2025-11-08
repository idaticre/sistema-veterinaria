import { useEffect, useState } from 'react';
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa';
import './regis_mascotas.css';
import type { Razas, Especie, MascotaRequest, ClienteResponse, Estado_Mascota, Tamaño_Mascota, Etapa_Mascota } from '../../../components/interfaces/interfaces';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

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

    const [busqueda, setBusqueda] = useState(""); 
    const [resultados, setResultados] = useState<ClienteResponse[]>([]);
    const [duenoSeleccionado, setDuenoSeleccionado] = useState<ClienteResponse | null>(null);

    const navigate = useNavigate();

    useEffect(() => {
        const fetchData = async () => {
        try {
            const [resRazas, resEspecies, resTamaños, resEtapasV, resEstadosM, resDueños] = await Promise.all([
            axios.get("http://localhost:8088/api/razas"),
            axios.get("http://localhost:8088/api/especies"),
            axios.get("http://localhost:8088/api/tamanos"),
            axios.get("http://localhost:8088/api/etapasVida"),
            axios.get("http://localhost:8088/api/estado-mascota"),
            axios.get("http://localhost:8088/api/clientes"), // esto se cambiara a futuro
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

    const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file) {
            const fileName = `${nombre || "mascota"}_${Date.now()}_${file.name}`; // nombre único simulado
            const fakePath = `/guardados/mascotas/${fileName}`; // ruta simulada dentro del public

            const reader = new FileReader();
            reader.onloadend = () => {
            setImagenMascota(reader.result as string); // para mostrar preview
            setFoto(fakePath); // guardamos solo la ruta simulada en la base de datos
            };
            reader.readAsDataURL(file);
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

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
        foto,
        };

        console.log("Mascota a registrar:", nuevaMascota);

        try {
        const res = await axios.post("http://localhost:8088/api/mascotas/crear", nuevaMascota);
        alert("Mascota registrada correctamente ✅");
        console.log("Respuesta del servidor:", res.data);
        navigate("/administracion/mascotas/lista"); 
        } catch (err) {
        console.error("Error al registrar mascota:", err);
        alert("Error al registrar mascota ❌");
        }
    };

    const handleBusqueda = (valor: string) => {
        setBusqueda(valor);
        console.log("Buscando:", valor);
        if (valor.trim() === "") {
            setResultados([]);
            return;
        }
        const filtrados = dueños.filter((d) =>
            d.nombre.toLowerCase().includes(valor.toLowerCase())
        );
        console.log("Coincidencias encontradas:", filtrados);
        setResultados(filtrados);
    };

    return (
        <>
            <div id="regis_mascotas">
                <Br_administrativa onMinimizeChange={setMinimizado} />
                <main className={minimizado ? 'minimize' : ''}>
                    <div className="content-section hidden" id="nueva-mascota-section">
                            <div className="form-header">
                                <h2>Información de la Mascota</h2>
                            </div>
                            <div className="form-content">
                                <div className="form-main">
                                    <div className="form-fields">
                                        <form id="pet-form" onSubmit={handleSubmit} >
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
                                                            <select value={idEspecie} onChange={(e) => setIdEspecie(Number(e.target.value))} required>
                                                                <option value="">Seleccionar especie</option>
                                                                {especies.map((esp) => (
                                                                    <option key={esp.id} value={esp.id}>{esp.nombre}</option>
                                                                ))}
                                                            </select>
                                                        </div>
                                                                
                                                        <div className="form-group">
                                                            <label>Raza *</label>
                                                            <select value={idRaza} onChange={(e) => setIdRaza(Number(e.target.value))} required>
                                                                <option value="">Seleccionar raza</option>
                                                                {razas.map((r) => (
                                                                    <option key={r.id} value={r.id}>{r.nombre}</option>
                                                                ))}
                                                            </select>
                                                        </div>
                                                                
                                                        {/*<div className="form-group">
                                                            <label htmlFor="color">Color Principal</label>
                                                            <input type="color" id="color" name="color" value="#8B4513" className="pet-color"/>
                                                        </div>*/}
                                                                
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
                                                    placeholder="Buscar cliente por nombre"
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
                                                            setDuenoSeleccionado(cliente);
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

                                                {duenoSeleccionado && (
                                                    <div className="owner-details" style={{ marginTop: "10px" }}>
                                                    <div><strong>Nombre:</strong> {duenoSeleccionado.nombre}</div>
                                                    <div><strong>DNI:</strong> {duenoSeleccionado.documento}</div>
                                                    <div><strong>Teléfono:</strong> {duenoSeleccionado.telefono}</div>
                                                    <div><strong>Email:</strong> {duenoSeleccionado.correo}</div>
                                                    </div>
                                                )}
                                            </div>
                                                    
                                            <button type="submit" className="submit-btn">Registrar Mascota</button>
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

