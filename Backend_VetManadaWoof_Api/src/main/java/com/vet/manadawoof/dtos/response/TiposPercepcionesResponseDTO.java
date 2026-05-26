package com.vet.manadawoof.dtos.response;

import lombok.*;
import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TiposPercepcionesResponseDTO {
    private Integer id;
    private String percepcion;
    private BigDecimal tasaPorcentaje;
}
