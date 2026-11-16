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
import Swal from 'sweetalert2';

pdfMake.vfs = pdfFonts.vfs; 

interface DetalleVenta {
    descripcion: string;
    cantidad: number;
    precioUnitario: number;
    subtotal: number;
    igv: number;
    total: number;
}

interface ComprobanteVenta {
    id: number;
    serie: string;
    numero: number;
    fechaEmision: string;
    tipoComprobante: number;      // 1 = Factura, 2 = Boleta
    nombreCliente: string;
    clienteNumeroDoc: string | null;
    totalGravada: number;
    totalIGV: number;
    totalAnticipio: number;
    total: number;
    pdfurlNubefact: string | null;
    detalles: DetalleVenta[];
}

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

    // Reporte de ventas
    const [tipoVenta, setTipoVenta] = useState("0"); // 0 = todos, 1 = factura, 2 = boleta
    const [fechaDesde, setFechaDesde] = useState("");
    const [fechaHasta, setFechaHasta] = useState("");
    const [incluirDetalle, setIncluirDetalle] = useState(false);
    const [excluirRechazados, setExcluirRechazados] = useState(true);
    const [datosVentas, setDatosVentas] = useState<ComprobanteVenta[] | null>(null);

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
        } catch (error) {
            Swal.fire({
                title: "Error desconocido",
                text: "refresque la página por favor",
                icon: "error"
            });
        }
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
        } catch (error) {
            Swal.fire({
                title: "Error desconocido",
                text: "refresque la página por favor",
                icon: "error"
            });
        }
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
        } catch (error) {
            Swal.fire({
                title: "Error desconocido",
                text: "refresque la página por favor",
                icon: "error"
            });
        }
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
        } catch (error) {
            Swal.fire({
                title: "Error desconocido",
                text: "refresque la página por favor",
                icon: "error"
            });
        }
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
        Swal.fire({
            title: "Alerta",
            text: "Selecciona un cliente válido",
            icon: "warning"
        });
        return;
    }
    try {
        const resCliente = await IST.get(`/clientes/${clienteSeleccionado}`);
        const cliente = resCliente.data.data || resCliente.data;
        let mascotasCliente: MascotaExtendido[] = [];
        if (incluirMascotas) {mascotasCliente = mascotas.filter((m) => m.cliente?.id === clienteSeleccionado);}
        setDatosReporte({
            cliente,
            mascotas: mascotasCliente,
        });
        Swal.fire({
            title: "Datos cargados",
            text: "Ahora puede generar un PDF",
            icon: "success"
        });

    } catch (error) {
        Swal.fire({
            title: "Error",
            text: "cargando datos del reporte, intente de nuevo",
            icon: "error"
        });
    }
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
        if (!datosReporte) { 
            Swal.fire({
                title: "Alerta",
                text: "no hay datos cargados para generar un PDF",
                icon: "warning"
            });
            return; 
        }
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


    // ===== REPORTE DE VENTAS =====
    const money = (n: number | null | undefined) => `S/ ${Number(n || 0).toFixed(2)}`;

    const fmtFecha = (f: string) => {
        if (!f) return "-";
        const [y, m, d] = f.split("-");
        return `${d}/${m}/${y}`;
    };

    const cargarVentas = async () => {
        if (fechaDesde && fechaHasta && fechaDesde > fechaHasta) {
            Swal.fire({
                title: "Alerta",
                text: "La fecha inicial no puede ser mayor a la final",
                icon: "warning"
            });
            return;
        }

        try {
            const url = tipoVenta === "0" ? `/comprobantes` : `/comprobantes/tipo/${tipoVenta}`;
            const respuesta = await IST.get(url);
            const lista: ComprobanteVenta[] = Array.isArray(respuesta.data)
                ? respuesta.data
                : respuesta.data.data ?? [];

            const filtrada = lista
                .filter((c) => !fechaDesde || c.fechaEmision >= fechaDesde)
                .filter((c) => !fechaHasta || c.fechaEmision <= fechaHasta)
                .filter((c) => !excluirRechazados || !!c.pdfurlNubefact)
                .sort((a, b) => a.fechaEmision.localeCompare(b.fechaEmision) || a.id - b.id);

            if (filtrada.length === 0) {
                setDatosVentas(null);
                Swal.fire({
                    title: "Sin resultados",
                    text: "No hay comprobantes con esos filtros",
                    icon: "info"
                });
                return;
            }

            setDatosVentas(filtrada);
            Swal.fire({
                title: "Datos cargados",
                text: `${filtrada.length} comprobante(s). Ahora puede generar el PDF`,
                icon: "success"
            });
        } catch (error) {
            setDatosVentas(null);
            Swal.fire({
                title: "Error",
                text: "cargando las ventas, intente de nuevo",
                icon: "error"
            });
        }
    };

    const generarPDFVentas = () => {
        if (!datosVentas || datosVentas.length === 0) {
            Swal.fire({
                title: "Alerta",
                text: "no hay ventas cargadas para generar un PDF",
                icon: "warning"
            });
            return;
        }

        const sumar = (lista: ComprobanteVenta[], campo: "totalGravada" | "totalIGV" | "total") =>
            lista.reduce((acc, c) => acc + Number(c[campo] || 0), 0);

        const facturas = datosVentas.filter((c) => Number(c.tipoComprobante) === 1);
        const boletas = datosVentas.filter((c) => Number(c.tipoComprobante) === 2);

        const th = (t: string, alignment: string = "left") =>
            ({ text: t, style: "th", alignment });
        const right = (t: string, bold = false) => ({ text: t, alignment: "right", bold });

        // ---- Tabla principal ----
        const filas = datosVentas.map((c) => [
            Number(c.tipoComprobante) === 1 ? "Factura" : "Boleta",
            `${c.serie}-${c.numero}`,
            fmtFecha(c.fechaEmision),
            c.nombreCliente ?? "-",
            c.clienteNumeroDoc ?? "-",
            right(money(c.totalGravada)),
            right(money(c.totalIGV)),
            right(money(c.total)),
        ]);

        const tablaPrincipal = {
            table: {
                headerRows: 1,
                widths: [50, 75, 55, "*", 65, 65, 55, 65],
                body: [
                    [
                        th("Tipo"), th("Número"), th("Fecha"), th("Cliente"), th("Documento"),
                        th("Subtotal", "right"), th("IGV", "right"), th("Total", "right"),
                    ],
                    ...filas,
                    [
                        { text: "TOTAL GENERAL", colSpan: 5, bold: true, alignment: "right" }, {}, {}, {}, {},
                        right(money(sumar(datosVentas, "totalGravada")), true),
                        right(money(sumar(datosVentas, "totalIGV")), true),
                        right(money(sumar(datosVentas, "total")), true),
                    ],
                ],
            },
            layout: "lightHorizontalLines",
            fontSize: 8,
        };

        // ---- Resumen por tipo ----
        const resumen = {
            table: {
                widths: [100, 60, 90],
                body: [
                    [th("Tipo"), th("Cantidad", "right"), th("Total", "right")],
                    ["Facturas", right(String(facturas.length)), right(money(sumar(facturas, "total")))],
                    ["Boletas", right(String(boletas.length)), right(money(sumar(boletas, "total")))],
                    [
                        { text: "Total", bold: true },
                        right(String(datosVentas.length), true),
                        right(money(sumar(datosVentas, "total")), true),
                    ],
                ],
            },
            layout: "lightHorizontalLines",
            fontSize: 9,
        };

        // ---- Detalle opcional ----
        const bloqueDetalle: any[] = [];
        if (incluirDetalle) {
            bloqueDetalle.push({ text: "Detalle de comprobantes", style: "subtitulo", pageBreak: "before" });
            datosVentas.forEach((c) => {
                bloqueDetalle.push({
                    text: `${Number(c.tipoComprobante) === 1 ? "Factura" : "Boleta"} ${c.serie}-${c.numero}  |  ${fmtFecha(c.fechaEmision)}  |  ${c.nombreCliente ?? "-"}`,
                    bold: true,
                    fontSize: 9,
                    margin: [0, 8, 0, 3],
                });
                bloqueDetalle.push({
                    table: {
                        headerRows: 1,
                        widths: ["*", 40, 70, 70, 60, 70],
                        body: [
                            [
                                th("Descripción"), th("Cant.", "right"), th("P. Unit.", "right"),
                                th("Subtotal", "right"), th("IGV", "right"), th("Total", "right"),
                            ],
                            ...(c.detalles ?? []).map((d) => [
                                d.descripcion,
                                right(String(Number(d.cantidad))),
                                right(money(d.precioUnitario)),
                                right(money(d.subtotal)),
                                right(money(d.igv)),
                                right(money(d.total)),
                            ]),
                        ],
                    },
                    layout: "lightHorizontalLines",
                    fontSize: 8,
                });
            });
        }

        const tipoTexto = tipoVenta === "0" ? "Todos" : tipoVenta === "1" ? "Facturas" : "Boletas";
        const rango =
            fechaDesde || fechaHasta
                ? `${fechaDesde ? fmtFecha(fechaDesde) : "inicio"} al ${fechaHasta ? fmtFecha(fechaHasta) : "hoy"}`
                : "Todas las fechas";

        const docDefinition: any = {
            pageOrientation: "landscape",
            pageMargins: [30, 30, 30, 40],
            content: [
                { text: "VETERINARIA MANADA WOOF", style: "empresa" },
                { text: "Reporte de Ventas", style: "titulo" },
                {
                    text: `Tipo: ${tipoTexto}   |   Periodo: ${rango}   |   Generado: ${new Date().toLocaleString("es-PE")}`,
                    alignment: "center",
                    fontSize: 9,
                    margin: [0, 0, 0, 12],
                },
                { text: "Resumen", style: "subtitulo" },
                resumen,
                { text: "Comprobantes", style: "subtitulo", margin: [0, 14, 0, 5] },
                tablaPrincipal,
                ...bloqueDetalle,
            ],
            footer: (currentPage: number, pageCount: number) => ({
                text: `Página ${currentPage} de ${pageCount}`,
                alignment: "center",
                fontSize: 8,
            }),
            styles: {
                empresa: { fontSize: 11, bold: true, alignment: "center" },
                titulo: { fontSize: 18, bold: true, alignment: "center", margin: [0, 2, 0, 4] },
                subtitulo: { fontSize: 13, bold: true, margin: [0, 8, 0, 5] },
                th: { bold: true, fillColor: "#001C3D", color: "white", fontSize: 8 },
            },
        };

        const hoy = new Date().toISOString().split("T")[0];
        pdfMake.createPdf(docDefinition).download(`reporte_ventas_${hoy}.pdf`);
    };

    return (
        <div id="cuerpo-main">
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? "minimize" : ""}>
                <div className='Reporte'>
                    <section id="cuerpo">
                        <div className="diva2">
                            <h3>Cómo generar un reporte</h3>
                            <ol>
                                <li>Seleccione un cliente (si quiere incluir sus mascotas en el reporte, de click en la casilla)</li>
                                <li>De click en el botón "Cargar datos". Si los datos fueron cargados exitosamente, el sistema se lo hará saber</li>
                                <li>De click en generar PDF. Una ventana nueva se abrirá</li>
                                <li>De click al botón de descargar en la esquina superior derecha para descargar el PDF</li>
                            </ol>
                        </div>
                        <div className="reportes-fila">
                            {/* ===== REPORTE DE CLIENTES (sin cambios) ===== */}
                            <div className="diva">
                                <div className="selecciones">
                                    <select className="combobo" value={clienteSeleccionado ?? ""} onChange={(cliente) => {const val = cliente.target.value; setClienteSeleccionado(val ? Number(val) : null);}}>
                                        <option value="">Seleccione un cliente</option>
                                        {clientes.map((cliente) => (<option key={cliente.id} value={cliente.id}>{cliente.nombre}</option>))}
                                    </select>
                                    <label className="checkbobo"><input type="checkbox" checked={incluirMascotas} onChange={(e) => setIncluirMascotas(e.target.checked)}/>Incluir mascotas</label>
                                </div>
                                <div className="botones-pdf">
                                    <button onClick={cargarDatos} className='amarillo'>Cargar datos</button>
                                    <button onClick={() => generarPDF(datosReporte)} disabled={!datosReporte} className='verde'>Generar PDF</button>
                                </div>
                            </div>

                            {/* ===== REPORTE DE VENTAS ===== */}
                            <div className="diva diva-ventas">
                                <h3 className="titulo-reporte">Reporte de ventas</h3>
                                <div className="selecciones">
                                    <select className="combobo" value={tipoVenta} onChange={(e) => { setTipoVenta(e.target.value); setDatosVentas(null); }}>
                                        <option value="0">Todos los comprobantes</option>
                                        <option value="1">Solo facturas</option>
                                        <option value="2">Solo boletas</option>
                                    </select>
                                </div>
                                <div className="selecciones fechas-ventas">
                                    <label className="label-fecha">Desde
                                        <input type="date" value={fechaDesde} onChange={(e) => { setFechaDesde(e.target.value); setDatosVentas(null); }} />
                                    </label>
                                    <label className="label-fecha">Hasta
                                        <input type="date" value={fechaHasta} onChange={(e) => { setFechaHasta(e.target.value); setDatosVentas(null); }} />
                                    </label>
                                </div>
                                <label className="checkbobo">
                                    <input type="checkbox" checked={incluirDetalle} onChange={(e) => setIncluirDetalle(e.target.checked)} />
                                    Incluir detalle de servicios
                                </label>
                                <label className="checkbobo">
                                    <input type="checkbox" checked={excluirRechazados} onChange={(e) => { setExcluirRechazados(e.target.checked); setDatosVentas(null); }} />
                                    Excluir rechazados (sin PDF)
                                </label>
                                <div className="botones-pdf">
                                    <button onClick={cargarVentas} className='amarillo'>Cargar ventas</button>
                                    <button onClick={generarPDFVentas} disabled={!datosVentas} className='verde'>Descargar PDF</button>
                                </div>
                            </div>
                        </div>
                    </section>
                </div>    
            </main>
        </div>
    )
}
export default GestionarColaboradores;