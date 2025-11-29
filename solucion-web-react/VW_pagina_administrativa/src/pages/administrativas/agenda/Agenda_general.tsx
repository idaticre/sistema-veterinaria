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

// --- INTERFACES ---
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

interface ServicioDetalle {
  id_servicio: number;
  nombre_servicio: string;
  id_veterinario: number;
  nombre_veterinario: string;
  cantidad: number;
  valor_servicio: number; // Valor Unitario de la venta
  bono_inicial: number; // Nuevo campo
  duracion_min: number; // Duración UNITARIA
  duracion_total: number; // Duración total (unidad * cant)
  subtotal: number; // (Valor_Servicio - Bono_Inicial) * Cantidad
  adicionales: string;
}
// ------------------


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
  
  // Nuevo estado para los servicios disponibles (inicializado vacío)
  const [serviciosDisponibles, setServiciosDisponibles] = useState<ServicioBase[]>([]);

  const [nuevoEvento, setNuevoEvento] = useState({
    id: '', // ID para saber si estamos editando o creando.
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
  const [bonoTemporal, setBonoTemporal] = useState(0); // Estado para el bono
  
  // --- CARGA DE DATOS INICIALES ---
  useEffect(() => {
    
    const listarServicios = async () => {
        try {
            const respuesta = await IST.get(`/servicios`);
            const lista = Array.isArray(respuesta.data) ? respuesta.data : respuesta.data.data
            if (Array.isArray(lista) && lista.length > 0) {
              // Convertir precios y duraciones a números si la API los devuelve como strings
              const serviciosParseados = lista.map(s => ({
                  ...s,
                  duracion: parseInt(s.duracion) || 0,
                  precio: parseFloat(s.precio) || 0,
              }));
              setServiciosDisponibles(serviciosParseados); 
            }
        } catch (error) {console.error("Error al obtener los servicios", error);}
    }
    listarServicios();
    
    // El resto de llamadas a la API
    IST.get("/clientes").then((r) => setClientes(r.data.data.filter((c: any) => c.activo)));
    IST.get("/colaboradores").then((r) => setColaboradores(r.data.data.filter((c: any) => c.activo))); 
    IST.get("/mascotas").then((res) => setMascotas(res.data.data)).catch(() => setMascotas([]));
  }, []);
  
  // CÁLCULO DE TOTALES
  const totalDuracion = serviciosRegistrados.reduce((sum, s) => sum + s.duracion_total, 0);
  const totalCosto = serviciosRegistrados.reduce((sum, s) => sum + s.subtotal, 0);

  // --- EFECTO CLAVE CORREGIDO: Sincronizar Servicio Temporal y prevenir crash ---
  useEffect(() => {
    // 1. CORRECCIÓN: Parsear a número el ID.
    const serviceId = parseInt(servicioTemporal.id_servicio as string);

    // 2. VALIDACIÓN: Si no es un número válido (ej: si se seleccionó la opción vacía ''), resetear y salir.
    if (!serviceId || isNaN(serviceId)) {
      setServicioTemporal(prev => ({
        ...prev,
        valor_servicio: 0,
        duracion_min: 0,
      }));
      setBonoTemporal(0);
      return; 
    }

    // 3. Buscar el servicio y si existe, establecer valores predeterminados.
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
  // ------------------------------------------------------------------------
  
  // ================== CÓDIGO DE GOOGLE CALENDAR ==================
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
       console.error("Error al cargar eventos:", error);
       setStatus("❌ Error al cargar eventos. Intente reconectar.");
    }
  };

  useEffect(() => {
    if (isSignedIn) cargarEventos();
  }, [fechaSeleccionada, isSignedIn]);

  const horaOcupada = (start: Date, end: Date, currentEventId?: string) => {
    return eventos.some((e) => {
      if (e.id === currentEventId) return false; // Ignorar el evento que se está editando
      const eStart = new Date(e.start.dateTime);
      const eEnd = new Date(e.end.dateTime);
      return (start < eEnd && end > eStart); // verifica solapamiento
    });
  };
  // ==============================================================================

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
    const bono = bonoTemporal;

    // CÁLCULO CLAVE: Subtotal = (Valor Unitario - Bono) * Cantidad
    const subtotalCalculado = Math.max(0, (valorUnitario - bono)) * cantidad; // Asegura que el subtotal no sea negativo

    const nuevoServicio: ServicioDetalle = {
      id_servicio: sId,
      nombre_servicio: servicioInfo.nombre,
      id_veterinario: vId,
      nombre_veterinario: veterinarioInfo.nombre,
      cantidad: cantidad,
      valor_servicio: valorUnitario,
      bono_inicial: bono, // Guardamos el bono en el detalle
      duracion_min: duracionUnitaria,
      duracion_total: duracionUnitaria * cantidad,
      subtotal: subtotalCalculado,
      adicionales: servicioTemporal.adicionales,
    };

    setServiciosRegistrados(prev => [...prev, nuevoServicio]);
    // Resetear el formulario temporal para el próximo servicio
    setServicioTemporal({
      id_servicio: '',
      valor_servicio: 0,
      cantidad: 1,
      duracion_min: 0,
      id_veterinario: servicioTemporal.id_veterinario, // Mantiene el veterinario seleccionado
      adicionales: '',
    });
    setBonoTemporal(0); // Reinicia el bono
  };

  const eliminarServicio = (index: number) => {
    setServiciosRegistrados(prev => prev.filter((_, i) => i !== index));
  };

  // --- FUNCIÓN PARA ABRIR MODAL EN MODO EDICIÓN ---
  const abrirModalEdicion = (evento: Evento) => {
    if (!evento.description) return alert("Este evento no tiene detalles de servicios para editar.");

    // Expresiones Regulares para parsear la descripción
    const serviceRegex = /•\s*(.*?)\s*\(([\d\.]+)x\s*\$([\d\.]+)\)\s*Bono:\s*\$([\d\.]+)\s*Subtotal:\s*\$([\d\.]+)\s*con\s*(.*?)\.\s*Adicionales:\s*(.*)/g;
    const clientRegex = /Cliente:\s*(.*?)\s*\(DNI:\s*(.*?)\)/;
    const petRegex = /Mascota:\s*(.*)/;
    const statusRegex = /Estado:\s*(.*)/;
    const observationsRegex = /\*\*OBSERVACIONES\*\*([\s\S]*)/;
    const totalDurationRegex = /Duración Total:\s*(\d+)\s*min/;

    const description = evento.description.trim();

    // 1. Parseo de Información General
    const clienteMatch = description.match(clientRegex);
    const mascotaMatch = description.match(petRegex);
    const estadoMatch = description.match(statusRegex);
    const totalDurationMatch = description.match(totalDurationRegex);
    
    // Extracción de Observaciones
    let obsText = '';
    const obsMatch = description.match(observationsRegex);
    if (obsMatch && obsMatch[1]) {
        obsText = obsMatch[1].trim();
        if (obsText === 'No hay observaciones adicionales.') {
            obsText = '';
        }
    }
    
    // Encontrar cliente por DNI para pre-seleccionar mascota
    const dni = clienteMatch ? clienteMatch[2].trim() : '';
    const clienteNombre = clienteMatch ? clienteMatch[1].trim() : '';
    const mascotaNombre = mascotaMatch ? mascotaMatch[1].trim() : '';
    const clienteEncontrado = clientes.find(c => c.documento === dni);

    // Formato de Fecha/Hora
    const datePart = evento.start.dateTime.split('T')[0];
    const timePart = new Date(evento.start.dateTime).toLocaleTimeString('en-US', { hour12: false, hour: '2-digit', minute: '2-digit' });

    // 2. Parseo de Servicios Detallados
    const parsedServices: ServicioDetalle[] = [];
    let match;
    // IMPORTANTE: Resetear el regex antes de usarlo en un bucle
    serviceRegex.lastIndex = 0; 
    while ((match = serviceRegex.exec(description)) !== null) {
        // Encontrar IDs por nombre (requiere que los nombres coincidan exactamente)
        const nombreServicio = match[1].trim();
        const nombreVeterinario = match[6].trim();

        const servicioBase = serviciosDisponibles.find(s => s.nombre.toUpperCase() === nombreServicio.toUpperCase());
        const veterinarioBase = colaboradores.find(c => c.nombre.toUpperCase() === nombreVeterinario.toUpperCase());

        const id_servicio = servicioBase?.id || 0;
        const id_veterinario = veterinarioBase?.id || 0;
        
        const adicionales = match[7].trim() === 'N/A' ? '' : match[7].trim();
        const cantidad = parseFloat(match[2].trim());
        // Usamos la duración base si no hay una duración explícita guardada
        const duracionUnitariaGuardada = servicioBase?.duracion || 0; 
        
        parsedServices.push({
            id_servicio: id_servicio,
            nombre_servicio: nombreServicio,
            id_veterinario: id_veterinario,
            nombre_veterinario: nombreVeterinario,
            cantidad: cantidad,
            valor_servicio: parseFloat(match[3].trim()),
            bono_inicial: parseFloat(match[4].trim()),
            duracion_min: duracionUnitariaGuardada,
            duracion_total: duracionUnitariaGuardada * cantidad, 
            subtotal: parseFloat(match[5].trim()),
            adicionales: adicionales,
        });
    }

    // 3. Establecer Estados
    setNuevoEvento({ 
        id: evento.id || '', // Guardamos el ID del evento para la edición
        summary: evento.summary,
        description: obsText,
        dni: dni,
        cliente: clienteNombre,
        clienteId: clienteEncontrado?.id || 0,
        mascota: mascotaNombre,
        servicio: '', 
        colaborador: '',
        date: datePart,
        startTime: timePart,
        duracion: totalDurationMatch ? totalDurationMatch[1] : '0',
        estado: estadoMatch ? estadoMatch[1].trim() : '',
    });
    setServiciosRegistrados(parsedServices);
    setServicioTemporal({ // Limpiar el formulario temporal, pero mantener el veterinario
      id_servicio: '',
      valor_servicio: 0,
      cantidad: 1,
      duracion_min: 0,
      // Usar el ID del veterinario del primer servicio si existe
      id_veterinario: parsedServices.length > 0 ? String(parsedServices[0].id_veterinario) : '', 
      adicionales: '',
    });
    setBonoTemporal(0);
    
    // 4. Abrir Modal
    setMostrarModal(true);
  };
  
  // --- FUNCIÓN PRINCIPAL DE GUARDADO (CREAR O EDITAR) ---
  const guardarEvento = async () => {
    if (!nuevoEvento.cliente || !nuevoEvento.mascota || !nuevoEvento.dni)
      return alert("Completa los campos de Cliente, DNI y Mascota.");
    
    if (serviciosRegistrados.length === 0) {
      return alert("Debe registrar al menos un servicio para la cita.");
    }

    // Usar la duración total de los servicios
    const duracionCitaTotal = totalDuracion; 

    const start = new Date(`${nuevoEvento.date}T${nuevoEvento.startTime}`);
    const end = new Date(start.getTime() + duracionCitaTotal * 60000);

    // Verificar solapamiento (se ignorará el evento actual si estamos editando)
    if (horaOcupada(start, end, nuevoEvento.id)) {
      return alert("⚠️ Ya existe una cita en este horario. Elige otro horario.");
    }


    // Preparar la descripción con los servicios detallados
    const serviciosLista = serviciosRegistrados.map(s => 
        `• ${s.nombre_servicio} (${s.cantidad}x $${s.valor_servicio.toFixed(2)})  Subtotal: $${s.subtotal.toFixed(2)} con ${s.nombre_veterinario}. Adicionales: ${s.adicionales || 'N/A'}`
    ).join('\n');

    const eventoResource = {
      summary: `${nuevoEvento.mascota} - Total: $${totalCosto.toFixed(2)}`,
      description: `**CLIENTE Y MASCOTA**
Cliente: ${nuevoEvento.cliente} (DNI: ${nuevoEvento.dni})
Mascota: ${nuevoEvento.mascota}
Estado: ${nuevoEvento.estado}
Costo Total: $${totalCosto.toFixed(2)}
Duración Total: ${duracionCitaTotal} min

**SERVICIOS REGISTRADOS**
${serviciosLista}

**BONO INICIAL / ABONO:** $${bonoTemporal.toFixed(2)}

**OBSERVACIONES**
${nuevoEvento.description || 'No hay observaciones adicionales.'}
      `.trim(),
      start: { dateTime: start.toISOString(), timeZone: "America/Lima" },
      end: { dateTime: end.toISOString(), timeZone: "America/Lima" },
    };

    try {
      if (nuevoEvento.id) {
        // Lógica de EDICIÓN (UPDATE)
        await window.gapi.client.calendar.events.update({ 
          calendarId: "primary", 
          eventId: nuevoEvento.id, 
          resource: eventoResource 
        });
        alert("Cita actualizada exitosamente.");
      } else {
        // Lógica de CREACIÓN (INSERT)
        await window.gapi.client.calendar.events.insert({ calendarId: "primary", resource: eventoResource });
        alert("Cita agendada exitosamente.");
      }
    } catch (error) {
      console.error("Error al guardar/actualizar evento:", error);
      alert(`Ocurrió un error al guardar la cita. ¿Está su sesión de Google activa? Error: ${error}`);
    }


    setMostrarModal(false);
    setServiciosRegistrados([]); // Limpiar servicios después de guardar
    // Resetear el estado de nuevoEvento, limpiando el ID de edición
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
      estado: "", 
    }));
    cargarEventos();
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
                      // Lógica para inicializar el modal en modo CREACIÓN
                      setNuevoEvento((prev) => ({
                        id: '', // MUY IMPORTANTE: Limpiar el ID para CREAR
                        summary: "",
                        description: "",
                        dni: "",
                        cliente: "",
                        clienteId: 0,
                        mascota: "",
                        servicio: "",
                        colaborador: prev.colaborador || "",
                        date: fechaSeleccionada.toISOString().split("T")[0],
                        startTime: "10:00",
                        duracion: "30",
                        estado: "",
                      }));
                      setServiciosRegistrados([]); // Limpiar servicios al abrir
                      setBonoTemporal(0); // Limpiar bono al abrir
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
                    // Simple parsing para mostrar datos
                    const detalles =
                      e.description?.split("\n").reduce((acc: any, linea) => {
                        const [k, v] = linea.split(":");
                        if (k && v && k.trim().length > 0) acc[k.trim()] = v.trim();
                        return acc;
                      }, {}) || {};
                    const inicio = new Date(e.start.dateTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
                    const fin = new Date(e.end.dateTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
                    return (
                      <div key={e.id} className="cita-card">
                        <p><strong>Cliente:</strong> {detalles.Cliente || 'N/A'}</p>
                        <p><strong>Mascota:</strong> {detalles.Mascota || 'N/A'}</p>
                        <p><strong>Costo Total:</strong> {detalles["Costo Total"] || 'N/A'}</p>
                        <p><strong>Hora:</strong> {inicio} - {fin}</p>
                        {/* Llama a la función de edición */}
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

      {/* ======================= MODAL NUEVA/EDITAR CITA ======================= */}
      {mostrarModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>{nuevoEvento.id ? `Editar cita #${nuevoEvento.id}` : "Agendar nueva cita 🗓️"}</h3>
            
            {/* --- COLUMNA IZQUIERDA: CLIENTE Y MASCOTA --- */}
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
            
            {/* --- COLUMNA DERECHA: TIEMPO Y ESTADO --- */}
            <div className="col-der"> 
              <label>Fecha *</label>
              <input type="date" value={nuevoEvento.date} onChange={(e) => setNuevoEvento({ ...nuevoEvento, date: e.target.value })} />
              <label>Hora *</label>
              <input type="time" value={nuevoEvento.startTime} onChange={(e) => setNuevoEvento({ ...nuevoEvento, startTime: e.target.value })} />
              
            </div>
            
            {/* --- SECCIÓN DE SERVICIOS (Full-Width) --- */}
            <div className="full-width-section">
              <h3>🛠 Servicios</h3>

              <div className="service-input-grid" id="serviceFormInputs">
                {/* 1. Servicio */}
                <div>
                  <label htmlFor="id_servicio">Servicio (id_servicio) <span className="required">*</span></label>
                  <select
                    id="id_servicio"
                    name="id_servicio"
                    value={servicioTemporal.id_servicio}
                    onChange={(e) => setServicioTemporal({ ...servicioTemporal, id_servicio: e.target.value })}
                  >
                    <option value="">Seleccione un servicio</option>
                    {serviciosDisponibles.map(s => (
                      <option key={s.id} value={s.id}>
                        {s.nombre} ({s.duracion} min)
                      </option>
                    ))}
                  </select>
                </div>

                {/* 2. Valor Unitario (EDITABLE) */}
                <div>
                  <label htmlFor="valor_servicio">Valor Unitario ($) <span className="required">*</span></label>
                  <input
                    type="number"
                    id="valor_servicio"
                    min="0"
                    step="0.01"
                    placeholder="0.00"
                    value={servicioTemporal.valor_servicio.toFixed(2)}
                    onChange={(e) => setServicioTemporal({ ...servicioTemporal, valor_servicio: parseFloat(e.target.value) || 0 })}
                  />
                </div>

                {/* 3. Cantidad (EDITABLE) */}
                <div>
                  <label htmlFor="cantidad">Cant. (cantidad)</label>
                  <input
                    type="number"
                    id="cantidad"
                    min="1"
                    step="1"
                    value={servicioTemporal.cantidad}
                    onChange={(e) => setServicioTemporal({ ...servicioTemporal, cantidad: parseInt(e.target.value) || 1 })}
                  />
                </div>
                
                {/* 4. Duración Unit. (EDITABLE) */}
                <div>
                  <label htmlFor="duracion_min">Duración Unit. (min)</label>
                  <input
                    type="number"
                    id="duracion_min"
                    min="5"
                    step="5"
                    value={servicioTemporal.duracion_min}
                    onChange={(e) => setServicioTemporal({ ...servicioTemporal, duracion_min: parseInt(e.target.value) || 0 })}
                  />
                </div>
                
                {/* 6. Veterinario */}
                <div>
                  <label htmlFor="id_veterinario">Veterinario <span className="required">*</span></label>
                  <select
                    id="id_veterinario"
                    name="id_veterinario"
                    value={servicioTemporal.id_veterinario}
                    onChange={(e) => setServicioTemporal({ ...servicioTemporal, id_veterinario: e.target.value })}
                  >
                    <option value="">Seleccione...</option>
                    {colaboradores.map(c => (
                      <option key={c.id} value={c.id}>
                        {c.nombre} (ID {c.id})
                      </option>
                    ))}
                  </select>
                </div>

                {/* 7. Adicionales */}
                <div>
                  <label htmlFor="adicionales">Adicionales</label>
                  <input
                    type="text"
                    id="adicionales"
                    placeholder="Color, tipo de corte..."
                    value={servicioTemporal.adicionales}
                    onChange={(e) => setServicioTemporal({ ...servicioTemporal, adicionales: e.target.value })}
                  />
                </div>

                {/* 8. Botón Agregar */}
                <div>
                  <button
                    type="button"
                    id="btnAddService"
                    className="btn-primary"
                    onClick={agregarServicio}
                    style={{marginTop: "20px"}} 
                  >
                    ➕ Agregar
                  </button>
                </div>
              </div>

{/* 5. BONO INICIAL (NUEVO CAMPO) */}
                <div>
                  <label htmlFor="bono_inicial">Bono Inicial ($)</label>
                  <input
                    type="number"
                    id="bono_inicial"
                    min="0"
                    step="0.01"
                    placeholder="0.00"
                    value={bonoTemporal.toFixed(2)}
                    onChange={(e) => setBonoTemporal(parseFloat(e.target.value) || 0)}
                  />
                </div>

              <h4>Detalle de Servicios:</h4>
              <table className="service-table">
                <thead>
                  <tr>
                    <th>Servicio</th>
                    <th>Responsable (ID)</th>
                    <th>Cant.</th>
                    <th>Duración Total (min)</th>
                    <th>Valor Unitario</th>
                    <th>Subtotal</th>
                    <th>Acción</th>
                  </tr>
                </thead>
                <tbody id="serviceTableBody">
                  {serviciosRegistrados.map((s, index) => (
                    <tr key={index}>
                      <td style={{textAlign: "left"}}>
                          <strong>{s.nombre_servicio}</strong>
                          {s.adicionales && <><br/><small>{s.adicionales}</small></>}
                      </td>
                      <td>{s.nombre_veterinario} ({s.id_veterinario})</td>
                      <td>{s.cantidad}</td>
                      <td>{s.duracion_total} min</td>
                      <td>$ {s.valor_servicio.toFixed(2)}</td>
                      {/* CÓDIGO CORREGIDO: SE ELIMINA LA COLUMNA EXTRA DE BONO INICIAL EN ESTA FILA */}
                      <td>$ {s.subtotal.toFixed(2)}</td>
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

                {/* CÓDIGO CORREGIDO: SE MODIFICA EL TFOOT PARA MOSTRAR LA RESTA DEL BONO */}
                <tfoot>
                  {/* Fila para el Total de Duración */}
                  <tr>
                    <td colSpan={3} style={{ textAlign: "right" }}>Total Duración:</td>
                    <td id="totalDuracion"><strong>{totalDuracion} min</strong></td>
                    {/* Dejamos las dos últimas columnas vacías para que la tabla sea de 7 columnas */}
                    <td colSpan={3}></td>
                  </tr>
                  {/* Fila para el Subtotal de Servicios (Suma de todos los servicios, calculado como Total + Bono) */}
                  <tr>
                    <td colSpan={5} style={{ textAlign: "right", fontWeight: "bold" }}>Subtotal Servicios:</td>
                    {/* Muestra el subtotal real (totalCosto + bonoTemporal) */}
                    <td style={{ fontWeight: "bold" }}>$ {(totalCosto ).toFixed(2)}</td>
                    <td></td>
                  </tr>
                  {/* Fila para el Bono Inicial (Resta) */}
                  <tr className="bono-row">
                    <td colSpan={5} style={{ textAlign: "right", fontWeight: "bold" }}>Bono Inicial / Abono (-):</td>
                    <td style={{ fontWeight: "bold", color: "red" }}>$ {bonoTemporal.toFixed(2)}</td>
                    <td></td>
                  </tr>
                  {/* Fila para el Total Final de la Cita (El resultado de la resta) */}
                  <tr className="total-row">
                    <td colSpan={5} style={{ textAlign: "right" }}>Costo Total Cita:</td>
                    <td id="totalCitaDisplay"><strong>$ {(totalCosto-bonoTemporal).toFixed(2)}</strong></td>
                    <td></td>
                  </tr>
                </tfoot>
              </table>
            </div>
            <label>Estado *</label>
              <select value={nuevoEvento.estado} onChange={(e) => setNuevoEvento({ ...nuevoEvento, estado: e.target.value })}>
                <option value="">Seleccione...</option>
                <option value="Pendiente">Pendiente</option>
                <option value="Confirmado">Confirmado</option>
                <option value="Cancelado">Cancelado</option>
              </select>
            <label className="label-obs">Observaciones</label>
            <textarea className="textarea-obs" value={nuevoEvento.description} onChange={(e) => setNuevoEvento({ ...nuevoEvento, description: e.target.value })} />

            <div className="acciones-modal">
              <button className="btn-agregar" onClick={guardarEvento}>
                💾 {nuevoEvento.id ? 'Actualizar Cita' : 'Guardar Cita'}
              </button>
              <button className="btn-cerrar" onClick={() => setMostrarModal(false)}>❌ Cancelar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Agenda_general;