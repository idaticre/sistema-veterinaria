import { useEffect, useRef, useState } from 'react'
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa'
import "./gestionarColaboradores.css"

interface Colaborador {
    ID: number,
    CODIGO: string,
    ENTIDAD: string,
    NOMBRE: string,
    FECHA_INGRESO: string,
    USUARIO: string,
    ACTIVO: number,
    FOTO?: string
}

function gestionarColaboradores() {
    const [minimizado, setMinimizado] = useState(false);
    const [busqueda, setBusqueda] = useState("");
    const [colaboradores, setColaborador] = useState<Colaborador[]>([]);
    const [filtrado, setFiltrado] = useState<Colaborador[]>([]);
    const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [edicion, setEdicion] = useState<Colaborador | null>(null);
    const menuRef = useRef<HTMLDivElement | null>(null);

    // Mete datos de ejemplo
    useEffect(() => {
        const ejemplo = [
            {ID: 1, CODIGO: "ZXC", ENTIDAD: "ENTIDAD", NOMBRE: "PLZ", FECHA_INGRESO: "12-12-12", USUARIO: "USUARIO", ACTIVO: 1, FOTO: "url/ay.jpg"},
            {ID: 2, CODIGO: "JKL", ENTIDAD: "ENTIDAD", NOMBRE: "PIO", FECHA_INGRESO: "12-12-12", USUARIO: "USUARIO", ACTIVO: 1, FOTO: "url/ay.jpg"},
            {ID: 3, CODIGO: "QWE", ENTIDAD: "ENTIDAD", NOMBRE: "AYA", FECHA_INGRESO: "12-12-12", USUARIO: "USUARIO", ACTIVO: 0, FOTO: "url/ay.jpg"},
        ];
        setColaborador(ejemplo);
        setFiltrado(ejemplo);
    }, []);

    // Filtra por búsqueda
    useEffect(() => {
        const lista = colaboradores.filter(value => value.NOMBRE.toLowerCase().includes(busqueda.toLowerCase()));
        setFiltrado(lista);
    }, [busqueda, colaboradores]);

    // Cerrar menú al hacer click afuera
    useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
        if (menuRef.current && !menuRef.current.contains(event.target as Node)) {setMenuActivoId(null);}};
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    // Eliminación de colaboradores
    const eliminarColaborador = (ID: number) => {
        const registros = colaboradores.filter(valor => valor.ID !== ID)
        setColaborador(registros);
        setFiltrado(registros);
        setMenuActivoId(null);
    }

    // Editar colaboradores
    const editarColaborador = (ID: number) => {
        const colaboradorEditado = colaboradores.find(colaborador => colaborador.ID === ID);
        if (colaboradorEditado) {
            setEdicion(colaboradorEditado);
            setMostrarModal(true);
        }
    }

    // Guardar colaborador
    const guardarColaborador = () => {
        if (edicion) {
            const registros = colaboradores.map(colaborador => colaborador.ID === edicion.ID ? edicion : colaborador);
            setColaborador(registros);
            setFiltrado(registros);
            setEdicion(null);
        } else {
            const nuevo: Colaborador = {
                ID: colaboradores.length + 1,
                CODIGO: "CODIGO0",
                ENTIDAD: "ENTIDAD",
                NOMBRE: "Nuevo Colaborador",
                FECHA_INGRESO: new Date().toISOString().split("T")[0],
                USUARIO: "USUARIO",
                ACTIVO: 1
            };
            setColaborador([...colaboradores, nuevo]);
            setFiltrado([...colaboradores, nuevo]);
        }
    }

    return (
        <div id="colaboradores">
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? "minimize" : ""}>
                <section id="listar_colaboradores">
                    <div className="encabezado"><h2>Lista de colaboradores</h2></div>
                    <div className="buscador">
                        <div className="barra_buscador"><input type="text" placeholder="Ingrese nombre de colaborador" value={busqueda} onChange={(e) => setBusqueda(e.target.value)}/></div>
                        <button onClick={() => { setMostrarModal(true); setEdicion(null); }}>Registrar colaborador</button>
                    </div>

                    <div className="listar-colaboradores">
                        {filtrado.map((registro) => (
                            <div className="registro-colaborador" key={registro.ID}>
                                <span className="texto-de-registro">{registro.CODIGO}</span>
                                <span className="texto-de-registro">{registro.ENTIDAD}</span>
                                <span className="texto-de-registro">{registro.NOMBRE}</span>
                                <span className="texto-de-registro">{registro.ACTIVO ? "Activo" : "Inactivo"}</span>                                
                                <span className="texto-de-registro">{registro.USUARIO}</span>
                                <span className="texto-de-registro">📅{registro.FECHA_INGRESO}</span>
                                <div className="listar-opciones-contenedor">
                                    <div className="listar-registro-opciones" onClick={() => setMenuActivoId(registro.ID)}><i className="fa-solid fa-ellipsis-vertical"/></div>
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
                <div className="ventana-overlay">
                    <div className="contenido-ventana">
                        <h3>{edicion ? "Editar colaborador" : "Registrar colaborador"}</h3>
                        <input type="text" placeholder="Seleccione tipo de entidad" value={edicion?.ENTIDAD || ""} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, ENTIDAD: nuevoValor.target.value } : null)}/>
                        <input type="text" placeholder="Nombre de colaborador" value={edicion?.NOMBRE || ""} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, NOMBRE: nuevoValor.target.value } : null)}/>
                        <input type="date" value={edicion?.FECHA_INGRESO || ""} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, FECHA_INGRESO: nuevoValor.target.value } : null)}/>
                        <input type="text" placeholder="Nombre de usuario" value={edicion?.USUARIO || ""} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, USUARIO: nuevoValor.target.value } : null)}/>
                        <select value={edicion?.ACTIVO || ""} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, ACTIVO: Number(nuevoValor.target.value) } : null)}>
                            <option value="">-- Selecciona estado --</option>
                            <option value="0">Inctivo</option>
                            <option value="1">Activo</option>
                            <option value="2">Suspendido</option>
                        </select>
                        <div className="acciones-de-registro">
                            <button onClick={guardarColaborador}>Guardar</button>
                            <button onClick={() => { setMostrarModal(false); setEdicion(null); }}>Cancelar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}

export default gestionarColaboradores