import { useMemo, useState } from "react";
import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";

import Br_administrativa from "../../../components/barra_administrativa/Br_administrativa";
import "./FacturacionElectronica.css";

interface Servicio {
  descripcion: string;
  cantidad: number;
  precio: number;
  subtotal: number;
  duracion?: number; // Opcional para evitar errores en carga manual
  veterinario?: string; // Opcional para evitar errores en carga manual
}

interface Cita {
  id: number;
  codigo: string;
  fecha: string;
  hora: string;
  totalCita: number;
  abonoInicial: number;
}

function FacturacionElectronica() {
  const [minimizado, setMinimizado] = useState(false);
  const [tipoComprobante, setTipoComprobante] = useState("BOLETA");
  const [cliente, setCliente] = useState("");
  const [documento, setDocumento] = useState("");
  const [clienteId, setClienteId] = useState(0);
  const [servicio, setServicio] = useState("");
  const [cantidad, setCantidad] = useState(1);
  const [precio, setPrecio] = useState(0);
  const [buscarCodigo, setBuscarCodigo] = useState("");
  const [servicios, setServicios] = useState<Servicio[]>([]);
  const [citasCliente, setCitasCliente] = useState<Cita[]>([]);

  // 🔥 BUSCAR CLIENTE
  const buscarCliente = async (doc: string) => {
    setDocumento(doc);

    // 🔥 VALIDAR DNI/RUC
    if (tipoComprobante === "BOLETA") {
      if (doc.length > 0 && doc.length !== 8) {
        setCliente("");
        setClienteId(0);
        setCitasCliente([]);
        return;
      }
    }

    if (tipoComprobante === "FACTURA") {
      if (doc.length > 0 && doc.length !== 11) {
        setCliente("");
        setClienteId(0);
        setCitasCliente([]);
        return;
      }
    }

    try {
      const token = sessionStorage.getItem("token");
      
      const response = await fetch(
        `http://localhost:8080/api/clientes/documento/${doc}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
          }
        }
      );

      if (!response.ok) {
        setCliente("");
        setClienteId(0);
        setCitasCliente([]);
        return;
      }

      const result = await response.json();
      const clienteData = result.data;

      setCliente(clienteData.nombre);
      setClienteId(clienteData.id);

      // 🔥 CITAS DEL CLIENTE
      const citasResponse = await fetch(
        `http://localhost:8080/api/agenda/cliente/${clienteData.id}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
          }
        }
      );

      if (!citasResponse.ok) {
        setCitasCliente([]);
        return;
      }

      const citasResult = await citasResponse.json();
      setCitasCliente(citasResult.data || []);
    } catch (error) {
      console.error(error);
    }
  };

  // 🔥 AGREGAR SERVICIO MANUAL
  const agregarServicio = () => {
    if (!servicio) {
      alert("Ingrese descripción");
      return;
    }

    if (precio <= 0) {
      alert("Ingrese un precio válido");
      return;
    }

    if (cantidad <= 0) {
      alert("Ingrese cantidad válida");
      return;
    }

    const nuevoServicio: Servicio = {
      descripcion: servicio,
      cantidad,
      precio,
      subtotal: cantidad * precio,
      duracion: 0, // Se inicializa para evitar errores en el mapeo del PDF
      veterinario: "N/A"
    };

    setServicios([...servicios, nuevoServicio]);
    setServicio("");
    setCantidad(1);
    setPrecio(0);
  };

  // 🔥 AGREGAR DESDE CITA
  const agregarDesdeCita = async (cita: Cita) => {
    try {
      const yaExiste = servicios.some((s) =>
        s.descripcion.startsWith(`Cita ${cita.codigo}`) || s.descripcion.includes(`(Cita ${cita.codigo})`)
      );

      if (yaExiste) {
        alert("La cita ya fue agregada");
        return;
      }

      const token = sessionStorage.getItem("token");

      const response = await fetch(
        `http://localhost:8080/api/agenda/${cita.id}/servicios`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
          }
        }
      );

      if (!response.ok) {
        alert("No se pudieron obtener los servicios");
        return;
      }

      const result = await response.json();
      const listaServicios = result.data || [];

      if (listaServicios.length === 0) {
        alert("La cita no tiene servicios");
        return;
      }

      const nuevosServicios: Servicio[] = listaServicios.map((srv: any) => ({
        descripcion: `${srv.descripcion} (Cita ${cita.codigo})`,
        cantidad: Number(srv.cantidad),
        precio: Number(srv.valorServicio),
        subtotal: Number(srv.subtotal),
        duracion: Number(srv.duracionMin || 0),
        veterinario: srv.nombreVeterinario || "No asignado",
      }));

      // Descontar abono inicial si existe
      if (Number(cita.abonoInicial) > 0) {
        nuevosServicios.push({
          descripcion: `Adelanto ${cita.codigo}`,
          cantidad: 1,
          precio: -Number(cita.abonoInicial),
          subtotal: -Number(cita.abonoInicial),
          duracion: 0,
          veterinario: "N/A"
        });
      }

      setServicios((prev) => [...prev, ...nuevosServicios]);
    } catch (error) {
      console.error(error);
      alert("Error obteniendo servicios");
    }
  };

  // 🔥 ELIMINAR
  const eliminarServicio = (index: number) => {
    setServicios(servicios.filter((_, i) => i !== index));
  };

  // 🔥 BUSCADOR
  const citasFiltradas = useMemo(() => {
    return citasCliente.filter((c) =>
      c.codigo.toLowerCase().includes(buscarCodigo.toLowerCase())
    );
  }, [citasCliente, buscarCodigo]);

  // 🔥 TOTALES
  const subtotal = servicios.reduce((acc, s) => acc + s.subtotal, 0);
  const igv = subtotal * 0.18;
  const total = subtotal + igv;

  // 🔥 GUARDAR
  const guardarComprobante = () => {
    if (clienteId === 0) {
      alert("Seleccione cliente");
      return;
    }

    if (servicios.length === 0) {
      alert("Agregue servicios");
      return;
    }

    alert("Comprobante generado correctamente");

    console.log({
      cliente,
      documento,
      tipoComprobante,
      servicios,
      subtotal,
      igv,
      total,
    });
  };

  // 🔥 PDF CORREGIDO
  const generarPDF = () => {
    if (servicios.length === 0) {
      alert("No hay servicios");
      return;
    }

    const doc = new jsPDF();
    doc.setFontSize(18);
    doc.text("VETERINARIA MANADA WOOF", 14, 20);

    doc.setFontSize(12);
    doc.text(`Cliente: ${cliente}`, 14, 35);
    doc.text(`Documento: ${documento}`, 14, 43);
    doc.text(`Tipo: ${tipoComprobante}`, 14, 51);

    autoTable(doc, {
      startY: 65,
      head: [["Descripción", "Cant.", "Precio Unit.", "Subtotal"]],
      body: servicios.map((s) => [
        s.descripcion,
        s.cantidad,
        `S/ ${s.precio.toFixed(2)}`,
        `S/ ${s.subtotal.toFixed(2)}`,
      ]),
    });

    const finalY = (doc as any).lastAutoTable.finalY + 15;

    doc.text(`Subtotal: S/ ${subtotal.toFixed(2)}`, 14, finalY);
    doc.text(`IGV (18%): S/ ${igv.toFixed(2)}`, 14, finalY + 10);
    doc.text(`TOTAL: S/ ${total.toFixed(2)}`, 14, finalY + 20);

    doc.save(`comprobante_${tipoComprobante.toLowerCase()}_${documento}.pdf`);
  };

  return (
    <div id="facturacion">
      <Br_administrativa onMinimizeChange={setMinimizado} />

      <main className={minimizado ? "minimize" : ""}>
        <section className="facturacion-container">
          <div className="header-factura">
            <h2>📄 Facturación Electrónica</h2>
          </div>

          <div className="factura-card">
            <div className="form-grid">
              <div>
                <label>Tipo comprobante</label>
                <select
                  value={tipoComprobante}
                  onChange={(e) => setTipoComprobante(e.target.value)}
                >
                  <option value="BOLETA">Boleta</option>
                  <option value="FACTURA">Factura</option>
                </select>
              </div>

              <div>
                <label>Serie</label>
                <input
                  type="text"
                  value={tipoComprobante === "FACTURA" ? "FFF1" : "BBB1"}
                  disabled
                />
              </div>

              <div>
                <label>{tipoComprobante === "FACTURA" ? "RUC" : "DNI"}</label>
                <input
                  type="text"
                  value={documento}
                  onChange={(e) => buscarCliente(e.target.value)}
                />
              </div>

              <div>
                <label>Cliente</label>
                <input type="text" value={cliente} readOnly />
              </div>
            </div>

            {/* 🔥 BUSCADOR */}
            <div style={{ marginTop: "20px" }}>
              <input
                type="text"
                placeholder="Buscar cita..."
                value={buscarCodigo}
                onChange={(e) => setBuscarCodigo(e.target.value)}
              />
            </div>

            {/* 🔥 CITAS */}
            {citasFiltradas.length > 0 && (
              <div
                style={{
                  marginTop: "25px",
                  maxHeight: "300px",
                  overflowY: "auto",
                }}
              >
                <h3>📅 Citas del Cliente</h3>

                <table className="tabla-servicios">
                  <thead>
                    <tr>
                      <th>Código</th>
                      <th>Fecha</th>
                      <th>Hora</th>
                      <th>Total</th>
                      <th>Abono</th>
                      <th>Pendiente</th>
                      <th>Acción</th>
                    </tr>
                  </thead>
                  <tbody>
                    {citasFiltradas.map((cita) => {
                      const pendiente =
                        Number(cita.totalCita || 0) -
                        Number(cita.abonoInicial || 0);

                      return (
                        <tr key={cita.id}>
                          <td>{cita.codigo}</td>
                          <td>{cita.fecha}</td>
                          <td>{cita.hora}</td>
                          <td>
                            S/ {Number(cita.totalCita || 0).toFixed(2)}
                          </td>
                          <td>
                            S/ {Number(cita.abonoInicial || 0).toFixed(2)}
                          </td>
                          <td>S/ {pendiente.toFixed(2)}</td>
                          <td>
                            <button onClick={() => agregarDesdeCita(cita)}>
                              ➕ Agregar
                            </button>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            )}

            <hr />

            <h3>🛠 Servicios</h3>

            <div className="servicio-grid">
              <input
                type="text"
                placeholder="Descripción"
                value={servicio}
                onChange={(e) => setServicio(e.target.value)}
              />

              <input
                type="number"
                placeholder="Cantidad"
                value={cantidad}
                onChange={(e) => setCantidad(Number(e.target.value))}
              />

              <input
                type="number"
                placeholder="Precio"
                value={precio}
                onChange={(e) => setPrecio(Number(e.target.value))}
              />

              <button onClick={agregarServicio}>➕ Agregar</button>
            </div>

            <table className="tabla-servicios">
              <thead>
                <tr>
                  <th>Descripción</th>
                  <th>Cantidad</th>
                  <th>Precio</th>
                  <th>Subtotal</th>
                  <th>Acción</th>
                </tr>
              </thead>
              <tbody>
                {servicios.map((s, index) => (
                  <tr key={index}>
                    <td>{s.descripcion}</td>
                    <td>{s.cantidad}</td>
                    <td>S/ {s.precio.toFixed(2)}</td>
                    <td>S/ {s.subtotal.toFixed(2)}</td>
                    <td>
                      <button
                        className="btn-eliminar"
                        onClick={() => eliminarServicio(index)}
                      >
                        🗑
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            <div className="totales">
              <h3>Subtotal: S/{subtotal.toFixed(2)}</h3>
              <h3>IGV (18%): S/{igv.toFixed(2)}</h3>
              <h2>Total: S/{total.toFixed(2)}</h2>
            </div>

            <div className="acciones">
              <button className="btn-guardar" onClick={guardarComprobante}>
                💾 Guardar comprobante
              </button>

              <button className="btn-pdf" onClick={generarPDF}>
                🖨 Generar PDF
              </button>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
}

export default FacturacionElectronica;