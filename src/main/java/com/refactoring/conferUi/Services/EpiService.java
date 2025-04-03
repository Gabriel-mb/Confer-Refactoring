package com.refactoring.conferUi.Services;

import com.refactoring.conferUi.Model.Entity.Epi;
import com.refactoring.conferUi.dao.EpiRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class EpiService {

    @Autowired
    private EpiRepository epiRepository;

    public EpiService(EpiRepository epiRepository) {
        this.epiRepository = epiRepository;
    }

    public void update(Epi epi) throws IOException {
       epiRepository.save(epi);
    }


    public void remove(String epiName, Integer numCa, Date date) throws SQLException {
        epiRepository.devolverEpiParaEstoque(epiName, numCa, date);
    }


    public ObservableList<Epi> listStock() throws SQLException {
        return FXCollections.observableArrayList(epiRepository.findAll());
    }

    public Optional<Epi> searchEpi(Integer cA) {
       return epiRepository.findById(cA);
    }

    public ObservableList<Optional<Epi>> episList(Integer id) {
        return FXCollections.observableArrayList(epiRepository.findById(id));
    }
}