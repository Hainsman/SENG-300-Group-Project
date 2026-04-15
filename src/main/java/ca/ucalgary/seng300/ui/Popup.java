package ca.ucalgary.seng300.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import ca.ucalgary.seng300.ui.windowmanager.WindowManager;

import java.io.IOException;

/**
 * Custom themed popup that replaces JavaFX Alert dialogs.
 * Each popup creates its own Stage and WindowManager (for dragging).
 * The popup is modal and owned by the caller's window so it appears
 * on the correct monitor.
 */
public class Popup {

    public enum PopupType { INFORMATION, WARNING, ERROR, CONFIRMATION }

    private static final double POPUP_WIDTH = 440;
    private static final double POPUP_HEIGHT = 220;

    @FXML private StackPane root;
    @FXML private BorderPane titleBarPane;
    @FXML private Label titleLabel;
    @FXML private Label iconLabel;
    @FXML private Label messageLabel;
    @FXML private Button okButton;
    @FXML private Button cancelButton;

    private Stage popupStage;
    private boolean confirmed = false;

    @FXML
    private void initialize() {
        // Dummy button for WindowControls (no close/minimize/maximize in popup)
        Button dummy = new Button();

        WindowManager.WindowControls controls = new WindowManager.WindowControls(
                root, titleBarPane, titleLabel,
                dummy, dummy, dummy,
                null, null, null
        );

        WindowManager wm = new WindowManager(controls, true);
        wm.setResizable(false);

        okButton.setOnAction(e -> { confirmed = true; close(); });
        cancelButton.setOnAction(e -> close());
    }

    private void close() {
        if (popupStage != null) popupStage.close();
    }

    // ── static convenience API ──────────────────────────────────────────

    public static void showInfo(Window owner, String message) {
        show(owner, PopupType.INFORMATION, "Notice", message);
    }

    public static void showWarning(Window owner, String message) {
        show(owner, PopupType.WARNING, "Warning", message);
    }

    public static void showError(Window owner, String message) {
        show(owner, PopupType.ERROR, "Error", message);
    }

    /**
     * Shows a confirmation popup. Returns true if the user clicked OK.
     */
    public static boolean showConfirm(Window owner, String title, String header, String message) {
        return show(owner, PopupType.CONFIRMATION, title,
                (header != null && !header.isEmpty() ? header + "\n\n" : "") + message);
    }

    // ── core show method ────────────────────────────────────────────────

    private static boolean show(Window owner, PopupType type, String title, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Popup.class.getResource("/ca.ucalgary.seng300.ui/Popup.fxml"));
            StackPane popupRoot = loader.load();
            Popup controller = loader.getController();

            // Configure content
            controller.titleLabel.setText(title);
            controller.messageLabel.setText(message);
            controller.iconLabel.setText(iconFor(type));

            if (type == PopupType.CONFIRMATION) {
                controller.cancelButton.setVisible(true);
                controller.cancelButton.setManaged(true);
                controller.okButton.setText("OK");
            }

            // Build stage
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.WINDOW_MODAL);
            if (owner != null) stage.initOwner(owner);

            Scene scene = new Scene(popupRoot, POPUP_WIDTH, POPUP_HEIGHT);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setResizable(false);

            controller.popupStage = stage;

            // Center on owner window
            if (owner != null) {
                stage.setX(owner.getX() + (owner.getWidth() - POPUP_WIDTH) / 2);
                stage.setY(owner.getY() + (owner.getHeight() - POPUP_HEIGHT) / 2);
            }

            stage.showAndWait();
            return controller.confirmed;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String iconFor(PopupType type) {
        return switch (type) {
            case INFORMATION  -> "\u2139";   // i
            case WARNING      -> "\u26A0";   // warning triangle
            case ERROR        -> "\u274C";   // red x
            case CONFIRMATION -> "\u2753";   // question mark
        };
    }
}
