import { useCallback, useEffect, useMemo, useState } from "react";
import type { FormEvent } from "react";
import Swal from "sweetalert2";
import Br_administrativa from "../../../components/barra_administrativa/Br_administrativa";
import IST from "../../../components/proteccion/IST";
import "./CajaDiaria.css";

type MedioPago = { id: number; nombre: string };
type Cliente = { id: number; nombre: string; documento?: string };
type Producto = { id: number; nombre: string; stock: number };
type Servicio = { id: number; nombre: string };

type Movimiento = {
  id: number;
  tipo: "INGRESO" | "EGRESO" | "REPOSICION";
  concepto: string;
  monto: number;
  medioPago: string;
  medioPagoId: number;
  usuario: string;
  cliente?: string;
  asociado?: string;
  producto?: string;
  cantidadProducto?: number;
  servicio?: string;
  fechaHora: string;
  comprobanteNombre?: string;
  tieneComprobante: boolean;
};

type ResumenMedio = {
  medioPagoId: number;
  medioPago: string;
  ingresos: number;
  reposiciones: number;
  egresos: number;
  neto: number;
};

type EstadoCaja = {
  fecha: string;
  abierta: boolean;
  saldoAperturaArrastrado: boolean;
  estado: string;
  saldoAperturaEfectivo: number;
  saldoEfectivoEsperado: number;
  totalIngresos: number;
  totalReposiciones: number;
  totalEgresos: number;
  resumenPorMedio: ResumenMedio[];
  movimientos: Movimiento[];
  saldoEfectivoContado?: number;
  diferencia?: number;
  observacionesCierre?: string;
};

const fechaLocal = () => {
  const ahora = new Date();
  return `${ahora.getFullYear()}-${String(ahora.getMonth() + 1).padStart(2, "0")}-${String(ahora.getDate()).padStart(2, "0")}`;
};

const dinero = (valor?: number | null) => `S/ ${Number(valor || 0).toFixed(2)}`;

const datosDe = (response: any) => response?.data?.data ?? response?.data ?? [];

function CajaDiaria() {
  const [minimizado, setMinimizado] = useState(false);
  const [estado, setEstado] = useState<EstadoCaja | null>(null);
  const [mediosPago, setMediosPago] = useState<MedioPago[]>([]);
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [productos, setProductos] = useState<Producto[]>([]);
  const [servicios, setServicios] = useState<Servicio[]>([]);
  const [cargando, setCargando] = useState(true);
  const [cargandoMovimiento, setCargandoMovimiento] = useState(false);
  const [historial, setHistorial] = useState<Movimiento[]>([]);
  const [desde, setDesde] = useState(fechaLocal());
  const [hasta, setHasta] = useState(fechaLocal());
  const [filtroMedio, setFiltroMedio] = useState("");
  const [filtroTipo, setFiltroTipo] = useState("");

  const [saldoApertura, setSaldoApertura] = useState("0.00");
  const [tipo, setTipo] = useState<"INGRESO" | "EGRESO" | "REPOSICION">("INGRESO");
  const [concepto, setConcepto] = useState("");
  const [monto, setMonto] = useState("");
  const [medioPagoId, setMedioPagoId] = useState("");
  const [clienteId, setClienteId] = useState("");
  const [clienteReferencia, setClienteReferencia] = useState("");
  const [productoId, setProductoId] = useState("");
  const [cantidadProducto, setCantidadProducto] = useState("");
  const [servicioId, setServicioId] = useState("");
  const [servicioReferencia, setServicioReferencia] = useState("");
  const [agendaId, setAgendaId] = useState("");
  const [comprobante, setComprobante] = useState<File | null>(null);
  const [efectivoContado, setEfectivoContado] = useState("");
  const [observacionesCierre, setObservacionesCierre] = useState("");

  const usuarioActivo = sessionStorage.getItem("usuario") || "Usuario activo";
  const productoSeleccionado = useMemo(
    () => productos.find((producto) => String(producto.id) === productoId),
    [productos, productoId],
  );
  const totalesFiltrados = useMemo(() => historial.reduce((totales, movimiento) => {
    if (movimiento.tipo === "INGRESO") totales.ingresos += Number(movimiento.monto);
    if (movimiento.tipo === "REPOSICION") totales.reposiciones += Number(movimiento.monto);
    if (movimiento.tipo === "EGRESO") totales.egresos += Number(movimiento.monto);
    return totales;
  }, { ingresos: 0, reposiciones: 0, egresos: 0 }), [historial]);

  const cargarEstado = useCallback(async () => {
    const response = await IST.get<EstadoCaja>("/caja/hoy");
    const caja = response.data;
    setEstado(caja);
    setSaldoApertura(Number(caja.saldoAperturaEfectivo || 0).toFixed(2));
    setEfectivoContado(Number(caja.saldoEfectivoEsperado || 0).toFixed(2));
  }, []);

  const cargarCatalogos = useCallback(async () => {
    const [medios, listaClientes, listaProductos, listaServicios] = await Promise.all([
      IST.get("/medios-pago"),
      IST.get("/clientes"),
      IST.get("/productos", { params: { pagina: 0, tamanio: 200, soloActivos: true } }),
      IST.get("/servicios"),
    ]);

    setMediosPago(datosDe(medios));
    setClientes(datosDe(listaClientes));
    const productosData = datosDe(listaProductos);
    setProductos(Array.isArray(productosData) ? productosData : productosData?.contenido || []);
    setServicios(datosDe(listaServicios));
  }, []);

  const cargarHistorial = useCallback(async () => {
    const response = await IST.get<Movimiento[]>("/caja/movimientos", {
      params: {
        desde,
        hasta,
        medioPagoId: filtroMedio || undefined,
        tipo: filtroTipo || undefined,
      },
    });
    setHistorial(response.data || []);
  }, [desde, hasta, filtroMedio, filtroTipo]);

  const cargarTodo = useCallback(async () => {
    setCargando(true);
    try {
      await Promise.all([cargarEstado(), cargarCatalogos()]);
    } catch (error: any) {
      Swal.fire("No se pudo cargar Caja", error?.response?.data?.mensaje || "Revise la conexión con el servidor.", "error");
    } finally {
      setCargando(false);
    }
  }, [cargarCatalogos, cargarEstado]);

  useEffect(() => {
    void cargarTodo();
  }, [cargarTodo]);

  useEffect(() => {
    void cargarHistorial().catch((error: any) => {
      Swal.fire("No se pudo cargar el historial", error?.response?.data?.mensaje || "Revise las fechas seleccionadas.", "error");
    });
  }, [cargarHistorial]);

  const abrirCaja = async (event: FormEvent) => {
    event.preventDefault();
    try {
      const response = await IST.post<EstadoCaja>("/caja/abrir", {
        saldoAperturaEfectivo: Number(saldoApertura),
      });
      setEstado(response.data);
      Swal.fire("Caja abierta", "La jornada está lista para registrar movimientos.", "success");
      await cargarHistorial();
    } catch (error: any) {
      Swal.fire("No se pudo abrir la caja", error?.response?.data?.mensaje || "Revise el saldo y vuelva a intentarlo.", "error");
    }
  };

  const limpiarFormulario = () => {
    setTipo("INGRESO");
    setConcepto("");
    setMonto("");
    setMedioPagoId("");
    setClienteId("");
    setClienteReferencia("");
    setProductoId("");
    setCantidadProducto("");
    setServicioId("");
    setServicioReferencia("");
    setAgendaId("");
    setComprobante(null);
    const input = document.getElementById("caja-comprobante") as HTMLInputElement | null;
    if (input) input.value = "";
  };

  const registrarMovimiento = async (event: FormEvent) => {
    event.preventDefault();
    if (!estado?.abierta) return;
    if (Number(monto) <= 0) {
      Swal.fire("Monto inválido", "El monto debe ser mayor que cero.", "warning");
      return;
    }
    if (productoId && Number(cantidadProducto) <= 0) {
      Swal.fire("Cantidad inválida", "Indique cuántas unidades se compraron o vendieron.", "warning");
      return;
    }

    const movimiento = {
      tipo,
      concepto: concepto.trim(),
      monto: Number(monto),
      medioPagoId: Number(medioPagoId),
      clienteId: clienteId ? Number(clienteId) : null,
      clienteReferencia: clienteReferencia.trim() || null,
      productoId: productoId ? Number(productoId) : null,
      cantidadProducto: productoId ? Number(cantidadProducto) : null,
      servicioId: servicioId ? Number(servicioId) : null,
      servicioReferencia: servicioReferencia.trim() || null,
      agendaId: agendaId ? Number(agendaId) : null,
    };

    const form = new FormData();
    form.append("movimiento", new Blob([JSON.stringify(movimiento)], { type: "application/json" }));
    if (comprobante) form.append("comprobante", comprobante);

    setCargandoMovimiento(true);
    try {
      await IST.post("/caja/movimientos", form);
      Swal.fire("Movimiento registrado", "Se guardó el movimiento con el usuario y la hora actuales.", "success");
      limpiarFormulario();
      await Promise.all([cargarEstado(), cargarHistorial()]);
    } catch (error: any) {
      Swal.fire("No se pudo registrar", error?.response?.data?.mensaje || "Revise los datos e inténtelo nuevamente.", "error");
    } finally {
      setCargandoMovimiento(false);
    }
  };

  const cerrarCaja = async (event: FormEvent) => {
    event.preventDefault();
    if (!estado?.abierta) return;
    const diferencia = Number(efectivoContado) - Number(estado.saldoEfectivoEsperado);
    if (diferencia !== 0 && !observacionesCierre.trim()) {
      Swal.fire("Falta explicar el descuadre", "Escriba el motivo antes de cerrar.", "warning");
      return;
    }
    try {
      const response = await IST.post<EstadoCaja>("/caja/cerrar", {
        saldoEfectivoContado: Number(efectivoContado),
        observaciones: observacionesCierre,
      });
      setEstado(response.data);
      Swal.fire("Caja cerrada", "El arqueo y el resultado del cierre quedaron registrados.", "success");
      await cargarHistorial();
    } catch (error: any) {
      Swal.fire("No se pudo cerrar la caja", error?.response?.data?.mensaje || "Revise los datos e inténtelo nuevamente.", "error");
    }
  };

  const verComprobante = async (id: number) => {
    try {
      const response = await IST.get(`/caja/movimientos/${id}/comprobante`, { responseType: "blob" });
      const url = URL.createObjectURL(response.data);
      window.open(url, "_blank", "noopener,noreferrer");
      window.setTimeout(() => URL.revokeObjectURL(url), 60_000);
    } catch {
      Swal.fire("No se pudo abrir el comprobante", "Vuelva a intentarlo.", "error");
    }
  };

  if (cargando || !estado) {
    return (
      <div id="caja-page">
        <Br_administrativa onMinimizeChange={setMinimizado} />
        <main className={minimizado ? "minimize" : ""}>
          <div className="caja-shell">
            <div className="caja-card caja-loading" role="status" aria-live="polite">
              Consultando el saldo y los movimientos actuales de caja…
            </div>
          </div>
        </main>
      </div>
    );
  }

  return (
    <div id="caja-page">
      <Br_administrativa onMinimizeChange={setMinimizado} />
      <main className={minimizado ? "minimize" : ""}>
        <div className="caja-shell">
          <header className="caja-header">
            <div>
              <span className="caja-kicker">CONTROL DIARIO</span>
              <h1>Caja</h1>
              <p>Ingresos, gastos, reposiciones y arqueo de la jornada.</p>
            </div>
            <div className={`caja-status ${estado.abierta ? "is-open" : "is-closed"}`}>
              <span className="caja-status-dot" />
              {estado.abierta ? "Caja abierta" : estado.estado === "CERRADA" ? "Caja cerrada" : "Pendiente de apertura"}
            </div>
          </header>

          <section className="caja-summary-grid">
            <article className="caja-summary-card">
              <span>Saldo de apertura</span>
              <strong>{dinero(estado.saldoAperturaEfectivo)}</strong>
              <small>{estado.fecha}</small>
            </article>
            <article className="caja-summary-card">
              <span>Efectivo esperado</span>
              <strong>{dinero(estado.saldoEfectivoEsperado)}</strong>
              <small>El efectivo se calcula por separado</small>
            </article>
            <article className="caja-summary-card is-positive">
              <span>Ingresos del día</span>
              <strong>{dinero(estado.totalIngresos)}</strong>
              <small>Incluye todos los medios de pago</small>
            </article>
            <article className="caja-summary-card is-negative">
              <span>Gastos del día</span>
              <strong>{dinero(estado.totalEgresos)}</strong>
              <small>Registrados por el personal autorizado</small>
            </article>
          </section>

          {!estado.abierta && estado.estado !== "CERRADA" && (
            <section className="caja-card caja-open-card">
              <div>
                <h2>Abrir caja del día</h2>
                <p>El monto se toma del cierre anterior. Para agregar dinero adicional, use “Reposición” después de abrir.</p>
              </div>
              <form onSubmit={abrirCaja} className="caja-inline-form">
                <label>
                  Efectivo inicial
                  <input type="number" min="0" step="0.01" value={saldoApertura} readOnly={estado.saldoAperturaArrastrado}
                    onChange={(event) => setSaldoApertura(event.target.value)} required />
                </label>
                <button className="caja-primary-button" type="submit">Abrir caja</button>
              </form>
            </section>
          )}

          <section className="caja-card">
            <div className="caja-section-heading">
              <div>
                <h2>Resumen por método de pago</h2>
                <p>Consulta cuánto ingresó y salió por cada medio.</p>
              </div>
            </div>
            {estado.resumenPorMedio?.length ? (
              <div className="caja-method-grid">
                {estado.resumenPorMedio.map((medio) => (
                  <article className="caja-method-card" key={medio.medioPagoId}>
                    <h3>{medio.medioPago}</h3>
                    <div><span>Ingresos</span><strong>{dinero(medio.ingresos)}</strong></div>
                    {Number(medio.reposiciones) > 0 && <div><span>Reposiciones</span><strong>{dinero(medio.reposiciones)}</strong></div>}
                    <div><span>Egresos</span><strong>{dinero(medio.egresos)}</strong></div>
                    <div className="caja-method-net"><span>Neto</span><strong>{dinero(medio.neto)}</strong></div>
                  </article>
                ))}
              </div>
            ) : <p className="caja-empty">Aún no hay movimientos registrados hoy.</p>}
          </section>

          {estado.abierta && (
            <div className="caja-work-grid">
              <section className="caja-card">
                <div className="caja-section-heading">
                  <div>
                    <h2>Registrar movimiento</h2>
                    <p>Usuario activo: <strong>{usuarioActivo}</strong>. Fecha y hora se asignan en el servidor.</p>
                  </div>
                </div>
                <form className="caja-form-grid" onSubmit={registrarMovimiento}>
                  <label>
                    Tipo de movimiento
                    <select value={tipo} onChange={(event) => setTipo(event.target.value as typeof tipo)}>
                      <option value="INGRESO">Ingreso por pago</option>
                      <option value="EGRESO">Gasto de la veterinaria</option>
                      <option value="REPOSICION">Reposición de efectivo</option>
                    </select>
                  </label>
                  <label>
                    Concepto
                    <input value={concepto} onChange={(event) => setConcepto(event.target.value)} maxLength={180} required placeholder="Ej. Consulta o compra de insumos" />
                  </label>
                  <label>
                    Monto (S/)
                    <input type="number" min="0.01" step="0.01" value={monto} onChange={(event) => setMonto(event.target.value)} required />
                  </label>
                  <label>
                    Método de pago
                    <select value={medioPagoId} onChange={(event) => setMedioPagoId(event.target.value)} required>
                      <option value="">Selecciona un método</option>
                      {mediosPago.map((medio) => <option value={medio.id} key={medio.id}>{medio.nombre}</option>)}
                    </select>
                  </label>
                  <label>
                    Cliente (opcional)
                    <select value={clienteId} onChange={(event) => setClienteId(event.target.value)}>
                      <option value="">Sin cliente asociado</option>
                      {clientes.map((cliente) => <option value={cliente.id} key={cliente.id}>{cliente.nombre} {cliente.documento ? `— ${cliente.documento}` : ""}</option>)}
                    </select>
                  </label>
                  <label>
                    Cliente no registrado (opcional)
                    <input value={clienteReferencia} onChange={(event) => setClienteReferencia(event.target.value)} maxLength={160} placeholder="Nombre del cliente" />
                  </label>
                  <label>
                    Servicio relacionado (opcional)
                    <select value={servicioId} onChange={(event) => setServicioId(event.target.value)}>
                      <option value="">Selecciona un servicio</option>
                      {servicios.map((servicio) => <option value={servicio.id} key={servicio.id}>{servicio.nombre}</option>)}
                    </select>
                  </label>
                  <label>
                    Otro producto o servicio (opcional)
                    <input value={servicioReferencia} onChange={(event) => setServicioReferencia(event.target.value)} maxLength={160} placeholder="Descripción asociada" />
                  </label>
                  <label>
                    Producto (para actualizar inventario)
                    <select value={productoId} onChange={(event) => setProductoId(event.target.value)}>
                      <option value="">Sin actualización de inventario</option>
                      {productos.map((producto) => <option value={producto.id} key={producto.id}>{producto.nombre} — stock: {producto.stock}</option>)}
                    </select>
                  </label>
                  {productoId && (
                    <label>
                      Unidades {tipo === "INGRESO" ? "vendidas" : "compradas"}
                      <input type="number" min="1" step="1" value={cantidadProducto} onChange={(event) => setCantidadProducto(event.target.value)} required />
                      {tipo === "INGRESO" && productoSeleccionado && <small>Stock disponible: {productoSeleccionado.stock}</small>}
                    </label>
                  )}
                  <label>
                    ID de cita (opcional)
                    <input type="number" min="1" value={agendaId} onChange={(event) => setAgendaId(event.target.value)} placeholder="Para asociar un pago de cita" />
                  </label>
                  <label className="caja-file-field">
                    Comprobante (PDF o imagen, hasta 8 MB)
                    <input id="caja-comprobante" type="file" accept="application/pdf,image/jpeg,image/png,image/webp" onChange={(event) => setComprobante(event.target.files?.[0] || null)} />
                  </label>
                  <div className="caja-form-actions">
                    <button className="caja-primary-button" type="submit" disabled={cargandoMovimiento}>
                      {cargandoMovimiento ? "Guardando…" : "Registrar movimiento"}
                    </button>
                  </div>
                </form>
                {tipo === "REPOSICION" && <p className="caja-note">Las reposiciones solo pueden registrarse como efectivo.</p>}
                {tipo === "EGRESO" && <p className="caja-note">El sistema rechazará el gasto si supera el saldo disponible del método seleccionado.</p>}
              </section>

              <section className="caja-card caja-close-card">
                <div className="caja-section-heading">
                  <div>
                    <h2>Arqueo y cierre</h2>
                    <p>Cuenta el efectivo físico y registra el motivo si no coincide.</p>
                  </div>
                </div>
                <div className="caja-close-expected">
                  <span>Efectivo calculado</span>
                  <strong>{dinero(estado.saldoEfectivoEsperado)}</strong>
                </div>
                <form onSubmit={cerrarCaja} className="caja-close-form">
                  <label>
                    Efectivo contado (S/)
                    <input type="number" min="0" step="0.01" value={efectivoContado} onChange={(event) => setEfectivoContado(event.target.value)} required />
                  </label>
                  <label>
                    Observaciones del cierre
                    <textarea value={observacionesCierre} onChange={(event) => setObservacionesCierre(event.target.value)} rows={4} placeholder="Obligatorio si hay descuadre" />
                  </label>
                  <button className="caja-danger-button" type="submit">Registrar arqueo y cerrar</button>
                </form>
              </section>
            </div>
          )}

          {estado.estado === "CERRADA" && (
            <section className="caja-card caja-closed-result">
              <div className="caja-section-heading">
                <div><h2>Resultado del cierre</h2><p>Reporte diario del arqueo de caja.</p></div>
                <button className="caja-secondary-button no-print" type="button" onClick={() => window.print()}>Imprimir reporte</button>
              </div>
              <div className="caja-close-result-grid">
                <div><span>Esperado</span><strong>{dinero(estado.saldoEfectivoEsperado)}</strong></div>
                <div><span>Contado</span><strong>{dinero(estado.saldoEfectivoContado)}</strong></div>
                <div><span>Diferencia</span><strong>{dinero(estado.diferencia)}</strong></div>
              </div>
              {estado.observacionesCierre && <p><strong>Observaciones:</strong> {estado.observacionesCierre}</p>}
            </section>
          )}

          <section className="caja-card caja-history-card">
            <div className="caja-section-heading">
              <div>
                <h2>Historial de movimientos</h2>
                <p>Filtra por fechas, método de pago y tipo de movimiento.</p>
              </div>
            </div>
            <div className="caja-filters">
              <label>Desde<input type="date" value={desde} onChange={(event) => setDesde(event.target.value)} /></label>
              <label>Hasta<input type="date" value={hasta} onChange={(event) => setHasta(event.target.value)} /></label>
              <label>Método<select value={filtroMedio} onChange={(event) => setFiltroMedio(event.target.value)}><option value="">Todos</option>{mediosPago.map((medio) => <option key={medio.id} value={medio.id}>{medio.nombre}</option>)}</select></label>
              <label>Tipo<select value={filtroTipo} onChange={(event) => setFiltroTipo(event.target.value)}><option value="">Todos</option><option value="INGRESO">Ingresos</option><option value="EGRESO">Gastos</option><option value="REPOSICION">Reposiciones</option></select></label>
              <button type="button" className="caja-secondary-button" onClick={() => void cargarHistorial()}>Aplicar filtros</button>
            </div>
            <div className="caja-filter-totals">
              <span>Ingresos: <strong>{dinero(totalesFiltrados.ingresos)}</strong></span>
              <span>Reposiciones: <strong>{dinero(totalesFiltrados.reposiciones)}</strong></span>
              <span>Gastos: <strong>{dinero(totalesFiltrados.egresos)}</strong></span>
              <span>Neto: <strong>{dinero(totalesFiltrados.ingresos + totalesFiltrados.reposiciones - totalesFiltrados.egresos)}</strong></span>
            </div>
            <div className="caja-table-wrap">
              <table className="caja-table">
                <thead><tr><th>Fecha y hora</th><th>Tipo</th><th>Concepto</th><th>Asociado</th><th>Método</th><th>Usuario</th><th>Monto</th><th>Comprobante</th></tr></thead>
                <tbody>
                  {historial.length ? historial.map((movimiento) => (
                    <tr key={movimiento.id}>
                      <td>{new Date(movimiento.fechaHora).toLocaleString("es-PE")}</td>
                      <td><span className={`caja-type-pill type-${movimiento.tipo.toLowerCase()}`}>{movimiento.tipo}</span></td>
                      <td>{movimiento.concepto}{movimiento.cliente && <small>Cliente: {movimiento.cliente}</small>}</td>
                      <td>{movimiento.asociado || movimiento.producto || movimiento.servicio || "—"}{movimiento.cantidadProducto ? <small>Unidades: {movimiento.cantidadProducto}</small> : null}</td>
                      <td>{movimiento.medioPago}</td>
                      <td>{movimiento.usuario}</td>
                      <td className={movimiento.tipo === "EGRESO" ? "amount-negative" : "amount-positive"}>{dinero(movimiento.monto)}</td>
                      <td>{movimiento.tieneComprobante ? <button className="caja-link-button" type="button" onClick={() => void verComprobante(movimiento.id)}>{movimiento.comprobanteNombre || "Ver"}</button> : "—"}</td>
                    </tr>
                  )) : <tr><td colSpan={8} className="caja-no-results">No hay movimientos para esos filtros.</td></tr>}
                </tbody>
              </table>
            </div>
          </section>
        </div>
      </main>
    </div>
  );
}

export default CajaDiaria;
