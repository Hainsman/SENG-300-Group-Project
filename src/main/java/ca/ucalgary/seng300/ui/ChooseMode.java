package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.game.core.GameType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ChooseMode extends AnchorPane implements Navigable {

    @FXML private Label gameTitleLabel;

    @FXML private VBox playWithAiCard;

    public ChooseMode() {
        try {
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
        if (CentralController.selectedGame != null)
            gameTitleLabel.setText(CentralController.selectedGame.toString());
        refreshAiCardVisibility();
    }

    /** Tic-Tac-Toe from the new dashboard uses {@code "TicTacToe"}. */
    private static boolean isTicTacToeSelection() {
        return CentralController.selectedGame == GameType.TIC_TAC_TOE;
    }

    private static boolean isConnectFourSelection() {
        return CentralController.selectedGame == GameType.CONNECT4;
    }

    private void refreshAiCardVisibility() {
        boolean showAi = isTicTacToeSelection() || isConnectFourSelection();
        if (playWithAiCard != null) {
            playWithAiCard.setVisible(showAi);
            playWithAiCard.setManaged(showAi);
        }
    }

    @FXML
    void handleHostGame(MouseEvent event) {
        CentralController.ticTacToeLocalAiMode = false;
        CentralController.connect4LocalAiMode = false;
        CentralController.moveTo(Navigation.HOST_LOBBY);
    }

    @FXML
    void handleJoinGame(MouseEvent event) {
        CentralController.ticTacToeLocalAiMode = false;
        CentralController.connect4LocalAiMode = false;
        CentralController.moveTo(Navigation.JOIN_LOBBY);
    }

    @FXML
    void handlePlayWithAi(MouseEvent event) {
        if (isTicTacToeSelection()) {
            CentralController.connect4LocalAiMode = false;
            CentralController.ticTacToeLocalAiMode = true;
            CentralController.moveTo(Navigation.TTT);
            return;
        }
        if (isConnectFourSelection()) {
            CentralController.ticTacToeLocalAiMode = false;
            CentralController.connect4LocalAiMode = true;
            CentralController.moveTo(Navigation.C4);
            return;
        }
    }

    @FXML
    void handleProfileDashboard(ActionEvent event) {
        CentralController.moveTo(Navigation.PROFILE_DASHBOARD);
    }

    @FXML
    void handleTeamSettings(ActionEvent event) {
        CentralController.moveTo(Navigation.SETTINGS);
    }

    @FXML
    void handleBack(MouseEvent event) {
        CentralController.moveTo(Navigation.GAME_DASHBOARD);
    }

    @Override
    public Navigation getNavigation() {
        return Navigation.CHOOSE_MODE;
    }

    @Override
    public void reset() {
        initialize();
    }
}
