import { useState, useEffect } from "react";
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import "./EditarCita.css";

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

function EditarCita() {
  const [minimizado, setMinimizado] = useState(false);
  const [isSignedIn, setIsSignedIn] = useState(false);
  const [status, setStatus] = useState("🔌 Inicializando Google Calendar...");
  const [tokenClient, setTokenClient] = useState<any>(null);
  const [gapiInited, setGapiInited] = useState(false);
  const [gisInited, setGisInited] = useState(false);
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [eventosFiltrados, setEventosFiltrados] = useState<Evento[]>([]);
  const [mostrarModal, setMostrarModal] = useState(false);
  const [editandoEvento, setEditandoEvento] = useState<Evento | null>(null);
  const [busqueda, setBusqueda] = useState("");
  const [filtroFecha, setFiltroFecha] = useState("");

  const [nuevoEvento, setNuevoEvento] = useState({
    summary: "",
    description: "",
    cliente: "",
    mascota: "",
    servicio: "",
    veterinario: "",
    date: "",
    startTime: "10:00",
    endTime: "11:00",
    duracion: "",
    estado: "",
  });

  // ================== CARGA GAPI Y GIS ==================
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
    setEventosFiltrados([]);
    setStatus("🔒 Sesión cerrada");
  };

  // ================== CARGAR EVENTOS ==================
  const cargarEventos = async () => {
    if (!isSignedIn) return;

    const seisMesesAtras = new Date();
    seisMesesAtras.setMonth(seisMesesAtras.getMonth() - 6);
    
    const seisMesesAdelante = new Date();
    seisMesesAdelante.setMonth(seisMesesAdelante.getMonth() + 6);

    try {
      const res = await window.gapi.client.calendar.events.list({
        calendarId: "primary",
        timeMin: seisMesesAtras.toISOString(),
        timeMax: seisMesesAdelante.toISOString(),
        singleEvents: true,
        orderBy: "startTime",
        maxResults: 250,
      });

      const eventosObtenidos = (res.result.items || []).filter((evento: Evento) => {
        // Filtrar eventos de cumpleaños o sin descripción estructurada
        const tieneDescripcionEstructurada = evento.description?.includes("Cliente:") || 
                                            evento.description?.includes("Mascota:");
        return tieneDescripcionEstructurada;
      });
      
      setEventos(eventosObtenidos);
      setEventosFiltrados(eventosObtenidos);
      setStatus(`📅 ${eventosObtenidos.length} citas encontradas`);
    } catch (err) {
      console.error("Error al cargar eventos:", err);
      setStatus("⚠️ Error al cargar los eventos");
    }
  };

  useEffect(() => {
    if (isSignedIn) cargarEventos();
  }, [isSignedIn]);

  // ================== FILTROS ==================
  useEffect(() => {
    let filtrados = eventos;

    if (busqueda) {
      const busquedaLower = busqueda.toLowerCase();
      filtrados = filtrados.filter(
        (e) =>
          e.summary?.toLowerCase().includes(busquedaLower) ||
          e.description?.toLowerCase().includes(busquedaLower)
      );
    }

    if (filtroFecha) {
      filtrados = filtrados.filter((e) => {
        const fechaEvento = new Date(e.start.dateTime)
          .toISOString()
          .split("T")[0];
        return fechaEvento === filtroFecha;
      });
    }

    setEventosFiltrados(filtrados);
  }, [busqueda, filtroFecha, eventos]);

  // ================== GUARDAR EVENTO ==================
  const guardarEvento = async () => {
    if (!nuevoEvento.cliente || !nuevoEvento.mascota || !nuevoEvento.date)
      return alert("Completa los campos obligatorios (*)");

    const start = new Date(`${nuevoEvento.date}T${nuevoEvento.startTime}`);
    const duracionMin = parseInt(nuevoEvento.duracion || "30", 10);
    const end = new Date(start.getTime() + duracionMin * 60000);

    if (nuevoEvento.startTime === nuevoEvento.endTime)
      return alert("⚠️ La hora de inicio y fin no pueden ser iguales.");
    if (end <= start)
      return alert("⚠️ La hora de fin debe ser posterior a la de inicio.");

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
      if (editandoEvento) {
        await window.gapi.client.calendar.events.update({
          calendarId: "primary",
          eventId: editandoEvento.id,
          resource: evento,
        });
        setStatus("✏️ Cita actualizada correctamente");
      } else {
        await window.gapi.client.calendar.events.insert({
          calendarId: "primary",
          resource: evento,
        });
        setStatus("✅ Cita registrada correctamente");
      }

      setMostrarModal(false);
      setEditandoEvento(null);
      cargarEventos();
    } catch (error) {
      console.error("Error al guardar cita:", error);
      setStatus("⚠️ Error al guardar la cita");
    }
  };

  // ================== EDITAR Y ELIMINAR ==================
  const editarEvento = (e: Evento) => {
    const inicio = new Date(e.start.dateTime);
    const fin = new Date(e.end.dateTime);

    const detalles =
      e.description?.split("\n").reduce((acc: any, linea) => {
        const [clave, valor] = linea.split(":");
        if (clave && valor) acc[clave.trim()] = valor.trim();
        return acc;
      }, {}) || {};

    setNuevoEvento({
      summary: e.summary || "",
      description: detalles["Observaciones"] || "",
      cliente: detalles["Cliente"] || "",
      mascota: detalles["Mascota"] || "",
      servicio: detalles["Servicio"] || "",
      veterinario: detalles["Veterinario"] || "",
      date: inicio.toISOString().split("T")[0],
      startTime: inicio.toTimeString().slice(0, 5),
      endTime: fin.toTimeString().slice(0, 5),
      duracion: "",
      estado: detalles["Estado"] || "",
    });

    setEditandoEvento(e);
    setMostrarModal(true);
  };

  const eliminarEvento = async (id: string) => {
    if (confirm("¿Eliminar esta cita?")) {
      await window.gapi.client.calendar.events.delete({
        calendarId: "primary",
        eventId: id,
      });
      cargarEventos();
      setStatus("🗑️ Cita eliminada");
    }
  };

  // ================== RENDER ==================
  return (
    <div id="editarita">
      <Br_administrativa onMinimizeChange={setMinimizado} />

      <main className={minimizado ? "minimize" : ""}>
        <section className="editarita-container">
          <h2 className="titulo-editarita">📝 Editor de Citas Agendadas</h2>

          {/* AUTENTICACIÓN */}
          {!isSignedIn ? (
            <div className="auth-section">
              <p className="status">{status}</p>
              <button className="btn-primary" onClick={iniciarSesion}>
                🔐 Iniciar sesión con Google
              </button>
            </div>
          ) : (
            <>
              {/* BARRA DE FILTROS */}
              <div className="filtros-section">
                <div className="filtro-busqueda">
                  <input
                    type="text"
                    placeholder="🔍 Buscar por cliente, mascota o servicio..."
                    value={busqueda}
                    onChange={(e) => setBusqueda(e.target.value)}
                  />
                </div>

                <div className="filtro-fecha">
                  <input
                    type="date"
                    value={filtroFecha}
                    onChange={(e) => setFiltroFecha(e.target.value)}
                  />
                  {filtroFecha && (
                    <button
                      className="btn-limpiar"
                      onClick={() => setFiltroFecha("")}
                    >
                      ✖️
                    </button>
                  )}
                </div>

                <button className="btn-recargar" onClick={cargarEventos}>
                  🔄 Recargar
                </button>

                <button className="btn-cerrar-sesion" onClick={cerrarSesion}>
                  🚪 Cerrar sesión
                </button>
              </div>

              {/* STATUS */}
              <p className="status-info">{status}</p>

              {/* LISTA DE CITAS */}
              {eventosFiltrados.length === 0 ? (
                <div className="no-eventos">
                  {eventos.length === 0
                    ? "No hay citas agendadas en los próximos 3 meses"
                    : "No se encontraron citas con esos filtros"}
                </div>
              ) : (
                <div className="citas-grid">
                  {eventosFiltrados.map((e) => {
                    const detalles = e.description
                      ?.split("\n")
                      .reduce((acc: any, linea) => {
                        const [clave, valor] = linea.split(":");
                        if (clave && valor) acc[clave.trim()] = valor.trim();
                        return acc;
                      }, {});

                    const fechaInicio = new Date(e.start.dateTime);
                    const fechaFin = new Date(e.end.dateTime);

                    return (
                      <div key={e.id} className="cita-card-edit">
                        <div className="cita-header">
                          <h3>{e.summary}</h3>
                          <span className={`estado estado-${detalles?.Estado?.toLowerCase()}`}>
                            {detalles?.Estado || "N/A"}
                          </span>
                        </div>

                        <div className="cita-info">
                          <p>
                            <strong>👤 Cliente:</strong> {detalles?.Cliente || "N/A"}
                          </p>
                          <p>
                            <strong>🐾 Mascota:</strong> {detalles?.Mascota || "N/A"}
                          </p>
                          <p>
                            <strong>💉 Servicio:</strong> {detalles?.Servicio || "N/A"}
                          </p>
                          <p>
                            <strong>👨‍⚕️ Veterinario:</strong>{" "}
                            {detalles?.Veterinario || "N/A"}
                          </p>
                          <p>
                            <strong>📅 Fecha:</strong>{" "}
                            {fechaInicio.toLocaleDateString("es-ES")}
                          </p>
                          <p>
                            <strong>🕐 Horario:</strong>{" "}
                            {fechaInicio.toLocaleTimeString("es-ES", {
                              hour: "2-digit",
                              minute: "2-digit",
                            })}{" "}
                            -{" "}
                            {fechaFin.toLocaleTimeString("es-ES", {
                              hour: "2-digit",
                              minute: "2-digit",
                            })}
                          </p>
                        </div>

                        <div className="cita-acciones">
                          <button
                            className="btn-editar"
                            onClick={() => editarEvento(e)}
                          >
                            ✏️ Editar
                          </button>
                          <button
                            className="btn-eliminar"
                            onClick={() => eliminarEvento(e.id!)}
                          >
                            🗑️ Eliminar
                          </button>
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </>
          )}
        </section>
      </main>

      {/* MODAL DE EDICIÓN */}
      {mostrarModal && (
        <div className="modal-overlay">
          <div className="modal-content-edit">
            <h3>{editandoEvento ? "✏️ Editar cita" : "➕ Nueva cita"}</h3>

            <div className="form-grid">
              <div className="form-group">
                <label>Cliente (Dueño) *</label>
                <input
                  type="text"
                  value={nuevoEvento.cliente}
                  onChange={(e) =>
                    setNuevoEvento({ ...nuevoEvento, cliente: e.target.value })
                  }
                />
              </div>

              <div className="form-group">
                <label>Mascota *</label>
                <input
                  type="text"
                  value={nuevoEvento.mascota}
                  onChange={(e) =>
                    setNuevoEvento({ ...nuevoEvento, mascota: e.target.value })
                  }
                  disabled={!nuevoEvento.cliente.trim()}
                />
              </div>

              <div className="form-group">
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
              </div>

              <div className="form-group">
                <label>Veterinario</label>
                <input
                  type="text"
                  value={nuevoEvento.veterinario}
                  onChange={(e) =>
                    setNuevoEvento({ ...nuevoEvento, veterinario: e.target.value })
                  }
                  disabled={!nuevoEvento.mascota.trim()}
                />
              </div>

              <div className="form-group">
                <label>Fecha *</label>
                <input
                  type="date"
                  value={nuevoEvento.date}
                  onChange={(e) =>
                    setNuevoEvento({ ...nuevoEvento, date: e.target.value })
                  }
                  disabled={!nuevoEvento.mascota.trim()}
                />
              </div>

              <div className="form-group">
                <label>Hora *</label>
                <input
                  type="time"
                  value={nuevoEvento.startTime}
                  onChange={(e) =>
                    setNuevoEvento({ ...nuevoEvento, startTime: e.target.value })
                  }
                  disabled={!nuevoEvento.mascota.trim()}
                />
              </div>

              <div className="form-group">
                <label>Duración (min)</label>
                <input
                  type="number"
                  placeholder="Ej: 30"
                  value={nuevoEvento.duracion}
                  onChange={(e) =>
                    setNuevoEvento({ ...nuevoEvento, duracion: e.target.value })
                  }
                  disabled={!nuevoEvento.mascota.trim()}
                />
              </div>

              <div className="form-group">
                <label>Estado *</label>
                <select
                  value={nuevoEvento.estado}
                  onChange={(e) =>
                    setNuevoEvento({ ...nuevoEvento, estado: e.target.value })
                  }
                  disabled={!nuevoEvento.mascota.trim()}
                >
                  <option value="">Seleccione...</option>
                  <option value="Pendiente">Pendiente</option>
                  <option value="Confirmado">Confirmado</option>
                  <option value="Cancelado">Cancelado</option>
                </select>
              </div>
            </div>

            <div className="form-group-full">
              <label>Observaciones</label>
              <textarea
                value={nuevoEvento.description}
                onChange={(e) =>
                  setNuevoEvento({ ...nuevoEvento, description: e.target.value })
                }
                disabled={!nuevoEvento.mascota.trim()}
              />
            </div>

            <div className="modal-acciones">
              <button className="btn-guardar" onClick={guardarEvento}>
                💾 Guardar
              </button>
              <button
                className="btn-cancelar"
                onClick={() => {
                  setMostrarModal(false);
                  setEditandoEvento(null);
                }}
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

export default EditarCita;