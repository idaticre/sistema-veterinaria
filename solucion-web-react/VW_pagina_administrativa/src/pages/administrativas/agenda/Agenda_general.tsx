import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
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
  const [editandoEvento, setEditandoEvento] = useState<Evento | null>(null);

  const [nuevoEvento, setNuevoEvento] = useState({
    summary: "",
    description: "",
    cliente: "",
    mascota: "",
    servicio: "",
    veterinario: "",
    date: "",
    startTime: "10:00",
    duracion: "",
    estado: "",
  });

  // ================== CARGA GAPI ==================
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

  // ================== CARGA GIS ==================
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

              // ⭐ Guardar token
              try {
                localStorage.setItem("google_token", tokenResponse.access_token);
              } catch (err) {
                console.warn("Error guardando token:", err);
              }

              setIsSignedIn(true);
              cargarEventos(); // cargar al iniciar sesión
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
    if (gapiInited && gisInited) setStatus("✅ Google Calendar listo para usar");
  }, [gapiInited, gisInited]);

  // ============= RESTAURAR SESIÓN AUTOMÁTICAMENTE ============
  useEffect(() => {
    if (!gapiInited || !gisInited) return;

    const savedToken = (() => {
      try {
        return localStorage.getItem("google_token");
      } catch {
        return null;
      }
    })();

    if (savedToken) {
      window.gapi.client.setToken({ access_token: savedToken });
      setIsSignedIn(true);
      setStatus("🔓 Sesión restaurada automáticamente");

      cargarEventos(); // 🔥 carga inmediata
    }
  }, [gapiInited, gisInited]);

  // ================== SESIÓN ==================
  const iniciarSesion = () => tokenClient?.requestAccessToken();

  const cerrarSesion = () => {
    const token = window.gapi.client.getToken();
    if (token) {
      window.google.accounts.oauth2.revoke(token.access_token);
      window.gapi.client.setToken(null);
    }

    try {
      localStorage.removeItem("google_token");
    } catch {}

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
    if (isSignedIn) cargarEventos();
  }, [fechaSeleccionada, isSignedIn]);

  // ================== GUARDAR EVENTO ==================
  const guardarEvento = async () => {
    if (!nuevoEvento.cliente?.trim() || !nuevoEvento.mascota?.trim() || !nuevoEvento.date)
      return alert("Completa los campos obligatorios (*)");

    const start = new Date(`${nuevoEvento.date}T${nuevoEvento.startTime}`);
    const duracionMin = parseInt(nuevoEvento.duracion || "30", 10);
    const end = new Date(start.getTime() + duracionMin * 60000);

    if (isNaN(start.getTime())) return alert("Fecha/hora inválida");
    if (end <= start) return alert("La hora de fin debe ser posterior a la de inicio.");

    const evento = {
      summary: `${nuevoEvento.servicio} - ${nuevoEvento.mascota} 🐾`,
      description: `
Cliente: ${nuevoEvento.cliente}
Mascota: ${nuevoEvento.mascota}
Servicio: ${nuevoEvento.servicio}
Veterinario: ${nuevoEvento.veterinario}
Estado: ${nuevoEvento.estado}
Observaciones: ${nuevoEvento.description || "Ninguna"}
      `,
      start: { dateTime: start.toISOString(), timeZone: "America/Lima" },
      end: { dateTime: end.toISOString(), timeZone: "America/Lima" },
    };

    try {
      await window.gapi.client.calendar.events.insert({
        calendarId: "primary",
        resource: evento,
      });

      setStatus("✅ Cita registrada correctamente");
      setMostrarModal(false);
      setEditandoEvento(null);

      cargarEventos();
    } catch (error) {
      console.error("Error al guardar cita:", error);
      setStatus("⚠️ Error al guardar la cita");
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
            {/* CALENDARIO */}
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

            {/* EVENTOS */}
            <div className="citas-container">
              <div className="citas-header">
                <h3>📋 Eventos del {fechaSeleccionada.toLocaleDateString()}</h3>

                {isSignedIn && (
                  <button
                    className="btn-agregar-linda"
                    onClick={() => {
                      setNuevoEvento({
                        summary: "",
                        description: "",
                        cliente: "",
                        mascota: "",
                        servicio: "",
                        veterinario: "",
                        date: fechaSeleccionada.toISOString().split("T")[0],
                        startTime: "10:00",
                        duracion: "",
                        estado: "",
                      });
                      setEditandoEvento(null);
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
                        const [clave, valor] = linea.split(":");
                        if (clave && valor) acc[clave.trim()] = valor.trim();
                        return acc;
                      }, {}) || {};

                    const horaInicio = new Date(e.start.dateTime).toLocaleTimeString([], {
                      hour: "2-digit",
                      minute: "2-digit",
                    });

                    const horaFin = new Date(e.end.dateTime).toLocaleTimeString([], {
                      hour: "2-digit",
                      minute: "2-digit",
                    });

                    return (
                      <div key={e.id} className="cita-card">
                        <div className="cita-info">
                          <p><strong>Cliente:</strong> {detalles.Cliente || "---"}</p>
                          <p><strong>Mascota:</strong> {detalles.Mascota || "---"}</p>
                          <p><strong>Servicio:</strong> {detalles.Servicio || "---"}</p>
                          <p><strong>Hora:</strong> {horaInicio} - {horaFin}</p>
                        </div>

                        <button
                          className="btn-mas-info"
                          onClick={() => navigate(`/administracion/agenda/EditarCita`)}
                        >
                          📄 Más información
                        </button>
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

      {/* MODAL */}
      {mostrarModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>{editandoEvento ? "Editar cita" : "Agendar nueva cita 🗓️"}</h3>

            <div className="col-izq">
              <label>Fecha *</label>
              <input
                type="date"
                value={nuevoEvento.date}
                onChange={(e) =>
                  setNuevoEvento({ ...nuevoEvento, date: e.target.value })
                }
              />

              <label>Hora *</label>
              <input
                type="time"
                value={nuevoEvento.startTime}
                onChange={(e) =>
                  setNuevoEvento({ ...nuevoEvento, startTime: e.target.value })
                }
              />

              <label>Duración (min)</label>
              <input
                type="number"
                placeholder="Ej: 30"
                value={nuevoEvento.duracion}
                onChange={(e) =>
                  setNuevoEvento({ ...nuevoEvento, duracion: e.target.value })
                }
              />

              <label>Estado *</label>
              <select
                value={nuevoEvento.estado}
                onChange={(e) =>
                  setNuevoEvento({ ...nuevoEvento, estado: e.target.value })
                }
              >
                <option value="">Seleccione...</option>
                <option value="Pendiente">Pendiente</option>
                <option value="Confirmado">Confirmado</option>
                <option value="Cancelado">Cancelado</option>
              </select>
            </div>

            <div className="col-der">
              <label>Cliente (Dueño) *</label>
              <input
                type="text"
                value={nuevoEvento.cliente}
                onChange={(e) =>
                  setNuevoEvento({ ...nuevoEvento, cliente: e.target.value })
                }
              />

              <label>Mascota *</label>
              <input
                type="text"
                value={nuevoEvento.mascota}
                onChange={(e) =>
                  setNuevoEvento({ ...nuevoEvento, mascota: e.target.value })
                }
                disabled={!nuevoEvento.cliente.trim()}
              />

              <label>Tipo de servicio *</label>
              <select
                value={nuevoEvento.servicio}
                onChange={(e) =>
                  setNuevoEvento({ ...nuevoEvento, servicio: e.target.value })
                }
                disabled={!nuevoEvento.mascota.trim()}
              >
                <option value="">Seleccione...</option>
                <option value="Baño">Baño</option>
                <option value="Consulta">Consulta</option>
                <option value="Vacunación">Vacunación</option>
                <option value="Control">Control</option>
              </select>

              <label>Veterinario</label>
              <input
                type="text"
                value={nuevoEvento.veterinario}
                onChange={(e) =>
                  setNuevoEvento({ ...nuevoEvento, veterinario: e.target.value })
                }
                disabled={!nuevoEvento.servicio.trim()}
              />
            </div>

            <label className="label-obs">Observaciones</label>
            <textarea
              className="textarea-obs"
              value={nuevoEvento.description}
              onChange={(e) =>
                setNuevoEvento({ ...nuevoEvento, description: e.target.value })
              }
            />

            <div className="acciones-modal">
              <button className="btn-agregar" onClick={guardarEvento}>
                💾 Guardar
              </button>
              <button
                className="btn-cerrar"
                onClick={() => setMostrarModal(false)}
              >
                ❌ Cancelar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Agenda_general;
