import React, { useState, /* useEffect */ } from 'react';
import './Carousel.css';

const slides = [
  {
    title: 'Servicio de Calidad',
    description:
      'Nuestro equipo de profesionales está altamente capacitado para cuidar de su mascota como si fuera nuestra propia familia. Nos aseguramos de que cada mascota reciba la atención y el amor que merece, brindando un servicio excepcional en cada visita.',
    image: 'images/slider1.png',
    whatsappMessage: 'Hola, quisiera más información sobre el servicio que brindan.',
  },
  {
    title: 'Pet Shop',
    description:
      'Descubre nuestra amplia gama de productos para mascotas, desde alimentos de alta calidad hasta accesorios y juguetes. Nos esforzamos por ofrecer solo lo mejor para tu compañero peludo, asegurando que encuentres todo lo que necesitas en un solo lugar.',
    image: 'images/slider2.png',
    whatsappMessage: 'Hola, estoy interesado en adquirir articulos de su catalogo PET-SHOP.',
  },
  {
    title: 'Guardería',
    description:
      'Ofrecemos un ambiente seguro y divertido para que tu mascota socialice y juegue mientras estás fuera. Nuestro personal capacitado se asegura de que cada mascota esté feliz y saludable, brindando atención personalizada y actividades adecuadas a sus necesidades.',
    image: 'images/slider3.png',
    whatsappMessage: 'Hola, quisiera más información sobre la Guardería, para mi mascota.',
  },
  {
    title: 'Peluquería Canina',
    description:
      'Tu mascota merece lucir siempre radiante. Ofrecemos baños, cortes de pelo y grooming especializado para mantener su higiene, comodidad y estilo.',
    image: 'images/peluqueria.png',
    whatsappMessage: 'Hola, quisiera reservar un servicio de Peluquería Canina.',
  },
  {
    title: 'Alimentos Premium',
    description:
      'Ofrecemos alimentos balanceados y nutritivos para perros y gatos, seleccionados de las mejores marcas. Nos preocupamos por la salud de tu mascota brindando opciones que se adaptan a cada etapa de su vida.',
    image: 'images/alimentos.png',
    whatsappMessage: 'Hola, quisiera más información sobre los alimentos de mascotas.',
  },
];

const Carousel: React.FC = () => {
  const [currentSlide, setCurrentSlide] = useState(0);

  const nextSlide = () => {
    setCurrentSlide((prev) => (prev + 1) % slides.length);
  };

  const prevSlide = () => {
    setCurrentSlide((prev) => (prev - 1 + slides.length) % slides.length);
  };

  /* useEffect(() => {
    const interval = setInterval(() => {
      nextSlide();
    }, 3000); // Cambia cada 3 segundos

    return () => clearInterval(interval); // Limpia el intervalo al desmontar
  }, []); */

  return (
    <section className="header-content container">
      <div className="custom-carousel">
        <div className="carousel-slide">
          <div className="slider">
            <div className="slider-txt">
              <h1>{slides[currentSlide].title}</h1>
              <p>{slides[currentSlide].description}</p>
              <div className="botones">
                <a
                  href={`https://wa.me/51917233145?text=${encodeURIComponent(
                    slides[currentSlide].whatsappMessage
                  )}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="btn-1"
                >
                  Info
                </a>
              </div>
            </div>
            <div className="slider-img">
              <img
                src={slides[currentSlide].image}
                alt={slides[currentSlide].title}
              />
            </div>
          </div>

          {/* Flechas */}
          <button className="carousel-btn prev-btn" onClick={prevSlide}>
            ⟨
          </button>
          <button className="carousel-btn next-btn" onClick={nextSlide}>
            ⟩
          </button>
        </div>

        {/* Puntos */}
        <div className="carousel-dots">
          {slides.map((_, idx) => (
            <span
              key={idx}
              className={idx === currentSlide ? 'dot active' : 'dot'}
              onClick={() => setCurrentSlide(idx)}
            ></span>
          ))}
        </div>
      </div>
    </section>
  );
};

export default Carousel;
