import  { useState } from 'react'
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa'
import './regis_dueños.css'

function regis_dueños() {
  const [minimizado, setMinimizado] = useState(false);
  const [imagenDueño, setImagenDueño] = useState<string | null>(null); 

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagenDueño(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

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

                <div className="form-row">
                  <div className="form-group">
                    <label htmlFor="dni">Tipo entidad</label>
                    <select name="" id="">
                      <option value="0" disabled selected>Elija Entidad</option>
                      <option value="1">Tipo 1</option>
                      <option value="2">Tipo 2</option>
                      <option value="3">Tipo 3</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label htmlFor="dni">Tipo persona juridica</label>
                    <select name="" id="">
                      <option value="0" disabled selected>Elija la persona</option>
                      <option value="1">Tipo 1</option>
                      <option value="2">Tipo 2</option>
                      <option value="3">Tipo 3</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label htmlFor="nombre">Nombre</label>
                    <input type="text" id="nombre" />
                  </div>
                </div>
                <div className="form-group full-width">
                  <label>Género</label>
                  <div className="radio-group">
                    <label><input type="radio" name="genero"/> Masculino</label>
                    <label><input type="radio" name="genero"/> Femenino</label>
                  </div>
                </div>

                <div className="form-row"> 
                  <div className="form-group">
                    <label htmlFor="dni">Tipo de documento</label>
                    <select name="" id="">
                      <option value="0" disabled selected>Elija documento</option>
                      <option value="1">Tipo 1</option>
                      <option value="2">Tipo 2</option>
                      <option value="3">Tipo 3</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label htmlFor="telefono">Numero de documento</label>
                    <input type="text" id="telefono"/>
                  </div>
                </div>
                <div className="form-row"> 
                  <div className="form-group">
                    <label htmlFor="telefono">Telefono</label>
                    <input type="email" id="telefono"/>
                  </div>
                  <div className="form-group">
                    <label htmlFor="email">Email</label>
                    <input type="email" id="email"/>
                  </div>
                </div>

                <div className="form-group full-width">
                  <label htmlFor="domicilio">Domicilio</label>
                  <input type="text" id="domicilio"/>
                </div>

                <div className="form-group full-width">
                  <button className="btn">Guardar</button>
                </div>
              </div>

              <div className="profile-section">
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
              </div>
              
            </div>
          </div>
        </main>
      </div>    
    </>
  )
}

export default regis_dueños