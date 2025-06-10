package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.DTO.StockDTO;
import com.refactoring.conferUi.Model.Entity.Stock;
import com.refactoring.conferUi.Services.StockService;
import com.refactoring.conferUi.Services.SupplierService;
import com.refactoring.conferUi.Utils.AlertUtils;
import com.refactoring.conferUi.Utils.NavigationUtils;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.selection.MultipleSelectionModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

@Component
public class StockController {

    @FXML
    MFXTableView<StockDTO> table;
    @FXML
    private ComboBox<String> supplierDropDown;
    @FXML
    private MFXTextField quantity;
    @FXML
    private MFXFilterComboBox<String> equipmentDropDown;

    private final SupplierService supplierService;
    private final StockService stockService;

    @Autowired
    public StockController(SupplierService supplierService, StockService stockService) {
        this.supplierService = supplierService;
        this.stockService = stockService;
    }

    @FXML
    private void initialize() throws SQLException, IOException {
        setTableEquipments();
        setEquipmentDropDown();
        setSupplierDropDown();
    }

    public void onMenuButtonClick(ActionEvent event) throws IOException {
        NavigationUtils.navigateTo(event, SearchController.class.getResource("/static/fxml/search-view.fxml"), null);
    }

    @FXML
    public void setTableEquipments() {
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


    @FXML
    public void setSupplierDropDown() throws SQLException {
        supplierDropDown.setItems(FXCollections.observableList(supplierService.selectSupplier()));
    }

    @FXML
    public void setEquipmentDropDown() {
        ObservableList<String> uniqueEquipmentNames = FXCollections.observableArrayList(
                new HashSet<>(stockService.getEquipmentNames())
        ).sorted();

        equipmentDropDown.setItems(uniqueEquipmentNames);
    }

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

    public void onRemoveButtonClick() {
        Optional<ButtonType> result = AlertUtils.showWarningAlert("Tem certeza que deseja continuar?", "Esta ação não pode ser desfeita.");

        if (result.isPresent() && result.get() == ButtonType.YES) {
            TextInputDialog inputDialog = new TextInputDialog();
            inputDialog.setTitle("Quantidade a ser removida");
            inputDialog.setContentText("Digite a quantidade a ser removida:");
            Optional<String> quantityResult = inputDialog.showAndWait();

            if (quantityResult.isPresent() && !quantityResult.get().isEmpty()) {
                try {
                    int quantityToRemove = parseInt(quantityResult.get());
                    if (quantityToRemove <= 0) {
                        AlertUtils.showErrorAlert("Erro", "A quantidade deve ser maior que zero.");
                        return;
                    }

                    MultipleSelectionModel<StockDTO> selectionModel = (MultipleSelectionModel<StockDTO>) table.getSelectionModel();
                    List<StockDTO> selectedItems = selectionModel.getSelectedValues();

                    for (StockDTO item : selectedItems) {
                        int supplierId = supplierService.findIdByName(item.getSupplierName());
                        boolean success = stockService.removeStock(
                                item.getEquipmentName(),
                                supplierId,
                                quantityToRemove
                        );

                        if (!success) {
                            AlertUtils.showErrorAlert("Erro",
                                    "Não foi possível remover a quantidade desejada de " + item.getEquipmentName() +
                                            "\nVerifique se há estoque suficiente.");
                        }
                    }

                    table.setItems(dtolistBuilder());
                    AlertUtils.showInfoAlert("Sucesso", "Operação de remoção concluída!");

                } catch (NumberFormatException e) {
                    AlertUtils.showErrorAlert("Erro", "Quantidade inválida. Digite um número válido.");
                } catch (Exception e) {
                    AlertUtils.showErrorAlert("Erro", "Ocorreu um erro inesperado: " + e.getMessage());
                }
            }
        }
    }

    private ObservableList<StockDTO> dtolistBuilder() {
        return FXCollections.observableArrayList(
                stockService.findAllAsDTO().stream()
                        .sorted(Comparator.comparing(StockDTO::getEquipmentName))
                        .collect(Collectors.toList())
        );
    }
}