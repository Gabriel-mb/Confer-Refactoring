package com.refactoring.conferUi.Model.DTO;


import java.sql.Date;

public class EquipmentDTO {

    private String nameEquip;
    private Integer idEquipment;
    private String supplierName;
    private String employeeName;
    private String status;
    private Date date;

    public EquipmentDTO(Integer idEquipment, String nameEquip, String supplierName,
                        String employeeName, String status, Date date) {
        this.idEquipment = idEquipment;
        this.nameEquip = nameEquip;
        this.supplierName = supplierName;
        this.employeeName = employeeName;
        this.status = status;
        this.date = date;
    }

    public String getNameEquip() {
        return nameEquip;
    }

    public void setNameEquip(String nameEquip) {
        this.nameEquip = nameEquip;
    }

    public Integer getIdEquipment() {
        return idEquipment;
    }

    public void setIdEquipment(Integer idEquipment) {
        this.idEquipment = idEquipment;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
