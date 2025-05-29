package com.refactoring.conferUi.Services;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import com.refactoring.conferUi.Model.Entity.Employee;
import com.refactoring.conferUi.dao.EmployeeRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
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
        return null;
    }

    public void updateName(Employee employee) throws SQLException {
        employeeRepository.save(employee);
    }

    public void delete(Integer id) throws SQLException {
        employeeRepository.deleteById(id);
    }

    public ObservableList<Employee> listEmployees() throws SQLException{
        List<Employee> employees = employeeRepository.findAll();
        return FXCollections.observableArrayList(employees);
    }

    public void deleteByList(List<Integer> idList) {
        employeeRepository.deleteByIdList(idList);
    }
}

