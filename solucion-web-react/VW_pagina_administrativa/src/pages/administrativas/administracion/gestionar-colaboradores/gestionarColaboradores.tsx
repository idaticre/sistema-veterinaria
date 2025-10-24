import { useEffect, useRef, useState } from 'react';
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa';
import "./styles.css";
import type { ColaboradorRequest, ColaboradorResponse, tipo_doc, TipoPersonaJuridica } from "../../../../components/interfaces/interfaces";
import axios from 'axios';

const gestionarColaboradores: React.FC = () => {
    const [minimizado, setMinimizado] = useState(false);
    const [busqueda, setBusqueda] = useState("");
    const [colaboradores, setColaboradores] = useState<ColaboradorResponse[]>([]);
    const [filtrado, setFiltrado] = useState<ColaboradorResponse[]>([]);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [edicion, setEdicion] = useState<ColaboradorRequest | null>(null);
    const [tiposDocumento, setTiposDocumento] = useState<tipo_doc[]>([]);
    const [tiposPersonasJuridicas, setTiposPersonasJuridicas] = useState<TipoPersonaJuridica[]>([]);
    const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
    const menuRef = useRef<HTMLDivElement | null>(null);
    const baseURL = "http://localhost:8088/api";
    
    // Obtener tipos de documentos
    useEffect(() => {obtenerTiposDocumento();}, []);
    const obtenerTiposDocumento = async () => {
        try {
            const response = await axios.get(`${baseURL}/tipo-documento`);
            setTiposDocumento(response.data);
        } catch (error) {console.error("Error al obtener tipos de documento:", error);}
    }; 

    // Obtener tipos de personas jurídicas
    useEffect(() => {obtenerTiposPersonasJuridicas();}, []);
    const obtenerTiposPersonasJuridicas = async () => {
        try {
            const response = await axios.get(`${baseURL}/tipo-persona-juridica`);
            setTiposPersonasJuridicas(response.data);
        } catch (error) {console.error("Error al obtener tipos de personas jurídicas", error);}
    };

    // Efecto de cerrar ventana
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
        if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
            setMenuActivoId(null);
        }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    // Filtra por búsqueda
    useEffect(() => {
        const lista = colaboradores.filter(value => value.nombre.toLowerCase().includes(busqueda.toLowerCase()));
        setFiltrado(lista);
    }, [busqueda, colaboradores]);

    // Listar
    useEffect(() => {listarColaboradores();}, []);
    const listarColaboradores = async () => {
        try {
            const respuesta = await axios.get(`${baseURL}/colaboradores`);
            console.log("Respuesta del backend:", respuesta.data);
            const lista = Array.isArray(respuesta.data)
            ? respuesta.data
            : respuesta.data.data;

            setColaboradores(lista);
            setFiltrado(lista);
        } catch (error) {console.error("Error al obtener los colaboradores", error);}
    };

    // Formulario nuevo y vacío
    const abrirFormularioNuevo = () => {
        const nuevo: ColaboradorRequest = {
            nombre: "",
            sexo: "M",
            documento: "",
            idTipoPersonaJuridica: 0,
            idTipoDocumento: 0,
            correo: "",
            telefono: "",
            direccion: "",
            ciudad: "",
            distrito: "",
            activo: true,
            fechaIngreso: new Date().toISOString().split("T")[0],
            foto: ""
        }
        setEdicion(nuevo);
        setMostrarModal(true);
    };

    // Formulario para editar
    const abrirFormularioEditar = (colaborador: ColaboradorResponse) => {
        const editado: ColaboradorRequest = {
            id: colaborador.idColaborador,                   
            nombre: colaborador.nombre,
            sexo: colaborador.sexo === "M" ? "M" : "F",          
            documento: colaborador.documento,
            idTipoPersonaJuridica: colaborador.idTipoPersonaJuridica,
            idTipoDocumento: colaborador.idTipoDocumento,
            correo: colaborador.correo,
            telefono: colaborador.telefono,
            direccion: colaborador.direccion,
            ciudad: colaborador.ciudad,
            distrito: colaborador.distrito,
            activo: colaborador.activo,                           
            fechaIngreso: colaborador.fechaIngreso,                
            foto: colaborador.foto || ""                           
        }
        setEdicion(editado);
        setMostrarModal(true);
    };

    // Guardar
    const guardarColaborador = async () => {
        if (!edicion) return;
        

        try {
            if (edicion.id && edicion.id > 0) {
                const response = await axios.put(`${baseURL}/colaboradores/actualizar/${edicion.id}`, edicion);
                console.log("Respuesta backend:", response.data);
            } else {
                const response = await axios.post(`${baseURL}/colaboradores/registrar`, edicion);
                console.log("Respuesta backend:", response.data);
            }
            listarColaboradores();
            setEdicion(null);
            setMostrarModal(false);
        } catch (error) {
            console.log("Datos enviados al backend:", edicion);
            console.error("Error al registrar/actualizar: ", error);
            alert(error);
        }
    }
    
    // Eliminar
    const eliminarColaborador = async (id: number) => {
        try {
            await axios.delete(`${baseURL}/colaboradores/eliminar/${id}`, { data: { id } });
            listarColaboradores();
        } catch (error) {
            alert(error);
            console.error("Error al eliminar: ", error);
        }
    }

    return (
        <div id="cuerpo-main">
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? "minimize" : ""}>
                <section id="listar-registros">
                    <div className="encabezado"><h2>Lista de colaboradores</h2></div>
                    <div className="goated">
                        <div className="barra-buscador"><input type="text" placeholder="Ingrese el nombre del colaborador que desea buscar 🔍" value={busqueda} onChange={(e) => setBusqueda(e.target.value)}/></div>
                        <button className="boton-goated anadir-a-goated animacion-goated" onClick={abrirFormularioNuevo}>Nuevo colaborador</button>
                    </div>

                    <div className="listar-registros">
                        {filtrado.map((registro) => (
                            <div className="mostrar-registros" key={registro.idColaborador}>
                                <span className="texto-de-registro">{registro.codigoColaborador}</span>
                                <span className="texto-de-registro">{registro.nombre}</span>
                                <span className="texto-de-registro">{registro.documento}</span>
                                <span className="texto-de-registro">{registro.telefono}</span>
                                <span className="texto-de-registro">📅{registro.fechaIngreso}</span>
                                <div className="listar-opciones-contenedor">
                                    <div className="listar-registro-opciones" onClick={() => setMenuActivoId(registro.idColaborador)}><i className="fa-solid fa-ellipsis-vertical"/></div>
                                    {menuActivoId === registro.idColaborador && (
                                        <div ref={menuRef} className="menu-opciones-gm">
                                            <button onClick={() => abrirFormularioEditar(registro)}>✏️ Editar</button>
                                            <button onClick={() => eliminarColaborador(registro.idColaborador)}>🗑️ Eliminar</button>
                                        </div>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                </section>
            </main>
            
            {mostrarModal && edicion && (
                <div className="ventana-overlay">
                    <div className="contenido-ventana">
                        <h3>Información del colaborador</h3>
                        <p><strong>Siendo registrado el:</strong> {edicion.fechaIngreso}</p>
                        <input type="text" placeholder="Nombre del colaborador" value={edicion?.nombre || ""} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, nombre: nuevoValor.target.value } : null)}/>
                        <select value={edicion?.sexo ?? "M"} onChange={(e) => setEdicion(edicion ? { ...edicion, sexo: e.target.value as "M" | "F" } : null)}>
                            <option value="">-- Seleccionar sexo --</option>
                            <option value="M">Masculino</option>
                            <option value="F">Femenino</option>
                        </select>
                        <select value={edicion.idTipoPersonaJuridica} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, idTipoPersonaJuridica: Number(nuevoValor.target.value) } : null)}>
                            <option value="">-- Seleccionar tipo de persona jurídica--</option>
                            {tiposPersonasJuridicas.map((tipoPersonaJuridica) => (<option key={tipoPersonaJuridica.id} value={tipoPersonaJuridica.id}>{tipoPersonaJuridica.nombre}</option>))}
                        </select>
                        <select value={edicion.idTipoDocumento} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, idTipoDocumento: Number(nuevoValor.target.value) } : null)}>
                            <option value="">-- Seleccionar tipo de documento</option>
                            {tiposDocumento.map((tipo) => (<option key={tipo.id} value={tipo.id}>{tipo.descripcion}</option>))}
                        </select>
                        <input type="text" placeholder="Documento" value={edicion.documento} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, documento: nuevoValor.target.value } : null)}/>
                        <input type="text" placeholder="Telefono" value={edicion.telefono} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, telefono: nuevoValor.target.value } : null)}/>
                        <input type="email" placeholder="Ingrese@correo" value={edicion.correo} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, correo: nuevoValor.target.value } : null)}/>
                        <input type="text" placeholder="Dirección" value={edicion.direccion} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, direccion: nuevoValor.target.value } : null)}/>
                        <input type="text" placeholder="Ciudad" value={edicion.ciudad} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, ciudad: nuevoValor.target.value } : null)}/>
                        <input type="text" placeholder="Distrito" value={edicion.distrito} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, distrito: nuevoValor.target.value } : null)}/>
                        
                        <div className="acciones-de-registro">
                            <button onClick={guardarColaborador}>Guardar</button>
                            <button onClick={() => { setMostrarModal(false); setEdicion(null); }}>Cancelar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}
export default gestionarColaboradores;