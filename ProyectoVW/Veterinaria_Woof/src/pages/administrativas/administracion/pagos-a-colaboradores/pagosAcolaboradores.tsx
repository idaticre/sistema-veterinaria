import { useState, useEffect, useRef } from "react";
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import "./pagos.css";

interface Pago {
  id: number;
  colaborador: string;
  cargo: string;
  monto: number;
  fecha: string;
  metodo: string;
  estado: string;
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

  // Cargar desde LocalStorage al iniciar
  useEffect(() => {
    const guardados = localStorage.getItem("pagos");
    if (guardados) {
      const datos = JSON.parse(guardados);
      setPagos(datos);
      setFiltrados(datos);
    } else {
      const inicial: Pago[] = [
        { id: 1, colaborador: "Carlos López", cargo: "Administrador", monto: 1200, fecha: "2025-08-10", metodo: "Transferencia", estado: "Pagado" },
        { id: 2, colaborador: "Ana Torres", cargo: "Veterinaria", monto: 1500, fecha: "2025-08-15", metodo: "Efectivo", estado: "Pendiente" },
      ];
      setPagos(inicial);
      setFiltrados(inicial);
    }
  }, []);

  // Guardar en LocalStorage cada vez que cambien los pagos
  useEffect(() => {
    localStorage.setItem("pagos", JSON.stringify(pagos));
  }, [pagos]);

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

  // Guardar nuevo o editado
  const guardarPago = () => {
    if (!edicion) return;

    if (edicion.id === 0) {
      const nuevo = { ...edicion, id: pagos.length ? Math.max(...pagos.map(p => p.id)) + 1 : 1 };
      const nuevosPagos = [...pagos, nuevo];
      setPagos(nuevosPagos);
      setFiltrados(nuevosPagos);
    } else {
      const actualizados = pagos.map(p =>
        p.id === edicion.id ? edicion : p
      );
      setPagos(actualizados);
      setFiltrados(actualizados);
    }

    setMostrarModal(false);
    setEdicion(null);
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
            <button
              onClick={() => {
                setMostrarModal(true);
                setEdicion({
                  id: 0,
                  colaborador: "",
                  cargo: "",
                  monto: 0,
                  fecha: new Date().toISOString().split("T")[0],
                  metodo: "",
                  estado: "Pendiente",
                });
              }}
            >
              ➕ Registrar Pago
            </button>
          </div>

          <div id="lista_pagos">
            {filtrados.map((p) => (
              <div className="registro_pago" key={p.id}>
                <div className="info_pago">
                  <span className="colaborador">{p.colaborador}</span>
                  <span className="cargo">👔 {p.cargo}</span>
                </div>
                <span className="monto">💲 {p.monto}</span>
                <span className="fecha">📅 {p.fecha}</span>
                <span className="metodo">💳 {p.metodo}</span>
                <span className={`estado ${p.estado.toLowerCase()}`}>
                  {p.estado}
                </span>
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
            <h3>{edicion?.id === 0 ? "Registrar Nuevo Pago" : "Editar Pago"}</h3>
            <input
              type="text"
              placeholder="Nombre del colaborador"
              value={edicion?.colaborador || ""}
              onChange={(e) =>
                setEdicion(prev => prev ? { ...prev, colaborador: e.target.value } : null)
              }
            />
            <input
              type="text"
              placeholder="Cargo"
              value={edicion?.cargo || ""}
              onChange={(e) =>
                setEdicion(prev => prev ? { ...prev, cargo: e.target.value } : null)
              }
            />
            <input
              type="number"
              placeholder="Monto"
              value={edicion?.monto || ""}
              onChange={(e) =>
                setEdicion(prev => prev ? { ...prev, monto: Number(e.target.value) } : null)
              }
            />
            <input
              type="date"
              value={edicion?.fecha || ""}
              onChange={(e) =>
                setEdicion(prev => prev ? { ...prev, fecha: e.target.value } : null)
              }
            />
            <select
              value={edicion?.metodo || ""}
              onChange={(e) =>
                setEdicion(prev => prev ? { ...prev, metodo: e.target.value } : null)
              }
            >
              <option value="">Seleccione método</option>
              <option value="Transferencia">Transferencia</option>
              <option value="Efectivo">Efectivo</option>
              <option value="Yape">Yape</option>
              <option value="Plin">Plin</option>
            </select>
            <select
              value={edicion?.estado || ""}
              onChange={(e) =>
                setEdicion(prev => prev ? { ...prev, estado: e.target.value } : null)
              }
            >
              <option value="Pendiente">Pendiente</option>
              <option value="Pagado">Pagado</option>
            </select>
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
