package com.refactoring.conferUi.Model.Entity;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "stockborrowed")
public class StockBorrowed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_borrowed")
    private Integer idBorrowed;

    @Embedded
    private StockId stockId;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEmployee")
    private Employee employee;

    @Column(name = "date")
    private Date date;

    public StockBorrowed() {
    }

    public StockBorrowed(StockId stockId, Integer quantity, Employee employee, Date date) {
        this.stockId = stockId;
        this.quantity = quantity;
        this.employee = employee;
        this.date = date;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public StockId getStockId() {
        return stockId;
    }

    public void setStockId(StockId stockId) {
        this.stockId = stockId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}