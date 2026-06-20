import { useEffect, useRef, useState } from "react";
import type { Productos, ProveedorResponse } from '../../../components/interfaces/interfaces';
import Br_administrativa from "../../../components/barra_administrativa/Br_administrativa"
import IST from '../../../components/proteccion/IST'
import "./inventario.css";
import Swal from "sweetalert2";

const PRODUCTO_VACIO = {
    nombre: "",
    descripcion: "",
    marca: "",
    precio: 0,
    stock: 0,
    proveedorId: 0,
    foto: "",
    activo: true,
};

type ModoModal = "crear" | "editar" | "ver";
type ModoFoto  = "url" | "archivo";

const imagenProducto = (foto?: string) => {
    if (!foto) return null;
    return foto; // siempre URL (local del backend o externa)
};

function Inventario() {
    const [minimizado, setMinimizado]       = useState(false);
    const [productos, setProductos]         = useState<Productos[]>([]);
    const [proveedores, setProveedores]     = useState<ProveedorResponse[]>([]);
    const [busqueda, setBusqueda]           = useState("");
    const [soloActivos, setSoloActivos]     = useState(true);
    const [mostrarModal, setMostrarModal]   = useState(false);
    const [modoModal, setModoModal]         = useState<ModoModal>("crear");
    const [form, setForm]                   = useState(PRODUCTO_VACIO);
    const [idEdicion, setIdEdicion]         = useState<number | null>(null);
    const [guardando, setGuardando]         = useState(false);
    const [modoFoto, setModoFoto]           = useState<ModoFoto>("url");
    const [subiendoFoto, setSubiendoFoto]   = useState(false);
    const inputFileRef                      = useRef<HTMLInputElement>(null);

    // ── Carga inicial ─────────────────────────────────────────
    useEffect(() => {
        obtenerProductos();
        obtenerProveedores();
    }, []);

    const obtenerProductos = async () => {
        try {
            const res = await IST.get("/productos");
            setProductos(res.data.data || res.data);
        } catch {
            Swal.fire({ title: "Error al cargar productos", text: "Por favor, refresque la página", icon: "error" });
        }
    };

    const obtenerProveedores = async () => {
        try {
            const res = await IST.get("/proveedores");
            setProveedores(res.data.data || res.data);
        } catch {
            Swal.fire({ title: "Error al cargar proveedores", icon: "error" });
        }
    };

    // ── Filtros ───────────────────────────────────────────────
    const productosFiltrados = productos.filter(p => {
        const coincideNombre = p.nombre.toLowerCase().includes(busqueda.toLowerCase()) ||
                               (p.marca ?? "").toLowerCase().includes(busqueda.toLowerCase());
        const coincideActivo = soloActivos ? p.activo : true;
        return coincideNombre && coincideActivo;
    });

    // ── Manejo del form ───────────────────────────────────────
    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        const { name, value, type } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: type === "checkbox"
                ? (e.target as HTMLInputElement).checked
                : (name === "precio" || name === "stock" || name === "proveedorId")
                    ? Number(value)
                    : value,
        }));
    };

    // ── Subida de archivo ─────────────────────────────────────
    const handleArchivoSeleccionado = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const archivo = e.target.files?.[0];
        if (!archivo) return;

        setSubiendoFoto(true);
        try {
            const formData = new FormData();
            formData.append("file", archivo);

            const res = await IST.post("/archivos/subir", formData, {
                headers: { "Content-Type": "multipart/form-data" },
            });

            // El endpoint devuelve directamente la URL pública como string
            const urlPublica: string = res.data;
            setForm(prev => ({ ...prev, foto: urlPublica }));
        } catch {
            Swal.fire({ title: "Error al subir la imagen", text: "Inténtelo de nuevo", icon: "error" });
        } finally {
            setSubiendoFoto(false);
            // Limpiar input file para permitir subir el mismo archivo de nuevo
            if (inputFileRef.current) inputFileRef.current.value = "";
        }
    };

    const cambiarModoFoto = (modo: ModoFoto) => {
        setModoFoto(modo);
        setForm(prev => ({ ...prev, foto: "" })); // limpiar foto al cambiar modo
    };

    // ── Abrir modales ─────────────────────────────────────────
    const abrirCrear = () => {
        setForm(PRODUCTO_VACIO);
        setIdEdicion(null);
        setModoModal("crear");
        setModoFoto("url");
        setMostrarModal(true);
    };

    const abrirEditar = (p: Productos) => {
        setForm({
            nombre:      p.nombre,
            descripcion: p.descripcion ?? "",
            marca:       p.marca ?? "",
            precio:      p.precio,
            stock:       p.stock,
            proveedorId: 0,
            foto:        p.foto ?? "",
            activo:      p.activo,
        });
        setIdEdicion(p.id);
        setModoModal("editar");
        setModoFoto("url");
        setMostrarModal(true);
    };

    const abrirVer = (p: Productos) => {
        abrirEditar(p);
        setModoModal("ver");
    };

    const cerrarModal = () => {
        setMostrarModal(false);
        setForm(PRODUCTO_VACIO);
        setIdEdicion(null);
        setModoFoto("url");
    };

    // ── Validación ────────────────────────────────────────────
    const validar = (): boolean => {
        if (!form.nombre.trim()) {
            Swal.fire({ title: "El nombre es obligatorio", icon: "warning" }); return false;
        }
        if (form.precio < 0) {
            Swal.fire({ title: "El precio no puede ser negativo", icon: "warning" }); return false;
        }
        if (form.stock < 0) {
            Swal.fire({ title: "El stock no puede ser negativo", icon: "warning" }); return false;
        }
        if (modoModal === "crear" && form.proveedorId === 0) {
            Swal.fire({ title: "Seleccione un proveedor", icon: "warning" }); return false;
        }
        return true;
    };

    // ── POST ──────────────────────────────────────────────────
    const crearProducto = async () => {
        if (!validar()) return;
        setGuardando(true);
        try {
            await IST.post("/productos", form);
            Swal.fire({ title: "Producto registrado correctamente", icon: "success" });
            cerrarModal();
            obtenerProductos();
        } catch (err: any) {
            Swal.fire({ title: "Error al registrar", text: err.response?.data?.message || "Inténtelo de nuevo", icon: "error" });
        } finally { setGuardando(false); }
    };

    // ── PUT ───────────────────────────────────────────────────
    const actualizarProducto = async () => {
        if (!validar()) return;
        setGuardando(true);
        try {
            await IST.put(`/productos/${idEdicion}`, form);
            Swal.fire({ title: "Producto actualizado correctamente", icon: "success" });
            cerrarModal();
            obtenerProductos();
        } catch (err: any) {
            Swal.fire({ title: "Error al actualizar", text: err.response?.data?.message || "Inténtelo de nuevo", icon: "error" });
        } finally { setGuardando(false); }
    };

    // ── DELETE lógico ─────────────────────────────────────────
    const eliminarProducto = async (p: Productos) => {
        const confirmacion = await Swal.fire({
            title: "¿Desactivar producto?",
            text: `"${p.nombre}" quedará como inactivo.`,
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Sí, desactivar",
            cancelButtonText: "Cancelar",
            confirmButtonColor: "#b34d16",
        });
        if (!confirmacion.isConfirmed) return;

        try {
            await IST.delete(`/productos/${p.id}`);
            Swal.fire({ title: "Producto desactivado correctamente", icon: "success" });
            obtenerProductos();
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
                    <div className="encabezado"><h2>Inventario de productos</h2></div>

                    <div className="goated">
                        <div className="barra-buscador">
                            <input
                                type="text"
                                placeholder="Buscar por nombre o marca 🔍"
                                value={busqueda}
                                onChange={e => setBusqueda(e.target.value)}
                            />
                        </div>
                        <label className="filtro-activos">
                            <input
                                type="checkbox"
                                checked={soloActivos}
                                onChange={e => setSoloActivos(e.target.checked)}
                            />
                            Solo activos
                        </label>
                        <button className="boton-goated anadir-a-goated animacion-goated" onClick={abrirCrear}>
                            Añadir producto
                        </button>
                    </div>

                    <div className="listar-registros">
                        <div className="registros">
                            {productosFiltrados.length === 0 && (
                                <p style={{ padding: "1rem", color: "#888" }}>No hay productos registrados.</p>
                            )}
                            {productosFiltrados.map(p => (
                                <div className={`tarjeta-producto ${!p.activo ? "tarjeta-inactiva" : ""}`} key={p.id}>

                                    {/* Imagen */}
                                    <div className="tarjeta-imagen">
                                        {imagenProducto(p.foto)
                                            ? <img src={imagenProducto(p.foto)!} alt={p.nombre} onError={e => (e.currentTarget.style.display = "none")} />
                                            : <div className="imagen-placeholder">Sin imagen</div>
                                        }
                                        {!p.activo && <span className="badge-inactivo">Inactivo</span>}
                                    </div>

                                    {/* Info */}
                                    <div className="tarjeta-info">
                                        {p.codigo && <span className="tarjeta-codigo">{p.codigo}</span>}
                                        <h3 className="tarjeta-nombre-producto">{p.nombre}</h3>
                                        {p.marca && <p className="tarjeta-marca">{p.marca}</p>}
                                        {p.descripcion && <p className="tarjeta-descripcion">{p.descripcion}</p>}
                                        <p className="tarjeta-proveedor">Proveedor: <span>{p.proveedor}</span></p>
                                    </div>

                                    {/* Precio y stock */}
                                    <div className="tarjeta-pie">
                                        <div className="tarjeta-precio">S/ {Number(p.precio).toFixed(2)}</div>
                                        <div className={`tarjeta-stock ${p.stock === 0 ? "sin-stock" : p.stock <= 5 ? "stock-bajo" : ""}`}>
                                            Stock: {p.stock}
                                        </div>
                                    </div>

                                    {/* Acciones */}
                                    <div className="tarjeta-acciones">
                                        <button className="btn-tarjeta btn-ver"      onClick={() => abrirVer(p)}>Ver</button>
                                        <button className="btn-tarjeta btn-editar"   onClick={() => abrirEditar(p)}>Editar</button>
                                        <button className="btn-tarjeta btn-eliminar" onClick={() => eliminarProducto(p)}>Desactivar</button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </section>
            </main>

            {mostrarModal && (
                <div className="ventana-overlay">
                    <div className="contenido-ventana ventana-producto">
                        <h3>
                            {modoModal === "crear" ? "Registrar producto"
                            : modoModal === "editar" ? "Editar producto"
                            : "Detalle del producto"}
                        </h3>

                        {/* Preview de imagen */}
                        {form.foto && (
                            <div className="modal-imagen-preview">
                                <img
                                    src={form.foto}
                                    alt="Preview"
                                    onError={e => (e.currentTarget.style.display = "none")}
                                />
                            </div>
                        )}

                        <div className="form-grid">
                            <div className="form-grupo form-grupo--full">
                                <label>Nombre</label>
                                <input name="nombre" value={form.nombre} onChange={handleChange} disabled={soloLectura} placeholder="Nombre del producto" />
                            </div>
                            <div className="form-grupo">
                                <label>Marca</label>
                                <input name="marca" value={form.marca} onChange={handleChange} disabled={soloLectura} placeholder="Marca" />
                            </div>
                            <div className="form-grupo">
                                <label>Precio (S/)</label>
                                <input name="precio" type="number" min="0" step="0.01" value={form.precio} onChange={handleChange} disabled={soloLectura} />
                            </div>
                            <div className="form-grupo">
                                <label>Stock</label>
                                <input name="stock" type="number" min="0" value={form.stock} onChange={handleChange} disabled={soloLectura} />
                            </div>

                            {/* Combo box de proveedores */}
                            {modoModal === "crear" && (
                                <div className="form-grupo">
                                    <label>Proveedor</label>
                                    <select name="proveedorId" value={form.proveedorId || ""} onChange={handleChange} disabled={soloLectura}>
                                        <option value="">— Seleccione un proveedor —</option>
                                        {proveedores.filter(p => p.activo).map(p => (
                                            <option key={p.id} value={p.id}>{p.nombre}</option>
                                        ))}
                                    </select>
                                </div>
                            )}

                            <div className="form-grupo form-grupo--full">
                                <label>Descripción</label>
                                <textarea name="descripcion" value={form.descripcion} onChange={handleChange} disabled={soloLectura} placeholder="Descripción del producto" rows={3} />
                            </div>

                            {/* Sección de foto */}
                            {!soloLectura && (
                                <div className="form-grupo form-grupo--full">
                                    <label>Foto del producto</label>
                                    <div className="foto-toggle">
                                        <button
                                            type="button"
                                            className={`foto-toggle-btn ${modoFoto === "url" ? "activo" : ""}`}
                                            onClick={() => cambiarModoFoto("url")}
                                        >
                                            URL externa
                                        </button>
                                        <button
                                            type="button"
                                            className={`foto-toggle-btn ${modoFoto === "archivo" ? "activo" : ""}`}
                                            onClick={() => cambiarModoFoto("archivo")}
                                        >
                                            Subir archivo
                                        </button>
                                    </div>

                                    {modoFoto === "url" && (
                                        <input
                                            name="foto"
                                            value={form.foto}
                                            onChange={handleChange}
                                            placeholder="https://ejemplo.com/imagen.jpg"
                                        />
                                    )}

                                    {modoFoto === "archivo" && (
                                        <div className="foto-archivo">
                                            <input
                                                ref={inputFileRef}
                                                type="file"
                                                accept=".jpg,.jpeg,.png"
                                                onChange={handleArchivoSeleccionado}
                                                disabled={subiendoFoto}
                                            />
                                            {subiendoFoto && <span className="foto-subiendo">Subiendo imagen...</span>}
                                            {form.foto && !subiendoFoto && (
                                                <span className="foto-ok">✔ Imagen subida correctamente</span>
                                            )}
                                        </div>
                                    )}
                                </div>
                            )}

                            {/* Mostrar URL en modo ver */}
                            {soloLectura && form.foto && (
                                <div className="form-grupo form-grupo--full">
                                    <label>Foto</label>
                                    <input value={form.foto} disabled />
                                </div>
                            )}

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
                            {modoModal === "crear"  && <button onClick={crearProducto}      disabled={guardando || subiendoFoto}>{guardando ? "Guardando..." : "Registrar"}</button>}
                            {modoModal === "editar" && <button onClick={actualizarProducto} disabled={guardando || subiendoFoto}>{guardando ? "Guardando..." : "Guardar cambios"}</button>}
                            <button onClick={cerrarModal} disabled={guardando}>{soloLectura ? "Cerrar" : "Cancelar"}</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Inventario;