package com.refactoring.conferUi.Services;

import com.refactoring.conferUi.Model.DTO.EpiDTO;
import com.refactoring.conferUi.Model.Entity.Epi;
import com.refactoring.conferUi.dao.EpiRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EpiService {

    @Autowired
    private EpiRepository epiRepository;

    public EpiService(EpiRepository epiRepository) {
        this.epiRepository = epiRepository;
    }

    public void update(EpiDTO epiDTO) {
        if (epiDTO.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        epiRepository.addToStock(epiDTO.getEpiName(), epiDTO.getNumCa(), epiDTO.getQuantity());
    }

    public void remove(String epiName, Integer numCa, Integer quantity) {
        epiRepository.decreaseStock(epiName, numCa, quantity);
    }


    public ObservableList<EpiDTO> listStock() {
        List<Epi> epiList = epiRepository.findAll();
        List<EpiDTO> epiDTOList = epiList.stream()
                .map(epi -> new EpiDTO(
                        epi.getEpiId().getEpiName(),
                        epi.getEpiId().getNumCa(),
                        epi.getQuantity()
                ))
                .collect(Collectors.toList());

        return FXCollections.observableArrayList(epiDTOList);
    }
}