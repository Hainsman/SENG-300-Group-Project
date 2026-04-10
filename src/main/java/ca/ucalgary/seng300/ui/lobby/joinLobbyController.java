package ca.ucalgary.seng300.ui.lobby;

import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.ui.centralcontroller.CentralController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class joinLobbyController {

    @FXML private Label gameTitleLabel;

    @FXML private TextField codeField;
    @FXML private Button joinByCodeBtn;

    @FXML private TextField searchField;
    @FXML private ListView<String> lobbyList;

    @FXML
    public void initialize() {
        if (gameTitleLabel != null) {
            String g = CentralController.selectedGame;
            gameTitleLabel.setText(g != null ? g : "Game");
        }

        if (lobbyList != null) {
            lobbyList.getItems().clear();
            lobbyList.setPlaceholder(new Label("Public lobby browser is not available yet.\nEnter a code from your host above."));
        }

        if (joinByCodeBtn != null) {
            joinByCodeBtn.setOnAction(e -> handleJoinByCode());
        }

        if (lobbyList != null) {
            lobbyList.setOnMouseClicked(this::onLobbyListClick);
        }
    }

    private static int selectedGameId() {
        String g = CentralController.selectedGame;
        if (g == null) {
            return Game.TICK_TACK_TOE;
        }
        String lower = g.toLowerCase();
        if (lower.contains("connect") || lower.contains("c4")) {
            return Game.CONNECT_FOUR;
        }
        if (lower.contains("tic") || lower.contains("tac")) {
            return Game.TICK_TACK_TOE;
        }
        return Game.TICK_TACK_TOE;
    }

    private void onLobbyListClick(MouseEvent event) {
        if (event.getClickCount() != 2) {
            return;
        }
        String selected = lobbyList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            joinLobbyFromList(selected);
        }
    }

    @FXML
    private void handleJoinByCode() {
        if (codeField == null) {
            return;
        }
        String raw = codeField.getText() != null ? codeField.getText().trim() : "";
        if (raw.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Enter the lobby code your host shared.", ButtonType.OK).showAndWait();
            return;
        }
        attemptJoin(raw.toUpperCase());
    }

    private void attemptJoin(String code) {
        CentralController c = CentralController.getInstance();
        if (c == null) {
            return;
        }
        ClientCore cc = c.getCC();
        if (cc == null || !cc.isConnected()) {
            new Alert(Alert.AlertType.WARNING,
                    "Connect to the game server first.\n\nRun PlatformCore (port 2020), restart this app, then try again.",
                    ButtonType.OK).showAndWait();
            return;
        }

        CentralController.hostingLobby = false;
        CentralController.lobbyCode = code;
        CentralController.lobbyPaired = false;
        CentralController.maxPlayers = 2;
        CentralController.privacy = "Public";
        CentralController.player1Name = "";
        CentralController.player2Name = "";

        LobbyBridge.onLobbyFail = msg -> new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
        LobbyBridge.onJoinOk = () -> {
            CentralController.previousScreen = "joinLobby.fxml";
            CentralController.moveTo("mainLobby.fxml");
        };

        System.out.println("JOIN_LOBBY sent for code " + code + " gameId=" + selectedGameId());
        cc.msgSend(new sysMSG("JOIN_LOBBY", code, String.valueOf(selectedGameId())));
    }

    private void joinLobbyFromList(String lobby) {
        String code = lobby.trim().toUpperCase();
        String[] parts = lobby.split("-");
        if (parts.length >= 2) {
            code = parts[parts.length - 1].trim().toUpperCase();
        }
        if (codeField != null) {
            codeField.setText(code);
        }
        attemptJoin(code);
    }

    @FXML
    private void handleBack() {
        CentralController.moveTo("chooseModeNew.fxml");
    }
}
