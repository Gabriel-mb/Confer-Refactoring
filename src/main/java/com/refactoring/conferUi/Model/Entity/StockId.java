package com.refactoring.conferUi.Model.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class StockId implements Serializable {

    @Column(name = "equipment_name")
    private String equipmentName;

    @Column(name = "supplier_id")
    private Integer supplierId;

    public StockId() {}

    public StockId(String equipmentName, Integer supplierId) {
        this.equipmentName = equipmentName;
        this.supplierId = supplierId;
    }

    // equals() e hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockId stockId = (StockId) o;
        return Objects.equals(equipmentName, stockId.equipmentName) &&
                Objects.equals(supplierId, stockId.supplierId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentName, supplierId);
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
}