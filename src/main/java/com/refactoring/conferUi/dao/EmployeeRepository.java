package com.refactoring.conferUi.dao;

import com.refactoring.conferUi.Model.Entity.Employee;
import com.refactoring.conferUi.Model.Entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
}
