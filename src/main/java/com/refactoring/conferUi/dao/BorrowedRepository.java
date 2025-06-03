package com.refactoring.conferUi.dao;

import com.refactoring.conferUi.Model.DTO.BorrowedDTO;
import com.refactoring.conferUi.Model.Entity.EquipmentBorrowed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface BorrowedRepository extends JpaRepository<EquipmentBorrowed, Integer> {

    @Modifying
    @Query("DELETE FROM EquipmentBorrowed b WHERE b.equipment.idEquipment = :idEquipment AND b.equipment.supplier.supplierId = :supplierId")
    void deleteByEquipmentIdAndSupplierId(
            @Param("idEquipment") int idEquipment,
            @Param("supplierId") int supplierId
    );

    @Query("SELECT b FROM EquipmentBorrowed b WHERE b.equipment.idEquipment = :idEquipment AND b.equipment.supplier.supplierId = :supplierId")
    Optional<EquipmentBorrowed> findByIdEquipmentAndSupplierId(
            @Param("idEquipment") int idEquipment,
            @Param("supplierId") int supplierId
    );

    @Query("SELECT NEW com.refactoring.conferUi.Model.DTO.BorrowedDTO(" +
            "e.nameEquip, e.idEquipment, b.date, s.supplierName) " +
            "FROM EquipmentBorrowed b " +
            "JOIN b.equipment e " +
            "JOIN e.supplier s " +
            "WHERE b.employee.idEmployee = :employeeId " +
            "ORDER BY b.date DESC")
    List<BorrowedDTO> findBorrowedDetailsByEmployee(@Param("employeeId") Integer employeeId);

    @Query("SELECT COUNT(b) > 0 FROM EquipmentBorrowed b " +
            "WHERE b.employee.idEmployee = :employeeId " +
            "AND b.equipment.idEquipment = :equipmentId " +
            "AND b.equipment.supplier.supplierId = :supplierId " +
            "AND b.date = :date")
    boolean existsSimilarBorrowing(
            @Param("employeeId") int employeeId,
            @Param("equipmentId") int equipmentId,
            @Param("supplierId") int supplierId,
            @Param("date") Date date);
}
