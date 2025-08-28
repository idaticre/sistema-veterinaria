import { useState, useEffect } from "react";
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import "./parametros.css";

interface Promo {
  id: number;
  titulo: string;
  descripcion: string;
}

function ParametrosYPromociones() {
  const [minimizado, setMinimizado] = useState(false);
  const [promos, setPromos] = useState<Promo[]>([]);
  const [mostrarModal, setMostrarModal] = useState(false);
  const [edicion, setEdicion] = useState<Promo | null>(null);

  // Cargar desde LocalStorage
  useEffect(() => {
    const guardados = localStorage.getItem("promos");
    if (guardados) {
      setPromos(JSON.parse(guardados));
    } else {
      setPromos([
        { id: 1, titulo: "Promo 2x1 Baño 🐶", descripcion: "Lleva 2 baños por el precio de 1." },
        { id: 2, titulo: "Descuento 20% 🐱", descripcion: "En alimentos para gatos." },
      ]);
    }
  }, []);

  // Guardar en LocalStorage
  useEffect(() => {
    localStorage.setItem("promos", JSON.stringify(promos));
  }, [promos]);

  const eliminarPromo = (id: number) => {
    setPromos(promos.filter(p => p.id !== id));
  };

  const guardarPromo = () => {
    if (!edicion) return;

    if (edicion.id === 0) {
      const nueva = { ...edicion, id: promos.length ? Math.max(...promos.map(p => p.id)) + 1 : 1 };
      setPromos([...promos, nueva]);
    } else {
      const actualizadas = promos.map(p => p.id === edicion.id ? edicion : p);
      setPromos(actualizadas);
    }
    setMostrarModal(false);
    setEdicion(null);
  };

  return (
    <div id="parametros">
      <Br_administrativa onMinimizeChange={setMinimizado} />
      <main className={minimizado ? "minimize" : ""}>
        <section>
          <h2>⚙️ Parámetros y Promociones</h2>
          <button
            className="btn"
            onClick={() => {
              setMostrarModal(true);
              setEdicion({ id: 0, titulo: "", descripcion: "" });
            }}
          >
            ➕ Nueva Promoción
          </button>

          <div className="lista_promos">
            {promos.map((p) => (
              <div className="promo_card" key={p.id}>
                <h3>{p.titulo}</h3>
                <p>{p.descripcion}</p>
                <div className="acciones">
                  <button onClick={() => { setEdicion(p); setMostrarModal(true); }}>✏️</button>
                  <button onClick={() => eliminarPromo(p.id)}>🗑️</button>
                </div>
              </div>
            ))}
          </div>
        </section>
      </main>

      {mostrarModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>{edicion?.id === 0 ? "Nueva Promoción" : "Editar Promoción"}</h3>
            <input
              type="text"
              placeholder="Título"
              value={edicion?.titulo || ""}
              onChange={(e) =>
                setEdicion(prev => prev ? { ...prev, titulo: e.target.value } : null)
              }
            />
            <textarea
              placeholder="Descripción"
              value={edicion?.descripcion || ""}
              onChange={(e) =>
                setEdicion(prev => prev ? { ...prev, descripcion: e.target.value } : null)
              }
            />
            <div className="acciones-modal">
              <button onClick={guardarPromo}>Guardar</button>
              <button onClick={() => { setMostrarModal(false); setEdicion(null); }}>Cancelar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ParametrosYPromociones;
