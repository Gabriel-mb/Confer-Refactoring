package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.DTO.BorrowedDTO;
import com.refactoring.conferUi.Model.Entity.Employee;
import com.refactoring.conferUi.Services.BorrowedService;
import com.refactoring.conferUi.Services.EmployeeService;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;


import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import static com.refactoring.conferUi.Utils.AlertUtils.showErrorAlert;
import static com.refactoring.conferUi.Utils.NavigationUtils.navigateTo;
import static java.lang.Integer.parseInt;

@Controller
public class CardController {
    @FXML
    private Label employeeId;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField newEmployeeId;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private MFXTableView<BorrowedDTO> table;
    @FXML
    private MFXDatePicker datePicker;
    @FXML
    private MFXButton resetButton;

    private ObservableList<BorrowedDTO> borrowingsList;
    private ObservableList<BorrowedDTO> filteredItems;

    private final double[] coordinates = new double[2];

    private final EmployeeService employeeService;
    private final BorrowedService borrowedService;

    @Autowired
    public CardController(EmployeeService employeeService, BorrowedService borrowedService) {
        this.employeeService = employeeService;
        this.borrowedService = borrowedService;
    }


    @FXML
    private void initialize() {
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                node.setFocusTraversable(false);
            }
        }
        newEmployeeId.setOnAction(event -> {
            try {
                Employee employee = employeeService.readId(parseInt(newEmployeeId.getText()));

                if (employee == null) {
                    showErrorAlert("Nenhum funcionário encontrado!", "Tente Novamente!");
                }

                navigateTo(event, CardController.class.getResource("/static/fxml/patCard-view.fxml"), controller -> {
                    if (controller instanceof CardController cardController) {
                        try {
                            cardController.setTableEmployee(employeeId.getText());
                        } catch (SQLException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });
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
        table.getTableColumns().clear();
        table.getItems().clear();
        employeeId.setText(id);

        List<BorrowedDTO> borrowedItems = borrowedService.listBorrowed(Integer.valueOf(employeeId.getText()));
        borrowingsList = FXCollections.observableArrayList(borrowedItems);

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
        table.setItems(borrowingsList);

        Employee employee = employeeService.readId(parseInt(employeeId.getText()));
        nameLabel.setText(employee.getName());
    }

    @FXML
    private void handleMouseEvents(MouseEvent event) {
        NavigationUtils.handleAnchorPaneDrag(event, anchorPane, coordinates);
    }

    public void onModifyButtonClick (ActionEvent event) throws IOException {
        navigateTo(event, EquipmentInputsController.class.getResource("/static/fxml/patInputsModify-view.fxml"), controller -> {
            if (controller instanceof EquipmentInputsController equipmentInputsController) {
                equipmentInputsController.setEmployee(employeeId.getText(), nameLabel.getText());
                equipmentInputsController.setTable(borrowingsList, true);
            }
        });
    }

    public void onPrintButtonClick() {
        try {
            // 1. Gerar o relatório
            ObservableList<BorrowedDTO> items = table.getTransformableList();
            Map<String, Object> params = new HashMap<>();
            params.put("CollectionBeanParam", new JRBeanCollectionDataSource(items));
            params.put("employeeName", nameLabel.getText());
            params.put("employeeId", parseInt(employeeId.getText()));
            params.put("image", ClassLoader.getSystemResourceAsStream("assets/LogoCorel.png"));

            // 2. Compilar e exportar para PDF
            JasperPrint print = JasperFillManager.fillReport(
                    JasperCompileManager.compileReport(
                            getClass().getResourceAsStream("/static/jrxml/CardPrint.jrxml")),
                    params,
                    new JREmptyDataSource()
            );

            // 3. Salvar e abrir no Windows
            File pdf = File.createTempFile("rel_", ".pdf");
            JasperExportManager.exportReportToPdfFile(print, pdf.getPath());
            Runtime.getRuntime().exec("cmd /c start \"\" \"" + pdf.getPath() + "\"");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   public void onEquipButtonClick(ActionEvent event) throws IOException {
       navigateTo(event, StockEquipCardController.class.getResource("/static/fxml/equipCard-view.fxml"), controller -> {
           if (controller instanceof StockEquipCardController stockEquipCardController) {
               try {
                   stockEquipCardController.setTableEmployee(employeeId.getText());
               } catch (SQLException | IOException e) {
                   throw new RuntimeException(e);
               }
           }
       });
    }

    public void onEpiButtonClick(ActionEvent event) throws IOException {
        navigateTo(event, EpiCardController.class.getResource("/static/fxml/epiCard-view.fxml"), controller -> {
            if (controller instanceof EpiCardController epiCardController) {
                try {
                    epiCardController.setTableEmployee(employeeId.getText());
                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void onDatePickerSelect() {
        LocalDate selectedDate = datePicker.getValue();
        filteredItems.clear();

        for (BorrowedDTO item : borrowingsList) {
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