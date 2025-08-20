import React from "react";
import "./Header.css";

const Header: React.FC = () => {
  return (
    <>
      <div className="menu container">
        <img className="logo-1" src="/images/logo.png" alt="Logo 1" />
        <input type="checkbox" id="menu" />
        <label htmlFor="menu">
          <img src="/images/menu.png" className="menu-icono" alt="Menú" />
        </label>

        <nav className="navbar">
          <div className="menu-1">
            <ul>
              <li><a href="#">Home</a></li>
              <li><a href="#instalaciones">Servicios</a></li>
              <li><a href="#">PetShop</a></li>
              <li><a href="#huellitas">Huellitas</a></li>
            </ul>
          </div>

          <img className="logo-2" src="/images/logo.png" alt="Logo 2" />

          <div className="menu-2">
            {/* <ul>
              <li><a href="login.html">Login</a></li>
            </ul> */}

            <div className="socials">
              <a href="#">
                <div className="social">
                  <img src="/images/s1.svg" alt="Social 1" />
                </div>
              </a>
              {/* <a href="#">
                <div className="social">
                  <img src="/images/s2.svg" alt="Social 2" />
                </div>
              </a> */}
              <a href="#">
                <div className="social">
                  <img src="/images/s3.svg" alt="Social 3" />
                </div>
              </a>
            </div>
          </div>
        </nav>
      </div>
    </>
  );
};

export default Header;
