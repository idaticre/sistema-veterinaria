import axios from "axios";

const IST = axios.create({
  baseURL: "http://localhost:8080/api",
});

IST.interceptors.request.use((config) => {
  const token = sessionStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => Promise.reject(error));

IST.interceptors.response.use(
  (response) => response,
  (error) => {
    let mensaje = "";

    if (!error.response) {
      mensaje = "🔌 Sin conexión a internet";
    } else {
      const status = error.response.status;

      if (status >= 500) {
        mensaje = "💥 Error interno del servidor";
      } else if (status === 404) {
        mensaje = "🔍 Recurso no encontrado";
      } else if (status === 401) {
        mensaje = "🔒 Sesión expirada";
      }
    }

    if (mensaje) {
      window.dispatchEvent(
        new CustomEvent("error-global", { detail: mensaje })
      );
    }

    return Promise.reject(error);
  }
);

export default IST;