package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.Entity.Employee;
import com.refactoring.conferUi.Model.Entity.Epi;
import com.refactoring.conferUi.Services.EmployeeService;
import com.refactoring.conferUi.Services.EpiService;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Integer.parseInt;

public class EpiCardController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Label employeeId;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField newEmployeeId;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private MFXTableView<Epi> table;
    @FXML
    private MFXButton minimizeButton;

    private Double x;
    private Double y;
    private ObservableList<Epi> epiList;

    EpiService epiService;
    EmployeeService employeeService;


    @FXML
    private void initialize() {
        // percorre todos os nós da cena e define o foco como não transversável para os TextFields
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                node.setFocusTraversable(false);
            }
        }
        minimizeButton.setOnAction(e ->
                ( (Stage) ( (Button) e.getSource() ).getScene().getWindow() ).setIconified(true)
        );
    }

    public void onSearchButtonClick(ActionEvent event) throws SQLException, IOException {
        if (newEmployeeId.getText().length() != 8) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Matrícula deve ter 8 digitos!");
            alert.showAndWait();
            return;
        }

        Employee employee = employeeService.readId(parseInt(newEmployeeId.getText()));

        if (employee == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Nenhum funcionário encontrado!");
            alert.showAndWait();
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("equipCard-view.fxml"));
        Parent root = loader.load();

        StockEquipCardController stockEquipCardController = loader.getController();
        stockEquipCardController.setTableEmployee(newEmployeeId.getText());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public void onMenuButtonClick(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public void setTableEmployee(String id) throws SQLException, IOException {
        /*//Preenche a TableView de ferramentas pesquisando o ID do funcionario na DataBase
        employeeId.setText(id);

        Connection connection = new ConnectionDAO().connect();
        EpiService epiService = new EpiService(connection);
        epiList = FXCollections.observableList(epiService.episListBorrowed(Integer.valueOf(employeeId.getText())));

        // Ordena a lista de estoque pelo nome do equipamento em ordem alfabética
        Collections.sort(epiList, Comparator.comparing(Epi::getEpiName));

        MFXTableColumn<Epi> epiName = new MFXTableColumn<>("Equipamentos", Comparator.comparing(Epi::getEpiName));
        MFXTableColumn<Epi> numCa = new MFXTableColumn<>("C.A", Comparator.comparing(Epi::getNumCa));
        MFXTableColumn<Epi> quantity = new MFXTableColumn<>("Quantidade", Comparator.comparing(Epi::getQuantity));
        MFXTableColumn<Epi> date = new MFXTableColumn<>("Date", Comparator.comparing(Epi::getDate));


        epiName.setRowCellFactory(Epi -> new MFXTableRowCell<>(com.refactoring.conferUi.Model.Entity.Epi::getEpiName));
        numCa.setRowCellFactory(Epi -> new MFXTableRowCell<>(com.refactoring.conferUi.Model.Entity.Epi::getNumCa));
        quantity.setRowCellFactory(Epi -> new MFXTableRowCell<>(com.refactoring.conferUi.Model.Entity.Epi::getQuantity));
        date.setRowCellFactory(Epi -> new MFXTableRowCell<>(com.refactoring.conferUi.Model.Entity.Epi::getDate));

        epiName.setPrefWidth(500);
        numCa.setPrefWidth(150);
        quantity.setPrefWidth(150);
        date.setPrefWidth(150);

        table.getTableColumns().addAll(epiName, numCa, quantity, date);
        table.getFilters().addAll(
                new StringFilter<>("Epi", Epi::getEpiName),
                new IntegerFilter<>("C.A", Epi::getNumCa)
        );
        table.setItems(epiList);

        Employee employee = employeeService.readId(parseInt(employeeId.getText()));
        nameLabel.setText(employee.getName());*/
    }
    public void onCloseButtonClick() {
        System.exit(0);
    }
    public void anchorPane_dragged(MouseEvent event) {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.setY(event.getScreenY() - y);
        stage.setX(event.getScreenX() - x);

    }

    public void anchorPane_pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }
    public void onModifyButtonClick (ActionEvent event) throws IOException, SQLException {
        /*FXMLLoader loader = new FXMLLoader(getClass().getResource("epiInputsModify-view.fxml"));
        Parent root = loader.load();
        EpiInputsController epiInputsController = loader.getController();
        epiInputsController.setEmployee(employeeId.getText(), nameLabel.getText());
        epiInputsController.setTable();
        epiInputsController.setEpiDropDown();

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();*/
    }

    public void onPrintButtonClick () throws JRException, SQLException, IOException {
        /*JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(epiService.episListBorrowed(Integer.valueOf(employeeId.getText())));
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("CollectionBeanParam", itemsJRBean);
        parameters.put("employeeName", nameLabel.getText());
        parameters.put("employeeId", parseInt(employeeId.getText()));
        parameters.put("image", ClassLoader.getSystemResourceAsStream("assets/LogoCorel.png"));

        InputStream inputStream = getClass().getResourceAsStream("/static/jrxml/StockCardPrint.jrxml");

        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        JasperViewer.viewReport(jasperPrint, false);*/
    }
    public void onBackButtonClick (MouseEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("patCard-view.fxml"));
        Parent root = loader.load();

        CardController cardController = loader.getController();
        cardController.setTableEmployee(employeeId.getText());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }
}