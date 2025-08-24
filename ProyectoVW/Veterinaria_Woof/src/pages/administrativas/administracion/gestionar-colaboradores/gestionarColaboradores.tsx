import { useEffect, useRef, useState } from 'react'
import Br_administrativa from '../../../../components/barra_administrativa/Br_administrativa'
import { Link } from 'react-router-dom';
// Importa los css de este CRUD luego

interface Colaborador {
    ID: number,
    ID_ENTIDAD: number,
    NOMBRE: string,
    ID_USUARIO: number,
    FOTO?: string
}

function gestionarColaboradores() {
    return (
        <>
            <div id="colaboradores">
                <Br_administrativa onMinimizeChange={setMinimizado}/>
                {/* yadda yadda ya */}
            </div>
        </>
    )
}

export default gestionarColaboradores