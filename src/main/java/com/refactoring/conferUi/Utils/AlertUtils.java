package com.refactoring.conferUi.Utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertUtils {

    private AlertUtils() {
    }

    public static void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showInfoAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static Optional<ButtonType> showWarningAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Confirmação");
        alert.setHeaderText(header);
        alert.setContentText(content);
        ButtonType yesButton = ButtonType.YES;
        ButtonType cancelButton = ButtonType.CANCEL;
        alert.getButtonTypes().setAll(cancelButton, yesButton);
        Optional<ButtonType> result = alert.showAndWait();
        return result;
    }
}
