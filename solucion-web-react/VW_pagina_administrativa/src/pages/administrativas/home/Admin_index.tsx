import { useEffect, useMemo, useState } from "react";
import Br_administrativa from "../../../components/barra_administrativa/Br_administrativa";
import { PieChart, Pie, Cell, Legend, ResponsiveContainer, Tooltip, BarChart, Bar, XAxis, YAxis, CartesianGrid } from 'recharts';
import "./Admin_index.css"
import type { UsuarioResponse, ClienteResponse, MascotaResponse, CitaResponse, CitaPorEstado, MascotaPorEspecie, Productos } from "../../../components/interfaces/interfaces";
import IST from "../../../components/proteccion/IST";
import Swal from 'sweetalert2';

interface ComprobanteIngreso {
    id: number;
    serie: string;
    numero: number;
    fechaEmision: string | null;
    tipoComprobante: number; // 1 = Factura, 2 = Boleta
    total: number;
    totalGravada: number;
    totalIGV: number;
    pdfurlNubefact: string | null;
}

interface CitasPorMes {
    nombre: string;
    cantidad: number;
}

const soles = (n: number) =>
    `S/ ${n.toLocaleString("es-PE", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;

const COLORS = ['#f79f4e', '#b34d16', '#9b7200', '#f1c461', '#6b8e23', '#4a90a4'];

// ---------------------------------------------------------------------
// Categorías de inventario
// El backend todavía no guarda la categoría del producto, así que se infiere
// por palabras clave (misma lógica que productosService.ts de la página comercial).
// Cuando exista una columna "categoria" en la BD, reemplazar esta función.
// ---------------------------------------------------------------------
const CATEGORIAS = [
    "Alimento", "Juguetes", "Ropa y accesorios", "Medicinas", "Aseo", "Transporte", "Camas",
];
const COLORES_CATEGORIA = ['#f79f4e', '#b34d16', '#9b7200', '#f1c461', '#6b8e23', '#4a90a4', '#8e5a9b'];
const COLOR_DESACTIVADO = '#9e9e9e';

function inferirCategoria(p: Productos): string {
    const t = `${p.nombre ?? ""} ${p.descripcion ?? ""} ${p.marca ?? ""}`.toLowerCase();
    if (/juguete|mordedor|pelota/.test(t)) return "Juguetes";
    if (/correa|collar|ropa|arn[eé]s|disfraz/.test(t)) return "Ropa y accesorios";
    if (/medicina|antibi[oó]tico|vitamina|antipulga|desparasit|jarabe|tableta/.test(t)) return "Medicinas";
    if (/shampoo|champ[uú]|jabon|jabón|aseo|perfume/.test(t)) return "Aseo";
    if (/transportadora|jaula|bolso de viaje/.test(t)) return "Transporte";
    if (/cama|cojin|coj[ií]n|manta/.test(t)) return "Camas";
    return "Alimento";
}

function Admin_index() {
    const [minimizado, setMinimizado] = useState(false);
    const [clientes, setClientes] = useState<ClienteResponse[]>([]);
    const [mascota, setMascota] = useState<MascotaResponse[]>([]);
    const [usuarios, setUsuarios] = useState<UsuarioResponse[]>([]);
    const [agenda, setAgenda] = useState<CitaResponse[]>([]);
    const [citaEs, setCitaEs] = useState<CitaPorEstado[]>([]);
    const [mascotaEsp, setMascotaEsp] = useState<MascotaPorEspecie[]>([]);
    const [citasM, setCitasM] = useState<CitasPorMes[]>([]);
    const [comprobantes, setComprobantes] = useState<ComprobanteIngreso[]>([]);
    const [productos, setProductos] = useState<Productos[]>([]);

    const puedeVerIngresos = (() => {
        try {
            const roles: string[] = JSON.parse(sessionStorage.getItem("roles") || "[]");
            return roles.includes("ADMINISTRADOR GENERAL");
        } catch {
            return false;
        }
    })();

    // ---------------- INGRESOS ----------------
    useEffect(() => {
        if (!puedeVerIngresos) return;

        IST.get("/comprobantes")
            .then(res => {
                const lista: ComprobanteIngreso[] = Array.isArray(res.data)
                    ? res.data
                    : res.data.data || [];
                // Solo comprobantes válidos (aceptados o pendientes). Los rechazados no tienen PDF.
                setComprobantes(lista.filter(c => !!c.pdfurlNubefact));
            })
            .catch(() => {
                Swal.fire({
                    title: "Error",
                    text: "No se pudieron cargar los ingresos",
                    icon: "error"
                });
            });
    }, [puedeVerIngresos]);

    const ingresos = useMemo(() => {
        const hoy = new Date();
        const clave = (y: number, m: number) => `${y}-${String(m + 1).padStart(2, "0")}`;

        const mesActual = clave(hoy.getFullYear(), hoy.getMonth());
        const prev = new Date(hoy.getFullYear(), hoy.getMonth() - 1, 1);
        const mesPrevio = clave(prev.getFullYear(), prev.getMonth());

        // Últimos 7 meses (misma ventana que el gráfico de citas)
        const meses: { key: string; label: string }[] = [];
        const porMes: Record<string, number> = {};
        for (let i = 6; i >= 0; i--) {
            const d = new Date(hoy.getFullYear(), hoy.getMonth() - i, 1);
            const key = clave(d.getFullYear(), d.getMonth());
            meses.push({ key, label: d.toLocaleString("es-PE", { month: "short", year: "numeric" }) });
            porMes[key] = 0;
        }

        let totalMes = 0, totalPrevio = 0, totalAnio = 0, igvMes = 0;
        let montoFacturas = 0, montoBoletas = 0, cantMes = 0;

        comprobantes.forEach(c => {
            if (!c.fechaEmision) return;
            const k = c.fechaEmision.slice(0, 7);
            const t = Number(c.total) || 0;

            if (k in porMes) porMes[k] += t;
            if (c.fechaEmision.startsWith(String(hoy.getFullYear()))) totalAnio += t;
            if (k === mesPrevio) totalPrevio += t;

            if (k === mesActual) {
                totalMes += t;
                igvMes += Number(c.totalIGV) || 0;
                cantMes++;
                if (Number(c.tipoComprobante) === 1) montoFacturas += t;
                else montoBoletas += t;
            }
        });

        const variacion = totalPrevio > 0 ? ((totalMes - totalPrevio) / totalPrevio) * 100 : null;

        return {
            totalMes, totalAnio, igvMes, cantMes, variacion,
            porMes: meses.map(m => ({ nombre: m.label, monto: Number(porMes[m.key].toFixed(2)) })),
            porTipo: [
                { nombre: "Facturas", monto: Number(montoFacturas.toFixed(2)) },
                { nombre: "Boletas", monto: Number(montoBoletas.toFixed(2)) },
            ].filter(x => x.monto > 0),
        };
    }, [comprobantes]);

    // ---------------- INVENTARIO ----------------
    useEffect(() => {
        const cargarInventario = async () => {
            try {
                // El backend limita tamanio a 100, así que se pagina hasta traer todo
                const todos: Productos[] = [];
                let pagina = 0;
                let totalPaginas = 1;

                while (pagina < totalPaginas) {
                    const res = await IST.get(`/productos?pagina=${pagina}&tamanio=100&soloActivos=false`);
                    const data = res.data.data;
                    todos.push(...(data.contenido || []));
                    totalPaginas = data.totalPaginas ?? 0;
                    pagina++;
                }
                setProductos(todos);
            } catch {
                Swal.fire({
                    title: "Error",
                    text: "No se pudo cargar el inventario",
                    icon: "error"
                });
            }
        };
        cargarInventario();
    }, []);

    const inventario = useMemo(() => {
        const total = productos.length;
        const activos = productos.filter(p => p.activo);
        const desactivados = total - activos.length;

        const conteo: Record<string, number> = {};
        activos.forEach(p => {
            const cat = inferirCategoria(p);
            conteo[cat] = (conteo[cat] || 0) + 1;
        });

        // Orden y color estables por categoría
        const data: { nombre: string; cantidad: number; color: string }[] = CATEGORIAS
            .filter(c => conteo[c])
            .map(c => ({
                nombre: c,
                cantidad: conteo[c],
                color: COLORES_CATEGORIA[CATEGORIAS.indexOf(c)],
            }));

        if (desactivados > 0) {
            data.push({ nombre: "Desactivados", cantidad: desactivados, color: COLOR_DESACTIVADO });
        }

        return { total, desactivados, data };
    }, [productos]);

    // ---------------- DATOS GENERALES ----------------
    useEffect(() => {
        const cargarDatos = async () => {
            try {
                const clientesRes = await IST.get("/clientes");
                const mascotasRes = await IST.get("/mascotas");
                const usuariosRes = await IST.get("/usuarios");
                const citasRes = await IST.get("/agenda");
                const estadoCRes = await IST.get("/estados-agenda");

                const listaClientes = clientesRes.data.data;
                const activos = listaClientes.filter((c: ClienteResponse) => c.activo === true);
                setClientes(activos);
                setMascota(mascotasRes.data.data);
                setUsuarios(usuariosRes.data);
                setAgenda(citasRes.data.data.content);

                const citas = citasRes.data.data.content;
                const estadosC = estadoCRes.data;
                const mascotas = mascotasRes.data.data;

                const estadosMap = estadosC.reduce((acc: any, estado: any) => {
                    acc[estado.id] = estado.nombre;
                    return acc;
                }, {});

                const conteo: Record<string, number> = {};
                citas.forEach((cita: any) => {
                    const nombreEstado = estadosMap[cita.idEstado] || "DESCONOCIDO";
                    conteo[nombreEstado] = (conteo[nombreEstado] || 0) + 1;
                });

                const dataPie: CitaPorEstado[] = Object.entries(conteo).map(
                    ([nombre, cantidad]) => ({ nombre, cantidad })
                );
                setCitaEs(dataPie);

                const conteoM: Record<string, number> = {};
                mascotas.forEach((m: MascotaResponse) => {
                    const nombreEspecie = m.especie?.nombre || "SIN ESPECIE";
                    conteoM[nombreEspecie] = (conteoM[nombreEspecie] || 0) + 1;
                });

                const dataPieEspecies: MascotaPorEspecie[] =
                    Object.entries(conteoM).map(([nombre, cantidad]) => ({ nombre, cantidad }));
                setMascotaEsp(dataPieEspecies);

            } catch (err) {
                Swal.fire({
                    title: "Error desconocido",
                    text: "Por favor refresque la página",
                    icon: "error"
                });
            }
        };

        cargarDatos();
    }, []);

    function obtenerUltimos7Meses() {
        const meses: string[] = [];
        const hoy = new Date();

        for (let i = 6; i >= 0; i--) {
            const d = new Date(hoy.getFullYear(), hoy.getMonth() - i, 1);
            const label = d.toLocaleString("es-PE", { month: "short", year: "numeric" });
            meses.push(label);
        }

        return meses;
    }

    useEffect(() => {
        if (!agenda.length) return;

        const meses = obtenerUltimos7Meses();

        const conteo: Record<string, number> = {};
        meses.forEach(m => (conteo[m] = 0));

        agenda.forEach(cita => {
            if (!cita.fechaRegistro) return;

            const fecha = new Date(cita.fechaRegistro);
            const label = fecha.toLocaleString("es-PE", { month: "short", year: "numeric" });

            if (conteo[label] !== undefined) {
                conteo[label]++;
            }
        });

        const dataChart: CitasPorMes[] = meses.map(mes => ({
            nombre: mes,
            cantidad: conteo[mes]
        }));

        setCitasM(dataChart);
    }, [agenda]);

    const legendFormatter = (value: any, entry: any) => `${value} (${entry.payload.cantidad})`;

    return (
        <>
            <div id="index_admin">
                <Br_administrativa onMinimizeChange={setMinimizado} />
                <main className={minimizado ? 'minimize' : ''}>
                    <div id="graficos" className={puedeVerIngresos ? "" : "sin_ingresos"}>

                        {/* ============ FILA DE KPIs ============ */}
                        <div className="fila_kpis">
                            <div className="kpi kpi_entidad">
                                <div className="kpi_texto">
                                    <span>Usuarios</span>
                                    <strong>{usuarios.length}</strong>
                                </div>
                                <img src="/2830573.png" alt="" />
                            </div>
                            <div className="kpi kpi_entidad">
                                <div className="kpi_texto">
                                    <span>Clientes</span>
                                    <strong>{clientes.length}</strong>
                                </div>
                                <img src="/6009864.png" alt="" />
                            </div>
                            <div className="kpi kpi_entidad">
                                <div className="kpi_texto">
                                    <span>Mascotas</span>
                                    <strong>{mascota.length}</strong>
                                </div>
                                <img src="/8334302.png" alt="" />
                            </div>
                            <div className="kpi kpi_entidad">
                                <div className="kpi_texto">
                                    <span>Citas registradas</span>
                                    <strong>{agenda.length}</strong>
                                </div>
                                <img src="/1005764.png" alt="" />
                            </div>

                            {puedeVerIngresos && (
                                <>
                                    <div className="kpi kpi_ingreso">
                                        <div className="kpi_texto">
                                            <span>Ingresos del mes</span>
                                            <strong>{soles(ingresos.totalMes)}</strong>
                                            {ingresos.variacion !== null && (
                                                <small className={ingresos.variacion >= 0 ? "sube" : "baja"}>
                                                    {ingresos.variacion >= 0 ? "▲" : "▼"} {Math.abs(ingresos.variacion).toFixed(1)}% vs mes ant.
                                                </small>
                                            )}
                                        </div>
                                    </div>
                                    <div className="kpi kpi_ingreso">
                                        <div className="kpi_texto">
                                            <span>Ingresos del año</span>
                                            <strong>{soles(ingresos.totalAnio)}</strong>
                                        </div>
                                    </div>
                                    <div className="kpi kpi_ingreso">
                                        <div className="kpi_texto">
                                            <span>IGV del mes</span>
                                            <strong>{soles(ingresos.igvMes)}</strong>
                                        </div>
                                    </div>
                                    <div className="kpi kpi_ingreso">
                                        <div className="kpi_texto">
                                            <span>Comprobantes del mes</span>
                                            <strong>{ingresos.cantMes}</strong>
                                        </div>
                                    </div>
                                </>
                            )}
                        </div>

                        {/* ============ FILA 1 DE GRÁFICOS ============ */}
                        <div className="tarjeta_grafico">
                            <div className="cabecera_grafico">
                                <h3>Especies</h3>
                            </div>
                            <div className="cuerpo_grafico">
                                <ResponsiveContainer width="100%" height="100%">
                                    <PieChart>
                                        <Pie
                                            data={mascotaEsp}
                                            cx="50%"
                                            cy="45%"
                                            innerRadius="50%"
                                            outerRadius="80%"
                                            paddingAngle={5}
                                            dataKey="cantidad"
                                            nameKey="nombre"
                                        >
                                            {mascotaEsp.map((_entry, index) => (
                                                <Cell key={index} fill={COLORS[index % COLORS.length]} />
                                            ))}
                                        </Pie>
                                        <Tooltip />
                                        <Legend
                                            verticalAlign="bottom"
                                            align="center"
                                            wrapperStyle={{ fontSize: 12 }}
                                            formatter={legendFormatter}
                                        />
                                    </PieChart>
                                </ResponsiveContainer>
                            </div>
                        </div>

                        <div className="tarjeta_grafico">
                            <div className="cabecera_grafico">
                                <h3>Citas por estado</h3>
                            </div>
                            <div className="cuerpo_grafico">
                                <ResponsiveContainer width="100%" height="100%">
                                    <PieChart>
                                        <Pie
                                            data={citaEs}
                                            cx="38%"
                                            cy="50%"
                                            innerRadius="50%"
                                            outerRadius="80%"
                                            paddingAngle={5}
                                            dataKey="cantidad"
                                            nameKey="nombre"
                                        >
                                            {citaEs.map((_entry, index) => (
                                                <Cell key={index} fill={COLORS[index % COLORS.length]} />
                                            ))}
                                        </Pie>
                                        <Tooltip />
                                        <Legend
                                            verticalAlign="middle"
                                            align="right"
                                            layout="vertical"
                                            wrapperStyle={{ fontSize: 12 }}
                                            formatter={legendFormatter}
                                        />
                                    </PieChart>
                                </ResponsiveContainer>
                            </div>
                        </div>

                        <div className="tarjeta_grafico">
                            <div className="cabecera_grafico">
                                <h3>Inventario</h3>
                                <span className="resumen_inventario">
                                    <b>{inventario.desactivados}</b> desactivados de <b>{inventario.total}</b>
                                </span>
                            </div>
                            <div className="cuerpo_grafico">
                                {inventario.data.length === 0 ? (
                                    <p className="sin_datos">Sin productos registrados</p>
                                ) : (
                                    <ResponsiveContainer width="100%" height="100%">
                                        <PieChart>
                                            <Pie
                                                data={inventario.data}
                                                cx="36%"
                                                cy="50%"
                                                innerRadius="50%"
                                                outerRadius="80%"
                                                paddingAngle={3}
                                                dataKey="cantidad"
                                                nameKey="nombre"
                                            >
                                                {inventario.data.map((entry, index) => (
                                                    <Cell key={index} fill={entry.color} />
                                                ))}
                                            </Pie>
                                            <Tooltip />
                                            <Legend
                                                verticalAlign="middle"
                                                align="right"
                                                layout="vertical"
                                                wrapperStyle={{ fontSize: 12 }}
                                                formatter={legendFormatter}
                                            />
                                        </PieChart>
                                    </ResponsiveContainer>
                                )}
                            </div>
                        </div>

                        {/* ============ FILA 2 DE GRÁFICOS ============ */}
                        <div className={`tarjeta_grafico ${puedeVerIngresos ? "" : "ancho_completo"}`}>
                            <div className="cabecera_grafico">
                                <h3>Citas por mes</h3>
                                <span className="subtitulo_grafico">Últimos 7 meses</span>
                            </div>
                            <div className="cuerpo_grafico">
                                <ResponsiveContainer width="100%" height="100%">
                                    <BarChart data={citasM} margin={{ top: 10, right: 15, left: -15, bottom: 0 }}>
                                        <CartesianGrid strokeDasharray="3 3" />
                                        <XAxis dataKey="nombre" stroke="black" tick={{ fontSize: 11 }} />
                                        <YAxis stroke="black" tick={{ fontSize: 11 }} allowDecimals={false} />
                                        <Tooltip />
                                        <Bar dataKey="cantidad" name="Citas" fill="#f1c461" />
                                    </BarChart>
                                </ResponsiveContainer>
                            </div>
                        </div>

                        {puedeVerIngresos && (
                            <>
                                <div className="tarjeta_grafico">
                                    <div className="cabecera_grafico">
                                        <h3>Ingresos por mes</h3>
                                        <span className="subtitulo_grafico">Últimos 7 meses</span>
                                    </div>
                                    <div className="cuerpo_grafico">
                                        <ResponsiveContainer width="100%" height="100%">
                                            <BarChart data={ingresos.porMes} margin={{ top: 10, right: 15, left: -5, bottom: 0 }}>
                                                <CartesianGrid strokeDasharray="3 3" />
                                                <XAxis dataKey="nombre" stroke="black" tick={{ fontSize: 11 }} />
                                                <YAxis stroke="black" tick={{ fontSize: 11 }} />
                                                <Tooltip formatter={(v: any) => soles(Number(v))} />
                                                <Bar dataKey="monto" name="Ingresos" fill="#b34d16" />
                                            </BarChart>
                                        </ResponsiveContainer>
                                    </div>
                                </div>

                                <div className="tarjeta_grafico">
                                    <div className="cabecera_grafico">
                                        <h3>Facturas vs boletas</h3>
                                        <span className="subtitulo_grafico">Mes actual</span>
                                    </div>
                                    <div className="cuerpo_grafico">
                                        {ingresos.porTipo.length === 0 ? (
                                            <p className="sin_datos">Sin comprobantes este mes</p>
                                        ) : (
                                            <ResponsiveContainer width="100%" height="100%">
                                                <PieChart>
                                                    <Pie
                                                        data={ingresos.porTipo}
                                                        cx="50%"
                                                        cy="45%"
                                                        innerRadius="50%"
                                                        outerRadius="80%"
                                                        paddingAngle={5}
                                                        dataKey="monto"
                                                        nameKey="nombre"
                                                    >
                                                        {ingresos.porTipo.map((_e, i) => (
                                                            <Cell key={i} fill={COLORS[i % COLORS.length]} />
                                                        ))}
                                                    </Pie>
                                                    <Tooltip formatter={(v: any) => soles(Number(v))} />
                                                    <Legend
                                                        verticalAlign="bottom"
                                                        wrapperStyle={{ fontSize: 12 }}
                                                        formatter={(value: any, entry: any) => `${value} (${soles(entry.payload.monto)})`}
                                                    />
                                                </PieChart>
                                            </ResponsiveContainer>
                                        )}
                                    </div>
                                </div>
                            </>
                        )}
                    </div>
                </main>
            </div>
        </>
    )
}

export default Admin_index