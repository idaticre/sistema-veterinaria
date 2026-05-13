package com.vet.manadawoof.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CorrelativoIDEntity implements Serializable {
    
    @Column(name = "tipo_comprobante_id")
    private Integer tipoComprobanteId;

    @Column(name = "serie")
    private String serie;

    public CorrelativoIDEntity() {
    }

    public CorrelativoIDEntity(Integer tipoComprobanteId, String serie) {
        this.tipoComprobanteId = tipoComprobanteId;
        this.serie = serie;
    }

    public Integer getTipoComprobanteId() {
        return tipoComprobanteId;
    }

    public void setTipoComprobanteId(Integer tipoComprobanteId) {
        this.tipoComprobanteId = tipoComprobanteId;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CorrelativoIDEntity that)) return false;
        return Objects.equals(tipoComprobanteId, that.tipoComprobanteId)
                && Objects.equals(serie, that.serie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipoComprobanteId, serie);
    }
    
}
