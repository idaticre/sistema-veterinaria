package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.RazaEntity;
import com.vet.manadawoof.entity.TamanoMascEntity;
import com.vet.manadawoof.repository.TamanoMascRepository;
import com.vet.manadawoof.service.TamanoMascService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TamanoMascServiceImpl implements TamanoMascService {
    private final TamanoMascRepository repository;
    
    /**
     * Crea un nuevo tamaño.
     * Verifica que no exista otro tamaño con el mismo nombre antes de registrarlo.
     *
     * @param entity Objeto TamanoMascEntity con los datos de la nueva medida
     * @return Tamaño vuelve
     * @throws RuntimeException Si ya existe un tamaño con el mismo nombre.
     */
    @Override
    @Transactional
    public TamanoMascEntity crear(TamanoMascEntity entity) {
        repository.findAll().stream().filter(e -> e.getTamano().equalsIgnoreCase(entity.getTamano())).findFirst().ifPresent(e -> {
            throw new RuntimeException("Tamaño ya existe");
        }); return repository.save(entity);
    }
    
    /**
     * Actualiza los datos de un tamaño existente.
     * Comprueba que el ID exista y que no haya duplicidad de nombres.
     *
     * @param entity Objeto tamanoMascEntity con los datos actualizados.
     * @return actualiza.
     * @throws RuntimeException Si no existe o si el nombre ya está en uso por otro dato.
     */
    @Override
    @Transactional
    public TamanoMascEntity actualizar(TamanoMascEntity entity) {
        TamanoMascEntity existente = repository.findById(entity.getId()).orElseThrow(() -> new RuntimeException("Tamaño no encontrado"));
        
        repository.findAll().stream().filter(e -> e.getTamano().equalsIgnoreCase(entity.getTamano()) && ! e.getId().equals(entity.getId())).findFirst().ifPresent(e -> {
            throw new RuntimeException("Otro tamaño con ese nombre ya existe");
        });
        
        existente.setTamano(entity.getTamano()); existente.setActivo(entity.getActivo());
        return repository.save(existente);
    }
    
    /**
     * Elimina de la base de datos por su ID.
     *
     * @param id Identificador de eliminar.
     * @return Mensaje de confirmación de eliminación.
     * @throws RuntimeException Si no existe.
     */
    
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Tamaño no encontrado");
        } repository.deleteById(id); return "Tamaño eliminado correctamente";
    }
    
    /**
     * @return Lista completa.
     */
    @Override
    @Transactional
    public List<TamanoMascEntity> listar() {
        return repository.findAll();
    }
    
    /**
     * Obtiene acción específica por su ID.
     *
     * @param id Identificador de tamaño.
     */
    
    @Override
    @Transactional
    public TamanoMascEntity obtenerPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Tamaño no encontrada"));
    }
}
