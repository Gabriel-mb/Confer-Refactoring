package com.refactoring.conferUi.Model.Entity;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "epis")
public class Epi {

    @EmbeddedId
    private EpiId epiId;

    @Column(name = "quantity")
    private Integer quantity;

    public Epi() {
    }

    public Epi(EpiId epiId, Integer quantity) {
        this.epiId = epiId;
        this.quantity = quantity;
    }

    public EpiId getEpiId() {
        return epiId;
    }

    public void setEpiId(EpiId epiId) {
        this.epiId = epiId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
