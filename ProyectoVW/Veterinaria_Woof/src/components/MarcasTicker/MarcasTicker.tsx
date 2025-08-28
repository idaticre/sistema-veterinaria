import React from "react";
import "./MarcasTicker.css";

type Logo = { src: string; alt: string };

const logos: Logo[] = [
  { src: "/src/pages/home/images/hills.png", alt: "Hill's" },
  { src: "/src/pages/home/images/pedigree.png", alt: "Pedigree" },
  { src: "/src/pages/home/images/catchow.jpg", alt: "Cat Chow" },
  { src: "/src/pages/home/images/ricocat.jpg", alt: "Ricocat" },
  { src: "/src/pages/home/images/whiskas.jpg", alt: "Whiskas" },
];

const MarcasTicker: React.FC = () => {
  
  const loop = [...logos, ...logos];

  return (
    <section className="marcas-wrapper" aria-label="Marcas destacadas">
      <h2 className="marcas-title">Contamos con las mejores marcas</h2>

      <div className="marcas-viewport">
        <div className="marcas-track">
          {loop.map((logo, i) => (
            <div className="marcas-item" key={`${logo.alt}-${i}`}>
              <img src={logo.src} alt={logo.alt} loading="lazy" />
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};

export default MarcasTicker;
