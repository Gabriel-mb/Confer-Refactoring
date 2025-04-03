package com.refactoring.conferUi.dao;

import com.refactoring.conferUi.Model.Entity.EpiBorrowed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface EpiBorrowedRepository extends JpaRepository<EpiBorrowed, Integer> {

    List<EpiBorrowed> findByEmployeeId(Integer employeeId);

    @Modifying
    @Query("UPDATE EpiBorrowed e SET e.quantity = :quantity " +
            "WHERE e.id.numCa = :numCa AND e.employeeId = :employeeId")
    void updateQuantityByNumCaAndEmployeeId(
            @Param("quantity") Integer quantity,
            @Param("numCa") Integer numCa,
            @Param("employeeId") Integer employeeId);

    @Query("SELECT e FROM EpiBorrowed e " +
            "WHERE e.id.epiName = :epiName " +
            "AND e.id.numCa = :numCa " +
            "AND FUNCTION('DATE', e.date) = FUNCTION('DATE', :date)")
    EpiBorrowed findByEpiNameNumCaAndDate(
            @Param("epiName") String epiName,
            @Param("numCa") Integer numCa,
            @Param("date") Date date);
}
