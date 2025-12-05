import{ useEffect, useState } from 'react'
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa';
import type { CitaResponse, HistorialCResponse, MascotaResponse } from '../../../../components/interfaces/interfaces';
import IST from '../../../../components/proteccion/IST';
import { Link, useLocation } from 'react-router-dom';
import "./historialM.css"

type Mascotaextendido = MascotaResponse & { nombre_dueño?: string; nombre_raza?: string; nombre_especie?: string;
   nombre_estado?: string; nombre_tamaño?: string; nombre_etapa?: string };

function HistorialM() {
    const [minimizado, setMinimizado] = useState(false);
    const location = useLocation();
    const mascotaHS = location.state?.mascotaSeleccionado as Mascotaextendido | undefined;
    const [historiaClinica, setHistoriaClinica] = useState<HistorialCResponse | null>(null);
    const [citas, setCitas] = useState<CitaResponse[]>([]);
    const [fechaFiltro, setFechaFiltro] = useState<string>("");

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

            setCitas(concluidos);
        })
        .catch(err => {
            console.error("Error en la carga de datos", err);
        });
    })

    const citasMascota = mascotaHS? citas.filter(cita => cita.idMascota == mascotaHS?.id) : [];
    const citasFiltradas = fechaFiltro? citasMascota.filter(cita => cita.fechaRegistro.startsWith(fechaFiltro)) : citasMascota;

    return (
        <div className='historialM'>
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? 'minimize' : ''}>
                <section className='historiales'>
                    <Link className='boton_retorno' to="/administracion/mascotas/lista"><i className="fa-solid fa-backward"></i></Link>
                    <div className='historial_encabezado'>
                        <h3>HISTORIAL: {historiaClinica?.codigo}</h3>
                        <div className='datos_encabezado_historial'>
                            <p>Fecha de apertura: <span>{historiaClinica?.fechaApertura}</span></p>
                        </div>
                        <div className='datos_encabezado_mascota'>
                            <p>Mascota: <span>{mascotaHS?.nombre}</span></p>
                            <p>Dueño: <span>{mascotaHS?.nombre_dueño}</span></p>
                        </div>
                    </div>
                    <div className='filtro_historial'>
                        <input type="date" value={fechaFiltro} onChange={(e) => setFechaFiltro(e.target.value)}/>
                    </div>
                    <div className='citas_mascota'>
                        {citasFiltradas.length === 0? (
                            <p>sin citas a su nombre</p>
                        ):(
                            citasFiltradas.map((CMT) =>(
                                <>
                                    <div className='cita' key={CMT.id}>
                                        <div className='citas_mascota_encabezado'>
                                            <p>{CMT.observaciones} </p><span>{new Date(CMT.fechaRegistro).toLocaleDateString("es-PE")}</span>
                                        </div>
                                        <div className='citas_mascota_datos'>
                                            <p>Codigo: <span>{CMT.codigo}</span></p>
                                            <p>Hora: <span>{CMT.fechaRegistro.split("T")[1].slice(0,5)}</span></p>
                                            <p>Monto total: {CMT.totalCita}</p>
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