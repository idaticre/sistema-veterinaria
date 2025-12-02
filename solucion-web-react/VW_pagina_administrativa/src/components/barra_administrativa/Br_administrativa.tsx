import { useRef, useState, useEffect } from "react";
import "./br_administrativa.css" 
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";
type MenuKey = "cliente" | "mascotas" | "distribucion" | "venta" | "informe" | "administracion" | "seguridad" | "agenda" | null;

interface BrProps {
  onMinimizeChange?: (minimizado: boolean) => void;
}

function Br_administrativa({ onMinimizeChange }: BrProps) {
    const [openMenu, setOpenMenu] = useState<MenuKey>(null);
    const [minimizado,setMinimizado] = useState(false)

    const nombreUsuario = sessionStorage.getItem("usuario");

    const rolesGuardados = sessionStorage.getItem("roles");
    const roles: string[] = rolesGuardados ? JSON.parse(rolesGuardados) : [];
    const tieneRol = (rol: string) => roles.includes(rol);
    const RolesPermitidos = (...rolesRequeridos: string[]) => {
        return rolesRequeridos.some(rol => tieneRol(rol));
    };

    const clienteRef = useRef<HTMLUListElement | null>(null);
    const mascotasRef = useRef<HTMLUListElement | null>(null);
    const distribRef = useRef<HTMLUListElement | null>(null);
    const ventaRef = useRef<HTMLUListElement | null>(null);
    const informRef = useRef<HTMLUListElement | null>(null);
    const adminRef = useRef<HTMLUListElement | null>(null);
    const seguridadRef = useRef<HTMLUListElement | null>(null);
    const agendaRef = useRef<HTMLUListElement | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const token = sessionStorage.getItem("token"); // sessionStorage, no localStorage
        if (!token) {
            navigate("/administracion/home", { replace: true });
        }
    }, [navigate]);

    const cerrarSesion = () => {
        sessionStorage.clear();
        navigate("/administracion/login", { replace: true });
    };

    const toggleMinimizado = () => {
        const nuevoEstado = !minimizado;
        setMinimizado(nuevoEstado);
        if (onMinimizeChange) {
            onMinimizeChange(nuevoEstado);
        }
    }

    const toggleMenu = (menu: MenuKey) => {
        setOpenMenu(openMenu === menu ? null : menu);
    };

    useEffect(() => {
        const refs: Record<Exclude<MenuKey, null>, React.RefObject<HTMLUListElement | null>> = {
            cliente: clienteRef,
            mascotas: mascotasRef,
            distribucion: distribRef,
            venta: ventaRef,
            informe: informRef,
            administracion: adminRef,
            seguridad: seguridadRef,
            agenda: agendaRef,
        };


        (Object.keys(refs) as Exclude<MenuKey, null>[]).forEach((key) => {
            const ref = refs[key];
            if (!ref.current) return;

            if (key === openMenu) {
            const scrollHeight = ref.current.scrollHeight + 6;
            ref.current.style.height = `${scrollHeight}px`;
            ref.current.style.padding = "0.2rem 0";
            } else {
            ref.current.style.height = "0";
            ref.current.style.padding = "0";
            }
        });
    }, [openMenu]);


    return (
        <>
            <section id="cuerpo">
                <div id="br_administrativa" className={minimizado ? "minimize" : ""}>
                    <div id="header">
                        <div id="despliegue" onClick={toggleMinimizado}>
                            <i className="fa-solid fa-chevron-left"></i>
                        </div>
                        <div id="cabezera">
                            <Link to="/"><img src="/perro 2.png" alt="" /></Link>
                            <span>Manada Woof</span>
                        </div>
                    </div>
                    <div id="menu">
                        <ul id="opciones">
                            <li className="opcion opcion_simple">
                                <Link to="/administracion/home" className="enlace_opcion">
                                    <i className="fa-solid fa-house"></i>
                                    <span>Inicio</span>
                                </Link>
                            </li>
                            {RolesPermitidos("ADMINISTRADOR GENERAL", "AUXILIAR CAJA", "AUXILIAR GROMERS") && (
                                <li className="opcion opcion_simple">
                                    <Link to="/administracion/cliente/lista" className="enlace_opcion">
                                        <i className="fa-solid fa-user"></i>
                                        <span>Gestión de clientes</span>
                                    </Link>
                                </li>
                            )}
                            {RolesPermitidos("ADMINISTRADOR GENERAL", "AUXILIAR CAJA", "AUXILIAR GROMERS") && (
                                <li className="opcion opcion_simple">
                                    <Link to="/administracion/mascotas/lista" className="enlace_opcion">
                                        <i className="fa-solid fa-shield-dog"></i>
                                        <span>Gestión Mascotas</span>
                                        {/*<i className={`fa-solid ${openMenu === "mascotas" ? "fa-chevron-up" : "fa-chevron-down"}`}></i>*/}
                                    </Link>
                                    {/*<ul ref={mascotasRef} className="submenu">
                                        <li><Link to="/administracion/mascotas/lista" className="sub_opcion">Gestion de Mascotas</Link></li>
                                        {!RolesPermitidos("AUXILIAR GROOMERS") && (
                                            <li><Link to="/administracion/mascotas/vacunas" className="sub_opcion">Vacunas disponibles</Link></li>
                                        )}
                                    </ul>*/}
                                </li>
                            )}
                            {RolesPermitidos("ADMINISTRADOR GENERAL", "AUXILIAR CAJA", "AUXILIAR GROMERS") && (
                                <li className={`opcion opcion_desplegable ${openMenu === "agenda"?"toggle_submenu":""}`} 
                                    onClick={() => toggleMenu("agenda")}
                                >
                                    <Link to="" className="enlace_opcion">
                                        <i className="fa-solid fa-calendar-days"></i>
                                        <span>Agenda</span>
                                        <i className={`fa-solid ${openMenu === "agenda" ? "fa-chevron-up" : "fa-chevron-down"}`}></i>
                                    </Link>
                                    <ul ref={agendaRef} className="submenu">
                                        <li><Link to="/administracion/agenda/Agenda_general" className="sub_opcion">Agenda general</Link></li>
                                        {!RolesPermitidos("AUXILIAR GROOMERS") && (
                                            <li><Link to="/administracion/agenda/EditarCita" className="sub_opcion">Editar cita</Link></li>
                                        )}
                                    </ul>
                                </li>
                            )}
                            {/*{RolesPermitidos("ADMINISTRADOR GENERAL") && (
                                <li className="opcion opcion_simple">
                                    <Link to="#" className="enlace_opcion">
                                        <i className="fa-solid fa-folder"></i>
                                        <span>Historial Médico</span>
                                    </Link>
                                </li>
                            )}*/}
                            {RolesPermitidos("ADMINISTRADOR GENERAL") && (
                                <li className="opcion opcion_simple">
                                    <Link to="/administracion/servicios" className="enlace_opcion">
                                        <i className="fa-solid fa-folder"></i>
                                        <span>Servicios</span>
                                    </Link>
                                </li>
                            )}
                            {/*
                            {RolesPermitidos("ADMINISTRADOR GENERAL") && (
                                <li className={`opcion opcion_desplegable ${openMenu === "distribucion"?"toggle_submenu":""}`} 
                                    onClick={() => toggleMenu("distribucion")}
                                >
                                    <Link to="" className="enlace_opcion">
                                        <i className="fa-solid fa-truck-moving"></i>
                                        <span>Distribución</span>
                                        <i className={`fa-solid ${openMenu === "mascotas" ? "fa-chevron-up" : "fa-chevron-down"}`}></i>
                                    </Link>
                                    <ul ref={distribRef} className="submenu">
                                        <li><Link to="/administracion/distribucion/inventario" className="sub_opcion">Inventario</Link></li>
                                        <li><Link to="/administracion/mascotas/registro" className="sub_opcion">Ordenes de compra</Link></li>
                                        <li><Link to="/administracion/mascotas/especies" className="sub_opcion">Proveedores</Link></li>
                                                
                                    </ul>
                                </li>
                            )}
                            */}
                            {/*
                            <li className={`opcion opcion_desplegable ${openMenu === "venta"?"toggle_submenu":""}`} 
                                onClick={() => toggleMenu("venta")}>
                                <Link to="" className="enlace_opcion">
                                    <i className="fa-solid fa-cart-shopping"></i>
                                    <span>Ventas</span>
                                    <i className={`fa-solid ${openMenu === "mascotas" ? "fa-chevron-up" : "fa-chevron-down"}`}></i>
                                </Link>
                                <ul ref={ventaRef} className="submenu">
                                    
                                    <li><Link to="/administracion/mascotas/lista" className="sub_opcion">Gestion de ventas</Link></li>
                                    <li><Link to="/administracion/mascotas/registro" className="sub_opcion">Facturación electronica</Link></li>
                                    <li><Link to="/administracion/mascotas/especies" className="sub_opcion">Notas de credito</Link></li>
                                    
                                </ul>
                            </li>
                            */}
                            
                            <li className={`opcion opcion_desplegable ${openMenu === "informe"?"toggle_submenu":""}`} 
                                onClick={() => toggleMenu("informe")}
                            >
                                <Link to="" className="enlace_opcion"> 
                                    <i className="fa-solid fa-file"></i>
                                    <span>Reportes e Informes</span>
                                    <i className={`fa-solid ${openMenu === "mascotas" ? "fa-chevron-up" : "fa-chevron-down"}`}></i>
                                </Link>
                                <ul ref={informRef} className="submenu">
                                    {/*<li><Link to="/administracion/mascotas/lista" className="sub_opcion">Ventas</Link></li>
                                    <li><Link to="/administracion/mascotas/registro" className="sub_opcion">Financieros</Link></li>
                                    <li><Link to="/administracion/mascotas/especies" className="sub_opcion">Inventarios</Link></li>*/}
                                    <li><Link to="/administracion/reportes_e_informes/clientes" className="sub_opcion">Clientes</Link></li>
                                    {/*<li><Link to="/administracion/mascotas/especies" className="sub_opcion">Proveedores</Link></li>
                                    <li><Link to="/administracion/mascotas/especies" className="sub_opcion">Caja general</Link></li>*/}
                                </ul>
                            </li>
                            
                            {RolesPermitidos("ADMINISTRADOR GENERAL") && (
                                <li className={`opcion opcion_desplegable ${openMenu === "administracion"?"toggle_submenu":""}`} 
                                    onClick={() => toggleMenu("administracion")}
                                >
                                    <Link to="" className="enlace_opcion"> 
                                        <i className="fa-solid fa-toolbox"></i>
                                        <span>Administracion</span>
                                        <i className={`fa-solid ${openMenu === "mascotas" ? "fa-chevron-up" : "fa-chevron-down"}`}></i>
                                    </Link>
                                    <ul ref={adminRef} className="submenu">
                                        <li><Link to="/administracion/administracion/gestionar_colaboradores" className="sub_opcion">Gestión de colaboradores</Link></li>
                                        {/*<li><Link to="/administracion/administracion/pagos_a_colaboradores" className="sub_opcion">Pagos a colaboradores</Link></li>*/}
                                        <li><Link to="/administracion/administracion/turnos_y_horarios" className="sub_opcion">Horarios de colaboradores</Link></li>
                                        <li><Link to="/administracion/administracion/asistencia_de_colaboradores" className="sub_opcion">Asistencia de colaboradores</Link></li>
                                        <li><Link to="/administracion/administracion/parametros_y_promociones" className="sub_opcion">Parámetros y promociones</Link></li>
                                        {<li><Link to="/administracion/administracion/dashboard_administrativo" className="sub_opcion">Dashboard de colaboradore</Link></li>}
                                        
                                    </ul>
                                </li>
                            )}
                            {RolesPermitidos("ADMINISTRADOR GENERAL") && (
                                <li className={`opcion opcion_desplegable ${openMenu === "seguridad"?"toggle_submenu":""}`} 
                                    onClick={() => toggleMenu("seguridad")}
                                >
                                    <Link to="" className="enlace_opcion"> 
                                        <i className="fa-solid fa-shield-halved"></i>
                                        <span>Seguridad y Mantenimiento</span>
                                        <i className={`fa-solid ${openMenu === "mascotas" ? "fa-chevron-up" : "fa-chevron-down"}`}></i>
                                    </Link>
                                    <ul ref={seguridadRef   } className="submenu">
                                        <li><Link to="/administracion/administracion/gestionar_usuarios" className="sub_opcion">Usuarios del sistema</Link></li>
                                        <li><Link to="/administracion/administracion/Asignar_roles_y_permisos" className="sub_opcion">Asignar y gestionar roles</Link></li>
                                        {/*<li><Link to="/administracion/mascotas/especies" className="sub_opcion">Copia de seguridad</Link></li>*/}
                                    </ul>
                                </li>
                            )}
                        </ul>
                    </div>
                    <div id="br_footer">
                        <ul id="opciones">
                            <li className="opcion opcion_simple">
                                <Link to="" className="enlace_opcion">
                                    <i className="fa-solid fa-bell" />
                                    <span>Anuncios</span>
                                </Link>
                            </li>
                        </ul>
                        <div id="user">
                            <div id="img_user">
                                <img src="/baño.png" alt="" />
                            </div>
                            <div id="inf_user">
                                <span className="nombre_user">{nombreUsuario}</span>
                                <span className="mail_user">123456@gmail.com</span>
                            </div>
                            <div className="user_icon">
                                <Link to="/" onClick={cerrarSesion}><i className="fa-solid fa-right-to-bracket"></i></Link>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </>
    )
}

export default Br_administrativa