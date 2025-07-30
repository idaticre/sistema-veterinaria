import { useState } from "react";
import Br_administrativa from "../../components/barra_administrativa/Br_administrativa";
import { PieChart, Pie, Cell, Legend, ResponsiveContainer, Tooltip, BarChart, Bar, XAxis, YAxis, CartesianGrid } from 'recharts';
import "./Admin_index.css"

function Admin_index() {
    const [minimizado, setMinimizado] = useState(false);
    const COLORS = ['#6c08beff', '#b34d16ff', '#17a832ff']; 

    const data = [
        { name: 'Canino', cantidad: 10 },
        { name: 'Felino', cantidad: 60 },
        { name: 'Ave', cantidad: 12 }
    ];

    const citas = [
        { name: 'Pendientes', cantidad: 9 },
        { name: 'Realizadas', cantidad: 30 }
    ];

    const ventas = [
        { name: 'Enero', cantidad: 90},
        { name: 'Febrero', cantidad: 45 },
        { name: 'Marzo', cantidad: 75 },
        { name: 'Abril', cantidad: 60 },
        { name: 'Mayo', cantidad: 95 },
        { name: 'Junio', cantidad: 80 },
        { name: 'Julio', cantidad: 50 },
    ];


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
                                    <p>2</p>
                                </div>
                                <img src="/2830573.png" alt="" />
                            </div>
                            <div id="conteo_clientes">
                                <div>
                                    <strong>Clientes</strong>
                                    <p>10</p>
                                </div>
                                <img src="/6009864.png" alt="" />
                            </div>
                            <div id="conteo_mascotas">
                                <div>
                                    <strong>Mascotas</strong>
                                    <p>16</p>
                                </div>
                                <img src="/8334302.png" alt="" />
                            </div>
                            <div id="conteo_citas">
                                <div>
                                    <strong>Citas Registradas</strong>
                                    <p>10</p>
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
                                        data={data}
                                        cx="50%"
                                        cy="50%"
                                        innerRadius={60}
                                        outerRadius={90}
                                        paddingAngle={5}
                                        dataKey="cantidad"
                                    >
                                        {data.map((_entry, index) => (
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
                                        data={citas}
                                        cx="50%"
                                        cy="50%"
                                        innerRadius={60}
                                        outerRadius={90}
                                        paddingAngle={5}
                                        dataKey="cantidad"
                                    >
                                        {citas.map((_entry, index) => (
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
                                    data={ventas}
                                    margin={{ top: 20, right: 30, left: 20}}
                                >
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis dataKey="name" stroke="black"/>
                                    <YAxis stroke="black" />
                                    <Tooltip />
                                    <Bar dataKey="cantidad" fill="#5047f5ff" />
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