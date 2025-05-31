package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Model.DTO.EquipmentDTO;
import com.refactoring.conferUi.Model.Entity.Equipment;
import com.refactoring.conferUi.Model.Entity.Supplier;
import com.refactoring.conferUi.Services.EquipmentsService;
import com.refactoring.conferUi.Services.SupplierService;
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
import static java.lang.Integer.valueOf;

@Component
public class InventoryController {
    @FXML
    private AnchorPane anchorPane;
    private final double[] coordinates = new double[2];

    @FXML
    MFXTableView<EquipmentDTO> table;

    @FXML
    private ComboBox<String> supplierDropDown;
    @FXML
    private MFXTextField idEquipment;
    @FXML
    private MFXTextField name;
    @FXML
    private MFXButton minimizeButton;

    private final EquipmentsService equipmentsService;
    private final SupplierService supplierService;

    @Autowired
    public InventoryController(EquipmentsService equipmentsService, SupplierService supplierService) {
        this.equipmentsService = equipmentsService;
        this.supplierService = supplierService;
    }

    @FXML
    private void initialize() throws SQLException, IOException {
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                node.setFocusTraversable(false);
            }
        }

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
        MFXTableColumn<EquipmentDTO> idColumn = new MFXTableColumn<>("Patrimônio", Comparator.comparing(EquipmentDTO::getIdEquipment));
        MFXTableColumn<EquipmentDTO> nameEquipColumn = new MFXTableColumn<>("Ferramenta", Comparator.comparing(EquipmentDTO::getNameEquip));
        MFXTableColumn<EquipmentDTO> nameSupplier = new MFXTableColumn<>("Fornecedor", Comparator.comparing(EquipmentDTO::getSupplierName));
        MFXTableColumn<EquipmentDTO> statusColumn = new MFXTableColumn<>("Status", Comparator.comparing(EquipmentDTO::getStatus));
        MFXTableColumn<EquipmentDTO> nameEmployeeColumn = new MFXTableColumn<>("Funcionário", Comparator.comparing(EquipmentDTO::getEmployeeName));
        MFXTableColumn<EquipmentDTO> dateColumn = new MFXTableColumn<>("Data", Comparator.comparing(EquipmentDTO::getDate));

        idColumn.setRowCellFactory(equipment -> new MFXTableRowCell<>(EquipmentDTO::getIdEquipment));
        nameEquipColumn.setRowCellFactory(equipment -> new MFXTableRowCell<>(EquipmentDTO::getNameEquip));
        nameSupplier.setRowCellFactory(equipment -> new MFXTableRowCell<>(EquipmentDTO::getSupplierName));
        statusColumn.setRowCellFactory(equipment -> new MFXTableRowCell<>(EquipmentDTO::getStatus));
        nameEmployeeColumn.setRowCellFactory(equipment -> new MFXTableRowCell<>(item -> Optional.ofNullable(item.getEmployeeName()).orElse(" ")));
        dateColumn.setRowCellFactory(equipment -> new MFXTableRowCell<>(item -> {
            Date dateValue = item.getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateValue != null ? dateFormat.format(dateValue) : " ";
        }));
        nameEquipColumn.setPrefWidth(300);

        table.getTableColumns().addAll(idColumn, nameEquipColumn, nameSupplier, statusColumn, nameEmployeeColumn, dateColumn);
        table.getFilters().addAll(
                new IntegerFilter<>("Patrimônio", EquipmentDTO::getIdEquipment),
                new StringFilter<>("Ferramenta", EquipmentDTO::getNameEquip),
                new StringFilter<>("Fornecedor", EquipmentDTO::getSupplierName),
                new StringFilter<>("Status", EquipmentDTO::getStatus),
                new StringFilter<>("Funcionário", EquipmentDTO::getEmployeeName)
        );
        table.setItems(equipmentsService.listEquipmentsStatus());
    }

    @FXML
    public void setSupplierDropDown() throws SQLException {
        supplierDropDown.setItems(FXCollections.observableList(supplierService.selectSupplier()));
    }

    @FXML
    public void onIncludeButtonClick() throws SQLException, IOException {
        if (Objects.equals(name.getText(), "")) {
            AlertUtils.showErrorAlert("Ocorreu um erro", "Insira um nome para a ferramenta!");
            return;
        }

        if (equipmentsService.searchEquipment(parseInt(idEquipment.getText()), supplierService.findIdByName(supplierDropDown.getValue()))) {
            AlertUtils.showErrorAlert("Ocorreu um erro", "Patrimônio ja cadastrado!");
            return;
        }
        try {
            equipmentsService.create(new Equipment(parseInt(idEquipment.getText()), name.getText(), supplierService.readById(supplierService.findIdByName(supplierDropDown.getValue()))));
            table.setItems(equipmentsService.listEquipmentsStatus());
            AlertUtils.showInfoAlert("Sucesso", "Ferramenta inserida com sucesso!");
        } catch (SQLException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onRemoveButtonClick() throws SQLException, IOException {
        Optional<ButtonType> result = AlertUtils.showWarningAlert("Tem certeza que deseja continuar?", "Esta ação não pode ser desfeita.");
        if (result.isPresent() && result.get() == ButtonType.YES) {
            MultipleSelectionModel<EquipmentDTO> selectionModel = (MultipleSelectionModel<EquipmentDTO>) table.getSelectionModel();
            List<EquipmentDTO> selectedItems = selectionModel.getSelectedValues();

            for (EquipmentDTO item : selectedItems) {
                equipmentsService.delete(item.getIdEquipment(), supplierService.findIdByName(supplierDropDown.getValue()));
            }
            table.setItems(equipmentsService.listEquipmentsStatus());
        }
    }

    public void onPrintButtonClick() throws JRException, SQLException, IOException {

        ObservableList<EquipmentDTO> filteredItems = table.getTransformableList();
        JRBeanCollectionDataSource filteredItemsJRBean = new JRBeanCollectionDataSource(filteredItems);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("CollectionBeanParam", filteredItemsJRBean);
        InputStream inputStream = getClass().getResourceAsStream("/static/jrxml/InventoryPrint.jrxml");
        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        JasperViewer.viewReport(jasperPrint, false);
    }
}