import React, { useState } from 'react';
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import './RegistrarSalidaMascota.css';

interface Mascota {
  id: number;
  nombre: string;
  especie: string;
  raza: string;
  propietario: string;
  estado: 'En servicio' | 'Esperando';
}

interface MascotaSalida extends Mascota {
  fecha_salida: string;
  hora_salida: string;
  observaciones: string;
  estado_salida: string;
}

const RegistrarSalidaMascota: React.FC = () => {
  const [mascotasPendientes, setMascotasPendientes] = useState<Mascota[]>([
    { id: 1, nombre: 'Max', especie: 'Perro', raza: 'Golden Retriever', propietario: 'Juan Pérez', estado: 'En servicio' },
    { id: 2, nombre: 'Luna', especie: 'Gato', raza: 'Persa', propietario: 'María García', estado: 'En servicio' },
    { id: 3, nombre: 'Rocky', especie: 'Perro', raza: 'Bulldog', propietario: 'Carlos López', estado: 'En servicio' },
    { id: 4, nombre: 'Milo', especie: 'Perro', raza: 'Beagle', propietario: 'Ana Torres', estado: 'Esperando' },
    { id: 5, nombre: 'Bella', especie: 'Gato', raza: 'Siamés', propietario: 'Luis Ramírez', estado: 'Esperando' },
  ]);

  const [mascotasCompletadas, setMascotasCompletadas] = useState<MascotaSalida[]>([]);
  
  const [vistaActual, setVistaActual] = useState<'pendientes' | 'completadas'>('pendientes');
  const [mascotaSeleccionada, setMascotaSeleccionada] = useState<number | null>(null);
  const [busqueda, setBusqueda] = useState('');
  const [observaciones, setObservaciones] = useState('');
  const [estadoSalida, setEstadoSalida] = useState('Normal');
  const [mensaje, setMensaje] = useState<{ tipo: 'success' | 'error' | 'warning' | '', texto: string }>({ tipo: '', texto: '' });
  const [minimizado, setMinimizado] = useState(false);

  const mascotasFiltradas = mascotasPendientes.filter(m =>
    m.nombre.toLowerCase().includes(busqueda.toLowerCase()) ||
    m.propietario.toLowerCase().includes(busqueda.toLowerCase())
  );

  const completadasFiltradas = mascotasCompletadas.filter(m =>
    m.nombre.toLowerCase().includes(busqueda.toLowerCase()) ||
    m.propietario.toLowerCase().includes(busqueda.toLowerCase())
  );

  const handleSeleccionarMascota = (mascotaId: number) => {
    if (vistaActual === 'completadas') return;
    
    setMascotaSeleccionada(mascotaId);
    setObservaciones('');
    setEstadoSalida('Normal');
    setMensaje({ tipo: '', texto: '' });
  };

  const handleRegistrarSalida = () => {
    if (!mascotaSeleccionada) {
      setMensaje({ tipo: 'error', texto: 'Debe seleccionar una mascota' });
      return;
    }

    if (observaciones.trim().length < 10) {
      setMensaje({ tipo: 'warning', texto: 'Las observaciones deben tener al menos 10 caracteres' });
      return;
    }

    const mascota = mascotasPendientes.find(m => m.id === mascotaSeleccionada);
    if (!mascota) return;

    const ahora = new Date();
    const mascotaCompletada: MascotaSalida = {
      ...mascota,
      fecha_salida: ahora.toLocaleDateString('es-PE'),
      hora_salida: ahora.toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' }),
      observaciones: observaciones,
      estado_salida: estadoSalida
    };

    setMascotasCompletadas(prev => [mascotaCompletada, ...prev]);
    setMascotasPendientes(prev => prev.filter(m => m.id !== mascotaSeleccionada));

    setMensaje({
      tipo: 'success',
      texto: `✓ Salida registrada: ${mascota.nombre} - ${mascotaCompletada.hora_salida}`
    });

    setTimeout(() => {
      setMascotaSeleccionada(null);
      setObservaciones('');
      setEstadoSalida('Normal');
      setMensaje({ tipo: '', texto: '' });
    }, 2000);
  };

  const handleCancelar = () => {
    setMascotaSeleccionada(null);
    setObservaciones('');
    setEstadoSalida('Normal');
    setMensaje({ tipo: '', texto: '' });
  };

  const handleCambiarVista = (vista: 'pendientes' | 'completadas') => {
    setVistaActual(vista);
    setMascotaSeleccionada(null);
    setBusqueda('');
    setMensaje({ tipo: '', texto: '' });
  };

  return (
    <div className="layout-salida">
      <Br_administrativa onMinimizeChange={setMinimizado} />

      <main className={`contenido-salida ${minimizado ? "minimize" : ""}`}>
        <div className="registrar-salida-mascota">
          <div className="header">
            <h1>Registrar Salida de Mascota</h1>
            <p className="subtitle">Control de salidas y observaciones - Manada Woof</p>
          </div>

          {mensaje.texto && (
            <div className={`mensaje ${mensaje.tipo}`}>
              {mensaje.texto}
            </div>
          )}

          <div className="contenido-principal">
            <div className="panel-mascotas">
              <div className="tabs-container">
                <button
                  className={`tab ${vistaActual === 'pendientes' ? 'active' : ''}`}
                  onClick={() => handleCambiarVista('pendientes')}
                >
                  Pendientes
                  <span className="badge-count">{mascotasPendientes.length}</span>
                </button>
                <button
                  className={`tab ${vistaActual === 'completadas' ? 'active' : ''}`}
                  onClick={() => handleCambiarVista('completadas')}
                >
                  Completadas
                  <span className="badge-count">{mascotasCompletadas.length}</span>
                </button>
              </div>

              <div className="panel-header">
                <input
                  type="text"
                  placeholder="Buscar mascota o propietario..."
                  value={busqueda}
                  onChange={(e) => setBusqueda(e.target.value)}
                  className="input-busqueda"
                />
              </div>

              <div className="lista-mascotas">
                {vistaActual === 'pendientes' ? (
                  mascotasFiltradas.length === 0 ? (
                    <p className="sin-resultados">
                      {busqueda ? 'No se encontraron mascotas' : 'No hay mascotas pendientes'}
                    </p>
                  ) : (
                    mascotasFiltradas.map(mascota => (
                      <div
                        key={mascota.id}
                        className={`mascota-item ${mascotaSeleccionada === mascota.id ? 'seleccionado' : ''}`}
                        onClick={() => handleSeleccionarMascota(mascota.id)}
                      >
                        <div className="mascota-avatar">
                          {mascota.nombre.charAt(0).toUpperCase()}
                        </div>
                        <div className="mascota-info">
                          <div className="mascota-nombre">{mascota.nombre}</div>
                          <div className="mascota-detalles">
                            {mascota.especie} • {mascota.raza}
                          </div>
                          <div className="mascota-propietario">
                            Dueño: {mascota.propietario}
                          </div>
                        </div>
                        <div className={`mascota-estado ${mascota.estado.toLowerCase().replace(' ', '-')}`}>
                          {mascota.estado}
                        </div>
                      </div>
                    ))
                  )
                ) : (
                  completadasFiltradas.length === 0 ? (
                    <p className="sin-resultados">
                      {busqueda ? 'No se encontraron mascotas' : 'No hay salidas registradas'}
                    </p>
                  ) : (
                    completadasFiltradas.map(mascota => (
                      <div key={mascota.id} className="mascota-item completada">
                        <div className="mascota-avatar completada">
                          {mascota.nombre.charAt(0).toUpperCase()}
                        </div>
                        <div className="mascota-info">
                          <div className="mascota-nombre">{mascota.nombre}</div>
                          <div className="mascota-detalles">
                            {mascota.especie} • {mascota.raza}
                          </div>
                          <div className="mascota-propietario">
                            Salida: {mascota.hora_salida}
                          </div>
                        </div>
                        <div className="icono-completado">✓</div>
                      </div>
                    ))
                  )
                )}
              </div>
            </div>

            <div className="panel-registro">
              {vistaActual === 'completadas' ? (
                <div className="vista-historial">
                  {mascotaSeleccionada === null && completadasFiltradas.length > 0 ? (
                    <div className="sin-seleccion">
                      <div className="icono-seleccion">📋</div>
                      <p>Historial de salidas registradas</p>
                      <div className="estadisticas">
                        <div className="stat-item">
                          <span className="stat-numero">{mascotasCompletadas.length}</span>
                          <span className="stat-label">Total completadas</span>
                        </div>
                      </div>
                    </div>
                  ) : completadasFiltradas.length === 0 ? (
                    <div className="sin-seleccion">
                      <div className="icono-seleccion">📋</div>
                      <p>Aún no hay salidas registradas hoy</p>
                    </div>
                  ) : null}
                </div>
              ) : mascotaSeleccionada ? (
                <>
                  <div className="panel-header">
                    <h2>Registrar Salida</h2>
                  </div>

                  <div className="detalle-mascota">
                    <label>Mascota seleccionada:</label>
                    <div className="mascota-seleccionada-card">
                      <div className="mascota-avatar-grande">
                        {mascotasPendientes.find(m => m.id === mascotaSeleccionada)?.nombre.charAt(0).toUpperCase()}
                      </div>
                      <div>
                        <div className="nombre-grande">
                          {mascotasPendientes.find(m => m.id === mascotaSeleccionada)?.nombre}
                        </div>
                        <div className="detalles-secundarios">
                          {mascotasPendientes.find(m => m.id === mascotaSeleccionada)?.especie} • {' '}
                          {mascotasPendientes.find(m => m.id === mascotaSeleccionada)?.raza}
                        </div>
                      </div>
                    </div>
                  </div>

                  <div className="form-group">
                    <label htmlFor="estado">Estado de salida:</label>
                    <select
                      id="estado"
                      value={estadoSalida}
                      onChange={(e) => setEstadoSalida(e.target.value)}
                      className="select-estado"
                    >
                      <option value="Normal">Normal</option>
                      <option value="Tranquilo">Tranquilo</option>
                      <option value="Inquieto">Inquieto</option>
                      <option value="Cansado">Cansado</option>
                      <option value="Estresado">Estresado</option>
                      <option value="Requiere atención">Requiere atención</option>
                    </select>
                  </div>

                  <div className="form-group">
                    <label htmlFor="observaciones">
                      Observaciones: <span className="requerido">*</span>
                    </label>
                    <textarea
                      id="observaciones"
                      value={observaciones}
                      onChange={(e) => setObservaciones(e.target.value)}
                      placeholder="Describa el comportamiento de la mascota, incidentes durante el servicio, alimentación, necesidades especiales, etc."
                      className="textarea-observaciones"
                      rows={6}
                    />
                    <div className="contador-caracteres">
                      {observaciones.length} caracteres {observaciones.length < 10 && '(mínimo 10)'}
                    </div>
                  </div>

                  <div className="info-fecha">
                    <div className="info-item">
                      <span className="label">Fecha:</span>
                      <span className="valor">{new Date().toLocaleDateString('es-PE')}</span>
                    </div>
                    <div className="info-item">
                      <span className="label">Hora:</span>
                      <span className="valor">{new Date().toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' })}</span>
                    </div>
                  </div>

                  <div className="acciones">
                    <button className="btn-cancelar" onClick={handleCancelar}>
                      Cancelar
                    </button>
                    <button className="btn-registrar" onClick={handleRegistrarSalida}>
                      Registrar Salida
                    </button>
                  </div>

                  <div className="nota-info">
                    <strong>Nota:</strong> Las observaciones quedarán registradas en el historial de la mascota 
                    y serán visibles para el propietario.
                  </div>
                </>
              ) : (
                <div className="sin-seleccion">
                  <div className="icono-seleccion">📋</div>
                  <p>Seleccione una mascota de la lista para registrar su salida</p>
                </div>
              )}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default RegistrarSalidaMascota;