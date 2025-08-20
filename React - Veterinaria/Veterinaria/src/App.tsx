import Header from './Components/Header/Header';
import Horario from './Components/Horario/Horario';
import Map from './Components/Map/Map';
import Footer from './Components/Footer/Footer';
import Carousel from "./Components/Carousel/Carousel";
import Testimonials from './Components/Testimonials/Testimonials';
import Marca from './Components/MarcasTicker/MarcasTicker';
import Instalaciones from './Components/Instalaciones/Instalaciones';

function App() {
  return (
    <div>
      <Header />
      <Carousel />
      <Marca />
      <Horario />
      <Map />

      <div id="instalaciones">
        <Instalaciones />
      </div>
      
      <div id="huellitas">
        <Testimonials />
      </div>

      <Footer />
    </div>
  );
}

export default App;
