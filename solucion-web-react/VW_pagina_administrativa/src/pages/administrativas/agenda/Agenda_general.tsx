import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import Br_administrativa from "../../../components/barra_administrativa/Br_administrativa";
import "./Agenda_general.css";
import IST from "../../../components/proteccion/IST";
import Swal from "sweetalert2";

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
  let cliente = "N/A";
  let mascota = "N/A";
  let costoTotal = "N/A";

  // 🔥 SACAR MASCOTA (igual que antes)
  mascota = summary.split(" - ")[0].trim();

  // 🔥 NUEVO: detectar cualquier S/xx.xx (más robusto)
  const totalMatch = summary.match(/S\/\s*(\d+\.?\d*)/i);
  if (totalMatch) {
    costoTotal = `S/${parseFloat(totalMatch[1]).toFixed(2)}`;
  }

  if (description) {
    const clienteMatch = description.match(
  /Cliente:\s*(.*?)(\s*\(Documento:.*?\))?\s*\n/i,
);
    if (clienteMatch) {
      cliente = clienteMatch[1].trim();
    }

    // 🔥 respaldo desde description (por si falla summary)
    if (costoTotal === "N/A") {
      const costoMatch = description.match(/S\/\s*(\d+\.?\d*)/i);
      if (costoMatch) {
        costoTotal = `S/${parseFloat(costoMatch[1]).toFixed(2)}`;
      }
    }
  }

  return {
    Cliente: cliente,
    Mascota: mascota,
    "Costo Total": costoTotal,
  };
};

function Agenda_general() {
  const navigate = useNavigate();
  const [minimizado, setMinimizado] = useState(false);
  const [fechaSeleccionada, setFechaSeleccionada] = useState<Date>(new Date()); // Estados de Google Calendar
  // 🔒 FERIADOS PERÚ 2026
const [fechasBloqueadas, setFechasBloqueadas] = useState<string[]>([
  "2026-01-01",
  "2026-04-17",
  "2026-04-18",
  "2026-05-01",
  "2026-06-07",
  "2026-06-29",
  "2026-07-28",
  "2026-07-29",
  "2026-08-30",
  "2026-12-25",
]);

// 🔓 PERMITIR TRABAJAR EN FERIADOS
const [permitirFeriados, setPermitirFeriados] = useState(false);

  const [isSignedIn, setIsSignedIn] = useState(false);
  const [status, setStatus] = useState("🔓 Google Calendar iniciado");
  const [tokenClient, setTokenClient] = useState<any>(null);
  const [gapiInited, setGapiInited] = useState(false);
  const [gisInited, setGisInited] = useState(false);
  const [eventos, setEventos] = useState<Evento[]>([]); // Estado para citas de la Base de Datos (BD) - SE MANTIENE EL ESTADO PARA EL FLUJO DE GUARDADO

  const [citasDB, setCitasDB] = useState<any[]>([]);

  const [mostrarModal, setMostrarModal] = useState(false); // Datos Maestros

  const [clientes, setClientes] = useState<any[]>([]);
  const [mascotas, setMascotas] = useState<any[]>([]);
  const [colaboradores, setColaboradores] = useState<any[]>([]);
  const [serviciosDisponibles, setServiciosDisponibles] = useState<
    ServicioBase[]
  >([]);
  const [estadosAgenda, setEstadosAgenda] = useState<EstadoAgenda[]>([]);

  const [nuevoEvento, setNuevoEvento] = useState({
    id: "",
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
  }); // --- ESTADOS PARA GESTIÓN DE SERVICIOS ---

  const [serviciosRegistrados, setServiciosRegistrados] = useState<
    ServicioDetalle[]
  >([]);
  const [servicioTemporal, setServicioTemporal] = useState({
    id_servicio: "",
    valor_servicio: 0,
    cantidad: 1,
    duracion_min: 0,
    id_veterinario: "",
    adicionales: "",
  });
  const [bonoTemporal, setBonoTemporal] = useState(0); // CÁLCULO DE TOTALES

  const totalDuracion = serviciosRegistrados.reduce(
    (sum, s) => sum + s.duracion_total,
    0,
  );
  const totalCosto = serviciosRegistrados.reduce(
  (sum, s) => sum + Number(s.subtotal || 0),
  0
); // --- CARGA DE DATOS INICIALES ---

  useEffect(() => {
    const listarServicios = async () => {
      try {
        const respuesta = await IST.get(`/servicios`);
        const lista = Array.isArray(respuesta.data)
          ? respuesta.data
          : respuesta.data.data;
        if (Array.isArray(lista) && lista.length > 0) {
          const serviciosParseados = lista.map((s: any) => ({
            ...s,
            duracion: parseInt(s.duracion) || 0,
            precio: parseFloat(s.precio) || 0,
          }));
          setServiciosDisponibles(serviciosParseados);
        }
      } catch (error) {
       
      }
    };

    const listarEstados = async () => {
      try {
        const res = await IST.get("/estados-agenda");
        const estados = res.data.map((e: any) => ({
          id: e.id,
          nombre: e.nombre.toUpperCase(),
        }));
        setEstadosAgenda(estados);

        const estadoPendiente = estados.find(
          (e: any) => e.nombre === "PENDIENTE",
        );
        if (estadoPendiente) {
          setNuevoEvento((prev) => ({
            ...prev,
            estado: estadoPendiente.nombre,
          }));
        }
      } catch (error) {
       
      }
    };

    listarServicios();
    listarEstados();

    IST.get("/clientes")
      .then((r) => setClientes(r.data.data.filter((c: any) => c.activo)))
      .catch(() => setClientes([]));
    IST.get("/colaboradores")
      .then((r) => setColaboradores(r.data.data.filter((c: any) => c.activo)))
      .catch(() => setColaboradores([]));
    IST.get("/mascotas")
      .then((res) => setMascotas(res.data.data))
      .catch(() => setMascotas([]));
  }, []); // --- EFECTO: Sincronizar Servicio Temporal ---

  useEffect(() => {
    const serviceId = parseInt(servicioTemporal.id_servicio as string);
    if (!serviceId || isNaN(serviceId)) {
      setServicioTemporal((prev) => ({
        ...prev,
        valor_servicio: 0,
        duracion_min: 0,
      }));
      setBonoTemporal(0);
      return;
    }
    const s = serviciosDisponibles.find((s) => s.id === serviceId);
    if (s) {
      setServicioTemporal((prev) => ({
        ...prev,
        valor_servicio: s.precio,
        duracion_min: s.duracion,
      }));
      setBonoTemporal(0);
    }
  }, [servicioTemporal.id_servicio, serviciosDisponibles]); // ================== CÓDIGO DE GOOGLE CALENDAR (GAPI/GIS) ==================

  useEffect(() => {
    const script = document.createElement("script");
    script.src = "https://apis.google.com/js/api.js";
    script.onload = async () => {
      await new Promise((resolve) =>
        window.gapi.load("client", { callback: resolve }),
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
  }, []);

  useEffect(() => {
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

            // ✅ CALCULAR EXPIRACIÓN (1 hora)
            const expirationTime = Date.now() + tokenResponse.expires_in * 1000;

            // ✅ GUARDAR EN LOCALSTORAGE
            localStorage.setItem("google_token", tokenResponse.access_token);
            localStorage.setItem(
              "google_token_expires",
              expirationTime.toString(),
            );

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
    if (!gapiInited || !gisInited) return;

    const savedToken = localStorage.getItem("google_token");
    const savedExpiry = localStorage.getItem("google_token_expires");

    if (savedToken && savedExpiry) {
      const now = Date.now();

      if (now < parseInt(savedExpiry)) {
        // ✅ Token válido
        window.gapi.client.setToken({ access_token: savedToken });
        setIsSignedIn(true);
        setStatus("🔓 Sesión restaurada automáticamente");
        cargarEventos();
      } else {
        // ❌ Token expirado
        localStorage.removeItem("google_token");
        localStorage.removeItem("google_token_expires");
        setIsSignedIn(false);
        setStatus("⚠️ Sesión expirada, vuelve a iniciar sesión");
      }
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
      
      setStatus("❌ Error al cargar eventos. Intente reconectar.");
    }
  }; // La función cargarCitasBD se mantiene para el caso de uso futuro o si el flujo de edición lo necesita,
  // pero el resultado (citasDB) ya no se usa para renderizar en el JSX principal.

  const cargarCitasBD = async (fecha: Date) => {
    const dateStr = fecha.toISOString().split("T")[0];
    try {
      const res = await IST.get(`/agenda?fecha=${dateStr}`);
      const listaCitas = res.data.data.content || [];
      setCitasDB(listaCitas);
    } catch (error) {
      
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
  }, [mostrarModal]); // Recarga Citas y Eventos cuando cambia la fecha o la autenticación

  useEffect(() => {
    if (isSignedIn) cargarEventos(); // Comentar o eliminar la carga de citas de BD si no es necesario para otra lógica
    // cargarCitasBD(fechaSeleccionada);
  }, [fechaSeleccionada, isSignedIn]);

  const horaOcupada = (start: Date, end: Date, currentEventId?: string) => {
    return eventos.some((e) => {
      if (e.id === currentEventId) return false;
      const eStart = new Date(e.start.dateTime);
      const eEnd = new Date(e.end.dateTime);
      return start < eEnd && end > eStart;
    });
  }; // --- FUNCIONES DE GESTIÓN DE SERVICIOS ---

 const agregarServicio = () => {

  const sId = parseInt(servicioTemporal.id_servicio as string);

  // 🔥 VALIDAR DUPLICADO ANTES DE AGREGAR
  const existe = serviciosRegistrados.some(
    (s) => s.id_servicio === sId
  );

  if (existe) {
    Swal.fire({
      icon: "warning",
      title: "Servicio duplicado",
      text: "Este servicio ya fue agregado a la cita.",
      confirmButtonText: "Aceptar",
    });

    return;
  }

  // resto de tu código...

    const vId = parseInt(servicioTemporal.id_veterinario as string);
    const servicioInfo = serviciosDisponibles.find((s) => s.id === sId);
    const veterinarioInfo = colaboradores.find((v) => v.id === vId);

if (!servicioInfo) {
  return Swal.fire({
    title: "Campo requerido",
    text: "Debe seleccionar un servicio válido.",
    icon: "warning",
  });
}

if (!veterinarioInfo) {
  return Swal.fire({
    title: "Campo requerido",
    text: "Debe seleccionar un veterinario.",
    icon: "warning",
  });
}

if (servicioTemporal.valor_servicio <= 0) {
  return Swal.fire({
    title: "Campo requerido",
    text: "El valor del servicio debe ser mayor a 0.",
    icon: "warning",
  });
}

if (servicioTemporal.cantidad <= 0) {
  return Swal.fire({
    title: "Campo requerido",
    text: "La cantidad debe ser mayor a 0.",
    icon: "warning",
  });
}

if (servicioTemporal.duracion_min <= 0) {
  return Swal.fire({
    title: "Campo requerido",
    text: "La duración debe ser mayor a 0 minutos.",
    icon: "warning",
  });
}

    const cantidad = servicioTemporal.cantidad;
    const valorUnitario = servicioTemporal.valor_servicio;
    const duracionUnitaria = servicioTemporal.duracion_min;
    const subtotalCalculado =
  Number(valorUnitario) * Number(cantidad);

 



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

    setServiciosRegistrados((prev) => [...prev, nuevoServicio]);

  

    setServicioTemporal({
      id_servicio: "",
      valor_servicio: 0,
      cantidad: 1,
      duracion_min: 0,
      id_veterinario: servicioTemporal.id_veterinario,
      adicionales: "",
    });
  };

  const eliminarServicio = (index: number) => {
    setServiciosRegistrados((prev) => prev.filter((_, i) => i !== index));
  }; // --- FUNCIÓN PRINCIPAL DE GUARDADO (ACTUALIZADA con S/ y Recordatorios) ---

const guardarEvento = async () => {
    // 1. VALIDACIONES INICIALES Y TOKEN
    const token = sessionStorage.getItem("token");
    if (!token) {
      Swal.fire({
        title: "Alerta",
        text: "Tu sesión ha expirado. Por favor, inicia sesión de nuevo",
        icon: "warning"
      });
      navigate("/administracion/login");
      return;
    }

    if (!isSignedIn) {
      return Swal.fire({
        title: "Alerta",
        text: "Debes iniciar sesión con Google antes de registrar una cita",
        icon: "warning"
      });
    }

    if (!permitirFeriados && fechasBloqueadas.includes(nuevoEvento.date)) {
      return Swal.fire({
        title: "Alerta",
        text: "No se puede agendar en feriados",
        icon: "warning"
      });
    }

    if (!nuevoEvento.cliente || !nuevoEvento.mascota || !nuevoEvento.dni) {
      return Swal.fire({
        title: "Alerta",
        text: "Completa los campos de Cliente, DNI y Mascota",
        icon: "warning"
      });
    }

    if (serviciosRegistrados.length === 0) {
      return Swal.fire({
        title: "Alerta",
        text: "Debe registrar al menos un servicio para la cita",
        icon: "warning"
      });
    }

    // 2. FORMATEO DE HORA Y FECHAS
    let horaDBFormateada = nuevoEvento.startTime;
    if (horaDBFormateada && horaDBFormateada.length === 5) {
      horaDBFormateada = horaDBFormateada + ":00"; // Formato HH:mm:ss requerido por Java
    }

    const duracionCitaTotal = totalDuracion;
    const start = new Date(`${nuevoEvento.date}T${nuevoEvento.startTime}`);
    const end = new Date(start.getTime() + duracionCitaTotal * 60000);

   /* if (horaOcupada(start, end, nuevoEvento.id)) {
      return Swal.fire({
        title: "Alerta",
        text: "Ya existe una cita en este horario en Google Calendar. Elige otro horario",
        icon: "warning"
      });
    }*/

    // 3. IDENTIFICACIÓN DE IDS (MASCOTA Y ESTADO)
    const mascotaEncontrada = mascotas.find(
  (m) => m.nombre === nuevoEvento.mascota && Number(m.cliente?.id) === Number(nuevoEvento.clienteId)
    );
    const idMascota = mascotaEncontrada ? mascotaEncontrada.id : null;

    if (!idMascota) {
      return Swal.fire({
        title: "Error",
        text: "No se pudo encontrar el ID de la mascota",
        icon: "error"
      });
    }

    const estadoEncontrado = estadosAgenda.find((e) => e.nombre === nuevoEvento.estado);
    const idEstado = estadoEncontrado ? estadoEncontrado.id : 1;

const AgendaRequestDTO = {
  idCliente: Number(nuevoEvento.clienteId),
  idMascota: Number(idMascota),

  // NUEVOS CAMPOS
  idServicio: Number(serviciosRegistrados[0].id_servicio),
  idColaborador: serviciosRegistrados[0].id_veterinario
      ? Number(serviciosRegistrados[0].id_veterinario)
      : null,
  idVeterinario: null,

  idMedioSolicitud: 4,
  fecha: nuevoEvento.date,
  hora: horaDBFormateada,
  duracionEstimadaMin: Number(duracionCitaTotal),
  abonoInicial: Number(bonoTemporal) || 0,
  totalCita: Number(totalCosto) || 0,
  idEstado: Number(idEstado),
  observaciones: nuevoEvento.description || ""
};

    // 5. PREPARAR RECURSO PARA GOOGLE CALENDAR
    const serviciosListaGC = serviciosRegistrados
      .map((s) => `• ${s.nombre_servicio} (${s.cantidad}x S/${s.valor_servicio.toFixed(2)}) con ${s.nombre_veterinario}`)
      .join("\n");

    const eventoResource = {
      summary: `${nuevoEvento.mascota} - Total: S/${totalCosto.toFixed(2)}`,
      description: `Cliente: ${nuevoEvento.cliente}\nMascota: ${nuevoEvento.mascota}\n\n**SERVICIOS**\n${serviciosListaGC}\n\nObs: ${nuevoEvento.description || ""}`,
      start: { dateTime: start.toISOString(), timeZone: "America/Lima" },
      end: { dateTime: end.toISOString(), timeZone: "America/Lima" },
    };

    try {
      // 🔥 PASO 1: INSERTAR CITA EN BD
      const responseDB = await IST.post("/agenda", AgendaRequestDTO);

      if (!responseDB.data || !responseDB.data.success) {throw new Error(responseDB.data.message || "Error al crear la cita en BD");}

      const citaCreada = responseDB.data.data;

      // 🔥 PASO 2: INSERTAR SERVICIOS UNO POR UNO
      for (const servicio of serviciosRegistrados) {
        const ingresoDTO = {
          idAgenda: Number(citaCreada.id),
          idServicio: Number(servicio.id_servicio),
          idColaborador:
  Number(servicio.id_veterinario),
          idVeterinario: null,
          cantidad:
  parseInt(
    servicio.cantidad.toString()
  ),
          duracionMin:
  parseInt(
    servicio.duracion_min.toString()
  ),
          valorServicio:
  parseFloat(
    servicio.valor_servicio.toString()
  ),
          observaciones: servicio.adicionales || ""
        };

     try {

  const responseIngreso =
    await IST.post(
      "/ingresos-servicios",
      ingresoDTO
    );

if (!responseIngreso.data.success) {
  await Swal.fire({
    icon: "error",
    title: "Error",
    text: responseIngreso.data.message,
  });
  return;
}

  if (
    responseIngreso.data?.mensaje &&
    responseIngreso.data.mensaje.startsWith("ERROR")
  ) {

    Swal.fire({
      icon: "error",
      title: "Colaborador ocupado",
      text: responseIngreso.data.mensaje,
      confirmButtonText: "Aceptar"
    });

    return;
  }

} catch (error: any) {

  Swal.fire({
    icon: "error",
    title: "Error",
    text:
      error.response?.data?.mensaje ||
      error.message ||
      "Error al registrar servicio"
  });

  return;
}
      }
      // 🔥 PASO 3: INSERTAR EN GOOGLE CALENDAR
      if (isSignedIn) {
        await window.gapi.client.calendar.events.insert({
          calendarId: "primary",
          resource: eventoResource,
        });
      }

     

      Swal.fire({
  icon: "success",
  title: "Cita registrada",
  text: "La cita fue registrada correctamente.",
  confirmButtonText: "Aceptar",
});

      // LIMPIEZA DE FORMULARIO
      setMostrarModal(false);
      setServiciosRegistrados([]);
      setBonoTemporal(0);
      setNuevoEvento((prev) => ({
        ...prev,
        id: "",
        summary: "",
        description: "",
        dni: "",
        cliente: "",
        clienteId: 0,
        mascota: "",
        servicio: "",
        estado: "PENDIENTE",
        date: fechaSeleccionada.toISOString().split("T")[0],
        startTime: "10:00",
      }));
      cargarEventos();

    } catch (error: any) {
      Swal.fire({
        title: "Error",
        text: `Error ${error.response?.status}: ${
          error.response?.data?.message || error.message
        }`,
        icon: "error"
      });
    }
  };// ---------------------------------------------
  // --- JSX DEL COMPONENTE ---
  return (
  <div id="agenda">
    <Br_administrativa onMinimizeChange={setMinimizado} />

    <main className={minimizado ? "minimize" : ""}>
      <section className="agenda-container">

        {/* HEADER */}
        <div className="header-agenda">

  <h2 className="titulo-agenda">Agenda de Citas</h2>

  <button
    className="btn-agregar-linda"
    onClick={() => {
      setNuevoEvento((prev) => ({
        ...prev,
        id: "",
        date: fechaSeleccionada.toISOString().split("T")[0],
        startTime: "10:00",
        estado:
          estadosAgenda.find((e) => e.nombre === "PENDIENTE")
            ?.nombre || "PENDIENTE",
      }));
      setServiciosRegistrados([]);
      setBonoTemporal(0);
      setMostrarModal(true);
    }}
  >
    ✨➕ Nueva cita
  </button>

</div>

        {/* GRID */}
        <div className="agenda-layout">

          {/* IZQUIERDA */}
          <div className="calendar-container">

            <Calendar
  onChange={(date) => setFechaSeleccionada(date as Date)}
  value={fechaSeleccionada}
  locale="es-ES"
  tileDisabled={({ date }) => {
    if (permitirFeriados) return false;

    const fecha = date.toISOString().split("T")[0];
    return fechasBloqueadas.includes(fecha);
  }}
  tileClassName={({ date }) => {
    const fecha = date.toISOString().split("T")[0];
    return fechasBloqueadas.includes(fecha) ? "feriado" : null;
  }}
/>

            <div className="auth-buttons">
  {!isSignedIn ? (
    <button className="btn-agregar" onClick={iniciarSesion}>
      🔐 Iniciar sesión
    </button>
  ) : (
    <div className="auth-buttons-row">
      
      {/* IZQUIERDA */}
      <button
        onClick={() => setPermitirFeriados(!permitirFeriados)}
        className="btn-feriado"
      >
        {permitirFeriados ? "🔓 Feriados ACTIVOS" : "🔒 Feriados BLOQUEADOS"}
      </button>

      {/* DERECHA */}
      <button className="btn-cerrar" onClick={cerrarSesion}>
        🚪Cerrar sesión
      </button>

    </div>
  )}
</div>

            <p style={{ marginTop: "10px", color: "#555" }}>{status}</p>

          </div>

          {/* DERECHA */}
          <div className="citas-container">

            <div className="citas-header">

  <h3 className="titulo-izquierda">
    📋 Citas Registradas del {fechaSeleccionada.toLocaleDateString()}
  </h3>

{isSignedIn && (
  <div className="cita-acciones">
    <button
      className="btn-mas-info"
      onClick={() => {
        const fechaISO = fechaSeleccionada
          ?.toISOString()
          .split("T")[0];

        navigate(`/administracion/agenda/EditarCita?fecha=${fechaISO}`);
      }}
    >
      📄 Más información
    </button>
  </div>
)}

</div>

            <div className="linea-divisoria"></div>

            <div className="citas-lista">

              {eventos.map((e) => {
                const inicio = new Date(e.start.dateTime).toLocaleTimeString([], {
                  hour: "2-digit",
                  minute: "2-digit",
                });

                const fin = new Date(e.end.dateTime).toLocaleTimeString([], {
                  hour: "2-digit",
                  minute: "2-digit",
                });

                const dia = new Date(e.start.dateTime).toLocaleDateString();

                const detalles = extraerDetallesGC(e.summary, e.description);

                return (
                  <div key={`gc-${e.id}`} className="cita-card">

 <div className="cita-info-horizontal">
  <span><strong>Cliente:</strong> {detalles.Cliente || "N/A"}</span>
  <span><strong>Mascota:</strong> {detalles.Mascota || "N/A"}</span>
  <span><strong>Hora:</strong> {inicio} - {fin}</span>
  <span><strong>Día:</strong> {dia}</span>
  <span>
    <strong>Total:</strong>
    {detalles["Costo Total"] || "S/0.00"}
  </span>
</div>
</div>
                );
              })}

              {eventos.length === 0 && (
                <p style={{ marginTop: "15px", padding: "10px" }}>
                  No hay citas registradas para este día.
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
                            <label> Numero de Documento <span className="required">*</span></label>
                           
              <input
                type="text"
                value={nuevoEvento.dni}
                onChange={(e) => {
                  const dni = e.target.value;
                  setNuevoEvento({ ...nuevoEvento, dni });
                  const encontrado = clientes.find(
  (c) => String(c.documento) === String(dni)
);
                  if (encontrado) {
                    setNuevoEvento((p) => ({
                      ...p,
                      cliente: encontrado.nombre,
                      clienteId: encontrado.id,
                      mascota: "",
                    }));
                  } else {
                    setNuevoEvento((p) => ({
                      ...p,
                      cliente: "",
                      clienteId: 0,
                      mascota: "",
                    }));
                  }
                }}
              />
                            <label>Cliente  <span className="required">*</span></label>
                          
              <input type="text" value={nuevoEvento.cliente} disabled />       
                     <label>Mascota  <span className="required">*</span></label>           
              <select
               value={nuevoEvento.mascota}
               onChange={(e) =>
               setNuevoEvento({ ...nuevoEvento, mascota: e.target.value })
                 }
              disabled={!nuevoEvento.clienteId}
              >
             <option value="">Seleccione mascota...</option>

              {mascotas
             .filter((m) => Number(m.cliente?.id) === Number(nuevoEvento.clienteId))
            .map((m) => (
            <option key={m.id} value={m.nombre}>
            {m.nombre}
            </option>
            ))}
            </select>
                        
            </div>
                       
            <div className="col-der">
                             <label>Fecha <span className="required">*</span></label>
                           
              <input
                type="date"
                value={nuevoEvento.date}
                onChange={(e) =>
                  setNuevoEvento({ ...nuevoEvento, date: e.target.value })
                }
              />
                             <label>Hora  <span className="required">*</span></label>
                          
              <input
                type="time"
                value={nuevoEvento.startTime}
                onChange={(e) =>
                  setNuevoEvento({ ...nuevoEvento, startTime: e.target.value })
                }
              />
                        
            </div>
                      
            <div className="full-width-section">
                            <h3>🛠 Servicios</h3>            
              <div className="service-input-grid" id="serviceFormInputs">
                             
                <div>
                                  
                  <label htmlFor="id_servicio">
                    Servicio<span className="required">*</span>
                  </label>
                                 
                  <select
                    id="id_servicio"
                    name="id_servicio"
                    value={servicioTemporal.id_servicio}
                    onChange={(e) =>
                      setServicioTemporal({
                        ...servicioTemporal,
                        id_servicio: e.target.value,
                      })
                    }
                  >
                                      
                    <option value="">Seleccione un servicio *</option>           
                          
                    {serviciosDisponibles.map((s) => (
                      <option key={s.id} value={s.id}>
                        {s.nombre} 
                      </option>
                    ))}
                                   
                  </select>
                                 
                </div>
                              
                <div>
                                  
                  <label htmlFor="valor_servicio">
                    Valor servicio <span className="required">*</span>
                  </label>
                                 
                  <input
  type="number"
  id="valor_servicio"
  min="1"
  step="0.01"
  placeholder="Ingrese precio"
  value={
    servicioTemporal.valor_servicio === 0
      ? ""
      : servicioTemporal.valor_servicio
  }
  onChange={(e) =>
    setServicioTemporal({
      ...servicioTemporal,
      valor_servicio: parseFloat(e.target.value) || 0,
    })
  }
/>
                                 
                </div>
                               
                <div>
                                   <label>Cantidad <span className="required">*</span></label>
                                  
                  <input
                    type="number"
                    id="cantidad"
                    min="1"
                    step="1"
                    value={servicioTemporal.cantidad.toFixed()}
                    onChange={(e) =>
                      setServicioTemporal({
                        ...servicioTemporal,
                        cantidad: parseInt(e.target.value) || 1,
                      })
                    }
                  />
                                
                </div>
                             
                <div>
                                  
                  <label htmlFor="duracion_min">Duración Servicio <span className="required">*</span></label>
                                  
                  <input
                    type="number"
                    id="duracion_min"
                    min="5"
                    step="5"
                    value={servicioTemporal.duracion_min.toFixed()}
                    onChange={(e) =>
                      setServicioTemporal({
                        ...servicioTemporal,
                        duracion_min: parseInt(e.target.value) || 0,
                      })
                    }
                  />
                                
                </div>
                             
                <div>
                                  
                  <label htmlFor="id_veterinario">
                    Veterinario<span className="required">*</span>
                  </label>
                                  
                  <select
                    name="id_veterinario"
                    value={servicioTemporal.id_veterinario}
                    onChange={(e) =>
                      setServicioTemporal({
                        ...servicioTemporal,
                        id_veterinario: e.target.value,
                      })
                    }
                  >
                                        <option value="">Seleccione...</option> 
                                     
                    {colaboradores.map((c) => (
  <option key={c.id} value={c.id}>
    {c.nombre}
  </option>
))}
                                  
                  </select>
                               
                </div>
                            
                <div>
                                 
                  <label>Adicionales  <span className="required">*</span></label>
                                 
                  <input
                    type="text"
                    id="adicionales"
                    placeholder="Color, tipo de corte..."
                    value={servicioTemporal.adicionales}
                    onChange={(e) =>
                      setServicioTemporal({
                        ...servicioTemporal,
                        adicionales: e.target.value,
                      })
                    }
                  />
                                
                </div>
                             
                <div>
                                  
                  <button
                    type="button"
                    id="btnAddService"
                    className="btn-primary"
                    onClick={agregarServicio}
                    style={{ marginTop: "20px" }}
                  >
                    Agregar
                  </button>
                                 
                </div>
                            
              </div>
                          
              <div>
                                <label htmlFor="bono_inicial">Adelanto</label>
                               
                <input
                  type="number"
                  id="bono_inicial"
                  min="0"
                  step="1.00"
                  value={bonoTemporal}
                  onChange={(e) =>
                    setBonoTemporal(parseFloat(e.target.value) || 0)
                  }
                />
                          
              </div>
                            <h4>Detalle de Servicios:</h4>           
              <table className="service-table">
                              
                <thead>
                                 
                  <tr>
                    <th>Servicio</th>              
                    <th>Responsable</th><th>Cantidad</th>   
                    <th>Duración Total</th>                  
                    <th>Valor Servicio (S/)</th>                  
                    <th>Subtotal (S/)</th><th>Acción</th>   
                                
                  </tr>
                                
                </thead>
                              
                <tbody id="serviceTableBody">
                                 
                  {serviciosRegistrados.map((s, index) => (
                    <tr key={index}>
                                         
                      <td style={{ textAlign: "left" }}>
                        <strong>{s.nombre_servicio}</strong>
                        {s.adicionales && (
                          <>
                            <br />
                            <small>{s.adicionales}</small>
                          </>
                        )}
                      </td>
                      <td>{s.nombre_veterinario}</td>     
                      <td>{s.cantidad}</td>                    
                      <td>{s.duracion_total} min</td>                   
                      <td>S/{s.valor_servicio.toFixed(2)}</td>
                      {/* AÑADIDO S/ */}                    
                      <td>S/{s.subtotal.toFixed(2)}</td> {/* AÑADIDO S/ */}     
                                    
                      <td>
                        <button
                          type="button"
                          className="btn-eliminar"
                          onClick={() => eliminarServicio(index)}
                        >
                          🗑️
                        </button>
                      </td>               
                    </tr>
                  ))}                  
                </tbody>                    
                <tfoot>                           
                  <tr>                                  
                    <td colSpan={3} style={{ textAlign: "right" }}>
                      Total Duración:
                    </td>                    
                    <td id="totalDuracion">
                      <strong>{totalDuracion} min</strong>
                    </td>
                  <td colSpan={3}></td>               
                  </tr>
                  <tr>
                    <td
                      colSpan={5}
                      style={{ textAlign: "right", fontWeight: "bold" }}
                    >
                      Total Servicios:
                    </td>
                    <td style={{ fontWeight: "bold" }}>
                      S/{totalCosto.toFixed(2)}
                    </td>
                    {/* AÑADIDO S/ */}<td></td>  
                  </tr>                         
                  <tr className="bono-row">                
                    <td
                      colSpan={5}
                      style={{ textAlign: "right", fontWeight: "bold" }}
                    >
                      Adelanto:
                    </td>
                                      
                    <td style={{ fontWeight: "bold", color: "red" }}>
                      S/{bonoTemporal.toFixed(2)}
                    </td>
                    {/* AÑADIDO S/ */}                    <td></td>             
                      
                  </tr>
                  <tr className="total-row">
                    <td colSpan={5} style={{ textAlign: "right" }}>
                      Pendiente de Pago:
                    </td>
                    <td id="totalCitaDisplay">
                      <strong>
                        S/{Math.max(0, totalCosto - bonoTemporal).toFixed(2)}
                      </strong>
                    </td>
                    {/* AÑADIDO S/ */}                    <td></td>             
                      
                  </tr>
                </tfoot>
              </table>
            </div>
                        {/* Estado y Observaciones */}          
             <label>Estado <span className="required">*</span></label>           
            <select
              value={nuevoEvento.estado}
              onChange={(e) =>
                setNuevoEvento({ ...nuevoEvento, estado: e.target.value })
              }
            >
                            <option value="">Seleccione...</option>           
              {estadosAgenda.map((estado) => (
                <option key={estado.id} value={estado.nombre}>
                  {estado.nombre}
                </option>
              ))}
                         
            </select>
                        <label>Observaciones  <span className="required">*</span></label>
            <textarea
              className="textarea-obs"
              value={nuevoEvento.description}
              onChange={(e) =>
                setNuevoEvento({ ...nuevoEvento, description: e.target.value })
              }
            />
                        {/* Botones Finales */}         
            <div className="acciones-modal">
                         
              <button className="btn-agregar" onClick={guardarEvento}>
                                💾 Guardar Cita             
              </button>
              <button
                className="btn-cerrar"
                onClick={() => {
                  setServiciosRegistrados([]); // Limpiar servicios al cancelar
                  setBonoTemporal(0);
                  setMostrarModal(false);
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

export default Agenda_general;