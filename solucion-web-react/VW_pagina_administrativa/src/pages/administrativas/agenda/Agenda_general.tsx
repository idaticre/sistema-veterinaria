import { useState, useEffect, useMemo } from "react";
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
  requiereSala?: boolean;
}

interface EstadoAgenda {
  id: number;
  nombre: string;
}

interface Sala {
  id: number;
  nombre: string;
  descripcion?: string;
  activo?: boolean;
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
  requiereSala?: boolean;
}

interface ResumenExito {
  codigo: string;
  cliente: string;
  mascota: string;
  fecha: string;
  hora: string;
  estado: string;
  observaciones: string;
  cantidadServicios: number;
  totalServicios: number;
  adelanto: number;
  pendiente: number;
  sala: string;
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
  const [pasoActual, setPasoActual] = useState(1);

  // 🆕 ESTADOS PARA EL PASO 3 (RESUMEN) Y PANTALLA DE ÉXITO
  const [mostrarExito, setMostrarExito] = useState(false);
  const [datosResumenExito, setDatosResumenExito] = useState<ResumenExito | null>(null);

  const [clientes, setClientes] = useState<any[]>([]);
  const [mascotas, setMascotas] = useState<any[]>([]);
  const [colaboradores, setColaboradores] = useState<any[]>([]);
  const [serviciosDisponibles, setServiciosDisponibles] = useState<ServicioBase[]>([]);
  const [estadosAgenda, setEstadosAgenda] = useState<EstadoAgenda[]>([]);
  const [salas, setSalas] = useState<Sala[]>([]);

  // 🆕 ESTADOS PARA SALAS DINÁMICAS
  const [salasDisponibles, setSalasDisponibles] = useState<Sala[]>([]);
  const [salaSeleccionada, setSalaSeleccionada] = useState<number | null>(null);
  const [cargandoSalas, setCargandoSalas] = useState(false);

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
    nombreSala: "",
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
  );

  // 🆕 ¿La cita actual requiere sala? (por el servicio seleccionado en el combo o por alguno ya agregado)
  const requiereSalaActual = useMemo(() => {
    const servicioSeleccionado = serviciosDisponibles.find(
      (s) => s.id === parseInt(servicioTemporal.id_servicio as string),
    );
    const requiereEnSeleccionado = servicioSeleccionado?.requiereSala === true;
    const requiereEnRegistrados = serviciosRegistrados.some(
      (s) => s.requiereSala === true,
    );
    return requiereEnSeleccionado || requiereEnRegistrados;
  }, [servicioTemporal.id_servicio, serviciosDisponibles, serviciosRegistrados]);

  // 🆕 VALIDACIÓN DEL PASO 1: habilita "Siguiente" solo cuando los campos obligatorios están completos
  const paso1Valido = useMemo(() => {
    return (
      nuevoEvento.dni.trim() !== "" &&
      nuevoEvento.cliente.trim() !== "" &&
      nuevoEvento.mascota.trim() !== "" &&
      nuevoEvento.date.trim() !== "" &&
      nuevoEvento.startTime.trim() !== "" &&
      nuevoEvento.estado.trim() !== ""
    );
  }, [
    nuevoEvento.dni,
    nuevoEvento.cliente,
    nuevoEvento.mascota,
    nuevoEvento.date,
    nuevoEvento.startTime,
    nuevoEvento.estado,
  ]);

  // 🆕 ¿Hay un borrador de cita en progreso? Se usa para saber si al hacer clic afuera
  // del modal (o al reabrirlo) debemos conservar lo ya escrito en vez de reiniciar el formulario.
  const hayDatosSinGuardar = useMemo(() => {
    return (
      nuevoEvento.dni.trim() !== "" ||
      nuevoEvento.cliente.trim() !== "" ||
      nuevoEvento.mascota.trim() !== "" ||
      nuevoEvento.description.trim() !== "" ||
      serviciosRegistrados.length > 0 ||
      salaSeleccionada !== null
    );
  }, [
    nuevoEvento.dni,
    nuevoEvento.cliente,
    nuevoEvento.mascota,
    nuevoEvento.description,
    serviciosRegistrados,
    salaSeleccionada,
  ]); // --- CARGA DE DATOS INICIALES ---

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
            requiereSala: s.requiereSala ?? s.requiere_sala ?? false,
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
    const listarSalas = async () => {

      try {

        const res = await IST.get("/salas");

        const listaSalas = Array.isArray(res.data)
          ? res.data
          : res.data?.data;

        setSalas(Array.isArray(listaSalas) ? listaSalas : []);

      } catch {

        setSalas([]);

      }

    }

    listarServicios();
    listarEstados();
    listarSalas();

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

  // 🆕 CONSULTAR SALAS DISPONIBLES EN EL BACKEND (dinámico, sin datos simulados)
  const cargarSalasDisponibles = async (fecha: string, hora: string) => {
    if (!fecha || !hora) return;
    setCargandoSalas(true);
    try {
      const res = await IST.get(`/salas/disponibles?fecha=${fecha}&hora=${hora}`);
      const lista = Array.isArray(res.data) ? res.data : res.data?.data || [];
      setSalasDisponibles(lista);

      // Si la sala previamente elegida ya no está entre las disponibles, se deselecciona
      setSalaSeleccionada((prev) => {
        if (prev && lista.some((s: Sala) => s.id === prev)) return prev;
        return null;
      });
    } catch (error) {
      setSalasDisponibles([]);
      setSalaSeleccionada(null);
    } finally {
      setCargandoSalas(false);
    }
  };

  // 🆕 RESET COMPLETO DEL MODAL (usado por: cancelar, cerrar con ×, OK del éxito)
  const resetModalCompleto = () => {
    setServiciosRegistrados([]);
    setServicioTemporal({
      id_servicio: "",
      valor_servicio: 0,
      cantidad: 1,
      duracion_min: 0,
      id_veterinario: "",
      adicionales: "",
    });
    setBonoTemporal(0);
    setPasoActual(1);
    setMostrarModal(false);
    setMostrarExito(false);
    setDatosResumenExito(null);
    setSalasDisponibles([]);
    setSalaSeleccionada(null);
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
      nombreSala: "",
      estado:
        estadosAgenda.find((e) => e.nombre === "PENDIENTE")?.nombre ||
        "PENDIENTE",
      date: fechaSeleccionada.toISOString().split("T")[0],
      startTime: "10:00",
    }));
  };

  // 🆕 CLIC FUERA DEL MODAL: solo lo oculta (no se pierde nada de lo ya escrito).
  // El modal solo se resetea de verdad con la X, "Cancelar" o al terminar el registro.
  useEffect(() => {
    if (!mostrarModal) return;
    const handleClickOutside = (e: MouseEvent) => {
      const overlay = document.querySelector(".modal-overlay");
      const content = document.querySelector(".modal-content");
      if (overlay && content && e.target === overlay) {
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
    if (isSignedIn) cargarEventos(); // Comentar o eliminar la carga de citas de BD si no es necesario para otra lógica
    // cargarCitasBD(fechaSeleccionada);
  }, [fechaSeleccionada, isSignedIn]);

  // 🆕 Cada vez que el servicio elegido requiera sala, o cambie la fecha/hora, se consulta al backend
  useEffect(() => {
    if (requiereSalaActual && nuevoEvento.date && nuevoEvento.startTime) {
      cargarSalasDisponibles(nuevoEvento.date, nuevoEvento.startTime);
    } else {
      setSalasDisponibles([]);
      setSalaSeleccionada(null);
    }
  }, [requiereSalaActual, nuevoEvento.date, nuevoEvento.startTime]);

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
      requiereSala: servicioInfo.requiereSala === true,
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
  }; // --- FUNCIÓN PRINCIPAL DE GUARDADO (ACTUALIZADA con S/ y pantalla de éxito) ---

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

    // 🆕 VALIDAR SALA CUANDO EL SERVICIO LA REQUIERE
    if (requiereSalaActual && !salaSeleccionada) {
      return Swal.fire({
        title: "Alerta",
        text: "Debes seleccionar una sala disponible para este servicio",
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

      // 🆕 SALA DINÁMICA
      idSala: requiereSalaActual && salaSeleccionada ? Number(salaSeleccionada) : null,

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

      if (!responseDB.data || !responseDB.data.success) { throw new Error(responseDB.data.message || "Error al crear la cita en BD"); }

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

      // 🆕 PASO 4: ARMAR RESUMEN Y MOSTRAR PANTALLA DE ÉXITO (en vez de cerrar el modal)
      const codigoCita = `C-${String(citaCreada.id).padStart(6, "0")}`;
      setDatosResumenExito({
        codigo: codigoCita,
        cliente: nuevoEvento.cliente,
        mascota: nuevoEvento.mascota,
        fecha: nuevoEvento.date,
        hora: nuevoEvento.startTime,
        estado: nuevoEvento.estado,
        observaciones: nuevoEvento.description || "(ninguna)",
        cantidadServicios: serviciosRegistrados.length,
        totalServicios: totalCosto,
        adelanto: bonoTemporal,
        pendiente: Math.max(0, totalCosto - bonoTemporal),
        sala: requiereSalaActual
          ? (nuevoEvento.nombreSala || "Sin asignar")
          : "No requiere sala",
      });
      setMostrarExito(true);
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

  const formatearFecha = (fecha: string) => {
    if (!fecha) return "-";
    const [y, m, d] = fecha.split("-");
    return `${d}/${m}/${y}`;
  };

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
                // 🆕 Si ya había un borrador en progreso (por ejemplo, el modal se
                // ocultó al hacer clic afuera), solo lo reabrimos sin perder nada.
                if (hayDatosSinGuardar) {
                  setMostrarModal(true);
                  return;
                }

                setNuevoEvento((prev) => ({
                  ...prev,
                  id: "",
                  date: fechaSeleccionada.toISOString().split("T")[0],
                  startTime: "10:00",
                  nombreSala: "",
                  estado:
                    estadosAgenda.find((e) => e.nombre === "PENDIENTE")
                      ?.nombre || "PENDIENTE",
                }));
                setServiciosRegistrados([]);
                setBonoTemporal(0);
                setPasoActual(1);
                setMostrarExito(false);
                setDatosResumenExito(null);
                setSalasDisponibles([]);
                setSalaSeleccionada(null);
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

            {!mostrarExito ? (
              <>
                <button
                  className="modal-close-x"
                  type="button"
                  onClick={resetModalCompleto}
                  aria-label="Cerrar"
                >
                  ×
                </button>

                <div className="modal-header-v2">
                  <div className="modal-header-icono">📅</div>
                  <div>
                    <h3>Agendar nueva cita</h3>
                    <p className="modal-header-sub">Paso {pasoActual} de 3</p>
                  </div>
                </div>

                <div className="wizard-container">

                  <div className={`wizard-step ${pasoActual === 1 ? "active" : pasoActual > 1 ? "completed" : ""}`}>
                    <div className="wizard-circle">{pasoActual > 1 ? "✓" : "1"}</div>
                    <span>Información básica</span>
                  </div>

                  <div className={`wizard-line ${pasoActual > 1 ? "completed" : ""}`}></div>

                  <div className={`wizard-step ${pasoActual === 2 ? "active" : pasoActual > 2 ? "completed" : ""}`}>
                    <div className="wizard-circle">{pasoActual > 2 ? "✓" : "2"}</div>
                    <span>Servicios y Salas</span>
                  </div>

                  <div className={`wizard-line ${pasoActual > 2 ? "completed" : ""}`}></div>

                  <div className={`wizard-step ${pasoActual === 3 ? "active" : ""}`}>
                    <div className="wizard-circle">3</div>
                    <span>Resumen</span>
                  </div>

                </div>

                {pasoActual === 1 && (
                  <>
                    <div className="seccion-card">
                      <div className="seccion-header">
                        <span className="seccion-icono">👤</span>
                        <div>
                          <h4>Información básica</h4>
                          <p>Completa los datos para la nueva cita</p>
                        </div>
                      </div>

                      <div className="step-grid-v2">

                        <div>
                          <label>
                            Número de Documento<span className="required">*</span>
                          </label>
                          <input
                            type="text"
                            value={nuevoEvento.dni}
                            onChange={(e) => {
                              const dni = e.target.value;

                              setNuevoEvento({
                                ...nuevoEvento,
                                dni,
                              });

                              const encontrado = clientes.find(
                                (c) =>
                                  String(c.documento) === String(dni)
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
                        </div>

                        <div>
                          <label>
                            Cliente<span className="required">*</span>
                          </label>
                          <input type="text" disabled value={nuevoEvento.cliente} />
                        </div>

                        <div>
                          <label>
                            Mascota<span className="required">*</span>
                          </label>
                          <select
                            value={nuevoEvento.mascota}
                            disabled={!nuevoEvento.clienteId}
                            onChange={(e) =>
                              setNuevoEvento({
                                ...nuevoEvento,
                                mascota: e.target.value,
                              })
                            }
                          >
                            <option value="">Seleccione mascota...</option>
                            {mascotas
                              .filter(
                                (m) =>
                                  Number(m.cliente?.id) ===
                                  Number(nuevoEvento.clienteId)
                              )
                              .map((m) => (
                                <option key={m.id} value={m.nombre}>
                                  {m.nombre}
                                </option>
                              ))}
                          </select>
                        </div>

                        <div>
                          <label>
                            Fecha<span className="required">*</span>
                          </label>
                          <input
                            type="date"
                            value={nuevoEvento.date}
                            onChange={(e) =>
                              setNuevoEvento({
                                ...nuevoEvento,
                                date: e.target.value,
                              })
                            }
                          />
                        </div>

                        <div>
                          <label>
                            Hora<span className="required">*</span>
                          </label>
                          <input
                            type="time"
                            value={nuevoEvento.startTime}
                            onChange={(e) =>
                              setNuevoEvento({
                                ...nuevoEvento,
                                startTime: e.target.value,
                              })
                            }
                          />
                        </div>

                        <div>
                          <label>
                            Estado<span className="required">*</span>
                          </label>
                          <select
                            value={nuevoEvento.estado}
                            onChange={(e) =>
                              setNuevoEvento({
                                ...nuevoEvento,
                                estado: e.target.value,
                              })
                            }
                          >
                            <option value="">Seleccione...</option>
                            {estadosAgenda.map((estado) => (
                              <option key={estado.id} value={estado.nombre}>
                                {estado.nombre}
                              </option>
                            ))}
                          </select>
                        </div>

                        <div className="col-span-2">
                          <label>Observaciones</label>
                          <textarea
                            className="textarea-obs"
                            placeholder="Escribe observaciones (opcional)..."
                            value={nuevoEvento.description}
                            maxLength={200}
                            onChange={(e) =>
                              setNuevoEvento({
                                ...nuevoEvento,
                                description: e.target.value,
                              })
                            }
                          />
                          <span className="contador-caracteres">
                            {nuevoEvento.description.length}/200
                          </span>
                        </div>

                      </div>
                    </div>

                    <div className="acciones-wizard">
                      <span />
                      <button
                        className="btn-siguiente"
                        type="button"
                        onClick={() => setPasoActual(2)}
                        disabled={!paso1Valido}
                        title={
                          !paso1Valido
                            ? "Completa Documento, Cliente, Mascota, Fecha, Hora y Estado para continuar"
                            : undefined
                        }
                        style={{
                          opacity: paso1Valido ? 1 : 0.5,
                          cursor: paso1Valido ? "pointer" : "not-allowed",
                        }}
                      >
                        Siguiente →
                      </button>
                    </div>
                  </>
                )}

                {pasoActual === 2 && (
                  <>
                    <div className="paso2-grid-v2">

                      {/* IZQUIERDA: SERVICIOS */}
                      <div className="seccion-card">
                        <div className="seccion-header">
                          <span className="seccion-icono">🛠️</span>
                          <div>
                            <h4>Servicios</h4>
                            <p>Agrega los servicios que necesita la mascota</p>
                          </div>
                        </div>

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
                              <option value="">Seleccione servicio</option>
                              {serviciosDisponibles.map((s) => (
                                <option key={s.id} value={s.id}>
                                  {s.nombre}
                                </option>
                              ))}
                            </select>
                          </div>

                          <div>
                            <label htmlFor="valor_servicio">
                              Valor Servicio<span className="required">*</span>
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
                                  valor_servicio:
                                    parseFloat(e.target.value) || 0,
                                })
                              }
                            />
                          </div>

                          <div>
                            <label>
                              Cantidad<span className="required">*</span>
                            </label>
                            <input
                              type="number"
                              id="cantidad"
                              min="1"
                              step="1"
                              value={servicioTemporal.cantidad.toFixed()}
                              onChange={(e) =>
                                setServicioTemporal({
                                  ...servicioTemporal,
                                  cantidad:
                                    parseInt(e.target.value) || 1,
                                })
                              }
                            />
                          </div>

                          <div>
                            <label htmlFor="duracion_min">
                              Duración (min)<span className="required">*</span>
                            </label>
                            <input
                              type="number"
                              id="duracion_min"
                              min="5"
                              step="5"
                              value={servicioTemporal.duracion_min.toFixed()}
                              onChange={(e) =>
                                setServicioTemporal({
                                  ...servicioTemporal,
                                  duracion_min:
                                    parseInt(e.target.value) || 0,
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
                            <label>Adicionales</label>
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
                            <label>&nbsp;</label>
                            <button
                              type="button"
                              id="btnAddService"
                              className="btn-primary"
                              onClick={agregarServicio}
                            >
                              ➕ Agregar
                            </button>
                          </div>

                        </div>

                        <div style={{ marginTop: "15px" }}>
                          <label htmlFor="bono_inicial">Adelanto</label>
                          <input
                            type="number"
                            id="bono_inicial"
                            min="0"
                            step="1"
                            value={bonoTemporal}
                            onChange={(e) =>
                              setBonoTemporal(
                                parseFloat(e.target.value) || 0
                              )
                            }
                          />
                        </div>
                      </div>

                      {/* DERECHA: SALAS (TODAS, DISPONIBLES/OCUPADAS) */}
                      {requiereSalaActual && (
                        <div className="seccion-card salas-card">
                          <div className="seccion-header">
                            <span className="seccion-icono">🏠</span>
                            <div>
                              <h4>Salas disponibles</h4>
                              <p>Seleccione una sala disponible</p>
                            </div>
                          </div>

                          {cargandoSalas && (
                            <p style={{ padding: "10px" }}>Cargando disponibilidad de salas...</p>
                          )}

                          {!cargandoSalas && salas.length === 0 && (
                            <p style={{ padding: "10px" }}>
                              No hay salas registradas.
                            </p>
                          )}

                          {/* 🆕 Se listan SIEMPRE todas las salas registradas (estado "salas").
                              La disponibilidad se determina comparando contra "salasDisponibles",
                              que sigue viniendo del mismo endpoint de antes. */}
                          {!cargandoSalas &&
                            salas.map((sala) => {
                              const disponible = salasDisponibles.some(
                                (s) => s.id === sala.id,
                              );
                              const seleccionada = salaSeleccionada === sala.id;

                              return (
                                <div
                                  key={sala.id}
                                  className={`card-sala ${
                                    disponible ? "libre" : "ocupada"
                                  } ${seleccionada ? "seleccionada" : ""}`}
                                  style={{
                                    cursor: disponible ? "pointer" : "not-allowed",
                                    opacity: disponible ? 1 : 0.65,
                                  }}
                                  onClick={() => {
                                    if (!disponible) return;
                                    setSalaSeleccionada(sala.id);
                                    setNuevoEvento((prev) => ({
                                      ...prev,
                                      nombreSala: sala.nombre,
                                    }));
                                  }}
                                >
                                  <div className="emoji">🐶</div>
                                  <div className="sala-info">
                                    <strong>{sala.nombre}</strong>
                                    <p>
                                      <span
                                        className={`dot ${disponible ? "verde" : "rojo"}`}
                                      ></span>
                                      {disponible ? "Disponible" : "Ocupada"}
                                    </p>
                                    {sala.descripcion && <small>{sala.descripcion}</small>}
                                  </div>
                                  <div
                                    className={`sala-radio ${
                                      seleccionada ? "activo" : ""
                                    }`}
                                  ></div>
                                </div>
                              );
                            })}

                          <div className="sala-leyenda">
                            <span><span className="dot verde"></span>Disponible</span>
                            <span><span className="dot rojo"></span>Ocupada</span>
                          </div>
                        </div>
                      )}

                    </div>

                    <div className="seccion-card">
                      <h4 style={{ marginBottom: "10px" }}>Servicios agregados</h4>

                      <table className="service-table">
                        <thead>
                          <tr>
                            <th>Servicio</th>
                            <th>Responsable</th>
                            <th>Cantidad</th>
                            <th>Duración</th>
                            <th>Valor Unit.</th>
                            <th>Subtotal</th>
                            <th>Acción</th>
                          </tr>
                        </thead>
                        <tbody>
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
                              <td>S/{s.subtotal.toFixed(2)}</td>
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
                          {serviciosRegistrados.length === 0 && (
                            <tr>
                              <td colSpan={7} className="tabla-vacia">
                                Aún no hay servicios agregados.
                              </td>
                            </tr>
                          )}
                        </tbody>
                      </table>

                      <div className="totales-mini">
                        <div className="totales-mini-card">
                          <span>Total Servicios:</span>
                          <strong>S/{totalCosto.toFixed(2)}</strong>
                        </div>
                        <div className="totales-mini-card">
                          <span>Tiempo Total:</span>
                          <strong>{totalDuracion} min</strong>
                        </div>
                        <div className="totales-mini-card">
                          <span>Adelanto:</span>
                          <strong>S/{bonoTemporal.toFixed(2)}</strong>
                        </div>
                      </div>
                    </div>

                    <div className="acciones-wizard">
                      <button
                        type="button"
                        className="btn-anterior"
                        onClick={() => setPasoActual(1)}
                      >
                        ← Anterior
                      </button>

                      <button
                        type="button"
                        className="btn-siguiente"
                        onClick={() => setPasoActual(3)}
                      >
                        Siguiente →
                      </button>
                    </div>
                  </>
                )}

                {pasoActual === 3 && (
                  <>
                    <div className="resumen-grid">

                      <div className="seccion-card">
                        <div className="seccion-header">
                          <span className="seccion-icono">👤</span>
                          <h4>Información básica</h4>
                        </div>
                        <div className="resumen-lista">
                          <div><span>Número de Documento</span><strong>{nuevoEvento.dni || "-"}</strong></div>
                          <div><span>Cliente</span><strong>{nuevoEvento.cliente || "-"}</strong></div>
                          <div><span>Mascota</span><strong>{nuevoEvento.mascota || "-"}</strong></div>
                          <div><span>Fecha</span><strong>{formatearFecha(nuevoEvento.date)}</strong></div>
                          <div><span>Hora</span><strong>{nuevoEvento.startTime}</strong></div>
                          <div><span>Estado</span><strong className="badge-estado">{nuevoEvento.estado}</strong></div>
                          <div><span>Observaciones</span><strong>{nuevoEvento.description || "(ninguna)"}</strong></div>
                        </div>
                      </div>

                      <div className="seccion-card">
                        <div className="seccion-header">
                          <span className="seccion-icono">🛠️</span>
                          <h4>Servicios</h4>
                        </div>

                        <table className="service-table service-table-mini">
                          <thead>
                            <tr>
                              <th>Servicio</th>
                              <th>Responsable</th>
                              <th>Cant.</th>
                              <th>Duración</th>
                              <th>Valor</th>
                              <th>Subtotal</th>
                            </tr>
                          </thead>
                          <tbody>
                            {serviciosRegistrados.map((s, index) => (
                              <tr key={index}>
                                <td style={{ textAlign: "left" }}>{s.nombre_servicio}</td>
                                <td>{s.nombre_veterinario}</td>
                                <td>{s.cantidad}</td>
                                <td>{s.duracion_total} min</td>
                                <td>S/{s.valor_servicio.toFixed(2)}</td>
                                <td>S/{s.subtotal.toFixed(2)}</td>
                              </tr>
                            ))}
                          </tbody>
                        </table>

                        <div className="resumen-totales">
                          <div><span>Total Servicios:</span><strong>S/{totalCosto.toFixed(2)}</strong></div>
                          <div><span>Adelanto:</span><strong>S/{bonoTemporal.toFixed(2)}</strong></div>
                          <div className="pendiente"><span>Pendiente de Pago:</span><strong>S/{Math.max(0, totalCosto - bonoTemporal).toFixed(2)}</strong></div>
                        </div>
                      </div>

                      <div className="seccion-card">
                        <div className="seccion-header">
                          <span className="seccion-icono">🏠</span>
                          <h4>Sala asignada</h4>
                        </div>

                        {requiereSalaActual ? (
                          salaSeleccionada ? (
                            <div className="card-sala libre sala-resumen">
                              <div className="emoji">🐶</div>
                              <strong>{nuevoEvento.nombreSala}</strong>
                              <p><span className="dot verde"></span>Disponible</p>
                            </div>
                          ) : (
                            <p style={{ padding: "10px" }}>No se ha seleccionado ninguna sala.</p>
                          )
                        ) : (
                          <p style={{ padding: "10px" }}>Este servicio no requiere asignación de sala.</p>
                        )}
                      </div>

                    </div>

                    <div className="acciones-wizard">
                      <button
                        type="button"
                        className="btn-anterior"
                        onClick={() => setPasoActual(2)}
                      >
                        ← Anterior
                      </button>

                      <button
                        type="button"
                        className="btn-siguiente"
                        onClick={guardarEvento}
                      >
                        Confirmar cita ✓
                      </button>
                    </div>
                  </>
                )}
              </>
            ) : (
              // 🆕 PANTALLA DE ÉXITO
              <div className="exito-container">
                <div className="exito-icono-wrapper">
                  <span className="confeti c1">🎉</span>
                  <span className="confeti c2">✨</span>
                  <span className="confeti c3">🎊</span>
                  <span className="confeti c4">✨</span>
                  <div className="exito-check">✓</div>
                  <span className="exito-mascota">🐶</span>
                </div>

                <h3 className="exito-titulo">¡Guardado con éxito!</h3>
                <p className="exito-subtitulo">La cita ha sido registrada correctamente.</p>

                {datosResumenExito && (
                  <div className="exito-detalle-box">
                    <h5>Detalles de la cita</h5>
                    <div className="exito-detalle-grid">
                      <div><span>Código de cita:</span><strong>{datosResumenExito.codigo}</strong></div>
                      <div><span>Sala:</span><strong>{datosResumenExito.sala}</strong></div>
                      <div><span>Cliente:</span><strong>{datosResumenExito.cliente}</strong></div>
                      <div><span>Servicios:</span><strong>{datosResumenExito.cantidadServicios} servicio(s)</strong></div>
                      <div><span>Mascota:</span><strong>{datosResumenExito.mascota}</strong></div>
                      <div><span>Total Servicios:</span><strong>S/{datosResumenExito.totalServicios.toFixed(2)}</strong></div>
                      <div><span>Fecha:</span><strong>{formatearFecha(datosResumenExito.fecha)}</strong></div>
                      <div><span>Adelanto:</span><strong>S/{datosResumenExito.adelanto.toFixed(2)}</strong></div>
                      <div><span>Hora:</span><strong>{datosResumenExito.hora}</strong></div>
                      <div><span>Pendiente de Pago:</span><strong>S/{datosResumenExito.pendiente.toFixed(2)}</strong></div>
                      <div><span>Estado:</span><strong className="badge-estado">{datosResumenExito.estado}</strong></div>
                      <div><span>Observaciones:</span><strong>{datosResumenExito.observaciones}</strong></div>
                    </div>
                  </div>
                )}

                <button
                  type="button"
                  className="btn-siguiente btn-ok-exito"
                  onClick={resetModalCompleto}
                >
                  ✓ OK
                </button>
              </div>
            )}

          </div>
        </div>
      )}
    </div>
  );
}

export default Agenda_general;
