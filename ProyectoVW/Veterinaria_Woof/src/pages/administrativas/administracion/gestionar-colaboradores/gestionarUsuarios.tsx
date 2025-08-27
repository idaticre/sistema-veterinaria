import { useEffect, useRef, useState } from 'react'
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa'
import "./styles.css"
import { Link } from "react-router-dom";

interface Usuario {
    ID: number,
    CODIGO: string,
    USERNAME: string,
    PASSWORD: string,
    ACTIVO: number,
    FECHA_CREACION: string,
    FECHA_BAJA?: string
}

function gestionarUsuarios() {
    const [minimizado, setMinimizado] = useState(false);
    const [busqueda, setBusqueda] = useState("");
    const [usuarios, setUsuarios] = useState<Usuario[]>([]);
    const [filtrado, setFiltrado] = useState<Usuario[]>([]);
    const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [edicion, setEdicion] = useState<Usuario | null>(null);
    const menuRef = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        const ejemplo = [
            {ID: 1, CODIGO: "RED", USERNAME: "ANA", PASSWORD: "123ASD", ACTIVO: 1, FECHA_CREACION: "12-12-12"},
            {ID: 2, CODIGO: "YELLOW", USERNAME: "BAP", PASSWORD: "123ASD", ACTIVO: 1, FECHA_CREACION: "12-12-12"},
            {ID: 3, CODIGO: "GREEN", USERNAME: "ROD", PASSWORD: "123ASD", ACTIVO: 1, FECHA_CREACION: "12-12-12"},  
        ];
        setUsuarios(ejemplo);
        setFiltrado(ejemplo);
    }, []);

    useEffect(() => {
        const lista = usuarios.filter(value => value.USERNAME.toLocaleLowerCase().includes(busqueda.toLowerCase()));
        setFiltrado(lista);
    }, [busqueda, usuarios]);

    useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
        if (menuRef.current && !menuRef.current.contains(event.target as Node)) {setMenuActivoId(null);}};
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const eliminarUsuario = (ID: number) => {
        const registros = usuarios.filter(valor => valor.ID !== ID);
        setUsuarios(registros);
        setFiltrado(registros);
        setMenuActivoId(null);
    }

    const editarUsuario = (ID: number) => {
        const usuarioEditado = usuarios.find(usuario => usuario.ID === ID);
        if (usuarioEditado) {
            setEdicion(usuarioEditado);
            setMostrarModal(true);
        }
    }

    const guardarUsuario = () => {
        if (edicion) {
            const registros = usuarios.map(usuario => usuario.ID === edicion.ID ? edicion : usuario);
            setUsuarios(registros);
            setFiltrado(registros);
            setEdicion(null);
        } else {
            const nuevo: Usuario = {
                ID: usuarios.length + 1,
                CODIGO: "CODIGO0",
                USERNAME: "USERNAME",
                PASSWORD: "PASSWORD",
                ACTIVO: 1,
                FECHA_CREACION: new Date().toISOString().split("T")[0],
                FECHA_BAJA: ""
            };
            setUsuarios([...usuarios, nuevo]);
            setFiltrado([...usuarios, nuevo]);
        }
    }

    return (
        <div id="cuerpo-main">
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? "minimize" : ""}>
                <section id="listar-registros">
                    <div className="encabezado"><h2>Lista de usuarios</h2></div>
                    <div className="goated">
                        <div className="barra-buscador"><input type="text" placeholder="Ingrese el nombre del usuario que desea buscar 🔍" value={busqueda} onChange={(e) => setBusqueda(e.target.value)}/></div>
                        <Link className="boton-goated ir-a-goated animacion-goated" to="/administracion/administracion/gestionar_colaboradores">Regresar</Link>
                        <button className="boton-goated anadir-a-goated animacion-goated" onClick={() => { setMostrarModal(true); setEdicion(null); }}>Registrar usuario</button>
                    </div>

                    <div className="listar-registros">
                        {filtrado.map((registro) => (
                            <div className="mostrar-registros" key={registro.ID}>
                                <span className="texto-de-registro">{registro.CODIGO}</span>
                                <span className="texto-de-registro">{registro.USERNAME}</span>
                                <span className="texto-de-registro">{registro.PASSWORD}</span>
                                <span className="texto-de-registro">{{1: "Inactivo", 2: "Activo", 3: "Suspendido"}[registro.ACTIVO] || "Desconocido"}</span>                                
                                <span className="texto-de-registro">{registro.FECHA_CREACION}</span>
                                <span className="texto-de-registro">📅{registro?.FECHA_BAJA}</span>
                                <div className="listar-opciones-contenedor">
                                    <div className="listar-registro-opciones" onClick={() => setMenuActivoId(registro.ID)}><i className="fa-solid fa-ellipsis-vertical"/></div>
                                    {menuActivoId === registro.ID && (
                                        <div ref={menuRef} className="menu-opciones">
                                            <button onClick={() => editarUsuario(registro.ID)}>✏️ Editar</button>
                                            <button onClick={() => eliminarUsuario(registro.ID)}>🗑️ Eliminar</button>
                                        </div>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                </section>
            </main>

            {mostrarModal && (
                <div className="ventana-overlay">
                    <div className="contenido-ventana">
                        <h3>{edicion ? "Editar usuario" : "Registrar usuario"}</h3>
                        <input type="text" placeholder="Ingrese el nuevo nombre de usuario" value={edicion?.USERNAME || ""} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, USERNAME: nuevoValor.target.value } : null)}/>
                        <input type="text" placeholder="Ingrese una contraseña" value={edicion?.PASSWORD || ""} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, PASSWORD: nuevoValor.target.value } : null)}/>
                        <input type="date" value={edicion?.FECHA_CREACION || ""} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, FECHA_CREACION: nuevoValor.target.value } : null)}/>
                        <input type="date" value={edicion?.FECHA_BAJA || ""} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, FECHA_BAJA: nuevoValor.target.value } : null)}/>
                        <select value={edicion?.ACTIVO || ""} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, ACTIVO: Number(nuevoValor.target.value) } : null)}>
                            <option value="">-- Selecciona estado --</option>
                            <option value={1}>Inactivo</option>
                            <option value={2}>Activo</option>
                            <option value={3}>Suspendido</option>
                        </select>
                        <div className="acciones-de-registro">
                            <button onClick={guardarUsuario}>Guardar</button>
                            <button onClick={() => { setMostrarModal(false); setEdicion(null); }}>Cancelar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>        
    )
}

/* Se requiere solucionar la adicion de nuevos registros */
export default gestionarUsuarios