package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.EspecialidadEntity;
import com.vet.manadawoof.repository.EspecialidadRepository;
import com.vet.manadawoof.service.EspecialidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio para la gestión de especialidades veterinarias.
 * Permite crear, actualizar, eliminar y listar las especialidades registradas.
 */
@Service
@RequiredArgsConstructor
public class EspecialidadServiceImpl implements EspecialidadService {
    
    private final EspecialidadRepository repository;
    
    /**
     * Crea una nueva especialidad.
     * Verifica que no exista otra especialidad con el mismo nombre antes de registrarla.
     *
     * @param entity Objeto EspecialidadEntity con los datos de la nueva especialidad.
     * @return La especialidad creada.
     * @throws RuntimeException Si ya existe una especialidad con el mismo nombre.
     */
    @Override
    @Transactional
    public EspecialidadEntity crearEspecialidad(EspecialidadEntity entity) {
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Especialidad ya existe");
                });
        return repository.save(entity);
    }
    
    /**
     * Actualiza los datos de una especialidad existente.
     * Comprueba que el ID exista y que no haya duplicidad de nombres.
     *
     * @param entity Objeto EspecialidadEntity con los datos actualizados.
     * @return La especialidad actualizada.
     * @throws RuntimeException Si la especialidad no existe o si el nombre ya está en uso por otra.
     */
    @Override
    @Transactional
    public EspecialidadEntity actualizarEspecialidad(EspecialidadEntity entity) {
        EspecialidadEntity existente = repository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));
        
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && ! e.getId().equals(entity.getId()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Otra especialidad con ese nombre ya existe");
                });
        
        existente.setNombre(entity.getNombre());
        existente.setActivo(entity.getActivo());
        return repository.save(existente);
    }
    
    /**
     * Elimina una especialidad de la base de datos por su ID.
     *
     * @param id Identificador de la especialidad a eliminar.
     * @return Mensaje de confirmación de eliminación.
     * @throws RuntimeException Si la especialidad no existe.
     */
    @Override
    @Transactional
    public String eliminarEspecialidad(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Especialidad no encontrada");
        }
        repository.deleteById(id);
        return "Especialidad eliminada correctamente";
    }
    
    /**
     * Lista todas las especialidades registradas.
     *
     * @return Lista completa de especialidades.
     */
    @Override
    @Transactional
    public List<EspecialidadEntity> listarEspecialidades() {
        return repository.findAll();
    }
    
    /**
     * Obtiene una especialidad específica por su ID.
     *
     * @param id Identificador de la especialidad.
     * @return La especialidad encontrada.
     * @throws RuntimeException Si no se encuentra la especialidad.
     */
    @Override
    @Transactional
    public EspecialidadEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));
    }
}
