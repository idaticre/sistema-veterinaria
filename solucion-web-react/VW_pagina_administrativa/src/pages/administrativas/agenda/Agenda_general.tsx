import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import Br_administrativa from "../../../components/barra_administrativa/Br_administrativa";
import "./Agenda_general.css";
import IST from "../../../components/proteccion/IST";
import axios from "axios"; 

const CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;
const API_KEY = import.meta.env.VITE_GOOGLE_API_KEY;
declare global {
  interface Window {
    google: any;
    gapi: any;
  }
}

// --- INTERFACES (se mantienen) ---
interface Evento {
  id?: string;
  summary: string;
  description?: string;
  start: { dateTime: string; timeZone: string };
  end: { dateTime: string; timeZone: string };
  htmlLink?: string;
}

interface ServicioBase {
  id: number;
  nombre: string;
  duracion: number;
  precio: number;
}

interface EstadoAgenda {
  id: number;
  nombre: string;
}

interface ServicioDetalle {
  id_servicio: number;
  nombre_servicio: string;
  id_veterinario: number;
  nombre_veterinario: string;
  cantidad: number;
  valor_servicio: number;
  bono_inicial: number;
  duracion_min: number;
  duracion_total: number;
  subtotal: number;
  adicionales: string;
}
// ------------------

const ID_USUARIO_DEFAULT = 1;
const ID_MEDIO_PAGO_DEFAULT = 1;

// --- FUNCIÓN AUXILIAR: Extraer detalles de texto de Google Calendar (ACTUALIZADA con S/) ---
const extraerDetallesGC = (summary: string, description?: string) => {
    let cliente = 'N/A';
    let mascota = 'N/A';
    let costoTotal = 'N/A';

    // Regex modificado para aceptar '$' o 'S/'
    const summaryMatch = summary.match(/(.*?) - Total: [\$S\/](\d+\.?\d*)/);
    if (summaryMatch) {
        mascota = summaryMatch[1].trim();
        // Formato de salida con S/
        costoTotal = `S/${parseFloat(summaryMatch[2]).toFixed(2)}`;
    } else {
        mascota = summary.split(' - ')[0].trim();
    }

    if (description) {
        const clienteMatch = description.match(/Cliente:\s*(.*?)(\s*\(DNI:.*?\))?\s*\n/i);
        if (clienteMatch) {
            cliente = clienteMatch[1].trim();
        }

        if (costoTotal === 'N/A') {
            // Regex modificado para aceptar '$' o 'S/'
            const costoMatch = description.match(/Costo Total:\s*[\$S\/](\d+\.?\d*)/i);
            if (costoMatch) {
                // Formato de salida con S/
                costoTotal = `S/${parseFloat(costoMatch[1]).toFixed(2)}`;
            }
        }
    }

    return { Cliente: cliente, Mascota: mascota, "Costo Total": costoTotal };
};


function Agenda_general() {
  const navigate = useNavigate();
  const [minimizado, setMinimizado] = useState(false);
  const [fechaSeleccionada, setFechaSeleccionada] = useState<Date>(new Date());

  // Estados de Google Calendar
  const [isSignedIn, setIsSignedIn] = useState(false);
  const [status, setStatus] = useState("🔌 Inicializando Google Calendar...");
  const [tokenClient, setTokenClient] = useState<any>(null);
  const [gapiInited, setGapiInited] = useState(false);
  const [gisInited, setGisInited] = useState(false);
  const [eventos, setEventos] = useState<Evento[]>([]);

  // Estado para citas de la Base de Datos (BD) - SE MANTIENE EL ESTADO PARA EL FLUJO DE GUARDADO
  const [citasDB, setCitasDB] = useState<any[]>([]);

  const [mostrarModal, setMostrarModal] = useState(false);

  // Datos Maestros
  const [clientes, setClientes] = useState<any[]>([]);
  const [mascotas, setMascotas] = useState<any[]>([]);
  const [colaboradores, setColaboradores] = useState<any[]>([]);
  const [serviciosDisponibles, setServiciosDisponibles] = useState<ServicioBase[]>([]);
  const [estadosAgenda, setEstadosAgenda] = useState<EstadoAgenda[]>([]);

  const [nuevoEvento, setNuevoEvento] = useState({
    id: '',
    summary: "",
    description: "",
    dni: "",
    cliente: "",
    clienteId: 0,
    mascota: "",
    servicio: "",
    colaborador: "",
    date: new Date().toISOString().split("T")[0],
    startTime: "10:00", // Viene de input type="time" (HH:mm)
    duracion: "30",
    estado: "PENDIENTE",
  });

  // --- ESTADOS PARA GESTIÓN DE SERVICIOS ---
  const [serviciosRegistrados, setServiciosRegistrados] = useState<ServicioDetalle[]>([]);
  const [servicioTemporal, setServicioTemporal] = useState({
    id_servicio: '',
    valor_servicio: 0,
    cantidad: 1,
    duracion_min: 0,
    id_veterinario: '',
    adicionales: '',
  });
  const [bonoTemporal, setBonoTemporal] = useState(0);

  // CÁLCULO DE TOTALES
  const totalDuracion = serviciosRegistrados.reduce((sum, s) => sum + s.duracion_total, 0);
  const totalCosto = serviciosRegistrados.reduce((sum, s) => sum + s.subtotal, 0);


  // --- CARGA DE DATOS INICIALES ---
  useEffect(() => {

    const listarServicios = async () => {
      try {
        const respuesta = await IST.get(`/servicios`);
        const lista = Array.isArray(respuesta.data) ? respuesta.data : respuesta.data.data
        if (Array.isArray(lista) && lista.length > 0) {
          const serviciosParseados = lista.map((s: any) => ({
            ...s,
            duracion: parseInt(s.duracion) || 0,
            precio: parseFloat(s.precio) || 0,
          }));
          setServiciosDisponibles(serviciosParseados);
        }
      } catch (error) { /* console.error("Error al obtener los servicios", error); */ }
    }

    const listarEstados = async () => {
      try {
        const res = await IST.get("/estados-agenda");
        const estados = res.data.map((e: any) => ({ id: e.id, nombre: e.nombre.toUpperCase() }));
        setEstadosAgenda(estados);

        const estadoPendiente = estados.find((e: any) => e.nombre === 'PENDIENTE');
        if (estadoPendiente) {
          setNuevoEvento(prev => ({ ...prev, estado: estadoPendiente.nombre }));
        }
      } catch (error) {
        /* console.error("Error al obtener los estados de la agenda:", error); */
      }
    }

    listarServicios();
    listarEstados();

    IST.get("/clientes").then((r) => setClientes(r.data.data.filter((c: any) => c.activo))).catch(() => setClientes([]));
    IST.get("/colaboradores").then((r) => setColaboradores(r.data.data.filter((c: any) => c.activo))).catch(() => setColaboradores([]));
    IST.get("/mascotas").then((res) => setMascotas(res.data.data)).catch(() => setMascotas([]));
  }, []);

  // --- EFECTO: Sincronizar Servicio Temporal ---
  useEffect(() => {
    const serviceId = parseInt(servicioTemporal.id_servicio as string);
    if (!serviceId || isNaN(serviceId)) {
      setServicioTemporal(prev => ({ ...prev, valor_servicio: 0, duracion_min: 0 }));
      setBonoTemporal(0);
      return;
    }
    const s = serviciosDisponibles.find(s => s.id === serviceId);
    if (s) {
      setServicioTemporal(prev => ({
        ...prev,
        valor_servicio: s.precio,
        duracion_min: s.duracion,
      }));
      setBonoTemporal(0);
    }
  }, [servicioTemporal.id_servicio, serviciosDisponibles]);

  // ================== CÓDIGO DE GOOGLE CALENDAR (GAPI/GIS) ==================

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
    try {
      const res = await window.gapi.client.calendar.events.list({
        calendarId: "primary",
        timeMin: inicio.toISOString(),
        timeMax: fin.toISOString(),
        singleEvents: true,
        orderBy: "startTime",
      });
      setEventos(res.result.items || []);
    } catch (error) {
      /* console.error("Error al cargar eventos:", error); */
      setStatus("❌ Error al cargar eventos. Intente reconectar.");
    }
  };

  // La función cargarCitasBD se mantiene para el caso de uso futuro o si el flujo de edición lo necesita,
  // pero el resultado (citasDB) ya no se usa para renderizar en el JSX principal.
  const cargarCitasBD = async (fecha: Date) => {
    const dateStr = fecha.toISOString().split('T')[0];
    try {
      const res = await IST.get(`/agenda?fecha=${dateStr}`);
      const listaCitas = res.data.data.content || [];
      setCitasDB(listaCitas);
    } catch (error) {
      /* console.error("Error al cargar citas desde BD:", error); */
      setCitasDB([]);
    }
  };

  useEffect(() => {
    if (!mostrarModal) return;
    const handleClickOutside = (e: MouseEvent) => {
      const overlay = document.querySelector(".modal-overlay");
      const content = document.querySelector(".modal-content");
      if (overlay && content && e.target === overlay) {
        setServiciosRegistrados([]);
        setMostrarModal(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [mostrarModal]);

  // Recarga Citas y Eventos cuando cambia la fecha o la autenticación
  useEffect(() => {
    if (isSignedIn) cargarEventos();
    // Comentar o eliminar la carga de citas de BD si no es necesario para otra lógica
    // cargarCitasBD(fechaSeleccionada);
  }, [fechaSeleccionada, isSignedIn]);

  const horaOcupada = (start: Date, end: Date, currentEventId?: string) => {
    return eventos.some((e) => {
      if (e.id === currentEventId) return false;
      const eStart = new Date(e.start.dateTime);
      const eEnd = new Date(e.end.dateTime);
      return (start < eEnd && end > eStart);
    });
  };

  // --- FUNCIONES DE GESTIÓN DE SERVICIOS ---
  const agregarServicio = () => {
    const sId = parseInt(servicioTemporal.id_servicio as string);
    const vId = parseInt(servicioTemporal.id_veterinario as string);
    const servicioInfo = serviciosDisponibles.find(s => s.id === sId);
    const veterinarioInfo = colaboradores.find(v => v.id === vId);

    if (!servicioInfo || !veterinarioInfo || servicioTemporal.valor_servicio <= 0 || servicioTemporal.cantidad <= 0 || servicioTemporal.duracion_min <= 0) {
      return alert("⚠️ Por favor, selecciona un servicio y veterinario, y verifica que Valor, Cantidad y Duración sean mayores a 0.");
    }

    const cantidad = servicioTemporal.cantidad;
    const valorUnitario = servicioTemporal.valor_servicio;
    const duracionUnitaria = servicioTemporal.duracion_min;
    const subtotalCalculado = valorUnitario * cantidad;

    const nuevoServicio: ServicioDetalle = {
      id_servicio: sId,
      nombre_servicio: servicioInfo.nombre,
      id_veterinario: vId,
      nombre_veterinario: veterinarioInfo.nombre,
      cantidad: cantidad,
      valor_servicio: valorUnitario,
      bono_inicial: 0,
      duracion_min: duracionUnitaria,
      duracion_total: duracionUnitaria * cantidad,
      subtotal: subtotalCalculado,
      adicionales: servicioTemporal.adicionales,
    };

    setServiciosRegistrados(prev => [...prev, nuevoServicio]);

    setServicioTemporal({
      id_servicio: '',
      valor_servicio: 0,
      cantidad: 1,
      duracion_min: 0,
      id_veterinario: servicioTemporal.id_veterinario,
      adicionales: '',
    });
  };

  const eliminarServicio = (index: number) => {
    setServiciosRegistrados(prev => prev.filter((_, i) => i !== index));
  };

  // --- FUNCIÓN PRINCIPAL DE GUARDADO (ACTUALIZADA con S/ y Recordatorios) ---
  const guardarEvento = async () => {
    if (!nuevoEvento.cliente || !nuevoEvento.mascota || !nuevoEvento.dni)
      return alert("Completa los campos de Cliente, DNI y Mascota.");

    if (serviciosRegistrados.length === 0) {
      return alert("Debe registrar al menos un servicio para la cita.");
    }

    let horaDBFormateada = nuevoEvento.startTime;
    if (horaDBFormateada && horaDBFormateada.length === 5) {
        horaDBFormateada = horaDBFormateada + ":00";
    } 

    const duracionCitaTotal = totalDuracion;
    const start = new Date(`${nuevoEvento.date}T${nuevoEvento.startTime}`);
    const end = new Date(start.getTime() + duracionCitaTotal * 60000);

    if (horaOcupada(start, end, nuevoEvento.id)) {
      return alert("⚠️ Ya existe una cita en este horario en Google Calendar. Elige otro horario.");
    }

    // 1. PREPARAR DATOS PARA EL BACKEND (BD)
    const mascotaEncontrada = mascotas.find(m => m.nombre === nuevoEvento.mascota && m.idCliente === nuevoEvento.clienteId);
    const idMascota = mascotaEncontrada ? mascotaEncontrada.id : null;

    const estadoEncontrado = estadosAgenda.find(e => e.nombre === nuevoEvento.estado);
    const idEstado = estadoEncontrado ? estadoEncontrado.id : 1;

    if (!idMascota) {
      return alert("Error: No se pudo encontrar el ID de la mascota. Revisa la carga inicial de datos.");
    }

    const AgendaRequestDTO = {
      idCliente: nuevoEvento.clienteId,
      idMascota: idMascota,
      idMedioSolicitud: 4,
      fecha: nuevoEvento.date,
      hora: horaDBFormateada,
      duracionEstimadaMin: duracionCitaTotal,
      abonoInicial: bonoTemporal,
      totalCita: totalCosto,
      idEstado: idEstado,
      observaciones: nuevoEvento.description,

      servicios: serviciosRegistrados.map(s => ({
        idServicio: s.id_servicio,
        idColaborador: s.id_veterinario,
        idVeterinario: s.id_veterinario,
        cantidad: s.cantidad,
        valorServicio: s.valor_servicio,
        duracionMin: s.duracion_min,
        observaciones: s.adicionales,
      })),
    };

    // 2. PREPARAR DATOS PARA GOOGLE CALENDAR (GC)
    // Se usa S/ en lugar de $
    const serviciosListaGC = serviciosRegistrados.map(s =>
        `• ${s.nombre_servicio} (${s.cantidad}x S/${s.valor_servicio.toFixed(2)})  Subtotal: S/${s.subtotal.toFixed(2)} con ${s.nombre_veterinario}. Adicionales: ${s.adicionales || 'N/A'}`
    ).join('\n');

    const eventoResource = {
        // Se usa S/ en lugar de $
        summary: `${nuevoEvento.mascota} - Total: S/${totalCosto.toFixed(2)}`,
        // Se usa S/ en lugar de $
        description: `**CLIENTE Y MASCOTA**\nCliente: ${nuevoEvento.cliente} (DNI: ${nuevoEvento.dni})\nMascota: ${nuevoEvento.mascota}\nEstado: ${nuevoEvento.estado}\nCosto Total: S/${totalCosto.toFixed(2)}\nDuración Total: ${duracionCitaTotal} min\n\n**SERVICIOS REGISTRADOS**\n${serviciosListaGC}\n\n**ADELANTO:** S/${bonoTemporal.toFixed(2)}\n\n**OBSERVACIONES**\n${nuevoEvento.description || 'No hay observaciones adicionales.'}`.trim(),
        start: { dateTime: start.toISOString(), timeZone: "America/Lima" },
        end: { dateTime: end.toISOString(), timeZone: "America/Lima" },
        // Implementación de Recordatorios
        reminders: {
            useDefault: false, 
            overrides: [
                { method: 'email', minutes: 43200 }, // 1 mes antes
                { method: 'popup', minutes: 30 }      // 30 minutos antes
            ]
        }
    };


    try {
      // *** PASO 1: INTENTAR INSERTAR EN LA BASE DE DATOS (Requiere JWT) ***
      const responseDB = await IST.post("/agenda/crear", AgendaRequestDTO);
      const citaCreada = responseDB.data.data;

      if (!responseDB.data.success) {
        // Falló la lógica de negocio del backend
        return alert(`Error BD: ${responseDB.data.message}`);
      }

      // *** PASO 2: INSERTAR EN GOOGLE CALENDAR (Solo si la BD fue exitosa) ***
      if (isSignedIn) {
          await window.gapi.client.calendar.events.insert({ calendarId: "primary", resource: eventoResource });
      }

      // *** PASO 3: REGISTRAR ABONO/PAGO si aplica ***
      if (bonoTemporal > 0) {
        const pagoRequestDTO = {
          idAgenda: citaCreada.id,
          idMedioPago: ID_MEDIO_PAGO_DEFAULT,
          idUsuario: ID_USUARIO_DEFAULT,
          monto: bonoTemporal,
          observaciones: "Adelanto registrado durante la creación de la cita."
        };
        await IST.post("/pagos-agenda/crear", pagoRequestDTO);
      }

      alert(`Cita Registrada Existosamenete`);

    } catch (error: any) {
        let errorMessage = "Ocurrió un error al guardar la cita. ";

        // MANEJO CRÍTICO DEL 401: Detiene y notifica la expiración de la sesión
        if (axios.isAxiosError(error) && error.response && error.response.status === 401) {
            errorMessage = "🚫 Error: 401 Unauthorized. Su sesión ha expirado o no tiene permisos. **NO se guardó la cita en la BD ni en Calendar.** Por favor, inicie sesión de nuevo.";
        } else if (error.message) {
             errorMessage += `Detalle: ${error.message}`;
        }
        
        alert(errorMessage);
        
        return; 
    }

    // --- CÓDIGO DE ÉXITO (Solo si el try completó todo) ---
    setMostrarModal(false);
    setServiciosRegistrados([]);
    setBonoTemporal(0);
    setNuevoEvento(prev => ({
      ...prev,
      id: '',
      summary: "",
      description: "",
      dni: "",
      cliente: "",
      clienteId: 0,
      mascota: "",
      servicio: "",
      estado: estadosAgenda.find(e => e.id === 1)?.nombre || "PENDIENTE",
      date: fechaSeleccionada.toISOString().split("T")[0],
      startTime: "10:00",
    }));

    // Recarga la lista de eventos de Google Calendar para mostrar el nuevo registro
    cargarEventos();
    // Se mantiene, aunque ya no se usa para renderizar: cargarCitasBD(fechaSeleccionada);
  };
  // ---------------------------------------------


  // --- JSX DEL COMPONENTE ---
  return (
    <div id="agenda">
      <Br_administrativa onMinimizeChange={setMinimizado} />
      <main className={minimizado ? "minimize" : ""}>
        <section className="agenda-container">
          <h2 className="titulo-agenda">Agenda de Citas</h2>
          <div className="agenda-layout">
            {/* Calendar y Auth Buttons */}
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
                  <button className="btn-cerrar" onClick={cerrarSesion}>🚪Cerrar sesión</button>
                )}
            </div>
              <p style={{ marginTop: "10px", color: "#555" }}>{status}</p>
            </div>

            {/* Citas Container (UNIFICADO) */}
            <div className="citas-container">
              <div className="citas-header">
                <h3>📋 Citas Registradas del {fechaSeleccionada.toLocaleDateString()}</h3>
                <button
                  className="btn-agregar-linda"
                  onClick={() => {
                    setNuevoEvento((prev) => ({
                      ...prev,
                      id: '',
                      date: fechaSeleccionada.toISOString().split("T")[0],
                      startTime: "10:00",
                      estado: estadosAgenda.find(e => e.nombre === 'PENDIENTE')?.nombre || 'PENDIENTE',
                    }));
                    setServiciosRegistrados([]);
                    setBonoTemporal(0);
                    setMostrarModal(true);
                  }}
                >
                  ✨➕ Nueva cita
                </button>
              </div>
              <div className="linea-divisoria"></div>

              <div className="citas-lista">

                

                {/* 2. EVENTOS DE GOOGLE CALENDAR (GC) - SIN CAMBIOS */}
                {eventos.map((e) => {
                  const inicio = new Date(e.start.dateTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
                  const fin = new Date(e.end.dateTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
                  const dia = new Date(e.start.dateTime).toLocaleDateString();

                  // Extraer los detalles del summary/description
                  const detalles = extraerDetallesGC(e.summary, e.description);

                  return (
                    <div key={`gc-${e.id}`} className="cita-card gc-cita">
                      <p><strong>Cliente:</strong> {detalles.Cliente || 'N/A'}</p>
                      <p><strong>Mascota:</strong> {detalles.Mascota || 'N/A'}</p>
                      <p><strong>Hora:</strong> {inicio} - {fin}</p>
                      <p><strong>Día:</strong> {dia}</p>
                      <button className="btn-mas-info" onClick={() => navigate(`/administracion/agenda/EditarCita`)}>📄 Más información</button>
                    </div>
                  );
                })}

                {/* Mensaje si no hay ninguna cita registrada */}
                {/* Se cambia la condición para que solo dependa de eventos (GC) */}
                {eventos.length === 0 && (
                  <p style={{ marginTop: '15px', padding: '10px', border: '1px solid #ccc' }}>
                    No hay citas de Google Calendar registradas para este día.
                  </p>
                )}
              </div>

            </div>
          </div>
        </section>
      </main>

      {/* ======================= MODAL NUEVA ======================= */}
      {mostrarModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Agendar nueva cita 🗓️</h3>

            <div className="col-izq">
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
            </div>
            <div className="col-der">
              <label>Fecha *</label>
              <input type="date" value={nuevoEvento.date} onChange={(e) => setNuevoEvento({ ...nuevoEvento, date: e.target.value })} />
              <label>Hora *</label>
              <input type="time" value={nuevoEvento.startTime} onChange={(e) => setNuevoEvento({ ...nuevoEvento, startTime: e.target.value })} />
            </div>

            <div className="full-width-section">
              <h3>🛠 Servicios</h3>
              <div className="service-input-grid" id="serviceFormInputs">

                <div>
                  <label htmlFor="id_servicio">Servicio<span className="required">*</span></label>
                  <select id="id_servicio" name="id_servicio" value={servicioTemporal.id_servicio} onChange={(e) => setServicioTemporal({ ...servicioTemporal, id_servicio: e.target.value })}>
                    <option value="">Seleccione un servicio</option>
                    {serviciosDisponibles.map(s => (
                      <option key={s.id} value={s.id}>{s.nombre} ({s.duracion} min)</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label htmlFor="valor_servicio">Valor servicio<span className="required">*</span></label>
                  <input type="number" id="valor_servicio" min="0" step="1.00" placeholder="0.00" value={servicioTemporal.valor_servicio.toFixed()} onChange={(e) => setServicioTemporal({ ...servicioTemporal, valor_servicio: parseFloat(e.target.value) || 0 })} />
                </div>

                <div>
                  <label htmlFor="cantidad">Cantidad</label>
                  <input type="number" id="cantidad" min="1" step="1" value={servicioTemporal.cantidad.toFixed()} onChange={(e) => setServicioTemporal({ ...servicioTemporal, cantidad: parseInt(e.target.value) || 1 })} />
                </div>

                <div>
                  <label htmlFor="duracion_min">Duración Servicio</label>
                  <input type="number" id="duracion_min" min="5" step="5" value={servicioTemporal.duracion_min.toFixed()} onChange={(e) => setServicioTemporal({ ...servicioTemporal, duracion_min: parseInt(e.target.value) || 0 })} />
                </div>

                <div>
                  <label htmlFor="id_veterinario">Veterinario <span className="required">*</span></label>
                  <select name="id_veterinario" value={servicioTemporal.id_veterinario} onChange={(e) => setServicioTemporal({ ...servicioTemporal, id_veterinario: e.target.value })}>
                    <option value="">Seleccione...</option>
                    {colaboradores.map(c => (
                      <option key={c.id} value={c.id}>{c.nombre} (ID {c.id})</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label htmlFor="adicionales">Adicionales</label>
                  <input type="text" id="adicionales" placeholder="Color, tipo de corte..." value={servicioTemporal.adicionales} onChange={(e) => setServicioTemporal({ ...servicioTemporal, adicionales: e.target.value })} />
                </div>

                <div>
                  <button type="button" id="btnAddService" className="btn-primary" onClick={agregarServicio} style={{ marginTop: "20px" }}>Agregar</button>
                </div>
              </div>

              <div>
                <label htmlFor="bono_inicial">Adelanto</label>
                <input type="number" id="bono_inicial" min="0" step="1.00" value={bonoTemporal} onChange={(e) => setBonoTemporal(parseFloat(e.target.value) || 0)} />
              </div>

              <h4>Detalle de Servicios:</h4>
              <table className="service-table">
                <thead>
                  <tr>
                    <th>Servicio</th>
                    <th>Responsable</th>
                    <th>Cantidad</th>
                    <th>Duración Total</th>
                    <th>Valor Servicio (S/)</th>
                    <th>Subtotal (S/)</th>
                    <th>Acción</th>
                  </tr>
                </thead>
                <tbody id="serviceTableBody">
                  {serviciosRegistrados.map((s, index) => (
                    <tr key={index}>
                      <td style={{ textAlign: "left" }}><strong>{s.nombre_servicio}</strong>{s.adicionales && <><br /><small>{s.adicionales}</small></>}</td>
                      <td>{s.nombre_veterinario}</td>
                      <td>{s.cantidad}</td>
                      <td>{s.duracion_total} min</td>
                      <td>S/{s.valor_servicio.toFixed(2)}</td> {/* AÑADIDO S/ */}
                      <td>S/{s.subtotal.toFixed(2)}</td> {/* AÑADIDO S/ */}
                      <td><button type="button" className="btn-eliminar" onClick={() => eliminarServicio(index)}>🗑️</button></td>
                  </tr>
                  ))}
                </tbody>
                <tfoot>
                  <tr>
                    <td colSpan={3} style={{ textAlign: "right" }}>Total Duración:</td>
                    <td id="totalDuracion"><strong>{totalDuracion} min</strong></td>
                    <td colSpan={3}></td>
                  </tr>
                  <tr>
                    <td colSpan={5} style={{ textAlign: "right", fontWeight: "bold" }}>Total Servicios:</td>
                    <td style={{ fontWeight: "bold" }}>S/{totalCosto.toFixed(2)}</td> {/* AÑADIDO S/ */}
                    <td></td>
                  </tr>
                  <tr className="bono-row">
                    <td colSpan={5} style={{ textAlign: "right", fontWeight: "bold" }}>Adelanto:</td>
                    <td style={{ fontWeight: "bold", color: "red" }}>S/{bonoTemporal.toFixed(2)}</td> {/* AÑADIDO S/ */}
                    <td></td>
                  </tr>
                  <tr className="total-row">
                    <td colSpan={5} style={{ textAlign: "right" }}>Pendiente de Pago:</td>
                    <td id="totalCitaDisplay"><strong>S/{Math.max(0, totalCosto - bonoTemporal).toFixed(2)}</strong></td> {/* AÑADIDO S/ */}
                    <td></td>
                  </tr>
                </tfoot>
              </table>
            </div>

            {/* Estado y Observaciones */}
            <label>Estado *</label>
            <select
              value={nuevoEvento.estado}
              onChange={(e) => setNuevoEvento({ ...nuevoEvento, estado: e.target.value })}
            >
              <option value="">Seleccione...</option>
              {estadosAgenda.map(estado => (
                <option key={estado.id} value={estado.nombre}>{estado.nombre}</option>
              ))}
            </select>

            <label className="label-obs">Observaciones</label>
            <textarea className="textarea-obs" value={nuevoEvento.description} onChange={(e) => setNuevoEvento({ ...nuevoEvento, description: e.target.value })} />

            {/* Botones Finales */}
            <div className="acciones-modal">
              <button className="btn-agregar" onClick={guardarEvento}>
                💾 Guardar Cita
              </button>
              <button className="btn-cerrar" onClick={() => {
                   setServiciosRegistrados([]); // Limpiar servicios al cancelar
                   setBonoTemporal(0);
                   setMostrarModal(false);
                }}>❌ Cancelar</button>
            </div>

          </div>
        </div>
      )}
    </div>
  );
}

export default Agenda_general;