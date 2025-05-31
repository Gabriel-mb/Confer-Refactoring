package com.refactoring.conferUi.Utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

@Controller
public class NavigationUtils {
    private static ApplicationContext applicationContext;

    private NavigationUtils(ApplicationContext applicationContext) {
        NavigationUtils.applicationContext = applicationContext;
    }

    @FXML
    public static void navigateTo(ActionEvent event, URL fxmlResource, Consumer<Object> controllerConsumer)
            throws IOException {

        FXMLLoader loader = new FXMLLoader(fxmlResource);
        loader.setControllerFactory(applicationContext::getBean);
        Parent parent = loader.load();

        if (controllerConsumer != null) {
            controllerConsumer.accept(loader.getController());
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(parent);
    }

    public static void handleAnchorPaneDrag(MouseEvent event, AnchorPane anchorPane, double[] coordinates) {
        Stage stage = (Stage) anchorPane.getScene().getWindow();

        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            coordinates[0] = event.getSceneX(); // x
            coordinates[1] = event.getSceneY(); // y
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            stage.setX(event.getScreenX() - coordinates[0]);
            stage.setY(event.getScreenY() - coordinates[1]);
        }
    }
}
