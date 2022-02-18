package com.jway.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Transizioni.
 */
@Table("transizioni")
public class Transizioni implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("id_transizione")
    private Long idTransizione;

    @Column("ds_transizione")
    private String dsTransizione;

    @Transient
    private Processo processo;

    @Transient
    @JsonIgnoreProperties(value = { "processo" }, allowSetters = true)
    private Stadio stadioIniziale;

    @Transient
    @JsonIgnoreProperties(value = { "processo" }, allowSetters = true)
    private Stadio stadioFinale;

    @Column("processo_id")
    private Long processoId;

    @Column("stadio_iniziale_id")
    private Long stadioInizialeId;

    @Column("stadio_finale_id")
    private Long stadioFinaleId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Transizioni id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdTransizione() {
        return this.idTransizione;
    }

    public Transizioni idTransizione(Long idTransizione) {
        this.setIdTransizione(idTransizione);
        return this;
    }

    public void setIdTransizione(Long idTransizione) {
        this.idTransizione = idTransizione;
    }

    public String getDsTransizione() {
        return this.dsTransizione;
    }

    public Transizioni dsTransizione(String dsTransizione) {
        this.setDsTransizione(dsTransizione);
        return this;
    }

    public void setDsTransizione(String dsTransizione) {
        this.dsTransizione = dsTransizione;
    }

    public Processo getProcesso() {
        return this.processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
        this.processoId = processo != null ? processo.getId() : null;
    }

    public Transizioni processo(Processo processo) {
        this.setProcesso(processo);
        return this;
    }

    public Stadio getStadioIniziale() {
        return this.stadioIniziale;
    }

    public void setStadioIniziale(Stadio stadio) {
        this.stadioIniziale = stadio;
        this.stadioInizialeId = stadio != null ? stadio.getId() : null;
    }

    public Transizioni stadioIniziale(Stadio stadio) {
        this.setStadioIniziale(stadio);
        return this;
    }

    public Stadio getStadioFinale() {
        return this.stadioFinale;
    }

    public void setStadioFinale(Stadio stadio) {
        this.stadioFinale = stadio;
        this.stadioFinaleId = stadio != null ? stadio.getId() : null;
    }

    public Transizioni stadioFinale(Stadio stadio) {
        this.setStadioFinale(stadio);
        return this;
    }

    public Long getProcessoId() {
        return this.processoId;
    }

    public void setProcessoId(Long processo) {
        this.processoId = processo;
    }

    public Long getStadioInizialeId() {
        return this.stadioInizialeId;
    }

    public void setStadioInizialeId(Long stadio) {
        this.stadioInizialeId = stadio;
    }

    public Long getStadioFinaleId() {
        return this.stadioFinaleId;
    }

    public void setStadioFinaleId(Long stadio) {
        this.stadioFinaleId = stadio;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transizioni)) {
            return false;
        }
        return id != null && id.equals(((Transizioni) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transizioni{" +
            "id=" + getId() +
            ", idTransizione=" + getIdTransizione() +
            ", dsTransizione='" + getDsTransizione() + "'" +
            "}";
    }
}
