import { /*useEffect,*/ useState } from 'react';
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa';
import './regis_mascotas.css';
/*import axios from 'axios';*/

/*interface dueño {
  id: number;
  tipo_documento: number;
  nombre: string;
  correo: string;
}*/

function Regis_mascotas() {
    const [minimizado, setMinimizado] = useState(false);
    const [imagenMascota, setImagenMascota] = useState<string | null>(null);
    {/*const [dueños, setDueños] = useState<dueño[]>([]);*/}

    const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file) {
        const reader = new FileReader();
        reader.onloadend = () => {
            setImagenMascota(reader.result as string);
        };
        reader.readAsDataURL(file);
        }
    };    

    {/*useEffect(() => {
        axios.get("http://localhost:8080/ls_pet")
        .then(res => {
        setDueños(res.data);
        })
        .catch(err => {
        console.error("Error en la carga de datos", err)
        });
    }, [])*/}

    return (
        <>
            <div id="regis_mascotas">
                <Br_administrativa onMinimizeChange={setMinimizado} />
                <main className={minimizado ? 'minimize' : ''}>
                    <div className="content-section hidden" id="nueva-mascota-section">
                            <div className="form-header">
                                <h3>Información de la Mascota</h3>
                            </div>
                            <div className="form-content">
                                <div className="form-main">
                                    <div className="form-fields">
                                        <form id="pet-form">
                                            <div className="form-grid">
                                                <div className="form-group">
                                                    <label htmlFor="pet-name">Nombre de la Mascota *</label>
                                                    <input type="text" id="pet-name" name="pet-name" 
                                                        placeholder="Ej: Max, Luna, Rocky" required />
                                                </div>
                                                    
                                                <div className="form-group">
                                                    <label htmlFor="birth-date">Fecha de Nacimiento</label>
                                                    <input type="date" id="birth-date" name="birth-date" />
                                                </div>
                                                    
                                                <div className="form-group">
                                                    <label htmlFor="species">Especie *</label>
                                                    <select id="species" name="species" required>
                                                        <option value="">Seleccionar especie</option>
                                                        <option value="perro">Perro</option>
                                                        <option value="gato">Gato</option>
                                                        <option value="conejo">Conejo</option>
                                                        <option value="hamster">Hámster</option>
                                                        <option value="ave">Ave</option>
                                                        <option value="otro">Otro</option>
                                                    </select>
                                                </div>
                                                        
                                                <div className="form-group">
                                                    <label htmlFor="breed">Raza</label>
                                                    <select id="breed" name="breed">
                                                        <option value="">Seleccionar raza</option>
                                                        <option value="mestizo">Mestizo</option>
                                                    </select>
                                                </div>
                                                        
                                                <div className="form-group">
                                                    <label htmlFor="color">Color Principal</label>
                                                    <input type="color" id="color" name="color" value="#8B4513" className="pet-color"/>
                                                </div>
                                                        
                                                <div className="form-group">
                                                    <label htmlFor="weight">Peso (kg)</label>
                                                    <input type="number" id="weight" name="weight" 
                                                            step="0.1" min="0" placeholder="Ej: 5.5" className="pet-weight"/>
                                                </div>
                                                        
                                                <div className="form-group">
                                                    <label>Sexo *</label>
                                                    <div className="gender-group">
                                                        <div className="radio-group">
                                                            <input type="radio" id="male" name="sex" value="macho" required/>
                                                            <label htmlFor="male">Macho</label>
                                                        </div>
                                                        <div className="radio-group">
                                                            <input type="radio" id="female" name="sex" value="hembra" required/>
                                                            <label htmlFor="female">Hembra</label>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div className="form-group">
                                                    <label>¿Está castrado/a?</label>
                                                    <div className="gender-group">
                                                        <div className="radio-group">
                                                            <input type="radio" id="neutered-yes" name="neutered" value="si"/>
                                                            <label htmlFor="neutered-yes">Sí</label>
                                                        </div>
                                                        <div className="radio-group">
                                                            <input type="radio" id="neutered-no" name="neutered" value="no"/>
                                                            <label htmlFor="neutered-no">No</label>
                                                        </div>
                                                    </div>
                                                </div>
                                                        
                                                <div className="form-group full-width">
                                                    <label htmlFor="medical-notes">Observaciones Médicas</label>
                                                    <textarea id="medical-notes" name="medical-notes" 
                                                                placeholder="Alergias, medicamentos, condiciones especiales, etc."></textarea>
                                                </div>
                                            </div>
                                                    
                                            <div className="owner-info">
                                                <h4>👤 Información del Dueño</h4>
                                                <div className="owner-search">
                                                    <input type="text" id="owner-search" placeholder="Buscar por DNI o nombre del cliente"/>
                                                    <button type="button" className="search-btn">Buscar</button>
                                                </div>
                                                <div className="owner-details" id="owner-details">
                                                    <div className="owner-item">
                                                        <strong>Nombre:</strong>
                                                                <span id="owner-name">-</span>
                                                    </div>
                                                    <div className="owner-item">
                                                        <strong>DNI:</strong>
                                                        <span id="owner-dni">-</span>
                                                    </div>
                                                    <div className="owner-item">
                                                        <strong>Teléfono:</strong>
                                                        <span id="owner-phone">-</span>
                                                    </div>
                                                    <div className="owner-item">
                                                        <strong>Email:</strong>
                                                        <span id="owner-email">-</span>
                                                    </div>
                                                </div>
                                            </div>
                                                    
                                            <button type="submit" className="submit-btn">Registrar Mascota</button>
                                        </form>
                                    </div>
                                            
                                    <div className="photo-section-form">
                                        <h4 style={{color: "#666", margin: "0 0 10px 0", fontSize: 16, fontWeight: 500}}>📷 Foto de la Mascota</h4>
                                        <label htmlFor="foto-mascota" className="photo-upload-text">Seleccionar foto</label>
                                        <input
                                            type="file"
                                            id="foto-mascota"
                                            accept="image/*"
                                            onChange={handleImageChange}
                                            style={{ display: "none" }}
                                        />
                                        <div className="profile-photo" id="pet-photo">
                                            {imagenMascota ? (
                                            <img
                                                src={imagenMascota}
                                                alt="Foto de la mascota"
                                                style={{
                                                width: "100%",
                                                height: "100%",
                                                objectFit: "cover",
                                                borderRadius: "50%",
                                                }}
                                            />
                                            ) : (
                                            "🐕"
                                            )}
                                        </div>
                                    </div>
                                </div>
                            </div>
                    </div>
                </main>    
            </div>
        </>
    )
}

export default Regis_mascotas