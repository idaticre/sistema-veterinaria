import React, { useEffect, useState } from "react";
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import "./AsignarRolesPermisos.css";
import type { UsuarioResponse, Rol, UsuarioRol } from "../../../../components/interfaces/interfaces";
import IST from "../../../../components/proteccion/IST";

// --- CONSTANTES EXACTAS DE TU BASE DE DATOS (SQL) ---
const ROL_ADMIN_GENERAL = "ADMINISTRADOR GENERAL";
const ROL_ADMIN_GENERAL_2 = "ADMINISTRADOR GENERAL 2";

const AsignarRolesPermisos: React.FC = () => {
  // --- ESTADOS DE DATOS ---
  const [usuarios, setUsuarios] = useState<UsuarioResponse[]>([]);
  const [roles, setRoles] = useState<Rol[]>([]);
  const [usuariosRoles, setUsuariosRoles] = useState<UsuarioRol[]>([]);
  const [rolSeleccionado, setRolSeleccionado] = useState<{ [key: number]: number }>({});

  // --- ESTADOS DE UI Y ACCESO ---
  const [minimizado, setMinimizado] = useState(false);
  const [accesoDenegado, setAccesoDenegado] = useState(false);
  const [cargando, setCargando] = useState(true);

  // Información del usuario actual
  const [currentUser, setCurrentUser] = useState<string>("");
  const [soyAdminGeneral, setSoyAdminGeneral] = useState(false);
  const [soyAdminGeneral2, setSoyAdminGeneral2] = useState(false);

  const baseURL = "http://localhost:8088/api";

  useEffect(() => {
    verificarAccesoYCargar();
  }, []);

  const verificarAccesoYCargar = async () => {
    // 1. OBTENER DATOS DE SESSIONSTORAGE
    const token = sessionStorage.getItem("token");
    const usuarioLogueado = sessionStorage.getItem("usuario");
    const rolesStorage = sessionStorage.getItem("roles");

    if (!token || !usuarioLogueado || !rolesStorage) {
      setAccesoDenegado(true);
      setCargando(false);
      return;
    }

    setCurrentUser(usuarioLogueado);

    // 2. PARSEAR ROLES DEL USUARIO LOGUEADO
    let rolesDelUsuario: string[] = [];
    try {
      const rolesParsed = JSON.parse(rolesStorage);
      if (Array.isArray(rolesParsed)) {
        rolesDelUsuario = rolesParsed.map((r: any) =>
          (typeof r === 'string' ? r : r.nombre || r.authority || "").toUpperCase()
        );
      }
    } catch (e) {
      console.error("Error al leer roles", e);
      setAccesoDenegado(true);
      setCargando(false);
      return;
    }

    // 3. DETERMINAR NIVEL DE PRIVILEGIO
    const esAdminG = rolesDelUsuario.includes(ROL_ADMIN_GENERAL);
    const esAdminG2 = rolesDelUsuario.includes(ROL_ADMIN_GENERAL_2);

    setSoyAdminGeneral(esAdminG);
    setSoyAdminGeneral2(esAdminG2);

    // 4. BLOQUEO DE SEGURIDAD
    if (!esAdminG && !esAdminG2) {
      setAccesoDenegado(true);
      setCargando(false);
    } else {
      setAccesoDenegado(false);
      // Cargar datos
      await Promise.all([obtenerUsuarios(), obtenerRoles(), obtenerUsuariosRoles()]);
      setCargando(false);
    }
  };

  // --- API CALLS ---
  const obtenerUsuarios = async () => {
    try {
      const res = await IST.get(`${baseURL}/usuarios`);
      setUsuarios(res.data);
    } catch (e) { console.error(e); }
  };

  const obtenerRoles = async () => {
    try {
      const res = await IST.get(`${baseURL}/roles`);
      setRoles(res.data);
    } catch (e) { console.error(e); }
  };

  const obtenerUsuariosRoles = async () => {
    try {
      const res = await IST.get(`${baseURL}/usuarios-roles`);
      setUsuariosRoles(res.data.data);
    } catch (e) { console.error(e); }
  };

  const asignarRol = async (idUsuario: number) => {
    const idRol = rolSeleccionado[idUsuario];
    if (!idRol) return alert("Selecciona un rol primero");
    try {
      await IST.post(`${baseURL}/usuarios-roles/asignar`, { idUsuario, idRol });
      alert("Rol asignado correctamente");
      obtenerUsuariosRoles();
      setRolSeleccionado({ ...rolSeleccionado, [idUsuario]: 0 });
    } catch (error) {
      alert("Error al asignar el rol");
    }
  };

  const eliminarRol = async (idUsuario: number, idRol: number) => {
    try {
      await IST.delete(`${baseURL}/usuarios-roles/eliminar`, { data: { idUsuario, idRol } });
      alert("Rol eliminado correctamente");
      obtenerUsuariosRoles();
    } catch (error) {
      alert("Error al eliminar el rol");
    }
  };

  // --- HELPERS ---
  const rolesDeUsuario = (idUsuario: number) => {
    return usuariosRoles.filter((ur) => ur.idUsuario === idUsuario);
  };

  const formatearFecha = (fechaStr: string) => {
    const fecha = new Date(fechaStr);
    return fecha.toLocaleDateString("es-PE", {
      day: "2-digit", month: "2-digit", year: "numeric",
      hour: "2-digit", minute: "2-digit", hour12: true,
    });
  };

  // =====================================================================
  //  LÓGICA MAESTRA DE VISUALIZACIÓN (TABLA)
  // =====================================================================
  const usuariosFiltrados = usuarios.filter((u) => {
    // 1. Nunca mostrar al propio usuario logueado
    if (u.username === currentUser) return false;

    // Obtenemos los roles del usuario que estamos evaluando mostrar
    const susRoles = rolesDeUsuario(u.id);
    const esTargetSuperAdmin = susRoles.some(r => r.rol === ROL_ADMIN_GENERAL);
    const esTargetAdminG2 = susRoles.some(r => r.rol === ROL_ADMIN_GENERAL_2);

    // 2. REGLAS PARA EL ADMINISTRADOR GENERAL (Tú)
    if (soyAdminGeneral) {
      // No mostrar a otros Administradores Generales (exclusividad)
      if (esTargetSuperAdmin) return false;
      // SI mostrar a Administrador General 2 (Permitido explícitamente)
      return true; 
    }

    // 3. REGLAS PARA EL ADMINISTRADOR GENERAL 2
    if (soyAdminGeneral2) {
      // NO ver al Administrador General (Superiores)
      if (esTargetSuperAdmin) return false;
      // NO ver a otros Administradores General 2 (Iguales)
      if (esTargetAdminG2) return false;
      // Ver solo roles inferiores
      return true;
    }

    return false;
  });

  // =====================================================================
  //  LÓGICA MAESTRA DE PERMISOS (SELECT DESPLEGABLE)
  // =====================================================================
  const filtrarRolesDisponibles = (rolesYaAsignados: string[]) => {
    return roles.filter(r => {
      // 1. Filtro básico: No mostrar roles que el usuario ya tiene
      if (rolesYaAsignados.includes(r.nombre)) return false;

      // 2. REGLA: NADIE asigna "ADMINISTRADOR GENERAL" (Rol único/dios)
      if (r.nombre === ROL_ADMIN_GENERAL) return false;

      // 3. REGLA: Si soy Admin G2, NO puedo asignar Admin G2
      if (soyAdminGeneral2) {
        if (r.nombre === ROL_ADMIN_GENERAL_2) return false;
      }
      
      // El Admin General SI puede asignar Admin G2 (por eso no se filtra si soyAdminGeneral)
      return true;
    });
  };

  // --- RENDER ---
  if (cargando) return <div className="loading-container">Verificando credenciales...</div>;

  if (accesoDenegado) {
    return (
      <div className="acceso-denegado-wrapper">
        <Br_administrativa onMinimizeChange={setMinimizado} />
        <div className={`contenido-roles ${minimizado ? "minimize" : ""}`}>
           <div className="mensaje-error" style={{ textAlign: "center", marginTop: "100px", color: "#e74c3c" }}>
             <i className="fa-solid fa-lock" style={{fontSize: "3rem", marginBottom: "20px"}}></i>
             <h2>Acceso No Permitido</h2>
             <p>Su usuario no cuenta con los permisos necesarios para acceder a la gestión de roles.</p>
           </div>
        </div>
      </div>
    );
  }

  return (
    <div className="layout-roles">
      <Br_administrativa onMinimizeChange={setMinimizado} />

      <main className={`contenido-roles ${minimizado ? "minimize" : ""}`}>
        <div className="header-roles">
          <h1>Gestión de Roles y Permisos</h1>
        </div>

        <div className="tabla-wrapper">
          <table className="tabla-roles">
            <thead>
              <tr>
                <th>Usuario</th>
                <th>Rol Asignado</th>
                <th>Fecha de Asignación</th>
                <th>Acción</th>
              </tr>
            </thead>

            <tbody>
              {usuariosFiltrados.map((usuario) => {
                const rolesUsuario = rolesDeUsuario(usuario.id);
                const rolesAsignadosNombres = rolesUsuario.map((r) => r.rol);
                
                // APLICAMOS EL FILTRO DE ROLES EN EL SELECT
                const rolesDisponibles = filtrarRolesDisponibles(rolesAsignadosNombres);
                
                // Calculamos si puede asignar más roles (solo si hay roles disponibles para su nivel)
                const puedeAsignarMas = rolesDisponibles.length > 0;

                // --- Caso: Usuario SIN roles ---
                if (rolesUsuario.length === 0) {
                  return (
                    <tr key={`${usuario.id}-sin-rol`} className="fila-usuario">
                      <td>{usuario.username}</td>
                      <td><span className="sin-rol">Sin rol asignado</span></td>
                      <td>-</td>
                      <td>
                        <div className="asignar-grupo">
                          <select
                            className="select-rol"
                            value={rolSeleccionado[usuario.id] || ""}
                            onChange={(e) => setRolSeleccionado({ ...rolSeleccionado, [usuario.id]: parseInt(e.target.value) })}
                          >
                            <option value="">Seleccione un rol</option>
                            {rolesDisponibles.map((rol) => (
                              <option key={rol.id} value={rol.id}>{rol.nombre}</option>
                            ))}
                          </select>
                          <button className="btn-asignar" onClick={() => asignarRol(usuario.id)}>Asignar</button>
                        </div>
                      </td>
                    </tr>
                  );
                }

                // --- Caso: Usuario CON roles ---
                return [
                  ...rolesUsuario.map((usuarioRol, index) => {
                    const esPrimeraFila = index === 0;
                    const rolObj = roles.find((r) => r.nombre === usuarioRol.rol);

                    // Lógica para permitir eliminar rol:
                    // Admin General puede eliminar todo (menos a otros admin generales, que ya están ocultos).
                    // Admin G2 NO puede eliminar rol de Super Admin (ya oculto) ni de Admin G2 (ya oculto).
                    const puedeEliminar = true; // El filtro visual ya protege lo que no se debe tocar.

                    return (
                      <tr key={`${usuario.id}-${usuarioRol.rol}`} className={esPrimeraFila ? "fila-usuario" : ""}>
                        {esPrimeraFila && (
                          <td rowSpan={puedeAsignarMas ? rolesUsuario.length + 1 : rolesUsuario.length}>
                            {usuario.username}
                          </td>
                        )}
                        <td><span className="badge-rol">{usuarioRol.rol}</span></td>
                        <td>{formatearFecha(usuarioRol.fechaAsignacion)}</td>
                        <td>
                          {rolObj && puedeEliminar && (
                            <button className="btn-eliminar" onClick={() => eliminarRol(usuario.id, rolObj.id)}>
                              Eliminar Rol
                            </button>
                          )}
                        </td>
                      </tr>
                    );
                  }),
                  // Fila para agregar otro rol
                  puedeAsignarMas && (
                    <tr key={`${usuario.id}-agregar`}>
                      <td colSpan={2}>
                        <span className="texto-agregar">Agregar otro rol</span>
                      </td>
                      <td>
                        <div className="asignar-grupo">
                          <select
                            className="select-rol"
                            value={rolSeleccionado[usuario.id] || ""}
                            onChange={(e) => setRolSeleccionado({ ...rolSeleccionado, [usuario.id]: parseInt(e.target.value) })}
                          >
                            <option value="">Seleccione un rol</option>
                            {rolesDisponibles.map((rol) => (
                              <option key={rol.id} value={rol.id}>{rol.nombre}</option>
                            ))}
                          </select>
                          <button className="btn-asignar" onClick={() => asignarRol(usuario.id)}>Asignar</button>
                        </div>
                      </td>
                    </tr>
                  ),
                ];
              })}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  );
};

export default AsignarRolesPermisos;