package com.refactoring.conferUi.Model.Entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @Column(name = "idEmployee")
    private Integer idEmployee;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<EquipmentBorrowed> borrowings;

    public Employee() {
    }

    public Employee(Integer idEmployee) {
        this.idEmployee = idEmployee;
    }

    public Employee(Integer idEmployee, String name) {
        this.idEmployee = idEmployee;
        this.name = name;
    }

    public Integer getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(Integer idEmployee) {
        this.idEmployee = idEmployee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EquipmentBorrowed> getBorrowings() {
        return borrowings;
    }

    public void setBorrowings(List<EquipmentBorrowed> borrowings) {
        this.borrowings = borrowings;
    }

}
