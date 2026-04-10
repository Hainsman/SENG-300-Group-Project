package ca.ucalgary.seng300.ui.lobby;

import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.ui.CentralController;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import ca.ucalgary.seng300.ui.util.PlayerStatsLeaderboardHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class ChooseModePage extends AnchorPane implements Navigable {

    @FXML private Label gameTitleLabel;

    @FXML private VBox playWithAiCard;
    @FXML private VBox leaderboardRows;

    public ChooseModePage() {
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
        refreshLeaderboard();
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

    private void refreshLeaderboard() {
        if (leaderboardRows == null || CentralController.selectedGame == null) {
            return;
        }
        leaderboardRows.getChildren().clear();
        List<PlayerStatsLeaderboardHelper.Entry> top = PlayerStatsLeaderboardHelper.topEntries(CentralController.selectedGame, 5);
        if (top.isEmpty()) {
            Label empty = new Label("No leaderboard entries yet.");
            empty.getStyleClass().add("leaderboard-empty");
            leaderboardRows.getChildren().add(empty);
            return;
        }
        int position = 1;
        for (PlayerStatsLeaderboardHelper.Entry entry : top) {
            HBox row = new HBox(8);
            row.getStyleClass().add("leaderboard-row");
            Label rankLabel = new Label("#" + position++);
            rankLabel.getStyleClass().add("leaderboard-rank");
            Label nameLabel = new Label(entry.login());
            nameLabel.getStyleClass().add("leaderboard-name");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label scoreLabel = new Label(String.format("%.1f", entry.score()));
            scoreLabel.getStyleClass().add("leaderboard-elo");
            Label tierLabel = new Label(entry.rank().getBadge());
            tierLabel.getStyleClass().add("leaderboard-tier");
            row.getChildren().addAll(rankLabel, nameLabel, spacer, scoreLabel, tierLabel);
            leaderboardRows.getChildren().add(row);
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
    void handleLogout(ActionEvent event) {
        CentralController.logout();
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
