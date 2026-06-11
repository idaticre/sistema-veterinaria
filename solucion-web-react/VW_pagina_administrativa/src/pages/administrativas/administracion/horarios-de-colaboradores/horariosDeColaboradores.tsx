import { useEffect, useRef, useState } from 'react'
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa'
import type { ColaboradorRequest } from '../../../../components/interfaces/interfaces';
import type { HorarioColaboradorCard, HorarioRequest, HorarioResponse } from '../../../../components/interfaces/interfaces';
import "./horariosDeColaboradores.css"
import IST from "../../../../components/proteccion/IST";
import Swal from 'sweetalert2';

const DIAS_SEMANA = [
    { id: 1, nombre: "Lunes" },
    { id: 2, nombre: "Martes" },
    { id: 3, nombre: "Miércoles" },
    { id: 4, nombre: "Jueves" },
    { id: 5, nombre: "Viernes" },
    { id: 6, nombre: "Sábado" },
    { id: 7, nombre: "Domingo" },
];

interface FilaDia {
    horarioId: number | null; // null si es nuevo registro
    diaId: number;
    nombreDia: string;
    trabaja: boolean;
    horaInicio: string;
    horaFin: string;
}

type ModoModal = "crear" | "editar";

const filasDiaVacias = (): FilaDia[] =>
    DIAS_SEMANA.map(d => ({ horarioId: null, diaId: d.id, nombreDia: d.nombre, trabaja: false, horaInicio: "", horaFin: "" }));

const horaParaInput = (hora: string | null): string => {
    if (!hora) return "";
    // Recorta segundos si vienen como HH:mm:ss
    return hora.length === 8 ? hora.slice(0, 5) : hora;
};

const formatearHora = (hora: string | null): string => {
    if (!hora) return "—";
    return hora.length === 8 ? hora.slice(0, 5) : hora;
};

function horariosDeColaboradores() {
    const [minimizado, setMinimizado] = useState(false);
    const [busquedaNombre, setBusquedaNombre] = useState("");
    const [diaFiltro, setDiaFiltro] = useState<number | "">("");
    const [colaboradores, setColaboradores] = useState<ColaboradorRequest[]>([]);
    const [tarjetas, setTarjetas] = useState<HorarioColaboradorCard[]>([]);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [modoModal, setModoModal] = useState<ModoModal>("crear");

    // Estado del formulario
    const [colaboradorSeleccionado, setColaboradorSeleccionado] = useState<number | "">("");
    const [filasDia, setFilasDia] = useState<FilaDia[]>(filasDiaVacias());
    const [guardando, setGuardando] = useState(false);

    // ── Carga inicial ──────────────────────────────────────────
    useEffect(() => { obtenerColaboradores(); }, []);
    useEffect(() => { cargarTarjetas(); }, [diaFiltro]);

    const obtenerColaboradores = async () => {
        try {
            const res = await IST.get(`/colaboradores`);
            setColaboradores(res.data.data || res.data);
        } catch {
            Swal.fire({ title: "Error al cargar colaboradores", text: "Por favor, refresque la página", icon: "error" });
        }
    };

    const cargarTarjetas = async () => {
        try {
            const url = diaFiltro !== "" ? `/horarios?dia=${diaFiltro}` : `/horarios`;
            const res = await IST.get(url);
            const horarios: HorarioResponse[] = res.data.data || res.data;
            setTarjetas(agruparPorColaborador(horarios));
        } catch {
            Swal.fire({ title: "Error al cargar horarios", text: "Por favor, refresque la página", icon: "error" });
        }
    };

    const agruparPorColaborador = (horarios: HorarioResponse[]): HorarioColaboradorCard[] => {
        const mapa = new Map<number, HorarioColaboradorCard>();
        horarios.forEach(h => {
            if (!mapa.has(h.trabajadorId)) {
                mapa.set(h.trabajadorId, { trabajadorId: h.trabajadorId, nombreColaborador: h.nombreColaborador, dias: [] });
            }
            mapa.get(h.trabajadorId)!.dias.push(h);
        });
        mapa.forEach(card => card.dias.sort((a, b) => a.diaId - b.diaId));
        return Array.from(mapa.values());
    };

    // ── Filtro por nombre (solo cliente) ──────────────────────
    const tarjetasFiltradas = tarjetas.filter(t =>
        t.nombreColaborador.toLowerCase().includes(busquedaNombre.toLowerCase())
    );

    // ── Helpers de formulario ─────────────────────────────────
    const handleTrabaja = (diaId: number, valor: boolean) => {
        setFilasDia(prev => prev.map(f =>
            f.diaId === diaId
                ? { ...f, trabaja: valor, horaInicio: valor ? f.horaInicio : "", horaFin: valor ? f.horaFin : "" }
                : f
        ));
    };

    const handleHora = (diaId: number, campo: "horaInicio" | "horaFin", valor: string) => {
        setFilasDia(prev => prev.map(f => f.diaId === diaId ? { ...f, [campo]: valor } : f));
    };

    // ── Abrir modal CREAR ─────────────────────────────────────
    const abrirModalCrear = () => {
        setModoModal("crear");
        setColaboradorSeleccionado("");
        setFilasDia(filasDiaVacias());
        setMostrarModal(true);
    };

    // ── Abrir modal EDITAR ────────────────────────────────────
    const abrirModalEditar = (card: HorarioColaboradorCard) => {
        setModoModal("editar");
        setColaboradorSeleccionado(card.trabajadorId);

        // Rellenar filas con datos existentes; días sin registro quedan vacíos
        const filas = DIAS_SEMANA.map(d => {
            const registro = card.dias.find(dia => dia.diaId === d.id);
            return {
                horarioId: registro?.id ?? null,
                diaId: d.id,
                nombreDia: d.nombre,
                trabaja: registro?.trabaja ?? false,
                horaInicio: horaParaInput(registro?.horaInicio ?? null),
                horaFin: horaParaInput(registro?.horaFin ?? null),
            };
        });
        setFilasDia(filas);
        setMostrarModal(true);
    };

    const cerrarModal = () => {
        setMostrarModal(false);
        setColaboradorSeleccionado("");
        setFilasDia(filasDiaVacias());
    };

    // ── Validación común ──────────────────────────────────────
    const validarFilas = (): boolean => {
        const invalido = filasDia.find(f => f.trabaja && (!f.horaInicio || !f.horaFin));
        if (invalido) {
            Swal.fire({ title: "Horas incompletas", text: `Complete la hora de entrada y salida del ${invalido.nombreDia}`, icon: "warning" });
            return false;
        }
        return true;
    };

    // ── POST: guardar horario nuevo ───────────────────────────
    const guardarHorarios = async () => {
        if (colaboradorSeleccionado === "") {
            Swal.fire({ title: "Seleccione un colaborador", icon: "warning" });
            return;
        }
        if (!validarFilas()) return;

        setGuardando(true);
        const errores: string[] = [];

        for (const fila of filasDia) {
            const body: HorarioRequest = {
                trabajadorId: colaboradorSeleccionado as number,
                diaId: fila.diaId,
                trabaja: fila.trabaja,
                horaInicio: fila.trabaja && fila.horaInicio ? fila.horaInicio + ":00" : null,
                horaFin:    fila.trabaja && fila.horaFin    ? fila.horaFin    + ":00" : null,
            };
            try {
                await IST.post(`/asignar-horario`, body);
            } catch (err: any) {
                errores.push(err.response?.data?.message || `Error en ${fila.nombreDia}`);
            }
        }

        setGuardando(false);

        if (errores.length === 0) {
            Swal.fire({ title: "Horario asignado correctamente", icon: "success" });
            cerrarModal();
        } else {
            Swal.fire({ title: "Algunos días no se pudieron guardar", text: errores.join("\n"), icon: "warning" });
        }
        cargarTarjetas();
    };

    // ── PUT: actualizar horario existente ─────────────────────
    const actualizarHorarios = async () => {
        if (!validarFilas()) return;

        setGuardando(true);
        const errores: string[] = [];

        for (const fila of filasDia) {
            if (fila.horarioId === null) continue; // días sin registro previo se ignoran en edición
            const body: HorarioRequest = {
                trabajadorId: colaboradorSeleccionado as number,
                diaId: fila.diaId,
                trabaja: fila.trabaja,
                horaInicio: fila.trabaja && fila.horaInicio ? fila.horaInicio + ":00" : null,
                horaFin:    fila.trabaja && fila.horaFin    ? fila.horaFin    + ":00" : null,
            };
            try {
                await IST.put(`/asignar-horario/${fila.horarioId}`, body);
            } catch (err: any) {
                errores.push(err.response?.data?.message || `Error actualizando ${fila.nombreDia}`);
            }
        }

        setGuardando(false);

        if (errores.length === 0) {
            Swal.fire({ title: "Horario actualizado correctamente", icon: "success" });
            cerrarModal();
        } else {
            Swal.fire({ title: "Algunos días no se pudieron actualizar", text: errores.join("\n"), icon: "warning" });
        }
        cargarTarjetas();
    };

    // ── DELETE ────────────────────────────────────────────────
    const eliminarHorario = async (card: HorarioColaboradorCard) => {
        const confirmacion = await Swal.fire({
            title: "¿Eliminar horario?",
            text: `Se eliminarán todos los días asignados a ${card.nombreColaborador}.`,
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Sí, eliminar",
            cancelButtonText: "Cancelar",
            confirmButtonColor: "#b34d16",
        });

        if (!confirmacion.isConfirmed) return;

        try {
            await IST.delete(`/eliminar-horario/${card.trabajadorId}`);
            Swal.fire({ title: "Horario eliminado correctamente", icon: "success" });
            cargarTarjetas();
        } catch (err: any) {
            Swal.fire({ title: "Error al eliminar", text: err.response?.data?.message || "Inténtelo de nuevo", icon: "error" });
        }
    };

    // ── Render ────────────────────────────────────────────────
    return (
        <div id="cuerpo-main">
            <Br_administrativa onMinimizeChange={setMinimizado} />
            <main className={minimizado ? "minimize" : ""}>
                <section id="listar-registros">
                    <div className="encabezado"><h2>Horarios de colaboradores</h2></div>

                    <div className="goated">
                        <div className="barra-buscador">
                            <input
                                type="text"
                                placeholder="Buscar colaborador por nombre 🔍"
                                value={busquedaNombre}
                                onChange={e => setBusquedaNombre(e.target.value)}
                            />
                        </div>

                        <select
                            className="select-dia-filtro"
                            value={diaFiltro}
                            onChange={e => setDiaFiltro(e.target.value === "" ? "" : Number(e.target.value))}
                        >
                            <option value="">Todos los días</option>
                            {DIAS_SEMANA.map(d => (
                                <option key={d.id} value={d.id}>{d.nombre}</option>
                            ))}
                        </select>

                        <button className="boton-goated anadir-a-goated animacion-goated" onClick={abrirModalCrear}>
                            Asignar horario
                        </button>
                    </div>

                    <div className="listar-registros">
                        <div className="registros">
                            {tarjetasFiltradas.length === 0 && (
                                <p style={{ padding: "1rem", color: "#888" }}>No hay horarios registrados.</p>
                            )}
                            {tarjetasFiltradas.map(card => (
                                <div className="tarjeta-horario" key={card.trabajadorId}>
                                    <h3 className="tarjeta-nombre">{card.nombreColaborador}</h3>
                                    <ul className="tarjeta-dias">
                                        {card.dias.map(dia => (
                                            <li key={dia.diaId} className={`tarjeta-dia-fila ${!dia.trabaja ? "no-trabaja" : ""}`}>
                                                <span className="dia-nombre">{dia.nombreDia}</span>
                                                <span className="dia-horas">
                                                    {dia.trabaja
                                                        ? `${formatearHora(dia.horaInicio)} - ${formatearHora(dia.horaFin)}`
                                                        : "No trabaja"}
                                                </span>
                                            </li>
                                        ))}
                                    </ul>
                                    <div className="tarjeta-acciones">
                                        <button className="btn-tarjeta btn-editar" onClick={() => abrirModalEditar(card)}>
                                            Editar
                                        </button>
                                        <button className="btn-tarjeta btn-eliminar" onClick={() => eliminarHorario(card)}>
                                            Eliminar
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </section>
            </main>

            {mostrarModal && (
                <div className="ventana-overlay">
                    <div className="contenido-ventana ventana-horario">
                        <h3>{modoModal === "crear" ? "Asignar horario" : "Editar horario"}</h3>

                        {modoModal === "crear" && (
                            <>
                                <label>Colaborador</label>
                                <select
                                    value={colaboradorSeleccionado}
                                    onChange={e => setColaboradorSeleccionado(Number(e.target.value))}
                                >
                                    <option value="">— Seleccione un colaborador —</option>
                                    {colaboradores.map(c => (
                                        <option key={c.id} value={c.id}>{c.nombre}</option>
                                    ))}
                                </select>
                            </>
                        )}

                        {modoModal === "editar" && (
                            <p className="modal-nombre-edicion">
                                {tarjetas.find(t => t.trabajadorId === colaboradorSeleccionado)?.nombreColaborador}
                            </p>
                        )}

                        <table className="tabla-dias-modal">
                            <thead>
                                <tr>
                                    <th>Día</th>
                                    <th>Trabaja</th>
                                    <th>Entrada</th>
                                    <th>Salida</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filasDia.map(fila => (
                                    <tr key={fila.diaId}>
                                        <td>{fila.nombreDia}</td>
                                        <td>
                                            <input
                                                type="checkbox"
                                                checked={fila.trabaja}
                                                onChange={e => handleTrabaja(fila.diaId, e.target.checked)}
                                            />
                                        </td>
                                        <td>
                                            <input
                                                type="time"
                                                value={fila.horaInicio}
                                                disabled={!fila.trabaja}
                                                onChange={e => handleHora(fila.diaId, "horaInicio", e.target.value)}
                                            />
                                        </td>
                                        <td>
                                            <input
                                                type="time"
                                                value={fila.horaFin}
                                                disabled={!fila.trabaja}
                                                onChange={e => handleHora(fila.diaId, "horaFin", e.target.value)}
                                            />
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>

                        <div className="acciones-de-registro">
                            <button
                                onClick={modoModal === "crear" ? guardarHorarios : actualizarHorarios}
                                disabled={guardando}
                            >
                                {guardando ? "Guardando..." : "Guardar"}
                            </button>
                            <button onClick={cerrarModal} disabled={guardando}>Cancelar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default horariosDeColaboradores;