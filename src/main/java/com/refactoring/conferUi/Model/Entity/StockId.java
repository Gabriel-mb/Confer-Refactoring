package com.refactoring.conferUi.Model.Entity;

import jakarta.persistence.Column;

import java.io.Serializable;
import java.util.Objects;

public class StockId implements Serializable {

    @Column(name = "equipmentName")
    private String equipmentName;

    @Column(name = "supplierId")
    private Integer supplierId;

    public StockId() {}

    public StockId(String equipmentName, Integer supplierId) {
        this.equipmentName = equipmentName;
        this.supplierId = supplierId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StockId stockId = (StockId) o;
        return Objects.equals(equipmentName, stockId.equipmentName) && Objects.equals(supplierId, stockId.supplierId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentName, supplierId);
    }
}
