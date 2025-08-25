import { useEffect, useRef, useState } from 'react'
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa'
import "./gestionarColaboradores.css"

interface Colaborador {
    ID: number,
    CODIGO: string,
    ID_ENTIDAD: number,
    NOMBRE: string,
    FECHA_INGRESO: string,
    ID_USUARIO: number,
    ACTIVITY: number,
    FOTO?: string
}

function gestionarColaboradores() {
    const [minimizado, setMinimizado] = useState(false);
    const [busqueda, setBusqueda] = useState("");
    const [colaborador, setColaborador] = useState<Colaborador[]>([]);
    const [filtrado, setFiltrado] = useState<Colaborador[]>([]);
    const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [edicion, setEdicion] = useState<Colaborador | null>(null);
    const menuRef = useRef<HTMLDivElement | null>(null);

    // Mete datos de ejemplo
    useEffect(() => {
        const ejemplo = [
            {ID: 1, CODIGO: "ZXC", ID_ENTIDAD: 1, NOMBRE: "PLZ", FECHA_INGRESO: "12-12-12", ID_USUARIO: 1, ACTIVITY: 1, FOTO: "url/ay.jpg"},
            {ID: 2, CODIGO: "JKL", ID_ENTIDAD: 2, NOMBRE: "PIO", FECHA_INGRESO: "12-12-12", ID_USUARIO: 2, ACTIVITY: 1, FOTO: "url/ay.jpg"},
        ];
        setColaborador(ejemplo);
        setFiltrado(ejemplo);
    }, []);

    // Filtra por búsqueda
    useEffect(() => {
        const lista = colaborador.filter(value => value.NOMBRE.toLowerCase().includes(busqueda.toLowerCase()));
        setFiltrado(lista);
    }, [busqueda, colaborador]);

    // Cerrar menú al hacer click afuera

    // Eliminación lógica de colaborador

    // Editar colaborador

    // Guardar colaborador

    return (
        <div id="colaboradores">
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? "minimize" : ""}>
                <section id="listar_colaboradores">
                    <div className="encabezado">
                        <h2>Lista de colaboradores</h2>
                    </div>
                    <div className="buscador">
                        <div className="barra_buscador"><input type="text" placeholder="Ingrese nombre de colaborador" value={busqueda} onChange={(e) => setBusqueda(e.target.value)}/></div>
                        <button onClick={() => { setMostrarModal(true); setEdicion(null); }}>Registrar colaborador</button>
                    </div>

                    <div className="listar_colaboradores">
                        {filtrado.map((registro) => (
                            <div className="registro_colaborador" key={registro.ID}>
                                <span className="colaborador"></span>
                                <span className="fecha">📅{registro.FECHA_INGRESO}</span>
                                <div className="listar_opciones_contenedor">
                                    <div className="listar_opciones" onClick={() => setMenuActivoId(registro.ID)}></div>
                                    {menuActivoId === registro.ID && (
                                        <div ref={menuRef} className="menu-opciones">
                                            <button onClick={() => editarColaborador(registro.ID)}>✏️ Editar</button>
                                            <button onClick={() => eliminarColaborador(registro.ID)}>🗑️ Eliminar</button>
                                        </div>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                </section>
            </main>
            
            {mostrarModal && (
                <div className="model-overlay">
                    <div className="model-content">
                        <h3>{edicion ? "Editar colaborador" : "Registrar colaborador"}</h3>
                        <input type="text" />
                        <input type="text" />
                        <input type="text" />
                        <input type="text" />
                    </div>
                </div>
            )}
        </div>
    )
}

export default gestionarColaboradores