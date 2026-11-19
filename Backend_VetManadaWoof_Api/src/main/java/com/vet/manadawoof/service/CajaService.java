package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.CajaAbrirRequestDTO;
import com.vet.manadawoof.dtos.request.CajaCierreRequestDTO;
import com.vet.manadawoof.dtos.request.CajaMovimientoRequestDTO;
import com.vet.manadawoof.dtos.response.CajaEstadoResponseDTO;
import com.vet.manadawoof.dtos.response.CajaArchivoResponseDTO;
import com.vet.manadawoof.dtos.response.CajaMovimientoResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface CajaService {

    CajaEstadoResponseDTO obtenerEstadoHoy();

    CajaEstadoResponseDTO abrirCaja(CajaAbrirRequestDTO request);

    CajaMovimientoResponseDTO registrarMovimiento(CajaMovimientoRequestDTO request, MultipartFile comprobante);

    CajaEstadoResponseDTO cerrarCaja(CajaCierreRequestDTO request);

    List<CajaMovimientoResponseDTO> listarMovimientos(
            java.time.LocalDate desde,
            java.time.LocalDate hasta,
            Integer medioPagoId,
            String tipo);

    void registrarIngresoDesdeComprobante(
            Long comprobanteId,
            BigDecimal monto,
            Integer medioPagoId,
            Long clienteId,
            Long agendaId,
            String concepto);

    CajaArchivoResponseDTO obtenerArchivoComprobante(Long movimientoId);
}
