import { useEffect, useRef, useState } from 'react'
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa'
import "./turnosYhorarios.css"

interface Colaborador {
    ID: number,
    NOMBRE: string;
    HORARIO: Horario[];
}

interface Horario {
    DIA: string,
    TIPO_DIA: number,
    HORA_INICIO: string,
    HORA_FIN: string
}

function turnosYhorarios() {
    const [minimizado, setMinimizado] = useState(false);
    const [busqueda, setBusqueda] = useState("");
    const [colaboradores, setColaboradores] = useState<Colaborador[]>([]);
    const [filtrado, setFiltrado] = useState<Colaborador[]>([]);
    const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [edicion, setEdicion] = useState<Colaborador | null>(null);
    const menuRef = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
    const ejemplo: Colaborador[] = [
        {
            ID: 1,
            NOMBRE: "Juan Pérez",
            HORARIO: [
            { DIA: "Lunes", TIPO_DIA: 1, HORA_INICIO: "08:00", HORA_FIN: "17:00" },
            { DIA: "Martes", TIPO_DIA: 1, HORA_INICIO: "08:00", HORA_FIN: "17:00" },
            { DIA: "Miércoles", TIPO_DIA: 1, HORA_INICIO: "-", HORA_FIN: "-" },
            { DIA: "Jueves", TIPO_DIA: 1, HORA_INICIO: "08:00", HORA_FIN: "17:00" },
            { DIA: "Viernes", TIPO_DIA: 1, HORA_INICIO: "08:00", HORA_FIN: "16:00" },
            { DIA: "Sábado", TIPO_DIA: 1, HORA_INICIO: "08:00", HORA_FIN: "16:00" },
            ],
        },
        {
            ID: 2,
            NOMBRE: "Ana Torres",
            HORARIO: [
            { DIA: "Lunes", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Martes", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Miércoles", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Jueves", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Viernes", TIPO_DIA: 1, HORA_INICIO: "-", HORA_FIN: "-" },
            { DIA: "Sábado", TIPO_DIA: 1, HORA_INICIO: "08:00", HORA_FIN: "16:00" },
            ],
        },
        {
            ID: 3,
            NOMBRE: "Ana Torres",
            HORARIO: [
            { DIA: "Lunes", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Martes", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Miércoles", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Jueves", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Viernes", TIPO_DIA: 1, HORA_INICIO: "-", HORA_FIN: "-" },
            { DIA: "Sábado", TIPO_DIA: 1, HORA_INICIO: "08:00", HORA_FIN: "16:00" },
            ],
        },
        {
            ID: 4,
            NOMBRE: "Ana Torres",
            HORARIO: [
            { DIA: "Lunes", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Martes", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Miércoles", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Jueves", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Viernes", TIPO_DIA: 1, HORA_INICIO: "-", HORA_FIN: "-" },
            { DIA: "Sábado", TIPO_DIA: 1, HORA_INICIO: "08:00", HORA_FIN: "16:00" },
            ],
        },
        {
            ID: 5,
            NOMBRE: "Ana Torres",
            HORARIO: [
            { DIA: "Lunes", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Martes", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Miércoles", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Jueves", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Viernes", TIPO_DIA: 1, HORA_INICIO: "-", HORA_FIN: "-" },
            { DIA: "Sábado", TIPO_DIA: 1, HORA_INICIO: "08:00", HORA_FIN: "16:00" },
            ],
        },
        {
            ID: 6,
            NOMBRE: "Ana Torres",
            HORARIO: [
            { DIA: "Lunes", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Martes", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Miércoles", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Jueves", TIPO_DIA: 1, HORA_INICIO: "09:00", HORA_FIN: "18:00" },
            { DIA: "Viernes", TIPO_DIA: 1, HORA_INICIO: "-", HORA_FIN: "-" },
            { DIA: "Sábado", TIPO_DIA: 1, HORA_INICIO: "08:00", HORA_FIN: "16:00" },
            ],
        }
        ];

        setColaboradores(ejemplo);
    }, []);

    useEffect(() => {
        const lista = colaboradores.filter(value => value.NOMBRE.toLocaleLowerCase().includes(busqueda.toLowerCase()));
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

    const editarHorario = (ID: number) => {
        const colaboradorEditado = colaboradores.find(colaborador => colaborador.ID === ID);
        if (colaboradorEditado) {
            setEdicion(colaboradorEditado);
            setMostrarModal(true);
        }
    };

    const eliminarHorario = (ID: number) => {
        const registros = colaboradores.filter(valor => valor.ID !== ID)
        setColaboradores(registros);
        setFiltrado(registros);
        setMenuActivoId(null);
    }

    const registrarHorario = () => {
        const nuevo: Colaborador = {
            ID: colaboradores.length + 1,
            NOMBRE: "",
            HORARIO: [
            { DIA: "Lunes", TIPO_DIA: 1, HORA_INICIO: "", HORA_FIN: "" },
            { DIA: "Martes", TIPO_DIA: 1, HORA_INICIO: "", HORA_FIN: "" },
            { DIA: "Miércoles", TIPO_DIA: 1, HORA_INICIO: "", HORA_FIN: "" },
            { DIA: "Jueves", TIPO_DIA: 1, HORA_INICIO: "", HORA_FIN: "" },
            { DIA: "Viernes", TIPO_DIA: 1, HORA_INICIO: "", HORA_FIN: "" },
            { DIA: "Sábado", TIPO_DIA: 1, HORA_INICIO: "", HORA_FIN: "" },
            ],
        }
        setEdicion(nuevo);
        setMostrarModal(true);
    }

    const guardarHorario = () => {
        if (!edicion) return;
        const incompletos = edicion.HORARIO.some(h => h.TIPO_DIA !== 0 && h.TIPO_DIA !== 1);
        if (incompletos) {
            alert("Debes seleccionar si cada día es laboral o no laboral");
            return;
        }

        const horasIncompletas = edicion.HORARIO.some(h => h.TIPO_DIA === 1 && (!h.HORA_INICIO || !h.HORA_FIN));
            if (horasIncompletas) {
                alert("Debes completar hora de inicio y fin en los días laborales");
                return;
        }
        
        if (!edicion.NOMBRE.trim()) {alert("Ingresar el nombre del colaborador es obligatorio"); return;}

        const existe = colaboradores.some(colaborador => colaborador.ID === edicion.ID);
        if (existe) {
            const registros = colaboradores.map(colaborador => colaborador.ID === edicion.ID ? edicion : colaborador);
            setColaboradores(registros);
            setFiltrado(registros);
        } else {
            const nuevo: Colaborador = {...edicion, ID: colaboradores.length + 1}
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
                    <div className="encabezado"><h2>Horarios de colaboradores</h2></div>
                    <div className="goated">
                        <div className="barra-buscador"><input type="text" placeholder="Ingrese el nombre del colaborador del que quiera encontrar su horario 🔍" value={busqueda} onChange={(e) => setBusqueda(e.target.value)}/></div>
                        <button className="boton-goated anadir-a-goated animacion-goated" onClick={registrarHorario}>Asignar horario</button>
                    </div>
                    <div className="listar-registros">
                        <div className="registros">
                            {filtrado.length > 0 ? (
                                filtrado.map((registro) => (
                                    <div key={registro.ID} className="tarjeta-horario">
                                    <h3 className="nombre-tarjeta">{registro.NOMBRE}</h3>
                                    <div className="info-tarjeta">
                                        {registro.HORARIO.map((h, i) => (
                                        <div key={i}>
                                            {h.TIPO_DIA === 1 ? (
                                            <p>
                                                {h.DIA}: {h.HORA_INICIO} - {h.HORA_FIN}
                                            </p>
                                            ) : (
                                            <p>{h.DIA}: No laboral</p>
                                            )}
                                        </div>
                                        ))}
                                    </div>
                                    <div>
                                        <div className="listar-opciones-contenedor">
                                        <div
                                            className="listar-registro-opciones"
                                            onClick={() => setMenuActivoId(registro.ID)}
                                        >
                                            <i className="fa-solid fa-ellipsis-vertical" />
                                        </div>
                                        {menuActivoId === registro.ID && (
                                            <div ref={menuRef} className="menu-opciones">
                                            <button onClick={() => editarHorario(registro.ID)}>✏️ Editar</button>
                                            <button onClick={() => eliminarHorario(registro.ID)}>🗑️ Eliminar</button>
                                            </div>
                                        )}
                                        </div>
                                    </div>
                                    </div>
                                ))
                                ) : (
                                <p className="mensaje-vacio">❌ No se encontraron colaboradores</p>
                                )}
                            </div>
                        </div>
                </section>
            </main>

            {mostrarModal && edicion && (
                <div className="ventana-overlay">
                    <div className="contenido-ventana">
                        <h3>Información del horario</h3>
                        <label>Nombre del colaborador:</label>
                        <input type="text" value={edicion.NOMBRE} onChange={(e) => setEdicion({ ...edicion, NOMBRE: e.target.value })} placeholder="Ingrese el nombre"/>
                        {edicion.HORARIO.map((h, index) => (
                        <div key={index} className="bloque-dia">
                            <h4>{h.DIA}</h4>
                            <label>Tipo de día:</label>
                            <select value={h.TIPO_DIA} onChange={(e) => {
                                    const nuevoHorario = [...edicion.HORARIO];
                                    nuevoHorario[index] = {
                                        ...h,
                                        TIPO_DIA: parseInt(e.target.value)
                                    };
                                    setEdicion({ ...edicion, HORARIO: nuevoHorario });
                                }}>
                                <option value={1}>Feriado</option>
                                <option value={2}>Laboral</option>
                                <option value={3}>Puente</option>
                                <option value={4}>No laborable</option>
                            </select>
                            <br></br>
                            <label>Hora inicio:</label>
                            <input type="time" value={h.HORA_INICIO} onChange={(e) => {
                                    const nuevoHorario = [...edicion.HORARIO];
                                    nuevoHorario[index] = {
                                        ...h,
                                        HORA_INICIO: e.target.value
                                    };
                                    setEdicion({ ...edicion, HORARIO: nuevoHorario });
                                }}/>

                            <label>Hora fin:</label>
                            <input type="time" value={h.HORA_FIN} onChange={(e) => {
                                    const nuevoHorario = [...edicion.HORARIO];
                                    nuevoHorario[index] = {
                                        ...h,
                                        HORA_FIN: e.target.value
                                    };
                                    setEdicion({ ...edicion, HORARIO: nuevoHorario });
                                }}/>
                        </div>
                        ))}
                        <div className="acciones-de-registro">
                            <button onClick={guardarHorario}>Guardar</button>
                            <button onClick={() => { setMostrarModal(false); setEdicion(null); }}>Cancelar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default turnosYhorarios