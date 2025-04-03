package com.refactoring.conferUi.dao;

import com.refactoring.conferUi.Model.Entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Integer> {

    @Query("SELECT s FROM Stock s WHERE s.equipment.nameEquip = :equipmentName AND s.equipment.supplier.supplierId = :supplierId")
    Stock findByNameEquipAndSupplierId(@Param("nameEquip") String nameEquip,
                                       @Param("supplierId") int supplierId);

    @Modifying
    @Query("UPDATE Stock s SET s.quantity = :quantity WHERE s.equipment.nameEquip = :equipmentName AND s.equipment.supplier.supplierId = :supplierId")
    void updateByNameEquipAndSupplierId(@Param("quantity") int quantity,
                                        @Param("equipmentName") String equipmentName,
                                        @Param("supplierId") int supplierId);

    @Query("SELECT s.equipmentName FROM Stock s")
    List<String> findByNameEquip();
}
