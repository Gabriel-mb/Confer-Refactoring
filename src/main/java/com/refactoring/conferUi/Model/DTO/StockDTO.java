package com.refactoring.conferUi.Model.DTO;

import java.sql.Date;

public class StockDTO {

    private String equipmentName;
    private Integer employeeId;
    private String supplierName;
    private Integer quantity;
    private Date date;

    public StockDTO() {
    }

    public StockDTO(String equipmentName, String supplierName, Integer quantity) {
        this.equipmentName = equipmentName;
        this.supplierName = supplierName;
        this.quantity = quantity;
    }

    public StockDTO(String equipmentName, String supplierName, Integer quantity, Date date) {
        this.equipmentName = equipmentName;
        this.supplierName = supplierName;
        this.quantity = quantity;
        this.date = date;
    }

    public StockDTO(String equipmentName, Integer employeeId, String supplierName, Integer quantity, Date date) {
        this.equipmentName = equipmentName;
        this.employeeId = employeeId;
        this.supplierName = supplierName;
        this.quantity = quantity;
        this.date = date;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
}
