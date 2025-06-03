package com.refactoring.conferUi.Model.Entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "equipments")
public class Equipment {

    @Id
    @Column(name = "id_equipment")
    private Integer idEquipment;

    @Column(name = "name", nullable = false)
    private String nameEquip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", referencedColumnName = "supplier_id")
    private Supplier supplier;

    @OneToMany(mappedBy = "equipment")
    private List<EquipmentBorrowed> borrowings;

    public Equipment() {
    }

    public Equipment(Integer idEquipment) {
        this.idEquipment = idEquipment;
    }

    public Equipment(Integer idEquipment, String nameEquip, Supplier supplier) {
        this.idEquipment = idEquipment;
        this.nameEquip = nameEquip;
        this.supplier = supplier;
    }

    public Integer getIdEquipment() {
        return idEquipment;
    }

    public void setIdEquipment(Integer idEquipment) {
        this.idEquipment = idEquipment;
    }

    public String getNameEquip() {
        return nameEquip;
    }

    public void setNameEquip(String nameEquip) {
        this.nameEquip = nameEquip;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public List<EquipmentBorrowed> getBorrowings() {
        return borrowings;
    }

    public void setBorrowings(List<EquipmentBorrowed> borrowings) {
        this.borrowings = borrowings;
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "idEquipment=" + idEquipment +
                ", nameEquip='" + nameEquip + '\'' +
                ", supplier=" + supplier +
                '}';
    }
}
