import "./Paginacion.css";

interface PaginacionProps {
    paginaActual: number;       // 0-indexed
    totalPaginas: number;
    totalRegistros: number;
    tamanio: number;            // registros por página
    onCambiarPagina: (pagina: number) => void;
    onCambiarTamanio?: (tamanio: number) => void;
}

const OPCIONES_TAMANIO = [5, 10, 20, 50];

function Paginacion({
    paginaActual,
    totalPaginas,
    totalRegistros,
    tamanio,
    onCambiarPagina,
    onCambiarTamanio,
}: PaginacionProps) {

    if (totalPaginas <= 0) return null;

    // Genera el rango de botones visibles (máx 5 páginas visibles)
    const generarPaginas = (): (number | "...")[] => {
        const paginas: (number | "...")[] = [];
        const rango = 2;

        for (let i = 0; i < totalPaginas; i++) {
            if (
                i === 0 ||
                i === totalPaginas - 1 ||
                (i >= paginaActual - rango && i <= paginaActual + rango)
            ) {
                paginas.push(i);
            } else if (
                paginas[paginas.length - 1] !== "..."
            ) {
                paginas.push("...");
            }
        }
        return paginas;
    };

    const inicio = paginaActual * tamanio + 1;
    const fin    = Math.min((paginaActual + 1) * tamanio, totalRegistros);

    return (
        <div className="paginacion-contenedor">

            {/* Info de registros */}
            <span className="paginacion-info">
                Mostrando {inicio}–{fin} de {totalRegistros} registros
            </span>

            {/* Botones de navegación */}
            <div className="paginacion-botones">
                <button
                    className="pag-btn"
                    onClick={() => onCambiarPagina(0)}
                    disabled={paginaActual === 0}
                    title="Primera página"
                >«</button>

                <button
                    className="pag-btn"
                    onClick={() => onCambiarPagina(paginaActual - 1)}
                    disabled={paginaActual === 0}
                    title="Página anterior"
                >‹</button>

                {generarPaginas().map((p, i) =>
                    p === "..." ? (
                        <span key={`ellipsis-${i}`} className="pag-ellipsis">…</span>
                    ) : (
                        <button
                            key={p}
                            className={`pag-btn ${p === paginaActual ? "pag-btn--activo" : ""}`}
                            onClick={() => onCambiarPagina(p)}
                        >
                            {p + 1}
                        </button>
                    )
                )}

                <button
                    className="pag-btn"
                    onClick={() => onCambiarPagina(paginaActual + 1)}
                    disabled={paginaActual === totalPaginas - 1}
                    title="Página siguiente"
                >›</button>

                <button
                    className="pag-btn"
                    onClick={() => onCambiarPagina(totalPaginas - 1)}
                    disabled={paginaActual === totalPaginas - 1}
                    title="Última página"
                >»</button>
            </div>

            {/* Selector de tamaño de página */}
            {onCambiarTamanio && (
                <div className="paginacion-tamanio">
                    <label>Por página:</label>
                    <select
                        value={tamanio}
                        onChange={e => {
                            onCambiarTamanio(Number(e.target.value));
                            onCambiarPagina(0); // reset a primera página
                        }}
                    >
                        {OPCIONES_TAMANIO.map(op => (
                            <option key={op} value={op}>{op}</option>
                        ))}
                    </select>
                </div>
            )}
        </div>
    );
}

export default Paginacion;
