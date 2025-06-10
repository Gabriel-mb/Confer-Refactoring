package com.refactoring.conferUi.dao;

import com.refactoring.conferUi.Model.Entity.Stock;
import com.refactoring.conferUi.Model.Entity.StockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, StockId> {

    @Query("SELECT s FROM Stock s WHERE s.stockId.equipmentName = :equipmentName AND s.stockId.supplierId = :supplierId")
    Stock findByEquipmentNameAndSupplierId(
            @Param("equipmentName") String equipmentName,
            @Param("supplierId") Integer supplierId);

    @Query("SELECT DISTINCT s.id.equipmentName FROM Stock s")
    List<String> findAllEquipmentNames();



}