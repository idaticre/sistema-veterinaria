package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "caja_movimientos")
@Getter
@Setter
@NoArgsConstructor
public class CajaMovimientoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caja_diaria_id", nullable = false)
    private CajaDiariaEntity cajaDiaria;

    @Column(nullable = false, length = 16)
    private String tipo;

    @Column(nullable = false, length = 180)
    private String concepto;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medio_pago_id", nullable = false)
    private MedioPagoEntity medioPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private ClienteEntity cliente;

    @Column(name = "cliente_referencia", length = 160)
    private String clienteReferencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private ProductoEntity producto;

    @Column(name = "cantidad_producto")
    private Integer cantidadProducto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio")
    private ServicioEntity servicio;

    @Column(name = "servicio_referencia", length = 160)
    private String servicioReferencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agenda")
    private AgendaEntity agenda;

    @Column(nullable = false, length = 32)
    private String origen = "MANUAL";

    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(name = "comprobante_nombre", length = 255)
    private String comprobanteNombre;

    @Column(name = "comprobante_tipo", length = 120)
    private String comprobanteTipo;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;
}
