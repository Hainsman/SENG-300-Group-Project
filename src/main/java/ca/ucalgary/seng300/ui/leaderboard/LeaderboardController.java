package ca.ucalgary.seng300.ui.leaderboard;

import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.ui.centralcontroller.CentralController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardController {
    @FXML
    private Label gameTitleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private ListView<String> leaderboardList;

    private ClientCore clientCore;

    @FXML
    public void initialize() {
        CentralController controller = CentralController.getInstance();
        this.clientCore = controller != null ? controller.getCC() : null;

        int gameId = resolveGameId();
        String gameName = gameId == Game.CONNECT_FOUR ? "Connect 4" : "Tic-Tac-Toe";
        if (gameTitleLabel != null) {
            gameTitleLabel.setText(gameName + " Leaderboard");
        }
        if (subtitleLabel != null) {
            subtitleLabel.setText("Showing top players by rank, score, and games played");
        }
        if (leaderboardList != null) {
            leaderboardList.setPlaceholder(new Label("Loading leaderboard..."));
        }

        if (clientCore != null && clientCore.isConnected()) {
            clientCore.setLeaderboardHandler(this::handleLeaderboardData);
            clientCore.msgSend(new sysMSG("LEADERBOARD_REQUEST", String.valueOf(gameId), ""));
        } else {
            if (leaderboardList != null) {
                leaderboardList.setPlaceholder(new Label("Leaderboard unavailable offline."));
            }
        }
    }

    private void handleLeaderboardData(sysMSG message) {
        if (message == null || !"LEADERBOARD_DATA".equals(message.getType())) {
            return;
        }
        if ("SUCCESS".equals(message.getFieldB())) {
            String data = message.getPayload();
            List<String> rows = new ArrayList<>();
            if (data != null && !data.isBlank()) {
                String[] entries = data.split(";");
                for (String entry : entries) {
                    if (entry.isBlank()) continue;
                    String[] parts = entry.split(":");
                    if (parts.length == 4) {
                        rows.add(parts[0] + " | Rank: " + parts[1] + " | Score: " + parts[2] + " | Games: " + parts[3]);
                    }
                }
            }
            Platform.runLater(() -> {
                if (leaderboardList != null) {
                    leaderboardList.setItems(FXCollections.observableArrayList(rows));
                    if (rows.isEmpty()) {
                        leaderboardList.setPlaceholder(new Label("No leaderboard entries available."));
                    }
                }
            });
        } else {
            Platform.runLater(() -> {
                if (leaderboardList != null) {
                    leaderboardList.setPlaceholder(new Label("Failed to load leaderboard."));
                }
            });
        }
    }

    @FXML
    void handleBack(MouseEvent event) {
        CentralController.moveTo("chooseModeNew.fxml");
    }

    private int resolveGameId() {
        String selectedGame = CentralController.selectedGame != null ? CentralController.selectedGame : "";
        return selectedGame.toLowerCase().contains("connect") ? Game.CONNECT_FOUR : Game.TICK_TACK_TOE;
    }
}
