package com.refactoring.conferUi.Model.DTO;

import java.sql.Date;

public class EpiDTO {
    private String epiName;
    private Integer numCa;
    private Integer quantity;
    private Date date;
    private Integer employeeId;

    public EpiDTO(String epiName, Integer numCa, Integer quantity, Date date, Integer employeeId) {
        this.epiName = epiName;
        this.numCa = numCa;
        this.quantity = quantity;
        this.date = date;
        this.employeeId = employeeId;
    }

    public EpiDTO(String epiName, Integer numCa, Date date) {
        this.epiName = epiName;
        this.numCa = numCa;
        this.date = date;
    }

    public EpiDTO(String epiName, Integer numCa, Integer quantity, Date date) {
        this.epiName = epiName;
        this.numCa = numCa;
        this.quantity = quantity;
        this.date = date;
    }

    public EpiDTO(String epiName, Integer numCa, Integer quantity) {
        this.epiName = epiName;
        this.numCa = numCa;
        this.quantity = quantity;
    }

    // Getters (sem setters para imutabilidade)
    public String getEpiName() { return epiName; }
    public Integer getNumCa() { return numCa; }
    public Integer getQuantity() { return quantity; }
    public Date getDate() { return date; }
    public Integer getEmployeeId() { return employeeId; }

}
