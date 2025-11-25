import { Navigate, Outlet } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

const RutaProtegida = () => {
  const token = sessionStorage.getItem("token");

  if (!token) return <Navigate to="/administracion/login" replace />;

  try {
    const decoded: any = jwtDecode(token);
    const exp = decoded.exp * 1000; 

    if (Date.now() > exp) {
      sessionStorage.clear();
      return <Navigate to="/administracion/login" replace />;
    }
  } catch (error) {
    sessionStorage.clear();
    return <Navigate to="/administracion/login" replace />;
  }

  return <Outlet />;
};

export default RutaProtegida;
