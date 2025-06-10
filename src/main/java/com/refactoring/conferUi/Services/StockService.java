package com.refactoring.conferUi.Services;

import com.refactoring.conferUi.Model.DTO.StockDTO;
import com.refactoring.conferUi.Model.Entity.*;
import com.refactoring.conferUi.dao.StockBorrowedRepository;
import com.refactoring.conferUi.dao.StockRepository;
import com.refactoring.conferUi.dao.SupplierRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StockService {

    private final StockRepository stockRepository;
    private final StockBorrowedRepository stockBorrowedRepository;
    private final SupplierRepository supplierRepository;

    @Autowired
    public StockService(StockRepository stockRepository, StockBorrowedRepository stockBorrowedRepository, SupplierRepository supplierRepository) {
        this.stockRepository = stockRepository;
        this.stockBorrowedRepository = stockBorrowedRepository;
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public void updateOrCreateStock(String equipmentName, Integer supplierId, Integer quantityChange) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier não encontrado"));

        Optional<Stock> existingStock = Optional.ofNullable(stockRepository.findByEquipmentNameAndSupplierId(equipmentName, supplierId));

        if (existingStock.isPresent()) {
            Stock stock = existingStock.get();
            stock.setQuantity(stock.getQuantity() + quantityChange);
        } else {
            Stock newStock = new Stock();
            StockId stockId = new StockId(equipmentName, supplierId);
            newStock.setStockId(stockId);
            newStock.setQuantity(quantityChange);
            newStock.setSupplier(supplier); // Associa o supplier carregado
            stockRepository.save(newStock);
        }
    }

    public List<String> getEquipmentNames() {
        return stockRepository.findAllEquipmentNames();
    }

    public List<StockDTO> findAllAsDTO() {
        List<Stock> stocks = stockRepository.findAll();
        return stocks.stream()
                .map(stock -> new StockDTO(
                        stock.getStockId().getEquipmentName(),
                        stock.getSupplier().getSupplierName(),
                        stock.getQuantity()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean removeStock(String equipmentName, int supplierId, int quantityToRemove) {
        Optional<Stock> stockOpt = Optional.ofNullable(stockRepository.findByEquipmentNameAndSupplierId(equipmentName, supplierId));

        if (stockOpt.isEmpty()) {
            return false;
        }

        Stock stock = stockOpt.get();
        int newQuantity = stock.getQuantity() - quantityToRemove;

        if (newQuantity < 0) {
            return false;
        }

        stock.setQuantity(newQuantity);
        stockRepository.save(stock);
        return true;
    }

    public ObservableList<StockDTO> stockListBorrowed(Integer employeeId) {
        return stockBorrowedRepository.findByEmployee_IdEmployee(employeeId).stream()
                .map(borrowed -> {
                    Stock stock = stockRepository.findById(borrowed.getStockId())
                            .orElseThrow(() -> new RuntimeException("Stock not found"));

                    return new StockDTO(
                            borrowed.getStockId().getEquipmentName(),
                            stock.getSupplier().getSupplierName(),
                            borrowed.getQuantity(),
                            borrowed.getDate()
                    );
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    @Transactional
    public boolean removeBorrowed(StockDTO borrowedItem, int quantityToRemove, Integer employeeId) {
        try {
            Integer supplierId = supplierRepository.findSupplierIdBySupplierName(borrowedItem.getSupplierName());
            if (supplierId == null) return false;

            updateOrCreateStock(
                    borrowedItem.getEquipmentName(),
                    supplierId,
                    quantityToRemove
            );

            StockId stockId = new StockId(borrowedItem.getEquipmentName(), supplierId);
            StockBorrowed borrowed = stockBorrowedRepository.findByStockIdAndEmployeeIdAndDate(
                    stockId,
                    employeeId,
                    borrowedItem.getDate()
            );

            if (borrowed != null) {
                if (borrowed.getQuantity() > quantityToRemove) {
                    borrowed.setQuantity(borrowed.getQuantity() - quantityToRemove);
                    stockBorrowedRepository.save(borrowed);
                } else {
                    stockBorrowedRepository.delete(borrowed);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean processEquipmentBorrow(Integer employeeId, String equipmentName,
                                          String supplierName, Integer quantity, Date date) {
        try {
            Integer supplierId = supplierRepository.findSupplierIdBySupplierName(supplierName);
            if (supplierId == null) {
                throw new RuntimeException("Fornecedor não encontrado: " + supplierName);
            }

            Stock stock = stockRepository.findByEquipmentNameAndSupplierId(equipmentName, supplierId);
            if (stock == null || stock.getQuantity() < quantity) {
                throw new RuntimeException("Estoque insuficiente ou equipamento não encontrado");
            }

            StockId stockId = new StockId(equipmentName, supplierId);
            StockBorrowed existingBorrow = stockBorrowedRepository.findByStockIdAndEmployeeIdAndDate(
                    stockId,
                    employeeId,
                    date
            );

            if (existingBorrow != null) {
                existingBorrow.setQuantity(existingBorrow.getQuantity() + quantity);
                stockBorrowedRepository.save(existingBorrow);
            } else {
                StockBorrowed newBorrow = new StockBorrowed();
                newBorrow.setStockId(new StockId(equipmentName, supplierId));
                newBorrow.setQuantity(quantity);
                newBorrow.setDate(date);
                newBorrow.setEmployee(new Employee(employeeId));
                stockBorrowedRepository.save(newBorrow);
            }

            stock.setQuantity(stock.getQuantity() - quantity);
            stockRepository.save(stock);

            return true;

        } catch (Exception e) {
            throw new RuntimeException("Falha ao processar empréstimo: " + e.getMessage(), e);
        }
    }
}