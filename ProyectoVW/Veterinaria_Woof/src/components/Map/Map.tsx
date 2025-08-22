// components/Map/Map.tsx
import React from 'react';
import './Map.css';

const Mapa: React.FC = () => {
  return (
    <section id="promo">
      <div id='contenido'>
        <div className='contenedor'>
          <h2>cuidados con amor</h2>
          <p id='parrafo'>Confiables y seguros a cuidado de tu mascota</p>
          <a href="https://wa.me/51917233145?text=Hola%2C%20quiero%20reservar%20una%20cita" target="_blank" rel="noopener noreferrer">
            <button className="btn-reserva">RESERVA AHORA</button>
          </a>
        </div>
        <div className='contenedor'>
          <img src="/src/pages/home/images/cat.png" alt="Cute dog" />
        </div>
      </div>
      <div id='mapa'>
        <iframe 
          src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3901.343229625235!2d-77.07087265712026!3d-12.08863901720622!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x9105c9a79fd12ded%3A0xad33ff95c62045e9!2sManada%20Woof%20-%20Sal%C3%B3n%20%26%20Spa%20hospedaje%20canino!5e0!3m2!1ses!2spe!4v1749941783519!5m2!1ses!2spe" 
          allowFullScreen
          loading="lazy"
          referrerPolicy="no-referrer-when-downgrade"
          title="Ubicación Manada Woof">
        </iframe>
      </div>
    </section>
  );
};

export default Mapa;