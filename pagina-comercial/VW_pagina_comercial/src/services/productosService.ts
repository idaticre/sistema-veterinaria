const API_URL = "http://localhost:8088/api/productos";

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

type PaginadoProductoAPI = {
  contenido: ProductoAPI[];
  totalPaginas: number;
  totalRegistros: number;
};

type ApiResponse<T> = {
  success: boolean;
  message: string;
  data: T;
};

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
  stock: number;
};

const FOTO_POR_DEFECTO = "/logo.png";

const CATEGORIA_ALIMENTO = 1;
const CATEGORIA_JUGUETES = 2;
const CATEGORIA_ROPA_ACCESORIOS = 3;
const CATEGORIA_MEDICINAS = 4;
const CATEGORIA_ASEO = 5;
const CATEGORIA_CAMAS = 7;

function inferirCategoria(texto: string): number {
  const t = texto.toLowerCase();

  if (/juguete|mordedor|pelota/.test(t)) {
    return CATEGORIA_JUGUETES;
  }

  if (
    /correa|collar|ropa|arn[eé]s|disfraz|transportadora|jaula|bolso de viaje/.test(t)
  ) {
    return CATEGORIA_ROPA_ACCESORIOS;
  }

  if (/medicina|antibi[oó]tico|vitamina|antipulga|desparasit|jarabe|tableta/.test(t)) {
    return CATEGORIA_MEDICINAS;
  }

  if (/shampoo|champ[uú]|jabon|jabón|aseo|perfume/.test(t)) {
    return CATEGORIA_ASEO;
  }

  if (/cama|cojin|coj[ií]n|manta/.test(t)) {
    return CATEGORIA_CAMAS;
  }

  return CATEGORIA_ALIMENTO;
}

function inferirEspecie(texto: string): number {
  const t = texto.toLowerCase();
  const esGato = /gato|felin|cat\b/.test(t);
  const esPerro = /perro|can\b|dog\b/.test(t);

  if (esGato && !esPerro) return 2;
  if (esPerro && !esGato) return 1;

  return 0;
}

function mapearProducto(dto: ProductoAPI): ArticuloData {
  const textoParaClasificar =
    `${dto.nombre} ${dto.descripcion ?? ""} ${dto.marca ?? ""}`;

  return {
    id: dto.id,
    titulo: dto.nombre,
    precio: Number(dto.precio) || 0,
    marca: dto.marca?.trim() || "Sin marca",
    kilo: "",
    descripcion: dto.descripcion?.trim() || "Sin descripción disponible.",
    categoriaID: inferirCategoria(textoParaClasificar),
    idEspecie: inferirEspecie(textoParaClasificar),
    foto: dto.foto?.trim() || FOTO_POR_DEFECTO,
    stock: Number(dto.stock) || 0,
  };
}

export async function obtenerProductos(): Promise<ArticuloData[]> {
  const productos: ProductoAPI[] = [];
  let pagina = 0;
  let totalPaginas = 1;

  while (pagina < totalPaginas) {
    const parametros = new URLSearchParams({
      pagina: String(pagina),
      tamanio: "100",
      soloActivos: "true",
    });

    const respuesta = await fetch(`${API_URL}?${parametros.toString()}`);

    if (!respuesta.ok) {
      throw new Error(
        `No se pudo obtener la lista de productos (HTTP ${respuesta.status})`
      );
    }

    const body: ApiResponse<PaginadoProductoAPI> = await respuesta.json();

    if (
      !body.success ||
      !body.data ||
      !Array.isArray(body.data.contenido)
    ) {
      throw new Error(
        body.message || "La respuesta del servidor no tiene el formato esperado."
      );
    }

    productos.push(...body.data.contenido);
    totalPaginas = Number(body.data.totalPaginas) || 0;
    pagina++;
  }

  return productos
    .filter((producto) => producto.activo)
    .map(mapearProducto);
}