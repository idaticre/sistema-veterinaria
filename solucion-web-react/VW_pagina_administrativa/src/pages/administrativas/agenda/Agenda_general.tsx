import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import Br_administrativa from "../../../components/barra_administrativa/Br_administrativa";
import "./Agenda_genera.css";
import IST from "../../../components/proteccion/IST";

const CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;
const API_KEY = import.meta.env.VITE_GOOGLE_API_KEY;

declare global {
  interface Window {
    google: any;
    gapi: any;
  }
}

interface Evento {
  id?: string;
  summary: string;
  description?: string;
  start: { dateTime: string; timeZone: string };
  end: { dateTime: string; timeZone: string };
  htmlLink?: string;
}

function Agenda_general() {
  const navigate = useNavigate();
  const [minimizado, setMinimizado] = useState(false);
  const [fechaSeleccionada, setFechaSeleccionada] = useState<Date>(new Date());
  const [isSignedIn, setIsSignedIn] = useState(false);
  const [status, setStatus] = useState("🔌 Inicializando Google Calendar...");
  const [tokenClient, setTokenClient] = useState<any>(null);
  const [gapiInited, setGapiInited] = useState(false);
  const [gisInited, setGisInited] = useState(false);
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [mostrarModal, setMostrarModal] = useState(false);

  const [clientes, setClientes] = useState<any[]>([]);
  const [mascotas, setMascotas] = useState<any[]>([]);
  const [colaboradores, setColaboradores] = useState<any[]>([]);

  const [nuevoEvento, setNuevoEvento] = useState({
    summary: "",
    description: "",
    dni: "",
    cliente: "",
    clienteId: 0,
    mascota: "",
    servicio: "",
    colaborador: "",
    date: "",
    startTime: "10:00",
    duracion: "30",
    estado: "",
  });

  // ================== CARGAR CLIENTES / COLABORADORES / MASCOTAS ==================
  useEffect(() => {
    IST.get("/clientes").then((r) => setClientes(r.data.data.filter((c: any) => c.activo)));
    IST.get("/colaboradores").then((r) => setColaboradores(r.data.data.filter((c: any) => c.activo)));
    IST.get("/mascotas").then((res) => setMascotas(res.data.data)).catch(() => setMascotas([]));
  }, []);

  // ================== CARGA GAPI ==================
  useEffect(() => {
    const script = document.createElement("script");
    script.src = "https://apis.google.com/js/api.js";
    script.onload = async () => {
      await new Promise((resolve) => window.gapi.load("client", { callback: resolve }));
      await window.gapi.client.init({
        apiKey: API_KEY,
        discoveryDocs: ["https://www.googleapis.com/discovery/v1/apis/calendar/v3/rest"],
      });
      setGapiInited(true);
    };
    document.body.appendChild(script);
  }, []);

  // ================== CARGA GIS ==================
  useEffect(() => {
    const script = document.createElement("script");
    script.src = "https://accounts.google.com/gsi/client";
    script.onload = () => {
      const client = window.google.accounts.oauth2.initTokenClient({
        client_id: CLIENT_ID,
        scope: "https://www.googleapis.com/auth/calendar.events https://www.googleapis.com/auth/calendar.readonly",
        callback: (tokenResponse: any) => {
          if (tokenResponse.access_token) {
            window.gapi.client.setToken({ access_token: tokenResponse.access_token });
            localStorage.setItem("google_token", tokenResponse.access_token);
            setIsSignedIn(true);
            cargarEventos();
          }
        },
      });
      setTokenClient(client);
      setGisInited(true);
    };
    document.body.appendChild(script);
  }, []);

  useEffect(() => {
    if (gapiInited && gisInited) setStatus("✅ Google Calendar listo para usar");
  }, [gapiInited, gisInited]);

  // ============= RESTAURAR SESIÓN =============
  useEffect(() => {
    if (!gapiInited || !gisInited) return;
    const saved = localStorage.getItem("google_token");
    if (saved) {
      window.gapi.client.setToken({ access_token: saved });
      setIsSignedIn(true);
      setStatus("🔓 Sesión restaurada automáticamente");
      cargarEventos();
    }
  }, [gapiInited, gisInited]);

  const iniciarSesion = () => tokenClient?.requestAccessToken();
  const cerrarSesion = () => {
    const token = window.gapi.client.getToken();
    if (token) window.google.accounts.oauth2.revoke(token.access_token);
    window.gapi.client.setToken(null);
    localStorage.removeItem("google_token");
    setIsSignedIn(false);
    setEventos([]);
  };

  const cargarEventos = async () => {
    if (!isSignedIn) return;
    const inicio = new Date(fechaSeleccionada);
    inicio.setHours(0, 0, 0, 0);
    const fin = new Date(fechaSeleccionada);
    fin.setHours(23, 59, 59, 999);
    const res = await window.gapi.client.calendar.events.list({
      calendarId: "primary",
      timeMin: inicio.toISOString(),
      timeMax: fin.toISOString(),
      singleEvents: true,
      orderBy: "startTime",
    });
    setEventos(res.result.items || []);
  };

  useEffect(() => {
    if (isSignedIn) cargarEventos();
  }, [fechaSeleccionada, isSignedIn]);

  // ================== VALIDAR HORARIOS ==================
  const horaOcupada = (start: Date, end: Date) => {
    return eventos.some((e) => {
      const eStart = new Date(e.start.dateTime);
      const eEnd = new Date(e.end.dateTime);
      return (start < eEnd && end > eStart); // verifica solapamiento
    });
  };

  const guardarEvento = async () => {
    if (!nuevoEvento.cliente || !nuevoEvento.mascota || !nuevoEvento.servicio || !nuevoEvento.colaborador)
      return alert("Completa los campos obligatorios");

    const start = new Date(`${nuevoEvento.date}T${nuevoEvento.startTime}`);
    const end = new Date(start.getTime() + (parseInt(nuevoEvento.duracion) || 30) * 60000);

    if (horaOcupada(start, end)) {
      return alert("⚠️ Ya existe una cita en este horario. Elige otro horario.");
    }

    const evento = {
      summary: `${nuevoEvento.servicio} - ${nuevoEvento.mascota}`,
      description: `Cliente: ${nuevoEvento.cliente}\nMascota: ${nuevoEvento.mascota}\nServicio: ${nuevoEvento.servicio}\nColaborador: ${nuevoEvento.colaborador}\nEstado: ${nuevoEvento.estado}\nObservaciones: ${nuevoEvento.description}`,
      start: { dateTime: start.toISOString(), timeZone: "America/Lima" },
      end: { dateTime: end.toISOString(), timeZone: "America/Lima" },
    };

    await window.gapi.client.calendar.events.insert({ calendarId: "primary", resource: evento });
    setMostrarModal(false);
    cargarEventos();
  };

  return (
    <div id="agenda">
      <Br_administrativa onMinimizeChange={setMinimizado} />
      <main className={minimizado ? "minimize" : ""}>
        <section className="agenda-container">
          <h2 className="titulo-agenda">Agenda de Citas</h2>
          <div className="agenda-layout">
            <div className="calendar-container">
              <Calendar
                onChange={(date) => setFechaSeleccionada(date as Date)}
                value={fechaSeleccionada}
                locale="es-ES"
              />
              <div className="auth-buttons">
                {!isSignedIn ? (
                  <button className="btn-agregar" onClick={iniciarSesion}>🔐 Iniciar sesión</button>
                ) : (
                  <button className="btn-cerrar" onClick={cerrarSesion}>🚪 Cerrar sesión</button>
                )}
              </div>
            </div>
            <div className="citas-container">
              <div className="citas-header">
                <h3>📋 Eventos del {fechaSeleccionada.toLocaleDateString()}</h3>
                {isSignedIn && (
                  <button
                    className="btn-agregar-linda"
                    onClick={() => {
                      setNuevoEvento((prev) => ({
                        ...prev,
                        summary: "",
                        description: "",
                        dni: "",
                        cliente: "",
                        clienteId: 0,
                        mascota: "",
                        servicio: "",
                        date: fechaSeleccionada.toISOString().split("T")[0],
                        startTime: "10:00",
                        duracion: "30",
                        estado: "",
                        colaborador: prev.colaborador || "",
                      }));
                      setMostrarModal(true);
                    }}
                  >
                    ✨➕ Nueva cita
                  </button>
                )}
              </div>
              <div className="linea-divisoria"></div>
              {eventos.length === 0 ? (
                <p>No hay eventos para este día.</p>
              ) : (
                <div className="citas-lista">
                  {eventos.map((e) => {
                    const detalles =
                      e.description?.split("\n").reduce((acc: any, linea) => {
                        const [k, v] = linea.split(":");
                        if (k && v) acc[k.trim()] = v.trim();
                        return acc;
                      }, {}) || {};
                    const inicio = new Date(e.start.dateTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
                    const fin = new Date(e.end.dateTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
                    return (
                      <div key={e.id} className="cita-card">
                        <p><strong>Cliente:</strong> {detalles.Cliente}</p>
                        <p><strong>Mascota:</strong> {detalles.Mascota}</p>
                        <p><strong>Servicio:</strong> {detalles.Servicio}</p>
                        <p><strong>Colaborador:</strong> {detalles.Colaborador}</p>
                        <p><strong>Hora:</strong> {inicio} - {fin}</p>
                        <button className="btn-mas-info" onClick={() => navigate(`/administracion/agenda/EditarCita`)}>📄 Más información</button>
                      </div>
                    );
                  })}
                </div>
              )}
              <p style={{ marginTop: "10px", color: "#555" }}>{status}</p>
            </div>
          </div>
        </section>
      </main>

      {mostrarModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Agendar nueva cita 🗓️</h3>
            <div className="col-izq">
              <label>Fecha *</label>
              <input type="date" value={nuevoEvento.date} onChange={(e) => setNuevoEvento({ ...nuevoEvento, date: e.target.value })} />
              <label>Hora *</label>
              <input type="time" value={nuevoEvento.startTime} onChange={(e) => setNuevoEvento({ ...nuevoEvento, startTime: e.target.value })} />
              <label>Duración (min)</label>
              <input type="number" value={nuevoEvento.duracion} onChange={(e) => setNuevoEvento({ ...nuevoEvento, duracion: e.target.value })} />
              <label>Estado *</label>
              <select value={nuevoEvento.estado} onChange={(e) => setNuevoEvento({ ...nuevoEvento, estado: e.target.value })}>
                <option value="">Seleccione...</option>
                <option value="Pendiente">Pendiente</option>
                <option value="Confirmado">Confirmado</option>
                <option value="Cancelado">Cancelado</option>
              </select>
            </div>

            <div className="col-der">
              <label>DNI *</label>
              <input type="text" value={nuevoEvento.dni} onChange={(e) => {
                const dni = e.target.value;
                setNuevoEvento({ ...nuevoEvento, dni });
                const encontrado = clientes.find((c) => c.documento === dni);
                if (encontrado) {
                  setNuevoEvento((p) => ({ ...p, cliente: encontrado.nombre, clienteId: encontrado.id, mascota: "" }));
                } else {
                  setNuevoEvento((p) => ({ ...p, cliente: "", clienteId: 0, mascota: "" }));
                }
              }} />

              <label>Cliente *</label>
              <input type="text" value={nuevoEvento.cliente} disabled />

              <label>Mascota *</label>
              <select value={nuevoEvento.mascota} onChange={(e) => setNuevoEvento({ ...nuevoEvento, mascota: e.target.value })} disabled={!nuevoEvento.clienteId}>
                <option value="">Seleccione mascota...</option>
                {mascotas.filter(m => m.idCliente === nuevoEvento.clienteId).map((m) => (
                  <option key={m.id} value={m.nombre}>{m.nombre}</option>
                ))}
              </select>

              <label>Servicio *</label>
              <select value={nuevoEvento.servicio} onChange={(e) => setNuevoEvento({ ...nuevoEvento, servicio: e.target.value })} disabled={!nuevoEvento.mascota}>
                <option value="">Seleccione...</option>
                <option value="Baño">Baño</option>
                <option value="Consulta">Consulta</option>
                <option value="Vacunación">Vacunación</option>
                <option value="Control">Control</option>
              </select>

              <label>Colaborador *</label>
              <select
                value={nuevoEvento.colaborador || ""}
                onChange={(e) => setNuevoEvento({ ...nuevoEvento, colaborador: e.target.value })}
                disabled={!nuevoEvento.servicio}
              >
                <option value="">Seleccione colaborador...</option>
                {colaboradores.map((c) => (<option key={c.id} value={c.nombre}>{c.nombre}</option>))}
              </select>
            </div>

            <label>Observaciones</label>
            <textarea value={nuevoEvento.description} onChange={(e) => setNuevoEvento({ ...nuevoEvento, description: e.target.value })} />

            <div className="acciones-modal">
              <button className="btn-agregar" onClick={guardarEvento}>💾 Guardar</button>
              <button className="btn-cerrar" onClick={() => setMostrarModal(false)}>❌ Cancelar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Agenda_general;
