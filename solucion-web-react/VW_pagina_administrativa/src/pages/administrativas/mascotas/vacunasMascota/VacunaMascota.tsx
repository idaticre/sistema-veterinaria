import React, { useEffect, useState } from 'react';
import IST from '../../../../components/proteccion/IST';
import Swal from 'sweetalert2';
import type { 
  vacunaMascotaRequest, 
  vacunaMascotaResponse,
  VacunaResponse,
  AplicacionViaResponse,
  ColaboradorResponse,
  veterinarioResponse
} from '../../../../components/interfaces/interfaces';
import './vacunaMascota.css';

interface VacunaMascotaProps {
  mascotaId: number;
  mascotaNombre: string;
  onClose: () => void;
}

const VacunaMascota: React.FC<VacunaMascotaProps> = ({ mascotaId, mascotaNombre, onClose }) => {
  const [registros, setRegistros] = useState<vacunaMascotaResponse[]>([]);
  const [loading, setLoading] = useState(true);

  // Catálogos
  const [vacunas, setVacunas] = useState<VacunaResponse[]>([]);
  const [vias, setVias] = useState<AplicacionViaResponse[]>([]);
  const [colaboradores, setColaboradores] = useState<ColaboradorResponse[]>([]);
  const [veterinarios, setVeterinarios] = useState<veterinarioResponse[]>([]);

  // Formulario
  const [mostrarForm, setMostrarForm] = useState(false);
  const [editandoId, setEditandoId] = useState<number | null>(null);
  
  const [formValues, setFormValues] = useState<vacunaMascotaRequest>({
    idVacuna: 0,
    idMascota: mascotaId,
    idVia: 0,
    dosis: '',
    fechaAplicacion: new Date().toISOString().split('T')[0],
    durabilidad: 1,
    proxDosis: '',
    idColaborador: undefined,
    idVeterinario: undefined,
    observaciones: '',
    activo: true
  });

  // Cargar datos
  const cargarDatos = async () => {
    setLoading(true);
    try {
      const resApps = await IST.get("/vacunas-mascota");
      const listApps = Array.isArray(resApps.data) ? resApps.data : (resApps.data.data || []);
      const filtradas = listApps.filter((r: vacunaMascotaResponse) => r.idMascota === mascotaId && r.activo !== false);
      setRegistros(filtradas);
    } catch (err) {
      console.error(err);
      Swal.fire("Error", "No se pudieron obtener las vacunas de la mascota", "error");
    } finally {
      setLoading(false);
    }
  };

  const cargarCatalogos = async () => {
    try {
      const [resVac, resVia, resColab, resVet] = await Promise.all([
        IST.get("/vacunas"),
        IST.get("/via-aplicacion"),
        IST.get("/colaboradores"),
        IST.get("/veterinarios")
      ]);

      setVacunas(Array.isArray(resVac.data) ? resVac.data : (resVac.data.data || []));
      setVias(Array.isArray(resVia.data) ? resVia.data : (resVia.data.data || []));
      setColaboradores(Array.isArray(resColab.data) ? resColab.data : (resColab.data.data || []));
      
      const vts = Array.isArray(resVet.data) ? resVet.data : (resVet.data.data || []);
      setVeterinarios(vts);
    } catch (err) {
      console.error("Error cargando catálogos", err);
    }
  };

  useEffect(() => {
    cargarDatos();
    cargarCatalogos();
  }, [mascotaId]);

  // Calcular próxima dosis automáticamente
  useEffect(() => {
    if (formValues.fechaAplicacion && formValues.durabilidad) {
      const date = new Date(formValues.fechaAplicacion + 'T00:00:00');
      if (!isNaN(date.getTime())) {
        date.setFullYear(date.getFullYear() + Number(formValues.durabilidad));
        const nextDateStr = date.toISOString().split('T')[0];
        setFormValues(prev => ({ ...prev, proxDosis: nextDateStr }));
      }
    }
  }, [formValues.fechaAplicacion, formValues.durabilidad]);

  const handleEdit = (reg: vacunaMascotaResponse) => {
    setEditandoId(reg.id || null);
    setFormValues({
      id: reg.id,
      idVacuna: reg.idVacuna,
      idMascota: reg.idMascota,
      idVia: reg.idVia,
      dosis: reg.dosis,
      fechaAplicacion: reg.fechaAplicacion ? reg.fechaAplicacion.split('T')[0] : '',
      durabilidad: reg.durabilidad,
      proxDosis: reg.proxDosis ? reg.proxDosis.split('T')[0] : '',
      idColaborador: reg.idColaborador || undefined,
      idVeterinario: reg.idVeterinario || undefined,
      observaciones: reg.observaciones,
      activo: reg.activo
    });
    setMostrarForm(true);
  };

  const handleAddNew = () => {
    setEditandoId(null);
    setFormValues({
      idVacuna: vacunas.length > 0 ? vacunas[0].id : 0,
      idMascota: mascotaId,
      idVia: vias.length > 0 ? vias[0].id : 0,
      dosis: '',
      fechaAplicacion: new Date().toISOString().split('T')[0],
      durabilidad: 1,
      proxDosis: '',
      idColaborador: colaboradores.length > 0 ? colaboradores[0].id : undefined,
      idVeterinario: veterinarios.length > 0 ? veterinarios[0].id : undefined,
      observaciones: '',
      activo: true
    });
    setMostrarForm(true);
  };

  const handleDelete = async (id?: number) => {
    if (id === undefined) return;
    const result = await Swal.fire({
      title: '¿Estás seguro?',
      text: 'Se eliminará el registro de vacunación de forma lógica.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6'
    });

    if (result.isConfirmed) {
      try {
        await IST.delete(`/vacunas-mascota/${id}`);
        Swal.fire("Eliminado", "El registro ha sido eliminado lógicamente.", "success");
        cargarDatos();
      } catch (err) {
        console.error(err);
        Swal.fire("Error", "No se pudo eliminar el registro", "error");
      }
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (formValues.idVacuna === 0 || formValues.idVia === 0) {
      Swal.fire("Advertencia", "Debe seleccionar una vacuna y una vía de aplicación", "warning");
      return;
    }

    try {
      if (editandoId !== null) {
        await IST.put("/vacunas-mascota", formValues);
        Swal.fire("Actualizado", "Registro de vacuna actualizado con éxito.", "success");
      } else {
        const { id, ...createValues } = formValues;
        await IST.post("/vacunas-mascota", createValues);
        Swal.fire("Registrado", "Vacuna agregada con éxito.", "success");
      }
      setMostrarForm(false);
      cargarDatos();
    } catch (err) {
      console.error(err);
      Swal.fire("Error", "Ocurrió un problema al guardar el registro", "error");
    }
  };

  return (
    <div className="modal-vacuna-overlay" onClick={onClose}>
      <div className="modal-vacuna-contenido" onClick={(e) => e.stopPropagation()}>
        
        {/* Cabecera del Modal */}
        <div className="modal-vacuna-cabecera">
          <h2>Vacunas de {mascotaNombre}</h2>
          <button className="modal-vacuna-cierre" onClick={onClose}>❌</button>
        </div>

        {/* Botón superior para agregar */}
        <div className="modal-vacuna-acciones-top">
          <button className="btn-vacuna btn-primario" onClick={handleAddNew}>
            ➕ AÑADIR VACUNACIÓN
          </button>
        </div>

        {/* Listado de vacunas en tabla */}
        <div className="modal-vacuna-cuerpo">
          {loading ? (
            <p className="loading-text">Cargando registros de vacunas...</p>
          ) : registros.length === 0 ? (
            <div className="sin-registros">
              <p>No hay vacunas registradas para esta mascota. 🐾</p>
            </div>
          ) : (
            <div className="tabla-wrapper">
              <table className="tabla-vacunas">
                <thead>
                  <tr>
                    <th>Código</th>
                    <th>Vacuna</th>
                    <th>Dosis</th>
                    <th>Vía</th>
                    <th>Fecha Aplicación</th>
                    <th>Próxima Dosis</th>
                    <th>Colaborador</th>
                    <th>Veterinario</th>
                    <th>Observaciones</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {registros.map((reg) => {
                    const vacunaNom = vacunas.find(v => v.id === reg.idVacuna)?.nombre || `ID: ${reg.idVacuna}`;
                    const viaNom = vias.find(v => v.id === reg.idVia)?.nombre || `ID: ${reg.idVia}`;
                    const colabNom = colaboradores.find(c => c.id === reg.idColaborador)?.nombre || 'No asignado';
                    const vetNom = veterinarios.find(v => v.id === reg.idVeterinario)?.nombre || 'No asignado';
                    
                    return (
                      <tr key={reg.id}>
                        <td><strong>{reg.codigo}</strong></td>
                        <td><span className="badge-vacuna">{vacunaNom}</span></td>
                        <td>{reg.dosis || 'N/A'}</td>
                        <td><span className="badge-via">{viaNom}</span></td>
                        <td>{reg.fechaAplicacion ? new Date(reg.fechaAplicacion + 'T00:00:00').toLocaleDateString() : 'N/A'}</td>
                        <td>
                          <span className="fecha-prox">
                            {reg.proxDosis ? new Date(reg.proxDosis + 'T00:00:00').toLocaleDateString() : 'N/A'}
                          </span>
                        </td>
                        <td>{colabNom}</td>
                        <td>{vetNom}</td>
                        <td className="observaciones-celda">{reg.observaciones || '-'}</td>
                        <td className="acciones-celdas">
                          <button className="btn-icon btn-editar" title="Editar" onClick={() => handleEdit(reg)}>✏️</button>
                          <button className="btn-icon btn-eliminar" title="Eliminar" onClick={() => handleDelete(reg.id)}>🗑️</button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Modal de Formulario Interno (Añadir / Editar) */}
        {mostrarForm && (
          <div className="form-vacuna-overlay">
            <form onSubmit={handleSubmit} className="form-vacuna-contenido">
              <h3>{editandoId ? `Editar Vacunación (ID: ${editandoId})` : "Nueva Vacunación"}</h3>
              
              {/* ID de registro - Solo visible si se está editando */}
              {editandoId && (
                <div className="form-grupo">
                  <label>ID Registro (Solo Lectura)</label>
                  <input type="text" value={editandoId} readOnly className="input-readonly" />
                </div>
              )}

              <div className="form-dos-columnas">
                <div className="form-grupo">
                  <label>Vacuna *</label>
                  <select 
                    value={formValues.idVacuna} 
                    onChange={(e) => setFormValues(prev => ({ ...prev, idVacuna: Number(e.target.value) }))}
                    required
                  >
                    <option value={0} disabled>Seleccione una vacuna</option>
                    {vacunas.filter(v => v.activo).map(v => (
                      <option key={v.id} value={v.id}>{v.nombre}</option>
                    ))}
                  </select>
                </div>

                <div className="form-grupo">
                  <label>Vía de Aplicación *</label>
                  <select 
                    value={formValues.idVia} 
                    onChange={(e) => setFormValues(prev => ({ ...prev, idVia: Number(e.target.value) }))}
                    required
                  >
                    <option value={0} disabled>Seleccione una vía</option>
                    {vias.filter(v => v.activo).map(v => (
                      <option key={v.id} value={v.id}>{v.nombre}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="form-dos-columnas">
                <div className="form-grupo">
                  <label>Dosis</label>
                  <input 
                    type="text" 
                    placeholder="Ej: 0.5 ml, 1 dosis" 
                    value={formValues.dosis} 
                    onChange={(e) => setFormValues(prev => ({ ...prev, dosis: e.target.value }))}
                  />
                </div>

                <div className="form-grupo">
                  <label>Durabilidad (años) *</label>
                  <input 
                    type="number" 
                    min={1} 
                    max={10}
                    value={formValues.durabilidad} 
                    onChange={(e) => setFormValues(prev => ({ ...prev, durabilidad: Number(e.target.value) }))}
                    required
                  />
                </div>
              </div>

              <div className="form-dos-columnas">
                <div className="form-grupo">
                  <label>Fecha de Aplicación *</label>
                  <input 
                    type="date" 
                    value={formValues.fechaAplicacion || ''} 
                    onChange={(e) => setFormValues(prev => ({ ...prev, fechaAplicacion: e.target.value }))}
                    required
                  />
                </div>

                <div className="form-grupo">
                  <label>Próxima Dosis (Calculada) *</label>
                  <input 
                    type="date" 
                    value={formValues.proxDosis || ''} 
                    onChange={(e) => setFormValues(prev => ({ ...prev, proxDosis: e.target.value }))}
                    required
                  />
                </div>
              </div>

              <div className="form-dos-columnas">
                <div className="form-grupo">
                  <label>Colaborador</label>
                  <select 
                    value={formValues.idColaborador || ''} 
                    onChange={(e) => setFormValues(prev => ({ ...prev, idColaborador: e.target.value ? Number(e.target.value) : undefined }))}
                  >
                    <option value="">Seleccione colaborador</option>
                    {colaboradores.filter(c => c.activo).map(c => (
                      <option key={c.id} value={c.id}>{c.nombre}</option>
                    ))}
                  </select>
                </div>

                <div className="form-grupo">
                  <label>Veterinario Responsable</label>
                  <select 
                    value={formValues.idVeterinario || ''} 
                    onChange={(e) => setFormValues(prev => ({ ...prev, idVeterinario: e.target.value ? Number(e.target.value) : undefined }))}
                  >
                    <option value="">Seleccione veterinario</option>
                    {veterinarios.map(v => (
                      <option key={v.id} value={v.id}>{v.nombre}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="form-grupo">
                <label>Observaciones</label>
                <textarea 
                  rows={2}
                  placeholder="Detalles adicionales sobre la vacunación..."
                  value={formValues.observaciones} 
                  onChange={(e) => setFormValues(prev => ({ ...prev, observaciones: e.target.value }))}
                />
              </div>

              {editandoId && (
                <div className="form-grupo checkbox-grupo">
                  <label>
                    <input 
                      type="checkbox" 
                      checked={formValues.activo} 
                      onChange={(e) => setFormValues(prev => ({ ...prev, activo: e.target.checked }))}
                    />
                    Registro Activo
                  </label>
                </div>
              )}

              <div className="form-vacuna-botones">
                <button type="submit" className="btn-vacuna btn-exito">
                  {editandoId ? "Actualizar" : "Guardar"}
                </button>
                <button type="button" className="btn-vacuna btn-secundario" onClick={() => setMostrarForm(false)}>
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        )}

      </div>
    </div>
  );
};

export default VacunaMascota;