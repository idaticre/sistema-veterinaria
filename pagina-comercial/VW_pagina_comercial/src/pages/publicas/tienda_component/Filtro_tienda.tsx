import { useState } from "react";
import { Range, getTrackBackground } from "react-range";
import "./filtro_tienda.css";

type FiltroTiendaProps = {
  precioMaximo: number;
  onPrecioChange?: (values: number[]) => void;
  categoriaSeleccionada: number; 
  onMarcasChange?: (marcas: string[]) => void;
  onEspeciesChange?: (especies: number[]) => void;
};

type marca={
  id: number;
  nombre: string;
  id_categoria: number;
};

type especie = {
  id: number;
  nombre: string;
};


function Filtro_tienda({ onPrecioChange, precioMaximo, categoriaSeleccionada, onMarcasChange, onEspeciesChange }: FiltroTiendaProps) {
  const min = 0; 
  const [values, setValues] = useState<number[]>([min, precioMaximo]);
  const [marcasSeleccionadas, setMarcasSeleccionadas] = useState<string[]>([]);
  const [especiesSeleccionadas, setEspeciesSeleccionadas] = useState<number[]>([]);

  const handleChange = (newValues: number[]) => {
    setValues(newValues);
    onPrecioChange?.(newValues);
  };

  const handleMarcaChange = (marca: string) => {
    setMarcasSeleccionadas(prev => {
      const nuevas = prev.includes(marca)
        ? prev.filter(m => m !== marca)
        : [...prev, marca];
      onMarcasChange?.(nuevas);
      return nuevas;
    });
  };

  const handleEspecieChange = (id: number) => {
    setEspeciesSeleccionadas(prev => {
      const nuevas = prev.includes(id)
        ? prev.filter(e => e !== id)
        : [...prev, id];
      onEspeciesChange?.(nuevas);
      return nuevas;
    });
  };

  const especies: especie[] = [
    { id: 1, nombre: "Perro"},
    { id: 2, nombre: "Gato"},
    { id: 3, nombre: "Ave"},
    { id: 4, nombre: "Conejo"},
  ];
  
  const marcas: marca[] = [
    { id: 1, nombre: 'Rico Can', id_categoria: 1 },
    { id: 2, nombre: 'Rico Cat', id_categoria: 1 },
    { id: 3, nombre: 'Dog Chow', id_categoria: 1 },
    { id: 4, nombre: 'Cat Chow', id_categoria: 1 },
    { id: 5, nombre: 'Whiskas', id_categoria: 1 },
    { id: 6, nombre: 'Pedigree', id_categoria: 1 },
    { id: 7, nombre: 'Kong', id_categoria: 2 },
    { id: 8, nombre: 'Chuckit!', id_categoria: 2 },
    { id: 9, nombre: 'Nylabone', id_categoria: 2 },
    { id: 10, nombre: 'Petstages', id_categoria: 2 },
  ]

  return (
    <>
      <div className={`filtro_tienda ${categoriaSeleccionada == 0?"Ubi_destacado":""}`}>
        <div className="filtro_titulo">
          <span className="filtro_titulo_icono">🔍</span>
          <h3>Filtros</h3>
        </div>
        <div className="filtro">
          <p>Marcas</p>
          <ul className="opciones_filtro">
            {marcas.filter(marca => marca.id_categoria === categoriaSeleccionada)
            .map(marca => (
              <li key={marca.id}>
                <label>
                  <input type="checkbox" 
                    checked={marcasSeleccionadas.includes(marca.nombre)}
                    onChange={() => handleMarcaChange(marca.nombre)}
                  />
                  <span>{marca.nombre}</span>
                </label>
              </li>
            ))}
          </ul>
        </div>
        <div className="filtro"> 
          <p>Especies</p>
          <ul className="opciones_filtro">
            {especies.map(especie => (
              <li key={especie.id}>
                <label>
                  <input 
                    type="checkbox"
                    checked={especiesSeleccionadas.includes(especie.id)}
                    onChange={() => handleEspecieChange(especie.id)}
                  />
                  <span>{especie.nombre}</span>
                </label>
              </li>
            ))}
          </ul>
        </div>
        <div className="filtro_precio">
          <p>Precio</p>
          <Range
            step={1}
            min={min}
            max={precioMaximo}
            values={values}
            onChange={handleChange}
            renderTrack={({ props, children }) => (
              <div
                {...props}
                style={{
                  height: "6px",
                  width: "92%",
                  background: getTrackBackground({
                    values,
                    colors: ["#e2e8f0", "#1eab7f", "#e2e8f0"],
                    min,
                    max: precioMaximo,
                  }),
                  alignSelf: "center",
                  borderRadius: "4px",
                  margin: "0.6rem 0 0 0.7rem",
                }}
              >
                {children}
              </div>
            )}
            renderThumb={({ props }) => (
              <div
                {...props}
                style={{
                  ...props.style,
                  height: "18px",
                  width: "18px",
                  backgroundColor: "#1eab7f",
                  borderRadius: "50%",
                  boxShadow: "0 2px 6px rgba(0,0,0,0.25)",
                  border: "2px solid white",
                }}
              />
            )}
          />
          <div className="precios_mostrados">
            <span>S/ {values[0]}</span> - <span>S/ {values[1]}</span>
          </div>
        </div>
      </div>
    </>
  );
}

export default Filtro_tienda;