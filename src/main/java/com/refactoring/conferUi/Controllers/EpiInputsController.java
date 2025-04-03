package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.Entity.Epi;
import com.refactoring.conferUi.Services.EpiService;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
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
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

import static java.lang.Integer.parseInt;

public class EpiInputsController {/*
    ObservableList<Epi> episList;
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
    private TextField epiQuantity;
    @FXML
    private MFXFilterComboBox epiName;
    @FXML
    private MFXTableView<Epi> table;

    private String epiSeparatedName;

    private Integer caSeparated;

    @FXML
    private MFXButton minimizeButton;
    private Double x;
    private Double y;


    public void onIncludeButtonClick() throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        EpiService epiService = new EpiService(connection);
        splitSelection();
        String selectedEquipment = epiSeparatedName;
        Integer numCa = caSeparated;
        Integer quantity = Integer.valueOf(epiQuantity.getText());
        Date selectedDate = Date.valueOf(date.getValue());

        Boolean terminate = false;
        if (epiName.getValue() == null) {
            showErrorAlert("Erro", "Ocorreu um erro", "Selecione um equipamento!");
        } else if (date.getValue() == null) {
            showErrorAlert("Erro", "Ocorreu um erro", "Selecione uma data válida!");
        } else if (epiQuantity == null) {
            showErrorAlert("Erro", "Ocorreu um erro", "Selecione uma Quantidade!");
        } else {
            Epi epi = epiService.searchEpi(caSeparated);
            if (epi.getQuantity() == null) {
                showErrorAlert("Erro", "Estoque vazio!", "Não há estoque deste equipamento.");
                return;
            } else {
                int quantityToBorrow = quantity;
                int currentStock = epi.getQuantity();

                if (currentStock < quantityToBorrow) {
                    showErrorAlert("Erro", "Estoque insuficiente", "Não há estoque suficiente para emprestar a quantidade desejada do equipamento.");
                    return;
                }
            }
            ObservableList<Epi> episBorrowed = epiService.episListBorrowed(parseInt(idLabel.getText()));
            for (Epi item : episBorrowed) {
                if (item.getNumCa().equals(numCa) && item.getDate().equals(selectedDate)) {
                    int updatedStock = epi.getQuantity() - parseInt(epiQuantity.getText());
                    epiService.updateStock(item.getEpiName(), item.getNumCa(), updatedStock);
                    int updatedQuantity = parseInt(epiQuantity.getText()) + item.getQuantity();
                    epiService.updateBorrowedEpi(updatedQuantity, item.getEpiName(), item.getNumCa(), Date.valueOf(date.getValue()), Integer.valueOf(idLabel.getText()));
                    terminate = true;
                }
            }
            if (!terminate) {
                int updatedStock = epi.getQuantity() - parseInt(epiQuantity.getText());
                epiService.updateStock(epi.getEpiName(), epi.getNumCa(), updatedStock);
                epiService.createBorrowed(new Epi(parseInt(epiQuantity.getText()), epi.getNumCa(), epi.getEpiName(), Date.valueOf(date.getValue())), parseInt(idLabel.getText()));
                episBorrowed.add(new Epi(parseInt(epiQuantity.getText()), epi.getNumCa(), epi.getEpiName(), Date.valueOf(date.getValue()), Integer.valueOf(idLabel.getText())));
                table.setItems(episBorrowed);
            }
            table.setItems(epiService.episListBorrowed(parseInt(idLabel.getText())));
        }
    }

    public void onRemoveButtonClick() throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        EpiService epiService = new EpiService(connection);
        List<Epi> selectedBorrowedList = (List<Epi>) table.getSelectionModel().getSelectedValues();

        if (selectedBorrowedList != null && !selectedBorrowedList.isEmpty()) {
            for (Epi selectedBorrowed : selectedBorrowedList) {
                episList = epiService.episListBorrowed(parseInt(idLabel.getText()));
                if (epiService.checkDate(selectedBorrowed)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Confirmação");
                    alert.setHeaderText("Tem certeza que deseja continuar?");
                    alert.setContentText("Esta ação não pode ser desfeita.");
                    ButtonType yesButton = new ButtonType("Sim");
                    ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(cancelButton, yesButton);
                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == yesButton) {
                        epiService.remove(selectedBorrowed.getEpiName(), selectedBorrowed.getNumCa(), selectedBorrowed.getDate());
                        episList.remove(selectedBorrowed);
                        table.getItems().remove(selectedBorrowed);
                        showSucessAlert("Sucesso", "Ferramenta Removida", "Ferramenta retornada ao estoque.");
                    }
                } else {
                    episList.remove(selectedBorrowed);
                    table.getItems().remove(selectedBorrowed);
                    showSucessAlert("Sucesso", "Ferramenta Removida", "Ferramenta removida com sucesso.");
                }
            }
        }
    }

    public void setEmployee(String id, String name) {
        idLabel.setText(id);
        nameLabel.setText(name);
    }

    public void setTable() throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        EpiService epiService = new EpiService(connection);
        ObservableList<Epi> epiList = FXCollections.observableList(epiService.episListBorrowed(Integer.valueOf(idLabel.getText())));

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
    }

    public void setEpiDropDown() throws SQLException, IOException {

        Connection connection = new ConnectionDAO().connect();
        EpiService epiService = new EpiService(connection);
        ObservableList<Epi> epiNames = FXCollections.observableList(epiService.listStock());
        // Usamos um HashSet temporário para armazenar os objetos Stock únicos com base no nome do equipamento
        HashSet<String> uniqueEpiNames = new HashSet<>();
        ObservableList<Epi> uniqueEpisNames = FXCollections.observableArrayList();

        for (Epi epi : epiNames) {
            String epiName = epi.getEpiName();
            if (!uniqueEpiNames.contains(epiName)) {
                uniqueEpiNames.add(epiName);
                uniqueEpisNames.add(epi);
            }
        }
        // Ordena a lista de objetos Stock em ordem alfabética
        uniqueEpisNames.sort((e1, e2) -> e1.getEpiName().compareToIgnoreCase(e2.getEpiName()));

        for (Epi i : uniqueEpisNames) {
            epiName.getItems().addAll(i.getEpiName() + " - C.A: " + i.getNumCa());
        }
    }


    @FXML
    private void initialize() throws SQLException, IOException {
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


    private void showErrorAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void showSucessAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public void onBackButtonClick(MouseEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("epiCard-view.fxml"));
        Parent root = loader.load();

        EpiCardController epiCardController = loader.getController();
        epiCardController.setTableEmployee(idLabel.getText());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    private void splitSelection() {
        String selection = (String) epiName.getValue();
        String[] sections = selection.split(" - C.A: ");

        epiSeparatedName = sections[0];
        caSeparated = Integer.valueOf(sections[1]);
    }
    */
}