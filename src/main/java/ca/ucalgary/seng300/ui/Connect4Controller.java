package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import ca.ucalgary.seng300.game.core.connect4.Connect4RandomMoveGenerator;
import ca.ucalgary.seng300.game.core.connect4.C4GameState;
import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.session.Connect4Session;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class Connect4Controller extends GameGUI {
    private static final Player AI_PLAYER = Player.PLAYER_2;
    private CentralController boss;
    private ClientCore clientCore = null;
    private Connect4Session session = null;
    private boolean aiResultRecorded;

    public void connectToRootForLobby(CentralController root) {
        boss = root;
        boss.setConnect4(this);
        clientCore = boss.getCC();

        if (clientCore != null && clientCore.isConnected()) {
            clientCore.setMoveResponseHandler(this::handleMoveResponse);
            clientCore.setPlayerStatsHandler(this::handlePlayerStatsUpdate);
            clientCore.setRematchHandler(this::handleRematchMessage);
            clientCore.setChatMessageHandler(this::handleChatMessage);

            // Request fresh stats from server as source of truth
            if (clientCore.CID != null && !clientCore.CID.isBlank()) {
                clientCore.msgSend(new sysMSG("PLAYER_STATS_REQUEST", clientCore.CID, String.valueOf(Game.CONNECT_FOUR)));
            }
        }
        populateProfileSidebar();

        if (CentralController.playWithAi) {
            session = new Connect4Session();
            aiResultRecorded = false;
            refreshFromGameStateUpdate(session.getGameState());
            return;
        }
        if (clientCore != null && clientCore.getLastUpdate() instanceof C4GameState gs) {
            refreshFromGameStateUpdate(gs);
        }
    }

    @FXML
    private StackPane boardPane;

    @FXML
    private GridPane gridPane;
    private Circle[][] circles = new Circle[6][7];
    private int rows = 6;
    private int cols = 7;
    @FXML
    private Label statusLabel;

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

    private final Color P1 = Color.web("#dd3030ff");
    private final Color P2 = Color.web("#fad44aff");

    @FXML
    public void initialize() {
        render();
        populateProfileSidebar();
        if (chatList != null) {
            chatList.setPlaceholder(new Label("No messages yet."));
        }
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
        resetButton.setVisible(false);
        GameGUI.clearHistory();
    }

    @Override
    public void update() {
        // State-driven rendering updates the label directly from C4GameState.
    }

    @Override
    protected void onGameCompletion() {
        disableBoard();
        resetButton.setVisible(true);
    }

    private void dropPiece(int col) {
        if (CentralController.playWithAi && session != null) {
            if (!session.submitMove(col)) {
                statusLabel.setText("Invalid move");
                return;
            }
            GameGUI.addHistory("Connect4", "P1", "Col " + (col + 1), P1);
            refreshFromGameStateUpdate(session.getGameState());
            maybeRunAiTurn();
            return;
        }

        if (clientCore != null && clientCore.isConnected()) {
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
        statusLabel.setText("No active game session.");
    }

    private void disableBoard() {
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                circles[row][col].setDisable(true);
    }

    public void setVisibility(Boolean status) {
        boardPane.setVisible(status);
    }

    @FXML
    private void resetClick(ActionEvent event) {
        if (CentralController.playWithAi && session != null) {
            session.startNewGame();
            aiResultRecorded = false;
            GameGUI.clearHistory();
            refreshFromGameStateUpdate(session.getGameState());
            return;
        }
        if (clientCore != null && clientCore.isConnected()) {
            GameGUI.clearHistory();
            clientCore.msgSend(new sysMSG("reset", ""));
            statusLabel.setText("Rematch request sent.");
            return;
        }
        if (session != null) {
            session.startNewGame();
            aiResultRecorded = false;
            GameGUI.clearHistory();
            refreshFromGameStateUpdate(session.getGameState());
            return;
        }
        render();
        statusLabel.setText("No active game session.");
    }

    @FXML
    private void handleBackToMode(ActionEvent event) {
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.moveTo("chooseModeNew.fxml");
    }

    public void refreshFromGameStateUpdate(C4GameState update) {
        if (update == null || update.getBoard() == null) {
            return;
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

        boolean gameOver = !State.IN_PROGRESS.equals(update.getState());
        Player myRole = clientCore != null ? clientCore.getMyNetworkRole() : null;
        boolean onlineTurns = clientCore != null && clientCore.isConnected() && myRole != null && !CentralController.playWithAi;
        boolean myTurn = !onlineTurns || gameOver || myRole.equals(update.getPlayerTurn());
        if (CentralController.playWithAi && !gameOver) {
            myTurn = Player.PLAYER_1.equals(update.getPlayerTurn());
        }
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                circles[row][col].setDisable(gameOver || !myTurn);
            }
        }

        if (State.PLAYER_1_WIN.equals(update.getState())) {
            recordAiResultIfNeeded(update.getState());
            statusLabel.setText("Player 1 Wins!");
            onGameCompletion();
        } else if (State.PLAYER_2_WIN.equals(update.getState())) {
            recordAiResultIfNeeded(update.getState());
            statusLabel.setText("Player 2 Wins!");
            onGameCompletion();
        } else if (State.TIE.equals(update.getState())) {
            recordAiResultIfNeeded(update.getState());
            statusLabel.setText("Draw!");
            onGameCompletion();
        } else {
            Player turn = update.getPlayerTurn();
            if (clientCore != null && clientCore.isConnected() && myRole != null && !myRole.equals(turn)) {
                statusLabel.setText(myRole == Player.PLAYER_1 ? "You are Player 1 - waiting for Player 2"
                        : "You are Player 2 - waiting for Player 1");
            } else {
                statusLabel.setText(turn == Player.PLAYER_1 ? "Player 1's Turn" : "Player 2's Turn");
            }
            resetButton.setVisible(false);
        }
    }

    private void handleMoveResponse(sysMSG response) {
        if (response == null || !"MOVE".equals(response.getType())) {
            return;
        }
        if ("FAIL".equals(response.getFieldA())) {
            statusLabel.setText("Invalid move");
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
            statusLabel.setText("Chat unavailable offline.");
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

    private void maybeRunAiTurn() {
        if (!CentralController.playWithAi || session == null || session.isGameOver() || session.getTurn() != AI_PLAYER) {
            return;
        }
        
        // Schedule with a small delay so UI updates are visible
        PauseTransition pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(event -> {
            Integer col = Connect4RandomMoveGenerator.chooseColumn(session.getGameState());
            if (col == null || !session.submitMove(col)) {
                return;
            }
            GameGUI.addHistory("Connect4", "P2", "Col " + (col + 1), P2);
            refreshFromGameStateUpdate(session.getGameState());
            maybeRunAiTurn();
        });
        pause.play();
    }

    @FXML
    private void handleForfeit(ActionEvent event) {
        if (clientCore != null && clientCore.isConnected()) {
            clientCore.msgSend(new sysMSG("FORFEIT", CentralController.lobbyCode != null ? CentralController.lobbyCode : ""));
            statusLabel.setText("Forfeit submitted.");
            return;
        }
        if (CentralController.playWithAi && !aiResultRecorded) {
            if (clientCore != null && clientCore.isConnected()) {
                clientCore.msgSend(new sysMSG("AI_GAME_RESULT", String.valueOf(Game.CONNECT_FOUR), "LOSS"));
            }
            aiResultRecorded = true;
            populateProfileSidebar();
        }
        statusLabel.setText("You forfeited.");
        exitToChooseMode();
    }

    @FXML
    private void handleInfo(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect 4");
        alert.setHeaderText("How to play");
        alert.setContentText(
                "Two players take turns dropping a piece into one of seven columns. "
                        + "The board is seven columns wide and six rows tall; pieces fall to the lowest empty slot in that column.\n\n"
                        + "The first player to connect four pieces in a row - horizontally, vertically, or diagonally - wins.\n\n"
                        + "If the board is full and neither player has four in a row, the game is a draw.\n\n"
                        + "Online: host or join a lobby, then take turns when it is your move. "
                        + "Use Forfeit in the sidebar to resign an online match; ELO may change when the result is recorded."
        );
        alert.showAndWait();
    }

    @FXML
    private void handleExit(MouseEvent event) {
        if (CentralController.getInstance().clientCore != null) {
            CentralController.getInstance().clientCore.sendLogout();
        }
        CentralController.moveTo("Login.fxml");
    }

    private void populateProfileSidebar() {
        if (sidebarGameTitleLabel != null) {
            sidebarGameTitleLabel.setText(CentralController.selectedGame != null ? CentralController.selectedGame : "Connect 4");
        }
        if (profileNameLabel != null) {
            String myUsername = (clientCore != null && clientCore.username != null) ? clientCore.username : "Player";
            profileNameLabel.setText(myUsername + " - " + getPlayerRoleLabel());
        }
        if (profileEmailLabel != null) {
            String myEmail = (clientCore != null && clientCore.email != null) ? clientCore.email : "Player@example.com";
            profileEmailLabel.setText(myEmail);
        }

        // Request fresh stats from server
        if (clientCore != null && clientCore.isConnected() && clientCore.CID != null) {
            clientCore.msgSend(new sysMSG("PLAYER_STATS_REQUEST", clientCore.CID, String.valueOf(Game.CONNECT_FOUR)));
        }
    }

    private void handlePlayerStatsUpdate(sysMSG message) {
        if (message == null) {
            return;
        }
        // Display fresh server data directly - this is the source of truth
        if (eloValueLabel != null && message.getFieldA() != null) {
            eloValueLabel.setText(message.getFieldA());
        }
        if (gamesPlayedValueLabel != null && message.getFieldB() != null) {
            gamesPlayedValueLabel.setText(message.getFieldB());
        }
        if (tierValueLabel != null && message.getPayload() != null) {
            tierValueLabel.setText(message.getPayload());
        }
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
            statusLabel.setText("Rematch declined.");
            exitToChooseMode();
            return;
        }
        if ("FORFEIT_COMPLETE".equals(message.getType())) {
            statusLabel.setText("Match ended by forfeit.");
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
        if (clientCore != null && clientCore.isConnected()) {
            String result = "DRAW";
            if (State.PLAYER_1_WIN.equals(state)) {
                result = "WIN";
            } else if (State.PLAYER_2_WIN.equals(state)) {
                result = "LOSS";
            }
            clientCore.msgSend(new sysMSG("AI_GAME_RESULT", String.valueOf(Game.CONNECT_FOUR), result));
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
