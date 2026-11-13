import Encabezado_tienda from "./tienda_component/Encabezado_tienda"
import Filtro_tienda from "./tienda_component/Filtro_tienda"
import "./tienda.css"
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Carrito from "./tienda_component/carrito";
import { obtenerProductos, type ArticuloData } from "../../services/productosService";

type categoria = {
  id: number;
  nombre: string;
  icono: string;
  descripcion: string;
};

type Orden = "relevancia" | "precio_asc" | "precio_desc" | "nombre";

// Mismos ids que ya usa Filtro_tienda.tsx y productosService.ts para especies.
const NOMBRE_ESPECIE: Record<number, string> = {
  1: "Perro",
  2: "Gato",
};

// Número de WhatsApp de la veterinaria (el mismo que ya usaba "Realizar compra").
const NUMERO_WHATSAPP = "51917233145";

// Ventajas que se muestran en el hero (solo presentación, todas ciertas para este sistema).
const VENTAJAS = [
  { icono: "📲", texto: "Pedido por WhatsApp" },
  { icono: "🐾", texto: "Para perros y gatos" },
  { icono: "💬", texto: "Atención personalizada" },
  { icono: "✅", texto: "Stock actualizado" },
];

const textoCantidad = (n: number) => `${n} ${n === 1 ? "producto" : "productos"}`;

function tienda() {
  const [mostrarInfo, setMostrarInfo] = useState<number | null>(null);
  const [cantidadModal, setCantidadModal] = useState(1);
  const [categoriaSeleccionada, setCategoriaSeleccionada] = useState<number>(0);
  const [carrito, setCarrito] = useState<{ id: number; titulo: string; precio: number; cantidad: number; foto: string; }[]>([]);
  const [filtroMarcas, setFiltroMarcas] = useState<string[]>([]);
  const [filtroEspecies, setFiltroEspecies] = useState<number[]>([]);
  const [abrirCarrito, setAbrirCarrito] = useState(false);
  const [busqueda, setBusqueda] = useState("");
  const [filtroMovilAbierto, setFiltroMovilAbierto] = useState(false);
  const [orden, setOrden] = useState<Orden>("relevancia");

  const categorias: categoria[] = [
    {id: 0, nombre: 'Tienda', icono: '🏠', descripcion: 'Lo mejor para tu mascota'},
    {id: 1, nombre: 'Alimento', icono: '🍖', descripcion: 'Nutrición de calidad para su bienestar'},
    {id: 2, nombre: 'Juguetes', icono: '🧸', descripcion: 'Diversión y juego todos los días'},
    {id: 3, nombre: 'Ropa y Accesorios', icono: '🎀', descripcion: 'Estilo y comodidad para tu mascota'},
    {id: 4, nombre: 'Medicinas', icono: '💊', descripcion: 'Cuidado y salud para tu mascota'},
    {id: 5, nombre: 'Aseo', icono: '🧴', descripcion: 'Higiene y cuidado del pelaje'},
    {id: 6, nombre: 'Transporte', icono: '🚚', descripcion: 'Viaja seguro con tu mascota'},
    {id: 7, nombre: 'Camas', icono: '🛏️', descripcion: 'Descanso ideal para tu mascota'},
  ]

  // Los productos ya NO se escriben a mano acá: se traen del sistema
  // de la veterinaria (backend) apenas se carga la tienda.
  const [productos, setProductos] = useState<ArticuloData[]>([]);
  const [cargandoProductos, setCargandoProductos] = useState(true);
  const [errorProductos, setErrorProductos] = useState<string | null>(null);

  useEffect(() => {
    let activo = true;

    obtenerProductos()
      .then((data) => {
        if (activo) setProductos(data);
      })
      .catch((error) => {
        console.error("Error al obtener productos del sistema:", error);
        if (activo) setErrorProductos("No se pudieron cargar los productos. Intenta nuevamente en unos minutos.");
      })
      .finally(() => {
        if (activo) setCargandoProductos(false);
      });

    return () => { activo = false; };
  }, []);

  const precios = productos.map((p) => p.precio);
  const precioMin = precios.length > 0 ? Math.min(...precios) : 0;
  const precioMayor = precios.length > 0 ? Math.max(...precios) : 0;
  const precioMax = Math.ceil(precioMayor/100)*100;

  const [rangoPrecio, setRangoPrecio] = useState<number[]>([0, 0]);

  // El rango de precios depende de los productos, que llegan después
  // de la carga inicial (fetch asíncrono), así que se actualiza apenas
  // los productos están disponibles.
  useEffect(() => {
    if (productos.length > 0) {
      setRangoPrecio([precioMin, precioMax]);
    }
  }, [productos.length]);

  const productosFiltrados  = categoriaSeleccionada === 0 ? [] :
    productos.filter(
      (producto) =>
        producto.categoriaID === categoriaSeleccionada &&
        producto.precio >= rangoPrecio[0] && producto.precio <= rangoPrecio[1] &&
        (filtroMarcas.length === 0 || filtroMarcas.includes(producto.marca)) &&
        (filtroEspecies.length === 0 || producto.idEspecie === 0 || filtroEspecies.includes(producto.idEspecie))
  );

  const productosBusqueda = productos.filter(
    (producto) =>
      producto.titulo.toLowerCase().includes(busqueda.toLowerCase()) ||
      producto.marca.toLowerCase().includes(busqueda.toLowerCase())
  );

  // Orden de la lista (solo cambia el orden en que se muestran, no qué se muestra).
  const ordenar = (lista: ArticuloData[]) => {
    const copia = [...lista];
    if (orden === "precio_asc") copia.sort((a, b) => a.precio - b.precio);
    else if (orden === "precio_desc") copia.sort((a, b) => b.precio - a.precio);
    else if (orden === "nombre") copia.sort((a, b) => a.titulo.localeCompare(b.titulo, "es"));
    return copia;
  };

  const productosFiltradosOrdenados = ordenar(productosFiltrados);
  const productosBusquedaOrdenados = ordenar(productosBusqueda);

  // Se agregó el parámetro "cantidad" (por defecto 1) solo para que el
  // selector de cantidad del modal pueda sumar más de una unidad de una vez.
  // El botón "Añadir al carrito" de la tarjeta lo llama sin ese parámetro.
  const agregarAlCarrito = (producto: ArticuloData, cantidad: number = 1) => {
    setCarrito(prev => {
      const existe = prev.find(item => item.id === producto.id);
      if (existe) {
        return prev.map(item =>
          item.id === producto.id
            ? { ...item, cantidad: item.cantidad + cantidad }
            : item
        );
      } else {
        return [...prev, { ...producto, cantidad }];
      }
    });
  };

  useEffect(() => {
      const handleKeyDown = (e: KeyboardEvent) => {
        if (e.key === "Escape") {
          setMostrarInfo(null);
        }
      };
  
      document.addEventListener("keydown", handleKeyDown);
      return () => document.removeEventListener("keydown", handleKeyDown);
  }, []);

  const abrirInfo = (id: number) => {
    setMostrarInfo(id);
    setCantidadModal(1);
  };

  // El modal NO vive dentro de la tarjeta (el hover con transform de la tarjeta
  // rompía su "position: fixed"); se dibuja una sola vez al final del componente.
  const tarjeta_produc = (producto: ArticuloData) => {
    const agotado = producto.stock <= 0;

    return (
      <div className={`card ${agotado ? "card-agotada" : ""}`} key={producto.id}>
        <div className="card-img-wrap">
          <img src={producto.foto} alt={producto.titulo} className="card-img" />
          {agotado && <span className="card-agotado">AGOTADO</span>}
        </div>
        <h3 className="card-title">{producto.titulo}</h3>
        <p className="card-marc">Marca: {producto.marca}</p>
        {NOMBRE_ESPECIE[producto.idEspecie] && (
          <p className="card-especie">🐾 {NOMBRE_ESPECIE[producto.idEspecie]}</p>
        )}
        <p className="card-price">S/ {producto.precio.toFixed(2)}</p>
        <div className="card-buttons">
          <button
            className="btn"
            disabled={agotado}
            onClick={() => agregarAlCarrito(producto)}
          >
            {agotado ? "Agotado" : "Añadir al carrito"}
          </button>
          <button className="btn btn-info" onClick={() => abrirInfo(producto.id)}>Ver más</button>
        </div>
      </div>
    );
  };

  // Producto que corresponde al modal abierto (si hay alguno). Se busca en
  // la lista completa para que funcione sin importar desde qué sección
  // (destacados, categoría o búsqueda) se abrió.
  const productoEnModal = productos.find(p => p.id === mostrarInfo) ?? null;

  const eliminarUnidadCarrito = (id: number) => {
    setCarrito(prevCarrito =>
      prevCarrito
        .map(item =>
          item.id === id
            ? { ...item, cantidad: item.cantidad - 1 }
            : item
        )
        .filter(item => item.cantidad > 0)
    );
  };

  const enviarPorWhatsApp = () => {
    let mensaje = "Mucho gusto, quiero comprar:\n";
    carrito.forEach(item => {
      mensaje += `- ${item.titulo} x ${item.cantidad} = S/. ${(item.precio * item.cantidad).toFixed(2)}\n`;
    });
    /*const total = carrito.reduce((acc, item) => acc + item.precio * item.cantidad, 0);
    mensaje += `\nTotal: S/. ${total.toFixed(2)}`;*/

    const url = `https://wa.me/${NUMERO_WHATSAPP}?text=${encodeURIComponent(mensaje)}`;
    window.open(url, "_blank");
  }

  // Secciones de la pestaña "Tienda": hasta 4 productos de cada categoría.
  // Antes la tercera sección decía "Medicina" pero mostraba los de Alimento (categoría 1
  // repetida); ahora usa la categoría 4 (Medicinas) y no muestra secciones vacías.
  const IDS_DESTACADOS = [1, 2, 4];
  const seccionesDestacadas = IDS_DESTACADOS
    .map((id) => ({
      categoria: categorias.find(c => c.id === id)!,
      items: productos
        .filter((producto) => producto.categoriaID === id && producto.precio >= rangoPrecio[0] && producto.precio <= rangoPrecio[1])
        .slice(0, 4),
    }))
    .filter((seccion) => seccion.items.length > 0);

  const categoriaActual = categorias.find(c => c.id === categoriaSeleccionada);
  const nombreCategoriaActual = categoriaActual?.nombre ?? '';

  const elegirCategoria = (id: number) => {
    setCategoriaSeleccionada(id);
    setFiltroMovilAbierto(false);
  };

  const irAProductos = () => {
    document.getElementById("productos_destacados")?.scrollIntoView({ behavior: "smooth" });
  };

  const banda = (icono: string, nombre: string, cantidad: number, descripcion?: string, compacta = false) => (
    <div className={`banda_categoria ${compacta ? "compacta" : ""}`}>
      <span className="banda_icono">{icono}</span>
      <div className="banda_texto">
        <h2>{nombre}</h2>
        {descripcion && <p>{descripcion}</p>}
      </div>
      <span className="banda_cant">{textoCantidad(cantidad)}</span>
    </div>
  );

  const barraOrden = (
    <div className="barra_herramientas">
      <label htmlFor="orden_productos">Ordenar por:</label>
      <select
        id="orden_productos"
        value={orden}
        onChange={(e) => setOrden(e.target.value as Orden)}
      >
        <option value="relevancia">Más relevantes</option>
        <option value="precio_asc">Precio: menor a mayor</option>
        <option value="precio_desc">Precio: mayor a menor</option>
        <option value="nombre">Nombre: A - Z</option>
      </select>
    </div>
  );

  return (
    <div className="pagina_tienda">
        <Encabezado_tienda 
          cantidadCarrito={carrito.reduce((acc, item) => acc + (item.cantidad || 0), 0)}
          onAbrirCarrito={() => setAbrirCarrito(true)}
          onBusquedaChange={setBusqueda}
        />

        {!busqueda && (
          <nav className="menu_categorias" aria-label="Categorías">
            <ul className="menu_categorias_interno">
              {categorias.map(categoria => (
                <li key={categoria.id}>
                  <button
                    type="button"
                    className={`menu_item ${categoriaSeleccionada === categoria.id ? "activo" : ""}`}
                    onClick={() => setCategoriaSeleccionada(categoria.id)}
                  >
                    <span className="menu_icono">{categoria.icono}</span>
                    <span>{categoria.nombre}</span>
                  </button>
                </li>
              ))}
            </ul>
          </nav>
        )}

        <div className="contenedor_tienda">
          <div className="barra_migas">
            <Link to="/">Inicio</Link>
            <span>/</span>
            {categoriaSeleccionada === 0 && !busqueda ? (
              <span className="migas_actual">Tienda</span>
            ) : (
              <>
                <button type="button" onClick={() => { setBusqueda(""); setCategoriaSeleccionada(0); }}>
                  Tienda
                </button>
                <span>/</span>
                <span className="migas_actual">{busqueda ? "Búsqueda" : nombreCategoriaActual}</span>
              </>
            )}
          </div>

          {!busqueda && categoriaSeleccionada !== 0 && (
            <button
              className="btn_filtro_movil"
              onClick={() => setFiltroMovilAbierto(true)}
            >
              🔎 Filtros
            </button>
          )}

          {!busqueda && categoriaSeleccionada === 0 && (
            <>
              <section className="hero_tienda">
                <img src="/perro 2.png" alt="Mascota Manada Woof" className="hero_img" />
                <div className="hero_texto">
                  <h2>Todo para el <span>bienestar de tu mascota</span></h2>
                  <p>Alimentos, medicinas, accesorios y más. En un solo lugar.</p>
                  <button type="button" className="hero_boton" onClick={irAProductos}>
                    Ver productos →
                  </button>
                </div>
                <ul className="hero_ventajas">
                  {VENTAJAS.map((ventaja) => (
                    <li key={ventaja.texto}>
                      <span>{ventaja.icono}</span>
                      {ventaja.texto}
                    </li>
                  ))}
                </ul>
              </section>

              <div className="tiles_categorias">
                {categorias.filter(c => c.id !== 0).map(c => (
                  <button
                    type="button"
                    key={c.id}
                    className="tile_categoria"
                    onClick={() => setCategoriaSeleccionada(c.id)}
                  >
                    <span className="tile_icono">{c.icono}</span>
                    <span className="tile_nombre">{c.nombre}</span>
                  </button>
                ))}
              </div>
            </>
          )}

          <div className="tienda">
            {cargandoProductos ? (
              <div className="articulos-grid">
                <p>Cargando productos...</p>
              </div>
            ) : errorProductos ? (
              <div className="articulos-grid">
                <p>{errorProductos}</p>
              </div>
            ) : busqueda ? (
              <div className="seccion_categoria">
                {banda("🔎", "Resultados de la búsqueda", productosBusqueda.length, `Mostrando resultados para "${busqueda}"`)}
                {barraOrden}
                <div className="articulos-grid">
                  {productosBusquedaOrdenados.length > 0 ? (
                    productosBusquedaOrdenados.map(tarjeta_produc)
                  ) : (
                    <p>No se encontraron productos</p>
                  )}
                </div>
              </div>
            ) : (
              <>
                {/* El filtro se mantiene siempre montado (solo se oculta en "Tienda")
                    para no perder lo que el cliente ya marcó al cambiar de pestaña. */}
                <div className={`filtro_wrapper ${filtroMovilAbierto ? "mostrar_movil" : ""} ${categoriaSeleccionada === 0 ? "oculto" : ""}`}>
                  {filtroMovilAbierto && (
                    <div className="filtro_movil_fondo" onClick={() => setFiltroMovilAbierto(false)} />
                  )}
                  <div className="filtro_movil_contenedor">
                    {filtroMovilAbierto && (
                      <button className="filtro_movil_cerrar" onClick={() => setFiltroMovilAbierto(false)}>✖</button>
                    )}
                    <div className="categorias_lateral">
                      <h3 className="categorias_lateral_titulo">Categorías</h3>
                      <ul>
                        {categorias.filter(c => c.id !== 0).map(categoria => (
                          <li key={categoria.id}>
                            <button
                              type="button"
                              className={categoriaSeleccionada === categoria.id ? "activo" : ""}
                              onClick={() => elegirCategoria(categoria.id)}
                            >
                              <span>{categoria.icono}</span> {categoria.nombre}
                            </button>
                          </li>
                        ))}
                      </ul>
                    </div>
                    <Filtro_tienda
                      precioMaximo={precioMax}
                      onPrecioChange={setRangoPrecio}
                      categoriaSeleccionada={categoriaSeleccionada}
                      onMarcasChange={setFiltroMarcas}
                      onEspeciesChange={setFiltroEspecies}
                    />
                  </div>
                </div>
                {categoriaSeleccionada === 0 ? (
                  <div className="destacados" id="productos_destacados">
                    <h2 className="titulo_seccion">Productos destacados</h2>
                    {seccionesDestacadas.length > 0 ? (
                      seccionesDestacadas.map((seccion) => (
                        <div key={seccion.categoria.id}>
                          {banda(seccion.categoria.icono, seccion.categoria.nombre, seccion.items.length, undefined, true)}
                          <div className="articulos-grid">
                            {seccion.items.map(tarjeta_produc)}
                          </div>
                        </div>
                      ))
                    ) : (
                      <p>Aún no hay productos destacados.</p>
                    )}
                  </div>
                ):(
                  <div className="seccion_categoria">
                    {banda(categoriaActual?.icono ?? '📦', nombreCategoriaActual, productosFiltrados.length, categoriaActual?.descripcion)}
                    {barraOrden}
                    <div className="articulos-grid">
                      {productosFiltradosOrdenados.length > 0 ? (
                        productosFiltradosOrdenados.map(tarjeta_produc)
                      ) : (
                        <p>No se encontraron productos con estos filtros</p>
                      )}
                    </div>
                  </div>
                )}
              </>
            )}
          </div>
        </div>

        <a
          className="whatsapp_flotante"
          href={`https://wa.me/${NUMERO_WHATSAPP}`}
          target="_blank"
          rel="noreferrer"
          aria-label="Escríbenos por WhatsApp"
        >
          <svg viewBox="0 0 24 24" width="30" height="30" aria-hidden="true">
            <path d="M12 3a9 9 0 0 0-7.8 13.5L3 21l4.6-1.2A9 9 0 1 0 12 3z" fill="none" stroke="#fff" strokeWidth="1.8" strokeLinejoin="round"/>
            <path d="M9.2 7.8c-.3 0-.7.2-.9.5-.4.6-.5 1.6.3 2.9 1 1.7 2.3 3 4 3.8 1 .5 2 .5 2.6-.1.3-.3.4-.7.3-.9l-1.5-.9-.8.8c-1-.4-1.9-1.3-2.5-2.3l.7-.8-.7-1.7c-.1-.2-.3-.3-.5-.3z" fill="#fff"/>
          </svg>
        </a>

        {abrirCarrito && (
          <Carrito
            desplegado={abrirCarrito}
            items={carrito}
            onEliminar={(id) => setCarrito(prev => prev.filter(p => p.id !== id))}
            onDisminuir = {eliminarUnidadCarrito}
            onVaciar={() => setCarrito([])}
            onCerrar={() => setAbrirCarrito(false)}
            onEnviarWhatsApp={enviarPorWhatsApp}
          />
        )}
        {productoEnModal && (
          <div className="ventanaInf_producto" onClick={() => setMostrarInfo(null)}>
            <div className="ventanaInf" onClick={(e) => e.stopPropagation()}>
              <button className="ventanaInf_cerrar_x" onClick={() => setMostrarInfo(null)} aria-label="Cerrar">✖</button>
              <div className="ventanaInf_producto_contenido">
                <div className="ventanaInf_producto_img">
                  <img src={productoEnModal.foto} alt={productoEnModal.titulo} />
                </div>
                <div className="ventanaInf_producto_inf">
                  <h2>{productoEnModal.titulo}</h2>
                  <p><strong>Marca:</strong> {productoEnModal.marca}</p>
                  {NOMBRE_ESPECIE[productoEnModal.idEspecie] && (
                    <p><strong>Especie:</strong> {NOMBRE_ESPECIE[productoEnModal.idEspecie]}</p>
                  )}
                  {productoEnModal.kilo && (
                    <p><strong>Peso:</strong> {productoEnModal.kilo}</p>
                  )}
                  <p className="ventanaInf_precio"><strong>Precio:</strong> S/ {productoEnModal.precio.toFixed(2)}</p>
                  <p className={`ventanaInf_stock ${productoEnModal.stock <= 0 ? "sin_stock" : ""}`}>
                    ● {productoEnModal.stock <= 0 ? "Agotado" : "En stock"}
                  </p>
                  <p><strong>Descripción:</strong><br/>{productoEnModal.descripcion}</p>
                  <div className="ventanaInf_cantidad">
                    <span>Cantidad:</span>
                    <div className="ventanaInf_cantidad_control">
                      <button onClick={() => setCantidadModal(c => Math.max(1, c - 1))}>−</button>
                      <span>{cantidadModal}</span>
                      <button onClick={() => setCantidadModal(c => c + 1)}>+</button>
                    </div>
                  </div>
                </div>
              </div>
              <div className="ventanaInf_producto_footer">
                <button
                  className="btn ventanaInf_agregar"
                  disabled={productoEnModal.stock <= 0}
                  onClick={() => { agregarAlCarrito(productoEnModal, cantidadModal); setMostrarInfo(null); }}
                >
                  {productoEnModal.stock <= 0 ? "Agotado" : "Añadir al carrito"}
                </button>
                <button className="btn cerrar-btn" onClick={() => setMostrarInfo(null)}>Cerrar</button>
              </div>
            </div>
          </div>
        )}
    </div>
  )
}

export default tienda