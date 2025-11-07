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

    // Filtrar por búsqueda
    useEffect(() => {
        const lista = servicios.filter(value => value.NOMBRE.toLocaleLowerCase().includes(busqueda.toLowerCase()));
        setFiltrado(lista);
    }, [busqueda, servicios]);

    // Listar
    useEffect(() => {listarServicios();}, []);
    const listarServicios = async () => {
        try {
            const ejemplo: Servicios[] = [
            { ID: 1, CODIGO: "COD0001", NOMBRE: "Primer servicio", DESCRIPCION: "Este es un ejemplo", ACTIVO: 1 },
            { ID: 2, CODIGO: "COD0002", NOMBRE: "Segundo servicio", DESCRIPCION: "Servicio inactivo", ACTIVO: 0 },
            { ID: 3, CODIGO: "COD0003", NOMBRE: "Tercer servicio", DESCRIPCION: "Activo también", ACTIVO: 1 },
        ];

        // Filtramos solo los activos (por ejemplo ACTIVO === 1)
        const activos = ejemplo.filter((servicio) => servicio.ACTIVO === 1);
            setServicios(activos);
            setFiltrado(activos);
        } catch (error) {console.error("Error al obtener los servicios", error);}
    }

    // Formulario nuevo y vacío
    const abrirFormularioNuevo = () => {
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

    // Formulario para editar
    const abrirFormularioEditar = (servicio: Servicios) => {
        const servicioEditado: Servicios = {
            ID: servicio.ID,
            CODIGO: servicio.CODIGO,
            NOMBRE: servicio.NOMBRE,
            DESCRIPCION: servicio.DESCRIPCION,
            ACTIVO: servicio.ACTIVO
        };
        setEdicion(servicioEditado);
        setMostrarModal(true);
    }

    // Guardar
    const guardarServicio = async () => {
        if (!edicion) return;
        if (!edicion.NOMBRE.trim()) {alert("El nombre de usuario es obligatorio"); return;}
        if (!edicion.ACTIVO) {alert("Debes seleccionar un estado"); return;}

        try {
            if (edicion.ID && edicion.ID > 0) {
                const response = servicios.map((servicio) => servicio.ID === edicion.ID ? { ...servicio, ...edicion } : servicio);
                setServicios(response);
                setFiltrado(response);
            } else {
                const response: Servicios = {
                    ...edicion,
                    ID: servicios.length > 0 ? Math.max(...servicios.map((s) => s.ID)) + 1 : 1,
                };
                const nuevos = [...servicios, response];
                setServicios(nuevos);
                setFiltrado(nuevos);
            }
            listarServicios();
            setEdicion(null);
            setMostrarModal(false);
        } catch (error) {
            console.log("Datos enviados al backend:", edicion);
            console.error("Error al registrar/actualizar: ", error);
            alert(error);
        }        

        setEdicion(null);
        setMostrarModal(false);
    }

    // Eliminar
    const eliminarServicio = (ID: number) => {
        const registros = servicios.filter(valor => valor.ID !== ID);
        setServicios(registros);
        setFiltrado(registros);
        setMenuActivoId(null);
    }

    return (
        <div id="cuerpo-main">
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? "minimize" : ""}>
                <section id="listar-registros">
                    <div className="encabezado"><h2>Lista de servicios (ESTILOS SIN API)</h2></div>
                    <div className="goated">
                        <div className="barra-buscador"><input type="text" placeholder="Ingrese el nombre del servicio que desea buscar 🔍" value={busqueda} onChange={(e) => setBusqueda(e.target.value)}/></div>
                        <button className="boton-goated anadir-a-goated animacion-goated" onClick={abrirFormularioNuevo}>Registrar servicio</button>
                    </div>
                    <table className="GM-table">
                        <thead className="GM-thead">
                            <tr className='GM-tr'>
                                <th className="GM-th" style={{width:"110px"}}>Código</th>
                                <th className="GM-th">Servicio</th>
                                <th className="GM-th">Descripción</th>
                                <th className="GM-th" style={{width:"100px"}}>Estado</th>
                                <th className="GM-th" style={{width:"150px"}}>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filtrado.map((registro) => (
                                <tr key={registro.ID}>
                                    <td className="GM-td">{registro.CODIGO}</td>
                                    <td className="GM-td">{registro.NOMBRE}</td>
                                    <td className="GM-td">{registro.DESCRIPCION}</td>
                                    <td className="GM-td">{{1: "Inactivo", 2: "Activo", 3: "Suspendido"}[registro.ACTIVO] || "Desconocido"}</td>                                
                                    <td className="GM-td" style={{display:"flex", justifyContent:"center"}}>
                                        <button className="boton-verde" onClick={() => abrirFormularioEditar(registro)}>Editar</button>
                                        <button className="boton-rojo" onClick={() => eliminarServicio(registro.ID)}>Eliminar</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </section>
            </main>

            {mostrarModal && edicion && (
                <div className="ventana-overlay">
                    <div className="contenido-ventana">
                        <h3>Información de servicio</h3>
                        <select value={edicion?.ACTIVO || ""} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, ACTIVO: Number(nuevoValor.target.value) } : null)}>
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

export default gestionarServicios;