package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.Entity.Equipment;
import com.refactoring.conferUi.Model.Entity.Supplier;
import com.refactoring.conferUi.Services.EquipmentsService;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Integer.parseInt;

@Component
public class InventoryController {/*
    @Autowired
    private EquipmentsService equipmentsService;
    @FXML
    private AnchorPane anchorPane;
    private final double[] coordinates = new double[2];

    @FXML
    MFXTableView<Equipment> table;

    @FXML
    private ComboBox<Supplier> supplierDropDown;
    @FXML
    private MFXTextField idEquipment;
    @FXML
    private MFXTextField name;
    @FXML
    private MFXButton minimizeButton;

    @FXML
    private void initialize() throws SQLException, IOException {
        // percorre todos os nós da cena e define o foco como não transversável para os TextFields
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                node.setFocusTraversable(false);
            }
        }
        minimizeButton.setOnAction(e ->
                ((Stage) ((Button) e.getSource()).getScene().getWindow()).setIconified(true)
        );

        setSupplierDropDown();
        setTableEquipments();
        table.autosizeColumnsOnInitialization();
    }

    @FXML
    private void handleMouseEvents(MouseEvent event) {
        NavigationUtils.handleAnchorPaneDrag(event, anchorPane, coordinates);
    }

    @FXML
    public void onMenuButtonClick(ActionEvent event) throws IOException {
        NavigationUtils.navigateTo(event, SearchController.class.getResource("/static/fxml/search-view.fxml"), null);
    }

    @FXML
    public void setTableEquipments() throws SQLException {
        MFXTableColumn<Equipment> idColumn = new MFXTableColumn<>("Patrimônio", Comparator.comparing(Equipment::getIdEquipment));
        MFXTableColumn<Equipment> nameEquipColumn = new MFXTableColumn<>("Ferramenta");
        MFXTableColumn<Equipment> nameSupplier = new MFXTableColumn<>("Fornecedor", Comparator.comparing(Equipment::getSupplierName));
        MFXTableColumn<Equipment> statusColumn = new MFXTableColumn<>("Status", Comparator.comparing(Equipment::getStatus));
        MFXTableColumn<Equipment> nameEmployeeColumn = new MFXTableColumn<>("Funcionário");
        MFXTableColumn<Equipment> dateColumn = new MFXTableColumn<>("Data");

        idColumn.setRowCellFactory(equipment -> new MFXTableRowCell<>(Equipment::getIdEquipment));
        nameEquipColumn.setRowCellFactory(equipment -> new MFXTableRowCell<>(Equipment::getNameEquip));
        nameSupplier.setRowCellFactory(equipment -> new MFXTableRowCell<>(Equipment::getSupplierName));
        statusColumn.setRowCellFactory(equipment -> new MFXTableRowCell<>(Equipment::getStatus));
        nameEmployeeColumn.setRowCellFactory(equipment -> new MFXTableRowCell<>(item -> Optional.ofNullable(item.getNameEmployee()).orElse(" ")));
        dateColumn.setRowCellFactory(equipment -> new MFXTableRowCell<>(item -> {
            Date dateValue = item.getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateValue != null ? dateFormat.format(dateValue) : " ";
        }));
        nameEquipColumn.setPrefWidth(300);

        table.getTableColumns().addAll(idColumn, nameEquipColumn, nameSupplier, statusColumn, nameEmployeeColumn, dateColumn);
        table.getFilters().addAll(
                new IntegerFilter<>("Patrimônio", Equipment::getIdEquipment),
                new StringFilter<>("Ferramenta", Equipment::getNameEquip),
                new StringFilter<>("Fornecedor", Equipment::getSupplierName),
                new StringFilter<>("Status", Equipment::getStatus),
                new StringFilter<>("Funcionário", Equipment::getNameEmployee)
        );
        table.setItems(equipmentsService.listEquipmentsStatus());
    }

    @FXML
    public void setSupplierDropDown() throws SQLException {
        supplierDropDown.setItems(FXCollections.observableList(equipmentsService.selectSupplier()));
    }

    @FXML
    public void onIncludeButtonClick() throws SQLException, IOException {
        if (Objects.equals(name.getText(), "")) {
            AlertUtils.showErrorAlert("Ocorreu um erro", "Insira um nome para a ferramenta!");
            return;
        }
        if (equipmentsService.searchEquipment(parseInt(idEquipment.getText()), equipmentsService.readId(String.valueOf(supplierDropDown.getValue())))) {
            AlertUtils.showErrorAlert("Ocorreu um erro", "Patrimônio ja cadastrado!");
            return;
        }
        try {
            equipmentsService.create(parseInt(idEquipment.getText()), name.getText(), String.valueOf(supplierDropDown.getValue()));
            table.setItems(equipmentsService.listEquipmentsStatus());
            AlertUtils.showInfoAlert("Sucesso", "Ferramenta inserida com sucesso!");
        } catch (SQLException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onCloseButtonClick() {
        System.exit(0);
    }

    public void removeData(Integer idEquip, String supplierName) throws SQLException {
        equipmentsService.delete(idEquip, equipmentsService.readId(supplierName));
    }

    @FXML
    public void onRemoveButtonClick() throws SQLException, IOException {
        Optional<ButtonType> result = AlertUtils.showWarningAlert("Tem certeza que deseja continuar?", "Esta ação não pode ser desfeita.");
        if (result.isPresent() && result.get() == ButtonType.YES) {
            MultipleSelectionModel<Equipment> selectionModel = (MultipleSelectionModel<Equipment>) table.getSelectionModel();
            List<Equipment> selectedItems = selectionModel.getSelectedValues();

            for (Equipment item : selectedItems) {
                equipmentsService.delete(item.getIdEquipment(), equipmentsService.readId(item.getSupplierName()));
            }
            table.setItems(equipmentsService.listEquipmentsStatus());
        }
    }

    public void onPrintButtonClick() throws JRException, SQLException, IOException {

        ObservableList<Equipment> filteredItems = table.getTransformableList();

        // Converta os itens filtrados em uma fonte de dados do JasperReports
        JRBeanCollectionDataSource filteredItemsJRBean = new JRBeanCollectionDataSource(filteredItems);

        // Parâmetros para o relatório
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("CollectionBeanParam", filteredItemsJRBean);

        // Carregue o arquivo do relatório
        InputStream inputStream = getClass().getResourceAsStream("/static/jrxml/InventoryPrint.jrxml");
        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);

        // Compile o relatório
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        // Preencha o relatório com os parâmetros e a fonte de dados filtrada
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

        // Exiba o relatório
        JasperViewer.viewReport(jasperPrint, false);
    }*/
}