package com.vet.manadawoof.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "comprobantes")
public class ComprobanteEntity implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_id", nullable = false)
    private AgendaEntity agenda;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_comprobante_id", nullable = false)
    private TiposComprobantesEntity tipoComprobante;
    
    @Column (name = "serie", length = 4)
    private String serie;
    
    @Column (name = "numero")
    private int numero;
    
    @Column (name = "fecha_emision")
    private LocalDate fechaEmision; 
    
    @Column (name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_moneda_id", nullable = false)
    private MonedasEntity tipoMoneda;
    
    @Builder.Default
    @Column(name = "total_gravada", precision = 14, scale = 2)
    private BigDecimal totalGravada = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "total_inafecta", precision = 14, scale = 2)
    private BigDecimal totalInafecta = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "total_exonerada", precision = 14, scale = 2)
    private BigDecimal totalExonerada = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_igv", precision = 14, scale = 2)
    private BigDecimal totalIGV = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "total_gratuita", precision = 14, scale = 2)
    private BigDecimal totalGratuita = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "total_otros_cargos", precision = 14, scale = 2)
    private BigDecimal totalOtrosCargos = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "total", precision = 14, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_percepcion_id")
    private TiposPercepcionesEntity tipoPercepcion;
    
    @Builder.Default
    @Column(name = "percepcion_base_imponible", precision = 14, scale = 2)
    private BigDecimal percepcionBaseImponible = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "total_percepcion", precision = 14, scale = 2)
    private BigDecimal totalPercepcion = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_incluido_percepcion", precision = 14, scale = 2)
    private BigDecimal totalIncluidoPercepcion = BigDecimal.ZERO;
    
    @Column (name = "observaciones")
    private String observaciones;
    
    @Column (name = "codigo_unico")
    private String codigoUnico;
    
    @Column (name = "condiciones_pago")
    private String condicionesPago;
    
    @Column (name = "nubecont_tipo_venta_codigo")
    private String nubecontTipoVentaCodigo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;
    
    @Builder.Default
    @Column(name = "total_anticipio", precision = 14, scale = 2)
    private BigDecimal totalAnticipio = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medio_pago_id")
    private MedioPagoEntity medioPago;
    
    @OneToMany(mappedBy = "comprobante", fetch = FetchType.LAZY)
    private List<ComprobanteDetalleEntity> detalles;
}
