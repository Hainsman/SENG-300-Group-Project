package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.services.RankingService;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public final class EloOverlayUtil {

    private EloOverlayUtil() {}

    public static void show(VBox overlay, Label title, Label deltaLabel, Label newRatingLabel, RankingService.OnlineEloResult r) {
        if (title == null || deltaLabel == null || newRatingLabel == null) {
            return;
        }
        title.setText(switch (r.outcome()) {
            case WIN -> "Victory!";
            case LOSS -> "Defeat";
            case DRAW -> "Draw";
        });
        int d = r.deltaElo();
        String sign = d >= 0 ? "+" : "";
        deltaLabel.setText("Rating change: " + sign + d + " ELO");
        if (d > 0) {
            deltaLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: #4ade80;");
        } else if (d < 0) {
            deltaLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: #f87171;");
        } else {
            deltaLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: #e5e5e5;");
        }
        newRatingLabel.setText("New rating: " + r.newElo());
        if (overlay != null) {
            overlay.setVisible(true);
            overlay.setMouseTransparent(false);
        }
    }

    public static void hide(VBox overlay) {
        if (overlay != null) {
            overlay.setVisible(false);
            overlay.setMouseTransparent(true);
        }
    }
}
