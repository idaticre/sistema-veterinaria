import { useEffect, useState } from 'react';
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa';
import "./reportesEinformes.css";
import type { ClienteResponse, MascotaResponse } from "../../../components/interfaces/interfaces";
import IST from '../../../components/proteccion_momentanea/IST';
import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';

pdfMake.vfs = pdfFonts.vfs; 

type MascotaExtendido = MascotaResponse & { 
    nombre_dueño?   : string;
    nombre_raza?    : string; 
    nombre_especie? : string;
    nombre_estado?  : string; 
    nombre_tamaño?  : string; 
    nombre_etapa?   : string
}

const GestionarColaboradores: React.FC = () => {
    const [minimizado, setMinimizado] = useState(false);
    const [clientes, setClientes] = useState<ClienteResponse[]>([]);
    const [mascotas, setMascotas] = useState<MascotaExtendido[]>([]);
    const [clienteSeleccionado, setClienteSeleccionado] = useState<number | null>(null);
    const [incluirMascotas, setIncluirMascotas] = useState(false);
    const [datosReporte, setDatosReporte] = useState<any>(null);

    // Listar clientes
    useEffect(() => {listarClientes();}, []);
    const listarClientes = async () => {
        try {
            const respuesta = await IST.get(`/clientes`);
            const lista = Array.isArray(respuesta.data)
            ? respuesta.data
            : respuesta.data.data;

            const clientesActivos = lista.filter((cliente: ClienteResponse) => cliente.activo === true);
            setClientes(clientesActivos);
        } catch (error) {console.error("Error al obtener los clientes", error);}
    };

    // Listar mascotas
    useEffect(() => {listarMascotas();}, []);
    const listarMascotas = async () => {
        try {
            const respuesta = await IST.get(`/mascotas`);
            const lista = Array.isArray(respuesta.data)
            ? respuesta.data
            : respuesta.data.data;
            setMascotas(lista);
        } catch (error) {console.error("Error al obtener las mascotas", error);}
    };

    // Cargar datos, frfr
    const cargarDatos = async () => {
    if (!clienteSeleccionado) {
        alert("Seleccione un cliente válido.");
        return;
    }
    try {
        const resCliente = await IST.get(`/clientes/${clienteSeleccionado}`);
        const cliente = resCliente.data.data || resCliente.data;
        let mascotasCliente: MascotaExtendido[] = [];
        if (incluirMascotas) {mascotasCliente = mascotas.filter((m) => m.idCliente === clienteSeleccionado);}
        setDatosReporte({
            cliente,
            mascotas: mascotasCliente,
        });
        console.log("Datos cargados para el reporte:", {cliente, mascotasCliente,});

    } catch (error) {console.error("Error cargando datos del reporte:", error);}
    };

    const generarPDF = (datosReporte: any) => {
        if (!datosReporte) {
            alert("No hay datos cargados para generar el PDF");
            return;
        }

        const { cliente, mascotas } = datosReporte;

        // ==== Secciones ====

        const datosCliente = Object.entries(cliente).map(([campo, valor]) => {
            return `${campo}: ${valor}`;
        });

        let datosMascotas: string[] = [];

        if (mascotas && mascotas.length > 0) {
            mascotas.forEach((m: MascotaExtendido, i: any) => {
            datosMascotas.push(`--- Mascota ${i + 1} ---`);
            Object.entries(m).forEach(([campo, valor]) => {
                datosMascotas.push(`${campo}: ${valor}`);
            });
            });
        }

        // ==== Definición del PDF ====

        const docDefinition = {
            content: [
                { text: "Reporte del Cliente", style: "titulo" },
                "\n",

                { text: "Información del Cliente", style: "subtitulo" },
                {
                    ul: datosCliente,
                },

                "\n",

                mascotas?.length > 0
                    ? { text: "Mascotas", style: "subtitulo" }
                    : "",

                mascotas?.length > 0
                    ? {
                        ul: datosMascotas,
                    }
                    : "",
            ],

            styles: {
                titulo: {
                    fontSize: 18,
                    bold: true,
                    alignment: "center" as const,
                },
                subtitulo: {
                    fontSize: 14,
                    bold: true,
                    margin: [0, 10, 0, 5] as [number, number, number, number],
                },
            },
        };

        // ==== Abrir en nueva pestaña (vista previa) ====
        pdfMake.createPdf(docDefinition).open();
        // Para descargar directamente sería:
        // pdfMake.createPdf(docDefinition).download("reporte.pdf");
    };

    return (
        <div id="cuerpo-main">
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? "minimize" : ""}>
                <section id="cuerpo">
                    <select value={clienteSeleccionado ?? ""} onChange={(cliente) => {const val = cliente.target.value; setClienteSeleccionado(val ? Number(val) : null);}}>
                        <option value="">Seleccione un cliente</option>
                        {clientes.map((cliente) => (<option key={cliente.id} value={cliente.id}>{cliente.nombre}</option>))}
                    </select>
                    <label><input type="checkbox" checked={incluirMascotas} onChange={(e) => setIncluirMascotas(e.target.checked)}/>Incluir mascotas</label>
                    <button onClick={cargarDatos}>Cargar datos</button>
                    <button onClick={() => generarPDF(datosReporte)} disabled={!datosReporte}>Generar PDF</button>
                </section>
            </main>
        </div>
    )
}
export default GestionarColaboradores;