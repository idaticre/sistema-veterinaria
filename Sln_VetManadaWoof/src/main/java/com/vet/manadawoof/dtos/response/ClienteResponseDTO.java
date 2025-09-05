package com.vet.manadawoof.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteResponseDTO {
    private String codigoCliente;
    private String mensaje;
}
