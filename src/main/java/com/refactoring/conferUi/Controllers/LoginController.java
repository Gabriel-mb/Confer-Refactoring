package com.refactoring.conferUi.Controllers;

import com.refactoring.conferUi.Utils.AlertUtils;
import com.refactoring.conferUi.Utils.NavigationUtils;
import com.refactoring.conferUi.Services.LoginService;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;
    @FXML
    private TextField nameInput;
    @FXML
    private PasswordField passwordInput;

    @FXML
    private void initialize() {
    }

    public void onLoginButtonClick(ActionEvent event) {
        try {
            if (loginService.verification(nameInput.getText(), passwordInput.getText())) {
                NavigationUtils.navigateTo(event, SearchController.class.getResource("/static/fxml/search-view.fxml"), null);
            } else {
                AlertUtils.showErrorAlert("Informações de Acesso Incorretas!", "Tente Novamente!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Erro inesperado", "Por favor, tente novamente.");
        }
    }
}

