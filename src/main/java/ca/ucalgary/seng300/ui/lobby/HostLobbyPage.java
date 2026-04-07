package ca.ucalgary.seng300.ui.lobby;

import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.ui.CentralController;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import ca.ucalgary.seng300.ui.util.PlayerProfileHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.UUID;

public class HostLobbyPage extends AnchorPane implements Navigable {

    private CentralController rootController;

    @FXML private Label gameLabel;
    @FXML private TextField lobbyNameField;

    @FXML private ToggleButton privateLobby;
    @FXML private ToggleButton publicLobby;
    @FXML private ToggleGroup privacyOptions;

    @FXML private Label previewGameLabel;
    @FXML private Label previewPlayersLabel;
    @FXML private Label previewPrivacyLabel;

    @FXML private Button createLobbyBtn;

    public HostLobbyPage(CentralController rootController) {
        try {
            this.rootController = rootController;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ca.ucalgary.seng300.ui/" + getNavigation().getFxml()));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        GameType game = CentralController.selectedGame;
        gameLabel.setText(game != null ? game.toString() : "Game");
        previewGameLabel.setText(game != null ? game.toString() : "-");

        previewPrivacyLabel.setText("Public");

        publicLobby.setSelected(true);

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

    @FXML
    void handleCreateLobby(ActionEvent event) {
        ClientCore cc = rootController.getCC();
        if (cc == null || !cc.isConnected()) {
            new Alert(Alert.AlertType.WARNING,
                    "Connect to the game server first.\n\nRun PlatformCore (listens on port 2020), restart this app, then try again.",
                    ButtonType.OK).showAndWait();
            return;
        }

        String lobbyNam = lobbyNameField != null && lobbyNameField.getText() != null
                ? lobbyNameField.getText().trim() : "";
        int maxPlayers = 2;

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
        CentralController.clearLobbyRoster();

        LobbyBridge.onLobbyFail = msg -> new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
        LobbyBridge.onHostOk = () -> {
            CentralController.moveTo(Navigation.MAIN_LOBBY);
            CentralController.previousScreen = Navigation.GAME_DASHBOARD;
        };

        cc.msgSend(new sysMSG("HOST_LOBBY", code, CentralController.selectedGame.toString(), PlayerProfileHelper.lobbyPayload()));
    }

    @FXML
    void handleBack(ActionEvent event) {
        CentralController.moveTo(Navigation.CHOOSE_MODE);
    }

    @Override
    public Navigation getNavigation() {
        return Navigation.HOST_LOBBY;
    }

    @Override
    public void reset() {
        initialize();

        lobbyNameField.clear();
    }
}
