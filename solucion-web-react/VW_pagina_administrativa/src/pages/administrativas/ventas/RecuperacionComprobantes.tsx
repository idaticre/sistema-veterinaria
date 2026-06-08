import { useEffect, useState } from "react";
import Br_administrativa from "../../../components/barra_administrativa/Br_administrativa";
import "./RecuperacionComprobantes.css";
import Swal from 'sweetalert2';


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

    const [tipo, setTipo] = useState("1");
    const [resultado, setResultado] = useState<Comprobante[]>([]);
    const [loading, setLoading] = useState(false);
    const [detalle, setDetalle] = useState<any>(null);
    const [mostrarDetalle, setMostrarDetalle] = useState(false);
    const [documento, setDocumento] = useState("");
const [clienteId, setClienteId] = useState(0);
const [clientes, setClientes] = useState<any[]>([]);
const [clienteNombre, setClienteNombre] = useState("");

    

const buscarPorTipo = async () => {
    try {
        setLoading(true);

        const token = sessionStorage.getItem("token");

        const response = await fetch(
            `https://sistema-veterinaria.onrender.com/api/comprobantes/tipo/${tipo}`,
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                }
            }
        );

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        const data = await response.json();
        setResultado(
            Array.isArray(data) ? data : [data]
        );

    } catch {
    Swal.fire({
        title: "Error",
        text: "al obtener los comprobantes, por faovr refresque la página",
        icon: "error"
      });
} finally {
        setLoading(false);
    }
};
const verComprobante = async (id: number) => {
    try {

        const token =
            sessionStorage.getItem("token");

        const response = await fetch(
            `https://sistema-veterinaria.onrender.com/api/comprobantes/${id}`,
            {
                headers: {
                    Authorization:
                        `Bearer ${token}`
                }
            }
        );

        if (!response.ok) {
            throw new Error();
        }

        const data = await response.json();

setDetalle(data);
setMostrarDetalle(true);



    } catch {
    Swal.fire({
        title: "Error",
        text: "al obtener los comprobantes, por faovr refresque la página",
        icon: "error"
      });
}
};

const buscarPorCliente = async () => {

    if (!documento.trim()) {
        return Swal.fire({
            title: "Alerta",
            text: "Ingrese un número de documento",
            icon: "warning"
        });
    }

    if (!clienteId || clienteId === 0) {
        return Swal.fire({
            title: "Alerta",
            text: "No se encontró un cliente con ese documento",
            icon: "warning"
        });
    }

    try {
        setLoading(true);

        const token = sessionStorage.getItem("token");

        const response = await fetch(
            `https://sistema-veterinaria.onrender.com/api/comprobantes/cliente/${clienteId}`,
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                }
            }
        );

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        const data = await response.json();

        setResultado(
            Array.isArray(data) ? data : [data]
        );

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
useEffect(() => {
    const cargarClientes = async () => {
        try {
            const token = sessionStorage.getItem("token");

            const response = await fetch(
                "https://sistema-veterinaria.onrender.com/api/clientes",
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );

            const data = await response.json();

            setClientes(data.data || []);

        } catch {
            console.error("Error cargando clientes");
        }
    };

    cargarClientes();
}, []);

    return (
        <>
            <Br_administrativa
                onMinimizeChange={setMinimizado}
            />

            <section
                id="recuperacion-comprobantes"
                className={
                    minimizado
                        ? "contenido-minimizado"
                        : "contenido-normal"
                }
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
                                    onChange={(e) =>
                                        setTipo(e.target.value)
                                    }
                                >
                                    <option value="1">
                                        Factura
                                    </option>

                                    <option value="2">
                                        Boleta
                                    </option>
                                </select>
                            </div>

                           <div>
    <label>Número de Documento</label>

    <input
        type="number"
        placeholder="Ingrese DNI o RUC"
        value={documento}
        onChange={(e) => {

            const doc = e.target.value;

            setDocumento(doc);

            const encontrado = clientes.find(
                (c) => c.documento === doc
            );

            if (encontrado) {

                setClienteId(encontrado.id);
                setClienteNombre(encontrado.nombre);

            } else {

                setClienteId(0);
                setClienteNombre("");

            }
        }}
    />
</div>

{/* <div>
    <label>Cliente</label>

    <input
        type="text"
        value={clienteNombre}
        disabled
        placeholder="Cliente encontrado"
    />
</div>*/}
                        </div>

                        <div className="acciones">

                            <button
                                className="btn-buscar"
                                onClick={buscarPorTipo}
                            >
                                Buscar por Tipo
                            </button>

                            <button
                                className="btn-buscar"
                                onClick={buscarPorCliente}
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
                    <td>S/ {item.total}</td>

                    <td>
                        <button
                            className="btn-buscar"
                            onClick={() =>
                                verComprobante(item.id)
                            }
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
        <strong>Abono:</strong> S/
{Number(detalle.totalAnticipio || 0).toFixed(2)}
    </p>

    <p>
        <strong>Total:</strong> S/
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
                    {detalle.detalles?.map(
                        (d: any, index: number) => (
                            <tr key={index}>
                                <td>{d.descripcion}</td>
                                <td>{d.cantidad}</td>
                                <td>S/ {d.total}</td>
                            </tr>
                        )
                    )}
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