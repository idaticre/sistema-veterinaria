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

// 🚨 FUNCIÓN CORREGIDA Y MEJORADA: BUSCAR EL ID DE GOOGLE CALENDAR
// Soluciona el problema de citas duplicadas validando la HORA exacta.
const buscarIdGoogleCalendar = async (cita: CitaBD, cliente: EntityBase): Promise<string | undefined> => {
    if (!window.gapi || !window.gapi.client || !window.gapi.client.calendar) {
        return undefined; 
    }
    
    // Asegurar rango de búsqueda de 00:00 a 23:59 LOCAL
    const startOfDay = new Date(`${cita.fecha}T00:00:00`).toISOString();
    const endOfDay = new Date(`${cita.fecha}T23:59:59`).toISOString();
    
    // Intento 1: Buscar por Documento (DNI) es más preciso
    let queryTerm = cliente.documento || cliente.nombre || "";
    if (!queryTerm) return undefined;
    
    try {
        const res = await window.gapi.client.calendar.events.list({
            calendarId: "primary",
            timeMin: startOfDay,
            timeMax: endOfDay,
            q: queryTerm,
            singleEvents: true,
            maxResults: 20, // Traemos un margen amplio para filtrar nosotros
        });
        
        const eventosEncontrados = res.result.items;
        if (!eventosEncontrados || eventosEncontrados.length === 0) return undefined;

        // 🔍 LÓGICA DE FILTRADO EXACTO
        const eventoCorrecto = eventosEncontrados.find((event: any) => {
            // 1. Prioridad: Buscar si ya tiene nuestro ID en la descripción (Huella Digital)
            if (event.description && event.description.includes(`[REF_ID:${cita.id}]`)) {
                return true;
            }

            // 2. Respaldo: Verificar la HORA exacta (Solución al problema 10am vs 11am)
            if (event.start && event.start.dateTime) {
                const eventDate = new Date(event.start.dateTime);
                // Extraemos hora y minuto de la cita (formato "HH:mm:ss")
                const [citaHora, citaMin] = cita.hora.split(':').map(Number);
                
                const coincideHora = eventDate.getHours() === citaHora;
                // Damos un margen de 2 minutos por si hubo un ligero desfase manual
                const coincideMin = Math.abs(eventDate.getMinutes() - citaMin) <= 2;

                if (coincideHora && coincideMin) {
                    // Verificación extra de texto para evitar falsos positivos con otros clientes
                    const textoCoincide = 
                        (event.description && event.description.includes(cliente.documento)) || 
                        (event.summary && event.summary.includes(cliente.nombre));
                    
                    return textoCoincide;
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

// 🚨 FUNCIÓN PARA PARSEAR SERVICIOS DESDE LA DESCRIPCIÓN DE GC
const parsearServiciosGC = (description: string, colaboradores: EntityBase[], serviciosDisponibles: ServicioBase[]): ServicioDetalle[] => {
    if (!description) return [];

    const serviciosSection = description.match(/\*\*SERVICIOS REGISTRADOS\*\*(.*?)\*\*ADELANTO/s);
    if (!serviciosSection || !serviciosSection[1]) return [];

    const lineas = serviciosSection[1].trim().split('\n');
    const resultados: ServicioDetalle[] = [];

    lineas.forEach(linea => {
        const match = linea.match(/• (.*?) \((\d+)x \$(\d+\.?\d*)\) Subtotal: \$(\d+\.?\d*) con (.*?)\. Adicionales: (.*)/i);
        if (match) {
            const [_, nombreCompleto, cantidadStr, valorStr, subtotalStr, veterinarioNombre, adicionales] = match;
            
            const nombre_servicio = nombreCompleto.trim();
            const cantidad = parseInt(cantidadStr);
            const valor_servicio = parseFloat(valorStr);
            const subtotal = parseFloat(subtotalStr);
            
            const veterinario = colaboradores.find(c => c.nombre === veterinarioNombre.trim());
            const servicioBase = serviciosDisponibles.find(s => s.nombre === nombre_servicio);
            
            resultados.push({
                id_servicio: servicioBase?.id || 0,
                nombre_servicio: nombre_servicio,
                id_veterinario: veterinario?.id || 0,
                nombre_veterinario: veterinarioNombre.trim(),
                cantidad: cantidad,
                valor_servicio: valor_servicio,
                bono_inicial: 0,
                duracion_min: servicioBase?.duracion || 30, 
                duracion_total: (servicioBase?.duracion || 30) * cantidad,
                subtotal: subtotal,
                adicionales: adicionales.trim() === 'N/A' ? '' : adicionales.trim(),
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

    // Determinar los IDs de estados que son terminales para la lógica de filtro por defecto
    const idsEstadosTerminales = useMemo(() => {
        return estadosAgenda
            .filter(e => ESTADOS_NO_EDITABLES.includes(e.nombre))
            .map(e => e.id);
    }, [estadosAgenda]);
    
    // CÁLCULO DE TOTALES
    const totalDuracion = serviciosRegistrados.reduce((sum, s) => sum + s.duracion_total, 0);
    const totalCosto = serviciosRegistrados.reduce((sum, s) => sum + s.subtotal, 0);

    // =======================================================
    // 🚨 EFECTO: ACTUALIZAR DURACIÓN ESTIMADA AUTOMÁTICAMENTE
    // =======================================================
    useEffect(() => {
        // La duración estimada es la suma de las duraciones de los servicios agregados
        if (serviciosRegistrados.length > 0) {
            setNuevoEvento(prev => ({
                ...prev,
                duracionEstimadaMin: totalDuracion
            }));
        }
    }, [serviciosRegistrados, totalDuracion]);


    // --- Lógica de Manejo de Servicios (Funciones dentro del componente) ---
    
    // 🚨 EFECTO para auto-llenar precio/duración del servicio temporal
    useEffect(() => {
        const serviceId = parseInt(servicioTemporal.id_servicio as string);
        if (!serviceId || isNaN(serviceId)) {
            setServicioTemporal(prev => ({ ...prev, valor_servicio: 0, duracion_min: 0 }));
            return;
        }
        const s = serviciosDisponibles.find(s => s.id === serviceId);
        if (s) {
            setServicioTemporal(prev => ({
                ...prev,
                valor_servicio: s.precio,
                duracion_min: s.duracion, // Carga duración por defecto
            }));
        }
    }, [servicioTemporal.id_servicio, serviciosDisponibles]);

    // 🚨 Función handleServicioTemporalChange para el selector de servicio
    const handleServicioTemporalChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const selectedId = parseInt(e.target.value);
        const servicio = serviciosDisponibles.find(s => s.id === selectedId);

        setServicioTemporal(prev => ({
            ...prev,
            id_servicio: e.target.value,
            valor_servicio: servicio?.precio || 0,
            duracion_min: servicio?.duracion || 0,
        }));
    };


    const agregarServicio = () => {
        const sId = parseInt(servicioTemporal.id_servicio as string);
        const vId = parseInt(servicioTemporal.id_veterinario as string);
        const servicioInfo = serviciosDisponibles.find(s => s.id === sId);
        const veterinarioInfo = colaboradores.find(v => v.id === vId);

        // 🚨 Validación
        if (!sId || isNaN(sId)) {
            return alert("⚠️ Debe seleccionar un Servicio.");
        }
        if (!vId || isNaN(vId)) {
            return alert("⚠️ Debe seleccionar un Veterinario.");
        }
        if (servicioTemporal.cantidad <= 0) {
            return alert("⚠️ La cantidad debe ser mayor a cero.");
        }
        if (servicioTemporal.duracion_min <= 0) {
             return alert("⚠️ La duración debe ser mayor a cero.");
        }

        const cantidad = servicioTemporal.cantidad;
        const valorUnitario = servicioTemporal.valor_servicio;
        
        // 🚨 USAMOS LA DURACIÓN INGRESADA POR EL USUARIO
        const duracionUnitaria = servicioTemporal.duracion_min; 
        
        const subtotalCalculado = valorUnitario * cantidad;

        const nuevoServicio: ServicioDetalle = {
            id_servicio: sId,
            nombre_servicio: servicioInfo!.nombre, 
            id_veterinario: vId,
            nombre_veterinario: veterinarioInfo!.nombre, 
            cantidad: cantidad,
            valor_servicio: valorUnitario,
            bono_inicial: 0,
            duracion_min: duracionUnitaria,
            duracion_total: duracionUnitaria * cantidad, // Recálculo total
            subtotal: subtotalCalculado,
            adicionales: servicioTemporal.adicionales,
        };

        setServiciosRegistrados(prev => [...prev, nuevoServicio]);
        setServicioTemporal(prev => ({
            id_servicio: '',
            valor_servicio: 0,
            cantidad: 1,
            duracion_min: 0,
            id_veterinario: prev.id_veterinario,
            adicionales: '',
        }));
    };

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
                scope: "https://www.googleapis.com/auth/calendar.events", // Permiso de lectura/escritura
                callback: (tokenResponse: any) => {
                    if (tokenResponse.access_token) {
                        window.gapi.client.setToken({ access_token: tokenResponse.access_token });
                        localStorage.setItem("google_token_editor", tokenResponse.access_token);
                        setIsSignedIn(true);
                        setStatus("✅ Google Calendar autenticado. Ediciones se sincronizarán.");
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

    useEffect(() => {
        if (!gapiInited || !gisInited) return;
        const saved = localStorage.getItem("google_token_editor");
        if (saved) {
            window.gapi.client.setToken({ access_token: saved });
            setIsSignedIn(true);
            setStatus("🔓 Google Calendar sesión restaurada.");
        }
    }, [gapiInited, gisInited]);

    const iniciarSesion = () => tokenClient?.requestAccessToken();
    const cerrarSesion = () => {
        const token = window.gapi.client.getToken();
        if (token) window.google.accounts.oauth2.revoke(token.access_token);
        window.gapi.client.setToken(null);
        localStorage.removeItem("google_token_editor");
        setIsSignedIn(false);
        setStatus("🚪 Google Calendar sesión cerrada.");
    };


    // ================== CARGA INICIAL DE DATOS MAESTROS Y CITAS ==================
    const fetchCitas = async () => {
        try {
            // 1. Carga de datos maestros (Clientes, Colaboradores, Mascotas, Estados, Servicios)
            const [resClientes, resColaboradores, resMascotas, resEstados, resCitas, resServicios] = await Promise.all([
                IST.get("/clientes"),
                IST.get("/colaboradores"),
                IST.get("/mascotas"),
                IST.get("/estados-agenda"),
                IST.get("/agenda?page=0&size=100"), 
                IST.get("/servicios"),
            ]);

            // Procesar Clientes
            const listaClientes: EntityBase[] = resClientes.data.data.filter((c: any) => c.activo) || [];
            setClientes(listaClientes);

            // Procesar Colaboradores
            const listaColaboradores: EntityBase[] = resColaboradores.data.data || [];
            setColaboradores(listaColaboradores);

            // Procesar Mascotas
            const listaMascotas: MascotaBase[] = resMascotas.data.data || [];
            setMascotas(listaMascotas);

            // Procesar Estados
            const estadosData = Array.isArray(resEstados.data) ? resEstados.data : resEstados.data.data;
            const estadosParseados = estadosData.map((e: any) => ({ id: e.id, nombre: e.nombre.toUpperCase() }));
            setEstadosAgenda(estadosParseados);

            // Procesar Servicios Disponibles
            const serviciosData = Array.isArray(resServicios.data) ? resServicios.data : resServicios.data.data;
            const serviciosParseados = serviciosData.map((s: any) => ({
                ...s,
                duracion: parseInt(s.duracion) || 30,
                precio: parseFloat(s.precio) || 0,
            }));
            setServiciosDisponibles(serviciosParseados);
            
            // 2. Cargar Citas de la BD
            const citasObtenidas: CitaBD[] = resCitas.data.data.content || [];
            setEventos(citasObtenidas);
            
            setStatus(`✅ ${citasObtenidas.length} citas y datos maestros cargados.`);

        } catch (error) {
               console.error("Error al cargar datos de la BD:", error);
               setStatus("⚠️ Error al cargar datos. Verifique endpoints y conexión.");
        }
    };
    
    useEffect(() => {
        fetchCitas();
    }, []); 


    // ================== FUNCIONES DE FILTRADO UNIFICADO ==================
    useEffect(() => {
        let filtrados = eventos;

        // 1. Filtro de Búsqueda (DNI, cliente, mascota, observaciones)
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

        // 2. Filtro de Fecha
        if (filtroFecha) {
            filtrados = filtrados.filter((e) => e.fecha === filtroFecha);
        }

        // 3. Filtro de Estado
        if (filtroEstadoId === 0) {
            // Valor por defecto: Mostrar todos excepto estados terminales
            if (idsEstadosTerminales.length > 0) {
                 filtrados = filtrados.filter(e => !idsEstadosTerminales.includes(e.idEstado));
            }
        } else if (filtroEstadoId > 0) {
            // Mostrar solo el estado seleccionado
            filtrados = filtrados.filter(e => e.idEstado === filtroEstadoId);
        }

        setEventosFiltrados(filtrados);
    }, [busqueda, filtroFecha, filtroEstadoId, eventos, clientes, mascotas, idsEstadosTerminales]);


    // ================== GESTIÓN DE MODAL Y EDICIÓN ==================

    const resetModalState = () => {
        setMostrarModal(false);
        setEditandoCita(null);
        setEditingGCId(undefined); // 🚨 Limpiar ID de GC al cerrar
        setServiciosRegistrados([]); // Limpiar la tabla de servicios
        setNuevoEvento({
            dni: "", cliente: "", clienteId: 0, mascotaId: 0, 
            date: new Date().toISOString().split("T")[0], startTime: "10:00", 
            estado: "", duracionEstimadaMin: 30, abonoInicial: 0, observaciones: "", 
            colaboradorId: 0, colaboradorNombre: "", 
        });
    };
    
    const editarEvento = async (cita: CitaBD) => { // 🚨 Ahora es async
        // 🚨 PREVENCIÓN DE EDICIÓN EN ESTADOS TERMINALES
        const estado = estadosAgenda.find(e => e.id === cita.idEstado);
        const estadoNombre = estado?.nombre.toUpperCase() || 'N/A';
        
        if (ESTADOS_NO_EDITABLES.includes(estadoNombre)) {
            alert(`🚫 No se puede editar la cita ${cita.codigo} porque su estado es ${estadoNombre}.`);
            return; 
        }

        const cliente = clientes.find(c => c.id === cita.idCliente);
        const colaboradorAsignado = colaboradores.find(c => c.id === cita.idColaborador) || colaboradores[0];
        
        setEditandoCita(cita); 
        
        // 1. Inicializamos con las observaciones de la Base de Datos (Prioridad Máxima)
        let fetchedObservaciones = cita.observaciones;

        if (isSignedIn && cliente) {
            setStatus("🟡 Buscando ID en Google Calendar y servicios...");
            
            // Busca el ID de GC (si la cita en BD no lo tiene)
            let gcEventId = cita.idGoogleCalendar || await buscarIdGoogleCalendar(cita, cliente); 

            if (gcEventId) {
                // 2. Si encontramos el ID, buscamos la descripción para cargar los servicios
                try {
                    const res = await window.gapi.client.calendar.events.get({
                        calendarId: "primary",
                        eventId: gcEventId
                    });
                    
                    const descriptionGC = res.result.description || "";
                    // 🚨 Usamos la nueva función de parseo
                    const serviciosParseados = parsearServiciosGC(descriptionGC, colaboradores, serviciosDisponibles); 

                    // CORRECCIÓN: NO sobrescribir las observaciones de la BD con las de Google Calendar.
                    setServiciosRegistrados(serviciosParseados);
                    setEditingGCId(gcEventId);
                    setStatus("✅ Servicios de GC cargados. Listo para editar.");

                } catch (gcError) {
                    console.error("Error al obtener detalles del evento GC:", gcError);
                    setStatus("⚠️ Cita cargada. La sincronización de GC falló. Revise su sesión.");
                    setEditingGCId(gcEventId); // Mantenemos el ID aunque falle la carga de detalles para intentar actualizar después
                }
            } else {
                setEditingGCId(undefined);
                setStatus("⚠️ Cita cargada. No se encontró ID de GC para esta cita.");
            }
        } else {
             setEditingGCId(undefined);
        }

        // 3. Mapear al estado del formulario (usa las observaciones encontradas o las de la BD)
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
            observaciones: fetchedObservaciones || "", // 🚨 Usa las observaciones de la BD
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

    // Lógica para asegurar que la mascota de la cita esté siempre en la lista de opciones
    const mascotasDisponibles = useMemo(() => {
        const mascotasCliente = mascotas.filter(m => m.idCliente === nuevoEvento.clienteId);
        
        // Si estamos editando y la mascota actual no está en la lista del cliente (inconsistencia de data)
        const currentMascota = mascotas.find(m => m.id === nuevoEvento.mascotaId);
        
        if (currentMascota && !mascotasCliente.some(m => m.id === currentMascota.id)) {
            // Se clona la lista y se añade la mascota actual para que esté disponible para seleccionar
            return [...mascotasCliente, currentMascota];
        }

        return mascotasCliente;
    }, [mascotas, nuevoEvento.clienteId, nuevoEvento.mascotaId]);


    const eliminarEvento = async (id: number) => {
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

            // 1. Actualizar BD a CANCELADA
            const req = {
                ...citaActual, // Copia todos los campos actuales
                idEstado: estadoCancelado.id, // Sobrescribe el estado
            };

            const responseDB = await IST.put("/agenda/actualizar", req);

            if (responseDB.data.success) {
                setStatus(`🗑️ Cita ${citaActual.codigo} CANCELADA.`);
                
                // 2. ELIMINAR/CANCELAR en Google Calendar (si es necesario)
                if (citaActual.idGoogleCalendar && isSignedIn) {
                    try {
                        await window.gapi.client.calendar.events.delete({
                            calendarId: "primary",
                            eventId: citaActual.idGoogleCalendar
                        });
                        setStatus(`🗑️ Cita ${citaActual.codigo} CANCELADA y eliminada de Google Calendar.`);
                    } catch (gcError) {
                        console.error("Error al eliminar GC:", gcError);
                        alert("Cita cancelada en BD, pero falló la eliminación en Google Calendar. Revise la sesión de Google.");
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
        
        // 1. Validaciones
        if (nuevoEvento.mascotaId === 0)
          return alert("Debe seleccionar una mascota.");
        
        if (nuevoEvento.colaboradorId === 0)
          return alert("Debe seleccionar un colaborador."); 

        if (nuevoEvento.duracionEstimadaMin <= 0) return alert("La duración estimada debe ser mayor a 0 (Agregue servicios).");
        
        // Manejo de Abono y TotalCita
        const totalMinimoNecesario = totalCosto || editandoCita.totalCita || 0;
        const duracionFinal = totalDuracion || nuevoEvento.duracionEstimadaMin;
        
        try {
            const citaEditada: CitaBD = editandoCita; 

            // 2. Mapeo del estado
            const estadoEncontrado = estadosAgenda.find(e => e.nombre === nuevoEvento.estado.toUpperCase());
            const idEstado = estadoEncontrado ? estadoEncontrado.id : citaEditada.idEstado; 
            
            // Advertencia si se cambia un estado terminal
            if (ESTADOS_NO_EDITABLES.includes(citaEditada.codigo) && idEstado !== citaEditada.idEstado) {
                 if (!confirm(`La cita estaba en estado terminal. ¿Estás seguro de que quieres cambiar el estado a ${nuevoEvento.estado}?`)) {
                    return;
                }
            }

            // 3. Construcción del DTO para la BD
            // 🚨 IMPORTANTE: Pasamos el ID de GC para que se guarde en la BD y no se pierda la próxima vez
            const AgendaRequestDTO = {
                id: citaEditada.id,
                idCliente: nuevoEvento.clienteId,
                idMascota: nuevoEvento.mascotaId, 
                idMedioSolicitud: citaEditada.idMedioSolicitud || ID_MEDIO_SOLICITUD_DEFAULT, 
                
                totalCita: totalMinimoNecesario, 
                abonoInicial: citaEditada.abonoInicial, 
                
                idColaborador: nuevoEvento.colaboradorId,

                // Campos editables:
                fecha: nuevoEvento.date,
                hora: nuevoEvento.startTime + ":00", 
                duracionEstimadaMin: duracionFinal, // 🚨 Enviamos el total calculado
                idEstado: idEstado,
                observaciones: nuevoEvento.observaciones,
                
                // 🚨 GUARDA EL ID DE GC EN LA BD
                idGoogleCalendar: editingGCId || citaEditada.idGoogleCalendar || null 
            };
            
            // 4. Llamada al Backend (BD)
            const responseDB = await IST.put("/agenda/actualizar", AgendaRequestDTO);
            
            if (!responseDB.data.success) {
                throw new Error(`Error BD: ${responseDB.data.message}`);
            }
            
            setStatus(`✏️ Cita ${citaEditada.codigo} actualizada exitosamente.`);
            
            // ==========================================================
            //  LÓGICA: ACTUALIZAR GOOGLE CALENDAR (GC)
            // ==========================================================
            if (isSignedIn && editingGCId) {
                try { //  TRY ANIDADO para aislar fallas de GC
                    const cliente = clientes.find(c => c.id === nuevoEvento.clienteId);
                    const mascota = mascotas.find(m => m.id === nuevoEvento.mascotaId);
                    
                    const newStart = new Date(`${nuevoEvento.date}T${nuevoEvento.startTime}`);
                    const newEnd = new Date(newStart.getTime() + duracionFinal * 60000);

                    // 🚨 Reconstrucción del campo description usando la lista de servicios
                    const serviciosListaGC = serviciosRegistrados.map(s =>
                        `• ${s.nombre_servicio} (${s.cantidad}x $${s.valor_servicio.toFixed(2)})  Subtotal: $${s.subtotal.toFixed(2)} con ${s.nombre_veterinario}. Adicionales: ${s.adicionales || 'N/A'}`
                    ).join('\n');
                    
                    const updatedEventResource = {
                        summary: `${mascota?.nombre || 'Mascota'} - Total: $${totalMinimoNecesario.toFixed(2)}`, 
                        description: 
                            `**CLIENTE Y MASCOTA**\n` + 
                            `Cliente: ${cliente?.nombre} (DNI: ${cliente?.documento})\n` +
                            `Mascota: ${mascota?.nombre}\n` +
                            `Estado: ${nuevoEvento.estado}\n` + 
                            `Costo Total: $${totalCosto.toFixed(2)}\n` + // Usamos el TOTAL COSTO CALCULADO
                            `Duración Total: ${duracionFinal} min\n\n` + // Usamos la DURACIÓN TOTAL CALCULADA
                            `**SERVICIOS REGISTRADOS**\n${serviciosListaGC}\n\n` + 
                            `**ADELANTO/ABONO:** $${citaEditada.abonoInicial.toFixed(2)}\n\n` + 
                            `**OBSERVACIONES**\n${nuevoEvento.observaciones || 'No hay observaciones adicionales.'}\n\n` +
                            `--------------------------------\n` +
                            `[REF_ID:${citaEditada.id}]`.trim(), // 🚨 AGREGAMOS EL ID ÚNICO AQUÍ
                        
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
            
            // Manejo de errores de AXIOS/BD
            if (axios.isAxiosError(error) && error.response) {
                 errorMessage += ` Detalle API: ${error.response.data.message || 'Error de conexión'}`;
            } else if (error instanceof Error) {
                 errorMessage += ` Detalle: ${error.message}`;
            }
            
            console.error("Error al guardar cita:", error);
            alert(errorMessage);
            return;
        }

        // 5. Cierre y Recarga
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
                        
                        {/* 🚨 SECCIÓN DE AUTENTICACIÓN DE GOOGLE CALENDAR */}
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

                    {/* 🚨 AQUÍ ESTÁ EL CAMBIO IMPORTANTE: PROTECCIÓN DE LA INTERFAZ */}
                    {!isSignedIn ? (
                        <div className="no-eventos">
                            <p>⚠️ Para editar citas, por favor inicie sesión en Google Calendar.</p>
                        </div>
                    ) : (
                    <>
                        {/* BARRA DE FILTROS */}
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

                        {/* LISTA DE CITAS */}
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
                                {/* Usamos la lista de mascotas disponibles que incluye la actual */}
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
                                    
                            {/* 🚨 Duración Estimada (BLOQUEADA) */}
                            <div className="form-group">
                                <label>Duración Estimada (min)</label>
                                <input
                                    id="duracion-min"
                                    name="duracion-min"
                                    type="number"
                                    value={nuevoEvento.duracionEstimadaMin}
                                    disabled // 🔒 Bloqueado
                                    style={{ background: "#f0f0f0", fontWeight: "bold" }} // 🎨 Estilo visual
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
                                    value={editandoCita?.abonoInicial || 0} 
                                    disabled // 👈 Bloquea la edición
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

                        {/* 🚨 GESTIÓN DE SERVICIOS REGISTRADOS */}
                        <div className="full-width-section" style={{ borderTop: '1px solid #ccc', paddingTop: '20px', marginBottom: '20px' }}>
                            <h4>🛠 Servicios</h4>
                            
                            {/* 🚨 NUEVO DISEÑO GRID: Usamos 12 columnas para distribuir en filas ordenadas */}
                            <div className="service-input-grid" id="serviceFormInputs" style={{ 
                                display: 'grid', 
                                gridTemplateColumns: 'repeat(12, 1fr)', // Creamos 12 espacios disponibles
                                gap: '10px', 
                                alignItems: 'end' 
                            }}>

                                {/* 1. SELECCIÓN DE SERVICIO (Ocupa la mitad: 6 de 12) */}
                                <div className="form-group" style={{ gridColumn: 'span 6' }}>
                                    <label htmlFor="id_servicio_add">Servicio *</label>
                                    <select id="id_servicio_add" name="id_servicio_add" value={servicioTemporal.id_servicio} onChange={handleServicioTemporalChange} style={{ width: '100%' }}>
                                        <option value="">Seleccione...</option>
                                        {serviciosDisponibles.map(s => (
                                            <option key={s.id} value={s.id}>{s.nombre}</option>
                                        ))}
                                    </select>
                                </div>

                                {/* 2. SELECCIÓN DE VETERINARIO (Ocupa la otra mitad: 6 de 12) */}
                                <div className="form-group" style={{ gridColumn: 'span 6' }}>
                                    <label htmlFor="id_veterinario_add">Veterinario *</label>
                                    <select id="id_veterinario_add" name="id_veterinario_add" value={servicioTemporal.id_veterinario} onChange={(e) => setServicioTemporal({ ...servicioTemporal, id_veterinario: e.target.value })} style={{ width: '100%' }}>
                                        <option value="">Seleccione...</option>
                                        {colaboradores.map(c => (
                                            <option key={c.id} value={c.id}>{c.nombre}</option>
                                        ))}
                                    </select>
                                </div>

                                {/* 3. COSTO (Pequeño: 2 de 12) */}
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

                                {/* 4. CANTIDAD (Pequeño: 2 de 12) */}
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
                                
                                {/* 5. DURACIÓN (Pequeño: 2 de 12) */}
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

                                {/* 6. ADICIONALES (Ocupa el resto de la fila: 6 de 12) */}
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
                                
                                {/* BOTÓN AGREGAR (Ocupa todo el ancho abajo: 12 de 12 y centrado) */}
                                <div className="form-group" style={{ gridColumn: 'span 12', display: 'flex', justifyContent: 'center', marginTop: '10px' }}>
                                    <button 
                                        type="button" 
                                        id="btnAddService" 
                                        className="btn-primary" 
                                        onClick={agregarServicio} 
                                        style={{ height: '40px', width: '100%'}} // Max width para que se vea bien centrado
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
                                                <tr key={index}>
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
                                                        <button type="button" className="btn-eliminar" onClick={() => eliminarServicio(index)} style={{ padding: '2px 6px', fontSize: '1em', cursor: 'pointer', border: 'none', background: 'red' }}>🗑️</button>
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
                                                <td style={{ fontWeight: "bold", padding: '8px', color: "red", textAlign: 'right' }}>${(editandoCita?.abonoInicial || 0).toFixed(2)}</td>
                                                <td></td>
                                            </tr>
                                            <tr style={{ borderTop: '2px solid #ddd' }}>
                                                <td colSpan={5} style={{ textAlign: "right" }}><strong>Pendiente de Pago:</strong></td>
                                                <td id="totalCitaDisplay" style={{ fontWeight: "bold", textAlign: 'right', fontSize: '1.1em' }}>
                                                    ${Math.max(0, totalCosto - (editandoCita?.abonoInicial || 0)).toFixed(2)}
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