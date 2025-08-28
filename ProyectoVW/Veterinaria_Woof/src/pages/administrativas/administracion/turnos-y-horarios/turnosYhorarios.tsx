import { useEffect, useRef, useState } from 'react'
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa'
import "./turnosYhorarios.css"
import { Link } from "react-router-dom";
import { col } from 'framer-motion/client';

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
    const [horarios, setHorario] = useState<Horario[]>([]);
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
            { DIA: "Domingo", TIPO_DIA: 1, HORA_INICIO: "08:00", HORA_FIN: "16:00" },
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
            { DIA: "Domingo", TIPO_DIA: 1, HORA_INICIO: "08:00", HORA_FIN: "16:00" },
            ],
        },
        ];

        setColaboradores(ejemplo);
    }, []);

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
            ],
        }
        setEdicion(nuevo);
        setMostrarModal(true);
    }

    const guardarHorario = () => {
        if (!edicion) return;
        if (!edicion.HORARIO) {alert("Es necesario completar todas las casillas"); return;}
    
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
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 p-6">
                            {colaboradores.map((registro) => (
                                <div key={registro.ID} className="border rounded-xl shadow-md p-4 bg-white relative">
                                    <h3 className="text-xl font-bold mb-4">{registro.NOMBRE}</h3>
                                    <div className="space-y-2">
                                        {registro.HORARIO.map((h, i) => (
                                        <div key={i} className="flex gap-2 text-sm">
                                            <p className="font-semibold">{h.DIA}</p>
                                            <p className="text-gray-500">({h.TIPO_DIA})</p>
                                            <p className="ml-1">{h.HORA_INICIO} - {h.HORA_FIN}</p>
                                        </div>
                                        ))}
                                    </div>
                                    <div className="flex justify-between items-center mt-4">
                                        <span className="font-semibold">horario</span>
                                        <div className="listar-opciones-contenedor">
                                            <div className="listar-registro-opciones" onClick={() => setMenuActivoId(registro.ID)}><i className="fa-solid fa-ellipsis-vertical"/></div>
                                            {menuActivoId === registro.ID && (
                                                <div ref={menuRef} className="menu-opciones">
                                                    <button onClick={() => editarHorario(registro.ID)}>✏️ Editar</button>
                                                    <button onClick={() => eliminarHorario(registro.ID)}>🗑️ Eliminar</button>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </section>
            </main>

            {mostrarModal && edicion && (
                <div className="ventana-overlay">
                    <div className="contenido-ventana">
                        <h3>Información del horario</h3>
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