package com.vet.manadawoof.dtos.response;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TipoIGVResponseDTO {
    private Integer id;
    private String descripcion;
}
