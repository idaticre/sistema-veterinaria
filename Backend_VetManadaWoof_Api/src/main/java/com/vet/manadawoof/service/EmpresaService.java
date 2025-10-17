package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.EmpresaEntity;

import java.util.List;

public interface EmpresaService {

    List<EmpresaEntity> listarEmpresas();

    EmpresaEntity obtenerEmpresa(Integer id);

    String actualizarEmpresa(EmpresaEntity empresa);
}
