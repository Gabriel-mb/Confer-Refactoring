package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.DTO.StockDTO;
import com.refactoring.conferUi.Model.Entity.Employee;
import com.refactoring.conferUi.Services.EmployeeService;
import com.refactoring.conferUi.Services.StockService;
import com.refactoring.conferUi.Utils.AlertUtils;
import com.refactoring.conferUi.Utils.NavigationUtils;
import io.github.palexdev.materialfx.controls.MFXButton;
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
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static com.refactoring.conferUi.Utils.AlertUtils.showErrorAlert;
import static java.lang.Integer.parseInt;

@Component
public class StockEquipCardController {

    @FXML
    private Label employeeId;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField newEmployeeId;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private MFXTableView<StockDTO> table;
    @FXML
    private MFXDatePicker datePicker;
    @FXML
    private MFXButton resetButton;

    private ObservableList<StockDTO> borrowingsList;
    private ObservableList<StockDTO> filteredItems;

    private final EmployeeService employeeService;
    private final StockService stockService;

    @Autowired
    public StockEquipCardController(EmployeeService employeeService, StockService stockService) {
        this.employeeService = employeeService;
        this.stockService = stockService;
    }

    @FXML
    private void initialize() {
        datePicker.setOnAction(event -> onDatePickerSelect());
        resetButton.setOnAction(event -> resetDatePicker());
        filteredItems = FXCollections.observableArrayList();
    }

    public void onSearchButtonClick() throws SQLException, IOException {
        if (newEmployeeId.getText().length() != 8) {
            showErrorAlert("Ocorreu um erro!", "Matrícula deve ter 8 digitos!");
            return;
        }

        if (employeeService.readId(parseInt(newEmployeeId.getText())) == null) {
            showErrorAlert("Nenhum funcionário encontrado!", "Tente Novamente!");
            return;
        }

        setTableEmployee(newEmployeeId.getText());
    }

    public void onMenuButtonClick(ActionEvent event) throws IOException {
        NavigationUtils.navigateTo(event, SearchController.class.getResource("/static/fxml/search-view.fxml"), null);
    }

    public void onDeleteButtonClick(ActionEvent event) throws IOException, SQLException {
        Optional<ButtonType> result = AlertUtils.showWarningAlert("Tem certeza que deseja continuar?", "Esta ação não pode ser desfeita.");

        if (result.get() == ButtonType.YES) {
            employeeService.delete(parseInt(employeeId.getText()));
            AlertUtils.showInfoAlert("Ficha Deletada", "A ficha foi deletada com sucesso!");
            NavigationUtils.navigateTo(event, SearchController.class.getResource("/static/fxml/search-view.fxml"), null);
        }
    }

    public void setTableEmployee(String id) throws SQLException, IOException {
        employeeId.setText(id);
        Employee employee = employeeService.readId(parseInt(employeeId.getText()));
        nameLabel.setText(employee.getName());

        borrowingsList = FXCollections.observableList(stockService.stockListBorrowed(Integer.valueOf(employeeId.getText())));

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
    }

    public void onModifyButtonClick(ActionEvent event) throws IOException, SQLException {
        NavigationUtils.navigateTo(event, StockEquipInputsController.class.getResource("/static/fxml/equipInputsModify-view.fxml"), controller -> {
            if (controller instanceof StockEquipInputsController stockEquipInputsController) {
                try {
                    stockEquipInputsController.setTable(employeeId.getText());
                    stockEquipInputsController.setEmployee(employeeId.getText(), nameLabel.getText());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    public void onPrintButtonClick() {
        try {
            ObservableList<StockDTO> filteredItems = table.getTransformableList();
            JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(filteredItems);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("CollectionBeanParam", itemsJRBean);
            parameters.put("employeeName", nameLabel.getText());
            parameters.put("employeeId", Integer.parseInt(employeeId.getText()));
            parameters.put("image", ClassLoader.getSystemResourceAsStream("assets/LogoCorel.png"));

            InputStream inputStream = getClass().getResourceAsStream("/static/jrxml/StockCardPrint.jrxml");
            JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            File pdf = File.createTempFile("stock_card_", ".pdf");
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdf.getPath());
            pdf.deleteOnExit();

            Runtime.getRuntime().exec("cmd /c start \"\" \"" + pdf.getAbsolutePath() + "\"");

        } catch (NumberFormatException e) {
            AlertUtils.showErrorAlert("ID inválido", "O ID do funcionário deve ser um número.");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Erro ao gerar relatório", e.getMessage());
        }
    }

    public void onBackButtonClick(ActionEvent event) throws IOException, SQLException {
        NavigationUtils.navigateTo(event, CardController.class.getResource("/static/fxml/patCard-view.fxml"), controller -> {
            if (controller instanceof CardController cardController) {
                try {
                    cardController.setTableEmployee(employeeId.getText());
                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void onDatePickerSelect() {
        LocalDate selectedDate = datePicker.getValue();
        filteredItems.clear();

        for (StockDTO item : table.getItems()) {
            LocalDate itemDate = item.getDate().toLocalDate();
            if (itemDate.equals(selectedDate)) {
                filteredItems.add(item);
            }
        }

        table.setItems(filteredItems);
    }

    public void resetDatePicker() {
        table.setItems(FXCollections.observableList(stockService.stockListBorrowed(Integer.valueOf(employeeId.getText()))));
    }

}