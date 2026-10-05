// src/services/productosService.ts
//
// Este archivo se encarga de UNA sola cosa: traer los productos reales
// desde el backend del sistema de la veterinaria (Spring Boot),
// en vez de usar el arreglo "productos" que estaba escrito a mano en Tienda.tsx.
//
// Ruta real que consulta: GET http://localhost:8088/api/productos
// (ese endpoint ya existía en el backend: ProductoRestController.java)

// URL base del backend.
// OJO: cuando José defina dónde se despliega el backend (Render, etc.),
// esto se puede reemplazar por una variable de entorno (VITE_API_URL)
// para no tener que tocar el código de nuevo.
const API_URL = "http://localhost:8088/api/productos";

// Así es exactamente como el backend devuelve cada producto
// (ver ProductoResponseDTO.java). El backend NO maneja categoría,
// especie ni "kilo/peso" como campos propios: eso solo existía en el
// arreglo de ejemplo del frontend.
type ProductoAPI = {
  id: number;
  codigo: string;
  nombre: string;
  descripcion: string | null;
  marca: string | null;
  precio: number;
  stock: number;
  proveedorId: number;
  nombreProveedor: string | null;
  foto: string | null;
  activo: boolean;
};

// Respuesta genérica que envuelve todos los endpoints del backend
// (ver ApiResponse.java): { success, message, data }
type ApiResponse<T> = {
  success: boolean;
  message: string;
  data: T;
};

// Este es el mismo tipo que ya usaba la Tienda para pintar las tarjetas.
// Se mantiene igual a propósito para no tener que tocar el diseño/JSX.
export type ArticuloData = {
  id: number;
  titulo: string;
  precio: number;
  marca: string;
  kilo: string;
  descripcion: string;
  categoriaID: number;
  idEspecie: number;
  foto: string;
};

// Imagen que se muestra si el producto no tiene foto cargada en el sistema.
const FOTO_POR_DEFECTO = "/logo.png";

// -----------------------------------------------------------------------
// Categoría y especie: el backend todavía no guarda esos datos por producto
// (no existen esas columnas en la tabla "productos" real, solo en el mock).
// Mientras el equipo no agregue esos campos, se infieren por palabras clave
// en el nombre/descripción para que los filtros de la tienda sigan
// funcionando más o menos bien. Esto es un parche temporal, no una
// fuente de verdad — lo ideal a futuro es que el backend devuelva
// categoriaID/idEspecie reales.
// -----------------------------------------------------------------------

// ids según el arreglo "categorias" que ya existe en Tienda.tsx
const CATEGORIA_ALIMENTO = 1;
const CATEGORIA_JUGUETES = 2;
const CATEGORIA_ROPA_ACCESORIOS = 3;
const CATEGORIA_MEDICINAS = 4;
const CATEGORIA_ASEO = 5;
const CATEGORIA_TRANSPORTE = 6;
const CATEGORIA_CAMAS = 7;

function inferirCategoria(texto: string): number {
  const t = texto.toLowerCase();
  if (/juguete|mordedor|pelota/.test(t)) return CATEGORIA_JUGUETES;
  if (/correa|collar|ropa|arn[eé]s|disfraz/.test(t)) return CATEGORIA_ROPA_ACCESORIOS;
  if (/medicina|antibi[oó]tico|vitamina|antipulga|desparasit|jarabe|tableta/.test(t)) return CATEGORIA_MEDICINAS;
  if (/shampoo|champ[uú]|jabon|jabón|aseo|perfume/.test(t)) return CATEGORIA_ASEO;
  if (/transportadora|jaula|bolso de viaje/.test(t)) return CATEGORIA_TRANSPORTE;
  if (/cama|cojin|coj[ií]n|manta/.test(t)) return CATEGORIA_CAMAS;
  return CATEGORIA_ALIMENTO;
}

// 0 = aplica a cualquier especie (cuando no se puede saber),
// 1 = perro, 2 = gato (mismos ids que ya usaba Filtro_tienda)
function inferirEspecie(texto: string): number {
  const t = texto.toLowerCase();
  const esGato = /gato|felin|cat\b/.test(t);
  const esPerro = /perro|can\b|dog\b/.test(t);
  if (esGato && !esPerro) return 2;
  if (esPerro && !esGato) return 1;
  return 0;
}

function mapearProducto(dto: ProductoAPI): ArticuloData {
  const textoParaClasificar = `${dto.nombre} ${dto.descripcion ?? ""} ${dto.marca ?? ""}`;

  return {
    id: dto.id,
    titulo: dto.nombre,
    precio: Number(dto.precio) || 0,
    marca: dto.marca?.trim() || "Sin marca",
    kilo: "", // el backend no tiene un campo de peso/presentación todavía
    descripcion: dto.descripcion?.trim() || "Sin descripción disponible.",
    categoriaID: inferirCategoria(textoParaClasificar),
    idEspecie: inferirEspecie(textoParaClasificar),
    foto: dto.foto?.trim() || FOTO_POR_DEFECTO,
  };
}

// Trae solo los productos activos desde el sistema de la veterinaria.
export async function obtenerProductos(): Promise<ArticuloData[]> {
  const respuesta = await fetch(API_URL);

  if (!respuesta.ok) {
    throw new Error(`No se pudo obtener la lista de productos (HTTP ${respuesta.status})`);
  }

  const body: ApiResponse<ProductoAPI[]> = await respuesta.json();

  if (!body.success || !body.data) {
    throw new Error(body.message || "El sistema no devolvió productos");
  }

  return body.data
    .filter((p) => p.activo)
    .map(mapearProducto);
}