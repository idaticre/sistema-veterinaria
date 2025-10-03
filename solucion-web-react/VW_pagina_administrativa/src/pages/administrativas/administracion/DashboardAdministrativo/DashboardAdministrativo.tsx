import React, { useState } from 'react';
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import './DashboardAdministrativo.css';

interface Colaborador {
  id: number;
  nombre: string;
  rol: string;
  servicios_completados: number;
  servicios_pendientes: number;
  asistencia_porcentaje: number;
  calificacion_promedio: number;
  activo: boolean;
}

interface IndicadorGeneral {
  titulo: string;
  valor: number;
  unidad: string;
  tendencia: 'subida' | 'bajada' | 'neutral';
  porcentaje_cambio: number;
  icono: string;
}

const DashboardAdministrativo: React.FC = () => {
  const [periodo, setPeriodo] = useState<'dia' | 'semana' | 'mes'>('mes');
  const [minimizado, setMinimizado] = useState(false);
  
  const [indicadoresGenerales] = useState<IndicadorGeneral[]>([
    {
      titulo: 'Total Servicios',
      valor: 247,
      unidad: 'servicios',
      tendencia: 'subida',
      porcentaje_cambio: 12.5,
      icono: '📊'
    },
    {
      titulo: 'Asistencia Personal',
      valor: 94,
      unidad: '%',
      tendencia: 'subida',
      porcentaje_cambio: 3.2,
      icono: '✅'
    },
    {
      titulo: 'Servicios Pendientes',
      valor: 18,
      unidad: 'pendientes',
      tendencia: 'bajada',
      porcentaje_cambio: -5.8,
      icono: '⏳'
    },
    {
      titulo: 'Satisfacción Cliente',
      valor: 4.7,
      unidad: '/ 5.0',
      tendencia: 'subida',
      porcentaje_cambio: 2.1,
      icono: '⭐'
    }
  ]);

  const [colaboradores] = useState<Colaborador[]>([
    {
      id: 1,
      nombre: 'María García',
      rol: 'Veterinaria',
      servicios_completados: 45,
      servicios_pendientes: 3,
      asistencia_porcentaje: 98,
      calificacion_promedio: 4.8,
      activo: true
    },
    {
      id: 2,
      nombre: 'Juan Pérez',
      rol: 'Groomer',
      servicios_completados: 52,
      servicios_pendientes: 5,
      asistencia_porcentaje: 95,
      calificacion_promedio: 4.6,
      activo: true
    },
    {
      id: 3,
      nombre: 'Ana Torres',
      rol: 'Auxiliar Caja',
      servicios_completados: 78,
      servicios_pendientes: 2,
      asistencia_porcentaje: 92,
      calificacion_promedio: 4.9,
      activo: true
    },
    {
      id: 4,
      nombre: 'Carlos López',
      rol: 'Groomer',
      servicios_completados: 38,
      servicios_pendientes: 4,
      asistencia_porcentaje: 88,
      calificacion_promedio: 4.5,
      activo: true
    },
    {
      id: 5,
      nombre: 'Luis Ramírez',
      rol: 'Veterinario',
      servicios_completados: 34,
      servicios_pendientes: 4,
      asistencia_porcentaje: 91,
      calificacion_promedio: 4.7,
      activo: false
    }
  ]);

  const colaboradoresActivos = colaboradores.filter(c => c.activo);
  const colaboradoresInactivos = colaboradores.filter(c => !c.activo);

  const getColorAsistencia = (porcentaje: number): string => {
    if (porcentaje >= 95) return 'excelente';
    if (porcentaje >= 85) return 'bueno';
    if (porcentaje >= 75) return 'regular';
    return 'bajo';
  };

  const getColorCalificacion = (calificacion: number): string => {
    if (calificacion >= 4.5) return 'excelente';
    if (calificacion >= 4.0) return 'bueno';
    if (calificacion >= 3.5) return 'regular';
    return 'bajo';
  };

  return (
    <div className="layout-dashboard">
      <Br_administrativa onMinimizeChange={setMinimizado} />

      <main className={`contenido-dashboard ${minimizado ? "minimize" : ""}`}>
        <div className="dashboard-administrativo">
          <div className="header">
            <div className="header-content">
              <h1>Dashboard Administrativo</h1>
              <p className="subtitle">Panel de control y seguimiento del personal - Manada Woof</p>
            </div>
            <div className="periodo-selector">
              <button
                className={`btn-periodo ${periodo === 'dia' ? 'active' : ''}`}
                onClick={() => setPeriodo('dia')}
              >
                Hoy
              </button>
              <button
                className={`btn-periodo ${periodo === 'semana' ? 'active' : ''}`}
                onClick={() => setPeriodo('semana')}
              >
                Semana
              </button>
              <button
                className={`btn-periodo ${periodo === 'mes' ? 'active' : ''}`}
                onClick={() => setPeriodo('mes')}
              >
                Mes
              </button>
            </div>
          </div>

          <div className="indicadores-grid">
            {indicadoresGenerales.map((indicador, index) => (
              <div key={index} className="indicador-card">
                <div className="indicador-icono">{indicador.icono}</div>
                <div className="indicador-contenido">
                  <div className="indicador-titulo">{indicador.titulo}</div>
                  <div className="indicador-valor">
                    {indicador.valor}
                    <span className="indicador-unidad">{indicador.unidad}</span>
                  </div>
                  <div className={`indicador-tendencia ${indicador.tendencia}`}>
                    {indicador.tendencia === 'subida' && '↗'}
                    {indicador.tendencia === 'bajada' && '↘'}
                    {indicador.tendencia === 'neutral' && '→'}
                    <span>{Math.abs(indicador.porcentaje_cambio)}%</span>
                  </div>
                </div>
              </div>
            ))}
          </div>

          <div className="seccion-colaboradores">
            <div className="seccion-header">
              <h2>Desempeño del Personal</h2>
              <div className="leyenda">
                <span className="leyenda-item">
                  <span className="punto activo"></span> Activo
                </span>
                <span className="leyenda-item">
                  <span className="punto inactivo"></span> Inactivo
                </span>
              </div>
            </div>

            <div className="colaboradores-lista">
              {colaboradoresActivos.map(colaborador => (
                <div key={colaborador.id} className="colaborador-card">
                  <div className="colaborador-header">
                    <div className="colaborador-avatar">
                      {colaborador.nombre.split(' ').map(n => n[0]).join('')}
                    </div>
                    <div className="colaborador-info-basica">
                      <div className="colaborador-nombre">{colaborador.nombre}</div>
                      <div className="colaborador-rol">{colaborador.rol}</div>
                    </div>
                    <div className={`colaborador-estado ${colaborador.activo ? 'activo' : 'inactivo'}`}>
                      {colaborador.activo ? 'Activo' : 'Inactivo'}
                    </div>
                  </div>

                  <div className="colaborador-metricas">
                    <div className="metrica-item">
                      <div className="metrica-label">Servicios Completados</div>
                      <div className="metrica-valor destacado">{colaborador.servicios_completados}</div>
                    </div>

                    <div className="metrica-item">
                      <div className="metrica-label">Pendientes</div>
                      <div className="metrica-valor">{colaborador.servicios_pendientes}</div>
                    </div>

                    <div className="metrica-item">
                      <div className="metrica-label">Asistencia</div>
                      <div className={`metrica-valor-porcentaje ${getColorAsistencia(colaborador.asistencia_porcentaje)}`}>
                        {colaborador.asistencia_porcentaje}%
                      </div>
                      <div className="barra-progreso">
                        <div
                          className={`barra-fill ${getColorAsistencia(colaborador.asistencia_porcentaje)}`}
                          style={{ width: `${colaborador.asistencia_porcentaje}%` }}
                        ></div>
                      </div>
                    </div>

                    <div className="metrica-item">
                      <div className="metrica-label">Calificación</div>
                      <div className={`metrica-calificacion ${getColorCalificacion(colaborador.calificacion_promedio)}`}>
                        ⭐ {colaborador.calificacion_promedio.toFixed(1)}
                      </div>
                    </div>
                  </div>
                </div>
              ))}

              {colaboradoresInactivos.length > 0 && (
                <>
                  <div className="separador-inactivos">
                    <span>Personal Inactivo</span>
                  </div>
                  {colaboradoresInactivos.map(colaborador => (
                    <div key={colaborador.id} className="colaborador-card inactivo">
                      <div className="colaborador-header">
                        <div className="colaborador-avatar">
                          {colaborador.nombre.split(' ').map(n => n[0]).join('')}
                        </div>
                        <div className="colaborador-info-basica">
                          <div className="colaborador-nombre">{colaborador.nombre}</div>
                          <div className="colaborador-rol">{colaborador.rol}</div>
                        </div>
                        <div className="colaborador-estado inactivo">Inactivo</div>
                      </div>

                      <div className="colaborador-metricas">
                        <div className="metrica-item">
                          <div className="metrica-label">Servicios Completados</div>
                          <div className="metrica-valor destacado">{colaborador.servicios_completados}</div>
                        </div>

                        <div className="metrica-item">
                          <div className="metrica-label">Asistencia</div>
                          <div className={`metrica-valor-porcentaje ${getColorAsistencia(colaborador.asistencia_porcentaje)}`}>
                            {colaborador.asistencia_porcentaje}%
                          </div>
                        </div>

                        <div className="metrica-item">
                          <div className="metrica-label">Calificación</div>
                          <div className={`metrica-calificacion ${getColorCalificacion(colaborador.calificacion_promedio)}`}>
                            ⭐ {colaborador.calificacion_promedio.toFixed(1)}
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </>
              )}
            </div>
          </div>

          <div className="resumen-footer">
            <div className="resumen-item">
              <span className="resumen-label">Total Personal:</span>
              <span className="resumen-valor">{colaboradores.length}</span>
            </div>
            <div className="resumen-item">
              <span className="resumen-label">Activos:</span>
              <span className="resumen-valor activo">{colaboradoresActivos.length}</span>
            </div>
            <div className="resumen-item">
              <span className="resumen-label">Inactivos:</span>
              <span className="resumen-valor inactivo">{colaboradoresInactivos.length}</span>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default DashboardAdministrativo;