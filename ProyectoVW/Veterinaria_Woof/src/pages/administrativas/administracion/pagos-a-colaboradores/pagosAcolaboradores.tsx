import { useState, useEffect, useRef } from "react";
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import "./pagos.css";

interface Pago {
  id: number;
  colaborador: string;
  monto: number;
  fecha: string;
}

function Pagos() {
  const [minimizado, setMinimizado] = useState(false);
  const [busqueda, setBusqueda] = useState("");
  const [pagos, setPagos] = useState<Pago[]>([]);
  const [filtrados, setFiltrados] = useState<Pago[]>([]);
  const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
  const [mostrarModal, setMostrarModal] = useState(false);
  const [edicion, setEdicion] = useState<Pago | null>(null);
  const menuRef = useRef<HTMLDivElement | null>(null);

  // Datos de ejemplo
  useEffect(() => {
    const datos = [
      { id: 1, colaborador: "Carlos López", monto: 1200, fecha: "2025-08-10" },
      { id: 2, colaborador: "Ana Torres", monto: 1500, fecha: "2025-08-15" },
    ];
    setPagos(datos);
    setFiltrados(datos);
  }, []);

  // Filtrar por búsqueda
  useEffect(() => {
    const resultado = pagos.filter(p =>
      p.colaborador.toLowerCase().includes(busqueda.toLowerCase())
    );
    setFiltrados(resultado);
  }, [busqueda, pagos]);

  // Cerrar menú al hacer clic fuera
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setMenuActivoId(null);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // Eliminar pago
  const eliminarPago = (id: number) => {
    const actualizados = pagos.filter(p => p.id !== id);
    setPagos(actualizados);
    setFiltrados(actualizados);
    setMenuActivoId(null);
  };

  // Editar pago
  const editarPago = (id: number) => {
    const seleccionada = pagos.find(p => p.id === id);
    if (seleccionada) {
      setEdicion(seleccionada);
      setMostrarModal(true);
    }
  };

  // Guardar nuevo o editado pago
  const guardarPago = () => {
    if (edicion) {
      const actualizados = pagos.map(p =>
        p.id === edicion.id ? edicion : p
      );
      setPagos(actualizados);
      setFiltrados(actualizados);
      setEdicion(null);
    } else {
      const nuevo: Pago = {
        id: pagos.length + 1,
        colaborador: "Nuevo Colaborador",
        monto: 1000,
        fecha: new Date().toISOString().split("T")[0],
      };
      setPagos([...pagos, nuevo]);
      setFiltrados([...pagos, nuevo]);
    }
    setMostrarModal(false);
  };

  return (
    <div id="pagos">
      <Br_administrativa onMinimizeChange={setMinimizado} />
      <main className={minimizado ? "minimize" : ""}>
        <section id="lst_pagos">
          <div id="encabezado">
            <h2>💵 Pagos de Colaboradores</h2>
          </div>
          <div id="buscador">
            <div id="br_buscador">
              <input
                type="text"
                placeholder="Buscar colaborador..."
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
              />
            </div>
            <button onClick={() => { setMostrarModal(true); setEdicion(null); }}>
              ➕ Registrar Pago
            </button>
          </div>

          <div id="lista_pagos">
            {filtrados.map((p) => (
              <div className="registro_pago" key={p.id}>
                <span className="colaborador">{p.colaborador}</span>
                <span className="monto">💲 {p.monto}</span>
                <span className="fecha">📅 {p.fecha}</span>
                <div className="lst_opciones_container">
                  <div className="lst_opciones" onClick={() => setMenuActivoId(p.id)}>
                    <i className="fa-solid fa-ellipsis-vertical" />
                  </div>
                  {menuActivoId === p.id && (
                    <div ref={menuRef} className="menu-opciones">
                      <button onClick={() => editarPago(p.id)}>✏️ Editar</button>
                      <button onClick={() => eliminarPago(p.id)}>🗑️ Eliminar</button>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        </section>
      </main>

      {mostrarModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>{edicion ? "Editar Pago" : "Registrar Nuevo Pago"}</h3>
            <input
              type="text"
              placeholder="Nombre del colaborador"
              value={edicion?.colaborador || ""}
              onChange={(e) => setEdicion(edicion ? { ...edicion, colaborador: e.target.value } : null)}
            />
            <input
              type="number"
              placeholder="Monto"
              value={edicion?.monto || ""}
              onChange={(e) => setEdicion(edicion ? { ...edicion, monto: Number(e.target.value) } : null)}
            />
            <input
              type="date"
              value={edicion?.fecha || ""}
              onChange={(e) => setEdicion(edicion ? { ...edicion, fecha: e.target.value } : null)}
            />
            <div className="acciones-modal">
              <button onClick={guardarPago}>Guardar</button>
              <button onClick={() => { setMostrarModal(false); setEdicion(null); }}>Cancelar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Pagos;
