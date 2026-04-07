# 🐾 VetManadaWoof — Sistema de Gestión Veterinaria

Sistema web integral para la gestión operativa de la veterinaria **Manada Woof S.A.C.S** (Lima, Perú).
Cubre desde la administración del personal hasta la historia clínica de cada mascota.

---

## 🏗️ Arquitectura General

```
React (Frontend)
       ↓ HTTP/REST
Spring Boot (Backend API)
       ↓ Stored Procedures / JPA
MySQL (Aiven Cloud — Producción / Local — Desarrollo)
```

### Patrón de capas backend
```
Controller → Service → EntityManager (SP) → BD   [escritura]
Controller → Service → Repository (JPA)  → BD   [lectura]
```

---

## 🧰 Stack Tecnológico

| Capa | Tecnología |
|---|---|
| Frontend | React |
| Backend | Java 17 + Spring Boot |
| Base de datos | MySQL 8 |
| ORM | Spring Data JPA + Hibernate |
| Nube BD | Aiven Cloud (MySQL managed) |
| Despliegue | Render (Docker) |
| Seguridad | JWT |
| Build | Maven |

---

## 📦 Módulos del Sistema

### 01 — Administración

**Empresa**
Registro único de la veterinaria. Se visualiza y actualiza desde el sistema.

**Usuarios y Roles**
- Un usuario puede tener múltiples roles
- Al asignar el primer rol a un colaborador, el sistema auto-asigna sus horarios base
- Roles disponibles: Administrador General, Auxiliar Caja, Auxiliar Gromers, Auxiliar Bañador

**Entidades — Patrón central**
```
entidades (tabla base)
    ├── colaboradores  → COL000001
    ├── clientes       → CLI000001
    └── proveedores    → PRV000001
```
Toda persona o empresa pasa primero por la tabla `entidades`.
Los procedimientos `registrar_entidad_base` y `actualizar_entidad_base` son reutilizados por todos los módulos.

**Colaboradores y Veterinarios**
```
entidades → colaboradores → veterinarios
```
Un veterinario es siempre un colaborador primero.
Cada veterinario tiene especialidad y CMP registrado.

**Horarios**
```
roles → horarios_base_roles → horarios_base
                                    ↓
colaboradores → asignacion_horarios (por día, con vigencia)
                        ↓
              asignacion_horarios_detalle (excepciones por fecha)
```
- Los horarios son asignados por rol y día de la semana
- Se pueden hacer excepciones puntuales por día o por rango de fechas
- Soporta días de descanso, horarios personalizados y restauración al horario base

**Control de Asistencia**
```
registro_asistencias
    Marcaciones: ENTRADA → LUNCH_IN → LUNCH_OUT → SALIDA
```
- Calcula automáticamente tardanza, minutos trabajados y minutos de almuerzo
- Estado final: PRESENTE, TARDANZA, COMPLETADO, DESCANSO_SEMANAL, etc.

---

### 02 — Mascotas y Salud

**Catálogos**
- Especies: CANINO, FELINO, CONEJO
- Razas por especie
- Tamaños: XS, S, M, L, XL
- Etapas de vida: CACHORRO, JOVEN, ADULTO, SENIOR
- Estados de mascota: ACTIVA, EN TRATAMIENTO, CRITICA, FALLECIDA, etc.
- Vacunas por especie (catálogo ampliado)
- Medicamentos por tipo y vía de aplicación

**Mascota**
```
clientes → mascotas → PET000001
               ├── especie / raza / tamaño / etapa
               ├── datos clínicos (peso, alergias, chip, pedigree)
               └── estado actual
```

**Historial médico**
```
mascotas
    ├── vacunas_mascota      → VACM000001
    └── medicamentos_mascota → MEDM000001
```
Cada registro incluye quién aplicó (colaborador o veterinario), vía, dosis, fecha y próxima dosis.

---

### 03 — Agenda y Servicios

**Flujo completo de una cita**

```
1. SOLICITUD
   Cliente contacta por: Teléfono, WhatsApp, Web, Presencial, Redes Sociales

2. AGENDA CREADA → AG-000001
   Cliente + Mascota + Fecha + Hora + Medio de solicitud
   Estado inicial: PENDIENTE

3. SERVICIOS AGREGADOS → IS-000001
   Baño, Vacunación, Consulta, Grooming, Hospedaje, etc.
   El total_cita se recalcula automáticamente con cada cambio

4. PAGOS / ABONOS → AB-000001
   Efectivo, Tarjeta, Yape, Plin, Transferencia, Link de pago
   El sistema controla que el abono no supere el total

5. RECORDATORIOS
   Por canal: WhatsApp, Email, Llamada, SMS
   Tipos: Cita agendada, Vacunación, Control médico, etc.

6. CITA ATENDIDA
   Estado final: ATENDIDA → genera registro en Historia Clínica
```

**Estados de agenda**
```
PENDIENTE → CONFIRMADA → ATENDIDA
PENDIENTE → REPROGRAMADA
PENDIENTE / CONFIRMADA → CANCELADA
PENDIENTE / CONFIRMADA → NO ASISTIÓ
```

---

### 04 — Historia Clínica

**Estructura jerárquica**
```
mascotas
    └── historia_clinica (1 por mascota) → HIS-000001
            └── historia_clinica_registros (1 por atencion) → REG-000001
                    └── historia_clinica_archivos → ARC000001
```

**Flujo principal — cita atendida**
```
Agenda ATENDIDA
        ↓
registrar_cita_atendida
        ├── Busca o crea historia clínica de la mascota
        ├── Crea registro de atencion con:
        │       ├── Datos clínicos: motivo, anamnesis, diagnostico, tratamiento
        │       ├── Datos esteticos: pelaje, piel, observaciones grooming
        │       └── Datos hospedaje: comportamiento, alimentacion, actividad
        └── Marca la agenda como ATENDIDA
```

**Flujo secundario — atencion manual**
```
registrar_atencion
    → Emergencias o datos retrospectivos sin cita previa
    → Requiere historia clínica previamente existente
```

**Tipos de visita soportados**
`GENERAL` `MEDICA` `ESTETICA` `HOSPEDAJE` `CONSULTA` `PROCEDIMIENTO`

**Tipos de archivo clínico**
Radiografia, Ecografia, Analisis de sangre, Receta medica, Consentimiento informado,
Registro de grooming, Ficha de hospedaje, y mas.

**Consulta de historial completo**
```
GET /api/historia-clinica/mascota/{idMascota}/historial

Devuelve en una sola llamada:
    ├── RS1: Datos de la mascota + cliente + historia clínica
    ├── RS2: Todos los registros de atencion (mas reciente primero)
    └── RS3: Todos los archivos asociados a los registros
```

---

## 🔗 API REST — Endpoints principales

### Historia Clínica
| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/historia-clinica` | Crear historia clínica |
| GET | `/api/historia-clinica/{id}` | Obtener por ID |
| GET | `/api/historia-clinica/mascota/{idMascota}` | Obtener por mascota |
| GET | `/api/historia-clinica` | Listar todas (paginado) |
| GET | `/api/historia-clinica/mascota/{idMascota}/historial` | Historial completo |

### Atenciones Medicas
| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/atenciones-medicas/cita/{idAgenda}` | Registrar cita atendida |
| POST | `/api/atenciones-medicas` | Crear atencion manual |
| PUT | `/api/atenciones-medicas/{id}` | Actualizar atencion |
| DELETE | `/api/atenciones-medicas/{id}` | Eliminar atencion |
| GET | `/api/atenciones-medicas/{id}` | Obtener por ID |
| GET | `/api/atenciones-medicas/historia/{idHistoriaClinica}` | Listar por historia |
| GET | `/api/atenciones-medicas/historia/{idHistoriaClinica}/paginado` | Listar paginado |
| GET | `/api/atenciones-medicas/veterinario/{idVeterinario}` | Listar por veterinario |

### Archivos Clínicos
| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/archivos-clinicos` | Subir archivo |
| DELETE | `/api/archivos-clinicos/{id}` | Eliminar archivo |
| GET | `/api/archivos-clinicos/{id}` | Obtener por ID |
| GET | `/api/archivos-clinicos/registro/{idRegistroAtencion}` | Listar por registro |
| GET | `/api/archivos-clinicos/registro/{idRegistroAtencion}/paginado` | Listar paginado |

---

## 🗂️ Estructura del Proyecto Backend

```
Backend_VetManadaWoof_Api/
├── src/main/java/com/vet/manadawoof/
│   ├── config/
│   ├── controller/
│   ├── dtos/
│   │   ├── request/
│   │   └── response/
│   ├── entity/
│   ├── enums/
│   ├── mapper/
│   ├── repository/
│   ├── service/
│   │   └── impl/
│   └── VetManadaWoofApplication.java
├── src/main/resources/
│   ├── application.properties          ← produccion (Aiven)
│   └── application-dev.properties      ← desarrollo local (no se sube al repo)
├── Dockerfile
└── pom.xml
```

---

## ⚙️ Configuracion y Despliegue

### Desarrollo local

**Requisitos**
- Java 17
- Maven
- MySQL 8 local

**Pasos**
```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/Backend_VetManadaWoof_Api.git

# 2. Crear el archivo de configuracion local
# Crear: src/main/resources/application-dev.properties
# (ver properties.example como referencia)

# 3. Levantar con perfil dev
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Produccion (Render)

El proyecto se despliega automaticamente en Render usando el `Dockerfile` del repositorio.
La base de datos en produccion esta en **Aiven Cloud (MySQL managed)**.
No se requiere configuracion adicional — Render usa `application.properties` por defecto.

---

## 🔐 Seguridad

- Autenticacion mediante **JWT**
- Token expira en **24 horas** (configurable)
- Roles gestionados a nivel de base de datos y validados en backend

---

## 📋 Convencion de codigos

| Entidad | Prefijo | Ejemplo |
|---|---|---|
| Entidad base | ENT | ENT000001 |
| Colaborador | COL | COL000001 |
| Cliente | CLI | CLI000001 |
| Proveedor | PRV | PRV000001 |
| Veterinario | VET | VET000001 |
| Mascota | PET | PET000001 |
| Vacuna mascota | VACM | VACM000001 |
| Medicamento mascota | MEDM | MEDM000001 |
| Agenda | AG- | AG-000001 |
| Ingreso servicio | IS- | IS-000001 |
| Pago agenda | AB- | AB-000001 |
| Historia clinica | HIS- | HIS-000001 |
| Registro atencion | REG- | REG-000001 |
| Archivo clinico | ARC | ARC000001 |

---

## 🏢 Informacion de la Empresa

| Campo | Valor |
|---|---|
| Razon social | Manada Woof S.A.C.S |
| RUC | 20613366998 |
| Direccion | Jiron Arequipa 238, Magdalena del Mar, Lima |
| Telefono | 917 233 145 |
| Correo | manadawoof.vet@gmail.com |

---

## 👨‍💻 Desarrollo

Proyecto desarrollado con arquitectura de sistema de gestion empresarial vertical (ERP veterinario),
con separacion clara de capas, procedimientos almacenados para operaciones criticas
y JPA para consultas de lectura.
