import { useEffect, useState } from "react";
import Br_administrativa from "../../components/barra_administrativa/Br_administrativa"
import "./inventario.css";

interface producto{
  id: number;
  nombre: string;
  descrip: string;
  marca: string;
  categoria: string;
  precio: number;
  stock: number;
  proveedor: string;
  foto: string;
  activo: boolean;
}

function Inventario() {
  const [minimizado, setMinimizado] = useState(false);
  const [productos, setProductos] = useState<producto[]>([]);
  const [producfiltrado, setProducfiltrado] = useState<producto[]>([]);
  const [busqueda, setBusqueda] = useState("");
  const [mostrarModal, setMostrarModal] = useState(false);
  const [modoEdicion, setModoEdicion] = useState(false);
  const [productoActual, setProductoActual] = useState<producto | null>(null);

  useEffect(() => {
    const datos = [
        { id: 1, nombre: "rico can 15kg", categoria: "alimento", descrip: "descrip", marca: "ricocan", precio: 105, 
          stock: 8, proveedor: "tienda don pepe", foto: "/logo.png", activo: true},
      ];
      setProductos(datos);
  }, []);

  useEffect(() => {
        const palabrasBusqueda = busqueda.toLowerCase().split(" ").filter(Boolean);
  
        const resultado = productos.filter((produc) =>{
          const texto = `${produc.nombre} ${produc.marca}`.toLowerCase();
          return palabrasBusqueda.every(palabra => texto.includes(palabra));
        });
        setProducfiltrado(resultado);
  }, [busqueda, productos]);

  const abrirAgregar = () => {
    setModoEdicion(false);
    setProductoActual(null);
    setMostrarModal(true);
  };

  const abrirEditar = (produc: producto) => {
    setModoEdicion(true);
    setProductoActual(produc);
    setMostrarModal(true);
  };

  const eliminarProducto = (id: number) => {
    const nuevosProductos = productos.filter(p => p.id !== id);
    setProductos(nuevosProductos);
  };

  const guardarProducto = (nuevo: producto) => {
    if (modoEdicion) {
      const actualizados = productos.map(p =>
        p.id === nuevo.id ? nuevo : p
      );
      setProductos(actualizados);
    } else {
      setProductos([...productos, { ...nuevo, id: Date.now() }]);
    }
    setMostrarModal(false);
  };

  return (
    <div className="inventario">
      <Br_administrativa onMinimizeChange={setMinimizado}/>
      <main className={minimizado ? 'minimize' : ''}>
        <div className="lst_inventario">
          <h2>Inventario de Productos</h2>
          <div className="buscador_inventario">
            <input type="text" placeholder='Ingrese el nombre del producto' value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}/>
            <button onClick={abrirAgregar}>Añadir</button>
          </div>
          <div className="lst_inventario">
            <table className="tabla_productos">
              <thead>   
                <td>Producto</td>
                <td>Marca</td>
                <td>Categoria</td>
                <td>Precio</td>
                <td>stock</td>
                <td>proveedor</td>
                <td></td>
                <td></td>
              </thead>
              <tbody>
                {producfiltrado.length > 0 ?(
                  producfiltrado.map((produc)=>(
                    <tr key={produc.id}>
                      <td>{produc.nombre}</td>
                      <td>{produc.marca}</td>
                      <td>{produc.categoria}</td>
                      <td>S/{produc.precio.toFixed(2)}</td>
                      <td>{produc.stock}</td>
                      <td>{produc.proveedor}</td>
                      <td onClick={() => abrirEditar(produc)}>✏️</td>
                      <td onClick={() => eliminarProducto(produc.id)}>🗑️</td>
                    </tr>
                  ))
                ):(
                  <tr>
                    <td colSpan={9} style={{ textAlign: "center", padding: "10px" }}>
                      Producto no encontrado
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
        {mostrarModal && (
          <div className="inventario-modal-overlay">
            <div className="inventario-modal">
              <h3 className="inventario-modal-titulo">
                {modoEdicion ? "Editar Producto" : "Añadir Producto"}
              </h3>

              <form
                className="inventario-form"
                onSubmit={(e) => {
                  e.preventDefault();

                  const form = e.target as any;

                  const nuevoProducto: producto = {
                    id: productoActual?.id || 0,
                    nombre: form.nombre.value,
                    descrip: form.descrip.value,
                    marca: form.marca.value,
                    categoria: form.categoria.value,
                    precio: parseFloat(form.precio.value),
                    stock: parseInt(form.stock.value),
                    proveedor: form.proveedor.value,
                    foto: "/logo.png",
                    activo: true,
                  };

                  guardarProducto(nuevoProducto);
                }}
              >
                <input
                  className="inventario-input"
                  name="nombre"
                  placeholder="Nombre"
                  defaultValue={productoActual?.nombre || ""}
                  required
                />

                <input
                  className="inventario-input"
                  name="marca"
                  placeholder="Marca"
                  defaultValue={productoActual?.marca || ""}
                  required
                />

                <input
                  className="inventario-input"
                  name="categoria"
                  placeholder="Categoría"
                  defaultValue={productoActual?.categoria || ""}
                  required
                />

                <input
                  className="inventario-input"
                  name="precio"
                  type="number"
                  placeholder="Precio"
                  defaultValue={productoActual?.precio || ""}
                  required
                />

                <input
                  className="inventario-input"
                  name="stock"
                  type="number"
                  placeholder="Stock"
                  defaultValue={productoActual?.stock || ""}
                  required
                />

                <input
                  className="inventario-input"
                  name="proveedor"
                  placeholder="Proveedor"
                  defaultValue={productoActual?.proveedor || ""}
                  required
                />

                <textarea
                  className="inventario-textarea"
                  name="descrip"
                  placeholder="Descripción"
                  defaultValue={productoActual?.descrip || ""}
                />

                <div className="inventario-modal-acciones">
                  <button type="submit" className="inventario-btn guardar">
                    Guardar
                  </button>
                  <button
                    type="button"
                    className="inventario-btn cancelar"
                    onClick={() => setMostrarModal(false)}
                  >
                    Cancelar
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </main>
    </div>
  )
}

export default Inventario