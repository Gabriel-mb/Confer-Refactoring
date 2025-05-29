package com.refactoring.conferUi.dao;

import com.refactoring.conferUi.Model.Entity.Employee;
import com.refactoring.conferUi.Model.Entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Employee e WHERE e.idEmployee IN :ids")
    void deleteByIdList(@Param("ids") List<Integer> ids);
}
