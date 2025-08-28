import { useState, useEffect, useRef } from "react";
import Br_administrativa from "../../../../components/barra_administrativa/Br_administrativa";
import "./desempenoDeColaboradores.css";

function desempenoDeColaboradores() {
    
    return (
        <div id="cuerpo-main">
            <Br_administrativa onMinimizeChange={setMinimizado}/>
            <main className={minimizado ? "minimize" : ""}>
                <section id="listar-registros">
                    <div className="listar-registros">
                    </div>
                </section>
            </main>
        </div>
    )
}

export default desempenoDeColaboradores