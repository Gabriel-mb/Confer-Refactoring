package com.refactoring.conferUi.Services;

import java.sql.*;
import java.util.Optional;

import com.refactoring.conferUi.Model.Entity.Employee;
import com.refactoring.conferUi.dao.EmployeeRepository;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public void create(Employee employee) throws SQLException {
        employeeRepository.save(employee);
    }

    public Employee readId(Integer id) throws SQLException{
        Optional<Employee> result = employeeRepository.findById(id);

        if (result.isPresent()) {
            return result.get();
        }
        throw new RuntimeException();
    }

    public void updateName(Employee employee) throws SQLException {
        employeeRepository.save(employee);
    }

    public void delete(Integer id) throws SQLException {
        employeeRepository.deleteById(id);
    }

    public ObservableList<Employee> listEmployees() throws SQLException{
        return (ObservableList<Employee>) employeeRepository.findAll();
    }
}

