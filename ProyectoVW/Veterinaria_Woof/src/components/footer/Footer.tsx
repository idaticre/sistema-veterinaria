import React from "react";
import "./Footer.css";

const Footer: React.FC = () => {
  return (
    <footer className="app-footer container">
      <img className="footer-logo" src="/src/pages/home/images/logo.png" alt="Logo" />

      {/* Contacto */}
      <div className="footer-links">
        <h4>
          Contacto
          <hr />
        </h4>
        <ul>
          <li>
            <a
              href="https://wa.me/51917233145?text=Hola%2C%20quiero%20reservar%20una%20cita"
              target="_blank"
              rel="noopener noreferrer"
            >
              917 233 145
            </a>
          </li>
        </ul>
        <br />
        <h4>
          Dirección
          <hr />
        </h4>
        <ul>
          <li><a href="#">JR. Arequipa 238</a></li>
          <li><a href="#">Magdalena Del Mar</a></li>
        </ul>
      </div>

      {/* Mapa del sitio */}
      <div className="footer-links">
        <h4>
          Mapa del sitio
          <hr />
        </h4>
        <ul>
          <li><a href="#">Home</a></li>
          <li><a href="#">Servicios</a></li>
          <li><a href="#">&gt; Daycare</a></li>
          <li><a href="#">&gt; Overnight</a></li>
          <li><a href="#">&gt; Baños y Grooming</a></li>
          <li><a href="#">&gt; Veterinario</a></li>
          <li><a href="#">Blog</a></li>
          <li><a href="#">Contacto</a></li>
        </ul>
      </div>

      {/* Información */}
      <div className="footer-links">
        <h4>
          Información
          <hr />
        </h4>
        <ul>
          <li><a href="#">Políticas de Privacidad</a></li>
          <li><a href="#">Términos y condiciones de los servicios</a></li>
        </ul>
      </div>

      {/* Síguenos */}
      <div className="footer-links">
        <h4>
          Síguenos
          <hr />
        </h4>
        <div className="footer-socials">
          <a href="#">
            <div className="footer-social">
              <img src="/src/pages/home/images/s1.svg" alt="Facebook" />
            </div>
          </a>
          {/* <a href="#">
            <div className="footer-social">
              <img src="/images/s2.svg" alt="Twitter" />
            </div>
          </a> */}
          <a href="#">
            <div className="footer-social">
              <img src="/src/pages/home/images/s3.svg" alt="Instagram" />
            </div>
          </a>
        </div>
      </div>
    </footer>
  );
};

export default Footer;