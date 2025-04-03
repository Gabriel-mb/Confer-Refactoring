package com.refactoring.conferUi.Model.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EpiId implements Serializable {

    @Column(name = "epiName")
    private String epiName;

    @Column(name = "numCa")
    private Integer numCa;

    public EpiId() {
    }

    public EpiId(String epiName, Integer numCa) {
        this.epiName = epiName;
        this.numCa = numCa;
    }

    public String getEpiName() {
        return epiName;
    }

    public void setEpiName(String epiName) {
        this.epiName = epiName;
    }

    public Integer getNumCa() {
        return numCa;
    }

    public void setNumCa(Integer numCa) {
        this.numCa = numCa;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EpiId epiId = (EpiId) o;
        return Objects.equals(epiName, epiId.epiName) && Objects.equals(numCa, epiId.numCa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(epiName, numCa);
    }
}
