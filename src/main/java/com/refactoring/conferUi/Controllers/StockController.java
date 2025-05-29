package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.DTO.StockDTO;
import com.refactoring.conferUi.Model.Entity.Stock;
import com.refactoring.conferUi.Model.Entity.Supplier;
import com.refactoring.conferUi.Services.StockService;
import com.refactoring.conferUi.Services.SupplierService;
import com.refactoring.conferUi.Utils.AlertUtils;
import com.refactoring.conferUi.Utils.NavigationUtils;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.selection.MultipleSelectionModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

@Component
public class StockController {

    @FXML
    private AnchorPane anchorPane;
    private final double[] coordinates = new double[2];
    @FXML
    MFXTableView<StockDTO> table;
    @FXML
    private ComboBox<String> supplierDropDown;
    @FXML
    private MFXTextField quantity;
    @FXML
    private MFXFilterComboBox<String> equipmentDropDown;
    @FXML
    private MFXButton minimizeButton;

    private final SupplierService supplierService;
    private final StockService stockService;

    @Autowired
    public StockController(SupplierService supplierService, StockService stockService) {
        this.supplierService = supplierService;
        this.stockService = stockService;
    }

    @FXML
    private void initialize() throws SQLException, IOException {
        // percorre todos os nós da cena e define o foco como não transversável para os TextFields
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                node.setFocusTraversable(false);
            }
        }
        minimizeButton.setOnAction(e ->
                ((Stage) ((Button) e.getSource()).getScene().getWindow()).setIconified(true)
        );
        Platform.runLater(this::loadInitialData);
    }

    @FXML
    private void handleMouseEvents(MouseEvent event) {
        NavigationUtils.handleAnchorPaneDrag(event, anchorPane, coordinates);
    }

    public void onMenuButtonClick(ActionEvent event) throws IOException {
        NavigationUtils.navigateTo(event, SearchController.class.getResource("/static/fxml/search-view.fxml"), null);
    }

    public void setTableEquipments() throws SQLException, IOException {
        MFXTableColumn<StockDTO> equipmentName = new MFXTableColumn<>("Ferramenta", Comparator.comparing(StockDTO::getEquipmentName));
        MFXTableColumn<StockDTO> quantity = new MFXTableColumn<>("Quantidade", Comparator.comparing(StockDTO::getQuantity));
        MFXTableColumn<StockDTO> supplierName = new MFXTableColumn<>("Fornecedor", Comparator.comparing(StockDTO::getSupplierName));

        equipmentName.setRowCellFactory(stock -> new MFXTableRowCell<>(StockDTO::getEquipmentName));
        quantity.setRowCellFactory(stock -> new MFXTableRowCell<>(StockDTO::getQuantity));
        supplierName.setRowCellFactory(stock -> new MFXTableRowCell<>(StockDTO::getSupplierName));

        equipmentName.setPrefWidth(300);
        quantity.setPrefWidth(150);
        supplierName.setPrefWidth(300);

        table.getTableColumns().clear();
        table.getTableColumns().addAll(equipmentName, quantity, supplierName);

        table.getFilters().addAll(
                new StringFilter<>("Ferramenta", StockDTO::getEquipmentName),
                new IntegerFilter<>("Quantidade", StockDTO::getQuantity),
                new StringFilter<>("Fornecedor", StockDTO::getSupplierName)
        );

        table.setItems(dtolistBuilder());
    }


    public void setSupplierDropDown() throws SQLException {
        supplierDropDown.setItems(FXCollections.observableList(supplierService.selectSupplier()));
    }

    public void setEquipmentDropDown() throws SQLException {
        ObservableList<String> uniqueEquipmentNames = FXCollections.observableArrayList(
                new HashSet<>(stockService.getEquipmentNames())
        ).sorted();

        equipmentDropDown.setItems(uniqueEquipmentNames);
    }

    @Transactional
    public void onIncludeButtonClick() {
        if (Objects.equals(String.valueOf(equipmentDropDown.getValue()), "")) {
            AlertUtils.showErrorAlert("Ocorreu um erro", "Insira um nome para a ferramenta!");
            return;
        }
        if (Objects.equals(quantity.getText(), "")) {
            AlertUtils.showErrorAlert("Ocorreu um erro", "Insira uma quantidade valida!");
            return;
        }
        stockService.updateOrCreateStock(equipmentDropDown.getText(), supplierService.findIdByName(supplierDropDown.getValue()), parseInt(quantity.getText()));
        table.setItems(dtolistBuilder());
        AlertUtils.showInfoAlert("Sucesso", "Equipamento inserido com sucesso!");
    }

    public void onCloseButtonClick() {
        System.exit(0);
    }

    public void onRemoveButtonClick() throws SQLException, IOException {
        Optional<ButtonType> result = AlertUtils.showWarningAlert("Tem certeza que deseja continuar?", "Esta ação não pode ser desfeita.");

        if (result.isPresent() && result.get() == ButtonType.YES) {
            TextInputDialog inputDialog = new TextInputDialog();
            inputDialog.setTitle("Quantidade a ser removida");
            inputDialog.setContentText("Digite a quantidade a ser removida:");
            Optional<String> quantityResult = inputDialog.showAndWait();

            if (quantityResult.isPresent() && !quantityResult.get().isEmpty()) {
                String inputText = quantityResult.get();
                try {
                    MultipleSelectionModel<StockDTO> selectionModel = (MultipleSelectionModel<StockDTO>) table.getSelectionModel();
                    List<StockDTO> selectedItems = selectionModel.getSelectedValues();

                    for (StockDTO item : selectedItems) {
                        stockService.updateOrCreateStock(equipmentDropDown.getText(), supplierService.findIdByName(supplierDropDown.getValue()), parseInt(quantity.getText()));
                    }
                    AlertUtils.showInfoAlert("Estoque Alterado!", "Estoque alterado com sucesso!");
                    table.setItems(dtolistBuilder());
                } catch (NumberFormatException e) {
                    AlertUtils.showErrorAlert("Erro", "A quantidade inserida não é válida.");
                }
            }
        }
    }

    @Transactional(readOnly = true)
    private StockDTO convertToDTO(Stock stock) {
        return new StockDTO(
                stock.getStockId().getEquipmentName(), // Assuming Equipment is a related entity
                stock.getSupplier().getSupplierName(),               // Assuming Supplier has getName()
                stock.getQuantity()
        );
    }
    private ObservableList<StockDTO> dtolistBuilder() {
        List<Stock> stockEntities = stockService.findAll();
        return FXCollections.observableArrayList(
                stockEntities.stream()
                        .map(this::convertToDTO)
                        .sorted(Comparator.comparing(StockDTO::getEquipmentName))
                        .collect(Collectors.toList())
        );
    }

    private void loadInitialData() {
        try {
            supplierDropDown.getItems().setAll(supplierService.selectSupplier());

            List<String> equipments = stockService.getEquipmentNames();
            if (equipments != null) {
                equipmentDropDown.getItems().setAll(equipments);
                equipmentDropDown.getItems().sort(String::compareTo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            supplierDropDown.getItems().clear();
            equipmentDropDown.getItems().clear();
        }
    }

}