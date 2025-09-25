import { Link } from "react-router-dom"
import "./login.css" 

function Login() {
  return (
    <div className="contenedor_login">
        <div className="form_login">
          <h2>INGRESO</h2>
          <form action="">
            <div className="casilla_login">
              <label>Correo:</label>
              <input type="email" placeholder="Ingrese su correo"/>
            </div>
            <div className="casilla_login">
              <label>Contraseña:</label>
              <input type="password" placeholder="Ingresar contraseña"/>
            </div>
            <Link to="/administracion/home">
              <button type="submit" className="btn_login">Ingresar</button>
            </Link>
          </form>
        </div>
    </div>
  )
}


export default Login