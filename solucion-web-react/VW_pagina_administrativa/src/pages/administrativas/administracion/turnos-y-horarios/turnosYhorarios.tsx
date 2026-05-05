/*
    Este .tsx ha sido archivado porque se debe editar la funcionalidad de horarios, incluyendo:
    - sus tablas
    - sus apis

    La funcionalidad ha sido realizada de forma innecesariamente compleja, lo que evita que se pueda usar de forma eficiente. En la tabla, esta debería tener únicamente 6 campos:
    - id 
    - trabajador_id
    - dia_id
    - trabaja (boolean)
    - hora_inicio (timestamp)
    - hora_fin (timestamp) 

    "Horarios base", "Horarios base por roles", sus ramificaciones y sus tablas que puedan tener (que son conjuntos de horarios inmutables al parecer) no se seguirán usando por su nula modificabilidad, en cambio, se sugiere usar los siguientes endpoints usando 2 únicas tablas: HORARIOS y DIAS (antiguamente dias_semana)
    - Endpoint /horarios: muestra todos los horarios de la tabla HORARIOS. Todos los días de todos los colaboradores en el display principal de la página agrupados en tarjetas por colaborador

    - Parámetro "Trabajan en cierto día": es una extensión de /horarios que toma un parámetro ?dia={dia_id}. Muestra las personas que trabajan en un día específico. Necesita el ID del día.
    - Parámetro "Trabajan hoy": lo mismo que lo de arriba pero el frontend envía automáticamente el id del día en el que se está. ?dia={dia_id}
    - Funcionalidad buscar horario asignado por nombre: esto se maneja en el front-end


    POST: /asignar-horario permite añadir los registros en la tabla HORARIOS
    PUT: /asignar-horario permite editar los registros
    DELETE: /eliminar-horario elimina todos los registros de HORARIOS que contengan el ID de un colaborador, si se quiere indicar que un colaborador no trabaja un día específico, cambia el boolean del campo trabaja
*/

import { useEffect, useRef, useState } from 'react'
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa'
// import type { ColaboradorRequest } from '../../../../components/interfaces/interfaces';
import "./turnosYhorarios.css"
import IST from "../../../../components/proteccion/IST";

function turnosYhorarios() {
    /*
    const [minimizado, setMinimizado] = useState(false);
    const [busqueda, setBusqueda] = useState("");
    const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [edicion, setEdicion] = useState<ColaboradorRequest | null>(null);
    const [filtrado, setFiltrado] = useState<ColaboradorRequest[]>([]);
    const menuRef = useRef<HTMLDivElement | null>(null);

    const [colaboradores, setColaboradores] = useState<ColaboradorRequest[]>([]);
    const [horariosBase, setHorariosBase] = useState<HorarioBase[]>([]);
    const [horariosBasePorRoles, setHorariosBasePorRoles] = useState<HorariosBasePorRoles[]>([]);
    const [trabajanHoy, setTrabajanHoy] = useState<HorariosBasePorRoles[]>([]);
    
    
    // Obtener colaboradores
    useEffect(() => {obtenerColaboradores();}, []);
    const obtenerColaboradores = async () => {  
        try {
            const response = await IST.get(`/colaboradores`);
            setColaboradores(response.data);
        } catch (error) {
            console.error("Error al obtener tipos de documento:", error);
        }
    }

    // Obtener horarios base
    useEffect(() => {obtenerHorariosBase();}, []);
    const obtenerHorariosBase = async () => {
        try {
            const response = await IST.get(`/horarios-base`);
            setHorariosBase(response.data);
        } catch (error) {
            console.error("Error al obtener los horarios base:", error);
        }
    }
    
    // Obtener horarios base por roles
    useEffect(() => {obtenerHorariosBasePorRoles();}, []);
    const obtenerHorariosBasePorRoles = async () => {
        try {
            const response = await IST.get(`/horarios-base-roles`);
            setHorariosBasePorRoles(response.data);
        } catch (error) {
            console.error("Error al obtener los horarios base por roles:", error);
        }
    }

    // Obtener horario de quienes trabajan HOY

    // Consultar horarios de un colaborador específico

    useEffect(() => {
        const lista = colaboradores.filter(value => value.nombre.toLocaleLowerCase().includes(busqueda.toLowerCase()));
        setFiltrado(lista);
    }, [busqueda, colaboradores]);

    // Cerrar menú si hago click fuera
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
        if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
            setMenuActivoId(null);
        }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const registrarHorario = () => {
    }

    const guardarHorario = () => {
    }

    const editarHorario = () => {
    }

    const eliminarHorario = () => {
    }

    return (
        <div id="cuerpo-main">
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? "minimize" : ""}>
                <section id="listar-registros">
                    <div className="encabezado"><h2>Horarios de colaboradores</h2></div>
                    <div className="goated">
                        <div className="barra-buscador"><input type="text" placeholder="Ingrese el nombre del colaborador del que quiera encontrar su horario 🔍" value={busqueda} onChange={(e) => setBusqueda(e.target.value)}/></div>
                        <button className="boton-goated anadir-a-goated animacion-goated" onClick={registrarHorario}>Asignar horario</button>
                    </div>
                    <div className="center-this">
                        <button className='amarillo'>Horarios base</button>
                        <button className='amarillo'>Horarios asignados a roles</button>
                        <button className='amarillo'>Búsqueda avanzada</button>                    
                    </div>
                    <div className="listar-registros">
                        <div className="registros">
                        </div>
                    </div>
                </section>
            </main>

            {mostrarModal && edicion && (
                <div className="ventana-overlay">
                    <div className="contenido-ventana">
                        <h3>Información del horario</h3>
                        <label>Nombre del colaborador:</label>
                        
                        <div className="acciones-de-registro">
                            <button onClick={guardarHorario}>Guardar</button>
                            <button onClick={() => { setMostrarModal(false); setEdicion(null); }}>Cancelar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );

    */
};

export default turnosYhorarios