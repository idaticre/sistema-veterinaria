import { useEffect, useRef, useState } from 'react'
import type { ProveedorRequest, ProveedorResponse } from '../../../components/interfaces/interfaces'
import Br_administrativa from '../../../components/barra_administrativa/Br_administrativa'
import "./proveedores.css"
import IST from '../../../components/proteccion/IST'
import Swal from 'sweetalert2';

const TIPOS_DOCUMENTO = [
    { id: 1, nombre: "DNI" },
    { id: 2, nombre: "RUC" },
    { id: 3, nombre: "Carnet Ext." },
    { id: 4, nombre: "P. Nac." },
    { id: 5, nombre: "Pasaporte" },
    { id: 6, nombre: "Otros" },
];

const TIPOS_PERSONA_JURIDICA = [
    { id: 1, nombre: "Natural" },
    { id: 2, nombre: "Jurídica" },
];

const PROVEEDOR_VACIO: ProveedorRequest = {
    id: 0,
    idTipoPersonaJuridica: 0,
    nombre: "",
    sexo: "M",
    documento: "",
    idTipoDocumento: 0,
    correo: "",
    telefono: "",
    direccion: "",
    ciudad: "",
    distrito: "",
    representante: "",
    activo: true,
};

type ModoModal = "crear" | "editar" | "ver";

function Proveedores() {
    const [minimizado, setMinimizado]     = useState(false);
    const [proveedores, setProveedores]   = useState<ProveedorResponse[]>([]);
    const [busqueda, setBusqueda]         = useState("");
    const [mostrarModal, setMostrarModal] = useState(false);
    const [modoModal, setModoModal]       = useState<ModoModal>("crear");
    const [form, setForm]                 = useState<ProveedorRequest>(PROVEEDOR_VACIO);
    const [guardando, setGuardando]       = useState(false);
    const menuRef                         = useRef<HTMLDivElement | null>(null);

    // ── Carga inicial ─────────────────────────────────────────
    useEffect(() => { obtenerProveedores(); }, []);

    const obtenerProveedores = async () => {
        try {
            const res = await IST.get("/proveedores");
            setProveedores(res.data.data || res.data);
        } catch {
            Swal.fire({ title: "Error al cargar proveedores", text: "Por favor, refresque la página", icon: "error" });
        }
    };

    // ── Filtro por nombre ─────────────────────────────────────
    const proveedoresFiltrados = proveedores.filter(p =>
        p.nombre.toLowerCase().includes(busqueda.toLowerCase()) ||
        p.documento.toLowerCase().includes(busqueda.toLowerCase())
    );

    // ── Manejo del form ───────────────────────────────────────
    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value, type } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: type === "checkbox"
                ? (e.target as HTMLInputElement).checked
                : (name === "idTipoPersonaJuridica" || name === "idTipoDocumento") ? Number(value) : value,
        }));
    };

    // ── Abrir modales ─────────────────────────────────────────
    const abrirCrear = () => {
        setForm(PROVEEDOR_VACIO);
        setModoModal("crear");
        setMostrarModal(true);
    };

    const abrirEditar = (p: ProveedorResponse) => {
        setForm({
            id:                    p.id,
            idTipoPersonaJuridica: p.idTipoPersonaJuridica,
            nombre:                p.nombre,
            sexo:                  p.sexo as "M" | "F",
            documento:             p.documento,
            idTipoDocumento:       p.idTipoDocumento,
            correo:                p.correo,
            telefono:              p.telefono,
            direccion:             p.direccion,
            ciudad:                p.ciudad,
            distrito:              p.distrito,
            representante:         p.representante,
            activo:                p.activo,
        });
        setModoModal("editar");
        setMostrarModal(true);
    };

    const abrirVer = (p: ProveedorResponse) => {
        abrirEditar(p);        // reutiliza la carga de datos
        setModoModal("ver");   // pero bloquea edición
    };

    const cerrarModal = () => {
        setMostrarModal(false);
        setForm(PROVEEDOR_VACIO);
    };

    // ── Validación básica ─────────────────────────────────────
    const validar = (): boolean => {
        if (!form.nombre.trim()) {
            Swal.fire({ title: "El nombre es obligatorio", icon: "warning" }); return false;
        }
        if (!form.documento.trim()) {
            Swal.fire({ title: "El documento es obligatorio", icon: "warning" }); return false;
        }
        if (!form.correo.trim()) {
            Swal.fire({ title: "El correo es obligatorio", icon: "warning" }); return false;
        }
        return true;
    };

    // ── POST ──────────────────────────────────────────────────
    const crearProveedor = async () => {
        if (!validar()) return;
        setGuardando(true);
        try {
            await IST.post("/proveedores", form);
            Swal.fire({ title: "Proveedor registrado correctamente", icon: "success" });
            cerrarModal();
            obtenerProveedores();
        } catch (err: any) {
            Swal.fire({ title: "Error al registrar", text: err.response?.data?.message || "Inténtelo de nuevo", icon: "error" });
        } finally { setGuardando(false); }
    };

    // ── PUT ───────────────────────────────────────────────────
    const actualizarProveedor = async () => {
        if (!validar()) return;
        setGuardando(true);
        try {
            await IST.put("/proveedores", form);
            Swal.fire({ title: "Proveedor actualizado correctamente", icon: "success" });
            cerrarModal();
            obtenerProveedores();
        } catch (err: any) {
            Swal.fire({ title: "Error al actualizar", text: err.response?.data?.message || "Inténtelo de nuevo", icon: "error" });
        } finally { setGuardando(false); }
    };

    // ── DELETE (lógico) ───────────────────────────────────────
    const eliminarProveedor = async (p: ProveedorResponse) => {
        const confirmacion = await Swal.fire({
            title: "¿Desactivar proveedor?",
            text: `Se dará de baja a ${p.nombre}.`,
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Sí, desactivar",
            cancelButtonText: "Cancelar",
            confirmButtonColor: "#b34d16",
        });
        if (!confirmacion.isConfirmed) return;

        try {
            await IST.delete(`/proveedores/${p.id}`);
            Swal.fire({ title: "Proveedor desactivado correctamente", icon: "success" });
            obtenerProveedores();
        } catch (err: any) {
            Swal.fire({ title: "Error al desactivar", text: err.response?.data?.message || "Inténtelo de nuevo", icon: "error" });
        }
    };

    const soloLectura = modoModal === "ver";

    // ── Render ────────────────────────────────────────────────
    return (
        <div id="cuerpo-main">
            <Br_administrativa onMinimizeChange={setMinimizado} />
            <main className={minimizado ? "minimize" : ""}>
                <section id="listar-registros">
                    <div className="encabezado"><h2>Proveedores</h2></div>

                    <div className="goated">
                        <div className="barra-buscador">
                            <input
                                type="text"
                                placeholder="Buscar por nombre o documento 🔍"
                                value={busqueda}
                                onChange={e => setBusqueda(e.target.value)}
                            />
                        </div>
                        <button className="boton-goated anadir-a-goated animacion-goated" onClick={abrirCrear}>
                            Añadir proveedor
                        </button>
                    </div>

                    <div className="listar-registros">
                        <table className="tabla-proveedores">
                            <thead>
                                <tr>
                                    <th>Código</th>
                                    <th>Nombre</th>
                                    <th>Documento</th>
                                    <th>Correo</th>
                                    <th>Teléfono</th>
                                    <th>Ciudad</th>
                                    <th>Estado</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {proveedoresFiltrados.length === 0 && (
                                    <tr><td colSpan={8} className="tabla-vacia">No hay proveedores registrados.</td></tr>
                                )}
                                {proveedoresFiltrados.map(p => (
                                    <tr key={p.id}>
                                        <td>{p.codigoProveedor}</td>
                                        <td>{p.nombre}</td>
                                        <td>{p.documento}</td>
                                        <td>{p.correo}</td>
                                        <td>{p.telefono}</td>
                                        <td>{p.ciudad}</td>
                                        <td>
                                            <span className={`badge-estado ${p.activo ? "activo" : "inactivo"}`}>
                                                {p.activo ? "Activo" : "Inactivo"}
                                            </span>
                                        </td>
                                        <td className="acciones-tabla">
                                            <button className="btn-tabla btn-ver"      onClick={() => abrirVer(p)}>Ver</button>
                                            <button className="btn-tabla btn-editar"   onClick={() => abrirEditar(p)}>Editar</button>
                                            <button className="btn-tabla btn-eliminar" onClick={() => eliminarProveedor(p)}>Desactivar</button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </section>
            </main>

            {mostrarModal && (
                <div className="ventana-overlay">
                    <div className="contenido-ventana ventana-proveedor">
                        <h3>
                            {modoModal === "crear" ? "Registrar proveedor"
                            : modoModal === "editar" ? "Editar proveedor"
                            : "Detalle del proveedor"}
                        </h3>

                        <div className="form-grid">
                            <div className="form-grupo">
                                <label>Nombre</label>
                                <input name="nombre" value={form.nombre} onChange={handleChange} disabled={soloLectura} placeholder="Nombre completo o razón social" />
                            </div>
                            <div className="form-grupo">
                                <label>Documento</label>
                                <input name="documento" value={form.documento} onChange={handleChange} disabled={soloLectura} placeholder="RUC / DNI / Pasaporte" />
                            </div>
                            <div className="form-grupo">
                                <label>Tipo de documento</label>
                                <select name="idTipoDocumento" value={form.idTipoDocumento || ""} onChange={handleChange} disabled={soloLectura}>
                                    <option value="">— Seleccione —</option>
                                    {TIPOS_DOCUMENTO.map(t => (
                                        <option key={t.id} value={t.id}>{t.nombre}</option>
                                    ))}
                                </select>
                            </div>
                            <div className="form-grupo">
                                <label>Tipo persona jurídica</label>
                                <select name="idTipoPersonaJuridica" value={form.idTipoPersonaJuridica || ""} onChange={handleChange} disabled={soloLectura}>
                                    <option value="">— Seleccione —</option>
                                    {TIPOS_PERSONA_JURIDICA.map(t => (
                                        <option key={t.id} value={t.id}>{t.nombre}</option>
                                    ))}
                                </select>
                            </div>
                            <div className="form-grupo">
                                <label>Sexo</label>
                                <select name="sexo" value={form.sexo} onChange={handleChange} disabled={soloLectura}>
                                    <option value="M">Masculino</option>
                                    <option value="F">Femenino</option>
                                </select>
                            </div>
                            <div className="form-grupo">
                                <label>Correo</label>
                                <input name="correo" type="email" value={form.correo} onChange={handleChange} disabled={soloLectura} placeholder="correo@ejemplo.com" />
                            </div>
                            <div className="form-grupo">
                                <label>Teléfono</label>
                                <input name="telefono" value={form.telefono} onChange={handleChange} disabled={soloLectura} placeholder="Número de contacto" />
                            </div>
                            <div className="form-grupo">
                                <label>Representante</label>
                                <input name="representante" value={form.representante} onChange={handleChange} disabled={soloLectura} placeholder="Nombre del representante" />
                            </div>
                            <div className="form-grupo form-grupo--full">
                                <label>Dirección</label>
                                <input name="direccion" value={form.direccion} onChange={handleChange} disabled={soloLectura} placeholder="Dirección completa" />
                            </div>
                            <div className="form-grupo">
                                <label>Ciudad</label>
                                <input name="ciudad" value={form.ciudad} onChange={handleChange} disabled={soloLectura} placeholder="Ciudad" />
                            </div>
                            <div className="form-grupo">
                                <label>Distrito</label>
                                <input name="distrito" value={form.distrito} onChange={handleChange} disabled={soloLectura} placeholder="Distrito" />
                            </div>
                            {modoModal !== "crear" && (
                                <div className="form-grupo form-grupo--checkbox">
                                    <label>
                                        <input name="activo" type="checkbox" checked={form.activo} onChange={handleChange} disabled={soloLectura} />
                                        Activo
                                    </label>
                                </div>
                            )}
                        </div>

                        <div className="acciones-de-registro">
                            {modoModal === "crear"  && <button onClick={crearProveedor}      disabled={guardando}>{guardando ? "Guardando..." : "Registrar"}</button>}
                            {modoModal === "editar" && <button onClick={actualizarProveedor} disabled={guardando}>{guardando ? "Guardando..." : "Guardar cambios"}</button>}
                            <button onClick={cerrarModal} disabled={guardando}>
                                {soloLectura ? "Cerrar" : "Cancelar"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Proveedores;