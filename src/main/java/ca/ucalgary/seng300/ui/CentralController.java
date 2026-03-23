package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.MainApp;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

// CentralController purpose is to hold the central container that each page's fxml will display in.
public class CentralController {

    private static CentralController instance;

    public static final double WINDOW_MINIMUM_WIDTH = 1080;
    public static final double WINDOW_MINIMUM_HEIGHT = 730;

    private static final int RESIZE_MARGIN = 6;
    private static final int RESIZE_RIPPLE_MARGIN = 1;

    @FXML private StackPane root;
    @FXML private BorderPane viewContainer;
    @FXML private BorderPane titleBarPane;
    @FXML private Label titleLabel;

    private double xOffset = 0;
    private double yOffset = 0;
    private double xBeforeDragging = 0;
    private double yBeforeDragging = 0;
    private boolean draggingWindow = false;

    private boolean maximized = false;

    /**
     * basically should the window maximize when the mouse left button is released
     * */
    private boolean shouldBeMaximizedOnRelease = false;

    private double unMaximizedWidth;
    private double unMaximizedHeight;
    private double unMaximizedX;
    private double unMaximizedY;

    @FXML
    public void initialize() {
        instance = this;

        root.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        root.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        root.addEventFilter(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);

        titleLabel.setText(MainApp.TITLE + " v" + MainApp.VERSION);

        // REPLACE WITH LOGIN ONCE CREATED!
        loadView("GameGUI.fxml");
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
        ((Stage) root.getScene().getWindow()).close();
    }

    private void handleMousePressed(MouseEvent event) {
        if (!isOverControl(event)) {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();

            Point2D titleBarCoords = titleBarPane.localToScene(0, 0);
            double xMin = titleBarCoords.getX();
            double xMax = titleBarPane.getWidth() + xMin;
            double yMin = titleBarCoords.getY();
            double yMax = titleBarPane.getHeight() + yMin;

            if (xMin <= xOffset && xOffset <= xMax && yMin <= yOffset && yOffset <= yMax) {
                Stage stage = (Stage) root.getScene().getWindow();
                xBeforeDragging = stage.getX();
                yBeforeDragging = stage.getY();
                draggingWindow = true;
            }
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();

        if (draggingWindow)
            dragWindow(event, stage);
    }

    private void handleMouseReleased(MouseEvent event) {
        if (shouldBeMaximizedOnRelease) {
            maximizeWindow();
            shouldBeMaximizedOnRelease = false;
        }

        draggingWindow = false;
    }

    @FXML
    private void handleMinimizeAction(ActionEvent event) {
        ((Stage) root.getScene().getWindow()).setIconified(true);
    }

    @FXML
    private void handleMaximizeAction(ActionEvent event) {
        if (maximized)
            unMaximizeWindow();
        else
            maximizeWindow();
    }

    private void dragWindow(MouseEvent event, Stage stage) {
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(Math.max(event.getScreenY() - yOffset, 0));

        if (maximized) {
            maximized = false;
            unMaximizedX = stage.getX();
            unMaximizedY = stage.getY();
        }

        if (event.getScreenY() < RESIZE_RIPPLE_MARGIN && !shouldBeMaximizedOnRelease) {
            createRipple(event.getSceneX(), event.getSceneY());
            shouldBeMaximizedOnRelease = true;
        }

        if (event.getScreenY() > RESIZE_MARGIN)
            shouldBeMaximizedOnRelease = false;
    }

    private void unMaximizeWindow() {
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setWidth(unMaximizedWidth);
        stage.setHeight(unMaximizedHeight);
        stage.setX(unMaximizedX);
        stage.setY(unMaximizedY);

        maximized = false;
    }

    private void maximizeWindow() {
        Stage stage = (Stage) root.getScene().getWindow();

        unMaximizedWidth = stage.getWidth();
        unMaximizedHeight = stage.getHeight();
        unMaximizedX = shouldBeMaximizedOnRelease ? xBeforeDragging : stage.getX();
        unMaximizedY = shouldBeMaximizedOnRelease ? yBeforeDragging : stage.getY();

        Rectangle2D stageBounds = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());

        ObservableList<Screen> screens =
                Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());

        Rectangle2D bounds = getMostIntersectingBounds(screens, stageBounds);

        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());

        maximized = true;
    }

    private Rectangle2D getMostIntersectingBounds(ObservableList<Screen> screens, Rectangle2D stageBounds) {
        double bestArea = 0;
        Rectangle2D bounds = null;
        for (Screen screen : screens) {
            Rectangle2D screenBounds = screen.getVisualBounds();
            double xOverlap = Math.max(0,
                    Math.min(stageBounds.getMaxX(), screenBounds.getMaxX()) -
                    Math.max(stageBounds.getMinX(), screenBounds.getMinX())
            );
            double yOverlap = Math.max(0,
                    Math.min(stageBounds.getMaxY(), screenBounds.getMaxY()) -
                    Math.max(stageBounds.getMinY(), screenBounds.getMinY())
            );
            double area = xOverlap * yOverlap;

            if (area > bestArea) {
                bounds = screenBounds;
                bestArea = area;
            }
        }
        return bounds;
    }

    private void createRipple(double x, double y) {
        Circle ripple = new Circle(0, Color.TRANSPARENT);
        ripple.setStroke(Color.WHITE);
        ripple.setStrokeWidth(2);
        ripple.setManaged(false);
        ripple.setTranslateX(x);
        ripple.setTranslateY(y);

        root.getChildren().add(ripple);

        Timeline animation = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(ripple.radiusProperty(), 0),
                        new KeyValue(ripple.opacityProperty(), 0.6)
                ),
                new KeyFrame(
                        Duration.millis(300),
                        new KeyValue(ripple.radiusProperty(), 50, Interpolator.EASE_OUT),
                        new KeyValue(ripple.opacityProperty(), 0)
                )
        );

        animation.setOnFinished(e -> root.getChildren().remove(ripple));
        animation.play();
    }

    private boolean isOverControl(MouseEvent event) {
        Node node = (Node) event.getTarget();

        while (node != null) {
            if (node instanceof Button)
                return true;
            node = node.getParent();
        }

        return false;
    }
}
