package com.refactoring.conferUi.dao;

import com.refactoring.conferUi.Model.Entity.StockBorrowed;
import com.refactoring.conferUi.Model.Entity.StockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;
import java.util.Optional;


public interface StockBorrowedRepository extends JpaRepository<StockBorrowed, Integer> {

    List<StockBorrowed> findByEmployee_IdEmployee(Integer idEmployee);

    @Query("SELECT sb FROM StockBorrowed sb WHERE sb.stockId = :stockId " +
            "AND sb.employee.idEmployee = :employeeId " +
            "AND sb.date = :date")
    StockBorrowed findByStockIdAndEmployeeIdAndDate(
            @Param("stockId") StockId stockId,
            @Param("employeeId") Integer employeeId,
            @Param("date") Date date);


}