import { useEffect, useRef, useState } from 'react'
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa'
import type { UsuarioRequest, UsuarioResponse } from "../../../../components/interfaces/interfaces";
import IST from '../../../../components/proteccion/IST';

const gestionarUsuarios: React.FC = () => {
    const [minimizado, setMinimizado] = useState(false);
    const [busqueda, setBusqueda] = useState("");
    const [usuarios, setUsuarios] = useState<UsuarioResponse[]>([]);
    const [filtrado, setFiltrado] = useState<UsuarioResponse[]>([]);
    const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [edicion, setEdicion] = useState<UsuarioRequest | null>(null);
    const menuRef = useRef<HTMLDivElement | null>(null);

    // Efecto de cerrar ventana
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
        if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
            setMenuActivoId(null);
        }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    // Filtra por búsqueda
    useEffect(() => {
        const lista = usuarios.filter(value => value.username.toLowerCase().includes(busqueda.toLowerCase()));
        setFiltrado(lista);
    }, [busqueda, usuarios]);

    // Listar usuarios (Response)
    useEffect(() => {listarUsuarios();}, []);
    const listarUsuarios = async () => {
        try {
            const respuesta = await IST.get(`/usuarios`);
            const lista = Array.isArray(respuesta.data)
            ? respuesta.data
            : respuesta.data.data;

            const activos = lista.filter((cliente: UsuarioResponse) => cliente.activo === true);

            setUsuarios(activos);
            setFiltrado(activos);
        } catch (error) {console.error("Error al obtener los usuarios", error);}
    };

    /* Formulario nuevo y vacío
    const abrirFormularioNuevo = () => {
        const nuevo: UsuarioRequest = {
            username: "",
            passwordHash: "",
            activo: true,
            fechaCreacion: new Date().toISOString()
        }
        setEdicion(nuevo);
        setMostrarModal(true);
    };
    */

    // Formulario para editar
    const abrirFormularioEditar = (usuario: UsuarioResponse) => {
        const editado: UsuarioRequest = {
            id: usuario.id,
            username: usuario.username,
            passwordHash: usuario.passwordHash,
            activo: usuario.activo,
            fechaCreacion: usuario.fechaCreacion
        }
        setEdicion(editado);
        setMostrarModal(true);
    };

    // Guardar
    const guardarUsuario = async () => {
        if (!edicion) return;
        if (!edicion.username.trim()) {alert("Ingrese un nombre de usuario"); return;}
        if (!edicion.passwordHash.trim()) {alert("Ingrese una contraseña"); return;}

        try {
            if (edicion.id && edicion.id > 0) {await IST.put(`/usuarios/${edicion.id}`, edicion);}
            else {await IST.post(`/usuarios`, edicion);}
            listarUsuarios();
            setEdicion(null);
            setMostrarModal(false);
        } catch (error) {
            console.error("Error al registrar/actualizar: ", error);
            alert("Ocurrió un error al guardar el usuario.");
        }
    };

    /* Eliminar
    const eliminarUsuario = async (id: number) => {
        try {
            await IST.delete(`/usuarios/${id}`);
            listarUsuarios();
            alert("Eliminación exitosa");
        } catch (error) {
            alert(error);
            console.error("Error al eliminar: ", error);
        }
    }
    */

    return (
        <div id="cuerpo-main">
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? "minimize" : ""}>
                <section id="listar-registros">
                    <div className="encabezado"><h2>Lista de usuarios</h2></div>
                    <div className="goated">
                        <div className="barra-buscador"><input type="text" placeholder="Ingrese el nombre del usuario que desea buscar 🔍" value={busqueda} onChange={(e) => setBusqueda(e.target.value)}/></div>
                        {/*<button className="boton-goated anadir-a-goated animacion-goated" onClick={abrirFormularioNuevo}>Registrar usuario</button>*/}
                    </div>
                    <table className="GM-table">
                        <thead className="GM-thead">
                            <tr className="GM-tr">
                                <th className="GM-th" style={{width:"150px"}}>Usuario</th>
                                <th className="GM-th" style={{width:"150px"}}>Fecha de creación</th>
                                {/*<th className="GM-th" style={{width:"20px"}}>Acciones</th>*/}
                            </tr>
                        </thead>
                        <tbody>
                            {filtrado.map((registro) => (
                                <tr key={registro.id}>
                                    <td className="GM-td">{registro.username}</td>
                                    <td className="GM-td">{registro.fechaCreacion}</td>
                                    {/*<td className="GM-td">
                                        <button className="boton-verde" onClick={() => abrirFormularioEditar(registro)}>Editar</button>
                                        <button className="boton-rojo" onClick={() => eliminarUsuario(registro.id)}>Eliminar</button>
                                    </td>*/}
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </section>
            </main>
            
            {mostrarModal && edicion && (
                <div className="ventana-overlay">
                    <div className="contenido-ventana">
                        <h3>Información de usuario</h3>
                        <input type="text" placeholder="Ingrese un nombre de usuario" value={edicion.username} onChange={(e) => setEdicion(prev => prev ? { ...prev, username: e.target.value } : null)}/>
                        <input type="password" placeholder="Ingrese una contraseña" value={edicion.passwordHash} onChange={(e) => setEdicion(prev => prev ? { ...prev, passwordHash: e.target.value } : null)}/>
                        <div className="acciones-de-registro">
                            <button className="boton-verde" onClick={guardarUsuario}>Guardar</button>
                            <button onClick={() => { setMostrarModal(false); setEdicion(null); }}>Cancelar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>    
    )
}
export default gestionarUsuarios;