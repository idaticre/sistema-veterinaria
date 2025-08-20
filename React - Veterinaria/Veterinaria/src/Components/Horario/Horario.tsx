import React from "react";
import "./Horario.css";

const Horario: React.FC = () => {
  return (
    <section className="horario">
      <div className="horario-info container">
        <h2>Horario</h2>
        <div className="horario-txt">

          <div className="txt">
            <h4>Dirección</h4>
            <p>JR. Arequipa 238 Magdalena Del Mar</p>
          </div>

          <div className="txt">
            <h4>Horario</h4>
            <p>Lunes a Sábado : 9 am - 6 pm</p>
          </div>

          <div className="txt">
            <h4>Teléfono</h4>
            <a
              href="https://wa.me/51917233145?text=Hola%2C%20quiero%20reservar%20una%20cita"
              target="_blank"
              rel="noopener noreferrer"
            >
              <p style={{ color: "#ffffffff" }}>917 233 145</p>
            </a>
          </div>

          <div className="txt">
            <h4>Redes Sociales</h4>
            <div className="horario-socials">
              <a href="#">
                <div className="horario-social">
                  <img src="/images/s1.svg" alt="Facebook" />
                </div>
              </a>
              {/* <a href="#">
                <div className="horario-social">
                  <img src="/images/s2.svg" alt="Twitter" />
                </div>
              </a> */}
              <a href="#">
                <div className="horario-social">
                  <img src="/images/s3.svg" alt="Instagram" />
                </div>
              </a>
            </div>
          </div>

        </div>
      </div>
    </section>
  );
};

export default Horario;
