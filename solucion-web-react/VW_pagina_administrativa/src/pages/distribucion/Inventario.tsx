import { useEffect, useState } from "react";
import Br_administrativa from "../../components/barra_administrativa/Br_administrativa"
import "./inventario.css";

interface producto{
  id: number;
  nombre: string;
  descrip: string;
  marca: string;
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

  useEffect(() => {
    const datos = [
        { id: 1, nombre: "rico can 15kg", descrip: "descrip", marca: "ricocan", precio: 105, 
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

  return (
    <div className="inventario">
      <Br_administrativa onMinimizeChange={setMinimizado}/>
      <main className={minimizado ? 'minimize' : ''}>
        <div className="lst_inventario">
          <h2>Inventario de Productos</h2>
          <div className="buscador_inventario">
            <input type="text" placeholder='Ingrese el nombre del producto' value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}/>
            <button>Añadir</button>
          </div>
          <div className="lst_inventario">
            <table className="tabla_productos">
              <thead>   
                <td>Producto</td>
                <td>descrip</td>
                <td>Marca</td>
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
                      <td>{produc.descrip}</td>
                      <td>{produc.marca}</td>
                      <td>S/{produc.precio.toFixed(2)}</td>
                      <td>{produc.stock}</td>
                      <td>{produc.proveedor}</td>
                      <td>✏️</td>
                      <td>🗑️</td>
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
      </main>
    </div>
  )
}

export default Inventario