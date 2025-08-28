package com.vet.manadawoof.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "ColaboradorEntity")
@Table(name = "colaboradores")
public class ColaboradorEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.DATE)
    private Date fechaIngreso;

    @Column(name = "foto")
    private String foto;

    @Column(name = "activo")
    private Boolean activo;

    @ManyToOne
    @JoinColumn(name = "id_entidad", referencedColumnName = "id")
    private EntidadEntity entidad;

    @OneToMany(mappedBy = "colaborador", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<VeterinarioEntity> veterinarios;
}
