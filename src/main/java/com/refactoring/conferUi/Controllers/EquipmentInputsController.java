package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.DTO.BorrowedDTO;
import com.refactoring.conferUi.Model.DTO.EquipmentDTO;
import com.refactoring.conferUi.Services.BorrowedService;
import com.refactoring.conferUi.Services.EquipmentsService;
import com.refactoring.conferUi.Services.SupplierService;
import com.refactoring.conferUi.Utils.AlertUtils;
import com.refactoring.conferUi.Utils.NavigationUtils;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.scene.input.MouseEvent;

import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static java.lang.Integer.parseInt;

@Component
public class EquipmentInputsController {
    @FXML
    private Label nameLabel;
    @FXML
    private Label idLabel;
    @FXML
    private MFXDatePicker date;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField equipmentIdInput;
    @FXML
    private ComboBox<String> equipmentName;
    @FXML
    private MFXTableView<BorrowedDTO> table;
    ObservableList<BorrowedDTO> borrowingsList = FXCollections.observableArrayList();
    private final double[] coordinates = new double[2];
    private String equipName;
    private String supplierName;

    private final EquipmentsService equipmentsService;
    private final BorrowedService borrowedService;
    private final SupplierService supplierService;

    @Autowired
    public EquipmentInputsController(EquipmentsService equipmentsService, BorrowedService borrowedService, SupplierService supplierService) {
        this.equipmentsService = equipmentsService;
        this.borrowedService = borrowedService;
        this.supplierService = supplierService;
    }

    @FXML
    private void initialize() {
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                node.setFocusTraversable(false);
            }
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Supplier<StringConverter<LocalDate>> converterSupplier = () -> new LocalDateStringConverter(dateFormatter, null);
        date.setConverterSupplier(converterSupplier);
    }

    public void onSearchButtonClick() throws SQLException, IOException {
        if (Objects.equals(equipmentIdInput.getText(), "")) {
            AlertUtils.showErrorAlert("Ocorreu um erro", "Por favor insira uma matrícula adequada!");
            return;
        }
        List<EquipmentDTO> equipmentList = equipmentsService.readId(parseInt(equipmentIdInput.getText()));
        if (equipmentList.isEmpty()) {
            AlertUtils.showErrorAlert("Ocorreu um erro", "Nenhum equipamento encontrado!");
            return;
        } else {
            equipmentName.getItems().clear();
            for (EquipmentDTO i : equipmentList) {
                equipmentName.getItems().addAll(i.getSupplierName() + " - " + i.getNameEquip());
            }
        }
        equipmentName.getSelectionModel().selectFirst();
    }

    public void onIncludeButtonClick() {
        try {
            splitSelection();

            int equipmentId = parseInt(equipmentIdInput.getText());
            int supplierId = supplierService.findIdByName(supplierName);
            Date borrowDate = Date.valueOf(date.getValue());


            if (borrowingsList.stream().anyMatch(dto ->
                    dto.getIdEquipment() == equipmentId &&
                            dto.getSupplierName().equals(supplierName)) ||
                    borrowedService.searchBorrowed(equipmentId, supplierId)) {
                AlertUtils.showErrorAlert("Erro", "Ferramenta já cadastrada!");
                return;
            }

            BorrowedDTO newItem = new BorrowedDTO(equipName, equipmentId, borrowDate, supplierName);
            borrowingsList.add(newItem);
            borrowedService.create(new BorrowedDTO(equipmentId, parseInt(idLabel.getText()), supplierId, borrowDate));

            table.setItems(borrowingsList); // Atualiza a visualização
            clearForm();

        } catch (Exception e) {
            AlertUtils.showErrorAlert("Erro", e.getMessage());
        }
    }

    private void clearForm() {
        equipmentName.getItems().clear();
        equipmentIdInput.clear();
        date.setValue(null);
    }

    private void splitSelection() {
        String selection = equipmentName.getValue();
        String[] sections = selection.split(" - ");

        supplierName = sections[0];
        equipName = sections[1];
    }

    public void removeData(Integer idEquip, String supplierName) throws SQLException {
        if (borrowedService.read(idEquip, supplierService.findIdByName(supplierName)) != null)
            borrowedService.delete(idEquip, supplierService.findIdByName(supplierName));
    }

    public void setEmployee(String id, String name) {
        idLabel.setText(id);
        nameLabel.setText(name);
    }

    public void setTable(ObservableList<BorrowedDTO> list, Boolean confirm) {
        table.getTableColumns().clear();
        table.getItems().clear();

        MFXTableColumn<BorrowedDTO> idEquipment = new MFXTableColumn<>("Patrimônio",
                Comparator.comparing(BorrowedDTO::getIdEquipment));

        MFXTableColumn<BorrowedDTO> supplierName = new MFXTableColumn<>("Fornecedor",
                Comparator.comparing(BorrowedDTO::getSupplierName));

        MFXTableColumn<BorrowedDTO> equipmentName = new MFXTableColumn<>("Ferramenta",
                Comparator.comparing(BorrowedDTO::getEquipmentName));

        MFXTableColumn<BorrowedDTO> date = new MFXTableColumn<>("Data",
                Comparator.comparing(BorrowedDTO::getDate));

        idEquipment.setRowCellFactory(dto -> new MFXTableRowCell<>(BorrowedDTO::getIdEquipment));
        supplierName.setRowCellFactory(dto -> new MFXTableRowCell<>(BorrowedDTO::getSupplierName));
        equipmentName.setRowCellFactory(dto -> new MFXTableRowCell<>(BorrowedDTO::getEquipmentName));
        date.setRowCellFactory(dto -> new MFXTableRowCell<>(BorrowedDTO::getDate));

        table.getFilters().addAll(
                new StringFilter<>("Ferramenta", BorrowedDTO::getEquipmentName),
                new IntegerFilter<>("Patrimônio", BorrowedDTO::getIdEquipment),
                new StringFilter<>("Fornecedor", BorrowedDTO::getSupplierName)
        );

        equipmentName.setPrefWidth(350);
        table.getTableColumns().addAll(idEquipment, supplierName, equipmentName, date);
        table.setItems(list);
    }

    @FXML
    private void handleMouseEvents(MouseEvent event) {
        NavigationUtils.handleAnchorPaneDrag(event, anchorPane, coordinates);
    }

    public void onMenuButtonClick(ActionEvent event) throws IOException {
        NavigationUtils.navigateTo(event, SearchController.class.getResource("/static/fxml/search-view.fxml"), null);
    }

    public void onDevolutionClick(ActionEvent event) {
        /// //////////////////////
    }

    public void onBackButtonClick(ActionEvent event) throws IOException, SQLException {
        NavigationUtils.navigateTo(event, CardController.class.getResource("/static/fxml/patCard-view.fxml"), controller -> {
            if (controller instanceof CardController cardController) {
                try {
                    cardController.setTableEmployee(idLabel.getText());
                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}