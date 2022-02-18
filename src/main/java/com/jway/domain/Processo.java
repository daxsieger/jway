package com.jway.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Processo.
 */
@Table("processo")
public class Processo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("id_processo")
    private Long idProcesso;

    @Column("ds_processo")
    private String dsProcesso;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Processo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdProcesso() {
        return this.idProcesso;
    }

    public Processo idProcesso(Long idProcesso) {
        this.setIdProcesso(idProcesso);
        return this;
    }

    public void setIdProcesso(Long idProcesso) {
        this.idProcesso = idProcesso;
    }

    public String getDsProcesso() {
        return this.dsProcesso;
    }

    public Processo dsProcesso(String dsProcesso) {
        this.setDsProcesso(dsProcesso);
        return this;
    }

    public void setDsProcesso(String dsProcesso) {
        this.dsProcesso = dsProcesso;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Processo)) {
            return false;
        }
        return id != null && id.equals(((Processo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Processo{" +
            "id=" + getId() +
            ", idProcesso=" + getIdProcesso() +
            ", dsProcesso='" + getDsProcesso() + "'" +
            "}";
    }
}
