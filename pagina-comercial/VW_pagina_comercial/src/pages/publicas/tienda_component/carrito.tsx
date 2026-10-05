import { motion, AnimatePresence } from "framer-motion";
import './carrito.css'

type carritoProps = {
    onEnviarWhatsApp: () => void;
    items: { id: number; titulo: string; precio: number; cantidad: number; foto: string }[];
    onEliminar: (id: number) => void;
    onDisminuir: (id: number) => void;
    onCerrar: () => void;
    onVaciar: () => void;
    desplegado: boolean;
}

function Carrito({items, onDisminuir, onEliminar, onCerrar, onVaciar, onEnviarWhatsApp, desplegado}: carritoProps) {
    const total = items.reduce((acc, i) => acc + i.precio * i.cantidad, 0);

    return (
        <AnimatePresence>
            {desplegado && (
                <>
                    <motion.div
                        className="carrito_fondo"
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        transition={{ duration: 0.3 }}
                        onClick={onCerrar}
                    />
                    <motion.div
                        className="carrito_panel"
                        initial={{ x: "100%" }}
                        animate={{ x: 0 }}
                        exit={{ x: "100%" }}
                        transition={{ duration: 0.3 }}
                    >
                        <div className="carrito_header">
                            <h2>🛒 Carrito de Compras</h2>
                            <button className="cerrar_carrito" onClick={onCerrar}>✖</button>
                        </div>

                        {items.length === 0 ? (
                            <p className="carrito_vacio">No hay productos en el carrito</p>
                        ) : (
                            <ul className="carrito_lista">
                                {items.map(item => (
                                    <li key={item.id} className="carrito_producto">
                                        <img src={item.foto} alt={item.titulo}/>
                                        <div className="carrito_dets_produc">
                                            <span className="carrito_producto_nombre">{item.titulo}</span>
                                            <span className="carrito_producto_precio">P.Unitario: S/{item.precio.toFixed(2)}</span>
                                            <span className="carrito_producto_total">Subtotal: S/{(item.precio * item.cantidad).toFixed(2)}</span>
                                        </div>
                                        <div className="carrito_dets_btns">
                                            <button onClick={() => onDisminuir(item.id)} className="eliminar-btn">➖</button>
                                            <span className="carrito_cantidad_item">{item.cantidad}</span>
                                            <button onClick={() => onEliminar(item.id)} className="eliminar-btn carrito_btn_quitar">❌</button>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        )}

                        {items.length > 0 && (
                            <div className="carrito_footer">
                                <div className="carrito_total">
                                    <span>Total</span>
                                    <span className="carrito_total_monto">S/{total.toFixed(2)}</span>
                                </div>
                                <div className="carrito-footer-botones">
                                    <button className="btn_vaciar" onClick={onVaciar}>🗑️ Vaciar Carrito</button>
                                    <button className="btn_comprar" onClick={onEnviarWhatsApp}>✅ Realizar compra</button>
                                </div>
                            </div>
                        )}
                    </motion.div>
                </>
            )}
        </AnimatePresence>
    );
}

export default Carrito