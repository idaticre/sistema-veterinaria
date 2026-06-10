import axios from "axios";

const IST = axios.create({
  // Usando el backend en la nube de Rende baseURL: "https://sistema-veterinaria.onrender.com/api",
  baseURL: "http://localhost:8080/api",
});

IST.interceptors.request.use((config) => {
  const token = sessionStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => Promise.reject(error));

export default IST;