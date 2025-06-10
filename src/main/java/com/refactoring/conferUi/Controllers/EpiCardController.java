package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.DTO.EpiDTO;
import com.refactoring.conferUi.Model.Entity.Employee;
import com.refactoring.conferUi.Services.EmployeeService;
import com.refactoring.conferUi.Services.EpiService;
import com.refactoring.conferUi.Utils.NavigationUtils;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

import static com.refactoring.conferUi.Utils.AlertUtils.showErrorAlert;
import static java.lang.Integer.parseInt;

@Component
public class EpiCardController {
    @FXML
    private Label employeeId;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField newEmployeeId;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private MFXTableView<EpiDTO> table;

    private final EpiService epiService;
    private final EmployeeService employeeService;

    @Autowired
    public EpiCardController(EpiService epiService, EmployeeService employeeService) {
        this.epiService = epiService;
        this.employeeService = employeeService;
    }

    @FXML
    private void initialize() {
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

    public void setTableEmployee(String id) throws SQLException, IOException {
        table.getTableColumns().clear();
        table.getItems().clear();
        employeeId.setText(id);

        List<EpiDTO> epiDTOList = epiService.episListBorrowed(Integer.valueOf(employeeId.getText()));
        ObservableList<EpiDTO> epiList = FXCollections.observableArrayList(epiDTOList).sorted();

        MFXTableColumn<EpiDTO> epiName = new MFXTableColumn<>("EPI", Comparator.comparing(EpiDTO::getEpiName));
        MFXTableColumn<EpiDTO> numCa = new MFXTableColumn<>("C.A", Comparator.comparing(EpiDTO::getNumCa));
        MFXTableColumn<EpiDTO> quantity = new MFXTableColumn<>("Quantidade", Comparator.comparing(EpiDTO::getQuantity));
        MFXTableColumn<EpiDTO> date = new MFXTableColumn<>("Date", Comparator.comparing(EpiDTO::getDate));


        epiName.setRowCellFactory(Epi -> new MFXTableRowCell<>(EpiDTO::getEpiName));
        numCa.setRowCellFactory(Epi -> new MFXTableRowCell<>(EpiDTO::getNumCa));
        quantity.setRowCellFactory(Epi -> new MFXTableRowCell<>(EpiDTO::getQuantity));
        date.setRowCellFactory(Epi -> new MFXTableRowCell<>(EpiDTO::getDate));

        epiName.setPrefWidth(500);
        numCa.setPrefWidth(150);
        quantity.setPrefWidth(150);
        date.setPrefWidth(150);

        table.getTableColumns().addAll(epiName, numCa, quantity, date);
        table.getFilters().addAll(
                new StringFilter<>("Epi", EpiDTO::getEpiName),
                new IntegerFilter<>("C.A", EpiDTO::getNumCa)
        );
        table.setItems(epiList);

        Employee employee = employeeService.readId(parseInt(employeeId.getText()));
        nameLabel.setText(employee.getName());
    }

    public void onModifyButtonClick(ActionEvent event) throws IOException, SQLException {
        NavigationUtils.navigateTo(event, EpiInputsController.class.getResource("/static/fxml/epiInputsModify-view.fxml"), controller -> {
            if (controller instanceof EpiInputsController epiInputsController) {
                try {
                    epiInputsController.setEmployee(employeeId.getText(), nameLabel.getText());
                    epiInputsController.setTable();
                    epiInputsController.setEpiDropDown();
                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void onPrintButtonClick() throws JRException, SQLException, IOException {
        JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(epiService.episListBorrowed(Integer.valueOf(employeeId.getText())));
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("CollectionBeanParam", itemsJRBean);
        parameters.put("employeeName", nameLabel.getText());
        parameters.put("employeeId", parseInt(employeeId.getText()));
        parameters.put("image", ClassLoader.getSystemResourceAsStream("assets/LogoCorel.png"));

        InputStream inputStream = getClass().getResourceAsStream("/static/jrxml/StockCardPrint.jrxml");

        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        JasperViewer.viewReport(jasperPrint, false);
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
}