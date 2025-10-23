import { useNavigate } from "react-router-dom"
import "./login.css"
import { useState } from "react"

function Login() {
  const [usuario, setUsuario] = useState("")
  const [password, setPassword] = useState("")
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()

    try {
      const response = await fetch("http://localhost:8088/api/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          usuario: usuario, 
          password: password  
        })
      })

      const data = await response.json()
      console.log("Respuesta del servidor:", data)

      if (response.ok && data.success) {   
        console.log("Login exitoso:", data.message)
        navigate("/administracion/home")
      } else {
        alert(data.message || "Credenciales incorrectas")
        setUsuario("");
        setPassword("");
      }

    } catch (error) {
      console.error("Error al iniciar sesión:", error)
      alert("Error al conectar con el servidor")
    }
  }

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
            />
          </div>
          <div className="casilla_login">
            <label>Contraseña:</label>
            <input
              type="password"
              placeholder="Ingresar contraseña"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          <button type="submit" className="btn_login">Ingresar</button>
        </form>
      </div>
    </div>
  )
}

export default Login
