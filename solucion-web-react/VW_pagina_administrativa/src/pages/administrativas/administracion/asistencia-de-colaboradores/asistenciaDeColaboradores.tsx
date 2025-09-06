import { useState, useEffect, useRef } from "react";
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import "./asistencia.css";

interface Asistencia {
    id: number;
    colaborador: string;
    fecha: string;
    horaEntrada: string;
    horaSalida: string;
}

function AsistenciaColaboradores() {
    const [minimizado, setMinimizado] = useState(false);
    const [busqueda, setBusqueda] = useState("");
    const [asistencias, setAsistencias] = useState<Asistencia[]>([]);
    const [filtrados, setFiltrados] = useState<Asistencia[]>([]);
    const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [edicion, setEdicion] = useState<Asistencia | null>(null);
    const menuRef = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        const datos = [
            { id: 1, colaborador: "Carlos López", fecha: "2025-08-20", horaEntrada: "08:00", horaSalida: "17:00" },
            { id: 2, colaborador: "Ana Torres", fecha: "2025-08-20", horaEntrada: "09:00", horaSalida: "18:00" },
        ];
        setAsistencias(datos);
        setFiltrados(datos);
    }, []);

    useEffect(() => {
        const resultado = asistencias.filter(a => a.colaborador.toLowerCase().includes(busqueda.toLowerCase()));
        setFiltrados(resultado);
    }, [busqueda, asistencias]);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
                setMenuActivoId(null);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const eliminarAsistencia = (id: number) => {
        const actualizadas = asistencias.filter(a => a.id !== id);
        setAsistencias(actualizadas);
        setFiltrados(actualizadas);
        setMenuActivoId(null);
    };

    const editarAsistencia = (id: number) => {
        const seleccionada = asistencias.find(a => a.id === id);
        if (seleccionada) {
            setEdicion(seleccionada);
            setMostrarModal(true);
        }
    };

    const guardarAsistencia = () => {
        if (!edicion) return;

        const existe = asistencias.some(a => a.id === edicion.id);
        let actualizadas;
        if (existe) {
            actualizadas = asistencias.map(a => a.id === edicion.id ? edicion : a);
        } else {
            const nueva: Asistencia = {
                ...edicion,
                id: asistencias.length + 1,
                fecha: new Date().toISOString().split("T")[0],
            };
            actualizadas = [...asistencias, nueva];
        }

        setAsistencias(actualizadas);
        setFiltrados(actualizadas);
        setMostrarModal(false);
        setEdicion(null);
    };

    return (
        <div id="asistencia">
            <Br_administrativa onMinimizeChange={setMinimizado} />
            <main className={minimizado ? "minimize" : ""}>
                <section id="lst_asistencia">
                    <div id="encabezado">
                        <h2>📋 Asistencia de Colaboradores</h2>
                    </div>
                    <div id="buscador">
                        <input type="text" placeholder="Buscar colaborador..." value={busqueda} onChange={(e) => setBusqueda(e.target.value)} />
                        <button onClick={() => { setMostrarModal(true); setEdicion({ id: 0, colaborador: "", fecha: "", horaEntrada: "", horaSalida: "" }); }}>➕ Registrar Asistencia</button>
                    </div>
                    <div id="lista_asistencia">
                        {filtrados.map((a) => (
                            <div className="registro_asistencia" key={a.id}>
                                <span className="colaborador">{a.colaborador}</span>
                                <span className="fecha">📅 {a.fecha}</span>
                                <span>⏰ {a.horaEntrada} - {a.horaSalida}</span>
                                <div className="lst_opciones_container">
                                    <div className="lst_opciones" onClick={() => setMenuActivoId(a.id)}><i className="fa-solid fa-ellipsis-vertical" /></div>
                                    {menuActivoId === a.id && (
                                        <div ref={menuRef} className="menu-opciones">
                                            <button onClick={() => editarAsistencia(a.id)}>✏️ Editar</button>
                                            <button onClick={() => eliminarAsistencia(a.id)}>🗑️ Eliminar</button>
                                        </div>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                </section>
            </main>

            {mostrarModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>{edicion?.id ? "Editar Asistencia" : "Registrar Nueva Asistencia"}</h3>
                        <input type="text" placeholder="Colaborador" value={edicion?.colaborador || ""} onChange={(e) => setEdicion(edicion ? { ...edicion, colaborador: e.target.value } : null)} />
                        <input type="date" value={edicion?.fecha || ""} onChange={(e) => setEdicion(edicion ? { ...edicion, fecha: e.target.value } : null)} />
                        <input type="time" value={edicion?.horaEntrada || ""} onChange={(e) => setEdicion(edicion ? { ...edicion, horaEntrada: e.target.value } : null)} />
                        <input type="time" value={edicion?.horaSalida || ""} onChange={(e) => setEdicion(edicion ? { ...edicion, horaSalida: e.target.value } : null)} />
                        <div className="acciones-modal">
                            <button onClick={guardarAsistencia}>Guardar</button>
                            <button onClick={() => { setMostrarModal(false); setEdicion(null); }}>Cancelar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default AsistenciaColaboradores;
