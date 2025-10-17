import  { useEffect, useState } from 'react'
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa'
import './regis_dueños.css'
import type { tipo_doc, ClienteResponse, ClienteResquest } from '../../../components/interfaces/interfaces';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';

function regis_dueños() {
  const [minimizado, setMinimizado] = useState(false);
  const [imagenDueño, setImagenDueño] = useState<string | null>(null); 
  const [tipoDoc, setTipDoc] = useState<tipo_doc[]>([]);
  const [clienteReq, setClienteReq] = useState<ClienteResquest[]>([]);
  const [idTipoPersonaJuridica, setIdTipoPersonaJuridica] = useState<number>(0);
  const [nombre, setNombre] = useState("");
  const [sexo, setSexo] = useState<"M" | "F" | undefined>(undefined);
  const [documento, setDocumento] = useState("");
  const [idTipoDocumento, setIdTipoDocumento] = useState<number>(0);
  const [correo, setCorreo] = useState("");
  const [telefono, setTelefono] = useState("");
  const [direccion, setDireccion] = useState("");
  const [ciudad, setCiudad] = useState("");
  const [distrito, setDistrito] = useState("");
  const [activo, setActivo] = useState(true);
  const location = useLocation();
  const clienteSelecc = location.state?.cliente as ClienteResponse | undefined;
  const navigate = useNavigate();


  useEffect(() => {
    axios.get("http://localhost:8088/api/tipo-documento")
    .then(res => {
      setTipDoc(res.data);
    })
    .catch(err => {
      console.error("Error en la carga de datos", err)
    });
  }, [])

  useEffect(() => {
    if (clienteSelecc) {
      setIdTipoPersonaJuridica(clienteSelecc.idTipoPersonaJuridica || 0);
      setIdTipoDocumento(clienteSelecc.idTipoDocumento || 0);
      setNombre(clienteSelecc.nombre || "");
      setSexo(clienteSelecc.sexo as "M" | "F" | undefined);
      setDocumento(clienteSelecc.documento || "");
      setCorreo(clienteSelecc.correo || "");
      setTelefono(clienteSelecc.telefono || "");
      setDireccion(clienteSelecc.direccion || "");
      setCiudad(clienteSelecc.ciudad || "");
      setDistrito(clienteSelecc.distrito || "");
      setActivo(clienteSelecc.activo ?? true);
    }
  }, [clienteSelecc]);
  
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const nuevoCliente: ClienteResquest = {
      idEntidad: clienteSelecc?.idEntidad ?? undefined, 
      idTipoPersonaJuridica,
      nombre,
      sexo,
      documento,
      idTipoDocumento,
      correo,
      telefono,
      direccion,
      ciudad,
      distrito,
      activo
    };

    if (clienteSelecc) {
      axios.put("http://localhost:8088/api/clientes/actualizar", nuevoCliente)
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
      axios.post("http://localhost:8088/api/clientes/registrar", nuevoCliente)
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

  /*const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagenDueño(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };*/

  return (
    <>
      <div id='regis_dueños'>
        <Br_administrativa onMinimizeChange={setMinimizado} />

        <main className={minimizado ? 'minimize' : ''}>
          <div className="container">
            <h2><i className="icon-user"></i> Registro Cliente</h2>
            
            <div className="form-box">
              <div className="form-section">
                <h3><i className="icon-id-card"></i> Información General</h3>
                <form onSubmit={handleSubmit}>
                  {clienteSelecc && (
                    <div className="form-row">
                      <div className="form-group full-width">
                        <label>ID</label>
                        <input type="text" value={clienteSelecc.id} readOnly/>
                      </div>
                    </div>
                  )}
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="dni">Tipo persona juridica</label>
                      <select value={idTipoPersonaJuridica} onChange={(e) => setIdTipoPersonaJuridica(Number(e.target.value))}>
                        <option value="0" disabled>Elija la persona</option>
                        <option value="1">Natural</option>
                        <option value="2">Jurídica</option>
                      </select>
                    </div>
                    <div className="form-group">
                      <label htmlFor="nombre">Nombre</label>
                      <input type="text" value={nombre} onChange={(e) => setNombre(e.target.value)} />
                    </div>
                  </div>
                  <div className="form-group full-width">
                    <label>Género</label>
                    <div className="radio-group">
                      <input type="radio" name="genero" value="M" checked={sexo === "M"} onChange={() => setSexo("M")} /> Masculino
                      <input type="radio" name="genero" value="F" checked={sexo === "F"} onChange={() => setSexo("F")} /> Femenino
                    </div>
                  </div>

                  <div className="form-row"> 
                    <div className="form-group">
                      <label htmlFor="dni">Tipo de documento</label>
                      <select id="tipo_doc" value={idTipoDocumento} onChange={(e) => setIdTipoDocumento(Number(e.target.value))}>
                        <option value="0" disabled>Elija documento</option>
                        {tipoDoc.map((TD)=>(
                          <option key={TD.id} value={TD.id}>{TD.descripcion}</option>
                        ))}
                      </select>
                    </div>
                    <div className="form-group">
                      <label htmlFor="telefono">Numero de documento</label>
                      <input type="text" value={documento} onChange={(e) => setDocumento(e.target.value)} />
                    </div>
                  </div>
                  <div className="form-row"> 
                    <div className="form-group">
                      <label htmlFor="telefono">Telefono</label>
                      <input type="text" value={telefono} onChange={(e) => setTelefono(e.target.value)} />
                    </div>
                    <div className="form-group">
                      <label htmlFor="email">Email</label>
                      <input type="email" value={correo} onChange={(e) => setCorreo(e.target.value)} />
                    </div>
                  </div>
                  <div className="form-row"> 
                    <div className="form-group">
                      <label htmlFor="ciudad">Ciudad</label>
                      <input type="text" value={ciudad} onChange={(e) => setCiudad(e.target.value)} />
                    </div>
                    <div className="form-group">
                      <label htmlFor="distrito">Distrito</label>
                      <input type="text" value={distrito} onChange={(e) => setDistrito(e.target.value)}/>
                    </div>
                  </div>
                  <div className="form-row"> 
                    <div className="form-group">
                      <label htmlFor="domicilio">Dirección</label>
                      <input type="text" value={direccion} onChange={(e) => setDireccion(e.target.value)} />
                    </div>
                    <div className="form-group">
                      <label htmlFor="activo">Estado</label>
                      <select value={activo ? "true" : "false"} onChange={(e) => setActivo(e.target.value === "true")}>
                        <option value="false">Inactiva</option>
                        <option value="true">Activo</option>
                      </select>
                    </div>
                  </div>
                  

                  <div className="form-group full-width">
                    <button type='submit' className="btn">{clienteSelecc? "Actualizar" : "Guardar"}</button>
                  </div>
                </form>
              </div>

              {/*<div className="profile-section">
                <h3>
                  <i className="icon-camera"></i> Foto de perfil
                </h3>

                <div className="profile-pic">
                  {imagenDueño && (
                    <img
                      src={imagenDueño}
                      alt="Vista previa"
                      style={{
                        width: "100%",
                        height: "100%",
                        objectFit: "cover",
                        borderRadius: "50%",
                      }}
                    />
                  )}
                </div>

                <label className="upload-label">
                  Seleccionar foto
                  <input
                    type="file"
                    accept="image/*"
                    hidden
                    onChange={handleImageChange}
                  />
                </label>
              </div>*/}
              
            </div>
          </div>
        </main>
      </div>    
    </>
  )
}

export default regis_dueños