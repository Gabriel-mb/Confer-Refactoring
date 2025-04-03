package com.refactoring.conferUi.dao;

import com.refactoring.conferUi.Model.Entity.Epi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;

public interface EpiRepository extends JpaRepository<Epi, Integer> {

    @Modifying
    @Query(nativeQuery = true, value = """
        UPDATE epis e, episBorrowed eb
        SET e.quantity = e.quantity + eb.quantity,
            eb.quantity = 0
        WHERE e.epiName = eb.epiName\s
          AND e.numCa = eb.numCa
          AND eb.epiName = :epiName\s
          AND eb.numCa = :numCa\s
          AND DATE(eb.date) = :date
       \s""")
    void devolverEpiParaEstoque(
            @Param("epiName") String epiName,
            @Param("numCa") Integer numCa,
            @Param("date") Date date);

}
