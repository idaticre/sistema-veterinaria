import { useEffect, useState } from 'react'
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa';
import IST from '../../../../components/proteccion/IST';
import Swal from 'sweetalert2';
import type { auditoriaResponse } from '../../../../components/interfaces/interfaces';
import './auditoria.css';

function Auditoria() {
    const [minimizado, setMinimizado] = useState(false);
    const [registros, setRegistros] = useState<auditoriaResponse[]>([]);
    const [busqueda, setBusqueda] = useState("");
    const [filtrados, setFiltrados] = useState<auditoriaResponse[]>([]);

    useEffect(() => {
        IST.get("/auditoria")
        .then(res => {
            const data = Array.isArray(res.data) ? res.data : (res.data.data || []);
            setRegistros(data);
            setFiltrados(data);
        })
        .catch( () => {
            Swal.fire({
                title: "Error desconocido",
                text: "Por favor refresque la página",
                icon: "error"
            });
        });
    }, []);

    useEffect(() => {
        if (!registros) return;
        const query = busqueda.toLowerCase().trim();
        if (!query) {
            setFiltrados(registros);
            return;
        }
        const resultado = registros.filter(reg => {
            const username = reg.usuario?.username?.toLowerCase() || "";
            const accion = reg.tipoAccion?.nombre?.toLowerCase() || "";
            const entidad = reg.entidad?.toLowerCase() || "";
            const desc = reg.descripcion?.toLowerCase() || "";
            return username.includes(query) || 
                   accion.includes(query) || 
                   entidad.includes(query) || 
                   desc.includes(query);
        });
        setFiltrados(resultado);
    }, [busqueda, registros]);

    const formatFecha = (fechaStr: string | null) => {
        if (!fechaStr) return 'Sin fecha';
        try {
            const date = new Date(fechaStr);
            if (isNaN(date.getTime())) return fechaStr;
            
            const dia = String(date.getDate()).padStart(2, '0');
            const mes = String(date.getMonth() + 1).padStart(2, '0');
            const año = date.getFullYear();
            const hora = String(date.getHours()).padStart(2, '0');
            const minutos = String(date.getMinutes()).padStart(2, '0');
            const seconds = String(date.getSeconds()).padStart(2, '0');
            
            return `${dia}/${mes}/${año} ${hora}:${minutos}:${seconds}`;
        } catch {
            return fechaStr;
        }
    };

    return (
        <>
            <div id='auditoria'>
                <Br_administrativa onMinimizeChange={setMinimizado} />
                <main className={minimizado ? "minimize" : ""}>
                    <section id="lst_auditoria">
                        <div id="encabezado">
                            <h2>Registro de Auditoría</h2>
                        </div>
                        <div id="buscador">
                            <div id="br_buscador">
                                <input
                                    type="text"
                                    placeholder="Buscar por usuario, acción, entidad o descripción... 🔍"
                                    value={busqueda}
                                    onChange={(e) => setBusqueda(e.target.value)}
                                />
                            </div>
                        </div>
                        <div id="lista_auditoria">
                            <table>
                                <thead>
                                    <tr>
                                        <th style={{ width: '60px' }}>ID</th>
                                        <th>Usuario</th>
                                        <th>Acción</th>
                                        <th>Entidad</th>
                                        <th style={{ width: '40%' }}>Descripción</th>
                                        <th>Fecha y Hora</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {filtrados.length > 0 ? (
                                        filtrados.map((registro) => (
                                            <tr key={registro.id}>
                                                <td>{registro.id}</td>
                                                <td>
                                                    <span className="usuario-badge">
                                                        {registro.usuario?.username || 'N/A'}
                                                    </span>
                                                </td>
                                                <td>
                                                    <span className={`accion-badge ${(registro.tipoAccion?.nombre || '').toLowerCase()}`}>
                                                        {registro.tipoAccion?.nombre || 'N/A'}
                                                    </span>
                                                </td>
                                                <td>
                                                    <span className="entidad-badge">
                                                        {registro.entidad || 'N/A'}
                                                    </span>
                                                </td>
                                                <td className="desc-celda">{registro.descripcion || 'N/A'}</td>
                                                <td>{formatFecha(registro.fecha)}</td>
                                            </tr>
                                        ))
                                    ) : (
                                        <tr>
                                            <td colSpan={6} className="sin-registros">
                                                No se encontraron registros de auditoría.
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </section>
                </main>
            </div>
        </>
    )
}

export default Auditoria