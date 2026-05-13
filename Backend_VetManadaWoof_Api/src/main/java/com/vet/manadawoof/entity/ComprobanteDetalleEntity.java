package com.vet.manadawoof.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "comprobante_detalles")
public class ComprobanteDetalleEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprobante_id", nullable = false)
    private ComprobanteEntity comprobante;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_unidad_medida_id", nullable = false)
    private TipoUnidadMedidaEntity tipoUnidadMedida;
    
    @Column (name = "item_id")
    private int idItem;
    
    @Column (name = "descripcion")
    private String descripcion;
    
    @Builder.Default
    @Column(name = "cantidad", precision = 22, scale = 10)
    private BigDecimal cantidad = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "valor_unitario", precision = 22, scale = 10)
    private BigDecimal valorUnitario = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "precio_unitario", precision = 22, scale = 10)
    private BigDecimal precioUnitario = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "descuento", precision = 22, scale = 10)
    private BigDecimal descuento = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "subtotal", precision = 22, scale = 10)
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_igv_id", nullable = false)
    private TipoIGVEntity tipoIGV;
    
    @Builder.Default
    @Column(name = "igv", precision = 14, scale = 2)
    private BigDecimal igv = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "impuestos_bolsas", precision = 14, scale = 2)
    private BigDecimal impuestosBolsas = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "total", precision = 14, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    
    @Column (name = "anticipio_regularizacion")
    private boolean anticipio;
    
    @Column (name = "anticipio_documento_serie")
    private String anticipioDocSerie;
    
    @Column (name = "anticipio_documento_numero")
    private int anticipioDocNumero;
    
    @Column (name = "codigo_producto_sunat")
    private String codigoProducSunat;

}
