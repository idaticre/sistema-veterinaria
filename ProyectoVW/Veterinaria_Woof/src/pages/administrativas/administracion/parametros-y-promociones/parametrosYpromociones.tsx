import { useState } from "react";
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import "./parametros.css";

interface Promo {
  id: number;
  titulo: string;
  descripcion: string;
}

function ParametrosYPromociones() {
  const [minimizado, setMinimizado] = useState(false);
  const [promos, setPromos] = useState<Promo[]>([
    { id: 1, titulo: "Promo 2x1 Baño 🐶", descripcion: "Lleva 2 baños por el precio de 1." },
    { id: 2, titulo: "Descuento 20% 🐱", descripcion: "En alimentos para gatos." },
  ]);
  const [mostrarModal, setMostrarModal] = useState(false);
  const [edicion, setEdicion] = useState<Promo | null>(null);

  const eliminarPromo = (id: number) => {
    setPromos(promos.filter(p => p.id !== id));
  };

  const guardarPromo = () => {
    if (edicion) {
      const actualizadas = promos.map(p => 
        p.id === edicion.id ? edicion : p
      );
      setPromos(actualizadas);
      setEdicion(null);
    } else {
      const nueva: Promo = {
        id: promos.length + 1,
        titulo: "Nueva promoción",
        descripcion: "Descripción..."
      };
      setPromos([...promos, nueva]);
    }
    setMostrarModal(false);
  };

  return (
    <div id="parametros">
      <Br_administrativa onMinimizeChange={setMinimizado} />
      <main className={minimizado ? "minimize" : ""}>
        <section>
          <h2>⚙️ Parámetros y Promociones</h2>
          <button className="btn" onClick={() => { setMostrarModal(true); setEdicion(null); }}>
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
            <h3>{edicion ? "Editar Promoción" : "Nueva Promoción"}</h3>
            <input
              type="text"
              placeholder="Título"
              value={edicion?.titulo || ""}
              onChange={(e) => setEdicion(edicion ? { ...edicion, titulo: e.target.value } : null)}
            />
            <textarea
              placeholder="Descripción"
              value={edicion?.descripcion || ""}
              onChange={(e) => setEdicion(edicion ? { ...edicion, descripcion: e.target.value } : null)}
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
