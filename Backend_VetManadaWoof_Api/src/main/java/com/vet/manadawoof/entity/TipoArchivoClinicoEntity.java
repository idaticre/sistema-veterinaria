package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tipos_archivo_clinico")
public class TipoArchivoClinicoEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(length = 32, nullable = false, unique = true)
    private String nombre;
    
    @Column(length = 128)
    private String descripcion;
}
