package ca.ucalgary.seng300.ui.games;

import ca.ucalgary.seng300.game.client.ClientState;
import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.game.core.tictactoe.TicTacToeRandomMoveGenerator;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.session.TicTacToeSession;
import ca.ucalgary.seng300.ui.*;
import ca.ucalgary.seng300.ui.util.EloOverlayUtil;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import ca.ucalgary.seng300.ui.util.OnlineRankingHelper;
import ca.ucalgary.seng300.ui.util.ThemeAlerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;

import java.io.IOException;

public class TTTPage extends CommonGameLogic implements Navigable {

    public TTTPage(CentralController rootController, GameGUIPage parentGUI) {
        super(
                rootController,
                parentGUI,
                3,
                3,
                GameType.TIC_TAC_TOE,
                "ttt-cell",
                Color.web("#3B82F6"),
                Color.web("#A855F7"),
                "X",
                "O"
        );

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ca.ucalgary.seng300.ui/" + getNavigation().getFxml()));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Navigation getNavigation() {
        return Navigation.TTT;
    }

    @Override
    protected void updateButton(Button button, Player player) {
        button.setDisable(player != Player.EMPTY);
        if (player == Player.PLAYER_1) {
            button.setText("X");
            button.setStyle("-fx-text-fill:#3B82F6; -fx-font-size:40px; -fx-font-weight:bold;");
        } else if (player == Player.PLAYER_2) {
            button.setText("O");
            button.setStyle("-fx-text-fill:#A855F7; -fx-font-size:40px; -fx-font-weight:bold;");
        } else {
            button.setText("");
            button.setStyle("");
        }
    }

    @Override
    protected void setNewSession() {
        session = new TicTacToeSession();
    }

    private void navigateBackFromGame() {
        rootController.localAiMode = false;
        //randomOpponentEnabled = false;
        EloOverlayUtil.hide(eloResultOverlay);
        if (rootController.previousScreen != null) {
            rootController.moveTo(rootController.previousScreen);
        } else {
            rootController.moveTo(Navigation.GAME_DASHBOARD);
        }
    }

    @Override
    public void refreshFromGameState(GameState gameState) {
        super.refreshFromGameState(gameState);
    }
}