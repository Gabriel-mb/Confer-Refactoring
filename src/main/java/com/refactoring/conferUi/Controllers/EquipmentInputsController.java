package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.DTO.BorrowedDTO;
import com.refactoring.conferUi.Model.Entity.EquipmentBorrowed;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


import javafx.scene.input.MouseEvent;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;

import static java.lang.Integer.parseInt;

public class EquipmentInputsController {
    ObservableList<BorrowedDTO> borrowingsList = FXCollections.observableArrayList();
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Label nameLabel;
    @FXML
    private Label idLabel;
    @FXML
    private MFXDatePicker date;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField equipmentIdInput;
    @FXML
    private ComboBox<String> equipmentName;
    @FXML
    private TableView<BorrowedDTO> table;
    @FXML
    private TableColumn<EquipmentBorrowed, String> nameColumn;
    @FXML
    private TableColumn<EquipmentBorrowed, Integer> idColumn;
    @FXML
    private TableColumn<EquipmentBorrowed, java.util.Date> dateColumn;
    @FXML
    private TableColumn<EquipmentBorrowed, String> supplierColumn;
    @FXML
    private MFXButton minimizeButton;
    private Double x;
    private Double y;
    private Boolean confirmation = false;
    private String equipName;
    private String supplierName;


    /*public void onSearchButtonClick() throws SQLException, IOException {
        if (Objects.equals(equipmentIdInput.getText(), "")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Por favor insira uma matrícula adequada!");
            alert.showAndWait();
            return;
        }
        Connection connection = new ConnectionDAO().connect();
        EquipmentsService equipmentsService = new EquipmentsService(connection);
        List<Equipment> equipmentList = equipmentsService.readId(parseInt(equipmentIdInput.getText()));
        if (equipmentList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Nenhum equipamento encontrado!");
            alert.showAndWait();
            return;
        } else {
            equipmentName.getItems().clear();
            for (Equipment i : equipmentList) {
                equipmentName.getItems().addAll(i.getSupplierName() + " - " + i.getName());
            }
        }
        equipmentName.getSelectionModel().selectFirst();
    }*/

    /*public void onIncludeButtonClick() throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        BorrowedService borrowedService = new BorrowedService(connection);
        splitSelection();
        for (EquipmentBorrowed equipmentBorrowed : table.getItems()) {
            Integer id = idColumn.getCellData(equipmentBorrowed);
            String supplier = supplierColumn.getCellData(equipmentBorrowed);
            if (parseInt(equipmentIdInput.getText()) == id && Objects.equals(supplierName, supplier)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Ocorreu um erro");
                alert.setContentText("Ferramenta já cadastrada!");
                alert.showAndWait();

                equipmentName.getItems().clear();
                equipmentIdInput.setText("");
                date.setValue(null);

                return;
            }
        }
        if (equipmentName.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Por favor insira uma matrícula válida");
            alert.showAndWait();
        } else if (date.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Por favor insira uma data válida");
            alert.showAndWait();
        } else if (borrowedService.searchBorrowed(parseInt(equipmentIdInput.getText()), borrowedService.getSupplierId(supplierName))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ferramenta já Alocada");
            alert.setContentText("Por favor selecione outra ferramenta!");
            alert.showAndWait();
        } else {
            borrowingsList.add(new EquipmentBorrowed(equipName, parseInt(equipmentIdInput.getText()), Date.valueOf(date.getValue()), supplierName, borrowedService.getSupplierId(supplierName)));
            borrowedService.create(new EquipmentBorrowed(parseInt(idLabel.getText()), parseInt(equipmentIdInput.getText()), Date.valueOf(date.getValue()), borrowedService.getSupplierId(supplierName)));

            table.setItems(borrowingsList);

            equipmentName.getItems().clear();
            equipmentIdInput.setText("");
            date.setValue(null);
        }
    }*/

    private void splitSelection() {
        String selection = equipmentName.getValue();
        String[] sections = selection.split(" - ");

        supplierName = sections[0];
        equipName = sections[1];
    }

    /*public void removeData(Integer idEquip, String supplierName) throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        BorrowedService borrowedService = new BorrowedService(connection);
        HistoryDAO historyDAO = new HistoryDAO(connection);
        if (borrowedService.readId(idEquip) != null) borrowedService.delete(idEquip, historyDAO.getSupplierId(supplierName));
    }*/

    public void setEmployee(String id, String name) {
        idLabel.setText(id);
        nameLabel.setText(name);
    }

    public void setTable(ObservableList<BorrowedDTO> list, Boolean confirm) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<EquipmentBorrowed, String>("equipmentName"));
        idColumn.setCellValueFactory(new PropertyValueFactory<EquipmentBorrowed, Integer>("idEquipment"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<EquipmentBorrowed, java.util.Date>("date"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        table.setItems(list);
        confirmation = confirm;

        borrowingsList.addAll(list);
    }

    @FXML
    private void initialize() {
        // percorre todos os nós da cena e define o foco como não transversável para os TextFields
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                node.setFocusTraversable(false);
            }
        }
        //formata a data do datepicker
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Supplier<StringConverter<LocalDate>> converterSupplier = () -> new LocalDateStringConverter(dateFormatter, null);
        date.setConverterSupplier(converterSupplier);
        minimizeButton.setOnAction(e ->
                ( (Stage) ( (Button) e.getSource() ).getScene().getWindow() ).setIconified(true)
        );
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

    /*public void onDevolutionClick(ActionEvent event) throws IOException {
        //remover o dado do borrowed e gerar dado no histórico
        FXMLLoader loader = new FXMLLoader(getClass().getResource("devolution-view.fxml"));
        Parent root = loader.load();
        DevolutionController devolutionController = loader.getController();

        SelectionModel<EquipmentBorrowed> selectionModel = table.getSelectionModel();
        EquipmentBorrowed itemSelected = selectionModel.getSelectedItem();

        if (itemSelected == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Por favor selecione um item que quer devolver.");
            alert.showAndWait();
            return;
        }
        devolutionController.setData(nameLabel.getText(), idLabel.getText(), String.valueOf(itemSelected.getIdEquipment()), itemSelected.getEquipmentName(), String.valueOf(itemSelected.getDate()), itemSelected.getSupplierName());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }*/

    public void onBackButtonClick(MouseEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("patCard-view.fxml"));
        Parent root = loader.load();

        CardController cardController = loader.getController();
        cardController.setTableEmployee(idLabel.getText());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }
}