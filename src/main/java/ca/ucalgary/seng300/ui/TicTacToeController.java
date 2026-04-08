package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.session.TicTacToeSession;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;

public class TicTacToeController extends GameGUI {

    private CentralController boss;
    public void setBoss(CentralController c){
        boss = c;
        boss.setTic(this);
        clientCore = boss.getCC();
    }

    /**
     * Standalone Tic-Tac-Toe screen (lobby → start game): same wiring as {@link GameGUIController},
     * clears local {@link TicTacToeSession} when online, and paints from the last server state if present.
     */
    public void connectToRootForLobby(CentralController root) {
        setBoss(root);
        if (clientCore != null && clientCore.isConnected()) {
            session = null;
            clientCore.setMoveResponseHandler(this::handleMoveResponse);
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
            GameGUI.clearHistory();
            refreshFromGameStateUpdate(session.getGameState());
            return;
        }
        GameGUI.clearHistory();
        if (clientCore != null && clientCore.isConnected()) {
            clientCore.msgSend(new sysMSG("reset",""));
            return;
        }
        render();
        turnLabel.setText("No active game session.");
    }

    public void setSession(TicTacToeSession s) {
        this.session = s;
        if (s != null) refreshFromGameStateUpdate(s.getGameState());
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
    private void handleBackToMode(ActionEvent event) {
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.moveTo("chooseModeNew.fxml");
    }
}
