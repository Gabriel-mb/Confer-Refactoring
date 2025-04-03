package com.refactoring.conferUi.dao;

import com.refactoring.conferUi.Model.Entity.Supplier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    Optional<Supplier> findBySupplierName(String name);

    @Query("SELECT s.name FROM Supplier s")
    List<String> findAllSupplierNames();
}
