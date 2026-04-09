package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.domain.SessionHistory;
import ca.ucalgary.seng300.platform.store.SessionHistoryStore;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class ChooseModeController {

    @FXML
    private Label gameTitleLabel;

    @FXML
    private Label aiOpponentLabel;

    @FXML
    public void initialize() {

        gameTitleLabel.setText(CentralController.selectedGame);
        aiOpponentLabel.setText("Random opponent — " + CentralController.selectedGame);

    }

    @FXML
    void handleHostGame(MouseEvent event) {
        CentralController.playWithAi = false;
        CentralController.moveTo("hostLobby.fxml");
    }

    @FXML
    void handleJoinGame(MouseEvent event) {
        CentralController.playWithAi = false;
        CentralController.moveTo("joinLobby.fxml");
    }

    @FXML
    void handlePlayWithAi(MouseEvent event) {
        CentralController.playWithAi = true;
        String game = CentralController.selectedGame != null ? CentralController.selectedGame : "";
        if (game.toLowerCase().contains("connect")) {
            CentralController.moveTo("Connect4.fxml");
            return;
        }
        CentralController.moveTo("TicTacToe.fxml");
    }

    @FXML
    void handleOpenLeaderboard(MouseEvent event) {
        CentralController.moveTo("Leaderboard.fxml");
    }

    @FXML
    void handleOpenSettings(MouseEvent event) {
        CentralController.previousScreen = "chooseModeNew.fxml";
        CentralController.moveTo("Settings.fxml");
    }

    @FXML
    void handleBack(MouseEvent event) {
        CentralController.moveTo("GameDashboardNew.fxml");
    }

    @FXML
    void handleLogout(MouseEvent event) {
        if (CentralController.getInstance().clientCore != null) {
            String cid = CentralController.getInstance().clientCore.CID;
            if (cid != null && !cid.isEmpty()) {
                try {
                    int userId = Integer.parseInt(cid);
                    SessionHistoryStore store = SessionHistoryStore.getInstance();
                    List<SessionHistory> sessions = store.findSessionsByUserID(userId);
                    for (SessionHistory session : sessions) {
                        store.delete(new SessionHistory(session.getID(), session.getUserId(), session.getGloballyUniqueSessionIdentifier()));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            CentralController.getInstance().clientCore.sendLogout();
        }
        CentralController.moveTo("Login.fxml");
    }
}
