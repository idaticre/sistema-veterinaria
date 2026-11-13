import { useRef } from 'react'
import { Link } from 'react-router-dom'
import "./encabezado_tienda.css"

type tiendaProps = {
    cantidadCarrito: number;
    onAbrirCarrito: () => void;
    onBusquedaChange: (valor: string) => void;
}

function Encabezado_tienda({cantidadCarrito, onAbrirCarrito, onBusquedaChange}: tiendaProps) {
  const inputRef = useRef<HTMLInputElement>(null);

  return (
    <header className='encabezado_tienda'>
        <div className="encabezado_interno">
            <Link to="/" id='logo_tienda'>
                <img src="./logo.png" alt="Manada Woof" />
            </Link>

            {/* La búsqueda ya es en vivo (filtra mientras escribes); el botón
                solo cierra el teclado en celular y da la misma sensación que el mockup. */}
            <form
                id='buscador_tienda'
                onSubmit={(e) => { e.preventDefault(); inputRef.current?.blur(); }}
            >
                <svg className="buscador_icono" viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                    <circle cx="11" cy="11" r="7" fill="none" stroke="currentColor" strokeWidth="2"/>
                    <line x1="16.5" y1="16.5" x2="21" y2="21" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                </svg>
                <input
                    ref={inputRef}
                    type="text"
                    placeholder="Buscar productos o marcas..."
                    onChange={(e) => onBusquedaChange(e.target.value)}
                />
                <button type="submit" aria-label="Buscar">
                    <svg viewBox="0 0 24 24" width="22" height="22" aria-hidden="true">
                        <circle cx="11" cy="11" r="7" fill="none" stroke="currentColor" strokeWidth="2"/>
                        <line x1="16.5" y1="16.5" x2="21" y2="21" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                    </svg>
                </button>
            </form>

            <div className='carrito_btn'>
                <Link to="" onClick={onAbrirCarrito}>
                    <span className="carrito_icono">
                        <svg viewBox="0 0 24 24" width="28" height="28" aria-hidden="true">
                            <path d="M3 4h2.5l2.2 10.2a1.5 1.5 0 0 0 1.5 1.2h8.3a1.5 1.5 0 0 0 1.5-1.1L20.5 8H6.4" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"/>
                            <circle cx="9.5" cy="19.2" r="1.4" fill="currentColor"/>
                            <circle cx="16.5" cy="19.2" r="1.4" fill="currentColor"/>
                        </svg>
                        {cantidadCarrito > 0 && (
                            <span className="carrito_cantidad">{cantidadCarrito}</span>
                        )}
                    </span>
                    <span className="carrito_texto">Mi carrito</span>
                </Link>
            </div>
        </div>
    </header>
  )
}

export default Encabezado_tienda