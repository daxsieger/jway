package com.jway.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Gestore.
 */
@Table("gestore")
public class Gestore implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("id_gestore")
    private Long idGestore;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Gestore id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdGestore() {
        return this.idGestore;
    }

    public Gestore idGestore(Long idGestore) {
        this.setIdGestore(idGestore);
        return this;
    }

    public void setIdGestore(Long idGestore) {
        this.idGestore = idGestore;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Gestore)) {
            return false;
        }
        return id != null && id.equals(((Gestore) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Gestore{" +
            "id=" + getId() +
            ", idGestore=" + getIdGestore() +
            "}";
    }
}
