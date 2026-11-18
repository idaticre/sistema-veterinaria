import { useState } from "react";
import Br_administrativa from "../../../components/barra_administrativa/Br_administrativa";
import IST from "../../../components/proteccion/IST";
import "./RecuperacionComprobantes.css";
import Swal from "sweetalert2";

interface Comprobante {
    id: number;
    serie: string;
    numero: string;
    nombreCliente: string;
    fechaEmision: string;
    total: number;
}

// 🔥 NOTA DE CREDITO: 13 tipos del catalogo de NUBEFACT
const TIPOS_NC = [
    { codigo: 1, descripcion: "Anulación de la operación" },
    { codigo: 2, descripcion: "Anulación por error en el RUC" },
    { codigo: 3, descripcion: "Corrección por descripción" },
    { codigo: 4, descripcion: "Descuento global" },
    { codigo: 5, descripcion: "Descuento por ítem" },
    { codigo: 6, descripcion: "Devolución total" },
    { codigo: 7, descripcion: "Devolución por ítem" },
    { codigo: 8, descripcion: "Bonificación" },
    { codigo: 9, descripcion: "Disminución en el valor" },
    { codigo: 10, descripcion: "Otros conceptos" },
    { codigo: 11, descripcion: "Ajustes de montos y/o fechas de pago" },
    { codigo: 12, descripcion: "Ajustes por diferencias de medida" },
    { codigo: 13, descripcion: "Ajustes por cambio de valores" }
];

const RecuperacionComprobantes = () => {
    const [minimizado, setMinimizado] = useState(false);

    const [tipo, setTipo] = useState("1"); // 1 = Factura, 2 = Boleta
    const [resultado, setResultado] = useState<Comprobante[]>([]);
    const [loading, setLoading] = useState(false);
    const [detalle, setDetalle] = useState<any>(null);
    const [mostrarDetalle, setMostrarDetalle] = useState(false);
    const [documento, setDocumento] = useState("");
    const [clienteId, setClienteId] = useState(0);

    // 🔥 NOTA DE CREDITO
    const [mostrarNC, setMostrarNC] = useState(false);
    const [ncTipo, setNcTipo] = useState(0);
    const [ncMotivo, setNcMotivo] = useState("");
    const [ncMonto, setNcMonto] = useState("");
    const [enviandoNC, setEnviandoNC] = useState(false);
    const [ultimoBuscar, setUltimoBuscar] = useState<"tipo" | "cliente">("tipo");

    // 🔥 BUSCAR CLIENTE POR DOCUMENTO (igual que en Facturación)
    const buscarCliente = async (doc: string) => {
        setDocumento(doc);

        // Validar longitud: Factura = RUC (11), Boleta = DNI (8).
        // Nota de credito: puede venir de una boleta o de una factura,
        // asi que acepta DNI (8) o RUC (11)
        const longitudValida =
            tipo === "1"
                ? doc.length === 11
                : tipo === "3"
                    ? doc.length === 8 || doc.length === 11
                    : doc.length === 8;

        if (!longitudValida) {
            setClienteId(0);
            return;
        }

        try {
            const response = await IST.get(`/clientes/documento/${doc}`);
            setClienteId(response.data.data.id);
        } catch (error: any) {
            setClienteId(0);

            // 404 = no existe cliente con ese documento (normal), no se avisa aquí
            if (error?.response?.status && error.response.status !== 404) {
                Swal.fire({
                    title: "Error desconocido",
                    text: "Refresque la página por favor",
                    icon: "error"
                });
            }
        }
    };

    // 🔥 BUSCAR POR TIPO
    const buscarPorTipo = async () => {
        setUltimoBuscar("tipo");

        try {
            setLoading(true);

            const response = await IST.get(`/comprobantes/tipo/${tipo}`);
            const data = response.data;

            setResultado(Array.isArray(data) ? data : [data]);
        } catch {
            Swal.fire({
                title: "Error",
                text: "al obtener los comprobantes, por favor refresque la página",
                icon: "error"
            });
        } finally {
            setLoading(false);
        }
    };

    // 🔥 BUSCAR POR CLIENTE
    const buscarPorCliente = async () => {
        setUltimoBuscar("cliente");

        if (!documento.trim()) {
            return Swal.fire({
                title: "Alerta",
                text: "Ingrese un número de documento",
                icon: "warning"
            });
        }

        if (!clienteId) {
            return Swal.fire({
                title: "Alerta",
                text: "No se encontró un cliente con ese documento",
                icon: "warning"
            });
        }

        try {
            setLoading(true);

            const response = await IST.get(`/comprobantes/cliente/${clienteId}`);
            const data = response.data;

            setResultado(Array.isArray(data) ? data : [data]);
        } catch {
            Swal.fire({
                title: "Alerta",
                text: "No se pudo obtener los comprobantes",
                icon: "warning"
            });
        } finally {
            setLoading(false);
        }
    };

    // 🔥 VER DETALLE
    const verComprobante = async (id: number) => {
        try {
            const response = await IST.get(`/comprobantes/${id}`);

            setDetalle(response.data);
            setMostrarDetalle(true);
        } catch {
            Swal.fire({
                title: "Error",
                text: "al obtener el comprobante, por favor refresque la página",
                icon: "error"
            });
        }
    };

    // 🔥 NOTA DE CREDITO: abrir el modal con valores limpios
    const abrirModalNC = () => {
        setNcTipo(0);
        setNcMotivo("");
        setNcMonto("");
        setMostrarNC(true);
    };

    // Refresca el listado respetando la busqueda que uso el usuario
    const refrescarListado = async () => {
        if (ultimoBuscar === "cliente" && clienteId) {
            await buscarPorCliente();
        } else {
            await buscarPorTipo();
        }
    };

    // 🔥 EMITIR NOTA DE CREDITO contra el comprobante abierto en el modal
    const emitirNotaCredito = async () => {
        if (!ncTipo) {
            return Swal.fire({
                title: "Alerta",
                text: "Selecciona el tipo de nota de crédito",
                icon: "warning"
            });
        }

        if (!ncMotivo.trim()) {
            return Swal.fire({
                title: "Alerta",
                text: "El motivo o sustento es obligatorio para SUNAT",
                icon: "warning"
            });
        }

        const esDescuento = ncTipo === 4 || ncTipo === 5;
        const monto = Number(ncMonto);

        if (esDescuento && (!ncMonto || monto <= 0 || monto > Number(detalle.total))) {
            return Swal.fire({
                title: "Alerta",
                text: `El monto a descontar debe ser mayor a 0 y menor o igual al total (S/ ${Number(detalle.total).toFixed(2)})`,
                icon: "warning"
            });
        }

        try {
            setEnviandoNC(true);

            const body: any = { ncTipo, motivo: ncMotivo.trim() };
            if (esDescuento) body.montoDescuento = monto;

            const response = await IST.post(
                `/comprobantes/${detalle.id}/nota-credito`,
                body
            );
            const r = response.data;

            // ACEPTADA o PENDIENTE: la nota quedo emitida (con PDF)
            if (r.estado === "ACEPTADA" || r.estado === "PENDIENTE") {
                setMostrarNC(false);
                setMostrarDetalle(false);

                Swal.fire({
                    title: r.estado === "ACEPTADA"
                        ? "Nota de crédito aceptada por SUNAT"
                        : "Nota de crédito emitida (pendiente de SUNAT)",
                    html:
                        `Nota <strong>${r.serie}-${r.numero}</strong> emitida correctamente.<br>` +
                        (r.enlace_del_pdf
                            ? `<a href="${r.enlace_del_pdf}" target="_blank" rel="noopener">🧾 Ver PDF</a>`
                            : ""),
                    icon: "success"
                });

                await refrescarListado();
                return;
            }

            // RECHAZADA o error: el backend revierte la fila y devuelve el
            // correlativo (guardado_en_bd = false lo confirma)
            Swal.fire({
                title: r.estado === "RECHAZADA"
                    ? "Nota de crédito rechazada por SUNAT"
                    : "No se pudo emitir la nota de crédito",
                html:
                    `${r.sunat_description || r.sunat_note || r.mensaje || "Error desconocido"}<br><br>` +
                    (r.guardado_en_bd === false
                        ? "<small>La nota no fue guardada y el correlativo fue devuelto (sin descuadre).</small>"
                        : "<small>La nota quedó registrada; puedes reintentarla desde el listado.</small>"),
                icon: "error"
            });

            if (r.guardado_en_bd === false) {
                await refrescarListado();
            }
        } catch (error: any) {
            Swal.fire({
                title: "Error",
                text: error?.response?.data?.message
                    || error?.response?.data?.mensaje
                    || "No se pudo emitir la nota de crédito",
                icon: "error"
            });
        } finally {
            setEnviandoNC(false);
        }
    };

    return (
        <>
            <Br_administrativa onMinimizeChange={setMinimizado} />

            <section
                id="recuperacion-comprobantes"
                className={minimizado ? "contenido-minimizado" : "contenido-normal"}
            >
                <div className="recuperacion-container">
                    <div className="header-recuperacion">
                        <h2>Recuperación de Comprobantes</h2>
                    </div>

                    <div className="recuperacion-card">
                        <div className="form-grid">
                            <div>
                                <label>Tipo Comprobante</label>

                                <select
                                    value={tipo}
                                    onChange={(e) => {
                                        setTipo(e.target.value);
                                        setDocumento("");
                                        setClienteId(0);
                                    }}
                                >
                                    <option value="1">Factura</option>
                                    <option value="2">Boleta</option>
                                    <option value="3">Nota de crédito</option>
                                </select>
                            </div>

                            <div>
                                <label>
                                    {tipo === "1" ? "RUC" : tipo === "3" ? "DNI o RUC" : "DNI"}
                                </label>

                                <input
                                    type="text"
                                    placeholder="Ingrese DNI o RUC"
                                    value={documento}
                                    onChange={(e) => buscarCliente(e.target.value)}
                                />
                            </div>
                        </div>

                        <div className="acciones">
                            <button
                                className="btn-buscar"
                                onClick={buscarPorTipo}
                                disabled={loading}
                            >
                                Buscar por Tipo
                            </button>

                            <button
                                className="btn-buscar"
                                onClick={buscarPorCliente}
                                disabled={loading}
                            >
                                Buscar por Cliente
                            </button>
                        </div>

                        <table className="tabla-comprobantes">
                            <thead>
                                <tr>
                                    <th>Serie</th>
                                    <th>Número</th>
                                    <th>Cliente</th>
                                    <th>Fecha</th>
                                    <th>Total</th>
                                    <th>Acción</th>
                                </tr>
                            </thead>

                            <tbody>
                                {resultado.length === 0 ? (
                                    <tr>
                                        <td colSpan={6} className="sin-datos">
                                            No existen comprobantes
                                        </td>
                                    </tr>
                                ) : (
                                    resultado.map((item) => (
                                        <tr key={item.id}>
                                            <td>{item.serie}</td>
                                            <td>{item.numero}</td>
                                            <td>{item.nombreCliente}</td>
                                            <td>{item.fechaEmision}</td>
                                            <td>S/ {Number(item.total).toFixed(2)}</td>

                                            <td>
                                                <button
                                                    className="btn-buscar"
                                                    onClick={() => verComprobante(item.id)}
                                                >
                                                    👁 Ver
                                                </button>
                                            </td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                        </table>

                        {mostrarDetalle && detalle && (
                            <div className="modal-overlay">
                                <div className="modal-comprobante">
                                    <div className="modal-header">
                                        <h3>
                                            {Number(detalle.tipoComprobante) === 1
                                                ? "Factura"
                                                : Number(detalle.tipoComprobante) === 3
                                                    ? "Nota de crédito"
                                                    : "Boleta"}{" "}
                                            {detalle.serie}-{detalle.numero}
                                        </h3>

                                        <button
                                            className="btn-cerrar"
                                            onClick={() => setMostrarDetalle(false)}
                                        >
                                            ✖ Cerrar
                                        </button>
                                    </div>

                                    {/* DATOS DEL CLIENTE */}
                                    <div className="info-grid">
                                        <p>
                                            <strong>Cliente:</strong> {detalle.nombreCliente}
                                        </p>
                                        <p>
                                            <strong>Documento:</strong> {detalle.clienteNumeroDoc || "—"}
                                        </p>
                                        <p>
                                            <strong>Fecha de emisión:</strong> {detalle.fechaEmision}
                                        </p>
                                        <p>
                                            <strong>Dirección:</strong> {detalle.clienteDireccion || "—"}
                                        </p>
                                        <p>
                                            <strong>Correo:</strong> {detalle.clienteCorreo || "—"}
                                        </p>
                                        <p>
                                            <strong>Moneda:</strong>{" "}
                                            {Number(detalle.moneda) === 1 ? "Soles (S/)" : `ID ${detalle.moneda}`}
                                        </p>
                                    </div>

                                    {/* MONTOS */}
                                    <div className="montos-comprobante">
                                        <p>
                                            <strong>Subtotal (sin IGV):</strong> S/{" "}
                                            {Number(detalle.totalGravada || 0).toFixed(2)}
                                        </p>
                                        <p>
                                            <strong>IGV (18%):</strong> S/{" "}
                                            {Number(detalle.totalIGV || 0).toFixed(2)}
                                        </p>
                                        {Number(detalle.totalAnticipio || 0) > 0 && (
                                            <p>
                                                <strong>Abono aplicado:</strong> S/{" "}
                                                {Number(detalle.totalAnticipio).toFixed(2)}
                                            </p>
                                        )}
                                        <p className="total-final">
                                            <strong>TOTAL:</strong> S/{" "}
                                            {Number(detalle.total).toFixed(2)}
                                        </p>
                                    </div>

                                    {/* NOTA DE CREDITO: enlace al comprobante original */}
                                    {Number(detalle.tipoComprobante) === 3 && (
                                        <div className="nc-vinculo">
                                            <p>
                                                <strong>Modifica al comprobante:</strong>{" "}
                                                {detalle.docModificaSerie}-{detalle.docModificaNumero}
                                            </p>

                                            {detalle.ncTipo && (
                                                <p>
                                                    <strong>Tipo de nota:</strong>{" "}
                                                    {TIPOS_NC.find(
                                                        (t) => t.codigo === Number(detalle.ncTipo)
                                                    )?.descripcion || `Tipo ${detalle.ncTipo}`}
                                                </p>
                                            )}

                                            {detalle.comprobanteOrigenId && (
                                                <button
                                                    className="btn-ver-original"
                                                    onClick={() =>
                                                        verComprobante(detalle.comprobanteOrigenId)
                                                    }
                                                >
                                                    ↩ Ver comprobante original
                                                </button>
                                            )}
                                        </div>
                                    )}

                                    {/* VER COMPROBANTE EN NUBEFACT */}
                                    {detalle.pdfurlNubefact ? (
                                        <div className="modal-acciones">
                                            <button
                                                className="btn-ver-pdf"
                                                onClick={() =>
                                                    window.open(detalle.pdfurlNubefact, "_blank", "noopener")
                                                }
                                            >
                                                🧾 Ver comprobante (PDF)
                                            </button>

                                            {/* EMITIR NOTA DE CREDITO: solo boletas y
                                                facturas ya aceptadas por SUNAT */}
                                            {(Number(detalle.tipoComprobante) === 1 ||
                                                Number(detalle.tipoComprobante) === 2) && (
                                                <button
                                                    className="btn-nota-credito"
                                                    onClick={abrirModalNC}
                                                >
                                                    📄 Nota de crédito
                                                </button>
                                            )}
                                        </div>
                                    ) : (
                                        <p className="pdf-no-disponible">
                                            ⚠ PDF no disponible: el comprobante aún no fue aceptado
                                            por SUNAT o fue rechazado.
                                        </p>
                                    )}

                                    {/* DETALLE DE SERVICIOS */}
                                    <table className="tabla-comprobantes">
                                        <thead>
                                            <tr>
                                                <th>Descripción</th>
                                                <th>Cant.</th>
                                                <th>P. Unit. (con IGV)</th>
                                                <th>Subtotal (sin IGV)</th>
                                                <th>IGV</th>
                                                <th>Total</th>
                                            </tr>
                                        </thead>

                                        <tbody>
                                            {detalle.detalles?.map((d: any, index: number) => (
                                                <tr key={index}>
                                                    <td>{d.descripcion}</td>
                                                    <td>{Number(d.cantidad)}</td>
                                                    <td>S/ {Number(d.precioUnitario).toFixed(2)}</td>
                                                    <td>S/ {Number(d.subtotal).toFixed(2)}</td>
                                                    <td>S/ {Number(d.igv).toFixed(2)}</td>
                                                    <td>S/ {Number(d.total).toFixed(2)}</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>

                                    {detalle.observaciones && (
                                        <p className="observaciones-modal">
                                            <strong>Observaciones:</strong> {detalle.observaciones}
                                        </p>
                                    )}
                                </div>
                            </div>
                        )}

                        {/* MODAL NOTA DE CREDITO */}
                        {mostrarNC && detalle && (
                            <div className="modal-overlay">
                                <div className="modal-nota-credito">
                                    <div className="modal-header">
                                        <h3>
                                            📄 Nota de crédito sobre{" "}
                                            {Number(detalle.tipoComprobante) === 1 ? "Factura" : "Boleta"}{" "}
                                            {detalle.serie}-{detalle.numero}
                                        </h3>

                                        <button
                                            className="btn-cerrar"
                                            onClick={() => setMostrarNC(false)}
                                        >
                                            ✖ Cerrar
                                        </button>
                                    </div>

                                    <p className="nc-origen">
                                        Total del comprobante:{" "}
                                        <strong>S/ {Number(detalle.total).toFixed(2)}</strong>
                                    </p>

                                    <div className="nc-form">
                                        <label>Tipo de nota de crédito *</label>

                                        <select
                                            value={ncTipo}
                                            onChange={(e) => setNcTipo(Number(e.target.value))}
                                        >
                                            <option value={0}>— Seleccione un tipo —</option>

                                            {TIPOS_NC.map((t) => (
                                                <option key={t.codigo} value={t.codigo}>
                                                    {t.codigo} - {t.descripcion}
                                                </option>
                                            ))}
                                        </select>

                                        <label>Motivo o sustento *</label>

                                        <textarea
                                            rows={3}
                                            maxLength={500}
                                            placeholder="Ej: Devolución total de la operación"
                                            value={ncMotivo}
                                            onChange={(e) => setNcMotivo(e.target.value)}
                                        />

                                        {(ncTipo === 4 || ncTipo === 5) && (
                                            <>
                                                <label>Monto a descontar (con IGV) *</label>

                                                <input
                                                    type="number"
                                                    min="0.01"
                                                    step="0.01"
                                                    max={Number(detalle.total)}
                                                    placeholder={`Máximo S/ ${Number(detalle.total).toFixed(2)}`}
                                                    value={ncMonto}
                                                    onChange={(e) => setNcMonto(e.target.value)}
                                                />
                                            </>
                                        )}
                                    </div>

                                    <div className="nc-acciones">
                                        <button
                                            className="btn-secundario"
                                            onClick={() => setMostrarNC(false)}
                                        >
                                            Cancelar
                                        </button>

                                        <button
                                            className="btn-nota-credito"
                                            onClick={emitirNotaCredito}
                                            disabled={enviandoNC}
                                        >
                                            {enviandoNC ? "Emitiendo..." : "Emitir nota de crédito"}
                                        </button>
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </section>
        </>
    );
};

export default RecuperacionComprobantes;