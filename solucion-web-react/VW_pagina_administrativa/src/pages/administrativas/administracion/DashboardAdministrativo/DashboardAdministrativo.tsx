import React, { useState, useEffect } from "react";
import "./DashboardAdministrativo.css";
import IST from "../../../../components/proteccion/IST";
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";

interface Colaborador {
  id: number;
  codigoColaborador: string;
  nombre: string;
  sexo: string;
  documento: string;
  correo: string;
  telefono: string;
  direccion: string;
  ciudad: string;
  distrito: string;
  activo: boolean;
  fechaIngreso: string;
  foto: string;
}

const DashboardAdministrativo: React.FC = () => {
  const [colaboradores, setColaboradores] = useState<Colaborador[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");

  //  AGREGADO: ESTO ES NECESARIO PARA QUE SE EXPANDA
  const [minimizado, setMinimizado] = useState(false);

  const baseURL = "http://localhost:8088/api";

  useEffect(() => {
    fetchColaboradores();
  }, []);

  const fetchColaboradores = async () => {
    try {
      setLoading(true);

      const res = await IST.get(`${baseURL}/colaboradores`);
      const data = res.data;

      if (data.success) {
        setColaboradores(data.data);
      } else {
        setError("No se pudieron cargar los colaboradores");
      }
    } catch (err) {
      console.error("Error:", err);
      setError("Error al conectar con el servidor");
    } finally {
      setLoading(false);
    }
  };

  const calcularDiasActivo = (fechaIngreso: string): number => {
    const inicio = new Date(fechaIngreso);
    const hoy = new Date();
    const diferencia = hoy.getTime() - inicio.getTime();
    return Math.floor(diferencia / (1000 * 60 * 60 * 24));
  };

  const formatearFecha = (fecha: string): string => {
    const date = new Date(fecha);
    return date.toLocaleDateString("es-PE", {
      day: "2-digit",
      month: "long",
      year: "numeric",
    });
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Cargando colaboradores...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container">
        <p className="error-message">{error}</p>
        <button onClick={fetchColaboradores} className="retry-button">
          Reintentar
        </button>
      </div>
    );
  }

  const colaboradoresActivos = colaboradores.filter((c) => c.activo).length;
  const colaboradoresInactivos =
    colaboradores.length - colaboradoresActivos;

  return (
    <div className="dashboard-page">

      {/*  AGREGADO: AHORA EL SIDEBAR PUEDE ENVIAR CAMBIO */}
      <Br_administrativa onMinimizeChange={setMinimizado} />

      {/*  AGREGADO: ACTIVAR CLASE minimize */}
      <div className={`dashboard-container ${minimizado ? "minimize" : ""}`}>
        <header className="dashboard-header">
          <h1>Dashboard Administrativo</h1>
          <p>Gestión de Colaboradores</p>
        </header>

        <div className="stats-summary">
          <div className="stat-card total">
            <div className="stat-icon">👥</div>
            <div className="stat-content">
              <h3>Total Colaboradores</h3>
              <p className="stat-number">{colaboradores.length}</p>
            </div>
          </div>

          <div className="stat-card active">
            <div className="stat-icon">✅</div>
            <div className="stat-content">
              <h3>Activos</h3>
              <p className="stat-number">{colaboradoresActivos}</p>
            </div>
          </div>

          <div className="stat-card inactive">
            <div className="stat-icon">❌</div>
            <div className="stat-content">
              <h3>Inactivos</h3>
              <p className="stat-number">{colaboradoresInactivos}</p>
            </div>
          </div>
        </div>

        <div className="colaboradores-section">
          <h2>Listado de Colaboradores</h2>

          {colaboradores.length === 0 ? (
            <div className="no-data">
              <p>No hay colaboradores registrados</p>
            </div>
          ) : (
            <div className="colaboradores-grid">
              {colaboradores.map((colaborador) => {
                const diasActivo = calcularDiasActivo(colaborador.fechaIngreso);
                const mesesActivo = Math.floor(diasActivo / 30);

                return (
                  <div key={colaborador.id} className="colaborador-card">
                    <div className="card-header">
                      <div className="colaborador-avatar">
                        {colaborador.nombre.charAt(0).toUpperCase()}
                      </div>
                      <div className="colaborador-title">
                        <h3>{colaborador.nombre}</h3>
                        <span className="codigo">
                          {colaborador.codigoColaborador}
                        </span>
                      </div>
                      <span
                        className={`badge ${
                          colaborador.activo ? "activo" : "inactivo"
                        }`}
                      >
                        {colaborador.activo ? "● Activo" : "● Inactivo"}
                      </span>
                    </div>

                    <div className="card-body">
                      <div className="info-row">
                        <span className="label">📧 Correo:</span>
                        <span className="value">{colaborador.correo}</span>
                      </div>
                      <div className="info-row">
                        <span className="label">📱 Teléfono:</span>
                        <span className="value">{colaborador.telefono}</span>
                      </div>
                      <div className="info-row">
                        <span className="label">📄 Documento:</span>
                        <span className="value">{colaborador.documento}</span>
                      </div>
                      <div className="info-row">
                        <span className="label">📍 Ubicación:</span>
                        <span className="value">
                          {colaborador.distrito}, {colaborador.ciudad}
                        </span>
                      </div>
                    </div>

                    <div className="card-footer">
                      <div className="rendimiento-info">
                        <div className="rendimiento-item">
                          <span className="rendimiento-label">Ingreso</span>
                          <span className="rendimiento-value">
                            {formatearFecha(colaborador.fechaIngreso)}
                          </span>
                        </div>
                        <div className="rendimiento-item">
                          <span className="rendimiento-label">Antigüedad</span>
                          <span className="rendimiento-value highlight">
                            {mesesActivo > 0
                              ? `${mesesActivo} meses`
                              : `${diasActivo} días`}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        <footer className="dashboard-footer">
          <p>Última actualización: {new Date().toLocaleString("es-PE")}</p>
        </footer>
      </div>
    </div>
  );
};

export default DashboardAdministrativo;
