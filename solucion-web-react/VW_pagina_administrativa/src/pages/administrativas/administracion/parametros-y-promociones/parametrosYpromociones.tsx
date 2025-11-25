import { useState, useEffect } from "react";
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import "./parametros.css";

interface Promo {
  id: number;
  titulo: string;
  descripcion: string;
  publicada?: boolean;
  tipo?: "publico" | "clientes";
  clientes?: string[];
}

function ParametrosYPromociones() {
  const [minimizado, setMinimizado] = useState(false);
  const [promos, setPromos] = useState<Promo[]>([]);
  const [mostrarModal, setMostrarModal] = useState(false);
  const [edicion, setEdicion] = useState<Promo | null>(null);

  // Lista de clientes (simulada en frontend)
  const listaClientes = ["Carlos", "María", "Fernanda", "José", "Lucía"];

  // Promociones base
  const promocionesBase: Promo[] = [
    {
      id: 1,
      titulo: "Promociones de clientes frecuentes",
      descripcion: "Por cada 10 días de hospedaje un baño gratis.",
      publicada: true,
      tipo: "publico"
    },
    {
      id: 2,
      titulo: "Baños desde 35 soles",
      descripcion:
        "Precio 35 soles en adelante. Si vienen más de 3 mascotas se aplica precio de 30 soles.",
      publicada: true,
      tipo: "publico"
    },
    {
      id: 3,
      titulo: "Descuento 15%",
      descripcion: "15% de descuento en el primer baño si es cliente nuevo.",
      publicada: true,
      tipo: "publico"
    },
    {
      id: 4,
      titulo: "Hospedaje por 15 días",
      descripcion: "Incluye baño de ingreso y salida gratis.",
      publicada: true,
      tipo: "publico"
    }
  ];

  // Carga inicial
  useEffect(() => {
    const guardados = localStorage.getItem("promos");

    if (guardados) {
      const parsed = JSON.parse(guardados);
      if (!Array.isArray(parsed) || parsed.length < promocionesBase.length) {
        localStorage.removeItem("promos");
        setPromos(promocionesBase);
      } else {
        setPromos(parsed);
      }
    } else {
      setPromos(promocionesBase);
    }
  }, []);

  // Guardar cambios en localStorage
  useEffect(() => {
    localStorage.setItem("promos", JSON.stringify(promos));
  }, [promos]);

  // Eliminar promoción
  const eliminarPromo = (id: number) => {
    setPromos(promos.filter((p) => p.id !== id));
  };

  // Publicar promoción
  const publicarPromo = (id: number) => {
    setPromos(
      promos.map((p) =>
        p.id === id ? { ...p, publicada: true } : p
      )
    );
  };

  // Guardar edición o nueva promoción
  const guardarPromo = () => {
    if (!edicion) return;

    const promoCompleta = {
      ...edicion,
      tipo: edicion.tipo || "publico",
      clientes: edicion.tipo === "clientes" ? edicion.clientes || [] : [],
      publicada: false
    };

    if (edicion.id === 0) {
      const nueva = {
        ...promoCompleta,
        id: promos.length ? Math.max(...promos.map((p) => p.id)) + 1 : 1,
      };
      setPromos([...promos, nueva]);
    } else {
      const actualizadas = promos.map((p) =>
        p.id === edicion.id ? promoCompleta : p
      );
      setPromos(actualizadas);
    }

    setMostrarModal(false);
    setEdicion(null);
  };

  return (
    <div id="parametros">
      <Br_administrativa onMinimizeChange={setMinimizado} />

      <main className={minimizado ? "minimize" : ""}>
        <section className="parametros-container">
          <h2 className="titulo-parametros">⚙️ Parámetros y Promociones</h2>

          <button
            className="btn-agregar-linda"
            onClick={() => {
              setMostrarModal(true);
              setEdicion({
                id: 0,
                titulo: "",
                descripcion: "",
                tipo: "publico",
                clientes: []
              });
            }}
          >
            ➕ Nueva Promoción
          </button>

          <div className="lista-promos">
            {promos.map((p) => (
              <div className="promo-card" key={p.id}>
                <div className="promo-info">
                  <h3>{p.titulo}</h3>
                  <p>{p.descripcion}</p>

                  {p.tipo === "clientes" && (
                    <p className="clientes-tag">👥 Solo para: {p.clientes?.join(", ")}</p>
                  )}

                  {p.publicada ? (
                    <span className="publicada">✔️ Publicada</span>
                  ) : (
                    <span className="pendiente">⏳ Sin publicar</span>
                  )}
                </div>

                <div className="acciones">
                  {!p.publicada && (
                    <button
                      className="btn-publicar"
                      onClick={() => publicarPromo(p.id)}
                    >
                      📢 Publicar
                    </button>
                  )}

                  <button
                    className="btn-editar"
                    onClick={() => {
                      setEdicion(p);
                      setMostrarModal(true);
                    }}
                  >
                    ✏️ Editar
                  </button>

                  <button
                    className="btn-eliminar"
                    onClick={() => eliminarPromo(p.id)}
                  >
                    🗑️ Eliminar
                  </button>
                </div>
              </div>
            ))}
          </div>
        </section>
      </main>

      {mostrarModal && (
        <div className="modal-overlay">
          <div className="modal-content modal-promos">
            <h3>{edicion?.id === 0 ? "Nueva Promoción" : "Editar Promoción"}</h3>

            <input
              type="text"
              placeholder="Título"
              value={edicion?.titulo || ""}
              onChange={(e) =>
                setEdicion((prev) =>
                  prev ? { ...prev, titulo: e.target.value } : null
                )
              }
            />

            <textarea
              placeholder="Descripción"
              value={edicion?.descripcion || ""}
              onChange={(e) =>
                setEdicion((prev) =>
                  prev ? { ...prev, descripcion: e.target.value } : null
                )
              }
            />

            <select
              value={edicion?.tipo || "publico"}
              onChange={(e) =>
                setEdicion((prev) =>
                  prev ? { ...prev, tipo: e.target.value as any } : null
                )
              }
            >
              <option value="publico">Público</option>
              <option value="clientes">Clientes específicos</option>
            </select>

            {edicion?.tipo === "clientes" && (
              <div className="lista-clientes">
                {listaClientes.map((c) => (
                  <label key={c}>
                    <input
                      type="checkbox"
                      checked={edicion.clientes?.includes(c)}
                      onChange={() => {
                        setEdicion((prev) => {
                          if (!prev) return prev;

                          const existe = prev.clientes?.includes(c);

                          return {
                            ...prev,
                            clientes: existe
                              ? prev.clientes?.filter((x) => x !== c)
                              : [...(prev.clientes || []), c]
                          };
                        });
                      }}
                    />
                    {c}
                  </label>
                ))}
              </div>
            )}

            <div className="acciones-modal">
              <button className="btn-guardar" onClick={guardarPromo}>
                Guardar
              </button>

              <button
                className="btn-cancelar"
                onClick={() => {
                  setMostrarModal(false);
                  setEdicion(null);
                }}
              >
                Cancelar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ParametrosYPromociones;
