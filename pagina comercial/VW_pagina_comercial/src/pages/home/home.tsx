import Encabezado from '../../components/encabezado/Emcabezado';
import Footer from '../../components/footer/Footer';
import Carousel from '../../components/Carousel/Carousel';
import MarcasTicker from '../../components/MarcasTicker/MarcasTicker';
import Horario from '../../components/Horario/Horario';
import Mapa from '../../components/Map/Map';
import Instalaciones from '../../components/Instalaciones/Instalaciones';
import Testimonials from '../../components/Testimonials/Testimonials';

function Home() {
  return (
    <>
    <section className='home'>
      <Encabezado />
      <Carousel />
      <MarcasTicker />
      <Horario />
      <Mapa />

      <div id="servicios">
        <Instalaciones />
      </div>
      
      <div id="huellitas">
        <Testimonials />
      </div>

      <Footer />
    </section>
    </>
  )
}

export default Home