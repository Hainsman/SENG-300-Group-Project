package ca.ucalgary.seng300.ui.games;

import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.services.RankingService;
import ca.ucalgary.seng300.platform.session.Session;
import ca.ucalgary.seng300.ui.util.OnlineRankingHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public abstract class CommonGameLogic extends StackPane {

    private ClientCore clientCore = null;
    private boolean onlineEloApplied = false;
    private boolean randomOpponentEnabled = false;

    private Button[][] boardButtonGrid;

    private final int rows;
    private final int cols;
    private final GameType gameType;
    private final String styleClass;
    private final Color player1Color;
    private final Color player2Color;
    private final String player1Name;
    private final String player2Name;

    protected Session session = null;

    @FXML protected Label statusLabel;
    @FXML protected Button resetButton;

    @FXML private GridPane boardGridPane;
    @FXML private VBox eloResultOverlay;
    @FXML private Label eloOutcomeTitle;
    @FXML private Label eloDeltaLabel;
    @FXML private Label eloNewRatingLabel;

    public abstract void reset();

    protected abstract void updateButton(Button button, Player player);
    protected abstract void addHistory(Player player, int row, int col, Color color);
    protected abstract int getActualRow(int row, int col); // this is to figure out row for the Connect4
    protected abstract void maybePlayRandomOpponent();
    protected abstract void submitAgainstAiMove(int row, int col);

    public CommonGameLogic(int rows, int cols, GameType gameType, String styleClass, Color player1Color, Color player2Color, String player1Name, String player2Name) {
        this.rows = rows;
        this.cols = cols;
        this.gameType = gameType;
        this.styleClass = styleClass;
        this.player1Color = player1Color;
        this.player2Color = player2Color;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
    }

    @FXML
    private void handleDismissEloOverlay() {
        hideEloOverlay();
    }

    @FXML
    private void resetClick(ActionEvent event) {
        reset();
    }

    public void initialize() {
        setupBoardButtons();
    }

    public void refreshFromGameState(GameState gameState) {
        if (gameState != null && gameState.getBoard() != null) {
            State state = gameState.getState();
            if (state == State.IN_PROGRESS) {
                onlineEloApplied = false;
                hideEloOverlay();
            }

            updateBoard(gameState.getBoard());

            boolean gameOver = state != State.IN_PROGRESS;
            resetButton.setVisible(gameOver);

            Player myRole = clientCore != null ? clientCore.getMyNetworkRole() : null;
            boolean onlineTurns = clientCore != null && clientCore.isConnected() && myRole != null;
            boolean myTurn = !onlineTurns || gameOver || myRole.equals(gameState.getPlayerTurn());
            setDisableBoard(gameOver || !myTurn);

            updateStatusLabel(gameState, state);

            OnlineRankingHelper.applyIfOnlineGameFinished(clientCore, state, onlineEloApplied, gameType)
                    .ifPresent(r -> {
                        onlineEloApplied = true;
                        showEloOverlay(r);
                    }
            );
        }
    }

    private void setupBoardButtons() {
        boardButtonGrid = new Button[rows][cols];
        for (int i = 0; i < boardButtonGrid.length; i++) {
            for (int j = 0; j < boardButtonGrid[i].length; j++) {
                int row = i, col = j; // this is needed for lambda in setOnAction()

                Button button = new Button();
                button.getStyleClass().add(styleClass);
                button.setOnAction(e -> submitMove(row, col));

                boardButtonGrid[i][j] = button;
                boardGridPane.add(button, i, j);
            }
        }
    }

    private void submitMove(int row, int col) {
        if (isOnlineMode() && clientCore.isConnected())
            submitOnlineMove(row, col);
        else if (isSessionMode())
            submitSessionMove(row, col);
        else
            submitAgainstAiMove(row, col);
    }

    private boolean isOnlineMode() {
        return !randomOpponentEnabled
                && clientCore != null
                && GameType.getFromGameState(clientCore.getLastUpdate()) == gameType;
    }

    private void submitOnlineMove(int row, int col) {
        if (!clientCore.submitMove(row, col))
            statusLabel.setText("Could not send move");
    }

    private boolean isSessionMode() {
        return session != null;
    }

    private void submitSessionMove(int row, int col) {
        if (session.submitMove(row, col)) {
            int actualRow = getActualRow(row, col);
            addHistory(Player.PLAYER_1, actualRow, col, player1Color);
            refreshFromSession();
            maybePlayRandomOpponent();
        }
    }

    private void onGameOver() {
        setDisableBoard(true);
        resetButton.setVisible(true);
    }

    private void updateStatusLabel(GameState gameState, State state) {
        switch(state) {
            case IN_PROGRESS -> {
                Player cur = gameState.getPlayerTurn();
                Player myRole = clientCore != null ? clientCore.getMyNetworkRole() : null;
                if (isOnlineMode() && myRole != null && myRole != cur) {
                    String myName = myRole == Player.PLAYER_1 ? player1Name : player2Name;
                    String opponentName = myRole == Player.PLAYER_1 ? player2Name : player1Name;

                    statusLabel.setText("You are " + myName + " — waiting for " + opponentName);
                }
                else {
                    statusLabel.setText(session.getTurn() == Player.PLAYER_1
                            ? player1Name + " Turn"
                            : player2Name + " Turn");
                }
            }
            case TIE -> statusLabel.setText("Draw!");
            case PLAYER_1_WIN -> {
                statusLabel.setText(player1Name + " Wins!");
                onGameOver();
            }
            case PLAYER_2_WIN -> {
                statusLabel.setText(player2Name + " Wins!");
                onGameOver();
            }
        }
    }

    private void updateStatusLabel(State state) {
        updateStatusLabel(null, state);
    }

    protected void refreshFromSession() {
        updateBoard(session.getBoard());
        updateStatusLabel(session.getState());
    }

    protected void updateBoard(Player[][] board) {
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++)
                updateButton(boardButtonGrid[i][j], board[i][j]);
    }

    protected void setDisableBoard(boolean val) {
        for (Button[] buttons : boardButtonGrid)
            for (Button button : buttons)
                button.setDisable(val);
    }

    protected void showEloOverlay(RankingService.OnlineEloResult r) {
        eloOutcomeTitle.setText(switch (r.outcome()) {
            case WIN -> "Victory!";
            case LOSS -> "Defeat";
            case DRAW -> "Draw";
        });
        int d = r.deltaElo();
        String sign = d >= 0 ? "+" : "";
        eloDeltaLabel.setText("Rating change: " + sign + d + " ELO");
        if (d > 0) {
            eloDeltaLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: #4ade80;");
        } else if (d < 0) {
            eloDeltaLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: #f87171;");
        } else {
            eloDeltaLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: #e5e5e5;");
        }
        eloNewRatingLabel.setText("New rating: " + r.newElo());

        eloResultOverlay.setVisible(true);
        eloResultOverlay.setMouseTransparent(false);
    }

    protected void hideEloOverlay() {
        eloResultOverlay.setVisible(false);
        eloResultOverlay.setMouseTransparent(true);
    }
}
