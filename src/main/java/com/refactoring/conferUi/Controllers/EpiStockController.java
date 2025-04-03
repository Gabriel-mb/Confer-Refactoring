package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.Entity.Epi;
import com.refactoring.conferUi.Services.EpiService;
import com.refactoring.conferUi.Utils.AlertUtils;
import com.refactoring.conferUi.Utils.NavigationUtils;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.selection.MultipleSelectionModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Integer.parseInt;

@Component
public class EpiStockController {/*
    @Autowired
    private EpiService epiService;
    @FXML
    private AnchorPane anchorPane;
    private final double[] coordinates = new double[2];
    @FXML
    MFXTableView<Epi> table;
    @FXML
    private MFXTextField quantity;
    @FXML
    private MFXFilterComboBox<Epi> epiDropDown;
    @FXML
    private MFXTextField numCa;
    @FXML
    private MFXButton minimizeButton;

    @FXML
    private void handleMouseEvents(MouseEvent event) {
        NavigationUtils.handleAnchorPaneDrag(event, anchorPane, coordinates);
    }

    public void onMenuButtonClick(ActionEvent event) throws IOException {
        NavigationUtils.navigateTo(event, SearchController.class.getResource("/static/fxml/search-view.fxml"), null);
    }

    public void setTableEquipments() throws SQLException {
        ObservableList<Epi> epiList = FXCollections.observableArrayList(epiService.listStock()).sorted(Comparator.comparing(Epi::getEpiName));

        MFXTableColumn<Epi> epiName = new MFXTableColumn<>("Equipamentos", Comparator.comparing(Epi::getEpiName));
        MFXTableColumn<Epi> numCa = new MFXTableColumn<>("C.A", Comparator.comparing(Epi::getNumCa));
        MFXTableColumn<Epi> quantity = new MFXTableColumn<>("Quantidade", Comparator.comparing(Epi::getQuantity));

        epiName.setRowCellFactory(epi -> new MFXTableRowCell<>(Epi::getEpiName));
        numCa.setRowCellFactory(epi -> new MFXTableRowCell<>(Epi::getNumCa));
        quantity.setRowCellFactory(epi -> new MFXTableRowCell<>(Epi::getQuantity));

        epiName.setPrefWidth(500);
        numCa.setPrefWidth(150);
        quantity.setPrefWidth(150);

        table.getTableColumns().addAll(epiName, numCa, quantity);
        table.getFilters().addAll(
                new StringFilter<>("Epi", Epi::getEpiName),
                new IntegerFilter<>("C.A", Epi::getNumCa)
        );
        table.setItems(epiList);
    }

    public void setEpiDropDown() throws SQLException {
        HashSet<String> uniqueEpiNames = new HashSet<>();
        ObservableList<Epi> uniqueEpisNames = FXCollections.observableArrayList();

        for (Epi epi : FXCollections.observableList(epiService.listStock())) {
            String epiName = epi.getEpiName();
            if (!uniqueEpiNames.contains(epiName)) {
                uniqueEpiNames.add(epiName);
                uniqueEpisNames.add(epi);
            }
        }
        uniqueEpisNames.sort((e1, e2) -> e1.getEpiName().compareToIgnoreCase(e2.getEpiName()));

        epiDropDown.setItems(uniqueEpisNames);
    }

    public void onIncludeButtonClick() {
        if (Objects.equals(String.valueOf(epiDropDown.getValue()), "")) {
            AlertUtils.showErrorAlert("Ocorreu um erro", "Insira um nome para o Epi!");
            return;
        }
        if (Objects.equals(quantity.getText(), "")) {
            AlertUtils.showErrorAlert("Ocorreu um erro", "Insira uma quantidade valida!");
            return;
        }
        try {
            epiService.createOrUpdateStock(parseInt(quantity.getText()), epiDropDown.getText(), parseInt(numCa.getText()));
            ObservableList<Epi> epiList = FXCollections.observableArrayList(epiService.listStock());
            Collections.sort(epiList, Comparator.comparing(Epi::getEpiName));
            table.setItems(epiList);
            AlertUtils.showInfoAlert("Sucesso", "Epi inserido com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() throws SQLException {
        // percorre todos os nós da cena e define o foco como não transversável para os TextFields
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                node.setFocusTraversable(false);
            }
        }
        minimizeButton.setOnAction(e ->
                ((Stage) ((Button) e.getSource()).getScene().getWindow()).setIconified(true)
        );

        setTableEquipments();
        setEpiDropDown();
        table.autosizeColumnsOnInitialization();
    }

    public void onCloseButtonClick() {
        System.exit(0);
    }

    public void onRemoveButtonClick() throws SQLException, IOException {
        Optional<ButtonType> result = AlertUtils.showWarningAlert("Tem certeza que deseja continuar?", "Esta ação não pode ser desfeita.");

        if (result.isPresent() && result.get() == ButtonType.YES) {
            TextInputDialog inputDialog = new TextInputDialog();
            inputDialog.setTitle("Quantidade a ser removida");
            inputDialog.setHeaderText(null);
            inputDialog.setContentText("Digite a quantidade a ser removida:");

            Optional<String> quantityResult = inputDialog.showAndWait();

            if (quantityResult.isPresent() && !quantityResult.get().isEmpty()) {
                try {
                    MultipleSelectionModel<Epi> selectionModel = (MultipleSelectionModel<Epi>) table.getSelectionModel();
                    List<Epi> selectedItems = selectionModel.getSelectedValues();

                    for (Epi item : selectedItems) {
                        epiService.decreaseOrDeleteStock(Integer.parseInt(quantityResult.get()), item.getEpiName(), item.getNumCa());
                    }
                    AlertUtils.showInfoAlert("Estoque Alterado!", "Estoque alterado com sucesso!");
                    ObservableList<Epi> epiList = FXCollections.observableArrayList(epiService.listStock()).sorted(Comparator.comparing(Epi::getEpiName));
                    table.setItems(epiList);
                } catch (NumberFormatException e) {
                    AlertUtils.showErrorAlert("Erro", "A quantidade inserida não é válida.");
                }
            }
        }
    }*/
}