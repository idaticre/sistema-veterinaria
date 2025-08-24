/* GM: Este .tsx permite modificar como se muestran los URL de las páginas.
PRIMERO importa la página con el nombre que desees -> import (nombre) from (path)
SEGUNDO añade tu <Route> al cuerpo de la función function App() -> <Route path='URL/queGustes/QueAparezca/EnLaBarra' element={<NombreQueUsasteEnElImport>}/> 
TERCERO, al hacer eso e importar App.tsx a tu otro .tsx deseado, podrás usar el element en vez del path en el proyecto :D (vélo en src/components/barra_administrativa/Br_administrativa.tsx) */

// Ordenado por orden de aparición en la barra administrativa lateral de la página
import { Route, Routes } from 'react-router-dom';
import Home from './pages/home/home';

import Admin_index from './pages/administrativas/home/Admin_index';

import Lst_clientes from './pages/administrativas/clientes/Clientes';
import Regis_dueños from './pages/administrativas/clientes/Regis_dueños';

import Mascotas from './pages/administrativas/mascotas/Regis_mascotas';
import Especies_razas from './pages/administrativas/mascotas/Especies_razas';
import Lst_mascotas from './pages/administrativas/mascotas/Lst_mascotas';
import Vacunas from './pages/administrativas/mascotas/Vacunas';

import Gestionar_colaboradores from  './pages/administrativas/administracion/gestionar-colaboradores/gestionarColaboradores'
import Turnos_y_horarios from './pages/administrativas/administracion/turnos-y-horarios/turnosYhorarios'
import Asistencia_de_colaboradores from './pages/administrativas/administracion/asistencia-de-colaboradores/asistenciaDeColaboradores';
import Pagos_a_colaboradores from './pages/administrativas/administracion/pagos-a-colaboradores/pagosAcolaboradores'
import Parametros_y_promociones from './pages/administrativas/administracion/parametros-y-promociones/parametrosYpromociones'
import Desempeno_de_colaboradores from './pages/administrativas/administracion/desempeno-de-colaboradores/desempenoDeColaboradores'
import Tienda from './pages/publicas/Tienda';


function App() {
  return (
    <Routes>
      {/* Inicio web */}
      <Route path='/' element={<Home/>}/>

      {/* Tienda */}
      <Route path='/tienda' element={<Tienda/>}/>

      {/* Inicio de administración */}
      <Route path='/administracion/home' element={<Admin_index/>}/>

      {/* Clientes */}
      <Route path='/administracion/cliente/lista' element={<Lst_clientes/>}/>
      <Route path='/administracion/cliente/registro' element ={<Regis_dueños/>} />
      
      {/* Mascotas */}
      <Route path='/administracion/mascotas/registro' element={<Mascotas/>}/>
      <Route path='/administracion/mascotas/espcies_razas' element={<Especies_razas/>}/>
      <Route path='/administracion/mascotas/lista' element={<Lst_mascotas/>}/>
      <Route path='/administracion/mascotas/vacunas' element={<Vacunas/>}/>
      
      {/* Mascotas */}

      {/* Historial médico */}

      {/* Agenda */}

      {/* Distribución */}

      {/* Ventas */}

      {/* Reportes e informes */}

      {/* Administración */}
      <Route path='/administracion/administracion/gestionar_colaboradores' element={<Gestionar_colaboradores/>}/>
      <Route path='/administracion/administracion/turnos_y_horarios' element={<Turnos_y_horarios/>}/>
      <Route path='/administracion/administracion/asistencia_de_colaboradores' element={<Asistencia_de_colaboradores/>}/>
      <Route path='/administracion/administracion/pagos_a_colaboradores' element={<Pagos_a_colaboradores/>}/>
      <Route path='/administracion/administracion/parametros_y_promociones' element={<Parametros_y_promociones/>}/>
      <Route path='/administracion/administracion/desempeno_de_colaboradores' element={<Desempeno_de_colaboradores/>}/>      

      {/* Seguridad y mantenimiento */}
    </Routes>
  )
}

export default App