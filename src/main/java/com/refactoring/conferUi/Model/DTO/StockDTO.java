package com.refactoring.conferUi.Model.DTO;

public class StockDTO {

    private String equipmentName;
    private String supplierName;
    private Integer quantity;

    public StockDTO() {
    }

    public StockDTO(String equipmentName, String supplierName, Integer quantity) {
        this.equipmentName = equipmentName;
        this.supplierName = supplierName;
        this.quantity = quantity;
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
}
