import { Link } from 'react-router-dom'
import "./encabezado_tienda.css"

type tiendaProps = {
    cantidadCarrito: number;
    onAbrirCarrito: () => void;
    onBusquedaChange: (valor: string) => void;
}

function Encabezado_tienda({cantidadCarrito, onAbrirCarrito, onBusquedaChange}: tiendaProps) {
    
  return (
    <>
        <div className='encabezado_tienda'>
            <Link to="/" id='logo_tienda'>
                <img src="./logo.png" alt="" />
            </Link>
            <div id='buscador_tienda'>
                <svg className="buscador_icono" viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                    <circle cx="11" cy="11" r="7" fill="none" stroke="currentColor" strokeWidth="2"/>
                    <line x1="16.5" y1="16.5" x2="21" y2="21" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                </svg>
                <input 
                    type="text" 
                    placeholder="Ingrese el producto a buscar..."
                    onChange={(e) => onBusquedaChange(e.target.value)}
                />
            </div>
            <div className='carrito_btn'>
                <Link to="" onClick={onAbrirCarrito}>
                    🛒 
                    {cantidadCarrito > 0 && (
                        <span className="carrito_cantidad">{cantidadCarrito}</span>
                    )}
                </Link>
            </div>
        </div>
    </>
  )
}

export default Encabezado_tienda