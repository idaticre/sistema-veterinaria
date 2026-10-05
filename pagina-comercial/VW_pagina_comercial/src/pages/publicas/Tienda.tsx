import Encabezado_tienda from "./tienda_component/Encabezado_tienda"
import Filtro_tienda from "./tienda_component/Filtro_tienda"
import "./tienda.css"
import { useEffect, useState } from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import 'swiper/css';
import { FreeMode } from "swiper/modules";
import Carrito from "./tienda_component/carrito";
import { obtenerProductos, type ArticuloData } from "../../services/productosService";

type categoria = {
  id: number;
  nombre: string;
  icono: string;
};

// Mismos ids que ya usa Filtro_tienda.tsx y productosService.ts para especies.
const NOMBRE_ESPECIE: Record<number, string> = {
  1: "Perro",
  2: "Gato",
};

// Contenido decorativo del banner de la pestaña "Destacado".
// No trae ningún dato del sistema, es solo presentación.
const SLIDES_BANNER = [
  {
    titulo: "Todo para su mejor amigo",
    subtitulo: "Alimentos • Juguetes • Accesorios y mucho más",
  },
  {
    titulo: "Cuidado en cada producto",
    subtitulo: "Todo lo que tu mascota necesita, en un solo lugar",
  },
];

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
  const [slideBanner, setSlideBanner] = useState(0);

  const categorias: categoria[] = [
    {id: 0, nombre: 'Destacado', icono: '⭐'},
    {id: 1, nombre: 'Alimento', icono: '🍖'},
    {id: 2, nombre: 'juguetes', icono: '🧸'},
    {id: 3, nombre: 'Ropa y Accesorios', icono: '🎀'},
    {id: 4, nombre: 'Medicinas', icono: '💊'},
    {id: 5, nombre: 'Aseo', icono: '🧴'},
    {id: 6, nombre: 'Transporte', icono: '🚚'},
    {id: 7, nombre: 'Camas', icono: '🛏️'},
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

  // Se agregó el parámetro "cantidad" (por defecto 1) solo para que el
  // selector de cantidad del modal pueda sumar más de una unidad de una vez.
  // El botón "Agregar" de la tarjeta sigue llamando esto sin ese parámetro,
  // así que su comportamiento no cambió en nada.
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

  // Ya NO incluye el modal adentro: antes vivía dentro de cada ".card", y
  // como ".card" tiene un "transform" en :hover, eso rompía el
  // "position: fixed" del modal (quedaba encerrado dentro de la tarjeta
  // en vez de cubrir toda la pantalla). Ahora el modal se dibuja una sola
  // vez, fuera de las tarjetas, así ese problema desaparece.
  const tarjeta_produc = (producto: ArticuloData) => (
    <div className="card" key={producto.id}>
      <div className="card-img-wrap">
        <img src={producto.foto} alt={producto.titulo} className="card-img" />
      </div>
      <h3 className="card-title">{producto.titulo}</h3>
      <p className="card-marc">Marca: {producto.marca}</p>
      {NOMBRE_ESPECIE[producto.idEspecie] && (
        <p className="card-especie">🐾 {NOMBRE_ESPECIE[producto.idEspecie]}</p>
      )}
      <p className="card-price">S/ {producto.precio.toFixed(2)}</p>
      <div className="card-buttons">
        <button className="btn" onClick={() => agregarAlCarrito(producto)}>🛒 Agregar</button>
        <button className="btn btn-info" onClick={() => abrirInfo(producto.id)}>ℹ️ Info</button>
      </div>
    </div>
  );

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

    const numero = "51917233145";
    const url = `https://wa.me/${numero}?text=${encodeURIComponent(mensaje)}`;
    window.open(url, "_blank");
  }

  // Solo arma las listas por sección para poder mostrar "X productos"
  // junto al título; el filtro de cada una es exactamente el que ya había.
  const destacadosAlimento = productos.filter(
    (producto) => producto.categoriaID === 1 && producto.precio >= rangoPrecio[0] && producto.precio <= rangoPrecio[1]
  ).slice(0, 4);
  const destacadosJuguetes = productos.filter(
    (producto) => producto.categoriaID === 2 && producto.precio >= rangoPrecio[0] && producto.precio <= rangoPrecio[1]
  ).slice(0, 4);
  const destacadosMedicina = productos.filter(
    (producto) => producto.categoriaID === 1 && producto.precio >= rangoPrecio[0] && producto.precio <= rangoPrecio[1]
  ).slice(0, 4);

  const iconoCategoriaActual = categorias.find(c => c.id === categoriaSeleccionada)?.icono ?? '📦';
  const nombreCategoriaActual = categorias.find(c => c.id === categoriaSeleccionada)?.nombre ?? '';

  return (
    <>
        <Encabezado_tienda 
          cantidadCarrito={carrito.reduce((acc, item) => acc + (item.cantidad || 0), 0)}
          onAbrirCarrito={() => setAbrirCarrito(true)}
          onBusquedaChange={setBusqueda}
        />
        {!busqueda && (
          <div className="opciones_categorias">
            <Swiper
              slidesPerView="auto"
              spaceBetween={14}
              freeMode={true}
              modules={[FreeMode]}
            >
              {categorias.map(categoria =>(
                <SwiperSlide
                  key={categoria.id}
                  style={{ width: 'auto' }}
                >
                  <span onClick={() => setCategoriaSeleccionada(categoria.id)}
                    className={`${categoriaSeleccionada == categoria.id ?"Cat_select":""}`}
                  >
                    <span className="cat-icono">{categoria.icono}</span> {categoria.nombre}
                  </span>
                </SwiperSlide>
              ))}
            </Swiper>
          </div>
        )}
        {!busqueda && categoriaSeleccionada !== 0 && (
          <button
            className="btn_filtro_movil"
            onClick={() => setFiltroMovilAbierto(true)}
          >
            🔎 Filtros
          </button>
        )}
        {!busqueda && categoriaSeleccionada === 0 && (
          <div className="banner_tienda">
            <button
              className="banner_flecha banner_flecha_izq"
              onClick={() => setSlideBanner(s => (s === 0 ? SLIDES_BANNER.length - 1 : s - 1))}
              aria-label="Anterior"
            >
              ‹
            </button>
            <div className="banner_contenido">
              <img src="/perro 2.png" alt="Mascota Manada Woof" className="banner_img" />
              <div className="banner_texto">
                <h2>{SLIDES_BANNER[slideBanner].titulo}</h2>
                <p>{SLIDES_BANNER[slideBanner].subtitulo}</p>
              </div>
            </div>
            <button
              className="banner_flecha banner_flecha_der"
              onClick={() => setSlideBanner(s => (s === SLIDES_BANNER.length - 1 ? 0 : s + 1))}
              aria-label="Siguiente"
            >
              ›
            </button>
            <div className="banner_dots">
              {SLIDES_BANNER.map((_, i) => (
                <span
                  key={i}
                  className={`banner_dot ${i === slideBanner ? "activo" : ""}`}
                  onClick={() => setSlideBanner(i)}
                />
              ))}
            </div>
          </div>
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
            <div className="articulos-grid">
              {productosBusqueda.length > 0 ? (
                productosBusqueda.map(tarjeta_produc)
              ) : (
                <p>No se encontraron productos</p>
              )}
            </div>
          ) : (
            <>
              <div className={`filtro_wrapper ${filtroMovilAbierto ? "mostrar_movil" : ""}`}>
                {filtroMovilAbierto && (
                  <div className="filtro_movil_fondo" onClick={() => setFiltroMovilAbierto(false)} />
                )}
                <div className="filtro_movil_contenedor">
                  {filtroMovilAbierto && (
                    <button className="filtro_movil_cerrar" onClick={() => setFiltroMovilAbierto(false)}>✖</button>
                  )}
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
                <>
                  <div className="destacados">
                    <div className="T_categoraDesta">
                      <h2><span className="cat-icono">🍖</span> Alimento <span className="T_categoraDesta_cant">{destacadosAlimento.length} productos</span></h2>
                    </div>
                    <div className="articulos-grid">
                      {destacadosAlimento.map(tarjeta_produc)}
                    </div>
                      <div className="T_categoraDesta">
                        <h2><span className="cat-icono">🧸</span> Jueguetes <span className="T_categoraDesta_cant">{destacadosJuguetes.length} productos</span></h2>
                      </div>
                      <div className="articulos-grid">
                      {destacadosJuguetes.map(tarjeta_produc)}
                    </div>
                    <div className="T_categoraDesta">
                      <h2><span className="cat-icono">💊</span> Medicina <span className="T_categoraDesta_cant">{destacadosMedicina.length} productos</span></h2>
                    </div>
                    <div className="articulos-grid">
                      {destacadosMedicina.map(tarjeta_produc)}
                    </div>
                  </div>
                </>
              ):(
                <div className="seccion_categoria">
                  <div className="T_categoraDesta">
                    <h2><span className="cat-icono">{iconoCategoriaActual}</span> {nombreCategoriaActual} <span className="T_categoraDesta_cant">{productosFiltrados.length} productos</span></h2>
                  </div>
                  <div className="articulos-grid">
                    {productosFiltrados.length > 0 ? (
                      productosFiltrados.map(tarjeta_produc)
                    ) : (
                      <p>No se encontraron productos con estos filtros</p>
                    )}
                  </div>
                </div>
              )}
            </>
          )}
        </div>
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
                  <p><strong>Peso:</strong> {productoEnModal.kilo}</p>
                  <p className="ventanaInf_precio"><strong>Precio:</strong> S/ {productoEnModal.precio.toFixed(2)}</p>
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
                  onClick={() => { agregarAlCarrito(productoEnModal, cantidadModal); setMostrarInfo(null); }}
                >
                  🛒 Agregar al carrito
                </button>
                <button className="btn cerrar-btn" onClick={() => setMostrarInfo(null)}>Cerrar</button>
              </div>
            </div>
          </div>
        )}
    </>
  )
}

export default tienda