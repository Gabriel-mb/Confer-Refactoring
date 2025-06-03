package com.refactoring.conferUi.dao;

import com.refactoring.conferUi.Model.DTO.EpiDTO;
import com.refactoring.conferUi.Model.Entity.Epi;
import com.refactoring.conferUi.Model.Entity.EpiId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EpiRepository extends JpaRepository<Epi, EpiId> {


    @Modifying
    @Transactional
    @Query("UPDATE Epi e SET e.quantity = e.quantity - :quantity " +
            "WHERE e.epiId.epiName = :epiName AND e.epiId.numCa = :numCa " +
            "AND e.quantity >= :quantity AND :quantity > 0")
    void decreaseStock(
            @Param("epiName") String epiName,
            @Param("numCa") Integer numCa,
            @Param("quantity") Integer quantity);

    @Modifying
    @Transactional
    @Query("UPDATE Epi e SET e.quantity = e.quantity + :quantity " +
            "WHERE e.epiId.epiName = :epiName AND e.epiId.numCa = :numCa")
    void addToStock(
            @Param("epiName") String epiName,
            @Param("numCa") Integer numCa,
            @Param("quantity") Integer quantity);

    default void updateFromDto(EpiDTO dto) {
        EpiId id = new EpiId(dto.getEpiName(), dto.getNumCa());
        Epi epi = this.findById(id).orElse(new Epi(id, 0));
        epi.setQuantity(dto.getQuantity());
        this.save(epi);
    }

    default void addToStock(EpiDTO epiDTO) {
        addToStock(epiDTO.getEpiName(), epiDTO.getNumCa(), epiDTO.getQuantity());
    }

    default Epi searchEpi(String epiName, Integer numCa) {
        EpiId id = new EpiId(epiName, numCa);
        return this.findById(id).orElse(new Epi(id, null));
    }
}
