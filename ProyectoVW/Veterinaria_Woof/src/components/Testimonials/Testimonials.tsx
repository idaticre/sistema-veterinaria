import React, { useState, useEffect } from "react";
import "./Testimonials.css";

interface Testimonial {
  id: number;
  name: string;
  pet: string;
  image: string;
  text: string;
  rating: number;
}

const testimonials: Testimonial[] = [
  {
    id: 1,
    name: "Edgar Quispe",
    pet: "Rocky",
    image: "/src/pages/home/images/Testimonials1.jpg",
    text: "En Manada Woof siempre cuidan de Rocky como si fuera suyo. Lo dejan limpio, feliz y cansado de tanto jugar. Además, siempre me mandan fotos y videos, lo que me da mucha tranquilidad.",
    rating: 5,
  },
  {
    id: 2,
    name: "Javier Torres",
    pet: "Mía",
    image: "/src/pages/home/images/Testimonials2.jpg",
    text: "La guardería de Manada Woof es perfecta. Mía adora venir aquí y yo sé que está segura y bien atendida. Me encanta que también tengan juguetes de calidad para llevarle a casa.",
    rating: 5,
  },
  {
    id: 3,
    name: "Lucía Fernández",
    pet: "Simba",
    image: "/src/pages/home/images/Testimonials3.jpeg",
    text: "Simba es muy activo y en Manada Woof siempre encuentra amigos para jugar. El personal es muy amable y profesional, y sus juguetes son de lo mejor que he visto.",
    rating: 5,
  }

];

const Testimonials: React.FC = () => {
  const [activeIndex, setActiveIndex] = useState(0);

  useEffect(() => {
    const timer = setInterval(() => {
      setActiveIndex((prev) => (prev + 1) % testimonials.length);
    }, 4000);
    return () => clearInterval(timer);
  }, []);

  const goToSlide = (index: number) => {
    setActiveIndex(index);
  };

  return (
    <section className="testimonials-section">
      <h2 className="testimonials-title">HUELLITAS</h2>
      <p className="testimonials-subtitle">
        "Aquí, cada patita cuenta."
      </p>

      <div className="testimonial-card">
        <img
          src={testimonials[activeIndex].image}
          alt={testimonials[activeIndex].name}
          className="testimonial-img"
        />

        <div className="testimonial-info">
          <h3>
            {testimonials[activeIndex].name} & {testimonials[activeIndex].pet}
          </h3>
          <p className="testimonial-text">{testimonials[activeIndex].text}</p>

          <div className="stars">
            {"★".repeat(testimonials[activeIndex].rating)}
            {"☆".repeat(5 - testimonials[activeIndex].rating)}
          </div>
        </div>
      </div>

      <div className="dots">
        {testimonials.map((_, index) => (
          <span
            key={index}
            className={`dot ${index === activeIndex ? "active" : ""}`}
            onClick={() => goToSlide(index)}
          ></span>
        ))}
      </div>
    </section>
  );
};

export default Testimonials;
