import { useEffect, useRef, useState } from 'react'
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa'
import "./styles.css"
import { Link } from "react-router-dom";

interface Entidad {
    ID: number,
    CODIGO: string,
    TIPO_ENTIDAD: string,
    TIPO_PERSONA_JURIDICA: string,
    NOMBRE: string,
    SEXO?: string,
    DOCUMENTO: string,
    TIPO_DOCUMENTO: string,
    TELEFONO: string,
    CORREO: string,
    DIRECCION: string,
    CIUDAD: string,
    DISTRITO: string,
    REPRESENTANTE: string,
    FECHA_CREACION: string,
    FECHA_BAJA: string,
    ACTIVO: number
}

function gestionarEntidades() {
    const [minimizado, setMinimizado] = useState(false);
    const [busqueda, setBusqueda] = useState("");
    const [entidades, setEntidades] = useState<Entidad[]>([]);
    const [filtrado, setFiltrado] = useState<Entidad[]>([]);
    const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [edicion, setEdicion] = useState<Entidad | null>(null);
    const menuRef = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        const ejemplo = [
            {ID: 1, CODIGO: "EMP001", TIPO_ENTIDAD: "Empresa", TIPO_PERSONA_JURIDICA: "Sociedad Anónima", NOMBRE: "Tech Solutions SAC", SEXO: undefined, DOCUMENTO: "20123456789", TIPO_DOCUMENTO: "RUC", TELEFONO: "987654321", CORREO: "contacto@techsolutions.com", DIRECCION: "Av. Siempre Viva 123", CIUDAD: "Lima", DISTRITO: "Miraflores", REPRESENTANTE: "Carlos López", FECHA_CREACION: "2025-08-27", FECHA_BAJA: "2025-08-27", ACTIVO: 1,},
        ];
        setEntidades(ejemplo);
        setFiltrado(ejemplo);
    }, []);

    useEffect(() => {
        const lista = entidades.filter(value => value.NOMBRE.toLocaleLowerCase().includes(busqueda.toLowerCase()));
        setFiltrado(lista);
    }, [busqueda, entidades]);

    useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
        if (menuRef.current && !menuRef.current.contains(event.target as Node)) {setMenuActivoId(null);}};
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const eliminarEntidad = (ID: number) => {
        const registros = entidades.filter(valor => valor.ID !== ID);
        setEntidades(registros);
        setFiltrado(registros);
        setMenuActivoId(null);
    }

    const registrarEntidad = () => {
        const nuevo: Entidad = {
            ID: entidades.length + 1, // ALERTA "ERROR DE DUPLICADOS", NO USAR ESTO CUANDO CONECTEMOS LA BASE DE DATOS, DEJA QUE EL BACKEND EN return ASIGNE EL ID (para pruebas locales está bien)
            CODIGO: "EMP001",
            TIPO_ENTIDAD: "Empresa",
            TIPO_PERSONA_JURIDICA: "Sociedad Anónima",
            NOMBRE: "Tech Solutions SAC",
            SEXO: undefined, // opcional
            DOCUMENTO: "20123456789",
            TIPO_DOCUMENTO: "RUC",
            TELEFONO: "987654321",
            CORREO: "contacto@techsolutions.com",
            DIRECCION: "Av. Siempre Viva 123",
            CIUDAD: "Lima",
            DISTRITO: "Miraflores",
            REPRESENTANTE: "Carlos López",
            FECHA_CREACION: "2025-08-27",
            FECHA_BAJA: "2025-08-27",
            ACTIVO: 1,
        }
        setEdicion(nuevo);
        setMostrarModal(true)
    }

    const editarEntidad = (ID: number) => {
        const usuarioEditado = entidades.find(entidades => entidades.ID === ID);
        if (usuarioEditado) {
            setEdicion(usuarioEditado);
            setMostrarModal(true);
        }
    }

    const guardarEntidad = () => {
        if (!edicion) return;
        if (!edicion.NOMBRE.trim()) {alert("El nombre de usuario es obligatorio"); return;}
        if (!edicion.DOCUMENTO.trim()) {alert("La contraseña es obligatoria"); return;}
        if (!edicion.ACTIVO) {alert("Debes seleccionar un estado (Activo/Inactivo/Suspendido)"); return;}
        if (!edicion.REPRESENTANTE) {alert("Seleccione la fecha de creacion"); return;}

        
        // Madre para actualizar la vaina de la fecha de baja ALERTA, NO FUNCIONA = ESTÁ EN DESARROLLO
        let actualizado = { ...edicion };
        if (actualizado.ACTIVO === 1 || actualizado.ACTIVO === 3) {
            if (!actualizado.FECHA_BAJA) {actualizado.FECHA_BAJA = new Date().toISOString().split("T")[0];}} 
        else if (actualizado.ACTIVO === 2) {actualizado.FECHA_BAJA = "";}

        {/* Todo lo demás */}
        const existe = entidades.some(entidad => entidad.ID === edicion.ID);
        if (existe) {
            const registros = entidades.map(entidad => entidad.ID === edicion.ID ? edicion : entidad);
            setEntidades(registros);
            setFiltrado(registros);
        } else {
            const nuevo: Entidad = {
                ...edicion,
                ID: entidades.length + 1, // ALERTA "ERROR DE DUPLICADOS", NO USAR ESTO CUANDO CONECTEMOS LA BASE DE DATOS, DEJA QUE EL BACKEND EN return ASIGNE EL ID (para pruebas locales está bien)
                CODIGO: edicion.CODIGO || "CODIGO0", // CODIGO está siendo generado manualmente, editar cuando se conecte la base de datos para cumplir con la COMPOUND KEY
                FECHA_CREACION: new Date().toISOString().split("T")[0],
            };
            const registros = [...entidades, nuevo];
            setEntidades(registros);
            setFiltrado(registros);
        }

        setEdicion(null);
        setMostrarModal(false);
    }

    return (
        <div id="cuerpo-main">
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? "minimize" : ""}>
                <section id="listar-registros">
                    <div className="encabezado"><h2>Lista de entidades</h2></div>
                    <div className="goated">
                        <div className="barra-buscador"><input type="text" placeholder="Ingrese el nombre del usuario que desea buscar 🔍" value={busqueda} onChange={(e) => setBusqueda(e.target.value)}/></div>
                        <Link className="boton-goated ir-a-goated animacion-goated" to="/administracion/administracion/gestionar_colaboradores">Regresar</Link>
                        <button className="boton-goated anadir-a-goated animacion-goated" onClick={registrarEntidad}>Registrar entidad</button>
                    </div>

                    <div className="listar-registros">
                        {filtrado.map((registro) => (
                            <div className="mostrar-registros" key={registro.ID}>
                                <span className="texto-de-registro">{registro.CODIGO}</span>
                                <span className="texto-de-registro">{registro.TIPO_ENTIDAD}</span>
                                <span className="texto-de-registro">{registro.TIPO_PERSONA_JURIDICA}</span>
                                <span className="texto-de-registro">{registro.NOMBRE}</span>
                                <span className="texto-de-registro">{registro.SEXO}</span>
                                <span className="texto-de-registro">{registro.DOCUMENTO}</span>
                                <span className="texto-de-registro">{registro.TIPO_DOCUMENTO}</span>
                                <span className="texto-de-registro">{registro.TELEFONO}</span>
                                <span className="texto-de-registro">{registro.CORREO}</span>
                                <span className="texto-de-registro">{registro.DIRECCION}</span>
                                <span className="texto-de-registro">{registro.CIUDAD}</span>
                                <span className="texto-de-registro">{registro.DISTRITO}</span>
                                <span className="texto-de-registro">{registro.REPRESENTANTE}</span>
                                <span className="texto-de-registro">{registro.FECHA_CREACION}</span>
                                <span className="texto-de-registro">{registro.FECHA_BAJA}</span>
                                <span className="texto-de-registro">{{1: "Inactivo", 2: "Activo", 3: "Suspendido"}[registro.ACTIVO] || "Desconocido"}</span>                                
                                <span className="texto-de-registro">{registro.FECHA_CREACION}</span>
                                <span className="texto-de-registro">{ // Arreglar display de la fecha de baja (checar comentario en editarUsuario)
                                    registro?.FECHA_BAJA === undefined 
                                        ? ""
                                        : registro.ACTIVO === 1
                                            ? `(Inactivo desde ${registro.FECHA_BAJA})`
                                            : registro.ACTIVO === 3
                                                ? `(Suspendido desde ${registro.FECHA_BAJA})`
                                                : "NO_ELIMINAR_ESTO"}</span>
                                <div className="listar-opciones-contenedor">
                                    <div className="listar-registro-opciones" onClick={() => setMenuActivoId(registro.ID)}><i className="fa-solid fa-ellipsis-vertical"/></div>
                                    {menuActivoId === registro.ID && (
                                        <div ref={menuRef} className="menu-opciones">
                                            <button onClick={() => editarEntidad(registro.ID)}>✏️ Editar</button>
                                            <button onClick={() => eliminarEntidad(registro.ID)}>🗑️ Eliminar</button>
                                        </div>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                </section>
            </main>

            {mostrarModal && edicion && (
                <div className="ventana-overlay">
                    <div className="contenido-ventana">
                        <h3>Registrar usuario</h3>
                        <p><strong>Siendo creado el:</strong> {edicion.FECHA_CREACION}</p>
                        <input type="text" placeholder="Seleccione tipo de entidad" value={edicion?.ENTIDAD || ""} onChange={(nuevoValor) => setEdicion(edicion ? { ...edicion, ENTIDAD: nuevoValor.target.value } : null)}/>
                        
                        <div className="acciones-de-registro">
                            <button onClick={guardarEntidad}>Guardar</button>
                            <button onClick={() => { setMostrarModal(false); setEdicion(null); }}>Cancelar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>        
    )
}

export default gestionarEntidades