package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.RazaEntity;
import com.vet.manadawoof.repository.RazaRepository;
import com.vet.manadawoof.service.RazaService;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RazaServiceImpl implements RazaService {
    
    private final RazaRepository repository;
    
    /**
     * Crea una nueva raza.
     * Verifica que no exista otra raza con el mismo nombre antes de registrarla.
     *
     * @param entity Objeto RazaEntity con los datos de la nueva raza
     * @return La raza creada.
     * @throws RuntimeException Si ya existe una raza con el mismo nombre.
     */
    @Override
    @Transactional
    public RazaEntity crear(RazaEntity entity) {
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Especialidad ya existe");
                });
        return repository.save(entity);
    }
    
    /**
     * Actualiza los datos de una raza existente.
     * Comprueba que el ID exista y que no haya duplicidad de nombres.
     *
     * @param entity Objeto RazaEntity con los datos actualizados.
     * @return La raza actualizada.
     * @throws RuntimeException Si la raza no existe o si el nombre ya está en uso por otra.
     */
    @Override
    @Transactional
    public RazaEntity actualizar(RazaEntity entity) {
        RazaEntity existente = repository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Raza no encontrada"));
        
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && ! e.getId().equals(entity.getId()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Otra raza con ese nombre ya existe");
                });
        
        existente.setNombre(entity.getNombre());
        existente.setActivo(entity.getActivo());
        return repository.save(existente);
    }
    
    /**
     * Elimina una raza de la base de datos por su ID.
     *
     * @param id Identificador de la raza a eliminar.
     * @return Mensaje de confirmación de eliminación.
     * @throws RuntimeException Si la raza no existe.
     */
    
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Raza no encontrada");
        }
        repository.deleteById(id);
        return "Raza eliminada correctamente";
    }
    
    /**
     * Lista todas las razas registradas.
     *
     * @return Lista completa de razas.
     */
    @Override
    @Transactional
    public List<RazaEntity> listar() {
        return repository.findAll();
    }
    
    /**
     * Obtiene una raza específica por su ID.
     *
     * @param id Identificador de la raza.
     * @return La raza encontrada.
     * @throws RuntimeException Si no se encuentra la raza
     */
    
    @Override
    @Transactional
    public RazaEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Raza no encontrada"));
    }
    
}
