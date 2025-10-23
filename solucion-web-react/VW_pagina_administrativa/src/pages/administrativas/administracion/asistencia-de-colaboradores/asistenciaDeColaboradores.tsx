import { useState, useEffect } from "react";
import axios from "axios";
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import "./asistencia.css";

interface Colaborador {
  id: number;
  codigoColaborador: string;
  nombre: string;
  correo?: string;
  telefono?: string;
  activo: boolean;
}

interface Asistencia {
  id: number;
  colaborador: Colaborador;
  fecha: string;
  horaEntrada?: string;
  horaSalidaAlmuerzo?: string;
  horaEntradaAlmuerzo?: string;
  horaSalida?: string;
}

function AsistenciaColaboradores() {
  const [minimizado, setMinimizado] = useState(false);
  const [busqueda, setBusqueda] = useState("");
  const [colaboradores, setColaboradores] = useState<Colaborador[]>([]);
  const [asistencias, setAsistencias] = useState<Asistencia[]>([]);
  const [filtrados, setFiltrados] = useState<Colaborador[]>([]);
  const [colaboradorSeleccionado, setColaboradorSeleccionado] = useState<Colaborador | null>(null);
  const [mostrarModal, setMostrarModal] = useState(false);

  const [nuevoColaborador, setNuevoColaborador] = useState({ nombre: "", cargo: "" });

  // 🔹 Cargar colaboradores
  useEffect(() => {
    axios
      .get("http://localhost:8088/api/colaboradores")
      .then((res) => {
        setColaboradores(res.data.data || []);
      })
      .catch((err) => console.error("Error cargando colaboradores:", err));

    axios
      .get("http://localhost:8088/api/registro-asistencia")
      .then((res) => setAsistencias(res.data.data || []))
      .catch((err) => console.error("Error cargando asistencias:", err));
  }, []);

  // 🔹 Filtrar colaboradores según la búsqueda
  useEffect(() => {
    setFiltrados(
      colaboradores.filter((c) =>
        c.nombre?.toLowerCase().includes(busqueda.toLowerCase())
      )
    );
  }, [busqueda, colaboradores]);

  // 🔹 Generar código automático
  const generarCodigo = () => {
    const numero = colaboradores.length + 1;
    return `COL-${String(numero).padStart(4, "0")}`;
  };

  // 🔹 Registrar colaborador
  const registrarColaborador = () => {
    if (!nuevoColaborador.nombre) {
      alert("Completa todos los campos");
      return;
    }

    const codigoGenerado = generarCodigo();

    const nuevo = {
      codigoColaborador: codigoGenerado,
      nombre: nuevoColaborador.nombre,
      activo: true,
    };

    axios
      .post("http://localhost:8088/api/colaboradores", nuevo)
      .then((res) => {
        alert(`✅ Colaborador registrado con código ${codigoGenerado}`);
        setColaboradores([...colaboradores, res.data.data || res.data]);
        setMostrarModal(false);
        setNuevoColaborador({ nombre: "", cargo: "" });
      })
      .catch((err) => {
        console.error("Error al registrar colaborador:", err);
        alert("❌ No se pudo registrar el colaborador");
      });
  };

  // 🔹 Marcar asistencia
  const marcarAsistencia = (
    tipo: "entrada" | "salidaAlmuerzo" | "entradaAlmuerzo" | "salidaFinal"
  ) => {
    if (!colaboradorSeleccionado) {
      alert("Selecciona un colaborador primero");
      return;
    }

    const ahora = new Date();
    const fecha = ahora.toISOString().split("T")[0];
    const hora = ahora.toLocaleTimeString("es-PE", { hour12: false });

    const nuevoRegistro = {
      colaborador: { id: colaboradorSeleccionado.id },
      fecha,
      horaEntrada: tipo === "entrada" ? hora : undefined,
      horaSalidaAlmuerzo: tipo === "salidaAlmuerzo" ? hora : undefined,
      horaEntradaAlmuerzo: tipo === "entradaAlmuerzo" ? hora : undefined,
      horaSalida: tipo === "salidaFinal" ? hora : undefined,
    };

    axios
      .post("http://localhost:8088/api/registro-asistencia", nuevoRegistro)
      .then((res) => {
        alert("✅ Registro de asistencia guardado correctamente");
        setAsistencias((prev) => [...prev, res.data.data || res.data]);
      })
      .catch((err) => {
        console.error("Error al registrar asistencia:", err);
        alert("❌ Error al guardar asistencia");
      });
  };

  // 🔹 Seleccionar colaborador (corregido)
  const seleccionarColaborador = (colab: Colaborador) => {
    setColaboradorSeleccionado(colab);
    setBusqueda(""); // limpia el campo de búsqueda
  };

  // 🔹 Filtrar registros del colaborador seleccionado
  const registrosColaborador = asistencias.filter(
    (a) => a.colaborador?.id === colaboradorSeleccionado?.id
  );

  return (
    <div id="asistencia">
      <Br_administrativa onMinimizeChange={setMinimizado} />
      <main className={minimizado ? "minimize" : ""}>
        <section id="lst_asistencia">
          <div id="encabezado">
            <h2>📋 Registro de Asistencia de Colaboradores</h2>
          </div>

          <div id="buscador">
            <input
              type="text"
              placeholder="Buscar colaborador..."
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
            />
          </div>

          {busqueda && filtrados.length > 0 && (
            <div className="resultados-busqueda">
              {filtrados.map((c) => (
                <div
                  key={c.id}
                  className={`item-colaborador ${colaboradorSeleccionado?.id === c.id ? "seleccionado" : ""}`}
                  onClick={() => seleccionarColaborador(c)} // ✅ usa la función corregida
                >
                  {c.nombre} ({c.codigoColaborador})
                </div>
              ))}
            </div>
          )}

          {colaboradorSeleccionado && (
            <div className="panel-registro">
              <h3>👤 {colaboradorSeleccionado.nombre}</h3>
              <p><strong>Código:</strong> {colaboradorSeleccionado.codigoColaborador}</p>

              <div className="acciones-asistencia">
                <button onClick={() => marcarAsistencia("entrada")}>🕒 Entrada</button>
                <button onClick={() => marcarAsistencia("salidaAlmuerzo")}>🍴 Salida Almuerzo</button>
                <button onClick={() => marcarAsistencia("entradaAlmuerzo")}>🍽️ Regreso</button>
                <button onClick={() => marcarAsistencia("salidaFinal")}>🏁 Salida Final</button>
              </div>

              <h4>📅 Registros</h4>
              <div className="tabla-asistencia">
                {registrosColaborador.map((r) => (
                  <div key={r.id} className="fila-asistencia">
                    <div>Fecha: {r.fecha}</div>
                    <div>Entrada: {r.horaEntrada || "—"}</div>
                    <div>Salida Almuerzo: {r.horaSalidaAlmuerzo || "—"}</div>
                    <div>Entrada Almuerzo: {r.horaEntradaAlmuerzo || "—"}</div>
                    <div>Salida Final: {r.horaSalida || "—"}</div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </section>
      </main>

      {mostrarModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Registrar nuevo colaborador</h3>
            <input
              type="text"
              placeholder="Nombre completo"
              value={nuevoColaborador.nombre}
              onChange={(e) =>
                setNuevoColaborador({ ...nuevoColaborador, nombre: e.target.value })
              }
            />
            <div className="acciones-modal">
              <button onClick={registrarColaborador}>Guardar</button>
              <button onClick={() => setMostrarModal(false)}>Cancelar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default AsistenciaColaboradores;
