package ca.ucalgary.seng300.ui.lobby;

import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.ui.CentralController;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import ca.ucalgary.seng300.ui.util.PlayerProfileHelper;
import ca.ucalgary.seng300.ui.util.ThemeAlerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class JoinLobbyPage extends AnchorPane implements Navigable {

    private CentralController rootController;

    @FXML private Label gameTitleLabel;

    @FXML private TextField codeField;
    @FXML private Button joinByCodeBtn;

    @FXML private TextField searchField;
    @FXML private ListView<String> lobbyList;

    public JoinLobbyPage(CentralController rootController) {
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
    public void initialize() {
        if (gameTitleLabel != null) {
            GameType g = CentralController.selectedGame;
            gameTitleLabel.setText(g != null ? g.toString() : "Game");
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
            Alert empty = new Alert(Alert.AlertType.INFORMATION, "Enter the lobby code your host shared.", ButtonType.OK);
            ThemeAlerts.apply(empty);
            empty.showAndWait();
            return;
        }
        attemptJoin(raw.toUpperCase());
    }

    private void attemptJoin(String code) {
        ClientCore cc = rootController.getCC();
        if (cc == null || !cc.isConnected()) {
            Alert warn = new Alert(Alert.AlertType.WARNING,
                    "Connect to the game server first.\n\nRun PlatformCore (port 2020), restart this app, then try again.",
                    ButtonType.OK);
            ThemeAlerts.apply(warn);
            warn.showAndWait();
            return;
        }

        CentralController.hostingLobby = false;
        CentralController.lobbyCode = code;
        CentralController.lobbyPaired = false;
        CentralController.maxPlayers = 2;
        CentralController.privacy = "Public";
        CentralController.clearLobbyRoster();

        cc.msgSend(new sysMSG("JOIN_LOBBY", code, "", PlayerProfileHelper.lobbyPayload()));
    }

    public void lobbyJoin(String a, String b){
        if(a.equals("OK")){
            CentralController.moveTo(Navigation.MAIN_LOBBY);
        }
        else {
            Alert err = new Alert(Alert.AlertType.ERROR, b, ButtonType.OK);
            ThemeAlerts.apply(err);
            err.showAndWait();
        }
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
        CentralController.moveTo(Navigation.CHOOSE_MODE);
    }

    @Override
    public Navigation getNavigation() {
        return Navigation.JOIN_LOBBY;
    }

    @Override
    public void reset() {
        initialize();

        codeField.clear();
    }
}
