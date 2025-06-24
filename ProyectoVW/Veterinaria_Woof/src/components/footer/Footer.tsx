import './footer.css';

function Pie_pagina() {
    return (
        <footer id="footer">
            <div className="footer-container">
                <div className="footer-column" id="footer-img">
                    <img src="./logo.png" alt="Logo o imagen" />
                </div>

                <div className="footer-column">
                    <h3>Información</h3>
                    <ul>
                        <li><a href="#">Sobre nosotros</a></li>
                        <li><a href="#">Servicios</a></li>
                        <li><a href="#">Política de privacidad</a></li>
                    </ul>
                </div>

                <div className="footer-column">
                    <h3>Contacto</h3>
                    <ul>
                        <li>Correo: contacto@ejemplo.com</li>
                        <li>Teléfono: (01) 123-4567</li>
                        <li>Dirección: Lima, Perú</li>
                    </ul>
                </div>
            </div>
        </footer>
    );
}

export default Pie_pagina;