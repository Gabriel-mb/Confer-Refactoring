package com.refactoring.conferUi.Services;

import com.refactoring.conferUi.Model.Entity.EquipmentBorrowed;
import com.refactoring.conferUi.Model.Entity.Stock;
import com.refactoring.conferUi.dao.StockRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.*;
import java.util.List;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public Stock readByNameAndSupplier(String equipmentName, Integer supplierId) {
        return stockRepository.findByNameEquipAndSupplierId(equipmentName, supplierId);
    }

    public void updateStock(String equipmentName, Integer supplierId, Integer quantity) throws IOException {
        stockRepository.updateByNameEquipAndSupplierId(quantity, equipmentName, supplierId);
    }

    public List<Stock> selectStock() throws SQLException {
        return stockRepository.findAll();
    }

    public List<String> selectNames() throws SQLException {
        return stockRepository.findByNameEquip();
    }
}