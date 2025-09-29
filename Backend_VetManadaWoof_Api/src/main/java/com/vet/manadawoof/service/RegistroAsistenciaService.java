package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.RegistroAsistenciaEntity;
import java.util.List;

public interface RegistroAsistenciaService {

    RegistroAsistenciaEntity crearRegistro(RegistroAsistenciaEntity entity);

    RegistroAsistenciaEntity actualizarRegistro(RegistroAsistenciaEntity entity);

    String eliminarRegistro(Integer id);

    List<RegistroAsistenciaEntity> listarRegistros();

    RegistroAsistenciaEntity obtenerPorId(Integer id);
}
