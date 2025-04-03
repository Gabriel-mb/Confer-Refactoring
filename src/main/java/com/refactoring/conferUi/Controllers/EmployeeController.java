package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.Entity.Employee;
import com.refactoring.conferUi.Services.EmployeeService;
import com.refactoring.conferUi.Utils.AlertUtils;
import com.refactoring.conferUi.Utils.NavigationUtils;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.selection.MultipleSelectionModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Integer.parseInt;

@Controller
public class EmployeeController {/*
    @Autowired
    private EmployeeService employeeService;
    @FXML
    private AnchorPane anchorPane;
    private final double[] coordinates = new double[2];
    @FXML
    MFXTableView<Employee> table;
    @FXML
    private MFXTextField idEmployee;
    @FXML
    private MFXTextField employeeName;
    @FXML
    private MFXButton minimizeButton;

    @FXML
    private void initialize() throws SQLException {
        minimizeButton.setOnAction(e ->
                ((Stage) ((Button) e.getSource()).getScene().getWindow()).setIconified(true)
        );
        setTableEmployees();
    }

    @FXML
    private void handleMouseEvents(MouseEvent event) {
        NavigationUtils.handleAnchorPaneDrag(event, anchorPane, coordinates);
    }

    @FXML
    public void onCloseButtonClick() {
        System.exit(0);
    }

    @FXML
    public void onMenuButtonClick(ActionEvent event) throws IOException {
        NavigationUtils.navigateTo(event, SearchController.class.getResource("/static/fxml/search-view.fxml"), null);
    }

    @FXML
    public void onIncludeButtonClick() {
        if (Objects.equals(employeeName.getText(), "")) {
            AlertUtils.showErrorAlert("Ocorreu um erro", "Insira um nome para o funcionário!");
            return;
        }
        try {
            employeeService.create(parseInt(idEmployee.getText()), employeeName.getText());
            table.setItems(employeeService.listEmployees());
            AlertUtils.showInfoAlert("Sucesso", "Funcionário cadastrado com sucesso!");
        } catch (SQLException e) {
            AlertUtils.showErrorAlert("Ocorreu um erro", "Funcionário já cadastrado!");
        }
    }

    @FXML
    public void onRemoveButtonClick() throws SQLException, IOException {
        Optional<ButtonType> result = AlertUtils.showWarningAlert("Tem certeza que deseja continuar?", "Esta ação não pode ser desfeita.");
        if (result.isPresent() && result.get() == ButtonType.YES) {
            MultipleSelectionModel<Employee> selectionModel = (MultipleSelectionModel<Employee>) table.getSelectionModel();
            List<Employee> selectedItems = selectionModel.getSelectedValues();

            for (Employee item : selectedItems) {
                employeeService.delete(item.getId());
            }
            table.setItems(employeeService.listEmployees());
        }
    }

    //QUEBRADO CONSERTAR
    @FXML
    public void onPrintButtonClick() throws JRException {
        JRBeanCollectionDataSource filteredItemsJRBean = new JRBeanCollectionDataSource(table.getTransformableList());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("CollectionBeanParam", filteredItemsJRBean);
        InputStream inputStream = getClass().getResourceAsStream("/static/jrxml/employeePrint.jrxml");
        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        JasperViewer.viewReport(jasperPrint, false);
    }

    @FXML
    public void setTableEmployees() throws SQLException {
        MFXTableColumn<Employee> idColumn = new MFXTableColumn<>("Matrícula", Comparator.comparing(Employee::getId));
        MFXTableColumn<Employee> nameEmployeeColumn = new MFXTableColumn<>("Funcionário", Comparator.comparing(Employee::getName));

        idColumn.setRowCellFactory(emp -> new MFXTableRowCell<>(Employee::getId));
        nameEmployeeColumn.setRowCellFactory(emp -> new MFXTableRowCell<>(Employee::getName));

        nameEmployeeColumn.setPrefWidth(600);

        table.getTableColumns().addAll(idColumn, nameEmployeeColumn);
        table.getFilters().addAll(
                new StringFilter<>("Funcionário", Employee::getName),
                new IntegerFilter<>("Matrícula", Employee::getId)
        );
        table.setItems(employeeService.listEmployees().sorted(Comparator.comparing(Employee::getName)));
    }*/
}