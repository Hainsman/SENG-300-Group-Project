package ca.ucalgary.seng300.ui.games;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.ui.CentralController;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import ca.ucalgary.seng300.ui.util.ThemeAlerts;
import ca.ucalgary.seng300.ui.util.PlayerProfileHelper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GameGUIPage extends VBox implements Navigable {

    private CentralController rootController;

    private C4Page c4Page;
    private TTTPage tttPage;

    @FXML private VBox gameBox;

    @FXML private Label eloValue;
    @FXML private Label winValue;
    @FXML private Label rankValue;

    @FXML private VBox matchChat;
    @FXML private TextField chatInput;

    @FXML private Label tttLabel;
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Button infoButton;

    public GameGUIPage(CentralController rootController) {
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

    public void setGameNode(Node node) {
        gameBox.getChildren().clear();
        gameBox.getChildren().add(node);

        c4Page = node instanceof C4Page connect4 ? connect4 : null;
        tttPage = node instanceof TTTPage ticTacToe ? ticTacToe : null;
        refreshSidebar();
    }

    /**
     * Left sidebar: game name (Tic-Tac-Toe vs Connect 4), signed-in user, ELO/tier/games for this game mode.
     */
    public void refreshSidebar() {
        GameType g = CentralController.selectedGame != null
                ? CentralController.selectedGame
                : GameType.TIC_TAC_TOE;

        if (tttLabel != null) {
            tttLabel.getStyleClass().removeAll("ttt-title", "c4-title");
            if (c4Page != null) {
                tttLabel.setText("Connect 4");
                tttLabel.getStyleClass().add("c4-title");
                tttLabel.setEffect(null);
            } else {
                tttLabel.setText("Tic-Tac-Toe");
                tttLabel.getStyleClass().add("ttt-title");
                tttLabel.setEffect(new DropShadow(8, Color.color(0.31, 0.88, 0.85)));
            }
        }

        int uid = CentralController.currentUserId;
        User u = uid > 0 ? UserStore.getInstance().get(uid) : null;
        String display = "Player";
        if (u != null) {
            display = u.getlogin();
        } else if (CentralController.displayName != null && !CentralController.displayName.isBlank()) {
            display = CentralController.displayName.trim();
        }
        if (usernameLabel != null) {
            usernameLabel.setText(display);
        }

        if (emailLabel != null) {
            String em = u != null && u.getEmail() != null ? u.getEmail().trim() : "";
            if (!em.isEmpty()) {
                emailLabel.setText(em);
                emailLabel.setVisible(true);
                emailLabel.setManaged(true);
            } else {
                emailLabel.setText("");
                emailLabel.setVisible(false);
                emailLabel.setManaged(false);
            }
        }

        int elo = 1200;
        int games = 0;
        if (u != null) {
            elo = u.getEloForGame(g);
            games = u.getGamesForGame(g);
        }
        if (eloValue != null) {
            eloValue.setText(String.valueOf(elo));
        }
        if (rankValue != null) {
            rankValue.setText(PlayerProfileHelper.eloTierLabel(elo));
        }
        if (winValue != null) {
            winValue.setText(String.valueOf(games));
        }
    }

    /**
     * Match history panel was removed; moves are still tracked in {@link #matchHistory} for networking.
     */
    public void addHistory(String gameType, String player, String details, Color color) {
        // No on-screen history list; data remains in matchHistory via onMoveChosen.
    }

    public void clearHistory() {
        matchHistory.clear();
    }

    private final List<String> matchHistory = new ArrayList<>();

    public void onMoveChosen(String player, String move) {
        String playerMove = player + move;
        matchHistory.add(playerMove);
    }

    public String getMatchHistory() {
        return matchHistory.toString();
    }

    public String getMove() {
        return matchHistory.isEmpty() ? "" : matchHistory.getLast();
    }

    public void addChat(String sender, String message, Color color) {
        if (matchChat == null) {
            return;
        }
        Label entry = new Label(sender + ": " + message);
        entry.setTextFill(color);
        entry.setWrapText(true);
        Platform.runLater(() -> matchChat.getChildren().add(entry));
    }

    public void clearChat() {
        if (matchChat != null) {
            Platform.runLater(() -> matchChat.getChildren().clear());
        }
    }

    @FXML
    private void handleForfeit(ActionEvent event) {
        if (tttPage != null) {
            tttPage.handleForfeitFromShell();
        } else if (c4Page != null) {
            c4Page.handleForfeitFromShell();
        }
    }

    @FXML
    private void handleInfoButton(ActionEvent event) {
        boolean onlineBlock = (tttPage != null && tttPage.isActiveOnlineMatch())
                || (c4Page != null && c4Page.isActiveOnlineMatch());
        if (onlineBlock) {
            Alert a = new Alert(Alert.AlertType.INFORMATION,
                    "Finish the match or use Forfeit before opening other screens.",
                    ButtonType.OK);
            ThemeAlerts.apply(a);
            a.showAndWait();
            return;
        }
        CentralController.moveTo(Navigation.INFO);
    }

    @FXML
    private void handleChatSend(ActionEvent event) {
        String msg = chatInput.getText().trim();
        if (msg.isBlank()) {
            return;
        }
        int uid = CentralController.currentUserId;
        User u = uid > 0 ? UserStore.getInstance().get(uid) : null;
        String sender = u != null ? u.getlogin()
                : (CentralController.displayName != null && !CentralController.displayName.isBlank()
                        ? CentralController.displayName.trim()
                        : "You");

        addChat(sender, msg, Color.web("#fafafa"));
        chatInput.clear();
    }

    @FXML
    private void handleExitButton(ActionEvent event) {
        boolean onlineBlock = (tttPage != null && tttPage.isActiveOnlineMatch())
                || (c4Page != null && c4Page.isActiveOnlineMatch());
        if (onlineBlock) {
            Alert a = new Alert(Alert.AlertType.WARNING,
                    "Use Forfeit in the sidebar to leave an online match in progress. You can quit the app afterward.",
                    ButtonType.OK);
            ThemeAlerts.apply(a);
            a.showAndWait();
            return;
        }
        Platform.exit();
    }

    @Override
    public Navigation getNavigation() {
        return Navigation.GAME_GUI;
    }

    @Override
    public void reset() {
        refreshSidebar();
    }
}
