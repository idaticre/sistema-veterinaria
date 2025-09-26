export interface TipoDocumento{
    id: number;
    codigo: string;
    descripcion: string;
    activo: boolean;
    entidades: Entidad[];
}

export interface TipoEntidad{
    id: number;
    codigo: string;
    nombre: string;
    activo: boolean;
    entidades: Entidad[];
}

export interface TipoPersonaJuridica{
    id: number;
    codigo:  string;
    nombre: string;
    descripcion: string;
    activo: boolean;
    entidades: Entidad[];
}

export interface Rol{
    id: number;
    codigo: string;
    nombre: string;
    descripcion: string;
    activo: boolean;
    mensaje: string;
    usuarios: Usuario;
}

export interface Entidad{
    id: number;
    tipoEntidad: TipoEntidad;
    tipoPersJurid: TipoPersonaJuridica;
    nombre: String;
    sexo: string;
    documento: string;
    tipoDocumento: TipoDocumento;
    correo: string;
    telefono: string;
    direccion: string;
    ciudad: string;
    destrito: string;
    representante: string;
    codigo: string;
    activo: boolean;
    clientes: Cliente[];
    proveedores: Proveedores[];
    colaboradores: Colaboradores[];
}

export interface Cliente{
    id: number;
    codigo: string;
    activo:  boolean;
    entidad: Entidad;
}

export interface Proveedores{
    id: number;
    codigo: string;
    activo: boolean;
}

export interface Colaboradores{
    id: number;
    entidad: Entidad;
    usuario: Usuario;
    codigo: string;
    fechaIngreso: string;
    foto: string;
    activo: boolean;
}

export interface Usuario{
    id: number;
    codigo: string;
    username: string;
    passwordHash: string;
    activo: boolean;
    colaborador:  Colaboradores;
    roles: Rol[];
}

export interface Dia{
    id: number;
    nombre: string;
    activo: boolean;
}

export interface TipoDia{
    id:number;
    codigo: string;
    nombre: string;
}

export interface Empresa{
    id: number;
    razonSocial: string;
    ruc: string;
    direccion: string;
    ciudad: string;
    distrito: string;
    telefono: string;
    correo: string;
    representante: string;
    logoEmpresa: string;
}

export interface Especialidad{
    id: number;
    codigo: string;
    nombre: string;
    activo: boolean;
    veterinarios: Veterinario[];
}

export interface Veterinario{
    id: number;
    codigo: string;
    cmp: string;
    activo: boolean;
    colaborador: Colaboradores;
    especialidad: Especialidad;
}

export interface HorarioTrabajo{
    id: number;
    colaborador: Colaboradores;
    dia: Dia;
    tipoDia: TipoDia;
    horaInicio: string;
    horaFin: string;
}

export interface UsuarioRol{
    id: number;
    usuario: Usuario;
    rol: Rol;
}