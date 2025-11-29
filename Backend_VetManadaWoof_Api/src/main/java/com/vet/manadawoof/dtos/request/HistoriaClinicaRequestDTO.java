package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoriaClinicaRequestDTO {
    
    @NotNull(message = "Mascota es obligatoria")
    private Long idMascota;
    
    private String observacionesGenerales;
}
