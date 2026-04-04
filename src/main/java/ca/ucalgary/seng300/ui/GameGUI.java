package ca.ucalgary.seng300.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

// parent class for both Connect 4 and Tic Tac Toe GUI

public class GameGUI extends VBox implements Navigable {
//    public void setRoot(CentralController c) {
//        rootController = c;
//        ticTacToeViewController.setBoss(rootController);
//        boolean vsAi = CentralController.ticTacToeLocalAiMode;
//        ticTacToeViewController.setRandomOpponentEnabled(vsAi);
//        if (vsAi) {
//            ticTacToeViewController.setSession(new TicTacToeSession());
//        } else {
//            ticTacToeViewController.prepareForNetworkPlayInShell();
//        }
//    }

    //    @FXML
//    private SplitPane splitPane;
//    @FXML
//    private VBox leftSideBar;
//    @FXML
//    private VBox childView; // Injects the root of child.fxml
    @FXML private VBox gameBox;
    private C4 c4;
    private TTT ttt;

    // ELO Table Variables
    @FXML
    private Label eloValue; // Value of elo to be displayed
    @FXML
    private Label winValue; // Elo change based on win
    //    @FXML
//    private Label lossValue; // Elo change based on loss
//    @FXML
//    private Label drawValue; // Elo change based on draw
    @FXML
    private Label rankValue; // Elo description string

    // History Box
    @FXML
    public VBox historyBox;

    // Chat box
//    @FXML
//    public VBox chatBox;
    @FXML private VBox matchChat;
    @FXML private TextField chatInput;

    // Accompanying Labels
//    @FXML
//    private Label connect4Label;
    @FXML
    private Label tttLabel;
    @FXML
    private Button infoButton;

    public GameGUI () {
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

    public void setGameNode(Node node) {
        gameBox.getChildren().clear();
        gameBox.getChildren().add(node);

        c4 = node instanceof C4 connect4 ? connect4 : null;
        ttt = node instanceof TTT ticTacToe ? ticTacToe : null;
    }

//    @FXML
//    public void initialize() {
//        GameGUI.setEloLabels(eloValue, winValue, lossValue, drawValue, rankValue);
//        GameGUI.setEloLabels(eloValue, winValue, rankValue);
//    }

    // Enums
//    public enum GameActionType {
//        PLAYER_MOVE,
//        FORFEIT,
//        SETTINGS,
//        INFO,
//        EXIT
//    }

//    public enum GameStatus {
//        WAITING,
//        IN_PROGRESS,
//        WIN,
//        LOSS,
//        DRAW,
//        QUIT,
//    }

    //
//    public static class GameViewData {
//        public final String playerID;
//        public final String opponentID;
//        public final String gameType;
//        public final int rows;
//        public final int cols;
//        public final String[] availableActions;
//
//        public GameViewData(String playerID, String opponentID,
//                String gameType, int rows, int cols,
//                String[] availableActions) {
//            this.playerID = playerID;
//            this.opponentID = opponentID;
//            this.gameType = gameType;
//            this.rows = rows;
//            this.cols = cols;
//            this.availableActions = availableActions;
//        }
//
//    }

//    /** Carries a player's move and actionType. */
//    public static class GameAction {
//        public final String playerID;
//        public final int row;
//        public final int col;
//        public final String optionalPayload;
//
//        public GameAction(String playerID,
//                int row, int col, String optionalPayload) {
//            this.playerID = playerID;
//            this.row = row;
//            this.col = col;
//            this.optionalPayload = optionalPayload;
//        }
//    }

//    /** Look at the game state; returned by getGameState(). */
//    public static class GameState {
//        public final String currentPlayerID;
//        public final char[][] board; // board[row][col]
//        public final ClientState status;
//        public final String statusMessage;
//        public final int moveCount;
//
//        public GameState(String currentPlayerID, char[][] board,
//                ClientState status, String statusMessage, int moveCount) {
//            this.currentPlayerID = currentPlayerID;
//            this.board = board;
//            this.status = status;
//            this.statusMessage = statusMessage;
//            this.moveCount = moveCount;
//        }
//    }

    // Internal current setup
//    protected GameGUI.GameViewData viewData;
//    protected char[][] board;
//    protected ClientState status;
//    protected String currentPlayerID;
//    protected int moveCount;
//    protected boolean isInitialized;

    /**
     * Set up the board render and update.
     * Child classes should call super.initialize(data) first, then add any
     * game-specific setup needed.
     */
//    public void initialize(GameGUI.GameViewData data) {
//        this.viewData = data;
//        this.board = new char[data.rows][data.cols];
//        this.status = ClientState.WAITING;
//        this.currentPlayerID = data.playerID;
//        this.moveCount = 0;
//        this.isInitialized = true;
//        // For child classes to establish.
//        render();
//        update();
//    }

//    /**
//     * Do specified GameAction.
//     * Child classes override this for game-specific move logic, then call
//     * super.handleGameAction(action) to let the parent handle FORFEIT or EXIT.
//     */
//    public void handleGameAction(GameGUI.GameAction action) {
//        ensureInitialized();
//        switch (action.actionType) {
//            case FORFEIT -> restart();
//            case EXIT -> status = ClientState.QUIT;
//            // child will handle other cases.
//            default -> {
//            }
//        }
//        update();
//    }

//    /** Return an current game state info. */
//    public GameGUI.GameState getGameState() {
//        ensureInitialized();
//        return new GameGUI.GameState(
//                currentPlayerID,
//                copyBoard(),
//                status,
//                createStatusMessage(),
//                moveCount);
//    }

//    /** Complete shutdown sequence. */
//    public void shutdown() {
//        isInitialized = false;
//        board = null;
//        viewData = null;
//    }

//    /** Methods to be fleshed out by child classes */
//    public abstract void render();
//
//    public abstract void update();
//
//    protected abstract void onGameCompletion();

    // Helpers

//    private void ensureInitialized() {
//        if (!isInitialized) {
//            throw new IllegalStateException(
//                    "GameGUI has not been initialized. Call initialize() first.");
//        }
//    }

//    private char[][] copyBoard() {
//        char[][] copy = new char[board.length][board[0].length];
//        for (int r = 0; r < board.length; r++) {
//            System.arraycopy(board[r], 0, copy[r], 0, board[r].length);
//        }
//        return copy;
//    }
//
//    private String createStatusMessage() {
//        return switch (status) {
//            case WAITING -> "Waiting to start...";
//            case IN_PROGRESS -> "Player " + currentPlayerID + "'s turn";
//            case GAME_CONCLUDED -> "Game concluded";
////            case WIN -> currentPlayerID + " wins!";
////            case LOSS -> currentPlayerID + " lost.";
////            case DRAW -> "Draw.";
//            case QUIT -> "Game quit.";
//        };
//    }

//    private void restart() {
//        // reset board, render and update
//        board = new char[viewData.rows][viewData.cols];
//        status = ClientState.WAITING;
//        currentPlayerID = viewData.playerID;
//        moveCount = 0;
//        render();
//        update();
//    };

    // Game History

    /*
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

    /*
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

    /*
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

    // Game Elo Labels + methods

//    public static Label eloLabel; // Value of elo to be displayed
//    public static Label winLabel; // Elo change based on win
////    public static Label lossLabel; // Elo change based on loss
////    public static Label drawLabel; // Elo change based on draw
//    public static Label rankLabel;

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
        if (ttt != null) {
            ttt.handleForfeitFromShell();
        }
    }

    @FXML
    private void handleInfoButton(ActionEvent event) {
        if (ttt != null && ttt.isActiveOnlineMatch()) {
            new Alert(Alert.AlertType.INFORMATION,
                    "Finish the match or use Forfeit before opening other screens.",
                    ButtonType.OK).showAndWait();
            return;
        }
        CentralController.moveTo(Navigation.INFO);
    }

    @FXML
    private void handleChatSend(ActionEvent event) {
        String msg = chatInput.getText().trim();
        if (msg.isBlank()) return;
        String sender = "You"; // default
        // TODO redo this
//        if (ticTacToeViewController != null && ticTacToeViewController.currentPlayerID != null) {
//            sender = ticTacToeViewController.currentPlayerID;
//        } else if (connect4ViewController != null && connect4ViewController.currentPlayerID != null) {
//            sender = connect4ViewController.currentPlayerID;
//        }
        addChat(sender, msg, Color.web("#fafafa"));
        chatInput.clear();
    }

    @FXML
    private void handleExitButton(ActionEvent event) {
        if (ttt != null && ttt.isActiveOnlineMatch()) {
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