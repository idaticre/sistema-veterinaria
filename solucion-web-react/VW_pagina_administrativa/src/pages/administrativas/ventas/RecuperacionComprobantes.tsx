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

const RecuperacionComprobantes = () => {
    const [minimizado, setMinimizado] = useState(false);

    const [tipo, setTipo] = useState("1"); // 1 = Factura, 2 = Boleta
    const [resultado, setResultado] = useState<Comprobante[]>([]);
    const [loading, setLoading] = useState(false);
    const [detalle, setDetalle] = useState<any>(null);
    const [mostrarDetalle, setMostrarDetalle] = useState(false);
    const [documento, setDocumento] = useState("");
    const [clienteId, setClienteId] = useState(0);

    // 🔥 BUSCAR CLIENTE POR DOCUMENTO (igual que en Facturación)
    const buscarCliente = async (doc: string) => {
        setDocumento(doc);

        // Validar longitud: Factura = RUC (11), Boleta = DNI (8)
        const longitudEsperada = tipo === "1" ? 11 : 8;

        if (doc.length !== longitudEsperada) {
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
                                </select>
                            </div>

                            <div>
                                <label>{tipo === "1" ? "RUC" : "DNI"}</label>

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
                                            Comprobante {detalle.serie}-{detalle.numero}
                                        </h3>

                                        <button
                                            className="btn-cerrar"
                                            onClick={() => setMostrarDetalle(false)}
                                        >
                                            ✖ Cerrar
                                        </button>
                                    </div>

                                    <div className="info-comprobante">
                                        <p>
                                            <strong>Cliente:</strong> {detalle.nombreCliente}
                                        </p>

                                        <p>
                                            <strong>Fecha:</strong> {detalle.fechaEmision}
                                        </p>

                                        <p>
                                            <strong>Abono:</strong> S/{" "}
                                            {Number(detalle.totalAnticipio || 0).toFixed(2)}
                                        </p>

                                        <p>
                                            <strong>Total:</strong> S/{" "}
                                            {Number(detalle.total).toFixed(2)}
                                        </p>
                                    </div>

                                    <table className="tabla-comprobantes">
                                        <thead>
                                            <tr>
                                                <th>Descripción</th>
                                                <th>Cantidad</th>
                                                <th>Total</th>
                                            </tr>
                                        </thead>

                                        <tbody>
                                            {detalle.detalles?.map((d: any, index: number) => (
                                                <tr key={index}>
                                                    <td>{d.descripcion}</td>
                                                    <td>{d.cantidad}</td>
                                                    <td>S/ {Number(d.total).toFixed(2)}</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
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