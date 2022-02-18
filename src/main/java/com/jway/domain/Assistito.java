package com.jway.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Assistito.
 */
@Table("assistito")
public class Assistito implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("id_assistito")
    private Long idAssistito;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Assistito id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdAssistito() {
        return this.idAssistito;
    }

    public Assistito idAssistito(Long idAssistito) {
        this.setIdAssistito(idAssistito);
        return this;
    }

    public void setIdAssistito(Long idAssistito) {
        this.idAssistito = idAssistito;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Assistito)) {
            return false;
        }
        return id != null && id.equals(((Assistito) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Assistito{" +
            "id=" + getId() +
            ", idAssistito=" + getIdAssistito() +
            "}";
    }
}
