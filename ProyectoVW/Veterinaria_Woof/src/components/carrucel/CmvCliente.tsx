import { useEffect, useRef } from "react";
import Swiper from "swiper";
import { Navigation, Pagination, Autoplay } from "swiper/modules";

import 'swiper/css';
import 'swiper/css/navigation';
import 'swiper/css/pagination';

import "./CmvCliente.css";

function Carrucel() {
    const swiperRef1 = useRef(null);

    useEffect(() => {
        if (swiperRef1.current) {

            Swiper.use([Navigation, Pagination, Autoplay]);

            new Swiper(swiperRef1.current, {
                slidesPerView: 1,
                spaceBetween: 30,
                loop: true,
                autoplay: {
                    delay: 5000,
                    disableOnInteraction: false,
                },
                pagination: {
                    el: ".swiper-pagination",
                    clickable: true,
                },
                navigation: {
                    nextEl: ".swiper-button-next",
                    prevEl: ".swiper-button-prev",
                },
            });
        }
    }, []);

    return (
        <section id="content">
            <h2>FOTOS DE NUESTROS CLIENTES</h2>
            <p>
                Nos llena de alegría ver a tantos peluditos saludables y bien cuidados. 
                Aquí compartimos algunos momentos especiales junto a nuestras queridas mascotas 
                y sus familias. ¡Gracias por confiar en nosotros!
            </p>
            <div className="swiper mySwiper-1" ref={swiperRef1}>
                <div className="swiper-wrapper">

                    <div className="swiper-slide">
                        <div className="slider">
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria1.jpg" alt=""/>
                            </div>
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria1.jpg" alt=""/>
                            </div>
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria1.jpg" alt=""/>
                            </div>
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria1.jpg" alt=""/>
                            </div>
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria1.jpg" alt=""/>
                            </div>
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria1.jpg" alt=""/>
                            </div>
                        </div>
                    </div>

                    <div className="swiper-slide">
                        <div className="slider">
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria2.jpg" alt=""/>
                            </div>
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria2.jpg" alt=""/>
                            </div>
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria2.jpg" alt=""/>
                            </div>
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria2.jpg" alt=""/>
                            </div>
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria2.jpg" alt=""/>
                            </div>
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria2.jpg" alt=""/>
                            </div>
                        </div>
                    </div>

                    <div className="swiper-slide">
                        <div className="slider">
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria3.jpg" alt="" />
                            </div>
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria3.jpg" alt="" />
                            </div>
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria3.jpg" alt="" />
                            </div>
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria3.jpg" alt="" />
                            </div>
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria3.jpg" alt="" />
                            </div>
                            <div className="slider-img">
                                <img src="src/assets/carrucel/veterinaria3.jpg" alt="" />
                            </div>
                        </div>
                    </div>

                </div>
                <div className="swiper-button-next"></div>
                <div className="swiper-button-prev"></div>
                <div className="swiper-pagination"></div>
            </div>
        </section>
    )
}

export default Carrucel;