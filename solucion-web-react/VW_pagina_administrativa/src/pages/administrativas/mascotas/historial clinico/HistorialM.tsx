import{ useEffect, useState } from 'react'
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa';
import type { CitaResponse, HistorialCResponse, MascotaResponse } from '../../../../components/interfaces/interfaces';
import IST from '../../../../components/proteccion/IST';
import { Link, useLocation } from 'react-router-dom';
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import "./historialM.css"

type Mascotaextendido = MascotaResponse & { nombre_dueño?: string; nombre_raza?: string; nombre_especie?: string;
   nombre_estado?: string; nombre_tamaño?: string; nombre_etapa?: string };

function HistorialM() {
    const [minimizado, setMinimizado] = useState(false);
    const location = useLocation();
    const mascotaHS = location.state?.mascotaSeleccionado as Mascotaextendido | undefined;
    const [historiaClinica, setHistoriaClinica] = useState<HistorialCResponse | null>(null);
    const [citas, setCitas] = useState<CitaResponse[]>([]);
    const [rangoFechas, setRangoFechas] = useState<[Date | null, Date | null]>([null, null]);

    useEffect(() => {
        IST.get(`/historia-clinica/${mascotaHS?.id}`)
        .then(res => {
            console.log("datos de historial:", res.data);
            setHistoriaClinica(res.data.data);
        })
        .catch(err => {
            console.error("Error en la carga de datos", err);
        });
    }, [mascotaHS]);

    useEffect(() => {
        IST.get("/agenda?page=0&size=10")
        .then(res => {
            const citasRegist = res.data.data.content;
            
            const concluidos = citasRegist.filter((cita: CitaResponse) => cita.idEstado === 5);
            console.log("datos de historial:", res.data.data.content);
            setCitas(concluidos);
        })
        .catch(err => {
            console.error("Error en la carga de datos", err);
        });
    }, []);

    const citasMascota = mascotaHS? citas.filter(cita => cita.idMascota == mascotaHS?.id) : [];
    const citasFiltradas = rangoFechas[0]? citasMascota.filter(cita => {
        const fechaCita = new Date(cita.fechaRegistro);

        const citaDia = new Date(fechaCita.getFullYear(), fechaCita.getMonth(), fechaCita.getDate());
        const inicioDia = new Date(rangoFechas[0]!.getFullYear(), rangoFechas[0]!.getMonth(), rangoFechas[0]!.getDate());
        const finDia = rangoFechas[1]
            ? new Date(rangoFechas[1]!.getFullYear(), rangoFechas[1]!.getMonth(), rangoFechas[1]!.getDate())
            : inicioDia; 

        return citaDia >= inicioDia && citaDia <= finDia;
    }): citasMascota;


    return (
        <div className='historialM'>
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? 'minimize' : ''}>
                <section className='historiales'>
                    <Link className='boton_retorno' to="/administracion/mascotas/lista"><i className="fa-solid fa-backward"></i></Link>
                    <div className="historial_encabezado">
                        <div className="datos_historial">
                            <h3>HISTORIAL CLÍNICO: {historiaClinica?.codigo}</h3>
                            <div className='DT_info'>
                                <div className='DT_info_cas'>
                                    <div className="DT_info_i">
                                        <p>Nombre: </p>
                                        <span>{mascotaHS?.nombre}</span>
                                    </div>
                                    <div className="DT_info_i">
                                        <p>Código: </p><span>{mascotaHS?.codigo}</span>
                                    </div>
                                    <div className="DT_info_i">
                                        <p>Fecha de apertura:</p>
                                        <span>{historiaClinica?.fechaApertura}</span>
                                    </div>
                                    <div className="DT_info_i">
                                        <p>Dueño: </p>
                                        <span>{mascotaHS?.nombre_dueño}</span>
                                    </div>
                                    <div className="DT_info_i">
                                        <p>Sexo:</p>
                                        <span>{mascotaHS?.sexo == 'M'? 'macho': 'hembra'}</span>
                                    </div>
                                    <div className="DT_info_i">
                                        <p>Raza: </p>
                                        <span>{mascotaHS?.nombre_raza}</span>
                                    </div>
                                </div>
                                <div className='DT_info_cas'>
                                    <div className="DT_info_i">
                                        <p>tamaño: </p>
                                        <span>{mascotaHS?.nombre_tamaño}</span>
                                    </div>
                                    <div className="DT_info_i">
                                        <p>Etapa: </p>
                                        <span>{mascotaHS?.nombre_etapa}</span>
                                    </div>
                                    <div className="DT_info_i">
                                        <p>castrado: </p>
                                        <span>{mascotaHS?.esterilizado? '✓':'✕'}</span>
                                    </div>
                                    <div className="DT_info_i">
                                        <p>Factor Dea:</p>
                                        <span>{mascotaHS?.factorDea? '✓':'✕'}</span>
                                    </div>
                                    <div className="DT_info_i">
                                        <p>Pedigree: </p>
                                        <span>{mascotaHS?.pedigree? '✓':'✕'}</span>
                                    </div>
                                    <div className="DT_info_i">
                                        <p>Alergico: </p>
                                        <span>{mascotaHS?.alergias}</span>
                                    </div>
                                </div>
                            </div>
                        </div>        
                        <div className='DT_foto'>
                            <img src={mascotaHS?.foto} alt="" />
                        </div>
                    </div>
                    <div className='filtro_historial'>
                        <DatePicker
                            selectsRange
                            startDate={rangoFechas[0]}
                            endDate={rangoFechas[1]}
                            onChange={(update) => setRangoFechas(update)}
                            isClearable={true}
                            className='ftr_fechas'
                            dateFormat="yyyy/MM/dd"
                            placeholderText="Fecha inicio - Fecha final"
                        />
                        {citasFiltradas.length === 0? (''):(
                            <>
                                <p>Citas encontras ({citasFiltradas.length})</p>
                            </>
                        )}
                    </div>
                    <div className='citas_mascota'>
                        {citasFiltradas.length === 0? (
                            <p>No hay registros</p>
                        ):(
                            citasFiltradas.map((CMT) =>(
                                <>
                                    <div className='cita' key={CMT.id}>
                                        <div className='citas_mascota_encabezado'>
                                            <p>servicio{/*CMT.observaciones*/} </p><span>{new Date(CMT.fechaRegistro).toLocaleDateString("es-PE")}</span>
                                        </div>
                                        <div className='citas_mascota_datos'>
                                            <p>Codigo: <span>{CMT.codigo}</span></p>
                                            <p>Hora: <span>{CMT.fechaRegistro.split("T")[1].slice(0,5)}</span></p>
                                            <p>Tiempo: <span>{CMT.duracionEstimadaMin} min</span></p>
                                            <p>Monto total: {CMT.totalCita}</p>
                                            <p>Veterinario: Nombre colaborador</p>
                                        </div>
                                    </div>
                                </>
                            ))
                        )}
                    </div>
                </section>
            </main>
        </div>
    )
}

export default HistorialM