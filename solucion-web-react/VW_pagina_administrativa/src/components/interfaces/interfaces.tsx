export interface EntidadRequest{
    id?: number;
    idTipoPersonaJuridica: number;
    nombre: string;
    sexo?: "M" | "F";
    documento: string;
    idTipoDocumento: number;
    correo: string;
    telefono: string;
    direccion: string;
    ciudad: string;    
    distrito: string;
    representante: string;
    activo: boolean;
}

export interface ClienteResquest{
    idEntidad?:number;
    idTipoPersonaJuridica: number;
    nombre: string;
    sexo?: "M" | "F";
    documento: string;
    idTipoDocumento: number;
    correo: string;
    telefono: string;
    direccion: string;
    ciudad: string;
    distrito: string;
    representante?: string;
    activo: boolean;
}

export interface ColaboradorRequest{
    id?: number;
    nombre: string;
    sexo: "M" | "F";
    documento:  string;
    idTipoPersonaJuridica: number;
    idTipoDocumento: number;
    correo: string;
    telefono: string;
    direccion:string;
    ciudad: string;
    distrito: string;
    idUsuario?: number | null;
    activo: boolean;
    fechaIngreso: string;
    foto?: string;
}

export interface ColaboradorResponse{
    id: number;
    codigoColaborador: string;
    idEntidad: number;
    nombre: string;
    sexo: string;
    documento: string;
    idTipoPersonaJuridica: number;
    idTipoDocumento: number;
    correo: string;
    telefono: string;
    direccion: string;
    ciudad: string;
    distrito: string;
    usuario: number;    // es un ID aunque el nombre no lo diga
    activo: boolean;
    fechaRegistro: string;
    fechaIngreso: string;
    foto?: string;
    mensaje: string;
}

export interface ProveedorRequest{
    id: number;
    idTipoPersonaJuridica: number;
    nombre: string;
    sexo: "M" | "F";
    documento: string;
    idTipoDocumento: number;
    correo: string;
    telefono: string;
    direccion: string;
    ciudad: string;
    distrito: string;
    representante: string;
    activo: boolean;
}

export interface ProveedorResponse{
    id: number;
    codigoProveedor: string;
    idEntidad: number;
    nombre: string;
    sexo: string;
    documento: string;
    idTipoPersonaJuridica: number;
    idTipoDocumento: number;
    correo: string;
    telefono: string;
    direccion: string;
    ciudad: string;
    distrito: string;
    representante: string;
    activo: boolean;
    fechaRegistro: string;
    mensaje: string;
}

export interface veterinarioRequest{
    id: number;
    nombre: string;
    sexo: "M" | "F";
    documento: string;
    idTipoPersonaJuridica: number;
    idTipoDocumento: number;
    correo: string;
    telefono: string;
    direccion: string;
    ciudad: string;
    distrito: string;
    idUsuario: number;
    activo: boolean;
    foto: string;
    idEspecialidad: number;
    cmp: string;
}

export interface MascotaRequest {
  id?: number;
  nombre: string;
  sexo?: "M" | "H";
  idCliente: number;
  idRaza: number;
  idEspecie: number;
  idEstado: number;
  fechaNacimiento: string;
  pelaje: string;
  idTamano: number;
  idEtapa: number;
  esterilizado: boolean;
  alergias: string;
  peso?: number;
  chip: boolean;
  pedigree: boolean;
  factorDea: boolean;
  agresividad: boolean;
  foto: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
}

export interface ClienteResponse{
    id: number;
    codigoCliente: string;
    idEntidad: number;
    nombre: string;
    sexo: "M" | "F";
    documento: string;
    idTipoPersonaJuridica: number;
    idTipoDocumento: number;
    correo: string;
    telefono: string;
    direccion: string;
    ciudad: string;
    distrito: string;
    activo: boolean;
    representante?: string;
    fechaRegistro: string;
    mensaje: string;
}

export interface MascotaResponse {
    id?: number;
    codigo: string;
    nombre: string;
    sexo: "M" | "H";
    cliente?: {id: number; nombre: string;};
    raza?: {id: number; nombre: string;};
    especie?: {id: number; nombre: string;};
    estado?: {id: number; nombre: string;};
    tamano?: {id: number; nombre: string;};
    etapa?: {id: number; nombre: string;};
    fechaNacimiento: string;
    pelaje: string;
    esterilizado: boolean;
    alergias: string;
    peso?: number;
    chip: boolean;
    pedigree: boolean;
    factorDea: boolean;
    agresividad: boolean;
    foto: string;

    fechaRegistro?: string;
    fechaModificacion: string;

    idColaborador?: number | null;
    idVeterinario?: number | null;
}
export interface EntidadResponse{
    id: number;
    codigo: string;
    nombre: string;
    correo: string;
    telefono: string;
    sexo: string;
    documento: string;
    direccion: string;
    idTipoDocumento: number;
    idTipoPersonaJuridica: number;
    ciudad: string;
    distrito: string;
    representante: string;
    activo: boolean;
    tipoDocumento: string;
    tipoPersonaJuridica: string;
    fechaRegistro: string;
}

export interface veterinarioResponse{
    id: number;
    codigo: string;
    cmp: string;
    especialidad: string;
    usuario: string;
    activo: boolean;
    fechaRegistro: string;
    fechaIngreso: string;
    foto: string;
    idColaborador: number;
    idEntidad: number;
    nombre: string;
    sexo: string;
    documento: string;
    idTipoPersonaJuridica: number;
    idTipoDocumento: number;
    correo: string;
    telefono: string;
    direccion: string;
    ciudad: string;
    distrito: string;
    mensaje: string;
}

export interface HistorialCResponse {
  id: number;
  codigo: string;
  idMascota: number;
  fechaApertura: string;
  observacionesGenerales: string | null;
  activo: boolean;
  fechaRegistro: string;
  mensaje: string | null;
}

export interface CitaResponse {
  id: number;
  codigo: string;
  idCliente: number;
  idMascota: number;
  idMedioSolicitud: number;
  fecha: string;         
  hora: string;            
  duracionEstimadaMin: number;
  abonoInicial: string;     
  totalCita: string;      
  idEstado: number;
  observaciones: string | null;
  fechaRegistro: string;   
  mensaje: string | null;
}


export interface Especialidad {
  id?: number;
  nombre: string;
  activo: boolean;
}

export interface tipo_doc{
    id: number;
    descripcion: string;
    activo: boolean;
}

export interface UsuarioResponse {
  id: number;
  username: string;
  passwordHash: string;
  activo: boolean;
  fechaCreacion: string,
  fechaBaja?: string | null
}

export interface UsuarioRequest {
  id?: number;
  username: string;
  passwordHash: string;
  activo: boolean;
  fechaCreacion?: string,
  fechaBaja?: string | null
}

export interface Rol {
  id: number;
  nombre: string;
  descripcion: string;
}

export interface UsuarioRol {
  idUsuario: number;
  rol: string;
  fechaAsignacion: string;
}

export interface TipoPersonaJuridica {
    id: number,
    nombre: string,
    descripcion: string,
    activo: boolean
}

export interface Razas{
    id: number;
    idEspecie: number;
    nombre: string;
    activo: boolean;
}

export interface Especie{
    id: number;
    nombre: string;
    activo: boolean;
}

export interface Estado_Mascota{
    id: number;
    nombre: string;
    decripcion: string;
    activo: boolean;
}

export interface Tamaño_Mascota{
    id: number;
    tamaño: string;
    descripcion: string;
    activo: boolean;
}

export interface Etapa_Mascota{
    id: number;
    descripcion: string;
    activo: boolean;
}

export interface dueñoNom{
    id: number;
    nombre?: string; 
}

export interface agenda_estado{
    id: number;
    nombre: string;
    description: string;
}

export interface CitaPorEstado {
  nombre: string;
  cantidad: number;
}

export interface MascotaPorEspecie{
    nombre: string;
    cantidad: number;
}


//Para el el Historail

export type InfoGeneral = [
  idMascota: number,        // 0
  codigo_Masc: string,      // 1
  nombre: string,           // 2
  sexo: "M" | "H",          // 3
  f_nacimiento: string,     // 4
  pelaje: string,           // 5
  peso: number,             // 6
  castrado: number,         // 7 (0/1)
  alergias: string,         // 8
  chip: number,             // 9 (0/1)
  pedigree: number,         // 10 (0/1)
  dea: number,              // 11 (0/1)
  agresivo: number,         // 12 (0/1)
  foto: string,             // 13
  especie: string,          // 14
  raza: string,             // 15
  tamaño: string,           // 16
  etapa_vida: string,       // 17
  estado: string,           // 18
  idCliente: number,        // 19
  codigo_cliente: string,   // 20
  nombre_cliente: string,   // 21
  telefono: string,         // 22
  correo: string,           // 23
  id_historial: number,     // 24
  codigo_histo: string,     // 25
  fecha_historial: string,  // 26
  extra: string | null,     // 27 (desconocido)
  activo: number,           // 28 (0/1)
  fecha_modificacion: string // 29
];

export interface historialCPorMascota {
  id: number;
  codigo: string;
  nombre: string;
  sexo: "M" | "H";
  fechaNacimiento: string;
  pelaje: string;
  peso: number;
  castrado: boolean;
  alergias: string;
  chip: boolean;
  pedigree: boolean;
  dea: boolean;
  agresivo: boolean;
  foto: string;
  especie: string;
  raza: string;
  tamaño: string;
  etapaVida: string;
  estado: string;
  idCliente: number;
  codigoCliente: string;
  nombreCliente: string;
  telefono: string;
  correo: string;
  idHistorial: number;
  codigoHistorial: string;
  fechaHistorial: string;
  activo: boolean;
  fechaModificacion: string;
}

export const mapInfoGeneral = (info: InfoGeneral): historialCPorMascota => ({
  id: info[0],
  codigo: info[1],
  nombre: info[2],
  sexo: info[3],
  fechaNacimiento: info[4],
  pelaje: info[5],
  peso: info[6],
  castrado: Boolean(info[7]),
  alergias: info[8],
  chip: Boolean(info[9]),
  pedigree: Boolean(info[10]),
  dea: Boolean(info[11]),
  agresivo: Boolean(info[12]),
  foto: info[13],
  especie: info[14],
  raza: info[15],
  tamaño: info[16],
  etapaVida: info[17],
  estado: info[18],
  idCliente: info[19],
  codigoCliente: info[20],
  nombreCliente: info[21],
  telefono: info[22],
  correo: info[23],
  idHistorial: info[24],
  codigoHistorial: info[25],
  fechaHistorial: info[26],
  activo: Boolean(info[28]),
  fechaModificacion: info[29],
});

export interface HorarioResponse {
    id: number;
    trabajadorId: number;
    nombreColaborador: string;
    diaId: number;
    nombreDia: string;
    trabaja: boolean;
    horaInicio: string | null; // "HH:mm:ss"
    horaFin: string | null;    // "HH:mm:ss"
}

export interface HorarioRequest {
    trabajadorId: number;
    diaId: number;
    trabaja: boolean;
    horaInicio: string | null;
    horaFin: string | null;
}

// Tarjeta agrupada por colaborador para renderizar en el frontend
export interface HorarioColaboradorCard {
    trabajadorId: number;
    nombreColaborador: string;
    dias: HorarioResponse[];
}

export interface ServicioResponse {
    id: number,
    nombre: string,
    descripcion?: string,
}

export interface ServicioRequest {
    id?: number,
    nombre: string,
    descripcion?: string
}

export interface Productos {
  id: number,
  codigo?: string,
  nombre: string,
  descripcion?: string,
  marca?: string,
  precio: number,
  stock: number,
  proveedor: string,
  foto?: string,
  activo: boolean
}

export interface usuarioAuResponse{
    id: number;
    username: string;
    nombre?: string;
}

export interface tipo_accion{
    id: number;
    nombre: string;
    descripcion: string | null;
}

export interface auditoriaResponse{
    id: number;
    usuario: usuarioAuResponse;
    tipoAccion: tipo_accion;
    entidad: string;
    idRegistro?: number;
    descripcion: string;
    fecha: string | null;
}

export interface vacunaMascotaRequest{
    id?: number;
    idVacuna: number;
    idMascota: number;
    idVia: number;
    dosis: string;
    fechaAplicacion: string | null;
    durabilidad: number;
    proxDosis: string;
    idColaborador?: number;
    idVeterinario?: number;
    observaciones: string;
    activo: boolean;
}

export interface vacunaMascotaResponse{
    id?: number;
    codigo: string;
    idVacuna: number;
    idMascota: number;
    idVia: number;
    dosis: string;
    fechaAplicacion: string | null;
    fechaModificacion: string | null;
    durabilidad: number;
    proxDosis: string;
    idColaborador?: number;
    idVeterinario?: number;
    observaciones: string;
    fechaRegistro: string;
    activo: boolean;
    mensaje: string;
}

export interface VacunaResponse {
    id: number;
    nombre: string;
    idEspecie: number;
    descripcion: string;
    activo: boolean;
}

export interface AplicacionViaResponse {
    id: number;
    nombre: string;
    activo: boolean;
}
