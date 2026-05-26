package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.ComprobanteRequestDTO;
import com.vet.manadawoof.dtos.response.ComprobanteResponseDTO;
import java.util.List;
import java.util.Map;


public interface ComprobanteService {
    
    Map<String, String> generarComprobante(ComprobanteRequestDTO request);//ComprobanteResponseDTO
    List<ComprobanteResponseDTO> listarComprobantes();
    ComprobanteResponseDTO obtenerComprobante(Long id);
    List<ComprobanteResponseDTO> obtenerPorTipo(Integer tipoComprobanteId);
    List<ComprobanteResponseDTO> obtenerPorCliente(Long clienteId);
    
}
