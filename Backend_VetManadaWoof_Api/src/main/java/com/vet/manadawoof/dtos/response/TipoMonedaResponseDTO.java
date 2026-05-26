package com.vet.manadawoof.dtos.response;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TipoMonedaResponseDTO {
    private Integer id;
    private String moneda;
}
