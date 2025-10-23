import { useState, useEffect } from "react";
import axios from "axios";
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import "./pagos.css";

interface Colaborador {
  id: number;
  codigoColaborador: string;
  nombre: string;
  correo?: string;
  telefono?: string;
  activo: boolean;
}

interface Pago {
  id: number;
  colaborador: Colaborador;
  monto: number;
  fecha: string;
  metodo: string;
  estado: string;
}

function PagosColaboradores() {
  const [minimizado, setMinimizado] = useState(false);
  const [busqueda, setBusqueda] = useState("");
  const [colaboradores, setColaboradores] = useState<Colaborador[]>([]);
  const [pagos, setPagos] = useState<Pago[]>([]);
  const [filtrados, setFiltrados] = useState<Colaborador[]>([]);
  const [colaboradorSeleccionado, setColaboradorSeleccionado] = useState<Colaborador | null>(null);
  const [mostrarModal, setMostrarModal] = useState(false);
  const [errorMonto, setErrorMonto] = useState("");
  const [nuevoPago, setNuevoPago] = useState({
    monto: "",
    fecha: new Date().toISOString().split("T")[0],
    metodo: "",
    estado: "Pendiente",
  });

  useEffect(() => {
    axios
      .get("http://localhost:8088/api/colaboradores")
      .then((res) => setColaboradores(res.data.data || []))
      .catch((err) => console.error("Error cargando colaboradores:", err));

    axios
      .get("http://localhost:8088/api/pagos")
      .then((res) => setPagos(res.data.data || []))
      .catch((err) => console.error("Error cargando pagos:", err));
  }, []);

  
  useEffect(() => {
    if (busqueda.trim() === "") {
      setFiltrados([]);
    } else {
      setFiltrados(
        colaboradores.filter((c) =>
          c.nombre?.toLowerCase().includes(busqueda.toLowerCase())
        )
      );
    }
  }, [busqueda, colaboradores]);

  const pagosColaborador = pagos.filter(
    (p) => p.colaborador?.id === colaboradorSeleccionado?.id
  );

  const registrarPago = () => {
    if (!colaboradorSeleccionado) {
      alert("Selecciona un colaborador primero");
      return;
    }

    if (!nuevoPago.monto || !nuevoPago.metodo) {
      alert("Completa todos los campos del pago");
      return;
    }

    const montoNum = Number(nuevoPago.monto);
    if (isNaN(montoNum) || montoNum <= 0) {
      setErrorMonto("Ingrese un número válido para el monto");
      return;
    }

    const pagoNuevo = {
      colaborador: { id: colaboradorSeleccionado.id },
      monto: montoNum,
      fecha: nuevoPago.fecha,
      metodo: nuevoPago.metodo,
      estado: nuevoPago.estado,
    };

    axios
      .post("http://localhost:8088/api/pagos", pagoNuevo)
      .then((res) => {
        alert("✅ Pago registrado correctamente");
        setPagos([...pagos, res.data.data || res.data]);
        setMostrarModal(false);
        setNuevoPago({
          monto: "",
          fecha: new Date().toISOString().split("T")[0],
          metodo: "",
          estado: "Pendiente",
        });
        setErrorMonto("");
      })
      .catch((err) => {
        console.error("Error al registrar pago:", err);
        alert("❌ No se pudo registrar el pago");
      });
  };

  const seleccionarColaborador = (colaborador: Colaborador) => {
    setColaboradorSeleccionado(colaborador);
    setBusqueda("");
    setFiltrados([]);
  };

  return (
    <div id="pagos">
      <Br_administrativa onMinimizeChange={setMinimizado} />
      <main className={minimizado ? "minimize" : ""}>
        <section id="lst_pagos">
          <div id="encabezado">
            <h2>💵 Pagos a Colaboradores</h2>
          </div>

          <div id="buscador">
            <input
              type="text"
              placeholder="Buscar colaborador..."
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
            />
          </div>

          {busqueda && filtrados.length > 0 && (
            <div className="resultados-busqueda">
              {filtrados.map((c) => (
                <div
                  key={c.id}
                  className={`item-colaborador ${colaboradorSeleccionado?.id === c.id ? "seleccionado" : ""}`}
                  onClick={() => seleccionarColaborador(c)}
                >
                  {c.nombre} ({c.codigoColaborador})
                </div>
              ))}
            </div>
          )}

          {colaboradorSeleccionado && (
            <div className="panel-pagos">
              <div className="info-colaborador">
                <h3>👤 {colaboradorSeleccionado.nombre}</h3>
                <p><strong>Código:</strong> {colaboradorSeleccionado.codigoColaborador}</p>
              </div>

              <button className="btn-registrar" onClick={() => setMostrarModal(true)}>
                ➕ Registrar Pago
              </button>

              <h4>📅 Historial de Pagos</h4>
              <div className="tabla-pagos">
                {pagosColaborador.length > 0 ? (
                  pagosColaborador.map((p) => (
                    <div key={p.id} className="fila-pago">
                      <div><strong>Fecha:</strong> {p.fecha}</div>
                      <div><strong>Monto:</strong> S/ {p.monto}</div>
                      <div><strong>Método:</strong> {p.metodo}</div>
                      <div><strong>Estado:</strong> {p.estado}</div>
                    </div>
                  ))
                ) : (
                  <p>No hay pagos registrados para este colaborador.</p>
                )}
              </div>
            </div>
          )}
        </section>
      </main>

      {mostrarModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Registrar nuevo pago</h3>
            <input
              type="text"
              placeholder="Monto (S/.)"
              value={nuevoPago.monto}
              onChange={(e) => {
                setNuevoPago({ ...nuevoPago, monto: e.target.value });
                setErrorMonto("");
              }}
            />
            {errorMonto && <span className="error">{errorMonto}</span>}

            <input
              type="date"
              value={nuevoPago.fecha}
              onChange={(e) => setNuevoPago({ ...nuevoPago, fecha: e.target.value })}
            />
            <select
              value={nuevoPago.metodo}
              onChange={(e) => setNuevoPago({ ...nuevoPago, metodo: e.target.value })}
            >
              <option value="">Seleccione método</option>
              <option value="Transferencia">Transferencia</option>
              <option value="Efectivo">Efectivo</option>
              <option value="Yape">Yape</option>
              <option value="Plin">Plin</option>
            </select>
            <select
              value={nuevoPago.estado}
              onChange={(e) => setNuevoPago({ ...nuevoPago, estado: e.target.value })}
            >
              <option value="Pendiente">Pendiente</option>
              <option value="Pagado">Pagado</option>
            </select>
            <div className="acciones-modal">
              <button onClick={registrarPago}>Guardar</button>
              <button onClick={() => setMostrarModal(false)}>Cancelar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default PagosColaboradores;
