package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.AuditoriaEntity;
import com.vet.manadawoof.repository.AuditoriaRepository;
import com.vet.manadawoof.repository.TipoAccionRepository;
import com.vet.manadawoof.repository.UsuarioRepository;
import com.vet.manadawoof.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {
    
    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoAccionRepository tipoAccionRepository;
    
    @Override
    @Transactional
    public AuditoriaEntity crear(AuditoriaEntity entity){
        return auditoriaRepository.save(entity);
    }
    
}
