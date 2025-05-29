package com.refactoring.conferUi.Services;

import com.refactoring.conferUi.Model.DTO.BorrowedDTO;
import com.refactoring.conferUi.Model.Entity.EpiBorrowed;
import com.refactoring.conferUi.Model.Entity.EquipmentBorrowed;
import com.refactoring.conferUi.Model.Entity.StockBorrowed;
import com.refactoring.conferUi.dao.BorrowedRepository;
import com.refactoring.conferUi.dao.EpiBorrowedRepository;
import com.refactoring.conferUi.dao.StockBorrowedRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowedService {

    private final BorrowedRepository borrowedRepository;
    private final EpiBorrowedRepository epiBorrowedRepository;
    private final StockBorrowedRepository stockBorrowedRepository;

    @Autowired
    public BorrowedService(
            BorrowedRepository borrowedRepository,
            EpiBorrowedRepository epiBorrowedRepository,
            StockBorrowedRepository stockBorrowedRepository
    ) {
        this.borrowedRepository = borrowedRepository;
        this.epiBorrowedRepository = epiBorrowedRepository;
        this.stockBorrowedRepository = stockBorrowedRepository;
    }

    public void create(EquipmentBorrowed equipmentBorrowed) throws SQLException {
        borrowedRepository.save(equipmentBorrowed);
    }

    public void delete(Integer idEquip, Integer supplierId) throws SQLException {
        borrowedRepository.deleteByEquipmentIdAndSupplierId(idEquip, supplierId);
    }

    public EquipmentBorrowed read(Integer idEquipment, Integer supplierId) {
        Optional<EquipmentBorrowed> result = borrowedRepository.findByIdEquipmentAndSupplierId(idEquipment, supplierId);
        if (result.isPresent()) {
            return result.get();
        }
        throw new RuntimeException();
    }

    public List<BorrowedDTO> listBorrowed(Integer employeeId) {
        return borrowedRepository.findBorrowedDetailsByEmployee(employeeId);
    }

    public Boolean searchBorrowed(Integer idEquipment, Integer supplierId) {
        Optional<EquipmentBorrowed> result = borrowedRepository.findByIdEquipmentAndSupplierId(idEquipment, supplierId);

        return result.isPresent();
    }

    public ObservableList<EpiBorrowed> episListBorrowed(Integer id) {
        return FXCollections.observableArrayList(epiBorrowedRepository.findByIdEmployee(id));
    }

    public void updateBorrowedEpi(Integer quantity, Integer numCa, Integer employeeId) {
        epiBorrowedRepository.updateQuantityByNumCaAndIdEmployee(quantity, numCa, employeeId);
    }

    public void create(EpiBorrowed epiBorrowed) {
        epiBorrowedRepository.save(epiBorrowed);
    }

    public boolean checkDate(String epiName, Integer numCa, Date date) {
        EpiBorrowed result = epiBorrowedRepository.findByEpiNameNumCaAndDate(epiName, numCa, date);
        return result != null;
    }

    public void create(StockBorrowed stockBorrowed) {
        stockBorrowedRepository.save(stockBorrowed);
    }

    /*public Boolean searchBorrowed(StockBorrowed stockBorrowed) {
       return stockBorrowedRepository.existsByEquipmentNameAndSupplierId(stockBorrowed.getStockId().getEquipmentName(), stockBorrowed.getStockId().getSupplierId());
    }

    public List<EquipmentBorrowed> stockListBorrowed(Integer id) throws SQLException {
        String sql = "SELECT stockBorrowed.equipmentName, stockBorrowed.quantity, stockBorrowed.date, sup.name " +
                "FROM stockBorrowed " +
                "INNER JOIN supplier sup ON stockBorrowed.supplierId = sup.supplierId " +
                "WHERE stockBorrowed.employeeId = ?";


        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);
        pstm.execute();
        ResultSet rst = pstm.getResultSet();
        List<EquipmentBorrowed> borrowings = new ArrayList<>();

        while (rst.next()) {
            String name = rst.getString(1);
            Integer quantity = rst.getInt(2);
            Date date = rst.getDate(3);
            String supplierName = rst.getString(4);

            borrowings.add(new EquipmentBorrowed(id, name, date, supplierName, quantity));
        }
        pstm.close();
        rst.close();

        return borrowings;
    }

    public boolean checkDate(EquipmentBorrowed newItem) throws SQLException {
        String query = "SELECT * FROM stockBorrowed WHERE equipmentName = ? AND supplierId = ? AND DATE(date) = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newItem.getEquipmentName());
            stmt.setInt(2, getSupplierId(newItem.getSupplierName()));

            // Ajuste para considerar apenas a parte da data
            java.sql.Date sqlDate = new java.sql.Date(newItem.getDate().getTime());
            stmt.setDate(3, sqlDate);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void updateBorrowedStock(Integer quantity, String equipmentName, Integer supplierId, Date date, Integer employeeId) throws SQLException {
        String updateQuery = "UPDATE stockBorrowed SET quantity = ? WHERE equipmentName = ? AND supplierId = ? AND date = ? AND employeeId = ?";

        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setInt(1, quantity);
            updateStatement.setString(2, equipmentName);
            updateStatement.setInt(3, supplierId);
            updateStatement.setDate(4, date);
            updateStatement.setInt(5, employeeId);
            updateStatement.executeUpdate();
        }
    }

    public ObservableList<EquipmentBorrowed> employeeListBorrowed(Integer id) throws SQLException {
        String sql = "SELECT stockBorrowed.equipmentName, stockBorrowed.quantity, stockBorrowed.date, sup.name " +
                "FROM stockBorrowed " +
                "INNER JOIN supplier sup ON stockBorrowed.supplierId = sup.supplierId " +
                "WHERE stockBorrowed.employeeId = ?";


        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);
        pstm.execute();
        ResultSet rst = pstm.getResultSet();
        ObservableList<EquipmentBorrowed> borrowings = FXCollections.observableArrayList();

        while (rst.next()) {
            String name = rst.getString(1);
            Integer quantity = rst.getInt(2);
            Date date = rst.getDate(3);
            String supplierName = rst.getString(4);

            borrowings.add(new EquipmentBorrowed(id, name, date, supplierName, quantity));
        }
        pstm.close();
        rst.close();

        return borrowings;
    }*/
}
