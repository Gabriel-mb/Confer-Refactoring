package com.refactoring.conferUi.Model.DTO;

import java.sql.Date;

public class BorrowedDTO {

    private String equipmentName;
    private Integer idEquipment;
    private Date date;
    private String supplierName;

    public BorrowedDTO(String equipmentName, Integer idEquipment, Date date, String supplierName) {
        this.equipmentName = equipmentName;
        this.idEquipment = idEquipment;
        this.date = date;
        this.supplierName = supplierName;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public Integer getIdEquipment() {
        return idEquipment;
    }

    public void setIdEquipment(Integer idEquipment) {
        this.idEquipment = idEquipment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}
