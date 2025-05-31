package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.Entity.EquipmentBorrowed;
import com.refactoring.conferUi.Model.Entity.Employee;
import com.refactoring.conferUi.Services.EmployeeService;
import com.refactoring.conferUi.Services.StockService;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.time.LocalDate;
import java.util.*;

import static java.lang.Integer.parseInt;

public class StockEquipCardController {

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
    private MFXTableView<EquipmentBorrowed> table;
    @FXML
    private MFXButton minimizeButton;
    @FXML
    private MFXDatePicker datePicker;
    @FXML
    private MFXButton resetButton;

    private ObservableList<EquipmentBorrowed> borrowingsList;
    private ObservableList<EquipmentBorrowed> filteredItems;

    EmployeeService employeeService;

    private Double x;
    private Double y;


    @FXML
    private void initialize() {
        // percorre todos os nós da cena e define o foco como não transversável para os TextFields
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                node.setFocusTraversable(false);
            }
        }
        datePicker.setOnAction(event -> onDatePickerSelect());
        resetButton.setOnAction(event -> resetDatePicker());
        filteredItems = FXCollections.observableArrayList();
    }

    public void onSearchButtonClick(MouseEvent event) throws SQLException, IOException {
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

    /*public void onDeleteButtonClick(ActionEvent event) throws IOException, SQLException {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Tem certeza que deseja continuar?");
        alert.setContentText("Esta ação não pode ser desfeita.");
        ButtonType yesButton = new ButtonType("Sim");
        ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(cancelButton, yesButton);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == yesButton) {
            Connection connection = new ConnectionDAO().connect();
            EmployeeService employeeService = new EmployeeService(connection);
            employeeService.delete(parseInt(employeeId.getText()));

            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ficha Deletada");
            alert.setHeaderText(null);
            alert.setContentText("A ficha foi deletada com sucesso!");
            alert.showAndWait();

            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }*/

    public void setTableEmployee(String id) throws SQLException, IOException {
        /*//Preenche a TableView de ferramentas pesquisando o ID do funcionario na DataBase
        employeeId.setText(id);

        Connection connection = new ConnectionDAO().connect();
        StockService stockService = new StockService(connection);
        borrowingsList = FXCollections.observableList(stockService.stockListBorrowed(Integer.valueOf(employeeId.getText())));

        MFXTableColumn<EquipmentBorrowed> equipmentName = new MFXTableColumn<>("Ferramenta", Comparator.comparing(EquipmentBorrowed::getEquipmentName));
        MFXTableColumn<EquipmentBorrowed> quantity = new MFXTableColumn<>("Quantidade", Comparator.comparing(EquipmentBorrowed::getQuantity));
        MFXTableColumn<EquipmentBorrowed> date = new MFXTableColumn<>("Data", Comparator.comparing(EquipmentBorrowed::getDate));
        MFXTableColumn<EquipmentBorrowed> supplierName = new MFXTableColumn<>("Fornecedor", Comparator.comparing(EquipmentBorrowed::getSupplierName));

        equipmentName.setRowCellFactory(Stock -> new MFXTableRowCell<>(EquipmentBorrowed::getEquipmentName));
        quantity.setRowCellFactory(Stock -> new MFXTableRowCell<>(EquipmentBorrowed::getQuantity));
        date.setRowCellFactory(Stock -> new MFXTableRowCell<>(EquipmentBorrowed::getDate));
        supplierName.setRowCellFactory(Stock -> new MFXTableRowCell<>(EquipmentBorrowed::getSupplierName));

        table.getTableColumns().addAll(equipmentName, quantity, date, supplierName);
        table.getFilters().addAll(
                new StringFilter<>("Ferramenta", EquipmentBorrowed::getEquipmentName),
                new IntegerFilter<>("Quantidade", EquipmentBorrowed::getQuantity),
                new StringFilter<>("Fornecedor", EquipmentBorrowed::getSupplierName)
        );

        equipmentName.setPrefWidth(350);
        table.setItems(borrowingsList);

        EmployeeService employeeService = new EmployeeService(connection);
        Employee employee = employeeService.readId(parseInt(employeeId.getText()));
        nameLabel.setText(employee.getName());*/
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

    /*public void onModifyButtonClick(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("equipInputsModify-view.fxml"));
        Parent root = loader.load();
        StockEquipInputsController stockEquipInputsController = loader.getController();
        stockEquipInputsController.setEmployee(employeeId.getText(), nameLabel.getText());
        stockEquipInputsController.setTable(borrowingsList, true);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }*/

    /*public void onPrintButtonClick() throws JRException, SQLException, IOException {
        ObservableList<EquipmentBorrowed> filteredItems = table.getTransformableList();
        JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(filteredItems);
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
    }*/

    public void onBackButtonClick(MouseEvent event) throws IOException, SQLException {
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

    public void onDatePickerSelect() {
        LocalDate selectedDate = datePicker.getValue();
        filteredItems.clear(); // Limpa a lista de itens filtrados

        for (EquipmentBorrowed item : borrowingsList) {
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