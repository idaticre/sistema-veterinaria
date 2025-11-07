import { useEffect, useRef, useState } from 'react'
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa'
import { Link } from "react-router-dom";
import type { Usuario } from "../../../../components/interfaces/interfaces";
import axios from 'axios';

function gestionarUsuarios() {
    const [minimizado, setMinimizado] = useState(false);
    const [busqueda, setBusqueda] = useState("");
    const [usuarios, setUsuarios] = useState<Usuario[]>([]);
    const [filtrado, setFiltrado] = useState<Usuario[]>([]);
    const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [edicion, setEdicion] = useState<Usuario | null>(null);
    const menuRef = useRef<HTMLDivElement | null>(null);
    const baseURL = "http://localhost:8088/api";

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

    // Listar
    useEffect(() => {listarUsuarios();}, []);
    const listarUsuarios = async () => {
        try {
            const respuesta = await axios.get(`${baseURL}/usuarios`);
            console.log("Respuesta del backend:", respuesta.data);
            const lista = Array.isArray(respuesta.data)
            ? respuesta.data
            : respuesta.data.data;

            const activos = lista.filter((cliente: Usuario) => cliente.activo === true);

            setUsuarios(activos);
            setFiltrado(activos);
        } catch (error) {console.error("Error al obtener los usuarios", error);}
    };

    // Formulario nuevo y vacío
    const abrirFormularioNuevo = () => {
        const nuevo: Usuario = {
            username: "",
            password: "",
            activo: true
        }
        setEdicion(nuevo);
        setMostrarModal(true);
    };

    // Formulario para editar
    const abrirFormularioEditar = (usuario: Usuario) => {
        const editado: Usuario = {
            id: usuario.id,                   
            username: usuario.username,
            password: usuario.password,
            activo: usuario.activo,      
        }
        setEdicion(editado);
        setMostrarModal(true);
    };

    // Guardar
    const guardarUsuario = async () => {
        if (!edicion) return;
        
        try {
            if (edicion.id && edicion.id > 0) {
                const response = await axios.put(`${baseURL}/usuarios/actualizar`, edicion);
                console.log("Respuesta backend:", response.data);
            } else {
                const response = await axios.post(`${baseURL}/usuarios/registrar`, edicion);
                console.log("Respuesta backend:", response.data);
            }
            listarUsuarios();
            setEdicion(null);
            setMostrarModal(false);
        } catch (error) {
            console.log("Datos enviados al backend:", edicion);
            console.error("Error al registrar/actualizar: ", error);
            alert(error);
        }
    }

    // Eliminar
    const eliminarUsuario = async (id: number) => {
        try {
            await axios.delete(`${baseURL}/usuarios/eliminar/${id}`);
            listarUsuarios();
            alert("Eliminación exitosa");
        } catch (error) {
            alert(error);
            console.error("Error al eliminar: ", error);
        }
    }


    return (
        <div id="cuerpo-main">
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? "minimize" : ""}>
                <section id="listar-registros">
                    <div className="encabezado"><h2>Lista de usuarios</h2></div>
                    <div className="goated">
                        <div className="barra-buscador"><input type="text" placeholder="Ingrese el nombre del usuario que desea buscar 🔍" value={busqueda} onChange={(e) => setBusqueda(e.target.value)}/></div>
                        <Link className="boton-goated ir-a-goated animacion-goated" to="/administracion/administracion/gestionar_colaboradores">Regresar</Link>
                        <button className="boton-goated anadir-a-goated animacion-goated" onClick={registrarUsuario}>Registrar usuario</button>
                    </div>

                    <div className="tabla-wrapper">
                        <table className="listar-registros">
                            <thead>
                                <tr>
                                    <th>Usuario</th>
                                    <th>Contraseña</th>
                                    <th>Estado</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filtrado.map((registro) => (
                                    <tr key={registro.id}>
                                        <td></td>
                                        <button onClick={() => abrirFormularioEditar(registro)}>Editar</button>
                                        <button onClick={() => eliminarUsuario(registro.id)}>Eliminar</button>
                                    </tr>
                                ))}
                            </tbody>                        
                        </table>
                    </div>
                </section>
            </main>

            {mostrarModal && edicion && (
                <div className="ventana-overlay">
                    <div className="contenido-ventana">
                        <h3>Información de usuario</h3>
                        <p><strong>Siendo creado el:</strong> {edicion.FECHA_CREACION}</p>
                        <select value={edicion?.ACTIVO || ""} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, ACTIVO: Number(nuevoValor.target.value) } : null)}>
                            <option value="">-- Selecciona estado --</option>
                            <option value="1">Inactivo</option>
                            <option value="2">Activo</option>
                            <option value="3">Suspendido</option>
                        </select>
                        <input type="text" placeholder="Ingrese el nuevo nombre de usuario" value={edicion.USERNAME} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, USERNAME: nuevoValor.target.value } : null)}/>
                        <input type="password" placeholder="Ingrese una contraseña" value={edicion.PASSWORD} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, PASSWORD: nuevoValor.target.value } : null)}/> {/* Añadir mostrar contraseña*/}
                        <div className="acciones-de-registro">
                            <button onClick={guardarUsuario}>Guardar</button>
                            <button onClick={() => { setMostrarModal(false); setEdicion(null); }}>Cancelar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>        
    )
}
export default gestionarUsuarios