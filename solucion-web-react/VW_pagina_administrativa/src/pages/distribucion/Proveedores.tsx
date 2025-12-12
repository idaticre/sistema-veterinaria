/*import { useEffect, useState } from 'react'
import Br_administrativa from '../../components/barra_administrativa/Br_administrativa';
import './proveedores.css';
import type { ProveedorRequest, ProveedorResponse } from '../../components/interfaces/interfaces';

function Proveedores() {
    const [minimizado, setMinimizado] = useState(false);
    const [busqueda, setBusqueda] = useState("");
    const [proveedores, setProveedores] =useState<ProveedorRequest[]>([]);
    const [filtrados, setFiltrados] = useState<ProveedorRequest[]>([]);
    

    useEffect(() => {
      const datos = [
          { id: 1, codigo: "vacuna 1", activo: true},
          { id: 2, codigo: "vacuna 2", activo: false},
        ];
        setProveedores(datos);
        setFiltrados(datos);
    }, []);

    useEffect(() => {
        const palabrasBusqueda = busqueda.toLowerCase().split(" ").filter(Boolean);
  
        const resultado = proveedores.filter((proveedor) =>{
          const texto = `${proveedor.codigo} ${proveedor.id}`.toLowerCase();
          return palabrasBusqueda.every(palabra => texto.includes(palabra));
        });
        setFiltrados(resultado);
    }, [busqueda, proveedores]);

    return (
        <>
            <div className='Proveedores'>
                <Br_administrativa onMinimizeChange={setMinimizado}/>
                <main className={minimizado? 'minimize' : ''}>
                    <div className='lst_proveedores'>
                        <div className=''>
                            <div id='buscador'>
                                <div id='br_buscador'>
                                    <input type="text" placeholder='Nombre del cliente.....' value={busqueda}
                                    onChange={(e) => setBusqueda(e.target.value)}/>
                                </div>
                                <div className='tbl_proveedores'>
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>ID</th>
                                                <th>CODIGO</th>
                                                <th>ESTADO</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {filtrados.map((proveedor)=>(
                                                <tr key={proveedor.id}>
                                                    <td>{proveedor.id}</td>
                                                    <td>{proveedor.codigo}</td>
                                                    <td>{proveedor.activo ? '✅' : '❌'}</td>
                                                    <td></td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </main>
            </div>
        </>
    )
}

export default Proveedores*/