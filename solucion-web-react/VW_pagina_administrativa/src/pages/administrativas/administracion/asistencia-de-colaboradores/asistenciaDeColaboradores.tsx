import { useState, useEffect } from "react";
import IST from "../../../../components/proteccion/IST";
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import "./asistencia.css";

// 🔥 AGREGAMOS EL CAMPO 'documento' a la interfaz Colaborador
interface Colaborador {
  id: number;
  codigoColaborador: string;
  nombre: string;
  documento: string; // <-- AGREGADO
  correo?: string;
  telefono?: string;
  activo: boolean;
}

type TipoMarcaFrontend = "ENTRADA" | "LUNCH_OUT" | "LUNCH_IN" | "SALIDA";

interface RegistroAsistenciaBackend {
  mensaje?: string;
  success?: boolean;
  horaMarcacion?: string;
  tipoMarca?: string;

  horaEntrada?: string;
  horaLunchInicio?: string;
  horaLunchFin?: string;
  horaSalida?: string;
  estadoAsistencia?: string;
  estadoFinal?: string;

  descansoProgramado?: boolean; // 🔥 AGREGADO
}

export default function AsistenciaColaboradores() {
  const [minimizado, setMinimizado] = useState(false);
  const [busqueda, setBusqueda] = useState("");
  // 'colaboradores' sigue cargando la lista completa al inicio
  const [colaboradores, setColaboradores] = useState<Colaborador[]>([]);
  const [colaboradorSeleccionado, setColaboradorSeleccionado] = useState<Colaborador | null>(null);
  const [registroHoy, setRegistroHoy] = useState<RegistroAsistenciaBackend | null>(null);
  const [loadingRegistroHoy, setLoadingRegistroHoy] = useState(false);
  const [loadingAction, setLoadingAction] = useState(false);

  const hoyStr = () => new Date().toISOString().split("T")[0];

  /* ============================ CARGAR COLABORADORES (SIN CAMBIOS) ============================ */
  useEffect(() => {
    IST.get("/colaboradores")
      .then((res) => {
        const data = res.data?.data ?? [];
        const normalized: Colaborador[] = data.map((c: any) => ({
          ...c,
          id: Number(c.id),
          activo: Boolean(c.activo),
          // 🔥 Mapeamos el campo 'documento' que viene del DTO
          documento: c.documento, 
        }));
        setColaboradores(normalized);

        const savedRegistroKey = Object.keys(localStorage).find((key) =>
          key.startsWith("registroHoy_")
        );

        if (savedRegistroKey) {
          const registro = JSON.parse(localStorage.getItem(savedRegistroKey) || "{}");
          const colaboradorId = Number(savedRegistroKey.replace("registroHoy_", ""));
          const col = normalized.find((x) => x.id === colaboradorId);
          if (col) {
            setColaboradorSeleccionado(col);
            setRegistroHoy(registro);
          }
        }
      })
      .catch((err) => {
        console.error("Error cargando colaboradores:", err);
        alert("No se pudieron cargar los colaboradores.");
      });
  }, []);

  /* ============================ FILTRAR (Lógica Modificada) ============================ */
  // 🔥 Ahora filtra por nombre O documento
  const filtrados = colaboradores.filter((c) => {
    const busquedaLower = busqueda.toLowerCase();
    return (
      c.nombre?.toLowerCase().includes(busquedaLower) ||
      c.documento?.toLowerCase().includes(busquedaLower) // <-- NUEVA CONDICIÓN
    );
  });

  /* ============================ CARGAR REGISTRO HOY (SIN CAMBIOS) ============================ */
  const cargarRegistroHoy = async (idColaborador: number) => {
    setLoadingRegistroHoy(true);
    try {
      const body = {
        fechaInicio: hoyStr(),
        fechaFin: hoyStr(),
        idColaborador: Number(idColaborador),
        idEstado: null,
      };

      const res = await IST.post("/asistencias/rango", body);
      const lista: RegistroAsistenciaBackend[] = res.data?.data ?? [];
      const registro = lista.length > 0 ? lista[0] : null;

      setRegistroHoy(registro);

      if (registro) {
        localStorage.setItem("registroHoy_" + idColaborador, JSON.stringify(registro));
      }
    } catch (err) {
      console.error("Error cargando registro del día:", err);
      setRegistroHoy(null);
    } finally {
      setLoadingRegistroHoy(false);
    }
  };

  /* ============================ SELECCIONAR COLABORADOR (SIN CAMBIOS) ============================ */
  const seleccionarColaborador = (c: Colaborador) => {
    const normalized = {
      ...c,
      id: Number(c.id),
      activo: Boolean(c.activo),
    };
    setColaboradorSeleccionado(normalized);
    setBusqueda("");
    setRegistroHoy(null);
    cargarRegistroHoy(normalized.id);
  };

  /* ============================ REGLAS (SIN CAMBIOS) ============================ */
  const haMarcadoTipoHoy = (tipo: TipoMarcaFrontend) => {
    if (!registroHoy) return false;
    if (tipo === "ENTRADA") return !!registroHoy.horaEntrada;
    if (tipo === "LUNCH_OUT") return !!registroHoy.horaLunchFin;
    if (tipo === "LUNCH_IN") return !!registroHoy.horaLunchInicio;
    if (tipo === "SALIDA") return !!registroHoy.horaSalida;
    return false;
  };

  const puedeMarcarTipo = (tipo: TipoMarcaFrontend) => {
    if (!colaboradorSeleccionado) return false;
    if (!colaboradorSeleccionado.activo) return false;
    if (haMarcadoTipoHoy(tipo)) return false;
    return true;
  };

  /* ============================ MARCAR ASISTENCIA (SIN CAMBIOS) ============================ */
  const marcarAsistencia = async (tipo: TipoMarcaFrontend) => {
    if (!colaboradorSeleccionado) {
      alert("Selecciona un colaborador.");
      return;
    }

    // 🔥 Mostrar mensaje si es día de descanso
    if (registroHoy?.descansoProgramado) {
      alert("Hoy es un día de descanso programado. No se puede registrar asistencia.");
      return;
    }

    
    setLoadingAction(true);

    const body = {
      idColaborador: Number(colaboradorSeleccionado.id),
      tipoMarca: tipo,
    };

    try {
      const res = await IST.post("/asistencias/registrar", body);
      const data = res.data?.data as RegistroAsistenciaBackend | undefined;

      if (!data) {
        alert("Respuesta inválida del servidor.");
        return;
      }

      if (data?.success === false) {
        alert(data.mensaje ?? "No se pudo marcar.");
        return;
      }

      alert(data.mensaje ?? "Marcación registrada.");

      // 🔥 Actualizar el registro en memoria y localStorage
      setRegistroHoy((prev) => {
        const base = prev ? { ...prev } : {};

        if (data.horaMarcacion) {
          if (tipo === "ENTRADA") base.horaEntrada = data.horaMarcacion;
          if (tipo === "LUNCH_IN") base.horaLunchInicio = data.horaMarcacion;
          if (tipo === "LUNCH_OUT") base.horaLunchFin = data.horaMarcacion;
          if (tipo === "SALIDA") base.horaSalida = data.horaMarcacion;
        }

        base.horaMarcacion = data.horaMarcacion;
        base.estadoAsistencia = data.estadoAsistencia ?? data.estadoFinal;

        if (colaboradorSeleccionado) {
          localStorage.setItem("registroHoy_" + colaboradorSeleccionado.id, JSON.stringify(base));
        }

        return base;
      });
    } catch (err) {
      console.error("Error registrando asistencia:", err);
      alert("Error registrando asistencia.");
    } finally {
      setLoadingAction(false);
    }
  };

  /* ============================ UI ============================ */
  return (
    <div id="asistencia">
      <Br_administrativa onMinimizeChange={setMinimizado} />
      <main className={minimizado ? "minimize" : ""}>
        <section id="lst_asistencia">
          <div id="encabezado">
            <h2>📋 Registro de Asistencia</h2>
          </div>

          {/* BUSCADOR */}
          <div id="buscador">
            <input
              type="text"
              // 🔥 Texto actualizado para indicar la nueva capacidad de búsqueda
              placeholder="Buscar por nombre o documento..." 
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
            />
          </div>

          {/* RESULTADOS */}
          {busqueda.length > 0 && filtrados.length > 0 && (
            <div className="resultados-busqueda">
              {filtrados.map((c) => (
                <div
                  key={c.id}
                  className={`item-colaborador ${colaboradorSeleccionado?.id === c.id ? "seleccionado" : ""}`}
                  onClick={() => seleccionarColaborador(c)}
                >
                  {/* 🔥 CAMBIO CLAVE: Mostramos el documento en lugar de codigoColaborador */}
                  {c.nombre} **({c.documento})** </div>
              ))}
            </div>
          )}

          {/* PANEL */}
          {colaboradorSeleccionado && (
            <div className="panel-registro">
              <h3>👤 {colaboradorSeleccionado.nombre}</h3>
              {/* 🔥 CAMBIO CLAVE: Mostramos el documento en el panel */}
              <p><strong>Documento:</strong> {colaboradorSeleccionado.documento}</p>

              <div className="acciones-asistencia">
                <button
                  disabled={loadingAction}
                  onClick={() => marcarAsistencia("ENTRADA")}
                >
                  🕒 Entrada
                </button>

                <button
                  disabled={loadingAction}
                  onClick={() => marcarAsistencia("LUNCH_IN")}
                >
                  🍽️ Inicio Almuerzo
                </button>

                <button
                  disabled={loadingAction}
                  onClick={() => marcarAsistencia("LUNCH_OUT")}
                >
                  🍴 Fin Almuerzo
                </button>

                <button
                  disabled={loadingAction}
                  onClick={() => marcarAsistencia("SALIDA")}
                >
                  🏁 Salida Final
                </button>
              </div>


              <h4>📅 Registro del día</h4>
              <div className="tabla-asistencia">
                {loadingRegistroHoy && <p>Cargando registro...</p>}
                {!loadingRegistroHoy && !registroHoy && <p>No hay marcas hoy.</p>}
                {!loadingRegistroHoy && registroHoy && (
                  <div className="fila-asistencia">
                    <div><strong>Entrada:</strong> {registroHoy.horaEntrada ?? "—"}</div>
                    <div><strong>Inicio Almuerzo:</strong> {registroHoy.horaLunchInicio ?? "—"}</div>
                    <div><strong>Fin Almuerzo:</strong> {registroHoy.horaLunchFin ?? "—"}</div>
                    <div><strong>Salida:</strong> {registroHoy.horaSalida ?? "—"}</div>
                    <div><strong>Última marcación:</strong> {registroHoy.horaMarcacion ?? "—"}</div>
                    <div><strong>Estado:</strong> {registroHoy.estadoAsistencia ?? registroHoy.estadoFinal ?? "—"}</div>
                  </div>
                )}
              </div>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}