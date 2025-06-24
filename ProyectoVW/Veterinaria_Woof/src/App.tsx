import Encabezado from './components/encabezado/Emcabezado';
import Carrucel from './components/carrucel/CmvCliente';
import Pie_pagina from './components/footer/Footer';
import './App.css';

function App() {
  return (
    <>
      <Encabezado/>
      <section id="promo">
        <div id='contenido'>
          <div className='contenedor'>
            <h2>cuidados con amor</h2>
            <p>Confiables y seguros a cuidado de tu mascota</p>
            <button className="btn-reserva"></button>
          </div>
          <div className='contenedor'>
            <img src="./perro 2.png" alt="" />
          </div>
        </div>
        <div id='mapa'>
          <iframe 
            src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3901.343229625226!2d-77.07087265712026!3d-12.08863901720622!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x9105c9a79fd12ded%3A0xad33ff95c62045e9!2sManada%20Woof%20-%20Sal%C3%B3n%20%26%20Spa%20hospedaje%20canino!5e0!3m2!1ses-419!2spe!4v1749957161644!5m2!1ses-419!2spe" 
            allowFullScreen
            loading="lazy"
            referrerPolicy="no-referrer-when-downgrade">
          </iframe>
        </div>
      </section>
      <section id="servicios">
        <h2>NUESTROS SERVICIOS</h2>
        <div>
          <div className='contenedor_serv'>
            <a href="">
              <img src="./mumei_2.1-removebg-preview.png" alt="" />
              <p>Guarderia</p>
            </a>
          </div>
          <div className='contenedor_serv'>
            <a href="">
              <img src="./overnigth.png" alt="" />
              <p>Over Night</p>
            </a>
          </div>
          <div className='contenedor_serv'>
            <a href="">
              <img src="./baño.png" alt="" />
              <p>Baño y grooming</p>
            </a>
          </div>
          <div className='contenedor_serv'>
            <a href="">
              <img src="./mumei_2.1-removebg-preview.png" alt="" />
              <p>Veterinaria</p>
            </a>
          </div>
        </div>
      </section>
      <Carrucel/>
      <Pie_pagina/>
    </>
  )
}

export default App