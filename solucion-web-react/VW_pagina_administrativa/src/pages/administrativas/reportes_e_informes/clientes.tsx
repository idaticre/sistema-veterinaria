import { useEffect, useState } from 'react';
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa';
import "./reportesEinformes.css";
import type { ClienteResponse, MascotaResponse } from "../../../components/interfaces/interfaces";
import IST from '../../../components/proteccion/IST';
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

const camposCliente: Record<string, string> = {
    codigo: "Código de cliente",
    fechaRegistro: "Fecha de registro",
    nombre: "Nombre",
    sexo: "Sexo",
    idTipoDocumento: "Tipo de documento",
    documento: "Documento",
    idTipoPersonaJurdicia: "Tipo de persona",
    correo: "Correo",
    telefono: "Teléfono",
    direccion: "Dirección",
    ciudad: "Ciudad",
    distrito: "Distrito",
};

const camposMascota: Record<string, string> = {
    codigo: "Código de mascota",
    nombre: "Nombre",
    sexo: "Sexo",
    idRaza: "Raza",
    idEspecie: "Especie",
    idEstado: "Estado",
    idTamano: "Tamaño",
    idEtapa: "Etapa",
    fechaNacimiento: "Fecha de nacimiento",
    pelaje: "Pelaje",
    esterilizado: "Esterilizado",
    alergias: "Alergias",
    peso: "Peso",
    chip: "Chip",
    pedigree: "Pedigree",
    factorDea: "Factor dea",
    agresividad: "Agresivo",
    fechaRegistro: "Fecha de registro"
};

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
        alert("Datos cargados, ahora puede generar un PDF");

    } catch (error) {console.error("Error cargando datos del reporte:", error);}
    };

    const generarPDF = (datosReporte: any) => {
        if (!datosReporte) { alert("No hay datos cargados para generar el PDF"); return; }
        const { cliente, mascotas } = datosReporte;

        // Parte de clientes
        const datosCliente = Object.entries(cliente)
            .filter(([campo]) => campo in camposCliente)            // solo campos permitidos
            .map(([campo, valor]) => {
                const valorFinal = typeof valor === "boolean" 
                    ? (valor ? "Sí" : "No") 
                    : valor;

                return `${camposCliente[campo]}: ${valorFinal}`;
            });
        
        // Parte de mascotas
        let datosMascotas: string[] = [];
        if (mascotas && mascotas.length > 0) {
            mascotas.forEach((m: MascotaExtendido, i: any) => {
                datosMascotas.push(`--- Mascota ${i + 1} ---`);

                Object.entries(m)
                    .filter(([campo]) => campo in camposMascota)
                    .forEach(([campo, valor]) => {
                        const valorFinal = typeof valor === "boolean"
                            ? (valor ? "Sí" : "No")
                            : valor;

                        datosMascotas.push(`${camposMascota[campo]}: ${valorFinal}`);
                    });
            });
        }

        // ==== Definición del PDF ====
        const docDefinition = {
            content: [
                { text: "Reporte del Cliente", style: "titulo" },
                "\n",

                { text: "Información del Cliente", style: "subtitulo" },
                { ul: datosCliente },

                "\n",

                mascotas?.length > 0 ? { text: "Mascotas", style: "subtitulo" } : "",
                mascotas?.length > 0 ? { ul: datosMascotas } : "",
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

        pdfMake.createPdf(docDefinition).open();
    };


    return (
        <div id="cuerpo-main">
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? "minimize" : ""}>
                <section id="cuerpo">
                    <div className="diva">
                        <div className="selecciones">
                            <select className="combobo" value={clienteSeleccionado ?? ""} onChange={(cliente) => {const val = cliente.target.value; setClienteSeleccionado(val ? Number(val) : null);}}>
                                <option value="">Seleccione un cliente</option>
                                {clientes.map((cliente) => (<option key={cliente.id} value={cliente.id}>{cliente.nombre}</option>))}
                            </select>
                            <label className="checkbobo"><input type="checkbox" checked={incluirMascotas} onChange={(e) => setIncluirMascotas(e.target.checked)}/>Incluir mascotas</label>
                        </div>
                        <div className="botones-pdf"> 
                            <button onClick={cargarDatos}>Cargar datos</button>
                            <button onClick={() => generarPDF(datosReporte)} disabled={!datosReporte}>Generar PDF</button>
                        </div>
                    </div>
                </section>
            </main>
        </div>
    )
}
export default GestionarColaboradores;