import { useEffect, useRef, useState } from 'react'
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa'
import IST from '../../../components/proteccion/IST'
import Swal from 'sweetalert2';

interface ServicioResponse {
    id: number,
    nombre: string,
    descripcion?: string,
}

interface ServicioRequest {
    id?: number,
    nombre: string,
    descripcion?: string
}

const InventarioMovimientos: React.FC = () => {
    const [minimizado, setMinimizado] = useState(false);
    const [servicios, setServicios] = useState<ServicioResponse[]>([]);
    const [filtrado, setFiltrado] = useState<ServicioResponse[]>([]);
    const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [edicion, setEdicion] = useState<ServicioRequest | null>(null);
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

    // Listar
    useEffect(() => {listarServicios();}, []);
    const listarServicios = async () => {
        try {
            const respuesta = await IST.get(`/servicios`);
            const lista = Array.isArray(respuesta.data)
            ? respuesta.data
            : respuesta.data.data

            setServicios(lista);
            setFiltrado(lista);
        } catch (error) {
            Swal.fire({
                title: "Error desconocido",
                text: "Por favor refresque la página",
                icon: "error"
            });
        }
    }

    // Formulario nuevo y vacío
    const abrirFormularioNuevo = () => {
        const nuevo: ServicioRequest = {
            nombre: "",
            descripcion: ""
        }
        setEdicion(nuevo);
        setMostrarModal(true);
    }

    // Formulario para editar
    const abrirFormularioEditar = (servicio: ServicioResponse) => {
        const servicioEditado: ServicioRequest = {
            id: servicio.id,
            nombre: servicio.nombre,
            descripcion: servicio.descripcion
        };
        setEdicion(servicioEditado);
        setMostrarModal(true);
    }

    // Guardar
    const guardarServicio = async () => {
        if (!edicion) return;
        if (!edicion.nombre.trim()) {
            Swal.fire({
                title: "Alerta",
                text: "Debe ingresar el nombre del servicio",
                icon: "warning"
            });
            return;
        }
        if (!edicion.descripcion?.trim()) {
            Swal.fire({
                title: "Alerta",
                text: "Debe ingresar la descripción del servicio",
                icon: "warning"
            });
            return;}

        try {
            if (edicion.id && edicion.id > 0) {await IST.put(`/servicios`, edicion);}
            else {await IST.post(`/servicios`, edicion)}
            listarServicios();
            setEdicion(null);
            setMostrarModal(false);
        } catch (error) {
            Swal.fire({
                title: "Error",
                text: "al registrar o actualizar",
                icon: "error"
            });
        }
    }

    // Eliminar
    const eliminarServicio = async (id: number) => {
        try {
            await IST.delete(`/servicios/${id}`)
            listarServicios();
            Swal.fire({
                title: "Éxito",
                text: "Operación exitosa",
                icon: "success"
            });
        } catch (error) {
            Swal.fire({
                title: "Error",
                text: "Hubo un error al eliminar",
                icon: "error"
            });
        }
    }

    return (
        <div id="cuerpo-main">
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? "minimize" : ""}>
                <section id="listar-registros">
                    <div className="encabezado"><h2>Lista de servicios</h2></div>
                    <div className="goated">
                        <button className="boton-goated anadir-a-goated animacion-goated" onClick={abrirFormularioNuevo}>Registrar servicio</button>
                    </div>
                    <table className="GM-table">
                        <thead className="GM-thead">
                            <tr className='GM-tr'>
                                <th className="GM-th">Servicio</th>
                                <th className="GM-th">Descripción</th>
                                <th className="GM-th" style={{width:"150px"}}>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filtrado.map((registro) => (
                                <tr key={registro.id}>
                                    <td className="GM-td">{registro.nombre}</td>
                                    <td className="GM-td">{registro.descripcion}</td>
                                    <td className="GM-td" style={{display:"flex", justifyContent:"center"}}>
                                        <button className="boton-verde" onClick={() => abrirFormularioEditar(registro)}>Editar</button>
                                        <button className="boton-rojo" onClick={() => eliminarServicio(registro.id)}>Eliminar</button>
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
                        <input type="text" placeholder="Servicio" value={edicion.nombre} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, nombre: nuevoValor.target.value } : null)}/>
                        <input type="text" placeholder="Descripción" value={edicion.descripcion} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, descripcion: nuevoValor.target.value } : null)}/>
                        <div className="acciones-de-registro-GM">
                            <button onClick={guardarServicio}>Guardar</button>
                            <button onClick={() => { setMostrarModal(false); setEdicion(null); }}>Cancelar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>        
    )
}

export default InventarioMovimientos;