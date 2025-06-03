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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

import static com.refactoring.conferUi.Utils.NavigationUtils.navigateTo;
import static java.lang.Integer.parseInt;

@Component
public class EpiInputsController {
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
    private MFXTableView<EpiDTO> table;

    private String epiSeparatedName;

    private Integer caSeparated;

    private final double[] coordinates = new double[2];

    ObservableList<EpiDTO> episList;

    @Autowired
    private EpiService epiService;

    @FXML
    private void initialize() throws SQLException, IOException {
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                node.setFocusTraversable(false);
            }
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Supplier<StringConverter<LocalDate>> converterSupplier = () -> new LocalDateStringConverter(dateFormatter, null);
        date.setConverterSupplier(converterSupplier);
    }

    @FXML
    private void handleMouseEvents(MouseEvent event) {
        NavigationUtils.handleAnchorPaneDrag(event, anchorPane, coordinates);
    }

    public void onIncludeButtonClick() throws SQLException, IOException {
        splitSelection();
        String selectedEquipment = epiSeparatedName;
        Integer numCa = caSeparated;
        Integer quantity = Integer.valueOf(epiQuantity.getText());
        Date selectedDate = Date.valueOf(date.getValue());

        if (epiName.getValue() == null) {
            AlertUtils.showErrorAlert("Ocorreu um erro", "Selecione um equipamento!");
            return;
        }
        if (date.getValue() == null) {
            AlertUtils.showErrorAlert("Ocorreu um erro", "Selecione uma data válida!");
            return;
        }
        if (epiQuantity.getText().isEmpty()) {
            AlertUtils.showErrorAlert("Ocorreu um erro", "Selecione uma Quantidade!");
            return;
        }

        Epi epi = epiService.searchEpi(epiSeparatedName, caSeparated);
        if (epi.getQuantity() == null || epi.getQuantity() < quantity) {
            AlertUtils.showErrorAlert("Estoque insuficiente", "Não há estoque suficiente para emprestar a quantidade desejada.");
            return;
        }

        // Primeiro subtrai do estoque
        epiService.remove(epiSeparatedName, caSeparated, quantity);

        // Depois trata o empréstimo
        ObservableList<EpiDTO> episBorrowed = epiService.episListBorrowed(parseInt(idLabel.getText()));
        boolean exists = false;

        for (EpiDTO item : episBorrowed) {
            if (item.getNumCa().equals(numCa) && item.getDate().equals(selectedDate)) {
                epiService.updateBorrowedEpi(item.getQuantity() + quantity, numCa, Integer.valueOf(idLabel.getText()));
                exists = true;
                break;
            }
        }

        if (!exists) {
            EpiDTO newBorrowed = new EpiDTO(epiSeparatedName, numCa, quantity, selectedDate, parseInt(idLabel.getText()));
            epiService.create(newBorrowed);
        }

        table.setItems(epiService.episListBorrowed(parseInt(idLabel.getText())));
    }

    public void onRemoveButtonClick() throws SQLException, IOException {
        List<EpiDTO> selectedBorrowedList = (List<EpiDTO>) table.getSelectionModel().getSelectedValues();

        if (selectedBorrowedList != null && !selectedBorrowedList.isEmpty()) {
            for (EpiDTO selectedBorrowed : selectedBorrowedList) {
                epiService.addToStock(selectedBorrowed.getEpiName(), selectedBorrowed.getNumCa(), selectedBorrowed.getQuantity());

                epiService.removeBorrowed(selectedBorrowed);

                episList = epiService.episListBorrowed(parseInt(idLabel.getText()));
                table.getItems().remove(selectedBorrowed);

                AlertUtils.showInfoAlert("Sucesso", "Ferramenta removida e retornada ao estoque.");
            }
        }
    }

    public void setEmployee(String id, String name) {
        idLabel.setText(id);
        nameLabel.setText(name);
    }

    public void setTable() throws SQLException, IOException {
        ObservableList<EpiDTO> epiList = FXCollections.observableList(epiService.episListBorrowed(Integer.valueOf(idLabel.getText())));

        // Ordena a lista de estoque pelo nome do equipamento em ordem alfabética
        Collections.sort(epiList, Comparator.comparing(EpiDTO::getEpiName));

        MFXTableColumn<EpiDTO> epiName = new MFXTableColumn<>("Equipamentos", Comparator.comparing(EpiDTO::getEpiName));
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
    }

    public void setEpiDropDown() throws SQLException, IOException {
        ObservableList<EpiDTO> epiNames = FXCollections.observableList(epiService.listStock());
        HashSet<String> uniqueEpiNames = new HashSet<>();
        ObservableList<EpiDTO> uniqueEpisNames = FXCollections.observableArrayList();

        for (EpiDTO epi : epiNames) {
            String epiName = epi.getEpiName();
            if (!uniqueEpiNames.contains(epiName)) {
                uniqueEpiNames.add(epiName);
                uniqueEpisNames.add(epi);
            }
        }
        uniqueEpisNames.sort((e1, e2) -> e1.getEpiName().compareToIgnoreCase(e2.getEpiName()));

        for (EpiDTO i : uniqueEpisNames) {
            epiName.getItems().addAll(i.getEpiName() + " - C.A: " + i.getNumCa());
        }
    }

    public void onMenuButtonClick(ActionEvent event) throws IOException {
        NavigationUtils.navigateTo(event, SearchController.class.getResource("/static/fxml/search-view.fxml"), null);
    }

    public void onBackButtonClick(ActionEvent event) throws IOException, SQLException {
        navigateTo(event, EpiCardController.class.getResource("/static/fxml/epiCard-view.fxml"), controller -> {
            if (controller instanceof EpiCardController epiCardController) {
                try {
                    epiCardController.setTableEmployee(idLabel.getText());
                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void splitSelection() {
        String selection = (String) epiName.getValue();
        String[] sections = selection.split(" - C.A: ");

        epiSeparatedName = sections[0];
        caSeparated = Integer.valueOf(sections[1]);
    }
}