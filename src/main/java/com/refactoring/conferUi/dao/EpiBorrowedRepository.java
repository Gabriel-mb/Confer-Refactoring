package com.refactoring.conferUi.dao;

import com.refactoring.conferUi.Model.Entity.EpiBorrowed;
import com.refactoring.conferUi.Model.Entity.EpiId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

public interface EpiBorrowedRepository extends JpaRepository<EpiBorrowed, EpiId> {

    @Query("SELECT e FROM EpiBorrowed e WHERE e.employee.idEmployee = :idEmployee")
    List<EpiBorrowed> findByIdEmployee(@Param("idEmployee") Integer idEmployee);

    @Modifying
    @Transactional
    @Query("UPDATE EpiBorrowed e SET e.quantity = :newQuantity " +
            "WHERE e.id.numCa = :numCa AND e.employee.idEmployee = :employeeId")
    int updateQuantityByNumCaAndIdEmployee(
            @Param("newQuantity") Integer newQuantity,
            @Param("numCa") Integer numCa,
            @Param("employeeId") Integer employeeId);

    @Query("SELECT e FROM EpiBorrowed e " +
            "WHERE e.id.epiName = :epiName " +
            "AND e.id.numCa = :numCa " +
            "AND CAST(e.date AS date) = CAST(:date AS date)")
    EpiBorrowed findByEpiNameNumCaAndDate(
            @Param("epiName") String epiName,
            @Param("numCa") Integer numCa,
            @Param("date") Date date);

    EpiBorrowed findById_EpiNameAndId_NumCa(String epiName, Integer numCa);
}