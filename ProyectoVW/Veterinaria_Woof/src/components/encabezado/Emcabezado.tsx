import { useState } from 'react';
import './Emcabezado.css';
import { Link } from 'react-router-dom';

function Encabezado() {
  const [mostrarIndice, setMostrarIndice] = useState(false);
  const [menuAbierto, setMenuAbierto] = useState(false);

  return (
    <header id="cabecera">
      <div id="contenedor_cab">
        <a href="/" id="logo"><img src="/src/pages/home/images/logo.png" alt="logo" /></a>

        <nav className="menu-navegacion">
          <ul id="paginas">
            <li><a href="">Home</a></li>
            <li
              onMouseEnter={() => setMostrarIndice(true)}
              onMouseLeave={() => setMostrarIndice(false)}
            >
              <a href="#servicios">Servicios</a>
              {mostrarIndice && (
                <ul id="indice">
                  <li><a href="">Consulta veterinaria</a></li>
                  <li><a href="">Cirugía</a></li>
                  <li><a href="">Baño y peluquería</a></li>
                </ul>
              )}
            </li>
            <li><Link to="/tienda">Pet-Shop</Link></li>
            <li><a href="#huellitas">Huellitas</a></li>
          </ul>
        </nav>

        <div id="enlaces">
          <button id="sign_in"><a href="">Sign in</a></button>
          <Link to="/administracion/home"><button id="login">Login</button></Link>
          <button id="btn_menu" onClick={() => setMenuAbierto(true)}>☰</button>
        </div>
      </div>

      <div className={`panel-lateral ${menuAbierto ? 'activo' : ''}`}>
        <button className="cerrar" onClick={() => setMenuAbierto(false)}>×</button>
        <ul id="paginas-lateral">
          <li><a href="">home</a></li>
          <li>
            <a href="#servicios">Servicios</a>
            <ul id="indice">
              <li><a href="">Consulta veterinaria</a></li>
              <li><a href="">Cirugía</a></li>
              <li><a href="">Baño y peluquería</a></li>
            </ul>
          </li>
          <li><Link to="/tienda">Pet-Shop</Link></li>
          <li><a href="#huellitas">Huellitas</a></li>
        </ul>
      </div>
    </header>
  );
}

export default Encabezado;
