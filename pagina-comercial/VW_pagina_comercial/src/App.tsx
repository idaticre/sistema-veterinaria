
import { Route, Routes } from 'react-router-dom';
import Home from './pages/home/home';

import Tienda from './pages/publicas/Tienda';

function App() {
  return (
    <Routes>
      {/* Inicio web */}
      <Route path='/' element={<Home/>}/>

      {/* Tienda */}
      <Route path='/tienda' element={<Tienda/>}/>
      
    </Routes>
  )
}

export default App