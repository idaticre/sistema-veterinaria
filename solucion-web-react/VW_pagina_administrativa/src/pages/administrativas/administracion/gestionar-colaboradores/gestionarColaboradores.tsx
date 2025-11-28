import { useEffect, useRef, useState } from 'react';
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa';
import "./styles.css";
import type { ColaboradorRequest, ColaboradorResponse, tipo_doc, TipoPersonaJuridica } from "../../../../components/interfaces/interfaces";
import IST from '../../../../components/proteccion/IST';

const GestionarColaboradores: React.FC = () => {
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
    /* 
        De forma similar a mostrarModal && edicion, usaremos consts para mostrar de forma extensiva la información de un colaborador mediante un
        tercer botón "Más..." uwu
    */
    const [masInformacion, setMasInformacion] = useState<ColaboradorResponse | null>(null);
    const [mostrarModalInformativo, setMostrarModalInformativo] = useState(false);

    // Obtener tipos de documentos
    useEffect(() => {obtenerTiposDocumento();}, []);
    const obtenerTiposDocumento = async () => {
        try {
            const response = await IST.get(`/tipo-documento`);
            setTiposDocumento(response.data);
        } catch (error) {console.error("Error al obtener tipos de documento:", error);}
    }; 

    // Obtener tipos de personas jurídicas
    useEffect(() => {obtenerTiposPersonasJuridicas();}, []);
    const obtenerTiposPersonasJuridicas = async () => {
        try {
            const response = await IST.get(`/tipo-persona-juridica`);
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
            const respuesta = await IST.get(`/colaboradores`);
            const lista = Array.isArray(respuesta.data)
            ? respuesta.data
            : respuesta.data.data;

            const activos = lista.filter((cliente: ColaboradorResponse) => cliente.activo === true);
            setColaboradores(activos);
            setFiltrado(activos);
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
            foto: "",
            idUsuario: null
        }
        setEdicion(nuevo);
        setMostrarModal(true);
    };

    // Formulario para editar
    const abrirFormularioEditar = (colaborador: ColaboradorResponse) => {
        const editado: ColaboradorRequest = {
            id: colaborador.id,                   
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
            foto: colaborador.foto || "",
            idUsuario: colaborador.usuario
        }
        setEdicion(editado);
        setMostrarModal(true);
    };

    // Guardar
    const guardarColaborador = async () => {
        if (!edicion) return;
        
        try {
            if (edicion.id && edicion.id > 0) {await IST.put(`/colaboradores/actualizar`, edicion);} 
            else {await IST.post(`/colaboradores/registrar`, edicion);}
            listarColaboradores();
            setEdicion(null);
            setMostrarModal(false);
        } catch (error) {
            console.error("Error al registrar/actualizar: ", error);
            alert("Error al aregistrar o actualizar");
        }
    }
    
    // Eliminar
    const eliminarColaborador = async (id: number) => {
        try {
            await IST.delete(`/colaboradores/eliminar/${id}`);
            listarColaboradores();
            alert("Eliminación exitosa");
        } catch (error) {
            alert(error);
            console.error("Error al eliminar: ", error);
        }
    }

    // Botón amarillo: ver más información
    const verMasInformacion = (colaborador: ColaboradorResponse) => {
        const fullInfo: ColaboradorResponse = {
            id: colaborador.id,
            codigoColaborador: colaborador.codigoColaborador,
            idEntidad: colaborador.idEntidad,
            nombre: colaborador.nombre,
            sexo: colaborador.sexo,
            documento: colaborador.documento,
            idTipoPersonaJuridica: colaborador.idTipoPersonaJuridica,
            idTipoDocumento: colaborador.idTipoDocumento,
            correo: colaborador.correo,
            telefono: colaborador.telefono,
            direccion: colaborador.direccion,
            ciudad: colaborador.ciudad,
            distrito: colaborador.distrito,
            usuario: colaborador.usuario,
            activo: colaborador.activo,
            fechaRegistro: colaborador.fechaRegistro,
            fechaIngreso: colaborador.fechaIngreso,
            mensaje: colaborador.mensaje         
        }
        setMasInformacion(fullInfo);
        setMostrarModalInformativo(true);
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
                    <table className="GM-table">
                        <thead className="GM-thead">
                            <tr className='GM-tr'>
                                <th className="GM-th" style={{width:"110px"}}>Código</th>
                                <th className="GM-th">Nombre</th>
                                <th className="GM-th" style={{width:"100px"}}>Tipo doc.</th>
                                <th className="GM-th" style={{width:"110px"}}>Documento</th>
                                <th className="GM-th" style={{width:"100px"}}>Teléfono</th>
                                <th className="GM-th" style={{width:"150px"}}>Fecha de ingreso</th>
                                <th className="GM-th" style={{width:"200px"}}>Correo</th>
                                <th className="GM-th">Dirección</th>
                                <th className="GM-th" style={{width:"150px"}}>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filtrado.map((registro) => {
                                const tiposDocumento: Record<number, string> = {
                                    1: "DNI",
                                    2: "RUC",
                                    3: "Carnet de extranjería",
                                    4: "Partida de nacimiento",
                                    5: "Pasaporte",
                                    6: "Otros",
                                };

                                return (
                                <tr key={registro.id}>
                                    <td className="GM-td">{registro.codigoColaborador}</td>
                                    <td className="GM-td">{registro.nombre}</td>
                                    <td className="GM-td">{tiposDocumento[Number(registro.idTipoDocumento)] || "Desconocido"}</td>
                                    <td className="GM-td">{registro.documento}</td>
                                    <td className="GM-td">{registro.telefono}</td>
                                    <td className="GM-td">{registro.fechaIngreso}</td>
                                    <td className="GM-td">{registro.correo}</td>
                                    <td className="GM-td">{registro.direccion}</td>
                                    <td className="GM-td" style={{display:"flex", justifyContent:"center"}}>
                                        <button className="boton-verde" onClick={() => abrirFormularioEditar(registro)}>Editar</button>
                                        <button className="boton-amarillo" onClick={() => verMasInformacion(registro)}>Más...</button>
                                        <button className="boton-rojo" onClick={() => eliminarColaborador(registro.id)}>Eliminar</button>
                                    </td>
                                </tr>
                                );
                            })}
                            </tbody> 
                    </table>
                </section>
            </main>
            
            {mostrarModal && edicion && (
                <div className="ventana-overlay">
                    <div className="contenido-ventana">
                        <h3>Editando colaborador</h3>
                        <p><strong>Siendo registrado el:</strong> {edicion.fechaIngreso}</p>
                        <input type="text" placeholder="Nombre del colaborador" value={edicion?.nombre || ""} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, nombre: nuevoValor.target.value } : null)}/>
                        <select value={edicion?.sexo ?? "M"} onChange={(e) => setEdicion(edicion ? { ...edicion, sexo: e.target.value as "M" | "F" } : null)}>
                            <option value="M">Masculino</option>
                            <option value="F">Femenino</option>
                        </select>
                        <select value={edicion.idTipoPersonaJuridica} onChange={(nuevoValor) => {
                            const nuevoId = Number(nuevoValor.target.value);
                            setEdicion((prev) => prev
                                    ? {
                                        ...prev,
                                        idTipoPersonaJuridica: nuevoId,
                                        idTipoDocumento: nuevoId === 2 ? 2 : prev.idTipoDocumento, // Si se elige "Jurídica", forzamos RUC (id = 2)
                                    }
                                    : null
                                );
                            }}>
                            {tiposPersonasJuridicas.map((tipoPersonaJuridica) => (
                                <option key={tipoPersonaJuridica.id} value={tipoPersonaJuridica.id}>{tipoPersonaJuridica.nombre}</option>
                            ))}
                        </select>
                        <select value={edicion.idTipoDocumento} onChange={(nuevoValor) =>
                            setEdicion((prev) =>prev ? { ...prev, idTipoDocumento: Number(nuevoValor.target.value) } : null)}
                            disabled = {edicion.idTipoPersonaJuridica === 2}>
                            {tiposDocumento
                                // Si se selecciona persona jurídica, solo mostramos el RUC como opción
                                .filter((tipo) => edicion.idTipoPersonaJuridica === 2 ? tipo.id === 2 : true)
                                .map((tipo) => (
                                <option key={tipo.id} value={tipo.id}>{tipo.descripcion}</option>
                                ))}
                        </select>
                        <input type="text" placeholder="Documento" value={edicion.documento} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, documento: nuevoValor.target.value } : null)}/>
                        <input type="text" placeholder="Telefono" value={edicion.telefono} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, telefono: nuevoValor.target.value } : null)}/>
                        <input type="email" placeholder="Ingrese@correo" value={edicion.correo} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, correo: nuevoValor.target.value } : null)}/>
                        <input type="text" placeholder="Dirección" value={edicion.direccion} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, direccion: nuevoValor.target.value } : null)}/>
                        <input type="text" placeholder="Ciudad" value={edicion.ciudad} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, ciudad: nuevoValor.target.value } : null)}/>
                        <input type="text" placeholder="Distrito" value={edicion.distrito} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, distrito: nuevoValor.target.value } : null)}/>
                        <div className="accDeReg">
                            <button onClick={guardarColaborador}>Guardar</button>
                            <button className="" onClick={() => { setMostrarModal(false); setEdicion(null); }}>Cancelar</button>
                        </div>
                    </div>
                </div>
            )}

            {mostrarModalInformativo && masInformacion && (
                <div className="ventana-overlay">
                    <div className="contenido-ventana">
                        <h3>Información de {masInformacion.nombre}</h3>
                        <div className="info-extensiva"><strong>Sexo: </strong>{masInformacion.sexo === "M" ? "Masculino" : "Femenino"}</div>
                        <div className="info-extensiva"><strong>Ciudad: </strong>{masInformacion.ciudad}</div>
                        <div className="info-extensiva"><strong>Distrito: </strong>{masInformacion.distrito}</div>
                    </div>
                    <div className="accDeReg-cer">
                        <button style={{color:"white", background:"red"}} onClick={() => { setMostrarModalInformativo(false); setMasInformacion(null); }}>Cerrar</button>                        
                    </div>
                </div>
            )}
        </div>
    )
}
export default GestionarColaboradores;