/* GM: Este .tsx permite modificar como se muestran los URL de las páginas.
PRIMERO importa la página con el nombre que desees -> import (nombre) from (path)
SEGUNDO añade tu <Route> al cuerpo de la función function App() -> <Route path='URL/queGustes/QueAparezca/EnLaBarra' element={<NombreQueUsasteEnElImport>}/> 
TERCERO, al hacer eso e importar App.tsx a tu otro .tsx deseado, podrás usar el element en vez del path en el proyecto :D (vélo en src/components/barra_administrativa/Br_administrativa.tsx) 

Ordenado por orden de aparición en la barra administrativa lateral de la página*/

import { Navigate, Route, Routes } from 'react-router-dom';

import Login from './pages/login/Login';

import Admin_index from './pages/administrativas/home/Admin_index';

import Lst_clientes from './pages/administrativas/clientes/Clientes';
import Regis_dueños from './pages/administrativas/clientes/Regis_dueños';

import Mascotas from './pages/administrativas/mascotas/Regis_mascotas';
import Especies_razas from './pages/administrativas/mascotas/Especies_razas';
import Lst_mascotas from './pages/administrativas/mascotas/Lst_mascotas';
import Vacunas from './pages/administrativas/mascotas/Vacunas';

import Servicios from './pages/administrativas/servicios/servicios';

import Agenda_general from './pages/administrativas/agenda/Agenda_general';
import EditarCita from './pages/administrativas/agenda/EditarAgendas/EditarCita';

import Reportes_e_informes from './pages/administrativas/reportes_e_informes/clientes';

import Gestionar_colaboradores from  './pages/administrativas/administracion/gestionar-colaboradores/gestionarColaboradores'
import Gestionar_usuarios from  './pages/administrativas/administracion/gestionar-usarios/gestionarUsuarios'
import Turnos_y_horarios from './pages/administrativas/administracion/turnos-y-horarios/turnosYhorarios'
import Asistencia_de_colaboradores from './pages/administrativas/administracion/asistencia-de-colaboradores/asistenciaDeColaboradores';
import Pagos_a_colaboradores from './pages/administrativas/administracion/pagos-a-colaboradores/pagosAcolaboradores'
import Parametros_y_promociones from './pages/administrativas/administracion/parametros-y-promociones/parametrosYpromociones'

import AsignarRolesPermisos from './pages/administrativas/administracion/Asignar-Gestionar-Roles/AsignarRolesPermisos';

import DashboardAdministrativo from './pages/administrativas/administracion/DashboardAdministrativo/DashboardAdministrativo';
import RutaProtegida from './components/proteccion/IPRT';
import RutaProtegidaPorRol from './components/proteccion/IPRR';
import IPRR from './components/proteccion/IPRR';
import Inventario from './pages/distribucion/Inventario';
import HistorialM from './pages/administrativas/mascotas/historial clinico/HistorialM';

function App() {

  return (
    <Routes>
      <Route path="/" element={<Navigate to="/administracion/login" replace />} />
      
      {/* Login */}
      <Route path='/administracion/login' element={<Login/>} />
      
      <Route element={<RutaProtegida />}>
      
        {/* Inicio de administración */}
        <Route path='/administracion/home'  element={<Admin_index/>}/>

        {/* Clientes */}
        <Route element={<IPRR roles={['ADMINISTRADOR GENERAL','AUXILIAR CAJA']} />} >
          <Route path='/administracion/cliente/registro' element ={<Regis_dueños/>} />
        </Route>
        <Route element={<IPRR roles={['ADMINISTRADOR GENERAL','AUXILIAR CAJA', 'AUXILIAR GROMERS']} />} >
          <Route path='/administracion/cliente/lista' element={<Lst_clientes/>}/>
        </Route>

        {/* Mascotas */}
        <Route element={<IPRR roles={['ADMINISTRADOR GENERAL', 'AUXILIAR CAJA']} />} >
          <Route path='/administracion/mascotas/registro' element={<Mascotas/>}/>
          <Route path='/administracion/mascotas/especies_razas' element={<Especies_razas/>}/>
          <Route path='/administracion/mascotas/vacunas' element={<Vacunas/>}/>
        </Route>
        <Route element={<IPRR roles={['ADMINISTRADOR GENERAL','AUXILIAR CAJA', 'AUXILIAR GROMERS']} />} >
          <Route path='/administracion/mascotas/lista' element={<Lst_mascotas/>}/>
        </Route>

        {/* Historial médico */}
        <Route element={<IPRR roles={['ADMINISTRADOR GENERAL']}/>}>
          <Route path='/administracion/historia-clinica' element={<HistorialM/>}/>
        </Route>
        
        {/* Servicios*/}
        <Route element={<RutaProtegidaPorRol roles={["ADMINISTRADOR GENERAL", "AUXILIAR CAJA"]} />}>
          <Route path='/administracion/servicios' element={<Servicios/>}/>      
        </Route>

        
        {/* Agenda */}
        <Route element={<RutaProtegidaPorRol roles={["ADMINISTRADOR GENERAL", "AUXILIAR CAJA"]} />}>
          <Route path='/administracion/agenda/EditarCita' element={<EditarCita />} />
        </Route>
        <Route element={<RutaProtegidaPorRol roles={["ADMINISTRADOR GENERAL", "AUXILIAR CAJA", "AUXILIAR GROMERS"]} />}>
          <Route path='/administracion/agenda/Agenda_general' element={<Agenda_general />} />
        </Route>

        {/* Distribución */}
        <Route element={<RutaProtegidaPorRol roles={["ADMINISTRADOR GENERAL"]} />}>
          <Route path='/administracion/distribucion/inventario' element={<Inventario/>}/>
        </Route>

        {/* Ventas */}

        {/* Reportes e informes */}
        <Route element={<RutaProtegidaPorRol roles={["ADMINISTRADOR GENERAL"]}/>}>
          <Route path='/administracion/reportes_e_informes/clientes' element={<Reportes_e_informes/>}/>
        </Route>

        {/* Administración */}
        <Route element={<RutaProtegidaPorRol roles={["ADMINISTRADOR GENERAL"]} />}>
          <Route path='/administracion/administracion/gestionar_colaboradores' element={<Gestionar_colaboradores/>}/>
          <Route path='/administracion/administracion/gestionar_usuarios' element={<Gestionar_usuarios/>}/>
          <Route path='/administracion/administracion/turnos_y_horarios' element={<Turnos_y_horarios/>}/>
          <Route path='/administracion/administracion/asistencia_de_colaboradores' element={<Asistencia_de_colaboradores/>}/>
          <Route path='/administracion/administracion/pagos_a_colaboradores' element={<Pagos_a_colaboradores/>}/>
          <Route path='/administracion/administracion/parametros_y_promociones' element={<Parametros_y_promociones/>}/>
        </Route>

        {/* Aquí van las nuevas páginas administrativas */}
        <Route element={<RutaProtegidaPorRol roles={["ADMINISTRADOR GENERAL"]} />}>
          <Route path='/administracion/administracion/dashboard_administrativo' element={<DashboardAdministrativo/>}/>
        </Route>  

        {/* Seguridad y mantenimiento */}
        <Route element={<RutaProtegidaPorRol roles={["ADMINISTRADOR GENERAL"]} />}>
          <Route path='/administracion/administracion/Asignar_roles_y_permisos' element={<AsignarRolesPermisos/>}/>
        </Route>

      </Route>
    </Routes>
  )
}

export default App