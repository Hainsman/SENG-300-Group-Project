package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

// CentralController purpose is to hold the central container that each page's fxml will display in.
public class CentralController {
    private static CentralController instance;

    public static final double WINDOW_MINIMUM_WIDTH = 1080;
    public static final double WINDOW_MINIMUM_HEIGHT = 730;

    @FXML private BorderPane viewContainer;
    @FXML private Label titleLabel;

    private double xOffset = 0;
    private double yOffset = 0;

    private boolean maximized = false;

    private double unMaximizedWidth;
    private double unMaximizedHeight;
    private double unMaximizedX;
    private double unMaximizedY;

    @FXML
    public void initialize() {
        instance = this;

        titleLabel.setText(MainApp.TITLE + " v" + MainApp.VERSION);

        // REPLACE WITH LOGIN ONCE CREATED!
        loadView("Login.fxml");
    }

    // call anytime to switch pages
    public static void moveTo(String fxml) {
        instance.loadView(fxml);
    }

    // private method used by navigateTo
    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ca.ucalgary.seng300.ui/" + fxml)
            );
            Node view = loader.load();
            viewContainer.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleQuitAction(ActionEvent event) {
        ((Stage) viewContainer.getScene().getWindow()).close();
    }

    @FXML
    private void handleTitlePressedAction(MouseEvent event) {
    }

    @FXML
    private void handleTitleDraggedAction(MouseEvent event) {
    }

    @FXML
    private void handleMinimizeAction(ActionEvent event) {
        ((Stage) viewContainer.getScene().getWindow()).setIconified(true);
    }

    @FXML
    private void handleMaximizeAction(ActionEvent event) {
        Stage stage = (Stage) viewContainer.getScene().getWindow();
        if (maximized) {
            stage.setWidth(unMaximizedWidth);
            stage.setHeight(unMaximizedHeight);
            stage.setX(unMaximizedX);
            stage.setY(unMaximizedY);
        }
        else {
            unMaximizedWidth = stage.getWidth();
            unMaximizedHeight = stage.getHeight();
            unMaximizedX = stage.getX();
            unMaximizedY = stage.getY();

            Screen screen = Screen.getScreensForRectangle(stage.getX(), stage.getY(), 1, 1).getFirst();
            Rectangle2D bounds = screen.getVisualBounds();

            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
        }

        maximized = !maximized;
    }
}
