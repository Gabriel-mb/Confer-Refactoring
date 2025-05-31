package com.refactoring.conferUi.Services;

import com.refactoring.conferUi.Model.DTO.StockDTO;
import com.refactoring.conferUi.Model.Entity.Stock;
import com.refactoring.conferUi.Model.Entity.StockId;
import com.refactoring.conferUi.Model.Entity.Supplier;
import com.refactoring.conferUi.dao.StockRepository;
import com.refactoring.conferUi.dao.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StockService {

    private final StockRepository stockRepository;
    private final SupplierRepository supplierRepository;

    @Autowired
    public StockService(StockRepository stockRepository, SupplierRepository supplierRepository) {
        this.stockRepository = stockRepository;
        this.supplierRepository = supplierRepository;
    }

    public List<Stock> findAll() {
       return stockRepository.findAll();
    }

    public Stock getStock(String equipmentName, Integer supplierId) throws ChangeSetPersister.NotFoundException {
        return stockRepository.findByEquipmentNameAndSupplierId(equipmentName, supplierId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
    }

    @Transactional
    public void updateOrCreateStock(String equipmentName, Integer supplierId, Integer quantityChange) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier não encontrado"));

        // Busca o stock existente
        Optional<Stock> existingStock = stockRepository.findByEquipmentNameAndSupplierId(equipmentName, supplierId);

        if (existingStock.isPresent()) {
            // Atualiza a quantidade existente
            Stock stock = existingStock.get();
            stock.setQuantity(stock.getQuantity() + quantityChange);
        } else {
            // Cria novo registro
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
                        stock.getSupplier().getSupplierName(), // This will work if fetched in same transaction
                        stock.getQuantity()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean removeStock(String equipmentName, int supplierId, int quantityToRemove) {
        Optional<Stock> stockOpt = stockRepository.findByEquipmentNameAndSupplierId(equipmentName, supplierId);

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
}