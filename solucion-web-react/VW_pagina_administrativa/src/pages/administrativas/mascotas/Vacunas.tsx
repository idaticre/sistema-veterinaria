import { useEffect, useState } from 'react'
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa';
import "./vacunas.css"
import type { Especialidad } from '../../../components/interfaces/interfaces';
import IST from '../../../components/proteccion/IST';

function Vacunas() {
  const [minimizado, setMinimizado] = useState(false);
  const [busqueda, setBusqueda] = useState("");
  const [especialidades, setEspecialidades] = useState<Especialidad[]>([]);
  const [filtrados, setFiltrados] = useState<Especialidad[]>([]);
  const [formulario, setFormulario] =useState(false);
  const [nombre, setNombre] = useState("");
  const [activo, setActivo] = useState(true);
  const [id, setId] = useState<number | undefined>(undefined);

 useEffect(() => {
    IST.get("http://localhost:8088/api/especialidades")
    .then(res => {
      setEspecialidades(res.data);
    })
    .catch(err => {
      console.error("Error en la carga de datos", err)
    });
  }, [])

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const especialidad: Especialidad = { id, nombre, activo };

    if (id !== undefined) {
      IST.put("http://localhost:8088/api/especialidades", especialidad)
        .then(res => {
          setEspecialidades(
            especialidades.map(e => e.id === id ? res.data : e)
          );
          limpiarFormulario();
        })
        .catch(err => console.error("Error al editar", err));
    } else {
      IST.post("http://localhost:8088/api/especialidades", especialidad)
        .then(res => {
          setEspecialidades([...especialidades, res.data]);
          limpiarFormulario();
        })
        .catch(err => console.error("Error al guardar", err));
    }
  };

  const handleEdit = (esp: Especialidad) => {
    setId(esp.id);
    setNombre(esp.nombre);
    setActivo(esp.activo);
    setFormulario(true);
  };

  const limpiarFormulario = () => {
    setFormulario(false);
    setId(undefined);
    setNombre("");
    setActivo(true);
  };


  const handleDelete = (id?: number) => {

    if (id === undefined) return; 

    if (!window.confirm("¿Seguro que deseas eliminar esta especialidad?")) {
      return;
    }

    IST.delete(`http://localhost:8088/api/especialidades/${id}`)
      .then(() => {
        setEspecialidades(especialidades.filter(e => e.id !== id));
      })
      .catch(err => {
        console.error("Error al eliminar la especialidad", err);
      });
  };

  useEffect(() => {
        const palabrasBusqueda = busqueda.toLowerCase().split(" ").filter(Boolean);
  
        const resultado = especialidades.filter((especialidad) =>{
          const texto = `${especialidad.nombre} ${especialidad.id}`.toLowerCase();
          return palabrasBusqueda.every(palabra => texto.includes(palabra));
        });
        setFiltrados(resultado);
  }, [busqueda, especialidades]);

  return (
    <>
      <div id='vacunas'>
        <Br_administrativa  onMinimizeChange={setMinimizado} />
        <main className={minimizado ? 'minimize' : ''}>
          <div id='lista_vacunas'>
            <div id='buscador'>
              <div id='br_buscador'>
                <input type="text" placeholder='Nombre del cliente.....' value={busqueda}
                  onChange={(e) => setBusqueda(e.target.value)}/>
              </div>
              <button onClick={()=>setFormulario(!formulario)}>➕AÑADIR</button>
            </div>
            <div>
              <table className='tabla-productos'>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  {filtrados.map((especialidad) => (
                    <tr key={especialidad.id}>
                      <td>{especialidad.id}</td>
                      <td>{especialidad.nombre}</td>
                      <td>
                        {especialidad.activo? "✅" : "❌"}
                      </td>
                      <td>
                        <i className="fa-solid fa-pen" onClick={() => handleEdit(especialidad)} />
                        <i className="fa-solid fa-trash" onClick={() => handleDelete(especialidad.id)}/>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {formulario && (
            <form onSubmit={handleSubmit} className="form-especialidad">
              <h3>{id ? "Editar Especialidad" : "Nueva Especialidad"}</h3>
              <input 
                type="text" 
                placeholder="Nombre" 
                value={nombre} 
                onChange={(e) => setNombre(e.target.value)} 
                required 
              />
              <label>
                Activo:
                <select value={activo ? "true" : "false"} onChange={(e) => setActivo(e.target.value === "true")}>
                  <option value="true">Activo</option>
                  <option value="false">Inactivo</option>
                </select>
              </label>
              <button type="submit">{id ? "Actualizar" : "Guardar"}</button>
              <button type="button" onClick={limpiarFormulario}>Cancelar</button>
            </form>
          )}
        </main>
      </div>
    </>
  )
}

export default Vacunas