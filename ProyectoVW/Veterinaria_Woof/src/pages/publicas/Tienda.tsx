import Encabezado_tienda from "./tienda_component/Encabezado_tienda"
import Filtro_tienda from "./tienda_component/Filtro_tienda"
import "./tienda.css"
import { useEffect, useState } from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import 'swiper/css';
import { FreeMode } from "swiper/modules";


type ArticuloData = {
  id: number;
  titulo: string;
  precio: number;
  marca: string;
  kilo: string;
  descripcion: string;
  categoriaID: number;
  idEspecie: number;
};

type categoria = {
  id: number;
  nombre: string;
};

function tienda() {
  const [mostrarInfo, setMostrarInfo] = useState<number | null>(null);
  const [categoriaSeleccionada, setCategoriaSeleccionada] = useState<number>(0);
  const [carrito, setCarrito] = useState<{ id: number; titulo: string; precio: number; cantidad: number; }[]>([]);
  const [filtroMarcas, setFiltroMarcas] = useState<string[]>([]);
  const [filtroEspecies, setFiltroEspecies] = useState<number[]>([]);

  const categorias: categoria[] = [
    {id: 0, nombre: 'Destacado',},
    {id: 1, nombre: 'Alimento'},
    {id: 2, nombre: 'juguetes'},
    {id: 3, nombre: 'Ropa y Accesorios'},
    {id: 4, nombre: 'Medicinas'},
    {id: 5, nombre: 'Aseo'},
    {id: 6, nombre: 'Transporte'},
    {id: 7, nombre: 'Camas'},
  ]

  const productos: ArticuloData[] = [
    {
      id: 1,
      titulo: 'Rico Cat Gatitos',
      precio: 128.00,
      marca: 'Rico Cat',
      kilo: '15 Kg',
      descripcion: '####.',
      categoriaID: 1,
      idEspecie: 2,
    },
    {
      id: 2,
      titulo: 'Dog Chow Adultos',
      precio: 31.80,
      marca: 'Dog Chow  ',
      kilo: '1.5 Kg',
      descripcion: '####.',
      categoriaID: 1,
      idEspecie: 2 
    },
    {
      id: 3,
      titulo: 'WHISKAS',
      precio: 36.69,
      marca: 'WHISKAS',
      kilo: '1.5 Kg',
      descripcion: '####.',
      categoriaID: 1,
      idEspecie: 2 
    },
    {
      id: 4,
      titulo: 'URINARY CARE',
      precio: 140.00,
      marca: 'URINARY CARE',
      kilo: '1.81 Kg',
      descripcion: '####.',
      categoriaID: 1,
      idEspecie: 2 
    },
    {
      id: 5,
      titulo: 'RICOCAN',
      precio: 132.00,
      marca: 'Rico Can',
      kilo: '15 Kg',
      descripcion: '####.',
      categoriaID: 1,
      idEspecie: 1 
    },
    {
      id: 6,
      titulo: 'DOG CHOW',
      precio: 49.50,
      marca: 'DOG CHOW',
      kilo: '3 Kg',
      descripcion: '####.',
      categoriaID: 1,
      idEspecie: 1 
    },
    {
      id: 7,
      titulo: 'PEDIGREE',
      precio: 78.50,
      marca: 'PEDIGREE',
      kilo: '1 Kg',
      descripcion: '####.',
      categoriaID: 1,
      idEspecie: 1 
    },
    {
      id: 8,
      titulo: 'Juguete Mordedor',
      precio: 29.90,
      marca: 'Kong',
      kilo: '1 Kg',
      descripcion: 'Juguete de caucho resistente con soga para juegos interactivos',
      categoriaID: 2,
      idEspecie: 1 
    }
  ];

  const precios = productos.map((p) => p.precio);
  const precioMin = Math.min(...precios);
  const precioMayor = Math.max(...precios);
  const precioMax = Math.ceil(precioMayor/100)*100;

  const [rangoPrecio, setRangoPrecio] = useState<number[]>([precioMin, precioMax]);

  const productosFiltrados  = categoriaSeleccionada === 0 ? [] :
  productos.filter(
    (producto) =>
      producto.categoriaID === categoriaSeleccionada &&
      producto.precio >= rangoPrecio[0] && producto.precio <= rangoPrecio[1] &&
      (filtroMarcas.length === 0 || filtroMarcas.includes(producto.marca)) &&
      (filtroEspecies.length === 0 || filtroEspecies.includes(producto.idEspecie))
  );

  const agregarAlCarrito = (producto: ArticuloData) => {
    setCarrito(prev => {
      const existe = prev.find(item => item.id === producto.id);
      if (existe) {
        return prev.map(item =>
          item.id === producto.id
            ? { ...item, cantidad: item.cantidad + 1 }
            : item
        );
      } else {
        return [...prev, { ...producto, cantidad: 1 }];
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

  const trajeta_produc = (producto: ArticuloData) => (
    <div className="card" key={producto.id}>
      <img src={`/${producto.id}.jpeg`} alt={producto.titulo} className="card-img" />
      <h3 className="card-title">{producto.titulo}</h3>
      <p className="card-marc">Marca: {producto.marca}</p>
      <p className="card-price">S/. {producto.precio.toFixed(2)}</p>
      <div className="card-buttons">
        <button className="btn" onClick={() => agregarAlCarrito(producto)}>Agregar</button>
        <button className="btn" onClick={() => setMostrarInfo(producto.id)}>Info</button>
      </div>
      {mostrarInfo === producto.id && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-content">
              <div className="modal-info">
                <h2>{producto.titulo}</h2>
                <p><strong>Marca:</strong> {producto.marca}</p>
                <p><strong>Peso:</strong> {producto.kilo}</p>
                <p><strong>Precio:</strong> S/. {producto.precio.toFixed(2)}</p>
                <p><strong>Descripción:</strong> <br/>{producto.descripcion}</p>
              </div>
              <div className="modal-img">
                <img src={`/${producto.id}.jpeg`} alt={producto.titulo} />
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn cerrar-btn" onClick={() => setMostrarInfo(null)}>Cerrar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );

  const enviarPorWhatsApp = () => {
    let mensaje = "Mucho gusto, quiero comprar:\n";
    carrito.forEach(item => {
      mensaje += `- ${item.titulo} x ${item.cantidad} = S/. ${(item.precio * item.cantidad).toFixed(2)}\n`;
    });
    /*const total = carrito.reduce((acc, item) => acc + item.precio * item.cantidad, 0);
    mensaje += `\nTotal: S/. ${total.toFixed(2)}`;*/

    const numero = "51982838996";
    const url = `https://wa.me/${numero}?text=${encodeURIComponent(mensaje)}`;
    window.open(url, "_blank");
  }

  return (
    <>
        <Encabezado_tienda 
          onEnviarWhatsApp={enviarPorWhatsApp}
          cantidadCarrito={carrito.reduce((acc, item) => acc + (item.cantidad || 0), 0)}
        />
        <div className="opciones_categorias">
          <Swiper
            slidesPerView="auto"
            spaceBetween={30}
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
                  {categoria.nombre}
                </span>
              </SwiperSlide>
            ))}
          </Swiper>
        </div>
        <div className="tienda">
          <Filtro_tienda
            precioMaximo={precioMax}
            onPrecioChange={setRangoPrecio}
            categoriaSeleccionada={categoriaSeleccionada}
            onMarcasChange={setFiltroMarcas}
            onEspeciesChange={setFiltroEspecies}
          />
          {categoriaSeleccionada === 0 ? (
            <>
              <div className="destacados">
                <div className="T_categoraDesta">
                  <h2>Alimento</h2>
                </div>
                <div className="articulos-grid">
                  {productos
                    .filter(
                      (producto) =>
                        producto.categoriaID === 1 &&
                        producto.precio >= rangoPrecio[0] &&
                        producto.precio <= rangoPrecio[1]
                    )
                    .slice(0, 4)
                    .map(trajeta_produc)}
                </div>
                  <div className="T_categoraDesta">
                    <h2>Jueguetes</h2>
                  </div>
                  <div className="articulos-grid">
                  {productos
                    .filter(
                      (producto) =>
                        producto.categoriaID === 2 &&
                        producto.precio >= rangoPrecio[0] &&
                        producto.precio <= rangoPrecio[1]
                    )
                    .slice(0, 4)
                    .map(trajeta_produc)}
                </div>
                <div className="T_categoraDesta">
                  <h2>Medicina</h2>
                </div>
                <div className="articulos-grid">
                  {productos
                    .filter(
                      (producto) =>
                        producto.categoriaID === 1 &&
                        producto.precio >= rangoPrecio[0] &&
                        producto.precio <= rangoPrecio[1]
                    )
                    .slice(0, 4)
                    .map(trajeta_produc)}
                </div>
              </div>
            </>
          ):(
            <>
              <div className="articulos-grid">
                {productosFiltrados.map(trajeta_produc)}
              </div>
            </>
          )}
        </div>
    </>
  )
}

export default tienda