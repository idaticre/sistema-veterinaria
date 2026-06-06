import { Navigate, Outlet } from "react-router-dom";
import Swal from 'sweetalert2';

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
    Swal.fire({
      title: "Error",
      text: "Permiso denegado",
      icon: "error"
    });
    return <Navigate to="/administracion/home" replace />;
  }

  return <Outlet />;
};

export default IPRR;