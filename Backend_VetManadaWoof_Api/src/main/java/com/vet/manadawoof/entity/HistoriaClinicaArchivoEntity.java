package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "historia_clinica_archivos")
public class HistoriaClinicaArchivoEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 16, nullable = false, unique = true)
    private String codigo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_atencion", nullable = false)
    private HistoriaClinicaRegistroEntity registroAtencion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_archivo")
    private TipoArchivoClinicoEntity tipoArchivo;
    
    @Column(name = "nombre_archivo", length = 128, nullable = false)
    private String nombreArchivo;
    
    @Column(name = "extension_archivo", length = 32, nullable = false)
    private String extensionArchivo;
    
    @Column(length = 256)
    private String descripcion;
    
    @Column(name = "fecha_subida", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaSubida;
    
    @PrePersist
    protected void onCreate() {
        fechaSubida = LocalDateTime.now();
    }
}
