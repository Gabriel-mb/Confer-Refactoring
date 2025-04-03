package com.refactoring.conferUi.Model.Entity;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "borrowed")
public class EquipmentBorrowed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_borrowed")
    private Integer idBorrowed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "id_equipment", referencedColumnName = "id_equipment"),
            @JoinColumn(name = "supplier_id", referencedColumnName = "supplier_id")
    })
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employee")
    private Employee employee;

    @Column(name = "date")
    private Date date;

    public EquipmentBorrowed() {
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Integer getIdBorrowed() {
        return idBorrowed;
    }

    public void setIdBorrowed(Integer idBorrowed) {
        this.idBorrowed = idBorrowed;
    }


    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }



    @Override
    public String toString() {
        return "Borrowed{" +
                ", equipment=" + equipment +
                ", date=" + date +
                '}';
    }
}
