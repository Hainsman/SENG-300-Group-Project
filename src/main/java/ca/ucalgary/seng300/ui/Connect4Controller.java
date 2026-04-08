package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import ca.ucalgary.seng300.game.core.connect4.C4GameState;
import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.event.ActionEvent;

public class Connect4Controller extends GameGUI {
    private CentralController boss;
    private ClientCore clientCore = null;

    public void connectToRootForLobby(CentralController root) {
        boss = root;
        boss.setConnect4(this);
        clientCore = boss.getCC();
        if (clientCore != null && clientCore.isConnected()) {
            clientCore.setMoveResponseHandler(this::handleMoveResponse);
            clientCore.setChatMessageHandler(this::handleChatMessage);
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

    private final Color P1 = Color.web("#dd3030ff");
    private final Color P2 = Color.web("#fad44aff");

    @FXML
    public void initialize() {
        render();
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
                circles[row][col].setOnMouseClicked(null);
    }

    public void setVisibility(Boolean status) {
        boardPane.setVisible(status);
    }

    @FXML
    private void resetClick(ActionEvent event) {
        if (clientCore != null && clientCore.isConnected()) {
            clientCore.msgSend(new sysMSG("reset", ""));
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
        boolean onlineTurns = clientCore != null && clientCore.isConnected() && myRole != null;
        boolean myTurn = !onlineTurns || gameOver || myRole.equals(update.getPlayerTurn());
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                circles[row][col].setDisable(gameOver || !myTurn);
            }
        }

        if (State.PLAYER_1_WIN.equals(update.getState())) {
            statusLabel.setText("Player 1 Wins!");
            onGameCompletion();
        } else if (State.PLAYER_2_WIN.equals(update.getState())) {
            statusLabel.setText("Player 2 Wins!");
            onGameCompletion();
        } else if (State.TIE.equals(update.getState())) {
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
}
