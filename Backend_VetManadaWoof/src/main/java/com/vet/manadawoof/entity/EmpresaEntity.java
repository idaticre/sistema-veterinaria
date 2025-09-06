package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "empresa")
@NamedStoredProcedureQuery(
        name = "EmpresaEntity.spEmpresa",
        procedureName = "sp_empresa",
        resultClasses = EmpresaEntity.class,
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_accion", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id", type = Integer.class),
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
    private Integer id;

    @Column(name = "razon_social", length = 128, nullable = false)
    private String razonSocial;

    @Column(name = "ruc", columnDefinition = "CHAR(11)", nullable = false)
    private String ruc;

    @Column(name = "direccion", length = 256)
    private String direccion;

    @Column(name = "ciudad", length = 64)
    private String ciudad;

    @Column(name = "distrito", length = 64)
    private String distrito;

    @Column(name = "telefono", length = 15)
    private String telefono;

    @Column(name = "correo", length = 64)
    private String correo;

    @Column(name = "representante", length = 64)
    private String representante;

    @Column(name = "logo_empresa", length = 255)
    private String logoEmpresa;
}
