package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.EspecieEntity;
import com.vet.manadawoof.repository.EspecieRepository;
import com.vet.manadawoof.service.EspecieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio para la gestión de especies de mascotas
 * Permite crear, actualizar, eliminar y listar las especies registradas.
 */
@Service
@RequiredArgsConstructor
public class EspecieServiceImpl implements EspecieService {
    private final EspecieRepository repository;
    
    /**
     * Crea una nueva especie.
     * Verifica que no exista otra especie con el mismo nombre antes de registrarla.
     *
     * @param entity Objeto EspecieEntity con los datos de la nueva especie.
     * @return La especie creada.
     * @throws RuntimeException Si ya existe una especie con el mismo nombre.
     */
    @Override
    @Transactional
    public EspecieEntity crear(EspecieEntity entity) {
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Especie ya existe");
                });
        return repository.save(entity);
    }
    
    /**
     * Actualiza una nueva especie.
     * Verifica que no exista otra especie con el mismo nombre antes de registrarla.
     *
     * @param entity Objeto EspeciesEntity con los datos de la nueva especie.
     * @return La especie creada.
     * @throws RuntimeException Si ya existe una especie con el mismo nombre.
     */
    @Override
    @Transactional
    public EspecieEntity actualizar(EspecieEntity entity) {
        EspecieEntity existente = repository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Especie no encontrada"));
        
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && ! e.getId().equals(entity.getId()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Otra especies con ese nombre ya existe");
                });
        
        existente.setNombre(entity.getNombre());
        existente.setActivo(entity.getActivo());
        return repository.save(existente);
    }
    
    /**
     * Elimina una especie de la base de datos por su ID.
     *
     * @param id Identificador de la especie a eliminar.
     * @return Mensaje de confirmación de eliminación.
     * @throws RuntimeException Si la especie no existe.
     */
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Especie no encontrada");
        }
        repository.deleteById(id);
        return "Especie eliminada correctamente";
    }
    
    /**
     * Lista todas las especies registradas.
     *
     * @return Lista completa de especies.
     */
    @Override
    @Transactional
    public List<EspecieEntity> listar() {
        return repository.findAll();
    }
    
    // Obtiene una especialidad específica por su ID.
    @Override
    @Transactional
    public EspecieEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especie no encontrada"));
    }
}
