package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.Entity.EquipmentBorrowed;
import com.refactoring.conferUi.Services.BorrowedService;
import com.refactoring.conferUi.Services.EquipmentsService;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Supplier;

import static java.lang.Integer.parseInt;

public class DevolutionController {/*
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Label nameLabel;
    @FXML
    private Label idLabel;
    @FXML
    private MFXDatePicker dateDevolution;
    @FXML
    private MFXTextField fine;
    @FXML
    private Label idEquipmentLabel;
    @FXML
    private Label equipmentNameLabel;
    @FXML
    private Label dateBorrowedLabel;
    @FXML
    private Label supplierLabel;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private MFXButton minimizeButton;
    private Double x;
    private Double y;
    private ObservableList<EquipmentBorrowed> borrowingsList;
    private int statusId = -1;

    public void onSaveButtonClick(MouseEvent event) throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        if (statusId == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Por favor selecione o estado do equipamento");
            alert.showAndWait();
            return;
        }
        //"DEVOLVIDO" -> 1;
        //"NÃO LOCALIZADO" -> 2;
        //"DANIFICADO" -> 3;
        //"ROUBADO" -> 4;
        switch (statusId) {
            case 1:
                //apenas salvar no histórico e tirar de borrowed, porém não excluir de equipments.
                HistoryDAO historyDAO = new HistoryDAO(connection);
                Integer supId = historyDAO.getSupplierId(supplierLabel.getText());
                historyDAO.create(new History(parseInt(idEquipmentLabel.getText()), supId, equipmentNameLabel.getText(), parseInt(idLabel.getText()), nameLabel.getText(), Date.valueOf(dateBorrowedLabel.getText()), statusId, Date.valueOf(dateDevolution.getValue()), BigDecimal.valueOf(Float.parseFloat(fine.getText()))));
                BorrowedService borrowedService = new BorrowedService(connection);
                borrowedService.delete(parseInt(idEquipmentLabel.getText()), supId);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("patInputsModify-view.fxml"));
                Parent root = loader.load();

                borrowingsList = FXCollections.observableList(borrowedService.listBorrowed(Integer.valueOf(idLabel.getText())));
                EquipmentInputsController equipmentInputsController = loader.getController();
                equipmentInputsController.setEmployee(idLabel.getText(), nameLabel.getText());
                equipmentInputsController.setTable(borrowingsList, true);

                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                scene.setFill(Color.TRANSPARENT);
                stage.show();
                break;
                //casos 2, 3 e 4 excluir de equipments e salvar no history.
            case 2, 3, 4:
                historyDAO = new HistoryDAO(connection);
                borrowedService = new BorrowedService(connection);
                supId = historyDAO.getSupplierId(supplierLabel.getText());
                historyDAO.create(new History(parseInt(idEquipmentLabel.getText()), supId, equipmentNameLabel.getText(), parseInt(idLabel.getText()), nameLabel.getText(), Date.valueOf(dateBorrowedLabel.getText()), statusId, Date.valueOf(dateDevolution.getValue()), BigDecimal.valueOf(Float.parseFloat(fine.getText()))));
                EquipmentsService equipmentsService = new EquipmentsService(connection);
                equipmentsService.delete(parseInt(idEquipmentLabel.getText()), supId);

                loader = new FXMLLoader(getClass().getResource("patInputsModify-view.fxml"));
                root = loader.load();

                borrowingsList = FXCollections.observableList(borrowedService.listBorrowed(Integer.valueOf(idLabel.getText())));
                equipmentInputsController = loader.getController();
                equipmentInputsController.setEmployee(idLabel.getText(), nameLabel.getText());
                equipmentInputsController.setTable(borrowingsList, true);

                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                scene.setFill(Color.TRANSPARENT);
                stage.show();
                break;
        }
    }


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
        //formata a data do datepicker
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Supplier<StringConverter<LocalDate>> converterSupplier = () -> new LocalDateStringConverter(dateFormatter, null);
        dateDevolution.setConverterSupplier(converterSupplier);

        statusComboBox.getItems().addAll("DEVOLVIDO", "NÃO LOCALIZADO", "DANIFICADO", "ROUBADO");
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
    public void onCloseButtonClick() {
        System.exit(0);
    }

    public void onMenuButtonClick(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public void setData(String employee, String idEmployee, String idEquip, String equipment, String dateBorrowed, String supplierName) {
        nameLabel.setText(employee);
        idLabel.setText(idEmployee);
        idEquipmentLabel.setText(idEquip);
        equipmentNameLabel.setText(equipment);
        dateBorrowedLabel.setText(dateBorrowed);
        supplierLabel.setText(supplierName);
    }

    public void setStatusComboBox () {
        String selectedItem = statusComboBox.getValue();
        int selectedValue = -1;

        switch (selectedItem) {
            case "DEVOLVIDO" -> selectedValue = 1;
            case "NÃO LOCALIZADO" -> selectedValue = 2;
            case "DANIFICADO" -> selectedValue = 3;
            case "ROUBADO" -> selectedValue = 4;
        }
        statusId = selectedValue;
    }
    public void onBackButtonClick (MouseEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("patInputsModify-view.fxml"));
        Parent root = loader.load();
        EquipmentInputsController equipmentInputsController = loader.getController();
        equipmentInputsController.setEmployee(idLabel.getText(), nameLabel.getText());
        Connection connection = new ConnectionDAO().connect();
        BorrowedService borrowedService = new BorrowedService(connection);
        borrowingsList = FXCollections.observableList(borrowedService.listBorrowed(Integer.valueOf(idLabel.getText())));
        equipmentInputsController.setTable(borrowingsList, true);


        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }*/
}