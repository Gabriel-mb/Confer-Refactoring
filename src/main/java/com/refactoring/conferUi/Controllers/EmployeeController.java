package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.DTO.BorrowedDTO;
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
import javafx.collections.ObservableList;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Integer.parseInt;

@Controller
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @FXML
    MFXTableView<Employee> table;
    @FXML
    private MFXTextField idEmployee;
    @FXML
    private MFXTextField employeeName;

    @FXML
    private void initialize() throws SQLException {
        setTableEmployees();
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
            if (employeeService.readId(parseInt(idEmployee.getText())) != null) {
                throw new SQLException();
            }
            employeeService.create(new Employee(parseInt(idEmployee.getText()), employeeName.getText()));
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
            List<Integer> idList = new ArrayList<>();
            for (Employee item : selectedItems) {
                idList.add(item.getIdEmployee());
            }
            employeeService.deleteByList(idList);
            table.setItems(employeeService.listEmployees());
        }
    }

    @FXML
    public void onPrintButtonClick() {
        try {
            List<Employee> employees = table.getTransformableList();
            Map<String, Object> params = new HashMap<>();
            params.put("CollectionBeanParam", new JRBeanCollectionDataSource(employees));

            JasperPrint print = JasperFillManager.fillReport(
                    JasperCompileManager.compileReport(
                            getClass().getResourceAsStream("/static/jrxml/employeePrint.jrxml")),
                    params,
                    new JREmptyDataSource()
            );

            JasperExportManager.exportReportToPdfFile(print, "employee_report.pdf");
            Runtime.getRuntime().exec("cmd /c start employee_report.pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void setTableEmployees() throws SQLException {
        MFXTableColumn<Employee> idColumn = new MFXTableColumn<>("Matrícula", Comparator.comparing(Employee::getIdEmployee));
        MFXTableColumn<Employee> nameEmployeeColumn = new MFXTableColumn<>("Funcionário", Comparator.comparing(Employee::getName));

        idColumn.setRowCellFactory(emp -> new MFXTableRowCell<>(Employee::getIdEmployee));
        nameEmployeeColumn.setRowCellFactory(emp -> new MFXTableRowCell<>(Employee::getName));

        nameEmployeeColumn.setPrefWidth(600);

        table.getTableColumns().addAll(idColumn, nameEmployeeColumn);
        table.getFilters().addAll(
                new StringFilter<>("Funcionário", Employee::getName),
                new IntegerFilter<>("Matrícula", Employee::getIdEmployee)
        );
        table.setItems(employeeService.listEmployees().sorted(Comparator.comparing(Employee::getName)));
    }
}