import React, { useState, useEffect } from 'react';
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import type { Usuario, Rol } from '../../../../components/interfaces/interfaces';
import './AsignarRolesPermisos.css';

const AsignarRolesPermisos: React.FC = () => {
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [roles, setRoles] = useState<Rol[]>([]);
  const [loading, setLoading] = useState(true);
  const [usuarioSeleccionado, setUsuarioSeleccionado] = useState<number | null>(null);
  const [rolSeleccionado, setRolSeleccionado] = useState<number | null>(null);
  const [busqueda, setBusqueda] = useState('');
  const [modoEdicion, setModoEdicion] = useState(false);
  const [mensaje, setMensaje] = useState<{ tipo: 'success' | 'error' | 'warning' | '', texto: string }>({ tipo: '', texto: '' });
  const [minimizado, setMinimizado] = useState(false);
  const [procesando, setProcesando] = useState(false);

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      setLoading(true);
      
      const [usuariosRes, rolesRes, asignacionesRes] = await Promise.all([
        fetch('http://localhost:8088/api/usuarios'),
        fetch('http://localhost:8088/api/roles'),
        fetch('http://localhost:8088/api/usuarios-roles/detallado')
      ]);

      const usuariosData: Usuario[] = await usuariosRes.json();
      const rolesData: Rol[] = await rolesRes.json();
      
      let asignaciones: any[] = [];
      if (asignacionesRes.ok && asignacionesRes.status !== 204) {
        asignaciones = await asignacionesRes.json();
      }

      // Enriquecer usuarios con información de roles
      const usuariosEnriquecidos = usuariosData.map(usuario => {
        const asignacion = asignaciones.find((a: any) => a.usuarioId === usuario.id);
        return {
          ...usuario,
          rol_actual: asignacion?.rolNombre || null,
          id_rol_actual: asignacion?.rolId || null,
          id_asignacion: asignacion?.id || null
        };
      });

      setUsuarios(usuariosEnriquecidos);
      setRoles(rolesData);
    } catch (error) {
      console.error('Error al cargar datos:', error);
      setMensaje({ tipo: 'error', texto: 'Error al cargar datos del servidor' });
    } finally {
      setLoading(false);
    }
  };

  const usuariosFiltrados = usuarios.filter(u =>
    u.username.toLowerCase().includes(busqueda.toLowerCase())
  );

  const handleSeleccionarUsuario = (usuarioId: number) => {
    const usuario = usuarios.find(u => u.id === usuarioId);
    if (usuario) {
      setUsuarioSeleccionado(usuarioId);
      setRolSeleccionado((usuario as any).id_rol_actual);
      setModoEdicion(false);
      setMensaje({ tipo: '', texto: '' });
    }
  };

  const handleAsignarRol = async () => {
    if (!usuarioSeleccionado || !rolSeleccionado) {
      setMensaje({ tipo: 'error', texto: 'Debe seleccionar un usuario y un rol' });
      return;
    }

    try {
      setProcesando(true);
      const usuario = usuarios.find(u => u.id === usuarioSeleccionado);
      const rol = roles.find(r => r.id === rolSeleccionado);

      if (!usuario || !rol) return;

      // Si el usuario ya tiene el mismo rol
      if ((usuario as any).id_rol_actual === rolSeleccionado) {
        setMensaje({ tipo: 'warning', texto: `El usuario ya tiene asignado el rol: ${rol.nombre}` });
        setProcesando(false);
        return;
      }

      // Si el usuario ya tiene un rol, eliminarlo primero
      if ((usuario as any).id_rol_actual) {
        const rolesUsuarioRes = await fetch(`http://localhost:8088/api/usuarios-roles/${usuarioSeleccionado}`);
        
        if (rolesUsuarioRes.ok && rolesUsuarioRes.status !== 204) {
          const rolesUsuario = await rolesUsuarioRes.json();
          
          if (rolesUsuario.length > 0) {
            const asignacionId = rolesUsuario[0].id;
            const deleteRes = await fetch(
              `http://localhost:8088/api/usuarios-roles/${asignacionId}`,
              { method: 'DELETE' }
            );
            
            if (!deleteRes.ok) {
              throw new Error('No se pudo eliminar el rol anterior');
            }
          }
        }
        
        await new Promise(resolve => setTimeout(resolve, 300));
      }

      // Asignar el nuevo rol
      const response = await fetch(
        `http://localhost:8088/api/usuarios-roles?usuarioId=${usuarioSeleccionado}&rolId=${rolSeleccionado}`,
        { method: 'POST' }
      );

      if (response.ok) {
        setMensaje({
          tipo: 'success',
          texto: `Rol "${rol.nombre}" asignado correctamente a ${usuario.username}`
        });
        setModoEdicion(false);
        await cargarDatos();
      } else {
        throw new Error('Error al asignar rol');
      }
    } catch (error) {
      console.error('Error:', error);
      setMensaje({ tipo: 'error', texto: 'Error al asignar el rol. Intente nuevamente.' });
    } finally {
      setProcesando(false);
    }
  };

  const handleRevocarRol = async () => {
    if (!usuarioSeleccionado) return;

    const usuario = usuarios.find(u => u.id === usuarioSeleccionado);
    if (!usuario || !(usuario as any).rol_actual) {
      setMensaje({ tipo: 'error', texto: 'El usuario no tiene un rol asignado' });
      return;
    }

    if (!confirm(`¿Seguro que deseas revocar el rol de ${usuario.username}?`)) {
      return;
    }

    try {
      setProcesando(true);

      const rolesUsuarioRes = await fetch(`http://localhost:8088/api/usuarios-roles/${usuarioSeleccionado}`);
      
      if (!rolesUsuarioRes.ok || rolesUsuarioRes.status === 204) {
        setMensaje({ tipo: 'error', texto: 'No se encontró la asignación de rol' });
        setProcesando(false);
        return;
      }

      const rolesUsuario = await rolesUsuarioRes.json();
      
      if (rolesUsuario.length === 0) {
        setMensaje({ tipo: 'error', texto: 'Este usuario no tiene rol asignado' });
        setProcesando(false);
        return;
      }

      const asignacionId = rolesUsuario[0].id;
      const response = await fetch(
        `http://localhost:8088/api/usuarios-roles/${asignacionId}`,
        { method: 'DELETE' }
      );

      if (response.ok) {
        setMensaje({
          tipo: 'success',
          texto: `Rol revocado correctamente de ${usuario.username}`
        });
        setRolSeleccionado(null);
        await cargarDatos();
      } else {
        throw new Error('Error al revocar rol');
      }
    } catch (error) {
      console.error('Error:', error);
      setMensaje({ tipo: 'error', texto: 'Error al revocar el rol. Intente nuevamente.' });
    } finally {
      setProcesando(false);
    }
  };

  const handleCancelar = () => {
    const usuario = usuarios.find(u => u.id === usuarioSeleccionado);
    if (usuario) {
      setRolSeleccionado((usuario as any).id_rol_actual);
    }
    setModoEdicion(false);
    setMensaje({ tipo: '', texto: '' });
  };

  if (loading) {
    return (
      <div className="layout-roles">
        <Br_administrativa onMinimizeChange={setMinimizado} />
        <main className={`contenido-roles ${minimizado ? "minimize" : ""}`}>
          <div className="header">
            <h1>Cargando datos...</h1>
          </div>
        </main>
      </div>
    );
  }

  return (
    <div className="layout-roles">
      <Br_administrativa onMinimizeChange={setMinimizado} />

      <main className={`contenido-roles ${minimizado ? "minimize" : ""}`}>
        <div className="header">
          <h1>Gestión de Roles de Usuario</h1>
          <p className="subtitle">Sistema de asignación de roles - Manada Woof</p>
        </div>

        {mensaje.texto && (
          <div className={`mensaje ${mensaje.tipo}`}>
            {mensaje.texto}
          </div>
        )}

        <div className="contenido-principal">
          <div className="panel-usuarios">
            <div className="panel-header">
              <h2>Usuarios del Sistema</h2>
              <input
                type="text"
                placeholder="Buscar usuario..."
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
                className="input-busqueda"
              />
            </div>

            <div className="lista-usuarios">
              {usuariosFiltrados.length === 0 ? (
                <p className="sin-resultados">No se encontraron usuarios</p>
              ) : (
                usuariosFiltrados.map(usuario => (
                  <div
                    key={usuario.id}
                    className={`usuario-item ${usuarioSeleccionado === usuario.id ? 'seleccionado' : ''}`}
                    onClick={() => handleSeleccionarUsuario(usuario.id)}
                  >
                    <div className="usuario-info">
                      <div className="usuario-nombre">{usuario.username}</div>
                      <div className="usuario-rol">
                        {(usuario as any).rol_actual ? (
                          <span className="badge-rol">{(usuario as any).rol_actual}</span>
                        ) : (
                          <span className="badge-sin-rol">Sin rol asignado</span>
                        )}
                      </div>
                    </div>
                    <div className={`usuario-estado ${usuario.activo ? 'activo' : 'inactivo'}`}>
                      {usuario.activo ? '●' : '○'}
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="panel-asignacion">
            {usuarioSeleccionado ? (
              <>
                <div className="panel-header">
                  <h2>Asignar Rol</h2>
                  {usuarios.find(u => u.id === usuarioSeleccionado && (u as any).rol_actual) && (
                    <button className="btn-editar" onClick={() => setModoEdicion(!modoEdicion)}>
                      {modoEdicion ? 'Cancelar edición' : 'Modificar rol'}
                    </button>
                  )}
                </div>

                <div className="detalle-usuario">
                  <label>Usuario seleccionado:</label>
                  <div className="usuario-seleccionado">
                    {usuarios.find(u => u.id === usuarioSeleccionado)?.username}
                  </div>
                </div>

                <div className="seccion-roles">
                  <label>Seleccionar rol:</label>
                  <div className="roles-grid">
                    {roles.map(rol => {
                      const usuario = usuarios.find(u => u.id === usuarioSeleccionado);
                      const isDisabled = !modoEdicion && (usuario as any)?.rol_actual !== null;
                      
                      return (
                        <div
                          key={rol.id}
                          className={`rol-card ${rolSeleccionado === rol.id ? 'seleccionado' : ''} ${isDisabled ? 'disabled' : ''}`}
                          onClick={() => {
                            if (!isDisabled) {
                              setRolSeleccionado(rol.id);
                            }
                          }}
                        >
                          <div className="rol-nombre">{rol.nombre}</div>
                          {rol.descripcion && (
                            <div className="rol-descripcion">{rol.descripcion}</div>
                          )}
                          {rolSeleccionado === rol.id && (
                            <div className="check-icon">✓</div>
                          )}
                        </div>
                      );
                    })}
                  </div>
                </div>

                <div className="acciones">
                  {usuarios.find(u => u.id === usuarioSeleccionado && (u as any).rol_actual) ? (
                    modoEdicion ? (
                      <>
                        <button 
                          className="btn-cancelar" 
                          onClick={handleCancelar}
                          disabled={procesando}
                        >
                          Cancelar
                        </button>
                        <button 
                          className="btn-guardar" 
                          onClick={handleAsignarRol}
                          disabled={procesando}
                        >
                          {procesando ? 'Guardando...' : 'Guardar Cambios'}
                        </button>
                      </>
                    ) : (
                      <button 
                        className="btn-revocar" 
                        onClick={handleRevocarRol}
                        disabled={procesando}
                      >
                        {procesando ? 'Revocando...' : 'Revocar Rol'}
                      </button>
                    )
                  ) : (
                    <>
                      <button 
                        className="btn-cancelar" 
                        onClick={() => setUsuarioSeleccionado(null)}
                        disabled={procesando}
                      >
                        Cancelar
                      </button>
                      <button 
                        className="btn-asignar" 
                        onClick={handleAsignarRol}
                        disabled={procesando}
                      >
                        {procesando ? 'Asignando...' : 'Asignar Rol'}
                      </button>
                    </>
                  )}
                </div>

                <div className="nota-info">
                  <strong>Nota:</strong> Cada usuario solo puede tener UN rol asignado. 
                  Si modifica el rol, el anterior será reemplazado automáticamente.
                </div>
              </>
            ) : (
              <div className="sin-seleccion">
                <p>Seleccione un usuario de la lista para asignarle un rol</p>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
};

export default AsignarRolesPermisos;