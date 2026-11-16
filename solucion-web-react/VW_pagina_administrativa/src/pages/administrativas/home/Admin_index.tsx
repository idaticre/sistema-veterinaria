import { useEffect, useMemo, useState } from "react";
import Br_administrativa from "../../../components/barra_administrativa/Br_administrativa";
import { PieChart, Pie, Cell, Legend, ResponsiveContainer, Tooltip, BarChart, Bar, XAxis, YAxis, CartesianGrid } from 'recharts';
import "./Admin_index.css"
import type { UsuarioResponse, ClienteResponse,  MascotaResponse, CitaResponse, CitaPorEstado, MascotaPorEspecie } from "../../../components/interfaces/interfaces";
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

const soles = (n: number) =>
    `S/ ${n.toLocaleString("es-PE", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;

function Admin_index() {
    const [minimizado, setMinimizado] = useState(false);
    const [clientes, setClientes] = useState<ClienteResponse[]>([]);
    const [mascota, setMascota] = useState<MascotaResponse[]>([]);
    const [usuarios, setUsuarios] = useState<UsuarioResponse[]>([]);
    const [agenda, setAgenda] = useState<CitaResponse[]>([]);
    const [citaEs, setCitaEs] = useState<CitaPorEstado[]>([]);
    const [mascotaEsp, setMascotaEsp] = useState<MascotaPorEspecie[]>([]);
    const COLORS = ['#f79f4eff', '#b34d16ff', '#9b7200ff']; 

    interface CitasPorMes {
        nombre: string;  
        cantidad: number;
    }
    const [citasM, setCitasM] = useState<CitasPorMes[]>([]);

    const [comprobantes, setComprobantes] = useState<ComprobanteIngreso[]>([]);

    const puedeVerIngresos = (() => {
        try {
            const roles: string[] = JSON.parse(sessionStorage.getItem("roles") || "[]");
            return roles.includes("ADMINISTRADOR GENERAL");
        } catch {
            return false;
        }
    })();

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
                    ([nombre, cantidad]) => ({
                        nombre,
                        cantidad 
                    })
                );
                setCitaEs(dataPie);

                const conteoM: Record<string, number> = {};

                mascotas.forEach((mascota: MascotaResponse) => {
                    const nombreEspecie = mascota.especie?.nombre || "SIN ESPECIE";

                    conteoM[nombreEspecie] = (conteoM[nombreEspecie] || 0) + 1;
                });

                const dataPieEspecies: MascotaPorEspecie[] =
                Object.entries(conteoM).map(([nombre, cantidad]) => ({
                    nombre,
                    cantidad
                }));

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
            const label = d.toLocaleString("es-PE", {
            month: "short",
            year: "numeric"
            });
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
            const label = fecha.toLocaleString("es-PE", {
            month: "short",
            year: "numeric"
            });

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


    return (
        <>
            <div id="index_admin">
                <Br_administrativa onMinimizeChange={setMinimizado} />
                <main className={minimizado ? 'minimize' : ''}>
                    <div id="graficos">
                        <div id="conteo_emtidades">
                            <div id="conteo_usuario">
                                <div>
                                    <strong>Usuarios</strong>
                                    <p>{usuarios.length}</p>
                                </div>
                                <img src="/2830573.png" alt="" />
                            </div>
                            <div id="conteo_clientes">
                                <div>
                                    <strong>Clientes</strong>
                                    <p>{clientes.length}</p>
                                </div>
                                <img src="/6009864.png" alt="" />
                            </div>
                            <div id="conteo_mascotas">
                                <div>
                                    <strong>Mascotas</strong>
                                    <p>{mascota.length}</p>
                                </div>
                                <img src="/8334302.png" alt="" />
                            </div>
                            <div id="conteo_citas">
                                <div>
                                    <strong>Citas Registradas</strong>
                                    <p>{agenda.length}</p>
                                </div>
                                <img src="/1005764.png" alt="" />
                            </div>
                        </div>
                        <div className="grafico_pie_especie">
                            <div className="cabezerra_grafic_pie">
                                <h3>Especies</h3>
                            </div>
                            <ResponsiveContainer width="100%" height="100%">
                                <PieChart>
                                    <Pie
                                        data={mascotaEsp}
                                        cx="50%"
                                        cy="50%"
                                        innerRadius={60}
                                        outerRadius={90}
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
                                        wrapperStyle={{ textAlign: 'center' }}
                                        formatter={(value: any, entry: any) => `${value} (${entry.payload.cantidad})`}
                                    />
                                </PieChart>
                            </ResponsiveContainer>
                        </div>
                        <div className="grafico_pie_citas">
                            <div className="cabezerra_grafic_pie">
                                <h3>Citas</h3>
                            </div>
                            <ResponsiveContainer width="100%" height="100%">
                                <PieChart>
                                    <Pie
                                        data={citaEs}
                                        cx="50%"
                                        cy="50%"
                                        innerRadius={60}
                                        outerRadius={90}
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
                                        align="left"
                                        layout="vertical"
                                        formatter={(value: any, entry: any) => `${value} (${entry.payload.cantidad})`}
                                    />
                                </PieChart>
                            </ResponsiveContainer>
                        </div>
                        <div id="grafico_barra">
                            <ResponsiveContainer width="100%" height="100%">
                                <BarChart
                                    data={citasM}
                                    margin={{ top: 20, right: 30, left: 20}}
                                >
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis dataKey="nombre" stroke="black"/>
                                    <YAxis stroke="black" />
                                    <Tooltip />
                                    <Bar dataKey="cantidad" fill="#f1c461ff" />
                                </BarChart>
                            </ResponsiveContainer>
                        </div>
                        {puedeVerIngresos && (
                            <div id="seccion_ingresos">
                                <h3 className="titulo_ingresos">Ingresos</h3>

                                <div className="kpis_ingresos">
                                    <div className="kpi_ingreso">
                                        <span>Ingresos del mes</span>
                                        <strong>{soles(ingresos.totalMes)}</strong>
                                        {ingresos.variacion !== null && (
                                            <small className={ingresos.variacion >= 0 ? "sube" : "baja"}>
                                                {ingresos.variacion >= 0 ? "▲" : "▼"} {Math.abs(ingresos.variacion).toFixed(1)}% vs mes anterior
                                            </small>
                                        )}
                                    </div>
                                    <div className="kpi_ingreso">
                                        <span>Ingresos del año</span>
                                        <strong>{soles(ingresos.totalAnio)}</strong>
                                    </div>
                                    <div className="kpi_ingreso">
                                        <span>IGV del mes</span>
                                        <strong>{soles(ingresos.igvMes)}</strong>
                                    </div>
                                    <div className="kpi_ingreso">
                                        <span>Comprobantes del mes</span>
                                        <strong>{ingresos.cantMes}</strong>
                                    </div>
                                </div>

                                <div className="graficos_ingresos">
                                    <div className="grafico_ingresos_barra">
                                        <h4>Ingresos por mes (últimos 7 meses)</h4>
                                        <ResponsiveContainer width="100%" height="100%">
                                            <BarChart data={ingresos.porMes} margin={{ top: 10, right: 20, left: 10 }}>
                                                <CartesianGrid strokeDasharray="3 3" />
                                                <XAxis dataKey="nombre" stroke="black" />
                                                <YAxis stroke="black" />
                                                <Tooltip formatter={(v: any) => soles(Number(v))} />
                                                <Bar dataKey="monto" name="Ingresos" fill="#b34d16" />
                                            </BarChart>
                                        </ResponsiveContainer>
                                    </div>

                                    <div className="grafico_ingresos_pie">
                                        <h4>Mes actual: facturas vs boletas</h4>
                                        {ingresos.porTipo.length === 0 ? (
                                            <p className="sin_datos_ingresos">Sin comprobantes este mes</p>
                                        ) : (
                                            <ResponsiveContainer width="100%" height="100%">
                                                <PieChart>
                                                    <Pie
                                                        data={ingresos.porTipo}
                                                        cx="50%"
                                                        cy="50%"
                                                        innerRadius={50}
                                                        outerRadius={80}
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
                                                        formatter={(value: any, entry: any) => `${value} (${soles(entry.payload.monto)})`}
                                                    />
                                                </PieChart>
                                            </ResponsiveContainer>
                                        )}
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>
                </main>
            </div>
        </>
    )
}

export default Admin_index