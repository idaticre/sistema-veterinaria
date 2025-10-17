import { motion, AnimatePresence } from "framer-motion";
import './carrito.css'

type carritoProps = {
    onEnviarWhatsApp: () => void;
    items: { id: number; titulo: string; precio: number; cantidad: number }[];
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
                        animate={{ opacity: 0.5 }}
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
                            <h2>Carrito de Compras</h2>
                            <button className="cerrar_carrito" onClick={onCerrar}>✖</button>
                        </div>

                        {items.length === 0 ? (
                            <p>No hay productos en el carrito</p>
                        ) : (
                            <ul className="carrito_lista">
                                {items.map(item => (
                                    <li key={item.id} className="carrito_producto">
                                        <img src={`/${item.id}.jpeg`} alt={item.titulo}/>
                                        <div className="carrito_dets_produc">
                                            <span>{item.titulo}</span>
                                            <span>Cant: {item.cantidad}</span>
                                            <span>P.Unitario: S/{item.precio.toFixed(2)}</span>
                                            <span>Total S/. {(item.precio * item.cantidad).toFixed(2)}</span>
                                        </div>
                                        <div className="carrito_dets_btns">
                                            <button onClick={() => onDisminuir(item.id)} className="eliminar-btn">➖</button>
                                            <button onClick={() => onEliminar(item.id)} className="eliminar-btn">❌</button>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        )}

                        {items.length > 0 && (
                            <div className="carrito_footer">
                                <h3>Total: S/{total.toFixed(2)}</h3>
                                <div className="carrito-footer-botones">
                                    <button className="btn_vaciar" onClick={onVaciar}>Vaciar Carrito</button>
                                    <button className="btn_comprar" onClick={onEnviarWhatsApp}>Realizar compra</button>
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