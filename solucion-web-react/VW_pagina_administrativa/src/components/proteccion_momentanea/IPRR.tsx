import { Navigate, Outlet } from "react-router-dom";

interface Props {
  roles: string[];
}

const IPRR: React.FC<Props> = ({ roles }) => {
  const rolesGuardados = sessionStorage.getItem("roles");

  const rolesUsuario: string[] = rolesGuardados ? JSON.parse(rolesGuardados) : [];

  const tieneAcceso = roles.some(rolRequerido =>
    rolesUsuario.includes(rolRequerido)
  );

  if (!tieneAcceso) {
    alert("Permiso denegado");
    return <Navigate to="/administracion/home" replace />;
  }

  return <Outlet />;
};

export default IPRR;