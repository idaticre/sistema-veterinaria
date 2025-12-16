import { useEffect, useState } from "react";
import Br_administrativa from "../../../components/barra_administrativa/Br_administrativa";
import { PieChart, Pie, Cell, Legend, ResponsiveContainer, Tooltip, BarChart, Bar, XAxis, YAxis, CartesianGrid } from 'recharts';
import "./Admin_index.css"
import type { UsuarioResponse, ClienteResponse,  MascotaResponse, CitaResponse, CitaPorEstado, MascotaPorEspecie } from "../../../components/interfaces/interfaces";
import IST from "../../../components/proteccion/IST";

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

    useEffect(() => {
        const cargarDatos = async () => {
            try {
                const clientesRes = await IST.get("/clientes");
                const mascotasRes = await IST.get("/mascotas");
                const especieRes = await IST.get("/especies")
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
                const especies = especieRes.data;
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

                const especiesMap: Record<number, string> = especies.reduce(
                    (acc: Record<number, string>, especie: any) => {
                        acc[especie.id] = especie.nombre;
                        return acc;
                    },
                    {}
                );

                const conteoM: Record<string, number> = {};

                mascotas.forEach((mascota: any) => {
                const nombreEspecie =
                    especiesMap[mascota.idEspecie] || "SIN ESPECIE";

                    conteoM[nombreEspecie] = (conteoM[nombreEspecie] || 0) + 1;
                });

                const dataPieEspecies: MascotaPorEspecie[] =
                Object.entries(conteoM).map(([nombre, cantidad]) => ({
                    nombre,
                    cantidad
                }));

                setMascotaEsp(dataPieEspecies);


            } catch (err) {
                console.error("Error al cargar datos:", err);
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
                    </div>
                </main>
            </div>
        </>
    )
}

export default Admin_index