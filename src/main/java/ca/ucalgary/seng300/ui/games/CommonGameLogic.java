package ca.ucalgary.seng300.ui.games;

import ca.ucalgary.seng300.game.core.*;
import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.services.RankingService;
import ca.ucalgary.seng300.platform.session.Session;
import ca.ucalgary.seng300.ui.CentralController;
import ca.ucalgary.seng300.ui.util.Navigation;
import ca.ucalgary.seng300.ui.util.OnlineRankingHelper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Arrays;

public abstract class CommonGameLogic extends StackPane {

    private ClientCore clientCore = null;
    private boolean onlineEloApplied = false;

    private Button[][] boardButtonGrid;

    protected final CentralController rootController;
    protected final GameGUIPage parentGUI;
    private final int rows;
    private final int cols;
    private final GameType gameType;
    private final String styleClass;
    private final Color player1Color;
    private final Color player2Color;
    private final String player1Name;
    private final String player2Name;

    protected Session session = null;
    protected Player[][] currentPlayerBoard;

    @FXML protected Label statusLabel;
    @FXML protected Button resetButton;
    @FXML protected Button backToModeButton;

    @FXML protected GridPane boardGridPane;
    @FXML protected VBox eloResultOverlay;
    @FXML protected Label eloOutcomeTitle;
    @FXML protected Label eloDeltaLabel;
    @FXML protected Label eloNewRatingLabel;

    protected abstract void updateButton(Button button, Player player);
    protected abstract void setNewSession();

    public CommonGameLogic(CentralController rootController, GameGUIPage parentGUI, int rows, int cols, GameType gameType, String styleClass, Color player1Color, Color player2Color, String player1Name, String player2Name) {
        this.rootController = rootController;
        this.parentGUI = parentGUI;
        this.rows = rows;
        this.cols = cols;
        this.gameType = gameType;
        this.styleClass = styleClass;
        this.player1Color = player1Color;
        this.player2Color = player2Color;
        this.player1Name = player1Name;
        this.player2Name = player2Name;

        createNewPlayerBoard();
    }

    @FXML
    protected void handleBackToMode(ActionEvent event) {
        if (isOnlineMode() && clientCore.getLastUpdate().getState() == State.IN_PROGRESS) {
            new Alert(Alert.AlertType.INFORMATION,
                    "You cannot leave an online match in progress without using Forfeit in the sidebar.",
                    ButtonType.OK).showAndWait();
            return;
        }
        navigateBackFromGame();
    }

    @FXML
    protected void handleDismissEloOverlay() {
        hideEloOverlay();
    }

    @FXML
    protected void resetClick(ActionEvent event) {
        parentGUI.clearHistory();
        resetButton.setVisible(false);

        if (isSessionMode()) {
            session.startNewGame();
            refreshFromSession();
            setDisableBoard(false);
        }
        else if (isOnlineMode())
            clientCore.msgSend(new sysMSG("reset", ""));
    }

    @FXML
    public void initialize() {
        VBox.setVgrow(this, Priority.ALWAYS);
        setupBoardButtons();
    }

    public void reset() {
        createNewPlayerBoard();
        updateBoard(currentPlayerBoard);
        hideEloOverlay();
        if (rootController.localAiMode) {
            if (session == null)
                setNewSession();
            else
                session.startNewGame();
            refreshFromSession();
        }
        else {
            session = null;
            clientCore = rootController.clientCore;

            refreshFromGameState(clientCore.getLastUpdate());
        }
        refreshBackButtonVisibility();
        parentGUI.clearChat();
        parentGUI.clearHistory();
    }

    public void handleForfeitFromShell() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Forfeit");
        confirm.setHeaderText("Forfeit this match?");
        confirm.setContentText("Online: your opponent wins and ratings may update. Local / vs AI: you leave this game.");
        confirm.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
            hideEloOverlay();
            if (!isOnlineMode()) {
                navigateBackFromGame();
                return;
            }
            clientCore.msgSend(new sysMSG("FORFEIT", "", ""));
        });
    }

    public void refreshFromGameState(GameState gameState) {
        if (gameState != null && gameState.getBoard() != null) {
            State state = gameState.getState();
            if (state == State.IN_PROGRESS) {
                onlineEloApplied = false;
                hideEloOverlay();
            }

            updateBoard(gameState.getBoard());
            updateControls(gameState);
            updateStatusLabel(gameState, state);
            updateHistoryBoard(gameState);

            OnlineRankingHelper.applyIfOnlineGameFinished(clientCore, state, onlineEloApplied, gameType, rootController)
                    .ifPresent(r -> {
                        onlineEloApplied = true;
                        showEloOverlay(r);
                    }
            );

            refreshBackButtonVisibility();
        }
    }

    public boolean isOnlineMode() {
        return !rootController.localAiMode
                && clientCore != null
                && GameType.getFromGameState(clientCore.getLastUpdate()) == gameType;
    }

    private void createNewPlayerBoard() {
        currentPlayerBoard = new Player[rows][cols];
        for (Player[] players : currentPlayerBoard) Arrays.fill(players, Player.EMPTY);
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
                boardGridPane.add(button, j, i);

                if (gameType == GameType.CONNECT4) {
                    button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    GridPane.setHgrow(button, Priority.ALWAYS);
                    GridPane.setVgrow(button, Priority.ALWAYS);
                }
            }
        }
        if (gameType == GameType.CONNECT4)
            boardGridPane.maxHeightProperty().bind(boardGridPane.widthProperty().divide(cols).multiply(rows));
    }

    private void addHistory(Player[][] newBoard, Player whoseTurn) {
        int i = 0;
        int j = 0;
        boolean found = false;
        while (!found && i < newBoard.length) {
            j = 0;
            while (!found && j < newBoard[i].length) {
                if (newBoard[i][j] != currentPlayerBoard[i][j])
                    found = true;
                else
                    j++;
            }
            if (!found)
                i++;
        }
        parentGUI.addHistory(
                gameType.toString(),
                whoseTurn == Player.PLAYER_1 ? player1Name : player2Name,
                String.format("(%d, %d)", i+1, j+1),
                whoseTurn == Player.PLAYER_1 ? player1Color : player2Color);

        currentPlayerBoard = newBoard;
    }

    private void maybePlayRandomOpponent() {
        Platform.runLater(() -> {
            if (!session.isGameOver()) {
                int[] cell = RandomMoveGenerator.pickRandomLegalMove(session.getBoard(), gameType);

                if (session.submitMove(cell[0], cell[1])) { // isGameOver() should protect from null
                    addHistory(session.getBoard(), Player.PLAYER_2);
                    refreshFromSession();
                }
            }
        });
    }

    private void submitMove(int row, int col) {
        if (isOnlineMode() && clientCore.isConnected())
            submitOnlineMove(row, col);
        else if (isSessionMode())
            submitSessionMove(row, col);
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
            addHistory(session.getBoard(), Player.PLAYER_1);
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
                if (isOnlineMode()) {
                    Player cur = gameState.getPlayerTurn();
                    Player myRole = clientCore.getMyNetworkRole();
                    String myName = myRole == Player.PLAYER_1 ? player1Name : player2Name;
                    String opponentName = myRole == Player.PLAYER_1 ? player2Name : player1Name;

                    if (myRole != cur)
                        statusLabel.setText("You are " + myName + " — waiting for " + opponentName);
                    else
                        statusLabel.setText("You are " + myName + " — Your turn");
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

    private State resolveDisplayedState() {
        if (isOnlineMode())
            return clientCore.getLastUpdate().getState();

        return session.getState();
    }

    private void refreshBackButtonVisibility() {
        if (backToModeButton == null) {
            return;
        }
        State st = resolveDisplayedState();
        boolean terminal = st == State.PLAYER_1_WIN || st == State.PLAYER_2_WIN || st == State.TIE;
        boolean showBack = !isOnlineMode() || terminal;
        backToModeButton.setVisible(showBack);
    }

    private void navigateBackFromGame() {
        rootController.localAiMode = false;
        hideEloOverlay();

        if (rootController.previousScreen != null)
            rootController.moveTo(rootController.previousScreen);
        else
            rootController.moveTo(Navigation.GAME_DASHBOARD);
    }

    private boolean isBoardBlank(Player[][] board) {
        for (Player[] players : board)
            for (Player player : players)
                if (player != Player.EMPTY)
                    return false;

        return true;
    }

    private void updateControls(GameState gameState) {
        boolean gameOver = gameState.getState() != State.IN_PROGRESS;
        resetButton.setVisible(gameOver);

        Player myRole = clientCore.getMyNetworkRole();
        boolean onlineTurns = clientCore.isConnected() && myRole != null;
        boolean myTurn = !onlineTurns || gameOver || myRole == gameState.getPlayerTurn();
        setDisableBoard(gameOver || !myTurn);
        if (myTurn && !gameOver)
            rootController.getWindowManager().showNotification("Your Turn!", true);
    }

    private void updateHistoryBoard(GameState gameState) {
        if (isBoardBlank(gameState.getBoard()))
            parentGUI.clearHistory();
        else {
            if (gameState.getState() != State.IN_PROGRESS)
                addHistory(gameState.getBoard(), gameState.getPlayerTurn() == Player.PLAYER_1 ? Player.PLAYER_1 : Player.PLAYER_2);
            else
                addHistory(gameState.getBoard(), gameState.getPlayerTurn() == Player.PLAYER_1 ? Player.PLAYER_2 : Player.PLAYER_1);
        }
    }

    protected void refreshFromSession() {
        if (session.getTurn() == Player.PLAYER_1 && !session.isGameOver())
            rootController.getWindowManager().showNotification("Your Turn!", false);

        updateBoard(session.getBoard());
        updateStatusLabel(session.getState());
        refreshBackButtonVisibility();
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
