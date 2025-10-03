import React, { useState } from 'react';
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa"; // <-- agregado
import './AsignarRolesPermisos.css';

interface Usuario {
  id: number;
  username: string;
  rol_actual: string | null;
  id_rol_actual: number | null;
  activo: boolean;
}

interface Rol {
  id: number;
  nombre: string;
  descripcion: string | null;
}

const AsignarRolesPermisos: React.FC = () => {
  const [usuarios, setUsuarios] = useState<Usuario[]>([
    { id: 1, username: 'admin_woof', rol_actual: 'ADMINISTRADOR GENERAL', id_rol_actual: 1, activo: true },
    { id: 2, username: 'admin_g2', rol_actual: 'ADMINISTRADOR G 2', id_rol_actual: 2, activo: true },
    { id: 3, username: 'caja_milo', rol_actual: 'AUXILIAR CAJA', id_rol_actual: 3, activo: true },
    { id: 4, username: 'gromer_luna', rol_actual: 'AUXILIAR GROMERS', id_rol_actual: 4, activo: true },
    { id: 5, username: 'nuevo_usuario', rol_actual: null, id_rol_actual: null, activo: true }
  ]);

  const [roles] = useState<Rol[]>([
    { id: 1, nombre: 'ADMINISTRADOR GENERAL', descripcion: 'Acceso total al sistema' },
    { id: 2, nombre: 'ADMINISTRADOR G 2', descripcion: 'Gestión operativa y administrativa' },
    { id: 3, nombre: 'AUXILIAR CAJA', descripcion: 'Operaciones de facturación y pagos' },
    { id: 4, nombre: 'AUXILIAR GROMERS', descripcion: 'Gestión de servicios de grooming' }
  ]);

  const [usuarioSeleccionado, setUsuarioSeleccionado] = useState<number | null>(null);
  const [rolSeleccionado, setRolSeleccionado] = useState<number | null>(null);
  const [busqueda, setBusqueda] = useState('');
  const [modoEdicion, setModoEdicion] = useState(false);
  const [mensaje, setMensaje] = useState<{ tipo: 'success' | 'error' | 'warning' | '', texto: string }>({ tipo: '', texto: '' });

  // <-- agregado: estado para manejar el minimizar del menú lateral
  const [minimizado, setMinimizado] = useState(false);

  const usuariosFiltrados = usuarios.filter(u =>
    u.username.toLowerCase().includes(busqueda.toLowerCase())
  );

  const handleSeleccionarUsuario = (usuarioId: number) => {
    const usuario = usuarios.find(u => u.id === usuarioId);
    if (usuario) {
      setUsuarioSeleccionado(usuarioId);
      setRolSeleccionado(usuario.id_rol_actual);
      setModoEdicion(false);
      setMensaje({ tipo: '', texto: '' });
    }
  };

  const handleAsignarRol = () => {
    if (!usuarioSeleccionado || !rolSeleccionado) {
      setMensaje({ tipo: 'error', texto: 'Debe seleccionar un usuario y un rol' });
      return;
    }

    const usuario = usuarios.find(u => u.id === usuarioSeleccionado);
    const rol = roles.find(r => r.id === rolSeleccionado);

    if (usuario && rol) {
      if (usuario.id_rol_actual === rolSeleccionado) {
        setMensaje({ tipo: 'warning', texto: `El usuario ya tiene asignado el rol: ${rol.nombre}` });
        return;
      }

      setUsuarios(prevUsuarios =>
        prevUsuarios.map(u =>
          u.id === usuarioSeleccionado
            ? { ...u, rol_actual: rol.nombre, id_rol_actual: rol.id }
            : u
        )
      );

      setMensaje({
        tipo: 'success',
        texto: `Rol "${rol.nombre}" asignado correctamente a ${usuario.username}`
      });

      setModoEdicion(false);
    }
  };

  const handleRevocarRol = () => {
    if (!usuarioSeleccionado) return;

    const usuario = usuarios.find(u => u.id === usuarioSeleccionado);
    if (!usuario || !usuario.rol_actual) {
      setMensaje({ tipo: 'error', texto: 'El usuario no tiene un rol asignado' });
      return;
    }

    setUsuarios(prevUsuarios =>
      prevUsuarios.map(u =>
        u.id === usuarioSeleccionado
          ? { ...u, rol_actual: null, id_rol_actual: null }
          : u
      )
    );

    setMensaje({
      tipo: 'success',
      texto: `Rol revocado correctamente de ${usuario.username}`
    });

    setRolSeleccionado(null);
  };

  const handleCancelar = () => {
    const usuario = usuarios.find(u => u.id === usuarioSeleccionado);
    if (usuario) {
      setRolSeleccionado(usuario.id_rol_actual);
    }
    setModoEdicion(false);
    setMensaje({ tipo: '', texto: '' });
  };

  return (
  <div className="layout-roles">
    {/* Barra lateral fija a la izquierda */}
    <Br_administrativa onMinimizeChange={setMinimizado} />

    {/* Contenido principal a la derecha */}
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
        {/* --- Tu código actual (panel usuarios + panel asignación) --- */}
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
                      {usuario.rol_actual ? (
                        <span className="badge-rol">{usuario.rol_actual}</span>
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
                {usuarios.find(u => u.id === usuarioSeleccionado)?.rol_actual && (
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
                    const isDisabled = !modoEdicion && usuario?.rol_actual !== null;
                    
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
                {usuarios.find(u => u.id === usuarioSeleccionado)?.rol_actual ? (
                  modoEdicion ? (
                    <>
                      <button className="btn-cancelar" onClick={handleCancelar}>
                        Cancelar
                      </button>
                      <button className="btn-guardar" onClick={handleAsignarRol}>
                        Guardar Cambios
                      </button>
                    </>
                  ) : (
                    <button className="btn-revocar" onClick={handleRevocarRol}>
                      Revocar Rol
                    </button>
                  )
                ) : (
                  <>
                    <button className="btn-cancelar" onClick={() => setUsuarioSeleccionado(null)}>
                      Cancelar
                    </button>
                    <button className="btn-asignar" onClick={handleAsignarRol}>
                      Asignar Rol
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
