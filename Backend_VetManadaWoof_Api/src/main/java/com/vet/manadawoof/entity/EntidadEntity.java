package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "entidades")
public class EntidadEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    // Usamos Long para id por consistencia con BD y futuras referencias
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 16)
    private String codigo;
    
    @Column(nullable = false, length = 128)
    private String nombre;
    
    @Column(length = 1)
    private String sexo; // Valores permitidos: 'M', 'F', 'O' según SP
    
    @Column(length = 20, unique = true)
    private String documento;
    
    @Column(length = 64, unique = true)
    private String correo;
    
    @Column(length = 15)
    private String telefono;
    
    @Column(length = 128)
    private String direccion;
    
    @Column(length = 64)
    private String ciudad;
    
    @Column(length = 64)
    private String distrito;
    
    @Column(length = 64)
    private String representante;
    
    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    @JdbcTypeCode(SqlTypes.BIT)
    private Boolean activo; // Boolean para representar 1/0 en la BD
    
    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_tipo_persona_juridica", insertable = false, updatable = false)
    private TipoPersonaJuridicaEntity tipoPersonaJuridica;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_tipo_documento", insertable = false, updatable = false)
    private TipoDocumentoEntity tipoDocumento;
    
    // Campos auxiliares para el DTO
    // mantiene congruencia con DTO
    @Transient
    @Column(name = "id_tipo_documento", insertable = true, updatable = true)
    private Integer idTipoDocumento;
    
    // mantiene congruencia con DTO
    @Transient
    @Column(name = "id_tipo_persona_juridica", insertable = true, updatable = true)
    private Integer idTipoPersonaJuridica;
    
    @Column(name = "fecha_registro", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaRegistro;
    
    @OneToMany(mappedBy = "entidad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ClienteEntity> clientes;
    
    @OneToMany(mappedBy = "entidad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProveedorEntity> proveedores;
    
    @OneToMany(mappedBy = "entidad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ColaboradorEntity> colaboradores;
}
