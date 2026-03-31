import { useState, useEffect, useMemo } from "react";
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import "./EditarCita.css";
import IST from "../../../../components/proteccion/IST";
import axios from "axios"; 

// 🚨 Nuevas Constantes y Declaraciones de Google Calendar
const CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;
const API_KEY = import.meta.env.VITE_GOOGLE_API_KEY;

declare global {
    interface Window {
        google: any;
        gapi: any;
    }
}

// =======================================================
// INTERFACES
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
    idGoogleCalendar?: string; 
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
    idCliente: number;
}

// 🚨 Interfaces de Servicios
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

interface ServicioBase {
    id: number;
    nombre: string;
    duracion: number;
    precio: number;
}


// --- CONSTANTES DEL BACKEND Y ESTADOS TERMINALES ---
const ESTADOS_NO_EDITABLES = ['CANCELADA', 'ATENDIDA'];
const ID_MEDIO_SOLICITUD_DEFAULT = 4;

const CLIENTES_SIMULADOS: EntityBase[] = []; 
const MASCOTAS_SIMULADAS: MascotaBase[] = []; 
const ESTADOS_AGENDA_SIMULADOS: EstadoAgenda[] = []; 
const COLABORADORES_SIMULADOS: EntityBase[] = []; 

// --- FUNCIÓN AUXILIAR: Obtiene detalles ---
const getDetallesMascotaCliente = (cita: CitaBD, clientes: EntityBase[], mascotas: MascotaBase[], estadosAgenda: EstadoAgenda[], colaboradores: EntityBase[]) => {
    const cliente = clientes.find(c => c.id === cita.idCliente);
    const mascota = mascotas.find(m => m.id === cita.idMascota);
    const colaborador = colaboradores.find(c => c.id === cita.idColaborador); 
    
    const estadoNombre = (estadosAgenda.find(e => e.id === cita.idEstado))?.nombre?.toUpperCase() || 'N/A';

    return {
        clienteNombre: cliente?.nombre || `Cliente ID ${cita.idCliente}`,
        dni: cliente?.documento || 'N/A',
        mascotaNombre: mascota?.nombre || `Mascota ID ${cita.idMascota}`,
        colaboradorNombre: colaborador?.nombre || 'No Asignado',
        estadoNombre: estadoNombre,
    };
};

// 🚨 FUNCIÓN CORREGIDA: Ahora pide MASCOTA para evitar confusión de citas dobles
const buscarIdGoogleCalendar = async (cita: CitaBD, cliente: EntityBase, mascota: MascotaBase): Promise<string | undefined> => {
    if (!window.gapi || !window.gapi.client || !window.gapi.client.calendar) {
        return undefined; 
    }
    
    const startOfDay = new Date(`${cita.fecha}T00:00:00`).toISOString();
    const endOfDay = new Date(`${cita.fecha}T23:59:59`).toISOString();
    
    let queryTerm = cliente.documento || cliente.nombre || "";
    if (!queryTerm) return undefined;
    
    try {
        const res = await window.gapi.client.calendar.events.list({
            calendarId: "primary",
            timeMin: startOfDay,
            timeMax: endOfDay,
            q: queryTerm,
            singleEvents: true,
            maxResults: 50,
        });
        
        const eventosEncontrados = res.result.items;
        if (!eventosEncontrados || eventosEncontrados.length === 0) return undefined;

        const eventoCorrecto = eventosEncontrados.find((event: any) => {
            if (event.description && event.description.includes(`[REF_ID:${cita.id}]`)) {
                return true;
            }

            if (event.start && event.start.dateTime) {
                const eventDate = new Date(event.start.dateTime);
                const [citaHora, citaMin] = cita.hora.split(':').map(Number);
                const coincideHora = eventDate.getHours() === citaHora;
                const coincideMin = Math.abs(eventDate.getMinutes() - citaMin) <= 2;

                if (coincideHora && coincideMin) {
                    const nombreMascota = mascota.nombre.toLowerCase();
                    const tituloEvento = (event.summary || "").toLowerCase();
                    const descEvento = (event.description || "").toLowerCase();
                    const coincideMascota = tituloEvento.includes(nombreMascota) || descEvento.includes(nombreMascota);
                    return coincideMascota;
                }
            }
            return false;
        });

        return eventoCorrecto ? eventoCorrecto.id : undefined;

    } catch (e) {
        console.warn("Error en búsqueda GC con query:", queryTerm, e);
        return undefined;
    }
};

// 🚨 FUNCIÓN CORREGIDA (TypeScript Safe)
const parsearServiciosGC = (
    description: string, 
    colaboradores: EntityBase[], 
    serviciosDisponibles: ServicioBase[]
): ServicioDetalle[] => {

    if (!description) return [];

    const regexPrincipal = /\*\*SERVICIOS REGISTRADOS\*\*((.|\n)*?)\*\*ADELANTO/i;
    const regexAlternativo = /\*\*SERVICIOS REGISTRADOS\*\*((.|\n)*)/i;

    let textoServicios = "";
    const matchPrincipal = description.match(regexPrincipal);
    
    if (matchPrincipal && matchPrincipal[1]) {
        textoServicios = matchPrincipal[1];
    } else {
        const matchAlternativo = description.match(regexAlternativo);
        if (matchAlternativo && matchAlternativo[1]) {
            textoServicios = matchAlternativo[1];
        } else {
            return [];
        }
    }

    const lineas = textoServicios.trim().split('\n').filter(l => l.trim().startsWith('•'));
    const resultados: ServicioDetalle[] = [];

    lineas.forEach(linea => {

        const match = linea.match(/•\s*(.*?)\s*\((\d+)x\s*(?:S\/|\$)\s*(\d+(\.\d+)?)\)\s*Subtotal:\s*(?:S\/|\$)\s*(\d+(\.\d+)?)\s*con\s*(.*?)\.\s*Adicionales:\s*(.*)/i);

        if (match) {

            const nombreServicioRaw = match[1].trim();
            const cantidad = parseInt(match[2]);
            const precio = parseFloat(match[3]);
            const subtotal = parseFloat(match[5]);
            const nombreVetRaw = match[7].trim();
            const adicionalesRaw = match[8].trim();

            const servicioBase = serviciosDisponibles.find(
                s => s.nombre.trim().toLowerCase() === nombreServicioRaw.toLowerCase()
            );

            const veterinario = colaboradores.find(
                c => c.nombre.trim().toLowerCase() === nombreVetRaw.toLowerCase()
            );

            const idServicio = servicioBase ? servicioBase.id : 0;
            const idVeterinario = veterinario ? veterinario.id : (colaboradores[0]?.id || 0);

            // 🔥 AQUÍ ESTÁ LA CLAVE
            // usamos la duración BASE SOLO como referencia
            const duracionBase = servicioBase?.duracion || 0;

            // 🧠 si el usuario modificó duración, mantenemos la proporción
            let duracionCalculada = duracionBase;

            if (cantidad > 0 && duracionBase > 0) {
                duracionCalculada = duracionBase; // base por defecto
            }

            resultados.push({
                id_servicio: idServicio,
                nombre_servicio: nombreServicioRaw,
                id_veterinario: idVeterinario,
                nombre_veterinario: nombreVetRaw,
                cantidad: cantidad,
                valor_servicio: precio,
                bono_inicial: 0,

                // ✅ IMPORTANTE: NO forzar 30 ni 0
                duracion_min: duracionCalculada,
                duracion_total: duracionCalculada * cantidad,

                subtotal: subtotal,
                adicionales: adicionalesRaw === 'N/A' ? '' : adicionalesRaw,
            });
        }
    });

    return resultados;
};


function EditarCita() {
    const [minimizado, setMinimizado] = useState(false);
    const [status, setStatus] = useState("🟡 Inicializando...");
    
    // 🚨 ESTADOS DE GOOGLE CALENDAR
    const [isSignedIn, setIsSignedIn] = useState(false);
    const [tokenClient, setTokenClient] = useState<any>(null);
    const [gapiInited, setGapiInited] = useState(false);
    const [gisInited, setGisInited] = useState(false);
    const [editingGCId, setEditingGCId] = useState<string | undefined>(undefined); 

    const [eventos, setEventos] = useState<CitaBD[]>([]);
    const [eventosFiltrados, setEventosFiltrados] = useState<CitaBD[]>([]);
    const [mostrarModal, setMostrarModal] = useState(false);
    
    const [editandoCita, setEditandoCita] = useState<CitaBD | null>(null); 
    
    const [busqueda, setBusqueda] = useState("");
    const [filtroFecha, setFiltroFecha] = useState("");
    
    const [filtroEstadoId, setFiltroEstadoId] = useState<number>(0); 

    const [clientes, setClientes] = useState<EntityBase[]>(CLIENTES_SIMULADOS);
    const [mascotas, setMascotas] = useState<MascotaBase[]>(MASCOTAS_SIMULADAS);
    const [colaboradores, setColaboradores] = useState<EntityBase[]>(COLABORADORES_SIMULADOS);
    const [estadosAgenda, setEstadosAgenda] = useState<EstadoAgenda[]>(ESTADOS_AGENDA_SIMULADOS);
    
    // 🚨 ESTADOS DE SERVICIOS DINÁMICOS
   const [serviciosRegistrados, setServiciosRegistrados] = useState<ServicioDetalle[]>([]);
const [editIndexServicio, setEditIndexServicio] = useState<number | null>(null);
const [abonoEditable, setAbonoEditable] = useState(0);
const [serviciosDisponibles, setServiciosDisponibles] = useState<ServicioBase[]>([]); 

const [servicioTemporal, setServicioTemporal] = useState({
    id_servicio: '',
    valor_servicio: 0,
    cantidad: 1,
    duracion_min: 0,
    id_veterinario: '',
    adicionales: '',
});

const [nuevoEvento, setNuevoEvento] = useState({
    dni: "",
    cliente: "",
    clienteId: 0,
    mascotaId: 0, 
    date: new Date().toISOString().split("T")[0],
    startTime: "10:00",
    estado: "",
    duracionEstimadaMin: 30, 
    abonoInicial: 0,
    observaciones: "", 
    colaboradorId: 0, 
    colaboradorNombre: "",
});

const idsEstadosTerminales = useMemo(() => {
    return estadosAgenda
        .filter(e => ESTADOS_NO_EDITABLES.includes(e.nombre))
        .map(e => e.id);
}, [estadosAgenda]);

// ✅ SOLO UNA VEZ (para usar en todo el componente)
const totalDuracion = serviciosRegistrados.reduce(
    (sum, s) => sum + s.duracion_total,
    0
);

const totalCosto = serviciosRegistrados.reduce(
    (sum, s) => sum + s.subtotal,
    0
);

// =======================================================
// ✅ EFECTO: ACTUALIZAR DURACIÓN ESTIMADA
// =======================================================
useEffect(() => {
    setNuevoEvento(prev => ({
        ...prev,
        duracionEstimadaMin: totalDuracion
    }));
}, [totalDuracion]);

// =======================================================
// ✅ AUTOCARGAR PRECIO Y DURACIÓN
// =======================================================
useEffect(() => {
    // 🚫 NO ejecutar si estás editando
    if (editIndexServicio !== null) return;

    const serviceId = parseInt(servicioTemporal.id_servicio as string);

    if (!serviceId || isNaN(serviceId)) return;

    const s = serviciosDisponibles.find(s => s.id === serviceId);

    // 🔥 evita bugs
    if (!s) {
        console.warn("Servicio no encontrado:", serviceId);
        return;
    }

    setServicioTemporal(prev => ({
        ...prev,
        valor_servicio: s.precio,

        // ✅ nunca queda en 0
        duracion_min: prev.duracion_min === 0
            ? (s.duracion ?? 1)
            : prev.duracion_min,
    }));

}, [servicioTemporal.id_servicio, serviciosDisponibles, editIndexServicio]);

// =======================================================
// ✅ HANDLE CAMBIO SERVICIO
// =======================================================
const handleServicioTemporalChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedId = parseInt(e.target.value);

    if (!selectedId || isNaN(selectedId)) {
        setServicioTemporal(prev => ({
            ...prev,
            id_servicio: '',
            valor_servicio: 0,
            duracion_min: 0,
        }));
        return;
    }

    const servicio = serviciosDisponibles.find(s => s.id === selectedId);

    setServicioTemporal(prev => ({
        ...prev,
        id_servicio: e.target.value,
        valor_servicio: servicio?.precio || 0,

        // ✅ nunca 0 por error
        duracion_min: prev.duracion_min === 0
            ? (servicio?.duracion ?? 1)
            : prev.duracion_min,
    }));
};
  const agregarServicio = () => {
    const sId = parseInt(servicioTemporal.id_servicio as string);
    const vId = parseInt(servicioTemporal.id_veterinario as string);

    // 1. Validaciones de seguridad
    if (!sId || isNaN(sId)) return alert("Seleccione un servicio válido");
    if (!vId || isNaN(vId)) return alert("Seleccione un veterinario");

    const servicioInfo = serviciosDisponibles.find(s => s.id === sId);
    const veterinarioInfo = colaboradores.find(v => v.id === vId);

    // Verificamos que existan en las listas maestras
    if (!servicioInfo || !veterinarioInfo) return alert("Error al obtener información del servicio o veterinario");

    const nuevoServicio: ServicioDetalle = {
        id_servicio: sId,
        nombre_servicio: servicioInfo.nombre,
        id_veterinario: vId,
        nombre_veterinario: veterinarioInfo.nombre,
        cantidad: servicioTemporal.cantidad,
        valor_servicio: servicioTemporal.valor_servicio,
        bono_inicial: 0,
        duracion_min: servicioTemporal.duracion_min,
        duracion_total: Number(servicioTemporal.duracion_min) * Number(servicioTemporal.cantidad),
        subtotal: servicioTemporal.valor_servicio * servicioTemporal.cantidad,
        adicionales: servicioTemporal.adicionales,
    };

    // 2. Lógica de Guardado (Edición vs Nuevo)
    if (editIndexServicio !== null) {
        const copia = [...serviciosRegistrados];
        copia[editIndexServicio] = nuevoServicio;
        setServiciosRegistrados(copia);
    } else {
        setServiciosRegistrados(prev => [...prev, nuevoServicio]);
    }

    // 3. LIMPIEZA TOTAL (Fuera de los ifs para que siempre ocurra)
    setEditIndexServicio(null); // Fundamental para que el useEffect de precios vuelva a activarse
    setServicioTemporal({
        id_servicio: '',
        valor_servicio: 0,
        cantidad: 1,
        duracion_min: 0,
        id_veterinario: '',
        adicionales: '',
    });
};
    // 1. Función para SELECCIONAR (cargar los datos en los inputs)
const seleccionarServicio = (index: number) => {
    const s = serviciosRegistrados[index];

    // 🔥 PRIMERO activar modo edición
    setEditIndexServicio(index);

    // 🔥 SOLO UN setServicioTemporal (sin limpiar antes)
    setServicioTemporal({
        id_servicio: s.id_servicio.toString(),
        valor_servicio: s.valor_servicio,
        cantidad: s.cantidad,
        duracion_min: s.duracion_min, // ✅ respeta lo que ya guardaste
        id_veterinario: s.id_veterinario.toString(),
        adicionales: s.adicionales,
    });
};

// 2. Función para ELIMINAR (quitar de la lista definitivamente)
const eliminarServicio = (index: number) => {
    setServiciosRegistrados(prev => prev.filter((_, i) => i !== index));
};


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
                scope: "https://www.googleapis.com/auth/calendar.events",
                callback: (tokenResponse: any) => {
                    if (tokenResponse.access_token) {
                        window.gapi.client.setToken({ access_token: tokenResponse.access_token });
                        
                        // 🟢 GUARDAMOS TOKEN Y FECHA DE EXPIRACIÓN (1 Hora aprox)
                        const expiresIn = tokenResponse.expires_in || 3599; // Segundos
                        const expirationTime = Date.now() + (expiresIn * 1000);
                        
                        localStorage.setItem("google_token", tokenResponse.access_token);
                        localStorage.setItem("google_token_expires", expirationTime.toString());

                        setIsSignedIn(true);
                        setStatus("✅ Google Calendar autenticado.");
                    }
                },
            });
            setTokenClient(client);
            setGisInited(true);
        };
        document.body.appendChild(script);
    }, []);

    useEffect(() => {
        if (gapiInited && gisInited) setStatus("🔌 Google Calendar listo.");
    }, [gapiInited, gisInited]);

    // 🟢 VERIFICACIÓN DE SESIÓN AL CARGAR LA PÁGINA
    useEffect(() => {
        if (!gapiInited || !gisInited) return;
        
        const savedToken = localStorage.getItem("google_token");
        const savedExpiry = localStorage.getItem("google_token_expires");

        if (savedToken && savedExpiry) {
            // Verificamos si la fecha actual es MENOR a la fecha de expiración
            if (Date.now() < parseInt(savedExpiry)) {
                window.gapi.client.setToken({ access_token: savedToken });
                setIsSignedIn(true);
                setStatus("🔓 Google Calendar sesión restaurada.");
            } else {
                // Si ya expiró, limpiamos todo y obligamos a loguear
                console.warn("Token expirado, cerrando sesión local.");
                localStorage.removeItem("google_token_editor");
                localStorage.removeItem("google_token_expires");
                setIsSignedIn(false);
                setStatus("⚠️ Sesión caducada. Inicie sesión nuevamente.");
            }
        }
    }, [gapiInited, gisInited]);

    const iniciarSesion = () => tokenClient?.requestAccessToken();
    
    const cerrarSesion = () => {
        const token = window.gapi.client.getToken();
        if (token) window.google.accounts.oauth2.revoke(token.access_token);
        window.gapi.client.setToken(null);
        localStorage.removeItem("google_token_editor");
        localStorage.removeItem("google_token_expires");
        setIsSignedIn(false);
        setStatus("🚪 Google Calendar sesión cerrada.");
    };
    
    // 🟢 FUNCIÓN DE VALIDACIÓN PARA USAR ANTES DE EDITAR/BORRAR
    const checkTokenValidity = () => {
        const savedExpiry = localStorage.getItem("google_token_expires");
        if (!savedExpiry || Date.now() > parseInt(savedExpiry)) {
            alert("⌛ Su sesión de Google ha caducado. Por favor, inicie sesión nuevamente.");
            cerrarSesion(); // Esto limpiará el estado y mostrará el botón de login
            return false;
        }
        return true;
    };


    // ================== CARGA INICIAL DE DATOS MAESTROS Y CITAS ==================
    const fetchCitas = async () => {
        try {
            const [resClientes, resColaboradores, resMascotas, resEstados, resCitas, resServicios] = await Promise.all([
                IST.get("/clientes"),
                IST.get("/colaboradores"),
                IST.get("/mascotas"),
                IST.get("/estados-agenda"),
                IST.get("/agenda?page=0&size=100"), 
                IST.get("/servicios"),
            ]);

            const listaClientes: EntityBase[] = resClientes.data.data.filter((c: any) => c.activo) || [];
            setClientes(listaClientes);

            const listaColaboradores: EntityBase[] = resColaboradores.data.data || [];
            setColaboradores(listaColaboradores);

            const listaMascotas: MascotaBase[] = resMascotas.data.data || [];
            setMascotas(listaMascotas);

            const estadosData = Array.isArray(resEstados.data) ? resEstados.data : resEstados.data.data;
            const estadosParseados = estadosData.map((e: any) => ({ id: e.id, nombre: e.nombre.toUpperCase() }));
            setEstadosAgenda(estadosParseados);

            const serviciosData = Array.isArray(resServicios.data) ? resServicios.data : resServicios.data.data;
            const serviciosParseados = serviciosData.map((s: any) => ({
                ...s,
                duracion: parseInt(s.duracion) || 0,
                precio: parseFloat(s.precio) || 0,
            }));
            setServiciosDisponibles(serviciosParseados);
            
            const citasObtenidas: CitaBD[] = resCitas.data.data.content || [];
            setEventos(citasObtenidas);
            
            setStatus(`✅ ${citasObtenidas.length} citas cargadas.`);

        } catch (error) {
               console.error("Error al cargar datos de la BD:", error);
               setStatus("⚠️ Error al cargar datos. Verifique endpoints.");
        }
    };
    
    useEffect(() => {
        fetchCitas();
    }, []); 


    // ================== FUNCIONES DE FILTRADO UNIFICADO ==================
    useEffect(() => {
        let filtrados = eventos;

        if (busqueda) {
            const busquedaStr = busqueda.trim().toLowerCase();
            filtrados = filtrados.filter((e) => {
                const cliente = clientes.find(c => c.id === e.idCliente);
                const mascota = mascotas.find(m => m.id === e.idMascota);

                const enDNI = cliente?.documento?.includes(busquedaStr);
                const enCliente = cliente?.nombre?.toLowerCase().includes(busquedaStr);
                const enMascota = mascota?.nombre?.toLowerCase().includes(busquedaStr);
                const enObservaciones = e.observaciones?.toLowerCase().includes(busquedaStr);
                
                return enDNI || enCliente || enMascota || enObservaciones;
            });
        }

        if (filtroFecha) {
            filtrados = filtrados.filter((e) => e.fecha === filtroFecha);
        }

        if (filtroEstadoId === 0) {
            if (idsEstadosTerminales.length > 0) {
                 filtrados = filtrados.filter(e => !idsEstadosTerminales.includes(e.idEstado));
            }
        } else if (filtroEstadoId > 0) {
            filtrados = filtrados.filter(e => e.idEstado === filtroEstadoId);
        }

        setEventosFiltrados(filtrados);
    }, [busqueda, filtroFecha, filtroEstadoId, eventos, clientes, mascotas, idsEstadosTerminales]);


    // ================== GESTIÓN DE MODAL Y EDICIÓN ==================

    const resetModalState = () => {
    setMostrarModal(false);
    setEditandoCita(null);
    setEditingGCId(undefined);
    setServiciosRegistrados([]);

    // 🧹 LIMPIEZA NUEVA
    setServicioTemporal({
        id_servicio: '',
        valor_servicio: 0,
        cantidad: 1,
        duracion_min: 0,
        id_veterinario: '',
        adicionales: '',
    });

    setEditIndexServicio(null);

    setNuevoEvento({
        dni: "",
        cliente: "",
        clienteId: 0,
        mascotaId: 0,
        date: new Date().toISOString().split("T")[0],
        startTime: "10:00",
        estado: "",
        duracionEstimadaMin: 30,
        abonoInicial: 0,
        observaciones: "",
        colaboradorId: 0,
        colaboradorNombre: "",
    });
};
    
    // 🚨 FIX 2: INTEGRACIÓN DEL PARSEADOR EN LA EDICIÓN
    const editarEvento = async (cita: CitaBD) => { 
        // 🔒 BLOQUEO DE SEGURIDAD: Si no está logueado en GC, no permite abrir
        if (!isSignedIn) {
            alert("🔒 Por seguridad, debe iniciar sesión en Google Calendar para editar citas.");
            return;
        }

        // 🟢 VALIDACIÓN DE CADUCIDAD DEL TOKEN
        if (!checkTokenValidity()) return;

        const estado = estadosAgenda.find(e => e.id === cita.idEstado);
        const estadoNombre = estado?.nombre.toUpperCase() || 'N/A';
        
        if (ESTADOS_NO_EDITABLES.includes(estadoNombre)) {
            alert(`🚫 No se puede editar la cita ${cita.codigo} porque su estado es ${estadoNombre}.`);
            return; 
        }

        const cliente = clientes.find(c => c.id === cita.idCliente);
        const mascota = mascotas.find(m => m.id === cita.idMascota); // Necesitamos la mascota ahora
        const colaboradorAsignado = colaboradores.find(c => c.id === cita.idColaborador) || colaboradores[0];
        
        setEditandoCita(cita);
        setAbonoEditable(cita.abonoInicial || 0);
        
        // Inicializamos observaciones y limpiamos servicios previos
        let fetchedObservaciones = cita.observaciones;
        setServiciosRegistrados([]); 

        if (isSignedIn && cliente && mascota) {
            setStatus("🟡 Buscando datos en Google Calendar...");
            
            // 🚨 AHORA PASAMOS LA MASCOTA PARA DIFERENCIAR LAS CITAS
            let gcEventId = cita.idGoogleCalendar || await buscarIdGoogleCalendar(cita, cliente, mascota); 

            if (gcEventId) {
                try {
                    const res = await window.gapi.client.calendar.events.get({
                        calendarId: "primary",
                        eventId: gcEventId
                    });
                    
                    const descriptionGC = res.result.description || "";
                    
                    const serviciosRecuperados = parsearServiciosGC(descriptionGC, colaboradores, serviciosDisponibles); 

                    if (serviciosRecuperados.length > 0) {
                        console.log(serviciosRecuperados);
                         setServiciosRegistrados(serviciosRecuperados);
                         setStatus("✅ Servicios recuperados de Google Calendar.");
                    } else {
                         setStatus("⚠️ Evento encontrado en GC, pero no se pudieron leer los servicios.");
                    }

                    setEditingGCId(gcEventId);

                } catch (gcError) {
                    console.error("Error al obtener detalles del evento GC:", gcError);
                    setStatus("⚠️ Cita cargada. La sincronización de GC falló.");
                    setEditingGCId(gcEventId);
                }
            } else {
                setEditingGCId(undefined);
                setStatus("⚠️ Cita cargada. No se encontró ID de GC para esta cita.");
            }
        } else {
             setEditingGCId(undefined);
        }

        setNuevoEvento({
            dni: cliente?.documento || "",
            cliente: cliente?.nombre || "",
            clienteId: cita.idCliente,
            mascotaId: cita.idMascota, 
            date: cita.fecha,
            startTime: cita.hora.substring(0, 5), // HH:mm
            estado: estadoNombre,
            duracionEstimadaMin: cita.duracionEstimadaMin,
            abonoInicial: cita.abonoInicial, 
            observaciones: fetchedObservaciones || "", 
            colaboradorId: colaboradorAsignado?.id || 0,
            colaboradorNombre: colaboradorAsignado?.nombre || "",
        });

        setMostrarModal(true);
    };

    const handleMascotaChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const selectedMascotaId = parseInt(e.target.value);
        
        setNuevoEvento({ 
            ...nuevoEvento, 
            mascotaId: selectedMascotaId || 0
        });
    };

    const mascotasDisponibles = useMemo(() => {
        const mascotasCliente = mascotas.filter(m => m.idCliente === nuevoEvento.clienteId);
        
        const currentMascota = mascotas.find(m => m.id === nuevoEvento.mascotaId);
        
        if (currentMascota && !mascotasCliente.some(m => m.id === currentMascota.id)) {
            return [...mascotasCliente, currentMascota];
        }

        return mascotasCliente;
    }, [mascotas, nuevoEvento.clienteId, nuevoEvento.mascotaId]);


    const eliminarEvento = async (id: number) => {
        // 🔒 BLOQUEO: También impedimos cancelar si no está logueado
        if (!isSignedIn) {
            alert("🔒 Debe iniciar sesión en Google Calendar para cancelar citas.");
            return;
        }

        // 🟢 VALIDACIÓN DE CADUCIDAD DEL TOKEN
        if (!checkTokenValidity()) return;

        if (!confirm("¿Marcar esta cita como CANCELADA?")) return;

        try {
            const citaActual = eventos.find(e => e.id === id);
            if (!citaActual) {
                alert("Cita no encontrada en la lista.");
                return;
            }

            const estadoCancelado = estadosAgenda.find(e => e.nombre === 'CANCELADA');
            if (!estadoCancelado) {
                alert("No se encontró el estado 'CANCELADA'.");
                return;
            }

            const req = {
                ...citaActual,
                idEstado: estadoCancelado.id, 
            };

            const responseDB = await IST.put("/agenda/actualizar", req);

            if (responseDB.data.success) {
                setStatus(`🗑️ Cita ${citaActual.codigo} CANCELADA.`);
                
                if (citaActual.idGoogleCalendar && isSignedIn) {
                    try {
                        await window.gapi.client.calendar.events.delete({
                            calendarId: "primary",
                            eventId: citaActual.idGoogleCalendar
                        });
                        setStatus(`🗑️ Cita ${citaActual.codigo} CANCELADA y eliminada de Google Calendar.`);
                    } catch (gcError) {
                        console.error("Error al eliminar GC:", gcError);
                        alert("Cita cancelada en BD, pero falló la eliminación en Google Calendar.");
                    }
                }
                
                fetchCitas(); 
            } else {
                alert(`Error al cancelar: ${responseDB.data.message}`);
            }
            
        } catch (error) {
            console.error("Error al eliminar/cancelar cita:", error);
            setStatus("⚠️ Error al cancelar la cita");
        }
    };

    // ================== GUARDAR EVENTO (FUNCIÓN CENTRAL) ==================
    const guardarEvento = async () => {
        if (!editandoCita) {
            return alert("🚫 ERROR: No se encontró el ID de la cita para actualizar.");
        }
        
        // 🟢 VALIDACIÓN DE CADUCIDAD DEL TOKEN
        if (isSignedIn && !checkTokenValidity()) return;

        if (nuevoEvento.mascotaId === 0) return alert("Debe seleccionar una mascota.");
        if (nuevoEvento.colaboradorId === 0) return alert("Debe seleccionar un colaborador."); 
        if (nuevoEvento.duracionEstimadaMin <= 0) return alert("La duración estimada debe ser mayor a 0 (Agregue servicios).");
        
        const totalMinimoNecesario = totalCosto || editandoCita.totalCita || 0;
        const duracionFinal = totalDuracion || nuevoEvento.duracionEstimadaMin;
        
        try {
            const citaEditada: CitaBD = editandoCita; 

            const estadoEncontrado = estadosAgenda.find(e => e.nombre === nuevoEvento.estado.toUpperCase());
            const idEstado = estadoEncontrado ? estadoEncontrado.id : citaEditada.idEstado; 
            
            if (ESTADOS_NO_EDITABLES.includes(citaEditada.codigo) && idEstado !== citaEditada.idEstado) {
                 if (!confirm(`La cita estaba en estado terminal. ¿Estás seguro de que quieres cambiar el estado a ${nuevoEvento.estado}?`)) {
                    return;
                }
            }

            const AgendaRequestDTO = {
                id: citaEditada.id,
                idCliente: nuevoEvento.clienteId,
                idMascota: nuevoEvento.mascotaId, 
                idMedioSolicitud: citaEditada.idMedioSolicitud || ID_MEDIO_SOLICITUD_DEFAULT, 
                
                totalCita: totalMinimoNecesario, 
                abonoInicial: abonoEditable,
                
                idColaborador: nuevoEvento.colaboradorId,

                fecha: nuevoEvento.date,
                hora: nuevoEvento.startTime + ":00", 
                duracionEstimadaMin: duracionFinal, 
                idEstado: idEstado,
                observaciones: nuevoEvento.observaciones,
                
                idGoogleCalendar: editingGCId || citaEditada.idGoogleCalendar || null 
            };
            
            const responseDB = await IST.put("/agenda/actualizar", AgendaRequestDTO);
            
            if (!responseDB.data.success) {
                throw new Error(`Error BD: ${responseDB.data.message}`);
            }
            
            setStatus(`✏️ Cita ${citaEditada.codigo} actualizada exitosamente.`);
            
            if (isSignedIn && editingGCId) {
                try { 
                    const cliente = clientes.find(c => c.id === nuevoEvento.clienteId);
                    const mascota = mascotas.find(m => m.id === nuevoEvento.mascotaId);
                    
                    const newStart = new Date(`${nuevoEvento.date}T${nuevoEvento.startTime}`);
                    const newEnd = new Date(newStart.getTime() + duracionFinal * 60000);

                    // 🚨 Reconstrucción del texto de servicios
                    const serviciosListaGC = serviciosRegistrados.map(s =>
                        `• ${s.nombre_servicio} (${s.cantidad}x S/ ${s.valor_servicio.toFixed(2)})  Subtotal: S/ ${s.subtotal.toFixed(2)} con ${s.nombre_veterinario}. Adicionales: ${s.adicionales || 'N/A'}`
                    ).join('\n');
                    
                    const updatedEventResource = {
                        summary: `${mascota?.nombre || 'Mascota'} - Total: S/${totalMinimoNecesario.toFixed(2)}`, 
                        description: 
                            `**CLIENTE Y MASCOTA**\n` + 
                            `Cliente: ${cliente?.nombre} (DNI: ${cliente?.documento})\n` +
                            `Mascota: ${mascota?.nombre}\n` +
                            `Estado: ${nuevoEvento.estado}\n` + 
                            `Costo Total: S/${totalCosto.toFixed(2)}\n` + 
                            `Duración Total: ${duracionFinal} min\n\n` + 
                            `**SERVICIOS REGISTRADOS**\n${serviciosListaGC}\n\n` + 
                            `**ADELANTO/ABONO:** S/${citaEditada.abonoInicial.toFixed(2)}\n\n` + 
                            `**OBSERVACIONES**\n${nuevoEvento.observaciones || 'No hay observaciones adicionales.'}\n\n` +
                            `--------------------------------\n` +
                            `[REF_ID:${citaEditada.id}]`.trim(),
                        
                        start: { dateTime: newStart.toISOString(), timeZone: "America/Lima" }, 
                        end: { dateTime: newEnd.toISOString(), timeZone: "America/Lima" }, 
                    };
                    
                    await window.gapi.client.calendar.events.update({
                        calendarId: "primary",
                        eventId: editingGCId,
                        resource: updatedEventResource
                    });

                    setStatus(`✏️ Cita ${citaEditada.codigo} actualizada en BD y Google Calendar.`);

                } catch (gcError: any) {
                    let gcErrorMessage = "Error desconocido.";
                    if (gcError.result && gcError.result.error) {
                        const errorObj = gcError.result.error;
                        gcErrorMessage = `Error GC ${errorObj.code}: ${errorObj.message}`;
                    }
                    console.error("Error al actualizar Google Calendar:", gcError);
                    alert(`⚠️ Cita actualizada en BD. Falló la sincronización con Google Calendar. ${gcErrorMessage}.`);
                    setStatus(`✏️ Cita ${citaEditada.codigo} actualizada en BD. ⚠️ Sincronización GC fallida.`);
                }
            
            } else {
                setStatus(`✏️ Cita ${citaEditada.codigo} actualizada exitosamente en BD. (GC ID no encontrado/Sesión inactiva)`);
            }

        } catch (error: any) {
            let errorMessage = "⚠️ Error al guardar la cita. Verifique el formato de datos.";
            
            if (axios.isAxiosError(error) && error.response) {
                 errorMessage += ` Detalle API: ${error.response.data.message || 'Error de conexión'}`;
            } else if (error instanceof Error) {
                 errorMessage += ` Detalle: ${error.message}`;
            }
            
            console.error("Error al guardar cita:", error);
            alert(errorMessage);
            return;
        }

        resetModalState();
        fetchCitas();
    };


    // ================== RENDER ==================
    return (
        <div id="editarita">
            <Br_administrativa onMinimizeChange={setMinimizado} />

            <main className={minimizado ? "minimize" : ""}>
                <section className="editarita-container">
                    <h2 className="titulo-editarita">📝 Editor de Citas Agendadas</h2>
                    <div className="auth-section">
                        <p className="status-info" style={{ textAlign: 'left' }}>{status}</p>
                        
                        <div className="google-auth-controls" style={{ marginBottom: '20px', textAlign: 'left' }}>
                            {!isSignedIn ? (
                                <button className="btn-primary" onClick={iniciarSesion}>
                                    🔐 Iniciar sesión Google Calendar
                                </button>
                            ) : (
                                <button className="btn-cerrar-sesion" onClick={cerrarSesion} style={{ background: '#dc3545', color: 'white', padding: '10px 20px', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: '600' }}>
                                    🚪 Cerrar sesión de Google Calendar
                                </button>
                            )}
                        </div>
                    </div>

                    {!isSignedIn ? (
                        <div className="no-eventos">
                            {/* 🚨 AQUÍ PROTEGEMOS LA VISTA: Si no hay login, solo sale este mensaje */}
                            <p>⚠️ Para visualizar y editar citas, por favor inicie sesión en Google Calendar.</p>
                        </div>
                    ) : (
                    <>
                        {/* 🚨 TODO ESTO SOLO SE VE SI ESTÁS LOGUEADO */}
                        <div className="filtros-section">
                            <div className="filtro-busqueda">
                                <input
                                    id="input-busqueda"
                                    name="busqueda"
                                    type="text"
                                    placeholder="🔍 Buscar por DNI, cliente o mascota..."
                                    value={busqueda}
                                    onChange={(e) => setBusqueda(e.target.value)}
                                />
                            </div>

                        <div className="filtro-fecha">
                            <input
                                id="input-filtro-fecha"
                                name="filtro-fecha"
                                type="date"
                                value={filtroFecha}
                                onChange={(e) => setFiltroFecha(e.target.value)}
                            />
                            {filtroFecha && (
                                <button className="btn-limpiar" onClick={() => setFiltroFecha("")}>✖️</button>
                            )}
                        </div>
                                
                        <div className="filtro-estado">
                            <select
                                id="filtro-estado"
                                name="filtro-estado"
                                value={filtroEstadoId}
                                onChange={(e) => setFiltroEstadoId(parseInt(e.target.value))}
                            >
                                <option value={0}>Todos (Excepto finalizados)</option>
                                <option value={-1}>Mostrar Todos</option>
                                {estadosAgenda.map(estado => (
                                    <option key={estado.id} value={estado.id}>{estado.nombre}</option>
                                ))}
                            </select>
                        </div>
                                
                        </div>

                        {eventosFiltrados.length === 0 ? (
                            <div className="no-eventos">
                                {eventos.length === 0 ? "No hay citas agendadas" : "No se encontraron citas con esos filtros"}
                            </div>
                        ) : (
                        <div className="citas-grid">
                            {eventosFiltrados.map((e) => {
                                const detalles = getDetallesMascotaCliente(e, clientes, mascotas, estadosAgenda, colaboradores);
                                const fechaHora = new Date(`${e.fecha}T${e.hora}`);
                                const isEditable = !ESTADOS_NO_EDITABLES.includes(detalles.estadoNombre);
                                
                                return (
                                    <div key={e.id} className="cita-card-edit">
                                        <div className="cita-header">
                                            <h3>{detalles.mascotaNombre}</h3>
                                            <span className={`estado estado-${detalles.estadoNombre.toLowerCase()}`}>
                                                {detalles.estadoNombre || "N/A"}
                                            </span>
                                        </div>

                                        <div className="cita-info">
                                            <p><strong>📄 DNI:</strong> {detalles.dni}</p>
                                            <p><strong>👤 Cliente:</strong> {detalles.clienteNombre}</p>
                                            <p><strong>📅 Fecha:</strong> {fechaHora.toLocaleDateString("es-ES")}</p>
                                            <p><strong>🕐 Hora:</strong> {fechaHora.toLocaleTimeString("es-ES", { hour: "2-digit", minute: "2-digit" })}</p>
                                            <p><strong>Duración:</strong> {e.duracionEstimadaMin} min</p>
                                            <p><strong>💰 Abono:</strong> ${e.abonoInicial.toFixed(2)}</p>
                                        </div>

                                        <div className="cita-acciones">
                                            <button
                                                className={`btn-editar ${!isEditable ? 'disabled' : ''}`}
                                                onClick={() => editarEvento(e)}
                                                disabled={!isEditable}
                                                title={!isEditable ? "No se puede editar una cita en este estado terminal" : "Editar cita"}
                                            >
                                                ✏️ Editar
                                            </button>
                                            <button
                                                className="btn-eliminar"
                                                onClick={() => eliminarEvento(e.id)}
                                            >
                                                🗑️ Cancelar
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
                        <h3>✏️ Editar cita</h3>

                        <div className="form-grid">
                            
                            {/* Cliente (SOLO NOMBRE - DESHABILITADO) */}
                            <div className="form-group">
                                <label>Cliente *</label>
                                <input
                                    id="cliente-nombre"
                                    name="cliente-nombre"
                                    type="text"
                                    value={nuevoEvento.cliente} 
                                    disabled 
                                    style={{ background: "#f0f0f0" }}
                                />
                            </div>
                            
                            {/* DNI (CAMPO SEPARADO - DESHABILITADO) */}
                            <div className="form-group">
                                <label>DNI *</label>
                                <input
                                    id="cliente-dni"
                                    name="cliente-dni"
                                    type="text"
                                    value={nuevoEvento.dni} 
                                    disabled 
                                    style={{ background: "#f0f0f0" }}
                                />
                            </div>

                            {/* Select de Mascotas (EDITABLE, lista asegurada) */}
                            <div className="form-group">
                                <label>Mascota *</label>
                                <select
                                    id="mascota-select"
                                    name="mascota-select"
                                    value={nuevoEvento.mascotaId} 
                                    onChange={handleMascotaChange}
                                    disabled={!nuevoEvento.clienteId}
                                >
                                <option value={0}>Seleccione mascota...</option>
                                {mascotasDisponibles.map((m) => (
                                    <option key={m.id} value={m.id}>{m.nombre}</option> 
                                ))}
                                </select>
                            </div>
                                    
                            {/* Fecha y Hora (EDITABLE) */}
                            <div className="form-group">
                                <label>Fecha *</label>
                                <input
                                    id="cita-fecha"
                                    name="cita-fecha"
                                    type="date"
                                    value={nuevoEvento.date}
                                    onChange={(e) => setNuevoEvento({ ...nuevoEvento, date: e.target.value })}
                                />
                            </div>

                            <div className="form-group">
                                <label>Hora *</label>
                                <input
                                    id="cita-hora"
                                    name="cita-hora"
                                    type="time"
                                    value={nuevoEvento.startTime}
                                    onChange={(e) => setNuevoEvento({ ...nuevoEvento, startTime: e.target.value })}
                                />
                            </div>
                                    
                            {/* Duración Estimada (BLOQUEADA) */}
                            <div className="form-group">
                                <label>Duración Estimada (min)</label>
                                <input
                                    id="duracion-min"
                                    name="duracion-min"
                                    type="number"
                                    value={nuevoEvento.duracionEstimadaMin}
                                    disabled 
                                    style={{ background: "#f0f0f0", fontWeight: "bold" }} 
                                    title="Se calcula automáticamente sumando los servicios."
                                />
                            </div>
                                    
                            {/* Abono (BLOQUEADO) */}
                            <div className="form-group">
                                <label>Abono Inicial</label>
                                <input
                                    id="abono-inicial"
                                    name="abono-inicial"
                                    type="number"
                                    min="0"
                                    step="0.01"
                                    value={abonoEditable}
                                    onChange={(e) => setAbonoEditable(parseFloat(e.target.value) || 0)}
                                    style={{ background: "#f0f0f0" }}
                                    title="El abono solo se puede modificar mediante un registro de pago."
                                />
                            </div>

                            {/* Estado (EDITABLE) */}
                            <div className="form-group">
                                <label>Estado *</label>
                                <select
                                    id="estado-select"
                                    name="estado-select"
                                    value={nuevoEvento.estado}
                                    onChange={(e) => setNuevoEvento({ ...nuevoEvento, estado: e.target.value })}
                                >
                                <option value="">Seleccione...</option>
                                {estadosAgenda.map(estado => (
                                    <option key={estado.id} value={estado.nombre.toUpperCase()}>{estado.nombre}</option>
                                ))}
                                </select>
                            </div>
                        </div>

                        {/* GESTIÓN DE SERVICIOS REGISTRADOS */}
                        <div className="full-width-section" style={{ borderTop: '1px solid #ccc', paddingTop: '20px', marginBottom: '20px' }}>
                            <h4>🛠 Servicios</h4>
                            
                            <div className="service-input-grid" id="serviceFormInputs" style={{ 
                                display: 'grid', 
                                gridTemplateColumns: 'repeat(12, 1fr)', 
                                gap: '10px', 
                                alignItems: 'end' 
                            }}>

                                <div className="form-group" style={{ gridColumn: 'span 6' }}>
                                    <label htmlFor="id_servicio_add">Servicio *</label>
                                    <select id="id_servicio_add" name="id_servicio_add" value={servicioTemporal.id_servicio} onChange={handleServicioTemporalChange} style={{ width: '100%' }}>
                                        <option value="">Seleccione...</option>
                                        {serviciosDisponibles.map(s => (
                                            <option key={s.id} value={s.id}>{s.nombre}</option>
                                        ))}
                                    </select>
                                </div>

                                <div className="form-group" style={{ gridColumn: 'span 6' }}>
                                    <label htmlFor="id_veterinario_add">Veterinario *</label>
                                    <select id="id_veterinario_add" name="id_veterinario_add" value={servicioTemporal.id_veterinario} onChange={(e) => setServicioTemporal({ ...servicioTemporal, id_veterinario: e.target.value })} style={{ width: '100%' }}>
                                        <option value="">Seleccione...</option>
                                        {colaboradores.map(c => (
                                            <option key={c.id} value={c.id}>{c.nombre}</option>
                                        ))}
                                    </select>
                                </div>

                                <div className="form-group" style={{ gridColumn: 'span 2' }}>
                                    <label htmlFor="valor_servicio_add">Costo ($)</label>
                                    <input 
                                        type="number" 
                                        id="valor_servicio_add" 
                                        name="valor_servicio_add" 
                                        min="0" 
                                        step="0.01" 
                                        value={servicioTemporal.valor_servicio} 
                                        onChange={(e) => setServicioTemporal({ ...servicioTemporal, valor_servicio: parseFloat(e.target.value) || 0 })} 
                                        style={{ width: '100%' }}
                                    />
                                </div>

                                <div className="form-group" style={{ gridColumn: 'span 2' }}>
                                    <label htmlFor="cantidad_add">Cant.</label>
                                    <input 
                                        type="number" 
                                        id="cantidad_add" 
                                        name="cantidad_add" 
                                        min="1" 
                                        step="1" 
                                        value={servicioTemporal.cantidad} 
                                        onChange={(e) => setServicioTemporal({ ...servicioTemporal, cantidad: parseInt(e.target.value) || 1 })} 
                                        style={{ width: '100%' }}
                                    />
                                </div>
                                
                                <div className="form-group" style={{ gridColumn: 'span 2' }}>
                                    <label htmlFor="duracion_servicio_add">Dur. (min)</label>
                                    <input 
                                        type="number" 
                                        id="duracion_servicio_add" 
                                        name="duracion_servicio_add" 
                                        min="1" 
                                        step="5" 
                                        value={servicioTemporal.duracion_min} 
                                        onChange={(e) => setServicioTemporal({ ...servicioTemporal, duracion_min: parseInt(e.target.value) || 0 })} 
                                        style={{ width: '100%' }}
                                    />
                                </div>

                                <div className="form-group" style={{ gridColumn: 'span 6' }}>
                                    <label htmlFor="adicionales_add">Adicionales</label>
                                    <input 
                                        type="text" 
                                        id="adicionales_add" 
                                        name="adicionales_add" 
                                        placeholder="Ej: Pelo largo"
                                        value={servicioTemporal.adicionales} 
                                        onChange={(e) => setServicioTemporal({ ...servicioTemporal, adicionales: e.target.value })} 
                                        style={{ width: '100%' }}
                                    />
                                </div>
                                
                                <div className="form-group" style={{ gridColumn: 'span 12', display: 'flex', justifyContent: 'center', marginTop: '10px' }}>
                                    <button 
                                        type="button" 
                                        id="btnAddService" 
                                        className="btn-primary" 
                                        onClick={agregarServicio} 
                                        style={{ height: '40px', width: '100%'}}
                                    >
                                        ➕ Agregar Servicio
                                    </button>
                                </div>
                            </div>
                            
                            {/* TABLA DE DETALLES DE SERVICIOS - DISEÑO COMPACTO */}
                            {serviciosRegistrados.length > 0 && (
                                <div style={{ overflowX: 'auto' }}>
                                    <table className="service-table" style={{ width: '100%', marginTop: '15px', borderCollapse: 'collapse', fontSize: '0.85rem' }}>
                                        <thead>
                                            <tr style={{ background: '#f8f9fa' }}>
                                                <th style={{ textAlign: 'left', padding: '8px', borderBottom: '2px solid #ddd' }}>Servicio</th>
                                                <th style={{ width: '18%', padding: '8px', borderBottom: '2px solid #ddd' }}>Veterinario</th>
                                                <th style={{ width: '8%', padding: '8px', borderBottom: '2px solid #ddd', textAlign: 'center' }}>Cant</th>
                                                <th style={{ width: '12%', padding: '8px', borderBottom: '2px solid #ddd', textAlign: 'right' }}>Valor</th>
                                                <th style={{ width: '12%', padding: '8px', borderBottom: '2px solid #ddd', textAlign: 'right' }}>Total</th>
                                                <th style={{ width: '12%', padding: '8px', borderBottom: '2px solid #ddd', textAlign: 'center' }}>Dur.</th>
                                                <th style={{ width: '5%', padding: '8px', borderBottom: '2px solid #ddd' }}></th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {serviciosRegistrados.map((s, index) => (
                                         <tr 
        key={index} 
        // 1. Al hacer clic en cualquier parte de la fila, se selecciona para editar
        onClick={() => seleccionarServicio(index)} 
        style={{ cursor: "pointer" }}
        className="fila-servicio"
    >
        <td style={{ textAlign: 'left', padding: '8px', borderBottom: '1px solid #eee' }}>
            <strong>{s.nombre_servicio}</strong>
            {s.adicionales && <div style={{ fontSize: '0.8em', color: '#666' }}>({s.adicionales})</div>}
        </td>
        <td style={{ padding: '8px', borderBottom: '1px solid #eee' }}>{s.nombre_veterinario}</td>
        <td style={{ padding: '8px', borderBottom: '1px solid #eee', textAlign: 'center' }}>{s.cantidad}</td>
        <td style={{ padding: '8px', borderBottom: '1px solid #eee', textAlign: 'right' }}>${s.valor_servicio.toFixed(2)}</td>
        <td style={{ padding: '8px', borderBottom: '1px solid #eee', textAlign: 'right' }}>${s.subtotal.toFixed(2)}</td>
        <td style={{ padding: '8px', borderBottom: '1px solid #eee', textAlign: 'center' }}>{s.duracion_total} m</td>
        <td style={{ padding: '8px', borderBottom: '1px solid #eee', textAlign: 'center' }}>
            <button 
                type="button" 
                className="btn-eliminar" 
                onClick={(e) => {
                    // 2. IMPORTANTE: e.stopPropagation() evita que se active el onClick de la fila (el de editar)
                    e.stopPropagation(); 
                    eliminarServicio(index);
                }} 
                style={{ padding: '2px 6px', fontSize: '1em', cursor: 'pointer', border: 'none', background: 'red', color: 'white', borderRadius: '4px' }}
            >
                🗑️
            </button>
        </td>
    </tr>
))}
                                        </tbody>
                                        <tfoot style={{ background: '#fafafa' }}>
                                            <tr>
                                                <td colSpan={5} style={{ textAlign: "right", padding: '8px', fontWeight: 'bold' }}>Total Duración:</td>
                                                <td style={{ fontWeight: "bold", padding: '8px', textAlign: 'center' }}>{totalDuracion} min</td>
                                                <td></td>
                                            </tr>
                                            <tr>
                                                <td colSpan={5} style={{ textAlign: "right", fontWeight: 'bold' }}>Total Servicios:</td>
                                                <td style={{ fontWeight: "bold", padding: '8px', textAlign: 'right' }}>${totalCosto.toFixed(2)}</td>
                                                <td></td>
                                            </tr>
                                            <tr className="total-row">
                                                <td colSpan={5} style={{ textAlign: "right", fontWeight: "bold" }}>Adelanto:</td>
                                                <td style={{ fontWeight: "bold", padding: '8px', color: "red", textAlign: 'right' }}>${(abonoEditable).toFixed(2)}</td>
                                                <td></td>
                                            </tr>
                                            <tr style={{ borderTop: '2px solid #ddd' }}>
                                                <td colSpan={5} style={{ textAlign: "right" }}><strong>Pendiente de Pago:</strong></td>
                                                <td id="totalCitaDisplay" style={{ fontWeight: "bold", textAlign: 'right', fontSize: '1.1em' }}>
                                                    ${Math.max(0, totalCosto - (abonoEditable)).toFixed(2)}
                                                </td>
                                                <td></td>
                                            </tr>
                                        </tfoot>
                                    </table>
                                </div>
                            )}
                        </div>

                        {/* Observaciones */}
                        <div className="form-group-full">
                            <label>Observaciones</label>
                            <textarea
                                id="observaciones"
                                name="observaciones"
                                value={nuevoEvento.observaciones}
                                onChange={(e) => setNuevoEvento({ ...nuevoEvento, observaciones: e.target.value })}
                            />
                        </div>

                        {/* Botones de acción */}
                        <div className="modal-acciones">
                            <button className="btn-guardar" onClick={guardarEvento}>
                                💾 Guardar Cambios
                            </button>
                            <button
                                className="btn-cancelar"
                                onClick={resetModalState}
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