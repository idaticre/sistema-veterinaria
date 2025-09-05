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
                <input 
                    type="text" 
                    placeholder="ingrese el producto a buscar"
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