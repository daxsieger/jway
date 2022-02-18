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
 * A Evento.
 */
@Table("evento")
public class Evento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("id_evento")
    private Long idEvento;

    @Column("ts_evento")
    private Instant tsEvento;

    @Column("note")
    private String note;

    @Transient
    private Assistito assistito;

    @Transient
    private TipoEvento tipo;

    @Transient
    private Gestore gestore;

    @Transient
    private Produttore origine;

    @Transient
    @JsonIgnoreProperties(value = { "stadio", "eventis" }, allowSetters = true)
    private Set<Stato> statis = new HashSet<>();

    @Column("assistito_id")
    private Long assistitoId;

    @Column("tipo_id")
    private Long tipoId;

    @Column("gestore_id")
    private Long gestoreId;

    @Column("origine_id")
    private Long origineId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Evento id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdEvento() {
        return this.idEvento;
    }

    public Evento idEvento(Long idEvento) {
        this.setIdEvento(idEvento);
        return this;
    }

    public void setIdEvento(Long idEvento) {
        this.idEvento = idEvento;
    }

    public Instant getTsEvento() {
        return this.tsEvento;
    }

    public Evento tsEvento(Instant tsEvento) {
        this.setTsEvento(tsEvento);
        return this;
    }

    public void setTsEvento(Instant tsEvento) {
        this.tsEvento = tsEvento;
    }

    public String getNote() {
        return this.note;
    }

    public Evento note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Assistito getAssistito() {
        return this.assistito;
    }

    public void setAssistito(Assistito assistito) {
        this.assistito = assistito;
        this.assistitoId = assistito != null ? assistito.getId() : null;
    }

    public Evento assistito(Assistito assistito) {
        this.setAssistito(assistito);
        return this;
    }

    public TipoEvento getTipo() {
        return this.tipo;
    }

    public void setTipo(TipoEvento tipoEvento) {
        this.tipo = tipoEvento;
        this.tipoId = tipoEvento != null ? tipoEvento.getId() : null;
    }

    public Evento tipo(TipoEvento tipoEvento) {
        this.setTipo(tipoEvento);
        return this;
    }

    public Gestore getGestore() {
        return this.gestore;
    }

    public void setGestore(Gestore gestore) {
        this.gestore = gestore;
        this.gestoreId = gestore != null ? gestore.getId() : null;
    }

    public Evento gestore(Gestore gestore) {
        this.setGestore(gestore);
        return this;
    }

    public Produttore getOrigine() {
        return this.origine;
    }

    public void setOrigine(Produttore produttore) {
        this.origine = produttore;
        this.origineId = produttore != null ? produttore.getId() : null;
    }

    public Evento origine(Produttore produttore) {
        this.setOrigine(produttore);
        return this;
    }

    public Set<Stato> getStatis() {
        return this.statis;
    }

    public void setStatis(Set<Stato> statoes) {
        this.statis = statoes;
    }

    public Evento statis(Set<Stato> statoes) {
        this.setStatis(statoes);
        return this;
    }

    public Evento addStati(Stato stato) {
        this.statis.add(stato);
        stato.getEventis().add(this);
        return this;
    }

    public Evento removeStati(Stato stato) {
        this.statis.remove(stato);
        stato.getEventis().remove(this);
        return this;
    }

    public Long getAssistitoId() {
        return this.assistitoId;
    }

    public void setAssistitoId(Long assistito) {
        this.assistitoId = assistito;
    }

    public Long getTipoId() {
        return this.tipoId;
    }

    public void setTipoId(Long tipoEvento) {
        this.tipoId = tipoEvento;
    }

    public Long getGestoreId() {
        return this.gestoreId;
    }

    public void setGestoreId(Long gestore) {
        this.gestoreId = gestore;
    }

    public Long getOrigineId() {
        return this.origineId;
    }

    public void setOrigineId(Long produttore) {
        this.origineId = produttore;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Evento)) {
            return false;
        }
        return id != null && id.equals(((Evento) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Evento{" +
            "id=" + getId() +
            ", idEvento=" + getIdEvento() +
            ", tsEvento='" + getTsEvento() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
