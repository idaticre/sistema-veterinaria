import { useEffect, useState } from 'react';
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa';
import './regis_mascotas.css';
import axios from 'axios';

interface dueño {
  id: number;
  tipo_documento: number;
  nombre: string;
  correo: string;
}

function Regis_mascotas() {
    const [minimizado, setMinimizado] = useState(false);
    const [imagenMascota, setImagenMascota] = useState<string | null>(null);
    const [dueños, setDueños] = useState<dueño[]>([]);

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

    return (
        <>
            <div id="regis_mascotas">
                <Br_administrativa onMinimizeChange={setMinimizado} />
                <main className={minimizado ? 'minimize' : ''}>
                    <div className="content-section hidden" id="nueva-mascota-section">
                            <div className="form-header">
                                <h2>Información de la Mascota</h2>
                            </div>
                            <div className="form-content">
                                <div className="form-main">
                                    <div className="form-fields">
                                        <form id="pet-form">
                                            <div className="form-grid">
                                                <div className="form-group">
                                                    <label htmlFor="pet-name">Nombre de la Mascota *</label>
                                                    <input type="text" id="pet-name" name="pet-name" placeholder="Ej: Max, Luna, Rocky" required />
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

                                                <div className="form-group">
                                                    <label htmlFor="estado">Estado *</label>
                                                    <select id="estado" name="estado" required>
                                                        <option selected>-- Seleccione estado --</option>
                                                        <option value={1}>Activa</option>
                                                        <option value={2}>En tratamiento</option>
                                                        <option value={3}>Fallecida</option>
                                                        <option value={4}>En adopción</option>
                                                    </select>
                                                </div>
                                                
                                                <div className="form-group">
                                                    <label htmlFor="veterinario">Veterinario a cargo *</label>
                                                    <select id="veterinario" name="veterinario" required>
                                                        <option selected>-- FALTA DESARROLLAR --</option>
                                                        {/* METER LOS VETERINARIOS ACÁ AUTOMÁTICAMENTE */}
                                                    </select>
                                                </div>

                                                <div className="form-group">
                                                    <label htmlFor="pelaje">Pelaje *</label>
                                                    <input type="text" id="pelaje" name="pelaje" placeholder="Ej: Corto, largo" required />
                                                </div>

                                                <div className="form-group">
                                                    <label htmlFor="tamano">Tamaño *</label>
                                                    <select id="tamano" name="tamano" required>
                                                        <option selected>-- Seleccione tamaño --</option>
                                                        <option value="XS">Muy pequeño</option>
                                                        <option value="S">Pequeño</option>
                                                        <option value="M">Mediano</option>
                                                        <option value="L">Grande</option>
                                                        <option value="XL">Muy grande</option>
                                                    </select>
                                                </div>

                                                <div className="form-group">
                                                    <label htmlFor="etapa-de-vida">Etapa de vida *</label>
                                                    <select id="etapa-de-vida" name="etapa-de-vida" required>
                                                        <option selected>-- Seleccione etapa --</option>
                                                        <option value={1}>Cachorro</option>
                                                        <option value={2}>Joven</option>
                                                        <option value={3}>Adulto</option>
                                                        <option value={4}>Señor</option>
                                                    </select>
                                                </div>

                                                <div className="form-group">
                                                    <label htmlFor="alergias">Alergias </label>
                                                    <input type="text" id="alergias" name="alergias" placeholder="Ej: Alimentarias o ambientales"/>
                                                </div>

                                                <div className="form-group">
                                                    <label htmlFor="chip">Chip</label>
                                                    <input type="checkbox" id="chip" name="chip"/>

                                                    <label htmlFor="pedigree">Pedigree</label>
                                                    <input type="checkbox" id="pedigree" name="pedigree"/>
                                                </div>

                                                <div className="form-group">
                                                    <label htmlFor="factor-dea">Factor DEA</label>
                                                    <input type="checkbox" id="factor-dea" name="factor-dea"/>

                                                    <label htmlFor="agresividad">Agresivo</label>
                                                    <input type="checkbox" id="agresividad" name="agresividad"/>
                                                </div>
                                            </div>

                                            <div className="owner-info">
                                                <h4>Dueño</h4>
                                                <div className="owner-search">
                                                    <input type="text" id="owner-search" placeholder="(FALTA DESARROLLAR) Buscar por DNI o número de documento"/>
                                                    <button type="button" className="search-btn">Mostrar información del dueño</button>
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