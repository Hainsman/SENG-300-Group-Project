package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import ca.ucalgary.seng300.game.core.connect4.C4GameState;
import ca.ucalgary.seng300.game.core.connect4.ConnectFourRandomMoveGenerator;
import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.session.ConnectFourSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.event.ActionEvent;

public class Connect4Controller extends GameGUI {
    private CentralController boss;
    private ClientCore clientCore = null;
    private boolean onlineEloApplied = false;

    /**
     * Local session using {@link C4Game} (human as player 1, optional random-move AI as player 2).
     */
    private ConnectFourSession session = null;
    /** When true with a non-null session, player 2 uses {@link ConnectFourRandomMoveGenerator}. */
    private boolean randomOpponentEnabled = false;

    public void connectToRootForLobby(CentralController root) {
        boss = root;
        boss.setConnect4(this);
        clientCore = boss.getCC();
        EloOverlayUtil.hide(eloResultOverlay);
        if (CentralController.connect4LocalAiMode) {
            randomOpponentEnabled = true;
            session = new ConnectFourSession();
            GameGUI.clearHistory();
            refreshFromSession();
        } else {
            randomOpponentEnabled = false;
            session = null;
            if (clientCore != null && clientCore.getLastUpdate() instanceof C4GameState gs) {
                refreshFromGameStateUpdate(gs);
            }
        }
    }

    @FXML
    private StackPane boardPane;

    @FXML
    private VBox eloResultOverlay;
    @FXML
    private Label eloOutcomeTitle;
    @FXML
    private Label eloDeltaLabel;
    @FXML
    private Label eloNewRatingLabel;

    @FXML
    private GridPane gridPane;
    private Circle[][] circles = new Circle[6][7];
    private int rows = 6;
    private int cols = 7;
    @FXML
    private Label statusLabel;

    @FXML
    private Button resetButton;

    private final Color P1 = Color.web("#dd3030ff");
    private final Color P2 = Color.web("#fad44aff");

    @FXML
    public void initialize() {
        render();
    }

    @Override
    public void render() {
        gridPane.getChildren().clear();
        circles = new Circle[6][7];
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                Circle circle = new Circle(32);
                circle.setFill(Color.WHITE);
                circles[row][col] = circle;

                final int c = col;
                circle.setOnMouseClicked(e -> dropPiece(c));

                gridPane.add(circle, col, row);
            }
        }
    }

    @Override
    public void update() {
        if (statusLabel == null)
            return;
        switch (status) {
            case IN_PROGRESS -> statusLabel.setText("Player " + currentPlayer + "'s Turn");
            case WIN -> statusLabel.setText("Player " + currentPlayer + " Wins!");
            case DRAW -> statusLabel.setText("It's a Draw!");
            default -> {
            }
        }

    }

    @Override
    protected void onGameCompletion() {
        disableBoard();
        resetButton.setVisible(true);
    }

    private int currentPlayer = 1;

    private void dropPiece(int col) {
        if (!randomOpponentEnabled && clientCore != null && clientCore.isConnected()) {
            Player myRole = clientCore.getMyNetworkRole();
            if (myRole != null && clientCore.getLastUpdate() instanceof C4GameState gs) {
                if (State.IN_PROGRESS.equals(gs.getState()) && !myRole.equals(gs.getPlayerTurn())) {
                    statusLabel.setText(myRole == Player.PLAYER_1 ? "Waiting for Player 2..." : "Waiting for Player 1...");
                    return;
                }
            }
            if (!clientCore.submitMove(0, col)) {
                statusLabel.setText("Could not send move.");
            }
            return;
        }
        if (session != null) {
            if (!session.submitMove(col)) {
                return;
            }
            Player[][] b = session.getBoard();
            int landingRow = landingRowForLastDrop(b, col);
            if (landingRow >= 0) {
                Player mover = b[landingRow][col];
                Color moverColor = mover == Player.PLAYER_1 ? P1 : P2;
                String label = mover == Player.PLAYER_1 ? "1" : "2";
                GameGUI.addHistory("Connect4", label,
                        "(" + (landingRow + 1) + ", " + (col + 1) + ")", moverColor);
            }
            refreshFromSession();
            maybePlayRandomOpponent();
            return;
        }
        for (int row = 5; row >= 0; row--) {
            if (circles[row][col].getFill().equals(Color.WHITE)) {
                Color playerColor;

                // Used for sending match history
                String rowString = String.valueOf(row + 1);
                String colString = String.valueOf(col + 1);

                if (currentPlayer == 1) {
                    playerColor = P1;
                    GameGUI.addHistory("Connect4", "1",
                            "(" + (row + 1) + ", " + (col + 1) + ")", playerColor);
                    GameGUI.onMoveChosen("P1", "(" + rowString + "," + colString + ")");
                } else {
                    playerColor = P2;
                    GameGUI.addHistory("Connect4", "2",
                            "(" + (row + 1) + ", " + (col + 1) + ")", playerColor);
                    GameGUI.onMoveChosen("P2", "(" + rowString + "," + colString + ")");
                }
                circles[row][col].setFill(playerColor);

                if (checkWin(row, col, playerColor)) {
                    status = GameStatus.WIN;
                    onGameCompletion();
                } else if (isBoardFull()) {
                    status = GameStatus.DRAW;
                    onGameCompletion();
                } else {
                    status = GameStatus.IN_PROGRESS;
                    if (currentPlayer == 1) {
                        currentPlayer = 2;
                    } else {
                        currentPlayer = 1;
                    }
                }
                update();
                return;
            }
        }
    }

    private static int landingRowForLastDrop(Player[][] b, int col) {
        for (int r = 5; r >= 0; r--) {
            if (b[r][col] != Player.EMPTY) {
                return r;
            }
        }
        return -1;
    }

    /** After the human plays player 1, player 2 picks a random legal column (if enabled and game continues). */
    private void maybePlayRandomOpponent() {
        if (!randomOpponentEnabled || session == null) {
            return;
        }
        Platform.runLater(() -> {
            if (session == null || session.isGameOver()) {
                return;
            }
            if (session.getTurn() != Player.PLAYER_2) {
                return;
            }
            Integer acol = ConnectFourRandomMoveGenerator.pickRandomLegalColumn(session.getBoard());
            if (acol == null) {
                return;
            }
            if (!session.submitMove(acol)) {
                return;
            }
            Player[][] b = session.getBoard();
            int landingRow = landingRowForLastDrop(b, acol);
            if (landingRow >= 0) {
                GameGUI.addHistory("Connect4", "2 (AI)",
                        "(" + (landingRow + 1) + ", " + (acol + 1) + ")", P2);
            }
            refreshFromSession();
        });
    }

    private void refreshFromSession() {
        if (session == null) {
            return;
        }
        Player[][] board = session.getBoard();
        State st = session.getState();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Circle circle = circles[row][col];
                Player p = board[row][col];
                if (p == Player.PLAYER_1) {
                    circle.setFill(P1);
                } else if (p == Player.PLAYER_2) {
                    circle.setFill(P2);
                } else {
                    circle.setFill(Color.WHITE);
                }
            }
        }
        boolean gameOver = session.isGameOver();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                circles[row][col].setDisable(gameOver);
            }
        }
        if (State.PLAYER_1_WIN.equals(st)) {
            statusLabel.setText("Player 1 Wins!");
            resetButton.setVisible(true);
            disableBoard();
        } else if (State.PLAYER_2_WIN.equals(st)) {
            statusLabel.setText("Player 2 (AI) Wins!");
            resetButton.setVisible(true);
            disableBoard();
        } else if (State.TIE.equals(st)) {
            statusLabel.setText("Draw!");
            resetButton.setVisible(true);
            disableBoard();
        } else {
            statusLabel.setText(session.getTurn() == Player.PLAYER_1 ? "Player 1's Turn (you)" : "Player 2 (AI) is moving…");
            resetButton.setVisible(false);
        }
    }

    private boolean checkWin(int row, int col, Color color) {
        return checkDirection(row, col, color, 1, 0) // horizontal
                || checkDirection(row, col, color, 0, 1) // vertical
                || checkDirection(row, col, color, 1, 1) // diagonal
                || checkDirection(row, col, color, 1, -1); // diagonal
    }

    private boolean checkDirection(int row, int col, Color color, int dr, int dc) {
        int count = 1;
        count += countFrom(row, col, color, dr, dc);
        count += countFrom(row, col, color, -dr, -dc);
        return count >= 4;
    }

    private int countFrom(int row, int col, Color color, int dr, int dc) {
        int count = 0;
        int r = row + dr;
        int c = col + dc;
        while (r >= 0 && r < rows && c >= 0 && c < cols
                && circles[r][c].getFill().equals(color)) {
            count++;
            r += dr;
            c += dc;
        }
        return count;
    }

    private boolean isBoardFull() {
        for (int col = 0; col < cols; col++)
            if (circles[0][col].getFill().equals(Color.WHITE))
                return false;
        return true;
    }

    private void disableBoard() {
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                circles[row][col].setOnMouseClicked(null);
    }

    public void setVisibility(Boolean status) {
        boardPane.setVisible(status);
    }

    @FXML
    private void resetClick(ActionEvent event) {
        EloOverlayUtil.hide(eloResultOverlay);
        onlineEloApplied = false;
        if (!randomOpponentEnabled && clientCore != null && clientCore.isConnected()) {
            clientCore.msgSend(new sysMSG("reset", ""));
            return;
        }
        if (session != null) {
            session.startNewGame();
            resetButton.setVisible(false);
            render();
            refreshFromSession();
            return;
        }
        resetButton.setVisible(false);
        status = GameStatus.IN_PROGRESS;
        currentPlayer = 1;
        render();
        update();
    }

    @FXML
    private void handleBackToMode(ActionEvent event) {
        CentralController.connect4LocalAiMode = false;
        randomOpponentEnabled = false;
        session = null;
        EloOverlayUtil.hide(eloResultOverlay);
        CentralController.previousScreen = "chooseModeNew.fxml";
        CentralController.moveTo("chooseModeNew.fxml");
    }

    public void refreshFromGameStateUpdate(C4GameState update) {
        if (update == null || update.getBoard() == null) {
            return;
        }
        State st = update.getState();
        if (State.IN_PROGRESS.equals(st)) {
            onlineEloApplied = false;
            EloOverlayUtil.hide(eloResultOverlay);
        }
        Player[][] board = update.getBoard();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Circle circle = circles[row][col];
                Player p = board[row][col];
                if (p == Player.PLAYER_1) {
                    circle.setFill(P1);
                } else if (p == Player.PLAYER_2) {
                    circle.setFill(P2);
                } else {
                    circle.setFill(Color.WHITE);
                }
            }
        }

        boolean gameOver = !State.IN_PROGRESS.equals(st);
        Player myRole = clientCore != null ? clientCore.getMyNetworkRole() : null;
        boolean onlineTurns = clientCore != null && clientCore.isConnected() && myRole != null;
        boolean myTurn = !onlineTurns || gameOver || myRole.equals(update.getPlayerTurn());
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                circles[row][col].setDisable(gameOver || !myTurn);
            }
        }

        if (State.PLAYER_1_WIN.equals(st)) {
            statusLabel.setText("Player 1 Wins!");
            resetButton.setVisible(true);
        } else if (State.PLAYER_2_WIN.equals(st)) {
            statusLabel.setText("Player 2 Wins!");
            resetButton.setVisible(true);
        } else if (State.TIE.equals(st)) {
            statusLabel.setText("Draw!");
            resetButton.setVisible(true);
        } else {
            statusLabel.setText(update.getPlayerTurn() == Player.PLAYER_1 ? "Player 1's Turn" : "Player 2's Turn");
            resetButton.setVisible(false);
        }
        OnlineRankingHelper.applyIfOnlineGameFinished(clientCore, st, onlineEloApplied, Game.CONNECT_FOUR)
                .ifPresent(r -> {
                    onlineEloApplied = true;
                    EloOverlayUtil.show(eloResultOverlay, eloOutcomeTitle, eloDeltaLabel, eloNewRatingLabel, r);
                });
    }

    @FXML
    private void handleDismissEloOverlay() {
        EloOverlayUtil.hide(eloResultOverlay);
    }
}
