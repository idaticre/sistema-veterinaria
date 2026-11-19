package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "caja_comprobantes")
@Getter
@Setter
@NoArgsConstructor
public class CajaComprobanteEntity {

    @Id
    @Column(name = "movimiento_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movimiento_id", nullable = false)
    private CajaMovimientoEntity movimiento;

    @Lob
    @Column(name = "archivo", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] archivo;
}
