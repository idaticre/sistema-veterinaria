import { useState, useEffect } from "react";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import Br_administrativa from "../../../components/barra_administrativa/Br_administrativa";
import "./Agenda_genera.css";

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
  const [minimizado, setMinimizado] = useState(false);
  const [fechaSeleccionada, setFechaSeleccionada] = useState<Date>(new Date());
  const [isSignedIn, setIsSignedIn] = useState(false);
  const [status, setStatus] = useState("🔌 Inicializando Google Calendar...");
  const [tokenClient, setTokenClient] = useState<any>(null);
  const [gapiInited, setGapiInited] = useState(false);
  const [gisInited, setGisInited] = useState(false);
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [mostrarModal, setMostrarModal] = useState(false);
  const [editandoEvento, setEditandoEvento] = useState<Evento | null>(null);
  const [nuevoEvento, setNuevoEvento] = useState({
    summary: "",
    description: "",
    date: "",
    startTime: "10:00",
    endTime: "11:00",
  });

  // ================== CARGA DE GAPI Y GIS ==================
  useEffect(() => {
    const cargarGapi = async () => {
      const script = document.createElement("script");
      script.src = "https://apis.google.com/js/api.js";
      script.onload = async () => {
        await new Promise((resolve) =>
          window.gapi.load("client", { callback: resolve })
        );
        await window.gapi.client.init({
          apiKey: API_KEY,
          discoveryDocs: [
            "https://www.googleapis.com/discovery/v1/apis/calendar/v3/rest",
          ],
        });
        setGapiInited(true);
      };
      document.body.appendChild(script);
    };
    cargarGapi();
  }, []);

  useEffect(() => {
    const cargarGis = () => {
      const script = document.createElement("script");
      script.src = "https://accounts.google.com/gsi/client";
      script.onload = () => {
        const client = window.google.accounts.oauth2.initTokenClient({
          client_id: CLIENT_ID,
          scope:
            "https://www.googleapis.com/auth/calendar.events https://www.googleapis.com/auth/calendar.readonly",
          callback: (tokenResponse: any) => {
            if (tokenResponse.access_token) {
              window.gapi.client.setToken({
                access_token: tokenResponse.access_token,
              });
              setIsSignedIn(true);
            }
          },
        });
        setTokenClient(client);
        setGisInited(true);
      };
      document.body.appendChild(script);
    };
    cargarGis();
  }, []);

  useEffect(() => {
    if (gapiInited && gisInited)
      setStatus("✅ Google Calendar listo para usar");
  }, [gapiInited, gisInited]);

  // ================== SESIÓN ==================
  const iniciarSesion = () => tokenClient?.requestAccessToken();

  const cerrarSesion = () => {
    const token = window.gapi.client.getToken();
    if (token) {
      window.google.accounts.oauth2.revoke(token.access_token);
      window.gapi.client.setToken(null);
    }
    setIsSignedIn(false);
    setEventos([]);
    setStatus("🔒 Sesión cerrada");
  };

  // ================== CARGAR EVENTOS ==================
  const cargarEventos = async () => {
    if (!isSignedIn) return;

    const inicio = new Date(fechaSeleccionada);
    inicio.setHours(0, 0, 0, 0);
    const fin = new Date(fechaSeleccionada);
    fin.setHours(23, 59, 59, 999);

    try {
      const res = await window.gapi.client.calendar.events.list({
        calendarId: "primary",
        timeMin: inicio.toISOString(),
        timeMax: fin.toISOString(),
        singleEvents: true,
        orderBy: "startTime",
      });

      setEventos(res.result.items || []);
      setStatus(`📅 Eventos cargados para ${fechaSeleccionada.toLocaleDateString()}`);
    } catch (err) {
      console.error("Error al cargar eventos:", err);
      setStatus("⚠️ Error al cargar los eventos");
    }
  };

  useEffect(() => {
    if (isSignedIn) {
      cargarEventos();
    }
  }, [fechaSeleccionada, isSignedIn]);

  // ================== CRUD ==================
  const guardarEvento = async () => {
    if (!nuevoEvento.summary || !nuevoEvento.date)
      return alert("Completa los campos obligatorios.");

    const start = new Date(`${nuevoEvento.date}T${nuevoEvento.startTime}`);
    const end = new Date(`${nuevoEvento.date}T${nuevoEvento.endTime}`);
    const evento = {
      summary: nuevoEvento.summary,
      description: nuevoEvento.description,
      start: { dateTime: start.toISOString(), timeZone: "America/Lima" },
      end: { dateTime: end.toISOString(), timeZone: "America/Lima" },
    };

    try {
      if (editandoEvento) {
        await window.gapi.client.calendar.events.update({
          calendarId: "primary",
          eventId: editandoEvento.id,
          resource: evento,
        });
        setStatus("✏️ Evento editado correctamente");
      } else {
        await window.gapi.client.calendar.events.insert({
          calendarId: "primary",
          resource: evento,
        });
        setStatus("✅ Evento creado correctamente");
      }
      setMostrarModal(false);
      setEditandoEvento(null);
      setNuevoEvento({
        summary: "",
        description: "",
        date: "",
        startTime: "10:00",
        endTime: "11:00",
      });
      cargarEventos();
    } catch (error) {
      console.error("Error al guardar evento:", error);
      setStatus("⚠️ Error al guardar el evento");
    }
  };

  const editarEvento = (e: Evento) => {
    const inicio = new Date(e.start.dateTime);
    const fin = new Date(e.end.dateTime);
    setNuevoEvento({
      summary: e.summary,
      description: e.description || "",
      date: inicio.toISOString().split("T")[0],
      startTime: inicio.toTimeString().slice(0, 5),
      endTime: fin.toTimeString().slice(0, 5),
    });
    setEditandoEvento(e);
    setMostrarModal(true);
  };

  const eliminarEvento = async (id: string) => {
    if (confirm("¿Eliminar este evento?")) {
      await window.gapi.client.calendar.events.delete({
        calendarId: "primary",
        eventId: id,
      });
      cargarEventos();
      setStatus("🗑️ Evento eliminado");
    }
  };

  // ================== RENDER ==================
  return (
    <div id="agenda">
      <Br_administrativa onMinimizeChange={setMinimizado} />

      <main className={minimizado ? "minimize" : ""}>
        <section className="agenda-container">
          <h2 className="titulo-agenda">Agenda de Citas</h2>

          <div className="agenda-layout">
            {/* 📅 CALENDARIO */}
            <div className="calendar-container">
              <Calendar
                onChange={(date) => setFechaSeleccionada(date as Date)}
                value={fechaSeleccionada}
                locale="es-ES"
              />
              <div className="auth-buttons">
                {!isSignedIn ? (
                  <button className="btn-agregar" onClick={iniciarSesion}>
                    🔐 Iniciar sesión
                  </button>
                ) : (
                  <button className="btn-cerrar" onClick={cerrarSesion}>
                    🚪 Cerrar sesión
                  </button>
                )}
              </div>
            </div>

            {/* 📋 EVENTOS */}
            <div className="citas-container">
              <div className="citas-header">
                <h3>📋 Eventos del {fechaSeleccionada.toLocaleDateString()}</h3>

                {isSignedIn && (
                  <button
                    className="btn-agregar"
                    onClick={() => {
                      const fechaISO = fechaSeleccionada.toISOString().split("T")[0];
                      setNuevoEvento({
                        summary: "",
                        description: "",
                        date: fechaISO,
                        startTime: "10:00",
                        endTime: "11:00",
                      });
                      setMostrarModal(true);
                    }}
                  >
                    ➕ Nuevo evento
                  </button>
                )}
              </div>

              <div className="linea-divisoria"></div>

              {eventos.length === 0 ? (
                <p>No hay eventos para este día.</p>
              ) : (
                <div className="citas-lista">
                  {eventos.map((e) => (
                    <div key={e.id} className="cita-card">
                      <strong>{e.summary}</strong>
                      {e.description && <p>{e.description}</p>}
                      <small>
                        {new Date(e.start.dateTime).toLocaleTimeString([], {
                          hour: "2-digit",
                          minute: "2-digit",
                        })}{" "}
                        -{" "}
                        {new Date(e.end.dateTime).toLocaleTimeString([], {
                          hour: "2-digit",
                          minute: "2-digit",
                        })}
                      </small>
                      <div className="acciones">
                        <button onClick={() => editarEvento(e)}>✏️</button>
                        <button onClick={() => eliminarEvento(e.id!)}>🗑️</button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
              <p style={{ marginTop: "10px", color: "#555" }}>{status}</p>
            </div>
          </div>
        </section>
      </main>

      {/* MODAL CREAR / EDITAR */}
      {mostrarModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>{editandoEvento ? "Editar evento" : "Crear nuevo evento"}</h3>

            <input
              type="text"
              placeholder="Título del evento"
              value={nuevoEvento.summary}
              onChange={(e) =>
                setNuevoEvento({ ...nuevoEvento, summary: e.target.value })
              }
            />
            <textarea
              placeholder="Descripción"
              value={nuevoEvento.description}
              onChange={(e) =>
                setNuevoEvento({ ...nuevoEvento, description: e.target.value })
              }
            />
            <input
              type="date"
              value={nuevoEvento.date}
              onChange={(e) =>
                setNuevoEvento({ ...nuevoEvento, date: e.target.value })
              }
            />
            <input
              type="time"
              value={nuevoEvento.startTime}
              onChange={(e) =>
                setNuevoEvento({ ...nuevoEvento, startTime: e.target.value })
              }
            />
            <input
              type="time"
              value={nuevoEvento.endTime}
              onChange={(e) =>
                setNuevoEvento({ ...nuevoEvento, endTime: e.target.value })
              }
            />

            <div className="acciones-modal">
              <button onClick={guardarEvento}>Guardar</button>
              <button onClick={() => setMostrarModal(false)}>Cancelar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Agenda_general;
