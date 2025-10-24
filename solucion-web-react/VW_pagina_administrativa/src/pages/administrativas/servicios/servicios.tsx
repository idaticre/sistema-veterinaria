import { useEffect, useRef, useState } from 'react'
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa'
import "./styles.css"

/* 

GM: La API de servicios todavía no ha sido programada, una vez esté lista proceder con:
    1. realizar los imports necesarios de interface de interfaces.tsx (eliminar el hardcodeado) y axios
    2. Reemplazar CRUD con la API

*/
    
interface Servicios {
    ID: number,
    CODIGO: string,
    NOMBRE: string,
    DESCRIPCION?: string,
    ACTIVO: number
}

function gestionarServicios() {
    const [minimizado, setMinimizado] = useState(false);
    const [busqueda, setBusqueda] = useState("");
    const [servicios, setServicios] = useState<Servicios[]>([]);
    const [filtrado, setFiltrado] = useState<Servicios[]>([]);
    const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [edicion, setEdicion] = useState<Servicios | null>(null);
    const menuRef = useRef<HTMLDivElement | null>(null);

    // Registros
    useEffect(() => {
        const ejemplo = [
            {ID: 2, CODIGO: "CODIGO1", NOMBRE: "USUARIO1", DESCRIPCION: "CONTRASEÑA1", ACTIVO: 2},
        ];
        setServicios(ejemplo);
        setFiltrado(ejemplo);
    }, []);

    // Filtro en barra de búsqueda
    useEffect(() => {
        const lista = servicios.filter(value => value.NOMBRE.toLocaleLowerCase().includes(busqueda.toLowerCase()));
        setFiltrado(lista);
    }, [busqueda, servicios]);

    const eliminarServicio = (ID: number) => {
        const registros = servicios.filter(valor => valor.ID !== ID);
        setServicios(registros);
        setFiltrado(registros);
        setMenuActivoId(null);
    }
    
    // Cerrar modal
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
        if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
            setMenuActivoId(null);
        }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const registrarServicio = () => {
        const nuevo: Servicios = {
            ID: servicios.length + 1, // ALERTA "ERROR DE DUPLICADOS", NO USAR ESTO CUANDO CONECTEMOS LA BASE DE DATOS, DEJA QUE EL BACKEND EN return ASIGNE EL ID (para pruebas locales está bien)
            CODIGO: "",
            NOMBRE: "",
            DESCRIPCION: "",
            ACTIVO: 2,
        }
        setEdicion(nuevo);
        setMostrarModal(true);
    }

    const editarServicio = (ID: number) => {
        const usuarioEditado = servicios.find(servicios => servicios.ID === ID);
        if (usuarioEditado) {
            setEdicion(usuarioEditado);
            setMostrarModal(true);
        }
    }

    const guardarServicio = () => {
        if (!edicion) return;
        if (!edicion.NOMBRE.trim()) {alert("El nombre de usuario es obligatorio"); return;}
        if (!edicion.ACTIVO) {alert("Debes seleccionar un estado (Activo/Inactivo/Suspendido)"); return;}

        {/* Todo lo demás */}
        const existe = servicios.some(servicio => servicio.ID === edicion.ID);
        if (existe) {
            const registros = servicios.map(servicio => servicio.ID === edicion.ID ? edicion : servicio);
            setServicios(registros);
            setFiltrado(registros);
        } else {
            const nuevo: Servicios = {
                ...edicion,
                ID: servicios.length + 1, // ALERTA "ERROR DE DUPLICADOS", NO USAR ESTO CUANDO CONECTEMOS LA BASE DE DATOS, DEJA QUE EL BACKEND EN return ASIGNE EL ID (para pruebas locales está bien)
                CODIGO: edicion.CODIGO || "CODIGO0", // CODIGO está siendo generado manualmente, editar cuando se conecte la base de datos para cumplir con la COMPOUND KEY
            };
            const registros = [...servicios, nuevo];
            setServicios(registros);
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
                    <div className="encabezado"><h2>Lista de servicios</h2></div>
                    <div className="goated">
                        <div className="barra-buscador"><input type="text" placeholder="Ingrese el nombre del servicio que desea buscar 🔍" value={busqueda} onChange={(e) => setBusqueda(e.target.value)}/></div>
                        <button className="boton-goated anadir-a-goated animacion-goated" onClick={registrarServicio}>Registrar servicio</button>
                    </div>

                    <div className="listar-registros">
                        {filtrado.map((registro) => (
                            <div className="mostrar-registros" key={registro.ID}>
                                <span className="texto-de-registro">{registro.CODIGO}</span>
                                <span className="texto-de-registro">{registro.NOMBRE}</span>
                                <span className="texto-de-registro">{registro.DESCRIPCION}</span>
                                <span className="texto-de-registro">{{1: "Inactivo", 2: "Activo", 3: "Suspendido"}[registro.ACTIVO] || "Desconocido"}</span>                                
                                <div className="listar-opciones-contenedor">
                                    <div className="listar-registro-opciones" onClick={() => setMenuActivoId(registro.ID)}><i className="fa-solid fa-ellipsis-vertical"/></div>
                                    {menuActivoId === registro.ID && (
                                        <div ref={menuRef} className="menu-opciones">
                                            <button onClick={() => editarServicio(registro.ID)}>✏️ Editar</button>
                                            <button onClick={() => eliminarServicio(registro.ID)}>🗑️ Eliminar</button>
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
                        <h3>Información de servicio</h3>
                        <select value={edicion?.ACTIVO || ""} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, ACTIVO: Number(nuevoValor.target.value) } : null)}>
                            <option value="">-- Selecciona estado --</option>
                            <option value="1">Inactivo</option>
                            <option value="2">Activo</option>
                            <option value="3">Suspendido</option>
                        </select>
                        <input type="text" placeholder="Servicio" value={edicion.NOMBRE} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, NOMBRE: nuevoValor.target.value } : null)}/>
                        <input type="text" placeholder="Descripción" value={edicion.DESCRIPCION} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, DESCRIPCION: nuevoValor.target.value } : null)}/>
                        <div className="acciones-de-registro">
                            <button onClick={guardarServicio}>Guardar</button>
                            <button onClick={() => { setMostrarModal(false); setEdicion(null); }}>Cancelar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>        
    )
}

export default gestionarServicios