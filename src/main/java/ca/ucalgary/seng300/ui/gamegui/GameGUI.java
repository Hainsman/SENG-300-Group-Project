package ca.ucalgary.seng300.ui.gamegui;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.Label;

// parent class for both Connect 4 and Tic Tac Toe GUI

public abstract class GameGUI {

    public enum GameStatus {
        WAITING,
        IN_PROGRESS,
        WIN,
        LOSS,
        DRAW,
        QUIT,
    }

    //
    public static class GameViewData {
        public final String playerID;
        public final String opponentID;
        public final String gameType;
        public final int rows;
        public final int cols;
        public final String[] availableActions;

        public GameViewData(String playerID, String opponentID,
                String gameType, int rows, int cols,
                String[] availableActions) {
            this.playerID = playerID;
            this.opponentID = opponentID;
            this.gameType = gameType;
            this.rows = rows;
            this.cols = cols;
            this.availableActions = availableActions;
        }

    }

    /** Look at the game state; returned by getGameState(). */
    public static class GameState {
        public final String currentPlayerID;
        public final char[][] board; // board[row][col]
        public final GameGUI.GameStatus status;
        public final String statusMessage;
        public final int moveCount;

        public GameState(String currentPlayerID, char[][] board,
                GameGUI.GameStatus status, String statusMessage, int moveCount) {
            this.currentPlayerID = currentPlayerID;
            this.board = board;
            this.status = status;
            this.statusMessage = statusMessage;
            this.moveCount = moveCount;
        }
    }

    // Internal current setup
    protected GameGUI.GameViewData viewData;
    protected char[][] board;
    protected GameGUI.GameStatus status;
    protected String currentPlayerID;
    protected int moveCount;
    protected boolean isInitialized;

    /**
     * Set up the board render and update.
     * Child classes should call super.initialize(data) first, then add any
     * game-specific setup needed.
     */
    public void initialize(GameGUI.GameViewData data) {
        this.viewData = data;
        this.board = new char[data.rows][data.cols];
        this.status = GameGUI.GameStatus.WAITING;
        this.currentPlayerID = data.playerID;
        this.moveCount = 0;
        this.isInitialized = true;
        // For child classes to establish.
        render();
        update();
    }

    /** Return an current game state info. */
    public GameGUI.GameState getGameState() {
        ensureInitialized();
        return new GameGUI.GameState(
                currentPlayerID,
                copyBoard(),
                status,
                createStatusMessage(),
                moveCount);
    }

    /** Methods to be fleshed out by child classes */
    public abstract void render();

    public abstract void update();

    protected abstract void onGameCompletion();

    // Helpers

    private void ensureInitialized() {
        if (!isInitialized) {
            throw new IllegalStateException(
                    "GameGUI has not been initialized. Call initialize() first.");
        }
    }

    private char[][] copyBoard() {
        char[][] copy = new char[board.length][board[0].length];
        for (int r = 0; r < board.length; r++) {
            System.arraycopy(board[r], 0, copy[r], 0, board[r].length);
        }
        return copy;
    }

    private String createStatusMessage() {
        return switch (status) {
            case WAITING -> "Waiting to start...";
            case IN_PROGRESS -> "Player " + currentPlayerID + "'s turn";
            case WIN -> currentPlayerID + " wins!";
            case LOSS -> currentPlayerID + " lost.";
            case DRAW -> "Draw.";
            case QUIT -> "Game quit.";
        };
    }

    private void restart() {
        // reset board, render and update
        board = new char[viewData.rows][viewData.cols];
        status = GameStatus.WAITING;
        currentPlayerID = viewData.playerID;
        moveCount = 0;
        render();
        update();
    };

    // Game History

    public static VBox historyBox;

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
    public static void addHistory(String gameType, String player, String details, Color color) {
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
    public static void clearHistory() {
        if (historyBox != null) {
            historyBox.getChildren().clear();
        }

        // Clearing array list for the sending the match history.
        matchHistory.clear();
    }

    // Array list is used for storing the match history.
    // The match history inside the array should look something like this:
    // "[TP1(0,0), TP2(1,0), TP1(1,1)".
    private static List<String> matchHistory = new ArrayList<>();

    // Player move getter method
    public String getMove() {
        return matchHistory.get(matchHistory.size() - 1); // The latest move e.g. "[TP1(0,0)]".
    }
}