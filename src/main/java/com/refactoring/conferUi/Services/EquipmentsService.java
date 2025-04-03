package com.refactoring.conferUi.Services;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.refactoring.conferUi.Model.DTO.EquipmentDTO;
import com.refactoring.conferUi.Model.Entity.Equipment;
import com.refactoring.conferUi.dao.EquipmentRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EquipmentsService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    public EquipmentsService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public void create(Equipment equipment) throws SQLException {
       equipmentRepository.save(equipment);
    }

    public ObservableList<EquipmentDTO> listEquipmentsStatus() throws SQLException {
        return FXCollections.observableArrayList(equipmentRepository.findAllEquipmentWithStatus());
    }

    public List<EquipmentDTO> readId(Integer id) {
        return equipmentRepository.findEquipmentWithSupplierById(id);
    }

    public void delete(Integer idEquip, Integer supplierId) throws SQLException {
        equipmentRepository.deleteByIdEquipmentAndSupplierId(idEquip, supplierId);
    }

    public Boolean searchEquipment(Integer id, Integer supplierId) {
        Optional<Equipment> result = equipmentRepository.findByIdEquipmentAndSupplierId(id, supplierId);

        return result.isPresent();
    }

}
