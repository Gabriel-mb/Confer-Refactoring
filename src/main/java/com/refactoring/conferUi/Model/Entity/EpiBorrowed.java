package com.refactoring.conferUi.Model.Entity;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "episborrowed")
public class EpiBorrowed {

    @EmbeddedId
    private EpiId id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "date")
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEmployee")
    private Employee employee;

    public EpiBorrowed() {}

    public EpiBorrowed(EpiId id, Integer quantity, Date date, Employee employee) {
        this.id = id;
        this.quantity = quantity;
        this.date = date;
        this.employee = employee;
    }

    public EpiId getId() {
        return id;
    }

    public void setId(EpiId id) {
        this.id = id;
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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

}
