package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.ComprobanteRequestDTO;
import com.vet.manadawoof.dtos.response.ComprobanteResponseDTO;


public interface ComprobanteService {
    
    ComprobanteResponseDTO generarComprobante(ComprobanteRequestDTO request);
    
}
