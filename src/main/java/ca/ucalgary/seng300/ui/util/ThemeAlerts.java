package ca.ucalgary.seng300.ui.util;

import ca.ucalgary.seng300.ui.CentralController;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.stage.Window;

/**
 * Applies the app dark theme to {@link Alert} dialogs (stylesheet + style class + optional owner window).
 */
public final class ThemeAlerts {

    private static final String STYLESHEET = ThemeAlerts.class.getResource(
            "/ca.ucalgary.seng300.ui/stylesheets/alert-dialog.css").toExternalForm();

    private ThemeAlerts() {}

    public static void apply(Alert alert) {
        apply(alert, CentralController.getAppWindow());
    }

    public static void apply(Alert alert, Window owner) {
        DialogPane pane = alert.getDialogPane();
        if (!pane.getStylesheets().contains(STYLESHEET)) {
            pane.getStylesheets().add(STYLESHEET);
        }
        if (!pane.getStyleClass().contains("bogo-dialog")) {
            pane.getStyleClass().add("bogo-dialog");
        }
        if (owner != null) {
            alert.initOwner(owner);
        }
    }
}
