package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Utils.AlertUtils;
import com.refactoring.conferUi.Services.EmployeeService;
import com.refactoring.conferUi.Utils.NavigationUtils;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.io.IOException;
import static com.refactoring.conferUi.Utils.NavigationUtils.navigateTo;

@Controller
public class SearchController {
    @Autowired
    private EmployeeService employeeService;
    @FXML
    public AnchorPane anchorPane;
    @FXML
    private TextField employeeId;
    @FXML
    private MFXButton minimizeButton;
    private final double[] coordinates = new double[2];

    @FXML
    private void initialize() {
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                node.setFocusTraversable(false);
            }
        }
        minimizeButton.setOnAction(e ->
                ((Stage) ((Button) e.getSource()).getScene().getWindow()).setIconified(true)
        );
    }

    public void onCloseButtonClick() {
        System.exit(0);
    }

    @FXML
    private void handleMouseEvents(MouseEvent event) {
        NavigationUtils.handleAnchorPaneDrag(event, anchorPane, coordinates);
    }
    /*@FXML
    public void onSearchButtonClick(ActionEvent event) throws SQLException {
        if (employeeId.getText().length() != 8) {
            AlertUtils.showErrorAlert("Matrícula deve ter 8 digitos!", "Tente Novamente!");
            return;
        }
        try {
            if (employeeService.readId(Integer.parseInt(employeeId.getText())) == null) {
                AlertUtils.showErrorAlert("Nenhum funcionário encontrado!", "Tente Novamente!");
                return;
            }
            try {
                navigateTo(event, CardController.class.getResource("/static/fxml/patCard-view.fxml"), controller -> {
                    if (controller instanceof CardController cardController) {
                        try {
                            cardController.setTableEmployee(employeeId.getText());
                        } catch (SQLException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Erro ao buscar funcionário!", "Tente Novamente!");
        }
    }*/

    @FXML
    public void onInventoryButtonClick(ActionEvent event) throws IOException {
        navigateTo(event, InventoryController.class.getResource("/static/fxml/patrimony-view.fxml"), null);
    }
    @FXML
    public void onStockButtonClick(ActionEvent event) throws IOException {
        navigateTo(event, StockController.class.getResource("/static/fxml/equipments-view.fxml"),null);
    }
    @FXML
    public void onEpiButtonClick(ActionEvent event) throws IOException {
        navigateTo(event, EpiStockController.class.getResource("/static/fxml/epis-view.fxml"),null);
    }
    @FXML
    public void onEmployeeButtonClick(ActionEvent event) throws IOException {
        navigateTo(event, EmployeeController.class.getResource("/static/fxml/employee-view.fxml"), null);
    }
}
