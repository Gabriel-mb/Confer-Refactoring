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

    @Query("""
        SELECT NEW com.seuprojeto.dto.EquipmentDTO(
            e.idEquipment,\s
            e.name,\s
            s.name
        )
        FROM Equipment e
        JOIN e.supplier s
        WHERE e.idEquipment = :id
   \s""")
    List<EquipmentDTO> findEquipmentWithSupplierById(@Param("id") Integer id);

    @Query("""
        SELECT NEW com.refactoring.conferUi.Model.DTO.EquipmentDTO(
            e.idEquipment,
            e.name,
            s.name,
            COALESCE(emp.name, ''),
            CASE WHEN b.idEquipment IS NOT NULL THEN 'Em Uso' ELSE 'Armazenado' END,
            b.date
        )
        FROM Equipment e
        JOIN e.supplier s
        LEFT JOIN Borrowed b ON e.idEquipment = b.equipment.idEquipment\s
                          AND e.supplier.id = b.equipment.supplier.id
                          AND b.date = (
                              SELECT MAX(b2.date)\s
                              FROM Borrowed b2\s
                              WHERE b2.equipment.idEquipment = e.idEquipment
                          )
        LEFT JOIN Employee emp ON b.idEmployee = emp.idEmployee
        ORDER BY e.idEquipment
   \s""")
    List<EquipmentDTO> findAllEquipmentWithStatus();
}