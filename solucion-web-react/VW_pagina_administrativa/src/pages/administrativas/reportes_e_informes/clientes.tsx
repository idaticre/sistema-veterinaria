import { useEffect, useState } from 'react';
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa';
import "./reportesEinformes.css";
import type { ClienteResponse,
    MascotaResponse,
    tipo_doc,
    TipoPersonaJuridica,
    Razas,
    Tamaño_Mascota,
    Etapa_Mascota,
    Estado_Mascota
 } from "../../../components/interfaces/interfaces";
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
    idTipoPersonaJuridica: "Tipo de persona",
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

const tipoDocsArray: tipo_doc[] = [
  { id: 1, descripcion: "DNI", activo: true },
  { id: 2, descripcion: "RUC", activo: true },
  { id: 3, descripcion: "Carné de extranjería", activo: true },
  { id: 4, descripcion: "P. Nac.", activo: true },
  { id: 5, descripcion: "Pasaporte", activo: true },
  { id: 6, descripcion: "Otro", activo: true },
];
const tipoDocsMap = tipoDocsArray.reduce((a, t) => (a[t.id] = t.descripcion, a), {} as Record<number,string>);
const obtenerDescripcionTipoDoc = (id?: number | null) => id == null ? "Desconocido" : tipoDocsMap[id] ?? `ID ${id}`;

const tipoPersonaJuridicaArray: TipoPersonaJuridica[] = [
  { id: 1, nombre: "Natural", descripcion: "Persona natural que representa una entidad individual", activo: true },
  { id: 2, nombre: "Jurídica", descripcion: "Entidad jurídica con existencia legal y RUC propio", activo: true },
];
const tipoPersonaJuridicaMap: Record<number, string> = tipoPersonaJuridicaArray.reduce((acc, item) => {acc[item.id] = item.nombre; return acc;}, {} as Record<number, string>);
const obtenerDescripcionTipoPersonaJuridica = (id?: number | null) => {
  if (id == null) return "Desconocido";
  return tipoPersonaJuridicaMap[id] ?? `ID ${id}`;
};

const estadoMascotaArray: Estado_Mascota[] = [
    { id: 1, nombre: "ACTIVA", decripcion: "Mascota con atención vigente en la veterinaria.", activo: true },
    { id: 2, nombre: "EN TRATAMIENTO", decripcion: "En tratamiento médico o quirúrgico.", activo: true },
    { id: 3, nombre: "RECUPERADA", decripcion: "Ha finalizado su tratamiento con éxito.", activo: true },
    { id: 4, nombre: "EN OBSERVACIÓN", decripcion: "Bajo control o evaluación médica.", activo: true },
    { id: 5, nombre: "EN CIRUGÍA", decripcion: "Actualmente en procedimiento quirúrgico.", activo: true },
    { id: 6, nombre: "EN REHABILITACIÓN", decripcion: "En terapia física o recuperación postoperatoria.", activo: true },
    { id: 7, nombre: "CRÓNICA", decripcion: "Con enfermedad crónica de seguimiento continuo.", activo: true },
    { id: 8, nombre: "CRÍTICA", decripcion: "En estado grave o internada.", activo: true },
    { id: 9, nombre: "INACTIVA", decripcion: "Sin actividad reciente o controles pendientes.", activo: true },
    { id: 10, nombre: "TRANSFERIDA", decripcion: "Trasladada a otro propietario o centro.", activo: true },
    { id: 11, nombre: "FALLECIDA", decripcion: "Mascota registrada como fallecida.", activo: true },
    { id: 12, nombre: "EXTRAVIADA", decripcion: "Reportada como perdida.", activo: true },
    { id: 13, nombre: "EN ADOPCIÓN", decripcion: "Disponible para adopción.", activo: true },
    { id: 14, nombre: "ADOPTADA", decripcion: "Entregada en adopción.", activo: true },
    { id: 15, nombre: "RESCATADA", decripcion: "Rescatada y en evaluación inicial.", activo: true },
    { id: 16, nombre: "EN CUARENTENA", decripcion: "En aislamiento preventivo.", activo: true },
    { id: 17, nombre: "EN ACOGIDA TEMPORAL", decripcion: "En hogar temporal de acogida.", activo: true },
    { id: 18, nombre: "DEVUELTA", decripcion: "Mascota devuelta tras adopción fallida.", activo: true }
]
const estadoMascotaMap: Record<number, string> = estadoMascotaArray.reduce((acc, est) => {acc[est.id] = est.nombre.trim(); return acc;}, {} as Record<number, string>);
const getEstadoMascotaNombre = (id?: number | null ) => {
    if (id == null) return "Desconocido";
    return estadoMascotaMap[id] ?? `ID ${id}`;
};

const tamañoMascotaArray: Tamaño_Mascota[] = [
    { id: 1, tamaño: "XS", descripcion: "MUY PEQUEÑO", activo: true },
    { id: 2, tamaño: "S", descripcion: "PEQUEÑO", activo: true },
    { id: 3, tamaño: "M", descripcion: "MEDIANO", activo: true },
    { id: 4, tamaño: "L", descripcion: "GRANDE", activo: true },
    { id: 5, tamaño: "XL", descripcion: "MUY GRANDE", activo: true }
]
const tamañoMascotaMap: Record<number, string> = tamañoMascotaArray.reduce((acc, t) => {acc[t.id] = t.descripcion.trim(); return acc;}, {} as Record<number, string>);
const obtenerDescripcionTamaño = (id?: number | null) => {if (id == null) return "Desconocido"; return tamañoMascotaMap[id] ?? `ID ${id}`;};

const etapaMascotaArray: Etapa_Mascota[] = [
    {id: 1, descripcion: "CACHORRO", activo: true},
    {id: 2, descripcion: "JOVEN", activo: true},
    {id: 3, descripcion: "ADULTO", activo: true},
    {id: 4, descripcion: "SENIOR", activo: true}
]
const etapaMascotaMap: Record<number, string> = etapaMascotaArray.reduce((acc, t) => {acc[t.id] = t.descripcion.trim(); return acc;}, {} as Record<number, string>);
const obtenerDescripcionEtapa = (id?: number | null) => {if (id == null) return "Desconocido"; return etapaMascotaMap[id] ?? `ID ${id}`;};

const GestionarColaboradores: React.FC = () => {
    const [minimizado, setMinimizado] = useState(false);
    const [clientes, setClientes] = useState<ClienteResponse[]>([]);
    const [mascotas, setMascotas] = useState<MascotaExtendido[]>([]);
    const [clienteSeleccionado, setClienteSeleccionado] = useState<number | null>(null);
    const [incluirMascotas, setIncluirMascotas] = useState(false);
    const [datosReporte, setDatosReporte] = useState<any>(null);
    const [razas, setRazas] = useState<Razas[]>([]);
    const [especies, setEspecies] = useState<Razas[]>([]);

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

    // Listar razas de mascotas
    useEffect(() => {listarRazas();}, []);
    const listarRazas = async () => {
        try {
            const respuesta = await IST.get(`/razas`);
            const lista = Array.isArray(respuesta.data)
            ? respuesta.data
            : respuesta.data.data;
            setRazas(lista);
        } catch (error) {console.error("Error al obtener las razas", error);}
    };
    const razasMap = razas.reduce((acc, r) => {
        acc[r.id] = r.nombre;
        return acc;
    }, {} as Record<number, string>);
    const obtenerNombreRaza = (id?: number | null) => {
        if (id == null) return "Desconocido";
        return razasMap[id] ?? `ID ${id}`;
    };

    // Listar especies de mascotas
    useEffect(() => {listarEspecies();}, []);
    const listarEspecies = async () => {
        try {
            const respuesta = await IST.get(`/especies`);
            const lista = Array.isArray(respuesta.data)
            ? respuesta.data
            : respuesta.data.data;
            setEspecies(lista);
        } catch (error) {console.error("Error al obtener las especies", error);}
    };
    const especiesMap = especies.reduce((acc, r) => {
        acc[r.id] = r.nombre;
        return acc;
    }, {} as Record<number, string>);
    const obtenerNombreEspecie = (id?: number | null) => {
        if (id == null) return "Desconocido";
        return especiesMap[id] ?? `ID ${id}`;
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

    const transformadoresEspeciales: Record<string, (v:any) => string> = {
        idTipoDocumento: (id) => obtenerDescripcionTipoDoc(Number(id)),
        idTipoPersonaJuridica: (id) => obtenerDescripcionTipoPersonaJuridica(Number(id)),
        idRaza: (id) => obtenerNombreRaza(Number(id)),
        idEspecie: (id) => obtenerNombreEspecie(Number(id)),
        idEstado: (id: number) => getEstadoMascotaNombre(id),
        idTamano: (id) => obtenerDescripcionTamaño(Number(id)),
        idEtapa: (id) => obtenerDescripcionEtapa(Number(id))
    };

    // Magia del PDF
    const generarPDF = (datosReporte: any) => {
        if (!datosReporte) { alert("No hay datos cargados para generar el PDF"); return; }
        const { cliente, mascotas } = datosReporte;

        // Parte de clientes
        const datosCliente = Object.entries(cliente)
            .filter(([campo]) => campo in camposCliente)
            .map(([campo, valor]) => {
                if (campo in transformadoresEspeciales) {
                valor = transformadoresEspeciales[campo](valor);
                } else if (typeof valor === "boolean") {
                valor = valor ? "Sí" : "No";
                }
                return `${camposCliente[campo]}: ${valor}`;
            });
                    
        // Parte de mascotas
        let datosMascotas: string[] = [];
        if (mascotas && mascotas.length > 0) {
            mascotas.forEach((m: MascotaExtendido, i: any) => {
                datosMascotas.push(`--- Mascota ${i + 1} ---`);

                Object.entries(m)
                    .filter(([campo]) => campo in camposMascota)
                    .forEach(([campo, valor]) => {

                        if (campo in transformadoresEspeciales) {
                            valor = transformadoresEspeciales[campo](valor);
                        } else if (typeof valor === "boolean") {
                            valor = valor ? "Sí" : "No";
                        }

                        datosMascotas.push(`${camposMascota[campo]}: ${valor}`);
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