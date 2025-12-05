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

export interface HorarioTrabajoRequest{
    id: number;
    idColaborador: number;
    idDiaSemana: number;
    idTipoDia: number;
    horaInicio: string;
    horaFin: string;
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

export interface MascotaResponse{
    id?: number;
    codigo: string;
    nombre: string;
    sexo: "M" | "H";
    idCliente: number;
    idRaza: number;
    idEspecie: number;
    idEstado: number;
    idTamano: number;
    idEtapa: number;
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
    fechaModificacion: string;
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
    

export interface HorarioTrabajoResponse{
    id: number;
    codigo: string;
    idColaborador: number;
    idDiaSemana: number;
    idTipoDia: number;
    horaInicio: string;
    horaFin: string;
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