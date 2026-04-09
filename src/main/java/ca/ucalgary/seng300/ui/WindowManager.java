package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.MainApp;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
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

public class WindowManager {
    /**
     * While not strictly necessary, this is a nice way to keep track what the window controls are
     *
     * @param root the main component of window
     * @param titleBarPane the pane which the title bar resizes
     * @param titleLabel the label for the title
     * @param quitButton the quit button for terminating the window
     * @param minimizeButton the minimize button for hiding the window
     * @param maximizeButton the maximize button for enlarging the window
     */
    public record WindowControls(
            StackPane root,
            BorderPane titleBarPane,
            Label titleLabel,
            Button quitButton,
            Button minimizeButton,
            Button maximizeButton
    ) { }

    private enum Direction {
        NW,
        NE,
        SW,
        SE,
        N,
        S,
        W,
        E,
        NONE;

        public boolean isLeft() {
            return  this == Direction.W ||
                    this == Direction.NW ||
                    this == Direction.SW;
        }
        public boolean isRight() {
            return  this == Direction.E ||
                    this == Direction.NE ||
                    this == Direction.SE;
        }
        public boolean isTop() {
            return  this == Direction.N ||
                    this == Direction.NW ||
                    this == Direction.NE;
        }
        public boolean isBottom() {
            return  this == Direction.S ||
                    this == Direction.SW ||
                    this == Direction.SE;
        }
    }

    public static final double WINDOW_MINIMUM_WIDTH = 1360;
    public static final double WINDOW_MINIMUM_HEIGHT = 860;

    private static final int RESIZE_MARGIN = 6;
    private static final int RESIZE_RIPPLE_MARGIN = 1;

    private final StackPane root;
    private final BorderPane titleBarPane;
    private Stage stage;

    private double xOffset = 0;
    private double yOffset = 0;
    private double lastX = 0;
    private double lastY = 0;
    private double xBeforeDragging = 0;
    private double yBeforeDragging = 0;
    private boolean draggingWindow = false;
    private boolean resizingWindow = false;
    private Direction resizingDirection = Direction.NONE;

    private boolean shouldBeMaximizedOnRelease = false;
    private boolean shouldBeResizedOnDrag = false;

    private boolean maximized = false;

    private double unMaximizedWidth;
    private double unMaximizedHeight;
    private double unMaximizedX;
    private double unMaximizedY;

    public WindowManager(WindowControls controls) {
        this.root = controls.root();
        this.titleBarPane = controls.titleBarPane();

        initStageListener();
        initEventFilters();

        controls.quitButton().setOnAction(this::handleQuitAction);
        controls.minimizeButton().setOnAction(this::handleMinimizeAction);
        controls.maximizeButton().setOnAction(this::handleMaximizeAction);

        controls.titleLabel().setText(MainApp.TITLE + " v" + MainApp.VERSION);
    }

    private void initStageListener() {
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        this.stage = (Stage) newWindow;
                    }
                });
            }
        });
    }

    private void initEventFilters() {
        root.addEventFilter(MouseEvent.MOUSE_MOVED, this::updateCursor);
        root.addEventFilter(MouseEvent.MOUSE_ENTERED, this::updateCursor);
        root.addEventFilter(MouseEvent.MOUSE_EXITED, this::updateCursor);
        root.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        root.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        root.addEventFilter(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
    }

    private void handleQuitAction(ActionEvent event) {
        stage.close();
    }

    private void handleMinimizeAction(ActionEvent event) {
        stage.setIconified(true);
    }

    private void handleMaximizeAction(ActionEvent event) {
        if (maximized)
            unMaximizeWindow();
        else
            maximizeWindow();
    }

    private void handleMousePressed(MouseEvent event) {
        if (!isOverControl(event)) {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
            lastX = event.getScreenX();
            lastY = event.getScreenY();

            Point2D titleBarCoords = titleBarPane.localToScene(0, 0);
            double xMin = titleBarCoords.getX();
            double xMax = titleBarPane.getWidth() + xMin;
            double yMin = titleBarCoords.getY();
            double yMax = titleBarPane.getHeight() + yMin;

            if (!maximized && shouldBeResizedOnDrag &&
                    (isNearLeftEdge(event) ||
                            isNearRightEdge(event) ||
                            isNearBottomEdge(event) ||
                            isNearTopEdge(event))) {
                resizingWindow = true;
                resizingDirection = getResizingDirection(event);
            }

            else if (xMin <= xOffset && xOffset <= xMax && yMin <= yOffset && yOffset <= yMax) {
                xBeforeDragging = stage.getX();
                yBeforeDragging = stage.getY();
                draggingWindow = true;
            }
        }
    }

    private void handleMouseDragged(MouseEvent event) {

        if (draggingWindow)
            dragWindow(event);
        else if (resizingWindow)
            resizeWindow(event);
    }

    private void handleMouseReleased(MouseEvent event) {
        if (shouldBeMaximizedOnRelease) {
            maximizeWindow();
            shouldBeMaximizedOnRelease = false;
        }

        draggingWindow = false;
        resizingWindow = false;
    }

    private void dragWindow(MouseEvent event) {
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

    private void resizeHorizontally(double dx) {
        if (resizingDirection.isRight()) {
            if (stage.getWidth() + dx >= WINDOW_MINIMUM_WIDTH)
                stage.setWidth(stage.getWidth() + dx);
            else
                resizingWindow = false;
        }

        if (resizingDirection.isLeft()) {
            if (stage.getWidth() - dx >= WINDOW_MINIMUM_WIDTH) {
                stage.setX(stage.getX() + dx);
                stage.setWidth(stage.getWidth() - dx);
            }
            else
                resizingWindow = false;
        }
    }

    private void resizeVertically(double dy) {
        if (resizingDirection.isBottom()) {
            if (stage.getHeight() + dy >= WINDOW_MINIMUM_HEIGHT)
                stage.setHeight(stage.getHeight() + dy);
            else
                resizingWindow = false;
        }

        if (resizingDirection.isTop()) {
            if (stage.getHeight() - dy >= WINDOW_MINIMUM_HEIGHT) {
                stage.setY(stage.getY() + dy);
                stage.setHeight(stage.getHeight() - dy);
            }
            else
                resizingWindow = false;
        }
    }

    private void resizeWindow(MouseEvent event) {
        double mouseX = event.getScreenX();
        double mouseY = Math.max(event.getScreenY(), 0);
        double dx = mouseX - lastX;
        double dy = mouseY - lastY;

        resizeHorizontally(dx);
        resizeVertically(dy);

        if (!resizingWindow)
            root.setCursor(Cursor.DEFAULT);

        lastX = mouseX;
        lastY = mouseY;
    }

    private void unMaximizeWindow() {
        stage.setWidth(unMaximizedWidth);
        stage.setHeight(unMaximizedHeight);
        stage.setX(unMaximizedX);
        stage.setY(unMaximizedY);

        maximized = false;
    }

    private void maximizeWindow() {
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

    private boolean isNearRightEdge(MouseEvent event) {
        return event.getSceneX() >= stage.getWidth() - RESIZE_MARGIN;
    }

    private boolean isNearLeftEdge(MouseEvent event) {
        return event.getSceneX() <= RESIZE_MARGIN;
    }

    private boolean isNearBottomEdge(MouseEvent event) {
        return event.getSceneY() >= stage.getHeight() - RESIZE_MARGIN;
    }

    private boolean isNearTopEdge(MouseEvent event) {
        return event.getSceneY() <= RESIZE_MARGIN;
    }

    private Direction getResizingDirection(MouseEvent event) {
        boolean left = isNearLeftEdge(event);
        boolean right = isNearRightEdge(event);
        boolean up = isNearTopEdge(event);
        boolean down = isNearBottomEdge(event);

        if (up && left)         return Direction.NW;
        else if (up && right)   return Direction.NE;
        else if (down && left)  return Direction.SW;
        else if (down && right) return Direction.SE;
        else if (up)            return Direction.N;
        else if (down)          return Direction.S;
        else if (left)          return Direction.W;
        else if (right)         return Direction.E;

        return Direction.NONE;
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

    private void updateCursor(MouseEvent event) {
        Direction dir = getResizingDirection(event);

        boolean overControl = isOverControl(event);

        if (maximized || (overControl && dir != Direction.NONE))
            root.setCursor(Cursor.DEFAULT);
        else {
            switch (dir) {
                case NW, SE:
                    root.setCursor(Cursor.NW_RESIZE);
                    break;
                case NE, SW:
                    root.setCursor(Cursor.NE_RESIZE);
                    break;
                case N, S:
                    root.setCursor(Cursor.N_RESIZE);
                    break;
                case W, E:
                    root.setCursor(Cursor.E_RESIZE);
                    break;
                case NONE:
                    root.setCursor(Cursor.DEFAULT);
                    break;
            }
        }

        shouldBeResizedOnDrag = !maximized && !(overControl && dir != Direction.NONE);
    }
}
