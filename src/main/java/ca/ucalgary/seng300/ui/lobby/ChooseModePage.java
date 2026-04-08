package ca.ucalgary.seng300.ui.lobby;

import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.ui.CentralController;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ChooseModePage extends AnchorPane implements Navigable {

    private CentralController rootController;

    @FXML private Label gameTitleLabel;

    @FXML private VBox playWithAiCard;

    public ChooseModePage(CentralController rootController) {
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
        if (rootController.selectedGame != null)
            gameTitleLabel.setText(rootController.selectedGame.toString());
        refreshAiCardVisibility();
    }

    /** Tic-Tac-Toe from the new dashboard uses {@code "TicTacToe"}. */
    private boolean isTicTacToeSelection() {
        return rootController.selectedGame == GameType.TIC_TAC_TOE;
    }

    private boolean isConnectFourSelection() {
        return rootController.selectedGame == GameType.CONNECT4;
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
        rootController.localAiMode = false;
        rootController.moveTo(Navigation.HOST_LOBBY);
    }

    @FXML
    void handleJoinGame(MouseEvent event) {
        rootController.localAiMode = false;
        rootController.moveTo(Navigation.JOIN_LOBBY);
    }

    @FXML
    void handlePlayWithAi(MouseEvent event) {
        rootController.localAiMode = true;
        if (isTicTacToeSelection())
            rootController.moveTo(Navigation.TTT);
        else if (isConnectFourSelection())
            rootController.moveTo(Navigation.C4);
    }

    @FXML
    void handleProfileDashboard(ActionEvent event) {
        rootController.moveTo(Navigation.PROFILE_DASHBOARD);
    }

    @FXML
    void handleTeamSettings(ActionEvent event) {
        rootController.moveTo(Navigation.SETTINGS);
    }

    @FXML
    void handleBack(MouseEvent event) {
        rootController.moveTo(Navigation.GAME_DASHBOARD);
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
