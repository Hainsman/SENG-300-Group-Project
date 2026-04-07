package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.util.UUID;

public class hostLobbyController {

    @FXML private Label gameLabel;
    @FXML private TextField lobbyNameField;
    @FXML private Slider playerSlider;

    @FXML private ToggleButton privateLobby;
    @FXML private ToggleButton publicLobby;
    @FXML private ToggleGroup privacyOptions;

    @FXML private Label previewGameLabel;
    @FXML private Label previewPlayersLabel;
    @FXML private Label previewPrivacyLabel;

    @FXML private Button createLobbyBtn;

    @FXML
    private void initialize() {
        String game = CentralController.selectedGame;
        if (gameLabel != null) {
            gameLabel.setText(game != null ? game : "Game");
        }
        if (previewGameLabel != null) {
            previewGameLabel.setText(game != null ? game : "-");
        }

        if (previewPlayersLabel != null && playerSlider != null) {
            previewPlayersLabel.setText(String.valueOf((int) playerSlider.getValue()));
        }
        if (previewPrivacyLabel != null) {
            previewPrivacyLabel.setText("Public");
        }

        if (playerSlider != null) {
            playerSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (previewPlayersLabel != null) {
                    previewPlayersLabel.setText(String.valueOf(newVal.intValue()));
                }
            });
        }

        if (publicLobby != null) {
            publicLobby.setSelected(true);
        }

        if (privacyOptions != null) {
            privacyOptions.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
                if (previewPrivacyLabel == null) {
                    return;
                }
                if (newVal == publicLobby) {
                    previewPrivacyLabel.setText("Public");
                } else if (newVal == privateLobby) {
                    previewPrivacyLabel.setText("Private");
                }
            });
        }
    }

    private static int serverGameKey() {
        String g = CentralController.selectedGame;
        if (g == null) {
            return Game.TICK_TACK_TOE;
        }
        String lower = g.toLowerCase();
        if (lower.contains("connect") || lower.contains("c4")) {
            return Game.CONNECT_FOUR;
        }
        if (g.contains("Tic") || g.contains("tac") || g.contains("TAC")) {
            return Game.TICK_TACK_TOE;
        }
        return Game.TICK_TACK_TOE;
    }

    @FXML
    void handleCreateLobby(ActionEvent event) {
        CentralController c = CentralController.getInstance();
        if (c == null) {
            return;
        }
        ClientCore cc = c.getCC();
        if (cc == null || !cc.isConnected()) {
            new Alert(Alert.AlertType.WARNING,
                    "Connect to the game server first.\n\nRun PlatformCore (listens on port 2020), restart this app, then try again.",
                    ButtonType.OK).showAndWait();
            return;
        }

        String lobbyNam = lobbyNameField != null && lobbyNameField.getText() != null
                ? lobbyNameField.getText().trim() : "";
        int maxPlayers = playerSlider != null ? (int) playerSlider.getValue() : 2;

        String privacy = "Public";
        if (privacyOptions != null && privacyOptions.getSelectedToggle() == privateLobby) {
            privacy = "Private";
        }

        String code = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        CentralController.maxPlayers = maxPlayers;
        CentralController.privacy = privacy;
        CentralController.lobbyCode = code;
        CentralController.lobbyName = lobbyNam.isEmpty() ? "Lobby" : lobbyNam;
        CentralController.hostingLobby = true;

        LobbyBridge.onLobbyFail = msg -> new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
        LobbyBridge.onHostOk = () -> {
            CentralController.previousScreen = "GameDashboardNew.fxml";
            CentralController.moveTo("mainLobby.fxml");
        };

        cc.msgSend(new sysMSG("HOST_LOBBY", code, String.valueOf(serverGameKey())));
    }

    @FXML
    void handleBack(ActionEvent event) {
        CentralController.moveTo("chooseModeNew.fxml");
    }
}
