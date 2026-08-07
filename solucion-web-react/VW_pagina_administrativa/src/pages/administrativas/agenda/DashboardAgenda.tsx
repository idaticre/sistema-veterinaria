import { useState, useEffect, useMemo, useCallback } from "react";
import Br_administrativa from "../../../components/barra_administrativa/Br_administrativa";
import {
  PieChart,
  Pie,
  Cell,
  Legend,
  ResponsiveContainer,
  Tooltip,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
} from "recharts";
import IST from "../../../components/proteccion/IST";
import "./DashboardAgenda.css";

// =======================================================
// INTERFACES (mismas formas que EditarCita.tsx / Agenda_general.tsx)
// =======================================================
interface CitaBD {
  id: number;
  codigo: string;
  idCliente: number;
  idMascota: number;
  idMedioSolicitud: number;
  fecha: string;
  hora: string;
  duracionEstimadaMin: number;
  abonoInicial: number;
  totalCita: number;
  idEstado: number;
  observaciones: string;
  fechaRegistro: string;
  idColaborador?: number;
}

interface EstadoAgenda {
  id: number;
  nombre: string;
}

interface EntityBase {
  id: number;
  nombre: string;
  documento: string;
  activo: boolean;
}

interface MascotaBase {
  id: number;
  nombre: string;
  cliente: {
    id: number;
    nombre: string;
  };
}

// ===========================
// 🎨 COLORES GENERALES
// ===========================
const COLORS: Record<string, string> = {
  PENDIENTE: "#ffc107",
  CONFIRMADA: "#4d7cff",
  REPROGRAMADA: "#9b7200",
  CANCELADA: "#d9534f",
  ATENDIDA: "#28a745",
  "NO ASISTIÓ": "#6c757d",
};
const COLORS_FALLBACK = ["#ff9900", "#b34d16", "#9b7200", "#4d7cff", "#d9534f", "#28a745"];

// Estados que ya no cuentan como "activos" en el día
const ESTADOS_FINALIZADOS = ["CANCELADA", "ATENDIDA", "NO ASISTIÓ"];

const INTERVALO_REFRESCO_MS = 20000; // 20s — tiempo real vía polling

// TODO: reemplazar por la forma real en que guardas el rol del usuario logueado.
// Ahora mismo el proyecto solo guarda "token" en sessionStorage (ver EditarCita.tsx / Agenda_general.tsx).
// Si el rol viene decodificado del JWT o en otro campo, ajustar aquí.
function esAdministrador(): boolean {
  const rol = sessionStorage.getItem("rol"); // <-- ajustar clave real
  if (!rol) {
    console.warn(
      "[DashboardAgenda] No se encontró el rol en sesión. Se muestra el dashboard sin restricción hasta conectar el campo real.",
    );
    return true; // fail-open temporal, cambiar a false cuando el rol esté disponible
  }
  return rol.toUpperCase().includes("ADMINISTRADOR");
}

// ================================================================
// GESTIÓN DE ALERTAS DE PAGO — 100% FRONTEND (localStorage)
// No existe endpoint de recordatorios/alertas en el backend (solo /agenda),
// así que esto persiste en el navegador. Si más adelante se agrega el
// endpoint, basta con cambiar estas 4 funciones por llamadas a IST.
// ================================================================
const LS_KEY_GESTION = "dashboard_alertas_gestion"; // { [idAgenda]: GestionAlerta }
const LS_KEY_RECORDATORIOS = "dashboard_alertas_recordatorios"; // { [idAgenda]: Recordatorio[] }

interface GestionAlerta {
  atendida: boolean;
  fechaGestion: string; // ISO
  usuario: string;
}

interface Recordatorio {
  fecha: string; // ISO
  usuario: string;
}

// Intenta identificar al usuario actual con las claves más comunes que
// suelen usarse en sessionStorage/localStorage o dentro del JWT.
function obtenerUsuarioActual(): string {
  const directos = ["username", "usuario", "nombreUsuario", "user"];
  for (const key of directos) {
    const v = sessionStorage.getItem(key) || localStorage.getItem(key);
    if (v) return v;
  }
  const token = sessionStorage.getItem("token");
  if (token) {
    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      return payload.username || payload.sub || payload.usuario || "Usuario";
    } catch {
      // token no es un JWT decodificable, seguimos al fallback
    }
  }
  return "Usuario desconocido";
}

function cargarGestionAlertas(): Record<number, GestionAlerta> {
  try {
    return JSON.parse(localStorage.getItem(LS_KEY_GESTION) || "{}");
  } catch {
    return {};
  }
}

function guardarGestionAlertas(data: Record<number, GestionAlerta>) {
  localStorage.setItem(LS_KEY_GESTION, JSON.stringify(data));
}

function cargarRecordatorios(): Record<number, Recordatorio[]> {
  try {
    return JSON.parse(localStorage.getItem(LS_KEY_RECORDATORIOS) || "{}");
  } catch {
    return {};
  }
}

function guardarRecordatorios(data: Record<number, Recordatorio[]>) {
  localStorage.setItem(LS_KEY_RECORDATORIOS, JSON.stringify(data));
}

export default function DashboardAgenda() {
  const [minimizado, setMinimizado] = useState(false);

  const [citas, setCitas] = useState<CitaBD[]>([]);
  const [estadosAgenda, setEstadosAgenda] = useState<EstadoAgenda[]>([]);
  const [clientes, setClientes] = useState<EntityBase[]>([]);
  const [mascotas, setMascotas] = useState<MascotaBase[]>([]);

  const [cargando, setCargando] = useState(true);
  const [ultimaActualizacion, setUltimaActualizacion] = useState<Date | null>(null);
  const [status, setStatus] = useState("🟡 Cargando datos...");

  // Filtros (criterio de aceptación: filtrar por fecha o rango horario)
  const hoyISO = new Date().toISOString().split("T")[0];
  const [filtroFecha, setFiltroFecha] = useState(hoyISO);
  const [filtroHoraInicio, setFiltroHoraInicio] = useState("");
  const [filtroHoraFin, setFiltroHoraFin] = useState("");

  // Gestión de alertas de pago (atendida/no atendida + fecha + usuario) y
  // recordatorios enviados — persistidos en localStorage, ver funciones arriba.
  const [gestionAlertas, setGestionAlertas] = useState<Record<number, GestionAlerta>>({});
  const [recordatorios, setRecordatorios] = useState<Record<number, Recordatorio[]>>({});
  const [toast, setToast] = useState<string | null>(null);

  const permitido = useMemo(() => esAdministrador(), []);

  // Cargar lo guardado en el navegador al montar el componente
  useEffect(() => {
    setGestionAlertas(cargarGestionAlertas());
    setRecordatorios(cargarRecordatorios());
  }, []);

  // Toast simple para reemplazar el alert() bloqueante
  useEffect(() => {
    if (!toast) return;
    const t = setTimeout(() => setToast(null), 3500);
    return () => clearTimeout(t);
  }, [toast]);

  // ================== CARGA DE DATOS ==================
  const cargarDatos = useCallback(async (silencioso = false) => {
    if (!silencioso) setCargando(true);
    try {
      const [resEstados, resAgenda, resClientes, resMascotas] = await Promise.all([
        IST.get("/estados-agenda"),
        // size grande porque necesitamos el conjunto completo para agregarlo en frontend
        IST.get("/agenda?page=0&size=2000"),
        IST.get("/clientes"),
        IST.get("/mascotas"),
      ]);

      const estadosData = Array.isArray(resEstados.data) ? resEstados.data : resEstados.data.data;
      setEstadosAgenda(
        estadosData.map((e: any) => ({ id: e.id, nombre: e.nombre.toUpperCase() })),
      );

      const citasData: CitaBD[] = resAgenda.data?.data?.content || [];
      setCitas(citasData);

      setClientes(resClientes.data?.data?.filter((c: any) => c.activo) || []);
      setMascotas(resMascotas.data?.data || []);

      setUltimaActualizacion(new Date());
      setStatus(`✅ ${citasData.length} citas cargadas`);
    } catch (error) {
      setStatus("⚠️ Error al cargar datos del dashboard");
    } finally {
      if (!silencioso) setCargando(false);
    }
  }, []);

  useEffect(() => {
    cargarDatos();
  }, [cargarDatos]);

  // ================== TIEMPO REAL (polling) ==================
  useEffect(() => {
    const interval = setInterval(() => {
      // Evita refrescar si la pestaña no está visible, para no gastar llamadas de más
      if (document.visibilityState === "visible") {
        cargarDatos(true);
      }
    }, INTERVALO_REFRESCO_MS);
    return () => clearInterval(interval);
  }, [cargarDatos]);

  // ================== HELPERS ==================
  const nombreEstado = (idEstado: number) =>
    estadosAgenda.find((e) => e.id === idEstado)?.nombre || "DESCONOCIDO";

  const nombreCliente = (idCliente: number) =>
    clientes.find((c) => c.id === idCliente)?.nombre || `Cliente #${idCliente}`;

  const nombreMascota = (idMascota: number) =>
    mascotas.find((m) => m.id === idMascota)?.nombre || `Mascota #${idMascota}`;

  // ================== FILTRADO ==================
  const citasFiltradas = useMemo(() => {
    return citas.filter((c) => {
      if (filtroFecha && c.fecha !== filtroFecha) return false;
      if (filtroHoraInicio && c.hora < filtroHoraInicio) return false;
      if (filtroHoraFin && c.hora > filtroHoraFin) return false;
      return true;
    });
  }, [citas, filtroFecha, filtroHoraInicio, filtroHoraFin]);

  // ================== MÉTRICAS EN TIEMPO REAL ==================
  const citasPorEstado = useMemo(() => {
    const conteo: Record<string, number> = {};
    estadosAgenda.forEach((e) => (conteo[e.nombre] = 0));
    citasFiltradas.forEach((c) => {
      const nombre = nombreEstado(c.idEstado);
      conteo[nombre] = (conteo[nombre] || 0) + 1;
    });
    return Object.entries(conteo)
      .filter(([, value]) => value > 0)
      .map(([name, value]) => ({ name, value }));
  }, [citasFiltradas, estadosAgenda]);

  const totalCitas = citasFiltradas.length;

  const citasActivas = citasFiltradas.filter(
    (c) => !ESTADOS_FINALIZADOS.includes(nombreEstado(c.idEstado)),
  ).length;

  const mascotasEnEspera = citasFiltradas.filter(
    (c) => nombreEstado(c.idEstado) === "PENDIENTE",
  ).length;

  const serviciosEnEjecucion = citasFiltradas.filter(
    (c) => nombreEstado(c.idEstado) === "CONFIRMADA",
  ).length;

  const porcentaje = (cantidad: number) =>
    totalCitas === 0 ? "0.0%" : ((cantidad / totalCitas) * 100).toFixed(1) + "%";

  const cantidadPorEstado = (nombre: string) =>
    citasPorEstado.find((c) => c.name === nombre)?.value || 0;

  // ================== ALERTAS DE PAGOS PENDIENTES ==================
  // saldo = total_cita - abono_inicial (mismo cálculo que EditarCita.tsx / Agenda_general.tsx)
  // Se excluyen citas CANCELADAS: no tiene sentido cobrar algo que no se realizará.
  const alertasPago = useMemo(() => {
    return citasFiltradas
      .map((c) => ({
        ...c,
        saldo: Math.max(0, (c.totalCita || 0) - (c.abonoInicial || 0)),
        estadoNombre: nombreEstado(c.idEstado),
      }))
      .filter((c) => c.saldo > 0 && c.estadoNombre !== "CANCELADA")
      .sort((a, b) => b.saldo - a.saldo);
  }, [citasFiltradas, clientes, mascotas, estadosAgenda]);

  const totalPendienteCobrar = alertasPago.reduce((sum, c) => sum + c.saldo, 0);

  // Marca/desmarca la alerta como atendida, guardando fecha y usuario que
  // la gestionó — cumple el criterio de aceptación de la HU de pagos.
  const marcarAlerta = (idAgenda: number) => {
    setGestionAlertas((prev) => {
      const yaAtendida = prev[idAgenda]?.atendida === true;
      const actualizado: Record<number, GestionAlerta> = {
        ...prev,
        [idAgenda]: {
          atendida: !yaAtendida,
          fechaGestion: new Date().toISOString(),
          usuario: obtenerUsuarioActual(),
        },
      };
      guardarGestionAlertas(actualizado);
      return actualizado;
    });
  };

  // Registra el envío de un recordatorio. No hay canal real de envío (no hay
  // endpoint de email/WhatsApp), así que esto deja constancia local de que
  // "se gestionó" el recordatorio, con fecha y usuario.
  const enviarRecordatorio = (cita: CitaBD) => {
    const nuevoRecordatorio: Recordatorio = {
      fecha: new Date().toISOString(),
      usuario: obtenerUsuarioActual(),
    };
    setRecordatorios((prev) => {
      const historialPrevio = prev[cita.id] || [];
      const actualizado = {
        ...prev,
        [cita.id]: [...historialPrevio, nuevoRecordatorio],
      };
      guardarRecordatorios(actualizado);
      return actualizado;
    });

    setToast(
      `📨 Recordatorio registrado para ${nombreCliente(cita.idCliente)} — Saldo: S/${Math.max(
        0,
        (cita.totalCita || 0) - (cita.abonoInicial || 0),
      ).toFixed(2)}`,
    );
  };

  // ================== GUARD DE ROL ==================
  if (!permitido) {
    return (
      <div id="dashboard_agenda">
        <Br_administrativa onMinimizeChange={setMinimizado} />
        <main className={minimizado ? "minimize" : ""}>
          <div className="no-eventos">
            ⛔ No tienes permisos para acceder a este dashboard.
          </div>
        </main>
      </div>
    );
  }

  // ================== RENDER ==================
  return (
    <div id="dashboard_agenda">
      <Br_administrativa onMinimizeChange={setMinimizado} />

      <main className={minimizado ? "minimize" : ""}>
        {/* ======================================================
            BARRA DE ESTADO + FILTROS
        ====================================================== */}
        <div id="dashboard_toolbar">
          <div className="filtros-dashboard">
            <div className="filtro-item">
              <label>Fecha</label>
              <input
                type="date"
                value={filtroFecha}
                onChange={(e) => setFiltroFecha(e.target.value)}
              />
            </div>
            <div className="filtro-item">
              <label>Desde</label>
              <input
                type="time"
                value={filtroHoraInicio}
                onChange={(e) => setFiltroHoraInicio(e.target.value)}
              />
            </div>
            <div className="filtro-item">
              <label>Hasta</label>
              <input
                type="time"
                value={filtroHoraFin}
                onChange={(e) => setFiltroHoraFin(e.target.value)}
              />
            </div>
            {(filtroFecha !== hoyISO || filtroHoraInicio || filtroHoraFin) && (
              <button
                className="btn-limpiar-dash"
                onClick={() => {
                  setFiltroFecha(hoyISO);
                  setFiltroHoraInicio("");
                  setFiltroHoraFin("");
                }}
              >
                ✖️ Limpiar filtros
              </button>
            )}
          </div>

          <div className="status-dashboard">
            <span>{status}</span>
            {ultimaActualizacion && (
              <span className="ultima-act">
                🔄 Actualizado {ultimaActualizacion.toLocaleTimeString("es-PE")}
              </span>
            )}
            <button className="btn-refrescar" onClick={() => cargarDatos()}>
              ↻ Refrescar ahora
            </button>
          </div>
        </div>

        {cargando ? (
          <div className="no-eventos">Cargando información del dashboard...</div>
        ) : (
          <>
            {/* ======================================================
                RESUMEN GENERAL
            ====================================================== */}
            <div id="resumen_cards">
              <div className="card_resumen">
                <h3>Citas Totales</h3>
                <p>{totalCitas}</p>
              </div>
              <div className="card_resumen">
                <h3>Citas Activas</h3>
                <p>{citasActivas}</p>
              </div>
              <div className="card_resumen">
                <h3>Mascotas en Espera</h3>
                <p>{mascotasEnEspera}</p>
              </div>
              <div className="card_resumen">
                <h3>Servicios en Ejecución</h3>
                <p>{serviciosEnEjecucion}</p>
              </div>
              <div className="card_resumen card_resumen_alerta">
                <h3>Pagos Pendientes</h3>
                <p>S/ {totalPendienteCobrar.toFixed(2)}</p>
              </div>
            </div>

            {/* ======================================================
                FILA DE GRÁFICOS
            ====================================================== */}
            <div id="fila_graficos">
              <div className="grafico_box">
                <h3>Citas por Estado</h3>
                {citasPorEstado.length === 0 ? (
                  <p className="sin-datos">No hay citas para el filtro seleccionado.</p>
                ) : (
                  <ResponsiveContainer width="100%" height={260}>
                    <PieChart>
                      <Pie
                        dataKey="value"
                        data={citasPorEstado}
                        cx="50%"
                        cy="50%"
                        outerRadius={80}
                        paddingAngle={4}
                      >
                        {citasPorEstado.map((entry, i) => (
                          <Cell
                            key={i}
                            fill={COLORS[entry.name] || COLORS_FALLBACK[i % COLORS_FALLBACK.length]}
                          />
                        ))}
                      </Pie>
                      <Tooltip />
                      <Legend />
                    </PieChart>
                  </ResponsiveContainer>
                )}
              </div>

              <div className="grafico_box">
                <h3>Distribución de Estados (%)</h3>
                {citasPorEstado.length === 0 ? (
                  <p className="sin-datos">No hay citas para el filtro seleccionado.</p>
                ) : (
                  <div className="lista-porcentajes">
                    {citasPorEstado.map((e) => (
                      <div key={e.name} className="fila-porcentaje">
                        <span
                          className="dot-estado"
                          style={{
                            background:
                              COLORS[e.name] ||
                              COLORS_FALLBACK[citasPorEstado.indexOf(e) % COLORS_FALLBACK.length],
                          }}
                        ></span>
                        <span className="nombre-estado">{e.name}</span>
                        <span className="valor-estado">
                          {e.value} ({porcentaje(e.value)})
                        </span>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>

            {/* -------- Citas por estado (barras) -------- */}
            <div id="servicios_grafico">
              <h3>Comparativo de Citas por Estado</h3>
              {citasPorEstado.length === 0 ? (
                <p className="sin-datos">No hay citas para el filtro seleccionado.</p>
              ) : (
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={citasPorEstado}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="name" stroke="#333" />
                    <YAxis stroke="#333" allowDecimals={false} />
                    <Tooltip />
                    <Bar dataKey="value" fill="#4d7cff">
                      {citasPorEstado.map((entry, i) => (
                        <Cell
                          key={i}
                          fill={COLORS[entry.name] || COLORS_FALLBACK[i % COLORS_FALLBACK.length]}
                        />
                      ))}
                    </Bar>
                  </BarChart>
                </ResponsiveContainer>
              )}
            </div>

            {/* ======================================================
                ALERTAS DE PAGOS PENDIENTES
            ====================================================== */}
            <div id="alertas_pagos">
              <div className="alertas-header">
                <h3>💰 Alertas de Pagos Pendientes</h3>
                <span className="contador-alertas">
                  {alertasPago.length} cliente(s) con saldo pendiente
                </span>
              </div>

              {alertasPago.length === 0 ? (
                <p className="sin-datos">No hay pagos pendientes para el filtro seleccionado.</p>
              ) : (
                <div style={{ overflowX: "auto" }}>
                  <table className="tabla-alertas">
                    <thead>
                      <tr>
                        <th>Código</th>
                        <th>Cliente</th>
                        <th>Mascota</th>
                        <th>Fecha</th>
                        <th>Estado</th>
                        <th>Total</th>
                        <th>Abonado</th>
                        <th>Saldo</th>
                        <th>Gestión</th>
                        <th></th>
                      </tr>
                    </thead>
                    <tbody>
                      {alertasPago.map((c) => {
                        const gestion = gestionAlertas[c.id];
                        const atendida = gestion?.atendida === true;
                        const historialRec = recordatorios[c.id] || [];
                        const ultimoRecordatorio = historialRec[historialRec.length - 1];

                        return (
                          <tr key={c.id} className={atendida ? "fila-atendida" : ""}>
                            <td>{c.codigo}</td>
                            <td>{nombreCliente(c.idCliente)}</td>
                            <td>{nombreMascota(c.idMascota)}</td>
                            <td>{c.fecha}</td>
                            <td>
                              <span
                                className={`estado-badge estado-${c.estadoNombre.toLowerCase()}`}
                              >
                                {c.estadoNombre}
                              </span>
                            </td>
                            <td>S/ {(c.totalCita || 0).toFixed(2)}</td>
                            <td>S/ {(c.abonoInicial || 0).toFixed(2)}</td>
                            <td className="saldo-pendiente">S/ {c.saldo.toFixed(2)}</td>
                            <td>
                              <span className={atendida ? "badge-atendida" : "badge-no-atendida"}>
                                {atendida ? "✅ Atendida" : "⏳ No atendida"}
                              </span>
                              {gestion && (
                                <div className="gestion-detalle">
                                  {new Date(gestion.fechaGestion).toLocaleString("es-PE")} —{" "}
                                  {gestion.usuario}
                                </div>
                              )}
                              {ultimoRecordatorio && (
                                <div className="gestion-detalle">
                                  📨 {historialRec.length}x — última{" "}
                                  {new Date(ultimoRecordatorio.fecha).toLocaleString("es-PE")}
                                </div>
                              )}
                            </td>
                            <td className="acciones-alerta">
                              <button
                                className="btn-recordatorio"
                                onClick={() => enviarRecordatorio(c)}
                                title="Registrar envío de recordatorio al cliente"
                              >
                                📨
                              </button>
                              <button
                                className="btn-toggle-alerta"
                                onClick={() => marcarAlerta(c.id)}
                                title={atendida ? "Marcar como no atendida" : "Marcar como atendida"}
                              >
                                {atendida ? "↺" : "✔"}
                              </button>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </>
        )}
      </main>

      {toast && <div className="toast-dashboard">{toast}</div>}
    </div>
  );
}
