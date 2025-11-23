package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "empresa")
public class EmpresaEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "razon_social", length = 128, nullable = false)
    private String razonSocial;
    
    @Column(name = "ruc", columnDefinition = "CHAR(11)", nullable = false, unique = true)
    private String ruc;
    
    @Column(name = "direccion", length = 256)
    private String direccion;
    
    @Column(name = "ciudad", length = 64)
    private String ciudad;
    
    @Column(name = "distrito", length = 64)
    private String distrito;
    
    @Column(name = "telefono", length = 15)
    private String telefono;
    
    @Column(name = "correo", length = 64)
    private String correo;
    
    @Column(name = "representante", length = 64)
    private String representante;
    
    @Column(name = "logo_empresa", length = 255)
    private String logoEmpresa;
    
    @Column(name = "fecha_registro", updatable = false, insertable = false)
    private Timestamp fechaRegistro;
}
