package com.refactoring.conferUi.dao;

import com.refactoring.conferUi.Model.DTO.EquipmentDTO;
import com.refactoring.conferUi.Model.Entity.Equipment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Equipment e WHERE e.idEquipment = :idEquipment AND e.supplier.supplierId = :supplierId")
    void deleteByIdEquipmentAndSupplierId(
            @Param("idEquipment") int idEquipment,
            @Param("supplierId") int supplierId
    );

    @Query("SELECT e FROM Equipment e WHERE e.idEquipment = :idEquipment AND e.supplier.supplierId = :supplierId")
    Optional<Equipment> findByIdEquipmentAndSupplierId(
            @Param("idEquipment") int idEquipment,
            @Param("supplierId") int supplierId
    );

    @Query("SELECT NEW com.refactoring.conferUi.Model.DTO.EquipmentDTO(" +
            "e.idEquipment, " +
            "e.nameEquip, " +
            "s.supplierName, " +
            "'', " +
            "'Armazenado', " +  // status
            "null " +
            ") " +
            "FROM Equipment e " +
            "JOIN e.supplier s " +
            "WHERE e.idEquipment = :id")
    List<EquipmentDTO> findEquipmentWithSupplierById(@Param("id") Integer id);

    @Query("""
    SELECT NEW com.refactoring.conferUi.Model.DTO.EquipmentDTO(
        e.idEquipment,
        e.nameEquip,
        s.supplierName,
        '',
        'Armazenado',
        null
    )
    FROM Equipment e
    JOIN e.supplier s
    WHERE NOT EXISTS (
        SELECT 1 FROM EquipmentBorrowed b 
        WHERE b.equipment = e
    )
    ORDER BY e.idEquipment
""")
    List<EquipmentDTO> findStoredEquipment();

    @Query("""
    SELECT NEW com.refactoring.conferUi.Model.DTO.EquipmentDTO(
        e.idEquipment,
        e.nameEquip,
        s.supplierName,
        emp.name,
        'Em Uso',
        b.date
    )
    FROM Equipment e
    JOIN e.supplier s
    JOIN EquipmentBorrowed b ON b.equipment = e
    JOIN b.employee emp
    WHERE b.date = (
        SELECT MAX(b2.date)
        FROM EquipmentBorrowed b2
        WHERE b2.equipment = e
    )
    ORDER BY e.idEquipment
""")
    List<EquipmentDTO> findBorrowedEquipment();
}