package com.jway.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Stato.
 */
@Table("stato")
public class Stato implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("id_stadio")
    private Long idStadio;

    @Column("ds_stadio")
    private String dsStadio;

    @Column("ts_cambio_stato")
    private Instant tsCambioStato;

    @Transient
    private Stadio stadio;

    @Transient
    @JsonIgnoreProperties(value = { "assistito", "tipo", "gestore", "origine", "statis" }, allowSetters = true)
    private Set<Evento> eventis = new HashSet<>();

    @Column("stadio_id")
    private Long stadioId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Stato id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdStadio() {
        return this.idStadio;
    }

    public Stato idStadio(Long idStadio) {
        this.setIdStadio(idStadio);
        return this;
    }

    public void setIdStadio(Long idStadio) {
        this.idStadio = idStadio;
    }

    public String getDsStadio() {
        return this.dsStadio;
    }

    public Stato dsStadio(String dsStadio) {
        this.setDsStadio(dsStadio);
        return this;
    }

    public void setDsStadio(String dsStadio) {
        this.dsStadio = dsStadio;
    }

    public Instant getTsCambioStato() {
        return this.tsCambioStato;
    }

    public Stato tsCambioStato(Instant tsCambioStato) {
        this.setTsCambioStato(tsCambioStato);
        return this;
    }

    public void setTsCambioStato(Instant tsCambioStato) {
        this.tsCambioStato = tsCambioStato;
    }

    public Stadio getStadio() {
        return this.stadio;
    }

    public void setStadio(Stadio stadio) {
        this.stadio = stadio;
        this.stadioId = stadio != null ? stadio.getId() : null;
    }

    public Stato stadio(Stadio stadio) {
        this.setStadio(stadio);
        return this;
    }

    public Set<Evento> getEventis() {
        return this.eventis;
    }

    public void setEventis(Set<Evento> eventos) {
        if (this.eventis != null) {
            this.eventis.forEach(i -> i.removeStati(this));
        }
        if (eventos != null) {
            eventos.forEach(i -> i.addStati(this));
        }
        this.eventis = eventos;
    }

    public Stato eventis(Set<Evento> eventos) {
        this.setEventis(eventos);
        return this;
    }

    public Stato addEventi(Evento evento) {
        this.eventis.add(evento);
        evento.getStatis().add(this);
        return this;
    }

    public Stato removeEventi(Evento evento) {
        this.eventis.remove(evento);
        evento.getStatis().remove(this);
        return this;
    }

    public Long getStadioId() {
        return this.stadioId;
    }

    public void setStadioId(Long stadio) {
        this.stadioId = stadio;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Stato)) {
            return false;
        }
        return id != null && id.equals(((Stato) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Stato{" +
            "id=" + getId() +
            ", idStadio=" + getIdStadio() +
            ", dsStadio='" + getDsStadio() + "'" +
            ", tsCambioStato='" + getTsCambioStato() + "'" +
            "}";
    }
}
