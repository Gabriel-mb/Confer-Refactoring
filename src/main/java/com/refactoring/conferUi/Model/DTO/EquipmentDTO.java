package com.refactoring.conferUi.Model.DTO;


import java.sql.Date;

public class EquipmentDTO {


    private String equipmentName;
    private Integer idEquipment;
    private String supplierName;
    private String employeeName;
    private String status;
    private Date date;

    public EquipmentDTO(Integer idEquipment, String equipmentName,
                              String supplierName, String employeeName,
                              String status, Date date) {
        this.idEquipment = idEquipment;
        this.equipmentName = equipmentName;
        this.supplierName = supplierName;
        this.employeeName = employeeName;
        this.status = status;
        this.date = date;
    }

    public EquipmentDTO(String equipmentName, Integer idEquipment, String supplierName) {
        this.equipmentName = equipmentName;
        this.idEquipment = idEquipment;
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

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}
