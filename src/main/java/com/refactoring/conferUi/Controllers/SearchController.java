package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Utils.AlertUtils;
import com.refactoring.conferUi.Services.EmployeeService;
import com.refactoring.conferUi.Utils.CardReaderListener;
import com.refactoring.conferUi.Utils.NavigationUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Controller
public class SearchController {
    @Autowired
    private EmployeeService employeeService;

    @FXML
    public AnchorPane anchorPane;

    @FXML
    private TextField employeeId;

    private final double[] coordinates = new double[2];
    private Thread readerThread;
    private CardReaderListener cardReaderListener;

    @FXML
    private void initialize() {
        startCardReaderListener();
    }

    private void startCardReaderListener() {
        try {
            var factory = javax.smartcardio.TerminalFactory.getDefault();
            List<javax.smartcardio.CardTerminal> terminals = factory.terminals().list();

            if (!terminals.isEmpty()) {
                var terminal = terminals.get(0);
                cardReaderListener = new CardReaderListener(terminal, employeeIdValue -> {
                    Platform.runLater(() -> {
                        this.employeeId.setText(employeeIdValue);
                        ActionEvent fakeEvent = new ActionEvent(anchorPane, null);
                        processEmployeeId(employeeIdValue, fakeEvent);
                    });
                });

                readerThread = new Thread(cardReaderListener);
                readerThread.setDaemon(true);
                readerThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopListener() {
        if (cardReaderListener != null) {
            cardReaderListener.stop();
        }
        if (readerThread != null && readerThread.isAlive()) {
            try {
                readerThread.join(2000); // espera até 2s para a thread terminar
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processEmployeeId(String id, ActionEvent event) {
        if (id == null || id.length() != 8) {
            AlertUtils.showErrorAlert("Matrícula deve ter 8 dígitos!", "Tente Novamente!");
            return;
        }
        try {
            if (employeeService.readId(Integer.parseInt(id)) == null) {
                AlertUtils.showErrorAlert("Nenhum funcionário encontrado!", "Tente Novamente!");
                return;
            }
            try {
                NavigationUtils.navigateTo(event, CardController.class.getResource("/static/fxml/patCard-view.fxml"), controller -> {
                    if (controller instanceof CardController cardController) {
                        try {
                            stopListener();
                            cardController.setTableEmployee(id);
                        } catch (SQLException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            AlertUtils.showErrorAlert("Erro ao buscar funcionário!", "Tente Novamente!");
        }
    }

    @FXML
    public void onSearchButtonClick(ActionEvent event) {
        processEmployeeId(employeeId.getText(), event);
    }

    @FXML
    public void onInventoryButtonClick(ActionEvent event) throws IOException {
        NavigationUtils.navigateTo(event, InventoryController.class.getResource("/static/fxml/patrimony-view.fxml"), null);
    }

    @FXML
    public void onStockButtonClick(ActionEvent event) throws IOException {
        NavigationUtils.navigateTo(event, StockController.class.getResource("/static/fxml/equipments-view.fxml"), null);
    }

    @FXML
    public void onEpiButtonClick(ActionEvent event) throws IOException {
        NavigationUtils.navigateTo(event, EpiStockController.class.getResource("/static/fxml/epis-view.fxml"), null);
    }

    @FXML
    public void onEmployeeButtonClick(ActionEvent event) throws IOException {
        NavigationUtils.navigateTo(event, EmployeeController.class.getResource("/static/fxml/employee-view.fxml"), null);
    }
}
