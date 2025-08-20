import React, { useState } from "react";
import "./Instalaciones.css";

const imagenes = [
  "/images/instalaciones1.jpg",
  "/images/instalaciones2.jpg",
  "/images/instalaciones3.jpg",
  "/images/instalaciones4.jpg",
  "/images/instalaciones5.jpg",
  "/images/instalaciones6.jpg",
];

const Instalaciones: React.FC = () => {
  const [imagenSeleccionada, setImagenSeleccionada] = useState<string | null>(null);

  return (
    <div className="instalaciones-container">
      <h2 className="titulo">Explora Nuestro Espacio</h2>
      <p id="parrafo">Un lugar diseñado con amor y cuidado, donde tu mascota se sentirá como en casa. Ambientes modernos, seguros y llenos de calidez para que cada visita sea una experiencia única.</p><br />
      <div className="grid-imagenes">
        {imagenes.map((src, index) => (
          <div className="cuadro" key={index}>
            <img
              src={src}
              alt={`instalacion-${index}`}
              className="imagen"
              onClick={() => setImagenSeleccionada(src)}
            />
          </div>
        ))}
      </div>

      {imagenSeleccionada && (
        <div className="overlay" onClick={() => setImagenSeleccionada(null)}>
          <img src={imagenSeleccionada} alt="ampliada" className="imagen-ampliada" />
        </div>
      )}
    </div>
  );
};

export default Instalaciones;
