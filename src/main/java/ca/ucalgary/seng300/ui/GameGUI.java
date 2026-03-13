package ca.ucalgary.seng300.ui;

import javafx.scene.layout.Pane;

// parent class for both Connect 4 and Tic Tac Toe GUI

public abstract class GameGUI {

    // Enums
    public enum GameActionType {
        PLAYER_MOVE,
        FORFEIT,
        SETTINGS,
        INFO,
        EXIT
    }

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
        public final String   playerID;
        public final String   opponentID;
        public final String   gameType;
        public final int      rows;
        public final int      cols;
        public final String[] availableActions;

        public GameViewData(String playerID, String opponentID,
                            String gameType, int rows, int cols,
                            String[] availableActions) {
            this.playerID         = playerID;
            this.opponentID       = opponentID;
            this.gameType         = gameType;
            this.rows             = rows;
            this.cols             = cols;
            this.availableActions = availableActions;
        }
    }

    /** Carries a player's move and actionType. */
    public static class GameAction {
        public final String         playerID;
        public final GameGUI.GameActionType actionType;
        public final int            row;
        public final int            col;
        public final String         optionalPayload;

        public GameAction(String playerID, GameGUI.GameActionType actionType,
                          int row, int col, String optionalPayload) {
            this.playerID        = playerID;
            this.actionType      = actionType;
            this.row             = row;
            this.col             = col;
            this.optionalPayload = optionalPayload;
        }
    }

    /** Look at the game state; returned by getGameState(). */
    public static class GameState {
        public final String     currentPlayerID;
        public final char[][]   board;        // board[row][col]
        public final GameGUI.GameStatus status;
        public final String     statusMessage;
        public final int        moveCount;

        public GameState(String currentPlayerID, char[][] board,
                         GameGUI
                                 .GameStatus status, String statusMessage, int moveCount) {
            this.currentPlayerID = currentPlayerID;
            this.board           = board;
            this.status          = status;
            this.statusMessage   = statusMessage;
            this.moveCount       = moveCount;
        }
    }


    // Internal current setup
    protected GameGUI.GameViewData viewData;
    protected char[][]     board;
    protected GameGUI.GameStatus status;
    protected String       currentPlayerID;
    protected int          moveCount;
    protected boolean      isInitialized;

    /**
     * Set up the board render and update.
     * Child classes should call super.initialize(data) first, then add any
     * game-specific setup needed.
     */
    public void initialize(GameGUI.GameViewData data) {
        this.viewData        = data;
        this.board           = new char[data.rows][data.cols];
        this.status          = GameGUI.GameStatus.WAITING;
        this.currentPlayerID = data.playerID;
        this.moveCount       = 0;
        this.isInitialized   = true;
        // For child classes to establish.
        render();
        update();
    }

    /**
     * Do specified GameAction.
     * Child classes override this for game-specific move logic, then call
     * super.handleGameAction(action) to let the parent handle FORFEIT or EXIT.
     */
    public void handleGameAction(GameGUI.GameAction action) {
        ensureInitialized();
        switch (action.actionType) {
            case FORFEIT  -> restart();
            case EXIT   -> status = GameGUI.GameStatus.QUIT;
            // child will handle other cases.
            default       -> { }
        }
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
                moveCount
        );
    }

    /** Complete shutdown sequence. */
    public void shutdown() {
        isInitialized = false;
        board         = null;
        viewData      = null;
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
            case WAITING     -> "Waiting to start...";
            case IN_PROGRESS -> "Player " + currentPlayerID + "'s turn";
            case WIN         -> currentPlayerID + " wins!";
            case LOSS        -> currentPlayerID + " lost.";
            case DRAW        -> "Draw.";
            case QUIT   -> "Game quit.";
        };
    }

    private void restart(){
        // reset board, render and update
        board = new char[viewData.rows][viewData.cols];
        status = GameStatus.WAITING;
        currentPlayerID = viewData.playerID;
        moveCount = 0;
        render();
        update();
    };
}


// TO ADD:
// finish restart() method
// History implementation. Check info from Platform Core.
// Settings.