package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.DTO.EpiDTO;
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
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Integer.parseInt;

@Component
public class EpiStockController {
    @Autowired
    private EpiService epiService;
    @FXML
    private AnchorPane anchorPane;
    private final double[] coordinates = new double[2];
    @FXML
    MFXTableView<EpiDTO> table;
    @FXML
    private MFXTextField quantity;
    @FXML
    private MFXFilterComboBox<String> epiDropDown;
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
        ObservableList<EpiDTO> epiList = FXCollections.observableArrayList(epiService.listStock()).sorted(Comparator.comparing(EpiDTO::getEpiName));

        MFXTableColumn<EpiDTO> epiName = new MFXTableColumn<>("Equipamentos", Comparator.comparing(EpiDTO::getEpiName));
        MFXTableColumn<EpiDTO> numCa = new MFXTableColumn<>("C.A", Comparator.comparing(EpiDTO::getNumCa));
        MFXTableColumn<EpiDTO> quantity = new MFXTableColumn<>("Quantidade", Comparator.comparing(EpiDTO::getQuantity));

        epiName.setRowCellFactory(epi -> new MFXTableRowCell<>(EpiDTO::getEpiName));
        numCa.setRowCellFactory(epi -> new MFXTableRowCell<>(EpiDTO::getNumCa));
        quantity.setRowCellFactory(epi -> new MFXTableRowCell<>(EpiDTO::getQuantity));

        epiName.setPrefWidth(500);
        numCa.setPrefWidth(150);
        quantity.setPrefWidth(150);

        table.getTableColumns().addAll(epiName, numCa, quantity);
        table.getFilters().addAll(
                new StringFilter<>("Epi", EpiDTO::getEpiName),
                new IntegerFilter<>("C.A", EpiDTO::getNumCa)
        );
        table.setItems(epiList);
    }

    public void setEpiDropDown() throws SQLException {
        HashSet<String> uniqueEpiNames = new HashSet<>();
        ObservableList<String> uniqueEpisNames = FXCollections.observableArrayList();

        for (EpiDTO epi : epiService.listStock()) {
            String epiName = epi.getEpiName();
            if (!uniqueEpiNames.contains(epiName)) {
                uniqueEpiNames.add(epiName);
                uniqueEpisNames.add(epiName);
            }
        }
        uniqueEpisNames.sort(String::compareToIgnoreCase);

        epiDropDown.setItems(uniqueEpisNames);

        epiDropDown.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        });
    }

    public void onIncludeButtonClick() {
        if (epiDropDown.getValue() == null || epiDropDown.getValue().isEmpty()) {
            AlertUtils.showErrorAlert("Erro", "Selecione um EPI!");
            return;
        }
        if (quantity.getText().isEmpty() || !quantity.getText().matches("\\d+")) {
            AlertUtils.showErrorAlert("Erro", "Insira uma quantidade válida!");
            return;
        }

        try {
            int quantidadeAdicionar = parseInt(quantity.getText());
            if (quantidadeAdicionar <= 0) {
                AlertUtils.showErrorAlert("Erro", "A quantidade deve ser maior que zero!");
                return;
            }

            epiService.update(new EpiDTO(
                    epiDropDown.getValue(),
                    parseInt(numCa.getText()),
                    quantidadeAdicionar
            ));
            ObservableList<EpiDTO> epiList = epiService.listStock()
                    .sorted(Comparator.comparing(EpiDTO::getEpiName));
            table.setItems(epiList);

            AlertUtils.showInfoAlert("Sucesso", "Estoque atualizado com sucesso!");
            quantity.clear();  // Limpa o campo após a operação
        } catch (Exception e) {
            AlertUtils.showErrorAlert("Erro", "Ocorreu um erro: " + e.getMessage());
        }
    }

    @FXML
    private void initialize() throws SQLException {
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                node.setFocusTraversable(false);
            }
        }
        setTableEquipments();
        setEpiDropDown();
        table.autosizeColumnsOnInitialization();
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
                    MultipleSelectionModel<EpiDTO> selectionModel = (MultipleSelectionModel<EpiDTO>) table.getSelectionModel();
                    List<EpiDTO> selectedItems = selectionModel.getSelectedValues();

                    for (EpiDTO item : selectedItems) {
                        epiService.remove(item.getEpiName(), item.getNumCa(), Integer.parseInt(quantityResult.get()));
                    }
                    AlertUtils.showInfoAlert("Estoque Alterado!", "Estoque alterado com sucesso!");
                    ObservableList<EpiDTO> epiList = epiService.listStock().sorted(Comparator.comparing(EpiDTO::getEpiName));
                    table.setItems(epiList);
                } catch (NumberFormatException e) {
                    AlertUtils.showErrorAlert("Erro", "A quantidade inserida não é válida.");
                }
            }
        }
    }
}