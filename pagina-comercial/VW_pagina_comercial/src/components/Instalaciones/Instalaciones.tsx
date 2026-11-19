import { useEffect, useState } from "react";
import "./Instalaciones.css";

import instalacion1 from "../../pages/home/images/instalaciones1.jpg";
import instalacion2 from "../../pages/home/images/instalaciones2.jpg";
import instalacion3 from "../../pages/home/images/instalaciones3.jpg";
import instalacion4 from "../../pages/home/images/instalaciones4.jpg";
import instalacion5 from "../../pages/home/images/instalaciones5.jpg";
import instalacion6 from "../../pages/home/images/instalaciones6.jpg";

type ImagenInstalacion = {
  src: string;
  titulo: string;
  alt: string;
};

const imagenes: ImagenInstalacion[] = [
  {
    src: instalacion1,
    titulo: "Área de baño",
    alt: "Área de baño para mascotas",
  },
  {
    src: instalacion2,
    titulo: "Recepción",
    alt: "Recepción de Manada Woof",
  },
  {
    src: instalacion3,
    titulo: "Área de atención",
    alt: "Área de atención veterinaria",
  },
  {
    src: instalacion4,
    titulo: "Espacio de cuidado",
    alt: "Espacio de cuidado para mascotas",
  },
  {
    src: instalacion5,
    titulo: "Salón y spa",
    alt: "Salón y spa para mascotas",
  },
  {
    src: instalacion6,
    titulo: "Zona de higiene",
    alt: "Estación de higiene para mascotas",
  },
];

function Instalaciones() {
  const [imagenSeleccionada, setImagenSeleccionada] =
    useState<ImagenInstalacion | null>(null);

  useEffect(() => {
    if (!imagenSeleccionada) return;

    const overflowAnterior = document.body.style.overflow;

    const cerrarConEscape = (evento: KeyboardEvent) => {
      if (evento.key === "Escape") {
        setImagenSeleccionada(null);
      }
    };

    document.body.style.overflow = "hidden";
    window.addEventListener("keydown", cerrarConEscape);

    return () => {
      document.body.style.overflow = overflowAnterior;
      window.removeEventListener("keydown", cerrarConEscape);
    };
  }, [imagenSeleccionada]);

  return (
    <section className="instalaciones-container" aria-labelledby="instalaciones-titulo">
      <div className="instalaciones-encabezado">
        <span className="instalaciones-etiqueta">MANADA WOOF</span>

        <h2 className="instalaciones-titulo" id="instalaciones-titulo">
          Explora nuestro espacio
        </h2>

        <p className="instalaciones-descripcion">
          Un lugar diseñado con amor y cuidado, donde tu mascota se sentirá
          como en casa. Conoce nuestros ambientes seguros, modernos y llenos
          de calidez.
        </p>
      </div>

      <div className="instalaciones-grid">
        {imagenes.map((imagen, index) => (
          <button
            className="instalaciones-tarjeta"
            type="button"
            key={imagen.src}
            onClick={() => setImagenSeleccionada(imagen)}
            aria-label={`Ampliar foto: ${imagen.titulo}`}
          >
            <img
              src={imagen.src}
              alt={imagen.alt}
              className="instalaciones-imagen"
              loading="lazy"
            />

            <span className="instalaciones-numero" aria-hidden="true">
              {String(index + 1).padStart(2, "0")}
            </span>

            <span className="instalaciones-pie">
              <span className="instalaciones-nombre">{imagen.titulo}</span>
              <span className="instalaciones-ver">
                Ver foto
                <span aria-hidden="true"> ↗</span>
              </span>
            </span>
          </button>
        ))}
      </div>

      {imagenSeleccionada && (
        <div
          className="instalaciones-modal-fondo"
          onClick={() => setImagenSeleccionada(null)}
          role="presentation"
        >
          <div
            className="instalaciones-modal"
            role="dialog"
            aria-modal="true"
            aria-label={imagenSeleccionada.titulo}
            onClick={(evento) => evento.stopPropagation()}
          >
            <button
              className="instalaciones-cerrar"
              type="button"
              onClick={() => setImagenSeleccionada(null)}
              aria-label="Cerrar imagen"
            >
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path d="M6 6l12 12M18 6L6 18" />
              </svg>
            </button>

            <img
              src={imagenSeleccionada.src}
              alt={imagenSeleccionada.alt}
              className="instalaciones-imagen-ampliada"
            />

            <p className="instalaciones-titulo-modal">
              {imagenSeleccionada.titulo}
            </p>
          </div>
        </div>
      )}
    </section>
  );
}

export default Instalaciones;