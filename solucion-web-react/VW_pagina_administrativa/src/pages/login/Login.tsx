import { useNavigate } from "react-router-dom";
import "./login.css";
import { useState } from "react";

function Login() {
  const [usuario, setUsuario] = useState("");
  const [password, setPassword] = useState("");
  const [mostrarPass, setMostrarPass] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    try {
      const response = await fetch("http://localhost:8088/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username: usuario,
          password: password,
        }),
      });

      if (!response.ok) {
        alert("Credenciales incorrectas");
        setUsuario("");
        setPassword("");
        return;
      }

      const data = await response.json(); 
      console.log("Respuesta del servidor:", data);

      if (data.token) {
        sessionStorage.setItem("token", data.token);
        sessionStorage.setItem("usuario", data.username);
        sessionStorage.setItem("roles", JSON.stringify(data.roles));

        navigate("/administracion/home");
      } else {
        alert("Credenciales incorrectas");
        setUsuario("");
        setPassword("");
      }
    } catch (error) {
      console.error("Error al iniciar sesión:", error);
      alert("Error al conectar con el servidor");
    }
  };

  return (
    <div className="contenedor_login">
      <div className="form_login">
        <h2>INGRESO</h2>
        <form onSubmit={handleSubmit}>
          <div className="casilla_login">
            <label>Usuario:</label>
            <input
              type="text"
              placeholder="Ingrese su usuario"
              value={usuario}
              onChange={(e) => setUsuario(e.target.value)}
              required
            />
          </div>
          <div className="casilla_login">
            <label>Contraseña:</label>
            <input
              type={mostrarPass ? "text" : "password"}
              placeholder="Ingrese su contraseña"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            <span className="mostrar_contra" onClick={() => setMostrarPass(!mostrarPass)}>
              {mostrarPass ? <i className="fa-solid fa-eye-slash"></i> : <i className="fa-solid fa-eye"></i>}
            </span>
          </div>
          <button type="submit" className="btn_login">Ingresar</button>
        </form>
      </div>
    </div>
  );
}

export default Login;
