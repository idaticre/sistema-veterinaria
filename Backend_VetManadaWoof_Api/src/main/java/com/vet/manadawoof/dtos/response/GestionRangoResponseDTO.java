package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GestionRangoResponseDTO {
    
    private String status; // "OK" o "ERROR"
    private String mensaje; // mensaje descriptivo
}
