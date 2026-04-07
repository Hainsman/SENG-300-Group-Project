package ca.ucalgary.seng300.ui.userdata;

import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.services.LeaderBoardService;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.ui.CentralController;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import ca.ucalgary.seng300.ui.util.PlayerProfileHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

/**
 * Profile dashboard: read-only ELO, rank badges, and leaderboards. Account changes: {@link SettingsPage}.
 */
public class ProfileDashboardPage extends AnchorPane implements Navigable {

    @FXML private Label loggedInAsLabel;
    @FXML private Label tttEloLabel;
    @FXML private Label tttGamesLabel;
    @FXML private Label tttRankBadge;
    @FXML private Label c4EloLabel;
    @FXML private Label c4GamesLabel;
    @FXML private Label c4RankBadge;
    @FXML private Label tierSummaryLabel;
    @FXML private VBox tttLeaderboardBox;
    @FXML private VBox c4LeaderboardBox;

    public ProfileDashboardPage() {
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
    private void initialize() {
        refreshLoggedInLine();
        refreshStats();
    }

    private void refreshLoggedInLine() {
        if (loggedInAsLabel == null) {
            return;
        }
        User u = currentUser();
        if (u == null) {
            loggedInAsLabel.setText("Not logged in.");
            return;
        }
        loggedInAsLabel.setText("Signed in as: " + u.getlogin() + "  ·  " + u.getEmail());
    }

    private User currentUser() {
        int id = CentralController.currentUserId;
        if (id == 0) {
            return null;
        }
        return UserStore.getInstance().get(id);
    }

    private void refreshStats() {
        int tttElo = PlayerProfileHelper.eloForGameType(GameType.TIC_TAC_TOE);
        int c4Elo = PlayerProfileHelper.eloForGameType(GameType.CONNECT4);
        int tttGames = PlayerProfileHelper.gamesForGameType(GameType.TIC_TAC_TOE);
        int c4Games = PlayerProfileHelper.gamesForGameType(GameType.CONNECT4);

        if (tttEloLabel != null) {
            tttEloLabel.setText(String.valueOf(tttElo));
        }
        if (tttGamesLabel != null) {
            tttGamesLabel.setText(tttGames + " games played");
        }
        if (tttRankBadge != null) {
            tttRankBadge.setText(PlayerProfileHelper.eloTierBadge(tttElo));
        }
        if (c4EloLabel != null) {
            c4EloLabel.setText(String.valueOf(c4Elo));
        }
        if (c4GamesLabel != null) {
            c4GamesLabel.setText(c4Games + " games played");
        }
        if (c4RankBadge != null) {
            c4RankBadge.setText(PlayerProfileHelper.eloTierBadge(c4Elo));
        }
        if (tierSummaryLabel != null) {
            tierSummaryLabel.setText(String.format(
                    "Ranks: TTT %s  ·  C4 %s",
                    PlayerProfileHelper.eloTierLabel(tttElo),
                    PlayerProfileHelper.eloTierLabel(c4Elo)));
        }
        if (tttLeaderboardBox != null && c4LeaderboardBox != null) {
            populateLeaderboard(GameType.TIC_TAC_TOE, tttLeaderboardBox);
            populateLeaderboard(GameType.CONNECT4, c4LeaderboardBox);
        }
    }

    private void populateLeaderboard(GameType gameType, VBox targetBox) {
        targetBox.getChildren().clear();
        List<User> top = LeaderBoardService.getInstance().getLeaderBoard(true, gameType, 20);
        if (top.isEmpty()) {
            Label empty = new Label("No players yet");
            empty.getStyleClass().add("leaderboard-empty");
            targetBox.getChildren().add(empty);
            return;
        }
        for (int i = 0; i < top.size(); i++) {
            User u = top.get(i);
            int elo = u.getEloForGame(gameType);
            targetBox.getChildren().add(buildLeaderboardRow(i + 1, u.getlogin(), elo));
        }
    }

    private HBox buildLeaderboardRow(int position, String login, int elo) {
        Label rankLabel = new Label("#" + position);
        rankLabel.getStyleClass().add("leaderboard-rank");

        Label nameLabel = new Label(login);
        nameLabel.getStyleClass().add("leaderboard-name");

        Label eloLabel = new Label(elo + " ELO");
        eloLabel.getStyleClass().add("leaderboard-elo");

        Label tierLabel = new Label(PlayerProfileHelper.eloTierLabel(elo));
        tierLabel.getStyleClass().add("leaderboard-tier");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(10, rankLabel, nameLabel, spacer, eloLabel, tierLabel);
        row.getStyleClass().add("leaderboard-row");
        return row;
    }

    @FXML
    private void handleBack(ActionEvent event) {
        if (CentralController.previousScreen != null) {
            CentralController.moveTo(CentralController.previousScreen);
            return;
        }
        CentralController.moveTo(Navigation.GAME_DASHBOARD);
    }

    @Override
    public Navigation getNavigation() {
        return Navigation.PROFILE_DASHBOARD;
    }

    @Override
    public void reset() {
        initialize();
    }
}
