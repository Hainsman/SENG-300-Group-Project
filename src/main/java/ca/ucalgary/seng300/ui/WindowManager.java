package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.MainApp;
import ca.ucalgary.seng300.ui.userdata.AppUiPreferences;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.scene.media.AudioClip;

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
            Button maximizeButton,
            Slider volumeSlider,
            Label volumeIcon,
            Label volumePercent
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

    public static final double WINDOW_MINIMUM_WIDTH = 1080;
    public static final double WINDOW_MINIMUM_HEIGHT = 840;

    private static final int RESIZE_MARGIN = 6;
    private static final int RESIZE_RIPPLE_MARGIN = 1;
    private static final int RESIZE_RIPPLE_DURATION_MILLIS = 300;

    private static final int NOTIFICATION_DURATION_MILLIS = 1000;
    private static final int NOTIFICATION_Y_OFFSET = 20;

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

    private boolean resizable = true;

    private boolean showingNotification = false;
    private boolean showingNotificationAnimation = false;
    private Label notificationLabel;

    private Slider volumeSlider;
    private Label volumeIcon;
    private Label volumePercent;
    private double settingsVolume = 1.0;
    private double volumeBeforeMute = 1.0;
    private AudioClip currentlyPlaying = null;

    public WindowManager(WindowControls controls) {
        this(controls, false);
    }

    public WindowManager(WindowControls controls, boolean minimal) {
        this.root = controls.root();
        this.titleBarPane = controls.titleBarPane();

        initStageListener();
        initEventFilters();
        initNotificationLabel();
        if (!minimal)
            initVolumeSlider(controls.volumeSlider(), controls.volumeIcon(), controls.volumePercent());

        controls.quitButton().setOnAction(this::handleQuitAction);
        controls.minimizeButton().setOnAction(this::handleMinimizeAction);
        controls.maximizeButton().setOnAction(this::handleMaximizeAction);

        if (!minimal)
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
        root.addEventFilter(MouseEvent.MOUSE_MOVED, this::handleMouseMoved);
        root.addEventFilter(MouseEvent.MOUSE_ENTERED, this::updateCursor);
        root.addEventFilter(MouseEvent.MOUSE_EXITED, this::updateCursor);
        root.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        root.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        root.addEventFilter(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
    }

    private void initNotificationLabel() {
        notificationLabel = new Label();
        notificationLabel.getStyleClass().add("notification-bar");
    }

    private void initVolumeSlider(Slider slider, Label icon, Label percent) {
        this.volumeSlider = slider;
        this.volumeIcon = icon;
        this.volumePercent = percent;
        if (slider == null) return;

        slider.setValue(settingsVolume);
        updateVolumeIcon(settingsVolume);
        updateVolumePercent(settingsVolume);

        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double v = newVal.doubleValue();
            settingsVolume = v;
            if (v > 0) volumeBeforeMute = v;
            if (v == 0 && currentlyPlaying != null) currentlyPlaying.stop();
            if (currentlyPlaying != null) currentlyPlaying.setVolume(getActualVolume());
            updateVolumeIcon(v);
            updateVolumePercent(v);
            updateTrackFill(slider, v);
        });

        // Apply initial track fill after CSS has been applied to the scene.
        slider.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> updateTrackFill(slider, slider.getValue()));
            }
        });

        if (icon != null) {
            icon.setOnMouseClicked(e -> {
                if (settingsVolume > 0) {
                    volumeBeforeMute = settingsVolume;
                    slider.setValue(0);
                } else {
                    slider.setValue(volumeBeforeMute > 0 ? volumeBeforeMute : 1.0);
                }
            });
        }

        // Hover-reveal behaviour: show only when hovering the icon itself,
        // stay open while the cursor is anywhere inside the reserved container
        // (so moving icon → slider → percent keeps it open), hide when leaving
        // that container. Stays open while actively dragging the thumb.
        Node container = (icon != null) ? icon.getParent() : null;
        if (icon != null) {
            icon.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> setVolumeControlsVisible(true));
        }
        if (container != null) {
            container.setOnMouseExited(e -> {
                if (!slider.isValueChanging()) setVolumeControlsVisible(false);
            });
            slider.valueChangingProperty().addListener((obs, was, isChanging) -> {
                if (!isChanging && !container.isHover()) setVolumeControlsVisible(false);
            });
        }
    }

    private void setVolumeControlsVisible(boolean visible) {
        if (volumeSlider != null) {
            volumeSlider.setVisible(visible);
            volumeSlider.setManaged(visible);
        }
        if (volumePercent != null) {
            volumePercent.setVisible(visible);
            volumePercent.setManaged(visible);
        }
    }

    private void updateVolumeIcon(double volume) {
        if (volumeIcon == null) return;
        if (volume <= 0.0)        volumeIcon.setText("\uD83D\uDD07"); // 🔇
        else if (volume <= 0.33)  volumeIcon.setText("\uD83D\uDD08"); // 🔈
        else if (volume <= 0.66)  volumeIcon.setText("\uD83D\uDD09"); // 🔉
        else                      volumeIcon.setText("\uD83D\uDD0A"); // 🔊
    }

    private void updateVolumePercent(double volume) {
        if (volumePercent == null) return;
        volumePercent.setText(Math.round(volume * 100) + "%");
    }

    private void updateTrackFill(Slider slider, double volume) {
        Node track = slider.lookup(".track");
        if (track == null) return;
        double pct = Math.max(0, Math.min(1, volume)) * 100.0;
        track.setStyle(String.format(java.util.Locale.US,
                "-fx-background-color: linear-gradient(to right, #22c55e 0%%, #22c55e %.2f%%, #2a2a30 %.2f%%, #2a2a30 100%%);",
                pct, pct));
    }

    private void handleQuitAction(ActionEvent event) {
        Platform.exit();
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

    private void handleMouseMoved(MouseEvent event) {
        updateCursor(event);

        if (showingNotification)
            hideNotificationWithDelay();
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

            if (resizable && !maximized && shouldBeResizedOnDrag &&
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
//        else
//            playAudioClip(SoundEffect.MOUSE_CLICKED);

        if (showingNotification)
            hideNotification();
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

    private double effectiveMinWidth() {
        return WINDOW_MINIMUM_WIDTH * AppUiPreferences.textScaleFactor;
    }

    private double effectiveMinHeight() {
        return WINDOW_MINIMUM_HEIGHT * AppUiPreferences.textScaleFactor;
    }

    private void resizeHorizontally(double dx) {
        if (resizingDirection.isRight()) {
            if (stage.getWidth() + dx >= effectiveMinWidth()) {
                stage.setWidth(stage.getWidth() + dx);
            } else {
                resizingWindow = false;
            }
        }

        if (resizingDirection.isLeft()) {
            if (stage.getWidth() - dx >= effectiveMinWidth()) {
                stage.setX(stage.getX() + dx);
                stage.setWidth(stage.getWidth() - dx);
            } else {
                resizingWindow = false;
            }
        }
    }

    private void resizeVertically(double dy) {
        if (resizingDirection.isBottom()) {
            if (stage.getHeight() + dy >= effectiveMinHeight()) {
                stage.setHeight(stage.getHeight() + dy);
            } else {
                resizingWindow = false;
            }
        }

        if (resizingDirection.isTop()) {
            if (stage.getHeight() - dy >= effectiveMinHeight()) {
                stage.setY(stage.getY() + dy);
                stage.setHeight(stage.getHeight() - dy);
            } else {
                resizingWindow = false;
            }
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
                        Duration.millis(RESIZE_RIPPLE_DURATION_MILLIS),
                        new KeyValue(ripple.radiusProperty(), 50, Interpolator.EASE_OUT),
                        new KeyValue(ripple.opacityProperty(), 0)
                )
        );

        animation.setOnFinished(e -> root.getChildren().remove(ripple));
        animation.play();
    }

    public void showNotification(String notification, boolean soundOn) {
        notificationLabel.setText(notification);

        root.getChildren().remove(notificationLabel); // added this to make sure no duplicates get added (which causes a crash)
        root.getChildren().add(notificationLabel);
        StackPane.setAlignment(notificationLabel, Pos.TOP_CENTER);

        Timeline animation = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(notificationLabel.translateYProperty(), titleBarPane.getHeight()),
                        new KeyValue(notificationLabel.opacityProperty(), 0)
                ),
                new KeyFrame(
                        Duration.millis(NOTIFICATION_DURATION_MILLIS),
                        new KeyValue(
                                notificationLabel.translateYProperty(),
                                titleBarPane.getHeight() + NOTIFICATION_Y_OFFSET ,
                                Interpolator.EASE_OUT),
                        new KeyValue(notificationLabel.opacityProperty(), 1)
                )
        );

        animation.play();
        showingNotification = true;

        if (soundOn)
            playAudioClip(SoundEffect.YOUR_TURN_NOTIFICATION);
    }

    private void hideNotification() {
        root.getChildren().remove(notificationLabel);
        showingNotification = false;
        showingNotificationAnimation = false;
    }

    private void hideNotificationWithDelay() {
        if (!showingNotificationAnimation) {
            Timeline animation = new Timeline(
                    new KeyFrame(
                            Duration.ZERO,
                            new KeyValue(notificationLabel.translateYProperty(), notificationLabel.getTranslateY()),
                            new KeyValue(notificationLabel.opacityProperty(), 1)
                    ),
                    new KeyFrame(
                            Duration.millis(NOTIFICATION_DURATION_MILLIS),
                            new KeyValue(notificationLabel.translateYProperty(), titleBarPane.getHeight(), Interpolator.EASE_OUT),
                            new KeyValue(notificationLabel.opacityProperty(), 0)
                    )
            );

            showingNotificationAnimation = true;

            animation.setOnFinished(e -> {
                root.getChildren().remove(notificationLabel);
                showingNotification = false;
                showingNotificationAnimation = false;
            });
            animation.play();
        }
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
            if (node instanceof Button || node instanceof Slider)
                return true;
            if (node instanceof Label && node.getStyleClass().contains("volume-icon"))
                return true;
            node = node.getParent();
        }

        return false;
    }

    private void updateCursor(MouseEvent event) {
        Direction dir = getResizingDirection(event);

        boolean overControl = isOverControl(event);

        if (!resizable || maximized || (overControl && dir != Direction.NONE))
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

        shouldBeResizedOnDrag = resizable && !maximized && !(overControl && dir != Direction.NONE);
    }

    private double getActualVolume() {
        return settingsVolume;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    public Stage getStage() {
        return stage;
    }

    public void playAudioClip(SoundEffect soundEffect) {
        playAudioClip(soundEffect, false);
    }

    public void playAudioClip(SoundEffect soundEffect, boolean cycle) {
        if (currentlyPlaying != null)
            currentlyPlaying.stop();
        currentlyPlaying = new AudioClip(soundEffect.getPath());
        currentlyPlaying.setVolume(getActualVolume() * soundEffect.getVolume());
        if (cycle)
            currentlyPlaying.setCycleCount(AudioClip.INDEFINITE);
        currentlyPlaying.play();
    }
}
