package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.game.core.tictactoe.TicTacToeRandomMoveGenerator;
import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.session.TicTacToeSession;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;

public class TicTacToeController extends GameGUI {
    private static final Player AI_PLAYER = Player.PLAYER_2;

    private CentralController boss;
    private boolean aiResultRecorded;

    public void setBoss(CentralController c){
        boss = c;
        boss.setTic(this);
        clientCore = CentralController.playWithAi ? null : boss.getCC();
    }

    /**
     * Standalone Tic-Tac-Toe screen (lobby → start game): same wiring as {@link GameGUIController},
     * clears local {@link TicTacToeSession} when online, and paints from the last server state if present.
     */
    public void connectToRootForLobby(CentralController root) {
        setBoss(root);
        if (CentralController.playWithAi) {
            session = new TicTacToeSession();
            aiResultRecorded = false;
            refreshFromGameStateUpdate(session.getGameState());
            return;
        }
        if (clientCore != null && clientCore.isConnected()) {
            session = null;
            clientCore.setMoveResponseHandler(this::handleMoveResponse);
            clientCore.setPlayerStatsHandler(this::handlePlayerStatsUpdate);
            clientCore.setRematchHandler(this::handleRematchMessage);
            clientCore.setChatMessageHandler(this::handleChatMessage);
        }
        if (clientCore != null && clientCore.getLastUpdate() instanceof TTTGameState gs) {
            refreshFromGameStateUpdate(gs);
        }
    }


    /** When set, moves and state use the local session. */
    private TicTacToeSession session = null;
    /** When set, moves are sent to the server and UI is refreshed from TTTGameState (online mode). */
    private ClientCore clientCore = null;

    @FXML
    private GridPane ticTacToeGrid;

    @FXML
    private Label turnLabel;

    @FXML
    private Button resetButton;

    @FXML
    private ListView<String> chatList;

    @FXML
    private TextField chatInput;

    @FXML
    private Button sendChatButton;
    @FXML
    private Label sidebarGameTitleLabel;
    @FXML
    private Label profileNameLabel;
    @FXML
    private Label profileEmailLabel;
    @FXML
    private Label eloValueLabel;
    @FXML
    private Label tierValueLabel;
    @FXML
    private Label gamesPlayedValueLabel;

    private static final Color xColor = Color.web("#3B82F6");
    private static final Color oColor = Color.web("#A855F7");

    private String getToken(Player player) {
        if (Player.PLAYER_1.equals(player)) return "X";
        if (Player.PLAYER_2.equals(player)) return "O";
        if (Player.EMPTY.equals(player)) return "";
        return "ERR";
    }

    @FXML
    public void initialize() {
        status = GameStatus.IN_PROGRESS;
        populateProfileSidebar();
        if (chatList != null) {
            chatList.setPlaceholder(new Label("No messages yet."));
        }
    }

    @Override
    public void render() {
        for (Node node : ticTacToeGrid.getChildren()) {
            if (node instanceof Button btn) {
                btn.setText("");
                btn.setStyle("");
                btn.setDisable(false);
            }
        }
        resetButton.setVisible(false);
        GameGUI.clearHistory();
    }

    @Override
    public void update() {
        // State-driven rendering updates the label directly from TTTGameState.
    }

    @Override
    protected void onGameCompletion() {
        disableBoard();
        resetButton.setVisible(true);
    }

    @FXML
    private void handleClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        Integer row = GridPane.getRowIndex(clickedButton);
        Integer col = GridPane.getColumnIndex(clickedButton);
        if (row == null) row = 0;
        if (col == null) col = 0;

        if (clientCore != null && clientCore.isConnected()) {
            Player myRole = clientCore.getMyNetworkRole();
            if (myRole != null && clientCore.getLastUpdate() instanceof TTTGameState gs) {
                if (State.IN_PROGRESS.equals(gs.getState()) && !myRole.equals(gs.getPlayerTurn())) {
                    turnLabel.setText(myRole == Player.PLAYER_1 ? "Waiting for O…" : "Waiting for X…");
                    return;
                }
            }
            if (!clientCore.submitMove(row, col)) {
                String base = turnLabel.getText() != null ? turnLabel.getText() : "";
                turnLabel.setText(base + " (could not send move)");
            }
            return;
        }

        if (session != null) {
            if (!session.submitMove(row, col)) return;
            String mover = session.getTurn() == Player.PLAYER_1 ? "O" : "X";
            Color moverColor = session.getTurn() == Player.PLAYER_1 ? oColor : xColor;
            GameGUI.addHistory("TicTacToe", mover, "(" + (row + 1) + ", " + (col + 1) + ")", moverColor);
            refreshFromGameStateUpdate(session.getGameState());
            maybeRunAiTurn();
            return;
        }

        turnLabel.setText("No active game session.");
    }

    private void disableBoard() {
        for (Node node : ticTacToeGrid.getChildren()) {
            if (node instanceof Button) {
                node.setDisable(true);
            }
        }
    }

    @FXML
    void resetClick(ActionEvent event) {
        if (session != null) {
            session.startNewGame();
            aiResultRecorded = false;
            GameGUI.clearHistory();
            refreshFromGameStateUpdate(session.getGameState());
            return;
        }
        GameGUI.clearHistory();
        if (clientCore != null && clientCore.isConnected()) {
            clientCore.msgSend(new sysMSG("reset",""));
            turnLabel.setText("Rematch request sent.");
            return;
        }
        render();
        turnLabel.setText("No active game session.");
    }

    public void setSession(TicTacToeSession s) {
        this.session = s;
        if (s != null) refreshFromGameStateUpdate(s.getGameState());
    }

    private void maybeRunAiTurn() {
        if (!CentralController.playWithAi || session == null || session.isGameOver() || session.getTurn() != AI_PLAYER) {
            return;
        }
        int[] move = TicTacToeRandomMoveGenerator.chooseMove(session.getGameState());
        if (move == null || !session.submitMove(move[0], move[1])) {
            return;
        }
        GameGUI.addHistory("TicTacToe", "O", "(" + (move[0] + 1) + ", " + (move[1] + 1) + ")", oColor);
        refreshFromGameStateUpdate(session.getGameState());
    }

    /** Use when in online mode: refresh grid and label from server update. */
    public void refreshFromGameStateUpdate(TTTGameState update) {
        if (update == null || update.getBoard() == null) return;
        Player[][] currentBoard = update.getBoard();
        boolean gameOver = !State.IN_PROGRESS.equals(update.getState());
        for (Node node : ticTacToeGrid.getChildren()) {
            if (node instanceof Button btn) {
                Integer r = GridPane.getRowIndex(btn);
                Integer c = GridPane.getColumnIndex(btn);

                if (r == null) r = 0;
                if (c == null) c = 0;

                btn.setText(getToken(currentBoard[r][c]));
                if (Player.PLAYER_1.equals(currentBoard[r][c])) {
                    btn.setStyle("-fx-text-fill:#3B82F6; -fx-font-size:40px; -fx-font-weight:bold;");
                } else if (Player.PLAYER_2.equals(currentBoard[r][c])) {
                    btn.setStyle("-fx-text-fill:#A855F7; -fx-font-size:40px; -fx-font-weight:bold;");
                } else {
                    btn.setStyle("");
                }
                Player myRole = clientCore != null ? clientCore.getMyNetworkRole() : null;
                boolean onlineTurns = clientCore != null && clientCore.isConnected() && myRole != null;
                boolean myTurn = !onlineTurns || gameOver || myRole.equals(update.getPlayerTurn());
                btn.setDisable(gameOver || !myTurn);
            }
        }
        resetButton.setVisible(gameOver);
        State status = update.getState();
        recordAiResultIfNeeded(status);
        if (State.PLAYER_1_WIN.equals(status)) {
            turnLabel.setText("X Wins!");
            onGameCompletion();
        } else if (State.PLAYER_2_WIN.equals(status)) {
            turnLabel.setText("O Wins!");
            onGameCompletion();
        } else if (State.TIE.equals(status)) {
            turnLabel.setText("Draw!");
            onGameCompletion();
        } else {
            Player cur = update.getPlayerTurn();
            Player myRole = clientCore != null ? clientCore.getMyNetworkRole() : null;
            if (clientCore != null && clientCore.isConnected() && myRole != null && !myRole.equals(cur)) {
                turnLabel.setText(myRole == Player.PLAYER_1 ? "You are X — waiting for O" : "You are O — waiting for X");
            } else {
                turnLabel.setText(cur == Player.PLAYER_1 ? "X's Turn" : "O's Turn");
            }
        }
    }

    private void handleMoveResponse(sysMSG response) {
        if (response == null || !"MOVE".equals(response.getType())) {
            return;
        }
        if ("FAIL".equals(response.getFieldA())) {
            turnLabel.setText("Invalid move");
        }
    }

    @FXML
    private void handleSendChat(ActionEvent event) {
        if (chatInput == null) {
            return;
        }
        String text = chatInput.getText();
        if (text == null || text.isBlank()) {
            return;
        }
        if (clientCore == null || !clientCore.isConnected()) {
            turnLabel.setText("Chat unavailable offline.");
            return;
        }
        String sender = clientCore.username != null && !clientCore.username.isBlank() ? clientCore.username : "Player";
        String senderId = clientCore.CID != null ? clientCore.CID : "";
        clientCore.msgSend(new chatMessage(CentralController.lobbyCode, sender, senderId, text.trim()));
        chatInput.clear();
    }

    private void handleChatMessage(chatMessage message) {
        if (message == null || chatList == null) {
            return;
        }
        String sender = message.getUserName() != null && !message.getUserName().isBlank()
                ? message.getUserName()
                : "Player";
        String content = message.getContent() != null ? message.getContent() : "";
        chatList.getItems().add(sender + ": " + content);
    }

    @FXML
    private void handleBackToMode(ActionEvent event) {
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.moveTo("chooseModeNew.fxml");
    }

    @FXML
    private void handleForfeit(ActionEvent event) {
        if (clientCore != null && clientCore.isConnected()) {
            clientCore.msgSend(new sysMSG("FORFEIT", CentralController.lobbyCode != null ? CentralController.lobbyCode : ""));
            turnLabel.setText("Forfeit submitted.");
            return;
        }
        if (CentralController.playWithAi && !aiResultRecorded) {
            GameProfileHelper.applyLocalResult(Game.TICK_TACK_TOE, GameProfileHelper.LocalMatchResult.FORFEIT);
            aiResultRecorded = true;
            populateProfileSidebar();
        }
        turnLabel.setText("You forfeited.");
        exitToChooseMode();
    }

    @FXML
    private void handleInfo(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tic Tac Toe");
        alert.setHeaderText("How to play");
        alert.setContentText(
                "Two players take turns on a 3x3 grid. You play as X or O depending on the match.\n\n"
                        + "Place your mark on an empty cell each turn. The first player to get three in a row - horizontally, vertically, or diagonally - wins the game.\n\n"
                        + "If every cell is filled and no one has three in a row, the game is a draw.\n\n"
                        + "Online: create or join a lobby from the game hub, then play in real time. "
                        + "While a ranked online match is in progress, use Forfeit in the sidebar if you must leave; ratings may update when the game ends."
        );
        alert.showAndWait();
    }

    @FXML
    private void handleExit(ActionEvent event) {
        Platform.exit();
    }

    private void populateProfileSidebar() {
        GameProfileHelper.ProfileData data = GameProfileHelper.loadProfile(Game.TICK_TACK_TOE);
        if (sidebarGameTitleLabel != null) {
            sidebarGameTitleLabel.setText(data.gameTitle());
        }
        if (profileNameLabel != null) {
            profileNameLabel.setText(data.username() + " - " + getPlayerRoleLabel());
        }
        if (profileEmailLabel != null) {
            profileEmailLabel.setText(data.email());
        }
        if (eloValueLabel != null) {
            eloValueLabel.setText(data.elo());
        }
        if (tierValueLabel != null) {
            tierValueLabel.setText(data.tier());
        }
        if (gamesPlayedValueLabel != null) {
            gamesPlayedValueLabel.setText(data.gamesPlayed());
        }
    }

    private void handlePlayerStatsUpdate(sysMSG message) {
        if (message == null) {
            return;
        }
        if (eloValueLabel != null && message.getFieldA() != null) {
            eloValueLabel.setText(message.getFieldA());
        }
        if (gamesPlayedValueLabel != null && message.getFieldB() != null) {
            gamesPlayedValueLabel.setText(message.getFieldB());
        }
        if (tierValueLabel != null && message.getPayload() != null) {
            tierValueLabel.setText(message.getPayload());
        }
        populateProfileSidebar();
    }

    private void handleRematchMessage(sysMSG message) {
        if (message == null) {
            return;
        }
        if ("FORFEIT_NOTICE".equals(message.getType())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Opponent Forfeited");
            alert.setHeaderText("Match ended");
            alert.setContentText(message.getPayload() != null && !message.getPayload().isBlank()
                    ? message.getPayload()
                    : "The other player has forfeited.");
            alert.showAndWait();
            return;
        }
        if ("REMATCH_REQUEST".equals(message.getType())) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Rematch Request");
            alert.setHeaderText("Your opponent wants a rematch");
            alert.setContentText("Do you want to play another game?");
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(yes, no);
            ButtonType result = alert.showAndWait().orElse(no);
            if (clientCore != null && clientCore.isConnected()) {
                clientCore.msgSend(new sysMSG("REMATCH_RESPONSE", result == yes ? "ACCEPT" : "DECLINE", ""));
            }
            return;
        }
        if ("REMATCH_DECLINED".equals(message.getType())) {
            turnLabel.setText("Rematch declined.");
            exitToChooseMode();
            return;
        }
        if ("FORFEIT_COMPLETE".equals(message.getType())) {
            turnLabel.setText("Match ended by forfeit.");
            exitToChooseMode();
        }
    }

    private String getPlayerRoleLabel() {
        if (CentralController.playWithAi || session != null) {
            return "Player 1";
        }
        if (clientCore != null) {
            Player role = clientCore.getMyNetworkRole();
            if (role == Player.PLAYER_2) {
                return "Player 2";
            }
        }
        return "Player 1";
    }

    private void recordAiResultIfNeeded(State state) {
        if (!CentralController.playWithAi || aiResultRecorded || state == null || State.IN_PROGRESS.equals(state)) {
            return;
        }
        if (State.PLAYER_1_WIN.equals(state)) {
            GameProfileHelper.applyLocalResult(Game.TICK_TACK_TOE, GameProfileHelper.LocalMatchResult.WIN);
        } else if (State.PLAYER_2_WIN.equals(state)) {
            GameProfileHelper.applyLocalResult(Game.TICK_TACK_TOE, GameProfileHelper.LocalMatchResult.LOSS);
        } else {
            GameProfileHelper.applyLocalResult(Game.TICK_TACK_TOE, GameProfileHelper.LocalMatchResult.DRAW);
        }
        aiResultRecorded = true;
        populateProfileSidebar();
    }

    private void exitToChooseMode() {
        CentralController.lobbyCode = null;
        CentralController.lobbyPaired = false;
        CentralController.hostingLobby = false;
        CentralController.playWithAi = false;
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.moveTo("chooseModeNew.fxml");
    }
}
