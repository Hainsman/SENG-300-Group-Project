package ca.ucalgary.seng300.ui.games;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.ui.CentralController;
import ca.ucalgary.seng300.ui.SoundEffect;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GameGUIPage extends VBox implements Navigable {

    private CentralController rootController;

    private String currentPlayerID;
    private C4Page c4Page;
    private TTTPage tttPage;

    @FXML private VBox gameBox;

    // ELO Table Variables
    @FXML private Label eloValue; // Value of elo to be displayed
    @FXML private Label winValue; // Elo change based on win
    //    @FXML private Label lossValue; // Elo change based on loss
//    @FXML private Label drawValue; // Elo change based on draw
    @FXML private Label rankValue; // Elo description string

    // History Box
    @FXML public VBox historyBox;

    @FXML private VBox matchChat;
    @FXML private TextField chatInput;

    // Accompanying Labels
//    @FXML private Label connect4Label;
    @FXML private Label gameTypeLabel;
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

    public void setGameNode(Node node, GameType gameType) {
        gameBox.getChildren().clear();
        gameBox.getChildren().add(node);

        c4Page = node instanceof C4Page connect4 ? connect4 : null;
        tttPage = node instanceof TTTPage ticTacToe ? ticTacToe : null;

        gameTypeLabel.setText(gameType.toString());
        rootController.getWindowManager().playAudioClip(SoundEffect.GAME_STARTED);
    }

//    @FXML
//    public void initialize() {
//        GameGUI.setEloLabels(eloValue, winValue, lossValue, drawValue, rankValue);
//        GameGUI.setEloLabels(eloValue, winValue, rankValue);
//    }

    /**
     * addHistory() is the single method any game controller calls to log a move.
     * It creates a colored dot + label entry and adds it to the historyBox.
     *
     * @param gameType "TicTacToe" or "Connect4"
     * 
     * @param player "X", "O", "Player 1", etc.
     * 
     * @param details "(1, 2)" or "Col 3" etc.
     * 
     * @param color dot color matching the player's piece
     */
    public void addHistory(String gameType, String player, String details, Color color) {
        if (historyBox == null) {
            return;
        }

        HBox entry = new HBox(8);
        entry.setAlignment(Pos.CENTER_LEFT);
        entry.setStyle("-fx-padding: 4 8;");

        Circle dot = new Circle(7);
        dot.setFill(color);

        Label label = new Label(gameType + "  |  " + player + "  →  " + details);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        entry.getChildren().addAll(dot, label);
        historyBox.getChildren().add(entry);
    }

    /**
     * clearHistory() wipes the history panel.
     * Call this when starting a new game or resetting one.
     */
    public void clearHistory() {
        if (historyBox != null) {
            historyBox.getChildren().clear();
        }

        // Clearing array list for the sending the match history.
        matchHistory.clear();
    }

    // Array list is used for storing the match history.
    // The match history inside the array should look something like this:
    // "[TP1(0,0), TP2(1,0), TP1(1,1)".
    private final List<String> matchHistory = new ArrayList<>();

    /**
     * onMoveChosen appends match history to an array list.
     * Returns a string containing the match history and game type.
     * 
     * @param player send TP1/TP2 for tictactoe and CP1/CP2 for connect4.
     * 
     * @param move "(1,2)".
     */
    public void onMoveChosen(String player, String move) {
        String playerMove = player + move;
        matchHistory.add(playerMove);
    }

    // Match history getter method
    public String getMatchHistory() {
        return matchHistory.toString(); // e.g. "[TP1(0,0), TP2(1,0), TP1(1,1)" OR "[CP1(0,0), CP2(1,0), CP1(1,1)".
    }

    // Player move getter method
    public String getMove() {
        return matchHistory.getLast(); // The latest move e.g. "[TP1(0,0)]".
    }

    public void addChat(String sender, String message, Color color) {
        if (matchChat == null) return;
        Label entry = new Label(sender + ": " + message);
        entry.setTextFill(color);
        entry.setWrapText(true);
        Platform.runLater(() -> matchChat.getChildren().add(entry));
    }

    public void clearChat() {
        if (matchChat != null) Platform.runLater(() -> matchChat.getChildren().clear());
    }

//    public static void setEloLabels(Label elo, Label win, Label loss, Label draw, Label rank) {
//    public static void setEloLabels(Label elo, Label win, Label rank) {
//        eloLabel = elo;
//        winLabel = win;
////        lossLabel = loss;
////        drawLabel = draw;
//        rankLabel = rank;
//    }

    // backend can call with necessary data inputs
//    public static void displayElo(int elo, int win, int loss, int draw, String rank) {
//        if (eloLabel == null)
//            return;
//        eloLabel.setText(String.valueOf(elo));
//        winLabel.setText(String.valueOf(win));
////        lossLabel.setText(String.valueOf(loss));
////        drawLabel.setText(String.valueOf(draw));
//        rankLabel.setText("PlaceHolder");
//
//        // RESOLVE PLEASE: Need to connect to backend constants / rank.java
//        // for (Rank rank : Rank.values()) {
//        // if (rank.isInRank(elo)) {
//        // rankLabel.setText(rank.getDisplayName());
//        // break;
//        // }
//        // }
//    }

    @FXML
    private void handleForfeit(ActionEvent event) {
        if (tttPage != null) {
            tttPage.handleForfeitFromShell();
        }
        else if (c4Page != null) {
            c4Page.handleForfeitFromShell();
        }
    }

    @FXML
    private void handleInfoButton(ActionEvent event) {
        if ((tttPage != null && tttPage.isOnlineMode()) || (c4Page != null && c4Page.isOnlineMode())) {
            new Alert(Alert.AlertType.INFORMATION,
                    "Finish the match or use Forfeit before opening other screens.",
                    ButtonType.OK).showAndWait();
            return;
        }
        rootController.moveTo(Navigation.INFO);
    }

    @FXML
    private void handleChatSend(ActionEvent event) {
        String msg = chatInput.getText().trim();
        if (msg.isBlank()) return;
        String sender = rootController.tttPage != null || rootController.connect4 != null
                ? currentPlayerID
                : "You";

        addChat(sender, msg, Color.web("#fafafa"));
        chatInput.clear();
    }

    @FXML
    private void handleExitButton(ActionEvent event) {
        if ((tttPage != null && tttPage.isOnlineMode()) || (c4Page != null && c4Page.isOnlineMode())) {
            new Alert(Alert.AlertType.WARNING,
                    "Use Forfeit in the sidebar to leave an online match in progress. You can quit the app afterward.",
                    ButtonType.OK).showAndWait();
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

    }
}

// TO ADD:
// finish restart() method
// History implementation. Check info from Platform Core.
// Settings.