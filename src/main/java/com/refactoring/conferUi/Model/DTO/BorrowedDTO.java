package com.refactoring.conferUi.Model.DTO;

import java.sql.Date;

public class BorrowedDTO {

    private String equipmentName;
    private Integer idEquipment;
    private Integer employeeId;
    private String supplierName;
    private Integer supplierId;
    private Date date;


    public BorrowedDTO(String equipmentName, Integer idEquipment, Date date, String supplierName) {
        this.equipmentName = equipmentName;
        this.idEquipment = idEquipment;
        this.date = date;
        this.supplierName = supplierName;
    }

    public BorrowedDTO(Integer idEquipment, Integer employeeId, Integer supplierId, Date date) {
        this.idEquipment = idEquipment;
        this.employeeId = employeeId;
        this.supplierId = supplierId;
        this.date = date;
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
