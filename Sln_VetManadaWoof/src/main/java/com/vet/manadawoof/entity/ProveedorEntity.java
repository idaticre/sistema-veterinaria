package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "ProveedorEntity")
@Table(name = "proveedores")
public class ProveedorEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "activo")
    private Boolean activo;

    @ManyToOne
    @JoinColumn(name = "id_entidad", referencedColumnName = "id")
    private EntidadEntity entidad;
}
