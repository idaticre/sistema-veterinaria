package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.response.TipoIGVResponseDTO;
import com.vet.manadawoof.dtos.response.TipoMonedaResponseDTO;
import com.vet.manadawoof.dtos.response.TiposComprobantesResponseDTO;
import com.vet.manadawoof.dtos.response.TiposPercepcionesResponseDTO;
import com.vet.manadawoof.mapper.ComprobanteMapper;
import com.vet.manadawoof.repository.TipoIGVRepository;
import com.vet.manadawoof.repository.TipoMonedaRepository;
import com.vet.manadawoof.repository.TiposComprobantesRepository;
import com.vet.manadawoof.repository.TiposPercepcionesRepository;
import com.vet.manadawoof.service.CatalogoService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CatalogoServiceImpl implements CatalogoService {

    private final TipoMonedaRepository tipoMonedaRepository;
    private final TiposComprobantesRepository tiposComprobantesRepository;
    private final TiposPercepcionesRepository tiposPercepcionesRepository;
    private final TipoIGVRepository tipoIGVRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TipoMonedaResponseDTO> listarMonedas() {
        return tipoMonedaRepository.listarMonedas()
                .stream()
                .map(ComprobanteMapper::toTipoMonedaResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TiposComprobantesResponseDTO> listarTiposComprobantes() {
        return tiposComprobantesRepository.listarTiposComprobantes()
                .stream()
                .map(ComprobanteMapper::toTiposComprobantesResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TiposPercepcionesResponseDTO> listarTiposPercepciones() {
        return tiposPercepcionesRepository.listarTiposPercepciones()
                .stream()
                .map(ComprobanteMapper::toTiposPercepcionesResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoIGVResponseDTO> listarTiposIgv() {
        return tipoIGVRepository.listarTiposIgv()
                .stream()
                .map(ComprobanteMapper::toTipoIGVResponseDTO)
                .collect(Collectors.toList());
    }
}
