import React, { useEffect, useState } from "react";

import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import "./AsignarRolesPermisos.css";
import type { UsuarioResponse, Rol, UsuarioRol } from "../../../../components/interfaces/interfaces";
import IST from "../../../../components/proteccion_momentanea/IST";


const AsignarRolesPermisos: React.FC = () => {
  const [usuarios, setUsuarios] = useState<UsuarioResponse[]>([]);
  const [roles, setRoles] = useState<Rol[]>([]);
  const [usuariosRoles, setUsuariosRoles] = useState<UsuarioRol[]>([]);
  const [rolSeleccionado, setRolSeleccionado] = useState<{ [key: number]: number }>({});
  const [minimizado, setMinimizado] = useState(false);

  const baseURL = "http://localhost:8088/api";

  useEffect(() => {
    obtenerUsuarios();
    obtenerRoles();
    obtenerUsuariosRoles();
  }, []);

  const obtenerUsuarios = async () => {
    const res = await IST.get(`${baseURL}/usuarios`);
    setUsuarios(res.data);
  };

  const obtenerRoles = async () => {
    const res = await IST.get(`${baseURL}/roles`);
    setRoles(res.data);
  };

  const obtenerUsuariosRoles = async () => {
    const res = await IST.get(`${baseURL}/usuarios-roles`);
    setUsuariosRoles(res.data.data);
  };

  const asignarRol = async (idUsuario: number) => {
    const idRol = rolSeleccionado[idUsuario];
    if (!idRol) return alert("Selecciona un rol primero");

    try {
      await IST.post(`${baseURL}/usuarios-roles/asignar`, {
        idUsuario,
        idRol
      });
      alert("Rol asignado correctamente ✅");
      obtenerUsuariosRoles();
      setRolSeleccionado({ ...rolSeleccionado, [idUsuario]: 0 });
    } catch (error) {
      alert("Error al asignar el rol ❌");
      console.error(error);
    }
  };

  const eliminarRol = async (idUsuario: number, idRol: number) => {
    try {
      await IST.delete(`${baseURL}/usuarios-roles/eliminar`, {
        data: { idUsuario, idRol }
      });
      alert("Rol eliminado correctamente 🗑️");
      obtenerUsuariosRoles();
    } catch (error) {
      alert("Error al eliminar el rol ❌");
      console.error(error);
    }
  };

  const rolesDeUsuario = (idUsuario: number) => {
    return usuariosRoles.filter((ur) => ur.idUsuario === idUsuario);
  };

  const formatearFecha = (fechaStr: string) => {
    const fecha = new Date(fechaStr);
    return fecha.toLocaleDateString("es-PE", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      hour12: true
    });
  };

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
                <th>Asignar Rol</th>
              </tr>
            </thead>
            <tbody>
              {usuarios.map((usuario) => {
                const rolesUsuario = rolesDeUsuario(usuario.id);
                const maxRoles = 4;
                const puedeAsignarMas = rolesUsuario.length < maxRoles;

                if (rolesUsuario.length === 0) {
                  return (
                    <tr key={`${usuario.id}-sin-rol`}>
                      <td>{usuario.username}</td>
                      <td><span className="sin-rol">Sin rol asignado</span></td>
                      <td>-</td>
                      <td>
                        <div className="asignar-grupo">
                          <select
                            className="select-rol"
                            value={rolSeleccionado[usuario.id] || ""}
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
                          <button className="btn-asignar" onClick={() => asignarRol(usuario.id)}>
                            Asignar
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                }

                return rolesUsuario.map((usuarioRol, index) => {
                  const rolObj = roles.find((r) => r.nombre === usuarioRol.rol);

                  return (
                    <tr key={`${usuario.id}-${usuarioRol.rol}`}>
                      {index === 0 && (
                        <td rowSpan={puedeAsignarMas ? rolesUsuario.length + 1 : rolesUsuario.length}>
                          {usuario.username}
                        </td>
                      )}
                      <td><span className="badge-rol">{usuarioRol.rol}</span></td>
                      <td>{formatearFecha(usuarioRol.fechaAsignacion)}</td>
                      <td>
                        {rolObj && (
                          <button
                            className="btn-eliminar"
                            onClick={() => eliminarRol(usuario.id, rolObj.id)}
                          >
                            Eliminar Rol
                          </button>
                        )}
                      </td>
                    </tr>
                  );
                }).concat(
                  puedeAsignarMas ? (
                    <tr key={`${usuario.id}-asignar`}>
                      <td colSpan={2}>
                        <span className="texto-agregar">
                          Agregar otro rol ({rolesUsuario.length}/{maxRoles})
                        </span>
                      </td>
                      <td>
                        <div className="asignar-grupo">
                          <select
                            className="select-rol"
                            value={rolSeleccionado[usuario.id] || ""}
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
                          <button className="btn-asignar" onClick={() => asignarRol(usuario.id)}>
                            Asignar
                          </button>
                        </div>
                      </td>
                    </tr>
                  ) : []
                );
              })}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  );
};

export default AsignarRolesPermisos;
