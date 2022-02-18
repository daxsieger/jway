package com.jway.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Stadio.
 */
@Table("stadio")
public class Stadio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("id_stadio")
    private Long idStadio;

    @Column("ds_stadio")
    private String dsStadio;

    @Transient
    private Processo processo;

    @Column("processo_id")
    private Long processoId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Stadio id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdStadio() {
        return this.idStadio;
    }

    public Stadio idStadio(Long idStadio) {
        this.setIdStadio(idStadio);
        return this;
    }

    public void setIdStadio(Long idStadio) {
        this.idStadio = idStadio;
    }

    public String getDsStadio() {
        return this.dsStadio;
    }

    public Stadio dsStadio(String dsStadio) {
        this.setDsStadio(dsStadio);
        return this;
    }

    public void setDsStadio(String dsStadio) {
        this.dsStadio = dsStadio;
    }

    public Processo getProcesso() {
        return this.processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
        this.processoId = processo != null ? processo.getId() : null;
    }

    public Stadio processo(Processo processo) {
        this.setProcesso(processo);
        return this;
    }

    public Long getProcessoId() {
        return this.processoId;
    }

    public void setProcessoId(Long processo) {
        this.processoId = processo;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Stadio)) {
            return false;
        }
        return id != null && id.equals(((Stadio) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Stadio{" +
            "id=" + getId() +
            ", idStadio=" + getIdStadio() +
            ", dsStadio='" + getDsStadio() + "'" +
            "}";
    }
}
