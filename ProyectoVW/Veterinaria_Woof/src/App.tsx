import { Route, Routes } from 'react-router-dom';
import Home from './pages/home/home';
import Mascotas from './pages/administrativas/mascotas/Regis_mascotas';
import Admin_index from './pages/administrativas/Admin_index';
import Regis_dueños from './pages/administrativas/clientes/Regis_dueños';
import Especies_razas from './pages/administrativas/mascotas/Especies_razas';
import Lst_mascotas from './pages/administrativas/mascotas/Lst_mascotas';
import Lst_clientes from './pages/administrativas/clientes/Clientes';
import Vacunas from './pages/administrativas/mascotas/Vacunas';
import Tienda from './pages/publicas/Tienda';
import Encabezado_tienda from './pages/publicas/tienda_component/Encabezado_tienda';
//import Clientes from './pages/administrativas/clientes';

function App() {
  return (
    <Routes>
      {/* inico web */}
      <Route path='/' element={<Home/>}/>

      {/* tienda */}
      <Route path='/tienda' element={<Tienda/>}/>

      {/* inicio de administracion */}
      <Route path='/administracion/home' element={<Admin_index/> }/>

      {/* cliente o dueño */}
      <Route path='/administracion/cliente/lista' element={<Lst_clientes/>}/>
      <Route path='/administracion/cliente/registro' element ={<Regis_dueños/>} />
      
      {/* mascota */}
      <Route path='/administracion/mascotas/registro' element={<Mascotas/>} />
      <Route path='/administracion/mascotas/espcies_razas' element={<Especies_razas/>}/>
      <Route path='/administracion/mascotas/lista' element={<Lst_mascotas/>}/>
      <Route path='/administracion/mascotas/vacunas' element={<Vacunas/>}/>
      
      {/* tienda */}
      

    </Routes>
  )
}

export default App