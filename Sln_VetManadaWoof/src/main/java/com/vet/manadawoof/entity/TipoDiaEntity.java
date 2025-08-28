package com.vet.manadawoof.entity;


import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "TipoDiaEntity")
@Table(name = "tipos_dia")
public class TipoDiaEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true)
    private String codigo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "activo")
    private Boolean activo;
}
