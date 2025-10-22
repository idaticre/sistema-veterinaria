import React, { useEffect, useState } from "react";
import axios from "axios";
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import "./AsignarRolesPermisos.css";
import type { Usuario, Rol, UsuarioRol } from "../../../../components/interfaces/interfaces";

const AsignarRolesPermisos: React.FC = () => {
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [roles, setRoles] = useState<Rol[]>([]);
  const [usuariosRoles, setUsuariosRoles] = useState<UsuarioRol[]>([]);
  const [rolSeleccionado, setRolSeleccionado] = useState<{ [key: number]: number }>({});
  const [minimizado, setMinimizado] = useState(false);

  const baseURL = "http://localhost:8088/api";

  //Cargar usuarios, roles y relaciones actuales
  useEffect(() => {
    obtenerUsuarios();
    obtenerRoles();
    obtenerUsuariosRoles();
  }, []);

  const obtenerUsuarios = async () => {
    const res = await axios.get(`${baseURL}/usuarios`);
    setUsuarios(res.data);
  };

  const obtenerRoles = async () => {
    const res = await axios.get(`${baseURL}/roles`);
    setRoles(res.data);
  };

  const obtenerUsuariosRoles = async () => {
    const res = await axios.get(`${baseURL}/usuarios-roles`);
    setUsuariosRoles(res.data.data);
  };

  //Asignar rol
  const asignarRol = async (idUsuario: number) => {
    const idRol = rolSeleccionado[idUsuario];
    if (!idRol) return alert("Selecciona un rol primero");

    try {
      await axios.post(`${baseURL}/usuarios-roles/asignar`, {
        idUsuario,
        idRol
      });
      alert("Rol asignado correctamente ✅");
      obtenerUsuariosRoles();
    } catch (error) {
      alert("Error al asignar el rol ❌");
      console.error(error);
    }
  };

  //Eliminar rol
  const eliminarRol = async (idUsuario: number, idRol: number) => {
    try {
      await axios.delete(`${baseURL}/usuarios-roles/eliminar`, {
        data: { idUsuario, idRol }
      });
      alert("Rol eliminado correctamente 🗑️");
      obtenerUsuariosRoles();
    } catch (error) {
      alert("Error al eliminar el rol ❌");
      console.error(error);
    }
  };

  //Obtener roles de un usuario
  const rolesDeUsuario = (username: string) => {
    return usuariosRoles.filter((ur) => ur.username === username).map((ur) => ur.rol);
  };

  return (
    <div className="layout-roles">
      <Br_administrativa onMinimizeChange={setMinimizado} />

      <main className={`contenido-roles ${minimizado ? "minimize" : ""}`}>
        <div className="asignar-container">
          <h2>Asignar y Gestionar Roles</h2>
          <div className="tabla-wrapper">
            <table className="asignar-tabla">
              <thead>
                <tr>
                  <th>Usuario</th>
                  <th>Roles Asignados</th>
                  <th>Asignar Nuevo Rol</th>
                  <th>Acción</th>
                </tr>
              </thead>
              <tbody>
                {usuarios.map((usuario) => {
                  const rolesUsuario = rolesDeUsuario(usuario.username);
                  return (
                    <tr key={usuario.id}>
                      <td>{usuario.username}</td>
                      <td>
                        {rolesUsuario.length > 0 ? (
                          <ul className="lista-roles">
                            {rolesUsuario.map((rolNombre) => {
                              const rolObj = roles.find((r) => r.nombre === rolNombre);
                              return (
                                <li key={rolNombre}>
                                  <span className="rol-nombre">{rolNombre}</span>
                                  {rolObj && (
                                    <button
                                      className="btn-eliminar"
                                      onClick={() => eliminarRol(usuario.id, rolObj.id)}
                                    >
                                      Eliminar
                                    </button>
                                  )}
                                </li>
                              );
                            })}
                          </ul>
                        ) : (
                          <span className="sin-rol">Sin rol asignado</span>
                        )}
                      </td>

                      <td>
                        <select
                          className="select-rol"
                          onChange={(e) =>
                            setRolSeleccionado({
                              ...rolSeleccionado,
                              [usuario.id]: parseInt(e.target.value)
                            })
                          }
                        >
                          <option value="">Seleccione un rol</option>
                          {roles.map((rol) => (
                            <option key={rol.id} value={rol.id}>
                              {rol.nombre}
                            </option>
                          ))}
                        </select>
                      </td>

                      <td>
                        <button className="btn-asignar" onClick={() => asignarRol(usuario.id)}>
                          Asignar
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      </main>
    </div>
  );
};

export default AsignarRolesPermisos;