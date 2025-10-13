import { useEffect, useRef, useState } from 'react'
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa'
import "./styles.css"
import { Link } from "react-router-dom";

interface Entidad {
    ID: number,
    CODIGO: string,
    TIPO_ENTIDAD: string,
    TIPO_PERSONA_JURIDICA: string,
    NOMBRE: string,
    SEXO?: string,
    DOCUMENTO: string,
    TIPO_DOCUMENTO: string,
    TELEFONO?: string,
    CORREO?: string,
    DIRECCION?: string,
    CIUDAD?: string,
    DISTRITO?: string,
    REPRESENTANTE: string,
    FECHA_CREACION: string,
    FECHA_BAJA: string,
    ACTIVO: number
}

interface Colaborador {
    ID: number,
    CODIGO: string,
    ENTIDAD: string,
    NOMBRE: string,
    FECHA_INGRESO: string,
    USUARIO: string,
    ACTIVO: number,
    FOTO?: string
}

function gestionarColaboradores() {
    const [minimizado, setMinimizado] = useState(false);
    const [busqueda, setBusqueda] = useState("");
    const [colaboradores, setColaboradores] = useState<Colaborador[]>([]);
    const [filtrado, setFiltrado] = useState<Colaborador[]>([]);
    const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [edicion, setEdicion] = useState<Colaborador | null>(null);
    const menuRef = useRef<HTMLDivElement | null>(null);

    // Mete datos de ejemplo
    useEffect(() => {
        const ejemplo = [
            {ID: 1, CODIGO: "CODIGO1", ENTIDAD: "COLABORADOR1", NOMBRE: "NOMBRE1", FECHA_INGRESO: "12-12-12", USUARIO: "USUARIO1", ACTIVO: 1, FOTO: "url/ay.jpg"},
            {ID: 2, CODIGO: "CODIGO2", ENTIDAD: "COLABORADOR2", NOMBRE: "NOMBRE2", FECHA_INGRESO: "12-12-12", USUARIO: "USUARIO2", ACTIVO: 1, FOTO: "url/ay.jpg"},
            {ID: 3, CODIGO: "CODIGO3", ENTIDAD: "COLABORADOR3", NOMBRE: "NOMBRE3", FECHA_INGRESO: "12-12-12", USUARIO: "USUARIO3", ACTIVO: 0, FOTO: "url/ay.jpg"},
        ];
        setColaboradores(ejemplo);
        setFiltrado(ejemplo);
    }, []);

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
        const lista = colaboradores.filter(value => value.NOMBRE.toLowerCase().includes(busqueda.toLowerCase()));
        setFiltrado(lista);
    }, [busqueda, colaboradores]);

    // Eliminación de colaboradores
    const eliminarColaborador = (ID: number) => {
        const registros = colaboradores.filter(valor => valor.ID !== ID)
        setColaboradores(registros);
        setFiltrado(registros);
        setMenuActivoId(null);
    }

    // Registrar colaborador beibi
    const registrarColaborador = () => {
        const nuevo: Colaborador = {
            ID: colaboradores.length + 1,
            CODIGO: "",
            ENTIDAD: "",
            NOMBRE: "",
            FECHA_INGRESO: new Date().toISOString().split("T")[0],
            USUARIO: "",
            ACTIVO: 2, // Al crear uno nuevo, siempre estará activo
            FOTO: ""    
        }
        setEdicion(nuevo);
        setMostrarModal(true);
    }

    // Editar colaboradores
    const editarColaborador = (ID: number) => {
        const colaboradorEditado = colaboradores.find(colaborador => colaborador.ID === ID);
        if (colaboradorEditado) {
            setEdicion(colaboradorEditado);
            setMostrarModal(true);
        }
    }

    // Guardar colaborador
    const guardarColaborador = () => {
        if (!edicion) return;
        if (!edicion.ENTIDAD.trim()) {alert("Ingresar una entidad es obligatorio"); return;}
        if (!edicion.NOMBRE.trim()) {alert("Ingresar un nombre es obligatorio"); return;}
        if (!edicion.USUARIO.trim()) {alert("Ingresar un usuario es obligatorio"); return;}
        if (!edicion.ACTIVO) {alert("Debes seleccionar un estado (Activo/Inactivo/Suspendido)"); return;}

        const existe = colaboradores.some(colaborador => colaborador.ID === edicion.ID);
        if (existe) {
            const registros = colaboradores.map(colaborador => colaborador.ID === edicion.ID ? edicion : colaborador);
            setColaboradores(registros);
            setFiltrado(registros);
        } else {
            const nuevo: Colaborador = {
                ...edicion,
                ID: colaboradores.length + 1, // ALERTA "ERROR DE DUPLICADOS", NO USAR ESTO CUANDO CONECTEMOS LA BASE DE DATOS, DEJA QUE EL BACKEND EN return ASIGNE EL ID (para pruebas locales está bien)
                CODIGO: edicion.CODIGO || "CODIGO0", // CODIGO está siendo generado manualmente, editar cuando se conecte la base de datos para cumplir con la COMPOUND KEY
                FECHA_INGRESO: new Date().toISOString().split("T")[0],
            };
            const registros = [...colaboradores, nuevo];
            setColaboradores(registros);
            setFiltrado(registros);
        }

        setEdicion(null);
        setMostrarModal(false);
    }

    return (
        <div id="cuerpo-main">
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? "minimize" : ""}>
                <section id="listar-registros">
                    <div className="encabezado"><h2>Lista de colaboradores</h2></div>
                    <div className="goated">
                        <div className="barra-buscador"><input type="text" placeholder="Ingrese el nombre del colaborador que desea buscar 🔍" value={busqueda} onChange={(e) => setBusqueda(e.target.value)}/></div>
                        <button className="boton-goated anadir-a-goated animacion-goated" onClick={registrarColaborador}>Registrar colaborador</button>
                    </div>

                    <div className="listar-registros">
                        {filtrado.map((registro) => (
                            <div className="mostrar-registros" key={registro.ID}>
                                <span className="texto-de-registro">{registro.CODIGO}</span>
                                <span className="texto-de-registro">{registro.ENTIDAD}</span>
                                <span className="texto-de-registro">{registro.NOMBRE}</span>
                                <span className="texto-de-registro">{{1: "Inactivo", 2: "Activo", 3: "Suspendido"}[registro.ACTIVO] || "Desconocido"}</span>                             
                                <span className="texto-de-registro">{registro.USUARIO}</span>
                                <span className="texto-de-registro">📅{registro.FECHA_INGRESO}</span>
                                <div className="listar-opciones-contenedor">
                                    <div className="listar-registro-opciones" onClick={() => setMenuActivoId(registro.ID)}><i className="fa-solid fa-ellipsis-vertical"/></div>
                                    {menuActivoId === registro.ID && (
                                        <div ref={menuRef} className="menu-opciones">
                                            <button onClick={() => editarColaborador(registro.ID)}>✏️ Editar</button>
                                            <button onClick={() => eliminarColaborador(registro.ID)}>🗑️ Eliminar</button>
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
                        <p><strong>Siendo registrado el:</strong> {edicion.FECHA_INGRESO}</p>
                        {/*
                        <input type="text" placeholder="Nombre del colaborador" value={edicion?.NOMBRE || ""} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, NOMBRE: nuevoValor.target.value } : null)}/>
                        <select value={edicion?.SEXO} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, SEXO: nuevoValor.target.value } : null)}>
                            <option value="">-- Seleccionar sexo --</option>
                            <option value="hombre">Hombre</option>
                            <option value="mujer">Mujer</option>
                        </select>
                        <select value={edicion.TIPO_PERSONA_JURIDICA} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, TIPO_PERSONA_JURIDICA: nuevoValor.target.value } : null)}>
                            <option value="">-- Seleccionar tipo de persona jurídica --</option>
                            <option value="derecho-publico">Derecho público</option>
                            <option value="derecho-privado">Derecho privado</option>
                        </select>
                        <select value={edicion.TIPO_DOCUMENTO} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, TIPO_DOCUMENTO: nuevoValor.target.value } : null)}>
                            <option value="">-- Seleccionar tipo de documento</option>
                            <option value="dni">DNI</option>
                            <option value="ce">CE</option>
                            <option value="ruc">RUC</option>
                        </select>
                        <input type="text" placeholder="Documento" value={edicion.DOCUMENTO } onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, DOCUMENTO: nuevoValor.target.value } : null)}/>
                        <input type="text" placeholder="Telefono" value={edicion.TELEFONO} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, TELEFONO: nuevoValor.target.value } : null)}/>
                        <input type="email" placeholder="Ingrese@correo" value={edicion.CORREO} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, CORREO: nuevoValor.target.value } : null)}/>
                        <input type="text" placeholder="Dirección" value={edicion.DIRECCION} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, DIRECCION: nuevoValor.target.value } : null)}/>
                        <input type="text" placeholder="Ciudad" value={edicion.CIUDAD} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, CIUDAD: nuevoValor.target.value } : null)}/>
                        <input type="text" placeholder="Distrito" value={edicion.DISTRITO} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, DISTRITO: nuevoValor.target.value } : null)}/>
                        */}
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
/* Checar los comentarios, los errores son generalmente los mismos en los 3 CRUDS. */
export default gestionarColaboradores