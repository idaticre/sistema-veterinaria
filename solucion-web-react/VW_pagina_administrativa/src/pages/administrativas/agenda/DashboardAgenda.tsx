import { useState } from "react";
import Br_administrativa from "../../../components/barra_administrativa/Br_administrativa";
import {
  PieChart,
  Pie,
  Cell,
  Legend,
  ResponsiveContainer,
  Tooltip,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
} from "recharts";
import "./DashboardAgenda.css";

export default function DashboardAgenda() {
  const [minimizado, setMinimizado] = useState(false);

  // ===========================
  // 🎨 COLORES GENERALES
  // ===========================
  const COLORS = ["#ff9900", "#b34d16", "#9b7200", "#4d7cff", "#d9534f"];

  // ============================================================
  // 🔥 MOCK DATA basado en tus tablas reales
  // ============================================================

  // Estado de citas (agenda.id_estado → estado_agenda.nombre)
  const citasPorEstado = [
    { name: "Pendientes", value: 25 },
    { name: "Confirmadas", value: 40 },
    { name: "Canceladas", value: 10 },
    { name: "Reprogramadas", value: 8 },
    { name: "Atendidas", value: 17 },
  ];

  // Medio de solicitud (agenda.id_medio_solicitud → medio_solicitud.nombre)
  const mediosSolicitud = [
    { name: "WhatsApp", value: 45 },
    { name: "Teléfono", value: 20 },
    { name: "Web", value: 12 },
    { name: "Presencial", value: 30 },
    { name: "Redes Sociales", value: 15 },
  ];

  // Servicios más usados (ingresos_servicios.id_servicio)
  const serviciosUsados = [
    { name: "Baño y Corte", value: 35 },
    { name: "Vacunación", value: 50 },
    { name: "Desparasitación", value: 20 },
    { name: "Guardería", value: 10 },
    { name: "Consulta Médica", value: 45 },
  ];

  // Veterinarios disponibles
  const veterinariosDisponibles = [
    { name: "Disponibles", value: 4 },
    { name: "No Disponibles", value: 2 },
  ];

  // ============================================================
  // 🔥 CÓMPUTO RÁPIDO DE PORCENTAJES
  // ============================================================
  const totalCitas = citasPorEstado.reduce((t, c) => t + c.value, 0);
  const porcentaje = (cantidad: number) =>
    ((cantidad / totalCitas) * 100).toFixed(1) + "%";

  return (
    <div id="dashboard_agenda">
      <Br_administrativa onMinimizeChange={setMinimizado} />

      <main className={minimizado ? "minimize" : ""}>
        

        {/* ======================================================
            RESUMEN GENERAL
        ====================================================== */}
        <div id="resumen_cards">
          <div className="card_resumen">
            <h3>Citas Totales</h3>
            <p>{totalCitas}</p>
          </div>
          <div className="card_resumen">
            <h3>Pendientes</h3>
            <p>{porcentaje(citasPorEstado[0].value)}</p>
          </div>
          <div className="card_resumen">
            <h3>Confirmadas</h3>
            <p>{porcentaje(citasPorEstado[1].value)}</p>
          </div>
          <div className="card_resumen">
            <h3>Canceladas</h3>
            <p>{porcentaje(citasPorEstado[2].value)}</p>
          </div>
          <div className="card_resumen">
            <h3>Veterinarios Disponibles</h3>
            <p>{veterinariosDisponibles[0].value}</p>
          </div>
        </div>

        {/* ======================================================
            FILA DE GRÁFICOS
        ====================================================== */}
        <div id="fila_graficos">
          {/* -------- Citas por estado -------- */}
          <div className="grafico_box">
            <h3>Citas por Estado</h3>
            <ResponsiveContainer width="100%" height={260}>
              <PieChart>
                <Pie
                  dataKey="value"
                  data={citasPorEstado}
                  cx="50%"
                  cy="50%"
                  outerRadius={80}
                  paddingAngle={4}
                >
                  {citasPorEstado.map((_, i) => (
                    <Cell key={i} fill={COLORS[i % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </div>

          {/* -------- Medio de Solicitud -------- */}
          <div className="grafico_box">
            <h3>Medios de Solicitud</h3>
            <ResponsiveContainer width="100%" height={260}>
              <PieChart>
                <Pie
                  data={mediosSolicitud}
                  dataKey="value"
                  cx="50%"
                  cy="50%"
                  outerRadius={80}
                >
                  {mediosSolicitud.map((_, i) => (
                    <Cell key={i} fill={COLORS[i % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* -------- Servicios más usados (barras) -------- */}
        <div id="servicios_grafico">
          <h3>Servicios Más Solicitados</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={serviciosUsados}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" stroke="#333" />
              <YAxis stroke="#333" />
              <Tooltip />
              <Bar dataKey="value" fill="#ff9900" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </main>
    </div>
  );
}
