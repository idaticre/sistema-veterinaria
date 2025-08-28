import { useEffect, useRef, useState } from 'react'
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa'
import "./turnosYhorarios.css"
import { Link } from "react-router-dom";

interface Horario {
    DIA: string,
    TIPO_DIA: number,
    HORA_INICIO: string,
    HORA_FIN: string
}

interface Colaborador {
    ID: number;
    NOMBRE: string;
    HORARIO: Horario[];
}

function turnosYhorarios() {
    const [minimizado, setMinimizado] = useState(false);
    const [busqueda, setBusqueda] = useState("");
    const [horarios, setHorario] = useState<Horario[]>([]);
    const [colaboradores, setColaborador] = useState<Colaborador[]>();
    const [filtrado, setFiltrado] = useState<Horario[]>([]);
    const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [edicion, setEdicion] = useState<Horario | null>(null);
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
            ],
        },
        ];

        setColaborador(ejemplo);
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

    const editarColaborador = (id: number) => {console.log("Editar", id);};
    const eliminarColaborador = (id: number) => {console.log("Eliminar", id);};

    return (
        <div id="cuerpo-main">
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? "minimize" : ""}>
                <section id="listar-registros">
                    <div className="encabezado"><h2>Horarios de colaboradores</h2></div>
                    <div className="goated">
                        <div className="barra-buscador"><input type="text" placeholder="Ingrese el nombre del colaborador del que quiera encontrar su horario 🔍" value={busqueda} onChange={(e) => setBusqueda(e.target.value)}/></div>
                        <button className="boton-goated anadir-a-goated animacion-goated" onClick={() => { setMostrarModal(true); setEdicion(null); }}>Asignar horario</button>
                    </div>

                    <div className="listar-registros">
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 p-6">
                        {colaboradores.map((registro) => (
                            <div key={registro.ID} className="border rounded-xl shadow-md p-4 bg-white relative">
                            <h3 className="text-xl font-bold mb-4">{registro.nombre}</h3>

                            <div className="space-y-2">
                                {registro.horario.map((h, i) => (
                                <div key={i} className="flex gap-2 text-sm">
                                    <p className="font-semibold">{h.dia}</p>
                                    <p className="text-gray-500">({h.tipo_dia})</p>
                                    <p className="ml-1">{h.inicio} - {h.fin}</p>
                                </div>
                                ))}
                            </div>

                            <div className="flex justify-between items-center mt-4">
                                <span className="font-semibold">horario</span>

                                <div className="relative">
                                <div
                                    className="cursor-pointer p-2 hover:bg-gray-100 rounded"
                                    onClick={() => setMenuActivoId(registro.ID)}
                                >
                                    <i className="fa-solid fa-ellipsis-vertical"></i>
                                </div>

                                {menuActivoId === registro.ID && (
                                    <div
                                    ref={menuRef}
                                    className="absolute right-0 mt-2 w-32 bg-white border rounded-lg shadow-lg z-10"
                                    >
                                    <button
                                        onClick={() => editarColaborador(registro.ID)}
                                        className="block w-full text-left px-3 py-2 hover:bg-gray-100"
                                    >
                                        ✏️ Editar
                                    </button>
                                    <button
                                        onClick={() => eliminarColaborador(registro.ID)}
                                        className="block w-full text-left px-3 py-2 hover:bg-gray-100"
                                    >
                                        🗑️ Eliminar
                                    </button>
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
        </div>
    );
};

export default turnosYhorarios