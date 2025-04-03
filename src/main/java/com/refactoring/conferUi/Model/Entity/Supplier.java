package com.refactoring.conferUi.Model.Entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "supplier")
public class Supplier {

    @Id
    @Column(name = "supplier_id")
    private Integer supplierId;

    @Column(name = "name")
    private String supplierName;

    @OneToMany(mappedBy = "supplier")
    private List<Equipment> equipment;

    public Supplier() {
    }

    public Supplier(String supplierName, Integer supplierId) {
        this.supplierName = supplierName;
        this.supplierId = supplierId;
    }

    public Supplier(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierName() {
        return supplierName;
    }


    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment;
    }

    @Override
    public String toString() {
        return supplierName;
    }
}
