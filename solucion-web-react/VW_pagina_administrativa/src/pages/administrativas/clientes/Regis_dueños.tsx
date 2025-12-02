import { useEffect, useState } from 'react'
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa'
import './regis_dueños.css'
import type { tipo_doc, ClienteResponse, ClienteResquest } from '../../../components/interfaces/interfaces';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import IST from '../../../components/proteccion/IST';

function regis_dueños() {
  const [minimizado, setMinimizado] = useState(false);
  const [tipoDoc, setTipDoc] = useState<tipo_doc[]>([]);
  const [idTipoPersonaJuridica, setIdTipoPersonaJuridica] = useState<number>(1); // Por defecto Natural
  const [nombre, setNombre] = useState("");
  const [sexo, setSexo] = useState<"M" | "F" | undefined>(undefined);
  const [documento, setDocumento] = useState("");
  const [idTipoDocumento, setIdTipoDocumento] = useState<number>(0);
  const [correo, setCorreo] = useState("");
  const [telefono, setTelefono] = useState("");
  const [direccion, setDireccion] = useState("");
  const [ciudad, setCiudad] = useState("");
  const [distrito, setDistrito] = useState("");
  const [representante, setRepresentante] = useState("");
  const [activo, setActivo] = useState(true);
  const location = useLocation();
  const clienteSelecc = location.state?.cliente as ClienteResponse | undefined;
  const navigate = useNavigate();

  // Estado para controlar si es persona jurídica
  const [esPersonaJuridica, setEsPersonaJuridica] = useState(false);

  useEffect(() => {
    IST.get("/tipo-documento")
    .then(res => {
      setTipDoc(res.data);
    })
    .catch(err => {
      console.error("Error en la carga de datos", err)
    });
  }, [])

  useEffect(() => {
    if (clienteSelecc) {
      const esJuridica = clienteSelecc.idTipoPersonaJuridica === 2;
      setIdTipoPersonaJuridica(clienteSelecc.idTipoPersonaJuridica || 1);
      setIdTipoDocumento(clienteSelecc.idTipoDocumento || 0);
      setNombre(clienteSelecc.nombre || "");
      setSexo(clienteSelecc.sexo as "M" | "F" | undefined);
      setDocumento(clienteSelecc.documento || "");
      setCorreo(clienteSelecc.correo || "");
      setTelefono(clienteSelecc.telefono || "");
      setDireccion(clienteSelecc.direccion || "");
      setCiudad(clienteSelecc.ciudad || "");
      setDistrito(clienteSelecc.distrito || "");
      setRepresentante(clienteSelecc.representante || "");
      setActivo(clienteSelecc.activo ?? true);
      setEsPersonaJuridica(esJuridica);
      
      // Si es jurídica y no tiene tipo documento, asignar RUC por defecto
      if (esJuridica && !clienteSelecc.idTipoDocumento) {
        setIdTipoDocumento(2); // RUC
      }
    }
  }, [clienteSelecc]);

  // Manejar cambio de tipo de persona
  const handleTipoPersonaChange = (nuevoId: number) => {
    const esJuridica = nuevoId === 2;
    setIdTipoPersonaJuridica(nuevoId);
    setEsPersonaJuridica(esJuridica);
    
    // Si cambia a jurídica, forzar RUC y limpiar sexo
    if (esJuridica) {
      setIdTipoDocumento(2); // RUC
      setSexo(undefined);
    } else {
      // Si cambia a natural, resetear tipo documento
      setIdTipoDocumento(0);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // Validaciones básicas
    if (!nombre.trim()) {
      alert("El nombre es obligatorio");
      return;
    }

    if (!documento.trim()) {
      alert("El documento es obligatorio");
      return;
    }

    // Para personas naturales, validar que tenga sexo
    if (!esPersonaJuridica && !sexo) {
      alert("El sexo es obligatorio para personas naturales");
      return;
    }

    const nuevoCliente: ClienteResquest = {
      idEntidad: clienteSelecc?.idEntidad ?? undefined, 
      idTipoPersonaJuridica,
      nombre,
      sexo: esPersonaJuridica ? undefined : sexo, // Solo enviar sexo para naturales
      documento,
      idTipoDocumento,
      correo,
      telefono,
      direccion,
      ciudad,
      distrito,
      representante : esPersonaJuridica ? representante : "" , 
      activo
    };

    if (clienteSelecc) {
      IST.put(`/clientes/actualizar/${clienteSelecc.id}`, nuevoCliente)
        .then(res => {
          console.log("cliente actualizado:", res.data);
          alert("Cliente actualizado correctamente ✅");
          navigate("/administracion/cliente/lista"); 
        })
        .catch(err => {
          console.error("Error al actualizar cliente", err);
          alert("Error al actualizar cliente ❌");
        });
    } else {
      IST.post("/clientes/registrar", nuevoCliente)
        .then(res => {
          console.log("cliente creado:", res.data);
          alert("Entidad registrada correctamente ✅");
          navigate("/administracion/cliente/lista"); 
        })
        .catch(err => {
          console.error("Error al registrar entidad", err);
          alert("Error al registrar entidad ❌");
        });
    }
  }

  return (
    <>
      <div id='regis_dueños'>
        <Br_administrativa onMinimizeChange={setMinimizado} />

        <main className={minimizado ? 'minimize' : ''}>
          <div className="container">
            <Link className='boton_retorno' to="/administracion/cliente/lista"><i className="fa-solid fa-backward"></i></Link>
            <h2><i className="icon-user"></i> Registro Cliente</h2>
            
            <div className="form-box">
              <div className="form-section">
                <h3><i className="icon-id-card"></i> Información General</h3>
                <form onSubmit={handleSubmit}>
                  {clienteSelecc && (
                    <div className="form-row">
                      <div className="form-group">
                        <label>ID</label>
                        <input type="text" value={clienteSelecc.codigoCliente} disabled readOnly/>
                      </div>
                      <div className='form-group'>
                        <label>Fecha de registro</label>
                        <input type="text" value={
                          clienteSelecc.fechaRegistro ? 
                          `${clienteSelecc.fechaRegistro.slice(11, 16)}  del  ${clienteSelecc.fechaRegistro.split('T')[0]}` : '' } disabled readOnly/>
                      </div>
                    </div>
                  )}
                  
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="tipoPersona">Tipo de Persona *</label>
                      <select 
                        value={idTipoPersonaJuridica} 
                        onChange={(e) => handleTipoPersonaChange(Number(e.target.value))} 
                        required
                      >
                        <option value="1">Natural</option>
                        <option value="2">Jurídica</option>
                      </select>
                    </div>
                    <div className="form-group">
                      <label htmlFor="nombre">
                        {esPersonaJuridica ? "Razón Social *" : "Nombre Completo *"}
                      </label>
                      <input 
                        type="text" 
                        value={nombre} 
                        onChange={(e) => setNombre(e.target.value)} 
                        placeholder={esPersonaJuridica ? "Razón Social de la empresa" : "Nombre completo del cliente"}
                        required 
                      />
                    </div>
                  </div>

                  {/* Campo Sexo - Solo para Personas Naturales */}
                  {!esPersonaJuridica && (
                    <div className="form-group full-width">
                      <label>Género *</label>
                      <div className="radio-group">
                        <input 
                          type="radio" 
                          name="genero" 
                          value="M" 
                          checked={sexo === "M"} 
                          onChange={() => setSexo("M")} 
                        /> Masculino
                        <input 
                          type="radio" 
                          name="genero" 
                          value="F" 
                          checked={sexo === "F"} 
                          onChange={() => setSexo("F")} 
                        /> Femenino
                      </div>
                    </div>
                  )}

                  <div className="form-row"> 
                    <div className="form-group">
                      <label htmlFor="tipoDocumento">Tipo de documento *</label>
                      <select 
                        id="tipo_doc" 
                        value={idTipoDocumento} 
                        onChange={(e) => setIdTipoDocumento(Number(e.target.value))}
                        disabled={esPersonaJuridica} // Deshabilitado para jurídicas (siempre RUC)
                      >
                        <option value="0" disabled>Elija documento</option>
                        {tipoDoc
                          .filter((TD) => esPersonaJuridica ? TD.id === 2 : true) // Solo RUC para jurídicas
                          .map((TD) => (
                            <option key={TD.id} value={TD.id}>{TD.descripcion}</option>
                          ))
                        }
                      </select>
                      {esPersonaJuridica && (
                        <small style={{color: '#666', fontSize: '12px', display: 'block', marginTop: '5px'}}>
                          Para personas jurídicas solo se permite RUC
                        </small>
                      )}
                    </div>
                    <div className="form-group">
                      <label htmlFor="documento">
                        {esPersonaJuridica ? "RUC *" : "Número de documento *"}
                      </label>
                      <input 
                        type="text" 
                        value={documento} 
                        onChange={(e) => setDocumento(e.target.value)} 
                        placeholder={esPersonaJuridica ? "Número de RUC" : "Número de documento"}
                        required
                      />
                    </div>
                  </div>
                  <div className="form-group full-width">
                    {esPersonaJuridica && 
                      <div className="form-group full-width">
                        <label htmlFor="representante">Representante</label>
                        <input 
                          type="text" 
                          value={representante} 
                          onChange={(e) => setRepresentante(e.target.value)} 
                          placeholder="Nombre del Representante"
                        />
                      </div>
                    }
                  </div>
                  <div className="form-row"> 
                    <div className="form-group">
                      <label htmlFor="telefono">Teléfono</label>
                      <input 
                        type="text" 
                        value={telefono} 
                        onChange={(e) => setTelefono(e.target.value)} 
                        placeholder="Teléfono"
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="email">Email</label>
                      <input 
                        type="email" 
                        value={correo} 
                        onChange={(e) => setCorreo(e.target.value)} 
                        placeholder="correo@ejemplo.com"
                      />
                    </div>
                  </div>

                  <div className="form-row"> 
                    <div className="form-group">
                      <label htmlFor="ciudad">Ciudad</label>
                      <input 
                        type="text" 
                        value={ciudad} 
                        onChange={(e) => setCiudad(e.target.value)} 
                        placeholder="Ciudad"
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="distrito">Distrito</label>
                      <input 
                        type="text" 
                        value={distrito} 
                        onChange={(e) => setDistrito(e.target.value)}
                        placeholder="Distrito" 
                      />
                    </div>
                  </div>

                  <div className="form-row"> 
                    <div className="form-group full-width">
                      <label htmlFor="domicilio">Dirección</label>
                      <input 
                        type="text" 
                        value={direccion} 
                        onChange={(e) => setDireccion(e.target.value)} 
                        placeholder="Dirección completa"
                      />
                    </div>
                  </div>

                  <div className="form-row"> 
                    <div className="form-group">
                      <label htmlFor="activo">Estado</label>
                      <select 
                        value={activo ? "true" : "false"} 
                        onChange={(e) => setActivo(e.target.value === "true")}
                      >
                        <option value="false">Inactivo</option>
                        <option value="true">Activo</option>
                      </select>
                    </div>
                  </div>

                  <div className="form-group full-width">
                    <button type='submit' className="btn">
                      {clienteSelecc ? "Actualizar" : "Guardar"}
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </main>
      </div>    
    </>
  )
}

export default regis_dueños