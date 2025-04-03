package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.Entity.Stock;
import com.refactoring.conferUi.Model.Entity.Supplier;
import com.refactoring.conferUi.Services.StockService;
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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Integer.parseInt;

@Component
public class StockController {
    /*
    @Autowired
    private StockService stockService;
    @FXML
    private AnchorPane anchorPane;
    private final double[] coordinates = new double[2];
    @FXML
    MFXTableView<Stock> table;
    @FXML
    private ComboBox<Supplier> supplierDropDown;
    @FXML
    private MFXTextField quantity;
    @FXML
    private MFXFilterComboBox<Stock> equipmentDropDown;
    @FXML
    private MFXButton minimizeButton;

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

        setEquipmentDropDown();
        setTableEquipments();
        setSupplierDropDown();
    }

    @FXML
    private void handleMouseEvents(MouseEvent event) {
        NavigationUtils.handleAnchorPaneDrag(event, anchorPane, coordinates);
    }

    public void onMenuButtonClick(ActionEvent event) throws IOException {
        NavigationUtils.navigateTo(event, SearchController.class.getResource("/static/fxml/search-view.fxml"), null);
    }

    public void setTableEquipments() throws SQLException, IOException {
        ObservableList<Stock> stockList = FXCollections.observableArrayList(stockService.listStock()).sorted(Comparator.comparing(Stock::getEquipmentName));

        MFXTableColumn<Stock> equipmentName = new MFXTableColumn<>("Ferramenta", Comparator.comparing(Stock::getEquipmentName));
        MFXTableColumn<Stock> quantity = new MFXTableColumn<>("Quantidade", Comparator.comparing(Stock::getQuantity));
        MFXTableColumn<Stock> supplierName = new MFXTableColumn<>("Fornecedor", Comparator.comparing(Stock::getSupplierName));

        equipmentName.setRowCellFactory(Stock -> new MFXTableRowCell<>(com.refactoring.conferUi.Model.Entity.Stock::getEquipmentName));
        quantity.setRowCellFactory(Stock -> new MFXTableRowCell<>(com.refactoring.conferUi.Model.Entity.Stock::getQuantity));
        supplierName.setRowCellFactory(Stock -> new MFXTableRowCell<>(com.refactoring.conferUi.Model.Entity.Stock::getSupplierName));

        equipmentName.setPrefWidth(300);
        quantity.setPrefWidth(150);
        supplierName.setPrefWidth(300);

        table.getTableColumns().addAll(equipmentName, quantity, supplierName);
        table.getFilters().addAll(
                new StringFilter<>("Ferramenta", Stock::getEquipmentName),
                new IntegerFilter<>("Quantidade", Stock::getQuantity),
                new StringFilter<>("Fornecedor", Stock::getSupplierName)
        );
        table.setItems(stockList);
    }


    public void setSupplierDropDown() throws SQLException {
        supplierDropDown.setItems(FXCollections.observableList(stockService.selectSupplier()));
    }

    public void setEquipmentDropDown() throws SQLException {
        ObservableList<Stock> stockNames = FXCollections.observableList(stockService.selectNames());
        HashSet<String> uniqueEquipmentNames = new HashSet<>();
        ObservableList<Stock> uniqueEquipmentsNames = FXCollections.observableArrayList();

        for (Stock stock : stockNames) {
            String equipmentName = stock.getEquipmentName();
            if (!uniqueEquipmentNames.contains(equipmentName)) {
                uniqueEquipmentNames.add(equipmentName);
                uniqueEquipmentsNames.add(stock);
            }
        }
        equipmentDropDown.setItems(uniqueEquipmentsNames.sorted(Comparator.comparing(Stock::getEquipmentName)));
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
        try {
            stockService.createOrUpdateStock(parseInt(quantity.getText()), equipmentDropDown.getText(), String.valueOf(supplierDropDown.getValue()));
            table.setItems(FXCollections.observableArrayList(stockService.listStock()).sorted(Comparator.comparing(Stock::getEquipmentName)));
            AlertUtils.showInfoAlert("Sucesso", "Equipamento inserido com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                    MultipleSelectionModel<Stock> selectionModel = (MultipleSelectionModel<Stock>) table.getSelectionModel();
                    List<Stock> selectedItems = selectionModel.getSelectedValues();

                    for (Stock item : selectedItems) {
                        stockService.decreaseOrDeleteStock(Integer.parseInt(inputText), item.getEquipmentName(), item.getSupplierName());
                    }
                    AlertUtils.showInfoAlert("Estoque Alterado!", "Estoque alterado com sucesso!");
                    table.setItems(FXCollections.observableArrayList(stockService.listStock()).sorted(Comparator.comparing(Stock::getEquipmentName)));
                } catch (NumberFormatException e) {
                    AlertUtils.showErrorAlert("Erro", "A quantidade inserida não é válida.");
                }
            }
        }
    }
     */
}