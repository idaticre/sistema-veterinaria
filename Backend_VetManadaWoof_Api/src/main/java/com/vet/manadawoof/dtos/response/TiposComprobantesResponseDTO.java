package com.vet.manadawoof.dtos.response;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TiposComprobantesResponseDTO {
    private Integer id;
    private String comprobante;
}
