import axios from "axios";

const IST = axios.create({
  baseURL: "https://sistema-veterinaria.onrender.com",
});

IST.interceptors.request.use((config) => {
  const token = sessionStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => Promise.reject(error));



export default IST;