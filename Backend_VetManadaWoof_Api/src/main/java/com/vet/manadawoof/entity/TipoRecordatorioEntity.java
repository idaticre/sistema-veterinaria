package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "tipo_recordatorio")
public class TipoRecordatorioEntity implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(length = 64, nullable = false, unique = true)
    private String nombre;
    
    @Column(length = 128)
    private String descripcion;
    
    @OneToMany(mappedBy = "tipoRecordatorio", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<RecordatorioAgendaEntity> recordatorios;
}
