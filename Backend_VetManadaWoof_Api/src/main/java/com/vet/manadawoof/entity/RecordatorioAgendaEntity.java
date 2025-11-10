package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "recordatorios_agenda", indexes = {
        @Index(name = "idx_recordatorio_agenda", columnList = "id_agenda"),
        @Index(name = "idx_recordatorio_fecha", columnList = "fecha_recordatorio"),
        @Index(name = "idx_recordatorio_enviado", columnList = "enviado")
})
public class RecordatorioAgendaEntity implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 16, nullable = false, unique = true)
    private String codigo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agenda", nullable = false, foreignKey = @ForeignKey(name = "fk_recordatorio_agenda"))
    private AgendaEntity agenda;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_recordatorio", nullable = false, foreignKey = @ForeignKey(name = "fk_recordatorio_tipo"))
    private TipoRecordatorioEntity tipoRecordatorio;
    
    @Column(name = "fecha_recordatorio", nullable = false)
    @Temporal(TemporalType.DATE)
    private java.time.LocalDate fechaRecordatorio;
    
    @Column
    private java.time.LocalTime hora;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_canal_comunicacion", foreignKey = @ForeignKey(name = "fk_recordatorio_canal"))
    private CanalComunicacionEntity canalComunicacion;
    
    @Column(name = "enviado", nullable = false, columnDefinition = "TINYINT(1)")
    @JdbcTypeCode(SqlTypes.BIT)
    private Boolean enviado = false;
    
    @Column(name = "fecha_envio")
    @Temporal(TemporalType.TIMESTAMP)
    private java.time.LocalDateTime fechaEnvio;
}
