import { useEffect, useRef, useState } from 'react'
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa'
import "./styles.css"
import { Link } from "react-router-dom";

interface Usuario {
    ID: number,
    CODIGO: string,
    USERNAME: string,
    PASSWORD: string,
    ACTIVO: number,
    FECHA_CREACION: string,
    FECHA_BAJA?: string
}

function gestionarUsuarios() {
    const [minimizado, setMinimizado] = useState(false);
    const [busqueda, setBusqueda] = useState("");
    const [usuarios, setUsuarios] = useState<Usuario[]>([]);
    const [filtrado, setFiltrado] = useState<Usuario[]>([]);
    const [menuActivoId, setMenuActivoId] = useState<number | null>(null);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [edicion, setEdicion] = useState<Usuario | null>(null);
    const menuRef = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        const ejemplo = [
            {ID: 1, CODIGO: "RED", USERNAME: "ANA", PASSWORD: "123ASD", ACTIVO: 1, FECHA_CREACION: "12-12-12"},
            {ID: 1, CODIGO: "YELLOW", USERNAME: "BAP", PASSWORD: "123ASD", ACTIVO: 1, FECHA_CREACION: "12-12-12"},
            {ID: 1, CODIGO: "GREEN", USERNAME: "ROD", PASSWORD: "123ASD", ACTIVO: 1, FECHA_CREACION: "12-12-12"},  
        ];
        setUsuarios(ejemplo);
        setFiltrado(ejemplo);
    }, []);

    useEffect(() => {
        const lista = usuarios.filter(value => value.USERNAME.toLocaleLowerCase().includes(busqueda.toLowerCase()));
        setFiltrado(lista);
    }, [busqueda, usuarios]);

    useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
        if (menuRef.current && !menuRef.current.contains(event.target as Node)) {setMenuActivoId(null);}};
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const eliminarUsuario = (ID: number) => {
        const registros = usuarios.filter(valor => valor.ID !== ID);
        setUsuarios(registros);
        setFiltrado(registros);
        setMenuActivoId(null);
    }

    // Editar colaboradores
    const editarColaborador = (ID: number) => {
        const colaboradorEditado = usuarios.find(usuario => usuario.ID === ID);
        if (colaboradorEditado) {
            setEdicion(colaboradorEditado);
            setMostrarModal(true);
        }
    }

    // Guardar colaborador
    const guardarUsuarios = () => {
        if (edicion) {
            const registros = usuarios.map(usuario => usuario.ID === edicion.ID ? edicion : usuario);
            setUsuarios(registros);
            setFiltrado(registros);
            setEdicion(null);
        } else {
            const nuevo: Usuario = {
                ID: usuarios.length + 1,
                CODIGO: "CODIGO0",
                USERNAME: "USERNAME",
                PASSWORD: "PASSWORD",
                ACTIVO: 1,
                FECHA_CREACION: new Date().toISOString().split("T")[0],
                FECHA_BAJA: ""
            };
            setUsuarios([...usuarios, nuevo]);
            setFiltrado([...usuarios, nuevo]);
        }
    }


    return (
        <div id="cuerpo-main">
            <Br_administrativa onMinimizeChange={setMinimizado}/>
        </div>        
    )
}


export default gestionarUsuarios