package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.DTO.EpiDTO;
import com.refactoring.conferUi.Model.DTO.StockDTO;
import com.refactoring.conferUi.Model.Entity.Employee;
import com.refactoring.conferUi.Model.Entity.Stock;
import com.refactoring.conferUi.Services.EmployeeService;
import com.refactoring.conferUi.Services.StockService;
import com.refactoring.conferUi.Services.SupplierService;
import com.refactoring.conferUi.Utils.AlertUtils;
import com.refactoring.conferUi.Utils.NavigationUtils;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.refactoring.conferUi.Utils.NavigationUtils.navigateTo;
import static java.lang.Integer.parseInt;

@Component
public class StockEquipInputsController {
    @FXML
    private Label nameLabel;
    @FXML
    private Label idLabel;
    @FXML
    private MFXDatePicker date;
    @FXML
    private TextField epiQuantity;
    @FXML
    private MFXFilterComboBox equipmentName;
    @FXML
    private MFXTableView<StockDTO> table;
    @FXML
    private ComboBox<String> supplierComboBox;
    @FXML
    private MFXDatePicker datePicker;
    @FXML
    private MFXButton resetButton;

    ObservableList<StockDTO> borrowingsList = FXCollections.observableArrayList();
    private ObservableList<StockDTO> filteredItems;

    private final StockService stockService;
    private final EmployeeService employeeService;
    private final SupplierService supplierService;

    @Autowired
    public StockEquipInputsController(StockService stockService, EmployeeService employeeService, SupplierService supplierService) {
        this.stockService = stockService;
        this.employeeService = employeeService;
        this.supplierService = supplierService;
    }

    public void onIncludeButtonClick() {
        try {
            if (equipmentName.getValue() == null || date.getValue() == null ||
                    supplierComboBox.getValue() == null || epiQuantity.getText().isEmpty()) {
                AlertUtils.showErrorAlert("Erro", "Preencha todos os campos obrigatórios!");
                return;
            }

            String eqName = equipmentName.getText();
            String supplierName = supplierComboBox.getValue();
            int quantity = Integer.parseInt(epiQuantity.getText());
            Date borrowDate = Date.valueOf(date.getValue());
            int employeeId = Integer.parseInt(idLabel.getText());

            boolean success = stockService.processEquipmentBorrow(
                    employeeId,
                    eqName,
                    supplierName,
                    quantity,
                    borrowDate
            );

            if (!success) {
                AlertUtils.showErrorAlert("Erro", "Não foi possível processar o empréstimo");
                return;
            }

            table.setItems(stockService.stockListBorrowed(Integer.valueOf(idLabel.getText())));

        } catch (NumberFormatException e) {
            AlertUtils.showErrorAlert("Erro", "Quantidade inválida!");
        } catch (RuntimeException e) {
            AlertUtils.showErrorAlert("Erro", e.getMessage());
        }
    }

    public void onRemoveButtonClick() {
        List<StockDTO> selectedItems = table.getSelectionModel().getSelectedValues();

        if (selectedItems == null || selectedItems.isEmpty()) {
            AlertUtils.showErrorAlert("Erro", "Nenhum item selecionado!");
            return;
        }

        Optional<Integer> quantityToRemove = showRemoveQuantityDialog(
                selectedItems.stream().mapToInt(StockDTO::getQuantity).min().orElse(0)
        );

        if (!quantityToRemove.isPresent()) return;

        try {
            boolean allRemoved = true;
            int employeeId = Integer.parseInt(idLabel.getText());

            for (StockDTO item : selectedItems) {
                if (!stockService.removeBorrowed(item, quantityToRemove.get(), employeeId)) {
                    allRemoved = false;
                    AlertUtils.showErrorAlert("Erro",
                            String.format("Falha ao devolver %s", item.getEquipmentName()));
                }
            }
            if (allRemoved) {
                table.setItems(stockService.stockListBorrowed(Integer.valueOf(idLabel.getText())));
                AlertUtils.showInfoAlert("Sucesso",
                        String.format("%d itens devolvidos ao estoque", selectedItems.size()));
            }

        } catch (Exception e) {
            AlertUtils.showErrorAlert("Erro", "Falha na operação: " + e.getMessage());
        }
    }

    private Optional<Integer> showRemoveQuantityDialog(int maxQuantity) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(maxQuantity));
        dialog.setTitle("Remover Quantidade");
        dialog.setHeaderText(String.format("Máximo: %d por item\nQuantidade a remover de cada:", maxQuantity));
        dialog.setContentText("Quantidade:");

        return dialog.showAndWait().map(input -> {
            try {
                int qty = Integer.parseInt(input);
                if (qty <= 0 || qty > maxQuantity) throw new NumberFormatException();
                return qty;
            } catch (NumberFormatException e) {
                AlertUtils.showErrorAlert("Erro",
                        String.format("Quantidade inválida! Deve ser entre 1 e %d", maxQuantity));
                return null;
            }
        });
    }

    public void setEmployee(String id, String name) {
        idLabel.setText(id);
        nameLabel.setText(name);
    }

    public void setTable(String id) throws SQLException {
        idLabel.setText(id);

        borrowingsList = FXCollections.observableList(stockService.stockListBorrowed(Integer.valueOf(idLabel.getText())));

        MFXTableColumn<StockDTO> equipmentName = new MFXTableColumn<>("Ferramenta", Comparator.comparing(StockDTO::getEquipmentName));
        MFXTableColumn<StockDTO> quantity = new MFXTableColumn<>("Quantidade", Comparator.comparing(StockDTO::getQuantity));
        MFXTableColumn<StockDTO> date = new MFXTableColumn<>("Data", Comparator.comparing(StockDTO::getDate));
        MFXTableColumn<StockDTO> supplierName = new MFXTableColumn<>("Fornecedor", Comparator.comparing(StockDTO::getSupplierName));

        equipmentName.setRowCellFactory(Stock -> new MFXTableRowCell<>(StockDTO::getEquipmentName));
        quantity.setRowCellFactory(Stock -> new MFXTableRowCell<>(StockDTO::getQuantity));
        date.setRowCellFactory(Stock -> new MFXTableRowCell<>(StockDTO::getDate));
        supplierName.setRowCellFactory(Stock -> new MFXTableRowCell<>(StockDTO::getSupplierName));

        table.getTableColumns().addAll(equipmentName, quantity, date, supplierName);
        table.getFilters().addAll(
                new StringFilter<>("Ferramenta", StockDTO::getEquipmentName),
                new IntegerFilter<>("Quantidade", StockDTO::getQuantity),
                new StringFilter<>("Fornecedor", StockDTO::getSupplierName)
        );

        equipmentName.setPrefWidth(350);
        table.setItems(borrowingsList);

        Employee employee = employeeService.readId(parseInt(idLabel.getText()));
        nameLabel.setText(employee.getName());
    }

    @FXML
    private void initialize() throws SQLException, IOException {
        setStockItemsDropDown();
        setSupplierDropDown();

        datePicker.setOnAction(event -> onDatePickerSelect());
        resetButton.setOnAction(event -> resetDatePicker());
        filteredItems = FXCollections.observableArrayList();
    }

    public void onMenuButtonClick(ActionEvent event) throws IOException {
        NavigationUtils.navigateTo(event, SearchController.class.getResource("/static/fxml/search-view.fxml"), null);
    }

    public void setStockItemsDropDown() {
        ObservableList<String> equipmentNames = FXCollections.observableList(stockService.getEquipmentNames());
        ObservableList<String> uniqueEquipmentNames = FXCollections.observableList(equipmentNames.stream().distinct().collect(Collectors.toList()));
        FXCollections.sort(uniqueEquipmentNames);
        equipmentName.setItems(uniqueEquipmentNames);
    }

    public void setSupplierDropDown() throws SQLException, IOException {
        ObservableList<String> suppliersNames = FXCollections.observableList(supplierService.selectSupplier());
        supplierComboBox.setItems(suppliersNames);
    }

    public void onBackButtonClick(ActionEvent event) throws IOException, SQLException {
        navigateTo(event, StockEquipCardController.class.getResource("/static/fxml/equipCard-view.fxml"), controller -> {
            if (controller instanceof StockEquipCardController stockEquipCardController) {
                try {
                    stockEquipCardController.setTableEmployee(idLabel.getText());
                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void onDatePickerSelect() {
        LocalDate selectedDate = datePicker.getValue();
        filteredItems.clear();

        for (StockDTO item : borrowingsList) {
            LocalDate itemDate = item.getDate().toLocalDate();
            if (itemDate.equals(selectedDate)) {
                filteredItems.add(item);
            }
        }

        table.setItems(filteredItems);
    }

    public void resetDatePicker() {
        table.setItems(borrowingsList);
    }
}