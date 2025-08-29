package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "EmpresaEntity")
@Table(name = "empresa")
@NamedStoredProcedureQuery(
        name = "EmpresaEntity.spEmpresa",
        procedureName = "sp_empresa",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_accion", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_razon_social", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_ruc", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_direccion", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_ciudad", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_distrito", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_telefono", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_correo", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_representante", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_logo_empresa", type = String.class)
        }
)
public class EmpresaEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String razonSocial;

    private String ruc;

    private String direccion;

    private String ciudad;

    private String distrito;

    private String telefono;

    private String correo;

    private String representante;

    private String logoEmpresa;
}
