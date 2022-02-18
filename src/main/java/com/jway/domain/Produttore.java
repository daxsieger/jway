package com.jway.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Produttore.
 */
@Table("produttore")
public class Produttore implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("id_produttore")
    private Long idProduttore;

    @Column("ds_produttore")
    private String dsProduttore;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Produttore id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdProduttore() {
        return this.idProduttore;
    }

    public Produttore idProduttore(Long idProduttore) {
        this.setIdProduttore(idProduttore);
        return this;
    }

    public void setIdProduttore(Long idProduttore) {
        this.idProduttore = idProduttore;
    }

    public String getDsProduttore() {
        return this.dsProduttore;
    }

    public Produttore dsProduttore(String dsProduttore) {
        this.setDsProduttore(dsProduttore);
        return this;
    }

    public void setDsProduttore(String dsProduttore) {
        this.dsProduttore = dsProduttore;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Produttore)) {
            return false;
        }
        return id != null && id.equals(((Produttore) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Produttore{" +
            "id=" + getId() +
            ", idProduttore=" + getIdProduttore() +
            ", dsProduttore='" + getDsProduttore() + "'" +
            "}";
    }
}
