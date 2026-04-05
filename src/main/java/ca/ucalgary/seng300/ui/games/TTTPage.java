package ca.ucalgary.seng300.ui.games;

import ca.ucalgary.seng300.game.client.ClientState;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.game.core.tictactoe.TicTacToeRandomMoveGenerator;
import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.session.TicTacToeSession;
import ca.ucalgary.seng300.ui.*;
import ca.ucalgary.seng300.ui.util.EloOverlayUtil;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import ca.ucalgary.seng300.ui.util.OnlineRankingHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;

import java.io.IOException;

public class TTTPage extends StackPane implements Navigable {

    private ClientState status;

    private CentralController rootController;
    private GameGUIPage parentGUI;

    private Player[][] board = new Player[3][3]; // stores PLAYER_1 PLAYER_2 NULL
    private Player myPlayer = Player.EMPTY;    // which player this user is, PLAYER_1 or PLAYER_2
    private boolean xTurn = true; // X starts
    private int moves = 0;

    private static final Color xColor = Color.web("#3B82F6");
    private static final Color oColor = Color.web("#A855F7");

    private final String player1Token = "X";
    private final String player2Token = "O";
    private final String emptyToken = "";

    /** When set, moves and state use the game core session; when null, existing local board logic is used. */
    private TicTacToeSession session = null;
    /** When set, moves are sent to the server and UI is refreshed from GameStateUpdate (online mode). */
    private ClientCore clientCore = null;
    private boolean onlineEloApplied = false;
    /**
     * Local play: after the human (X) moves via {@link #session}, O plays a random legal cell
     * using {@link TicTacToeRandomMoveGenerator}.
     */
    private boolean randomOpponentEnabled = false;

    @FXML private Button backToModeButton;
    @FXML private StackPane boardPane;

    @FXML private VBox eloResultOverlay;
    @FXML private Label eloOutcomeTitle;
    @FXML private Label eloDeltaLabel;
    @FXML private Label eloNewRatingLabel;

    @FXML private Button cell00;
    @FXML private Button cell01;
    @FXML private Button cell02;
    @FXML private Button cell10;
    @FXML private Button cell11;
    @FXML private Button cell12;
    @FXML private Button cell20;
    @FXML private Button cell21;
    @FXML private Button cell22;

    @FXML private GridPane ticTacToeGrid;

    @FXML private Label turnLabel;

    @FXML private Button resetButton;

    public TTTPage(CentralController rootController, GameGUIPage parentGUI) {
        try {
            this.rootController = rootController;
            this.parentGUI = parentGUI;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ca.ucalgary.seng300.ui/" + getNavigation().getFxml()));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Online Tic-Tac-Toe inside {@link GameGUIController}: no local {@link TicTacToeSession}, moves go through
     * {@link ClientCore}; board paints from {@link ClientCore#getLastUpdate()} when present.
     */
    public void prepareForNetworkPlayInShell() {
        randomOpponentEnabled = false;
        session = null;
        onlineEloApplied = false;
        EloOverlayUtil.hide(eloResultOverlay);
        if (rootController != null) {
            clientCore = rootController.getCC();
        }
        render();
        if (clientCore != null && clientCore.getLastUpdate() instanceof TTTGameState gs) {
            refreshFromGameStateUpdate(gs);
        }
        refreshBackButtonVisibility();
    }

    public void setRandomOpponentEnabled(boolean enabled) {
        this.randomOpponentEnabled = enabled;
        refreshBackButtonVisibility();
    }

    /** Online match in TTT shell: connected, not local AI, not using a local {@link TicTacToeSession}. */
    public boolean isStrictOnlineTtt() {
        return !randomOpponentEnabled
                && session == null
                && clientCore != null
                && clientCore.isConnected();
    }

    /**
     * True while an online TTT game is still in progress (must use Forfeit / finish; no Back or leaving via Info).
     */
    public boolean isActiveOnlineMatch() {
        if (!isStrictOnlineTtt()) {
            return false;
        }
        if (clientCore.getLastUpdate() instanceof TTTGameState gs) {
            return State.IN_PROGRESS.equals(gs.getState());
        }
        return true;
    }

    private void refreshBackButtonVisibility() {
        if (backToModeButton == null) {
            return;
        }
        State st = resolveDisplayedState();
        boolean terminal = st == State.PLAYER_1_WIN || st == State.PLAYER_2_WIN || st == State.TIE;
        boolean showBack = !isStrictOnlineTtt() || terminal;
        backToModeButton.setVisible(showBack);
    }

    private State resolveDisplayedState() {
        if (session != null) {
            return session.getState();
        }
        if (clientCore != null && clientCore.getLastUpdate() instanceof TTTGameState gs) {
            return gs.getState();
        }
        return State.IN_PROGRESS;
    }

    public void setPlayer(Player p){
        myPlayer = p;
    }

    private String getToken (Player p){
        if(p.equals(Player.PLAYER_1)){
            return player1Token;
        }
        else if (p.equals(Player.PLAYER_2)){
            return player2Token;
        }
        else if (p.equals(Player.EMPTY)){
            return emptyToken;
        }
        else{
            // this should probably never happen, but if null needs a token it goes here
            return "ERR";
        }
    }

    @FXML
    public void initialize() {
        status = ClientState.IN_PROGRESS;
        refreshBackButtonVisibility();
    }

    public void render() {
        board = new Player[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = Player.EMPTY;
            }
        }
        moves = 0;
        xTurn = true;
        for (Node node : ticTacToeGrid.getChildren()) {
            if (node instanceof Button btn) {
                btn.setText("");
                btn.setStyle("");
                btn.setDisable(false);
            }
        }
        parentGUI.clearHistory();
    }

    public void update() {
        if (turnLabel == null) {
            return;
        }
        switch (status) {
            case IN_PROGRESS -> turnLabel.setText(xTurn ? "X's Turn" : "O's Turn");
            case WIN -> turnLabel.setText((xTurn ? "O" : "X") + " Wins!");
            case DRAW -> turnLabel.setText("Draw!");
            default -> {
            }
        }
    }

    protected void onGameCompletion() {
        disableBoard();
        resetButton.setVisible(true);
    }

    private String checkWinner() {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (!board[i][0].equals(Player.EMPTY) &&
                    board[i][0].equals(board[i][1]) &&
                    board[i][1].equals(board[i][2])) {
                return getToken(board[i][0]);
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            if (!board[0][j].equals(Player.EMPTY) &&
                    board[0][j].equals(board[1][j]) &&
                    board[1][j].equals(board[2][j])) {
                return getToken(board[0][j]);
            }
        }

        // Check diagonals
        if (!board[0][0].equals(Player.EMPTY) &&
                board[0][0].equals(board[1][1]) &&
                board[1][1].equals(board[2][2])) {
            return getToken(board[0][0]);
        }
        if (!board[0][2].equals(Player.EMPTY) &&
                board[0][2].equals(board[1][1]) &&
                board[1][1].equals(board[2][0])) {
            return getToken(board[0][2]);
        }

        return null;
    }

    @FXML
    private void handleClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        Integer row = GridPane.getRowIndex(clickedButton);
        Integer col = GridPane.getColumnIndex(clickedButton);
        if (row == null) row = 0;
        if (col == null) col = 0;

        if (!randomOpponentEnabled && clientCore != null && clientCore.isConnected()) {
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
            if (!session.submitMove(row, col)) {
                return;
            }
            Color moverColor = xColor;
            parentGUI.addHistory("TicTacToe", "X", "(" + (row + 1) + ", " + (col + 1) + ")", moverColor);
            refreshFromSession();
            maybePlayRandomOpponent();
            return;
        }

        if (board[row][col] != null && !board[row][col].equals(Player.EMPTY)) {
            return;
        }

        if (xTurn) {
            clickedButton.setText(player1Token);
            clickedButton.setStyle("-fx-text-fill:#3B82F6; -fx-font-size:40px; -fx-font-weight:bold;");
            board[row][col] = Player.PLAYER_1;
            parentGUI.addHistory("TicTacToe", "X",
                    "(" + (row + 1) + ", " + (col + 1) + ")", xColor);
        } else {
            clickedButton.setText(player2Token);
            clickedButton.setStyle("-fx-text-fill:#A855F7; -fx-font-size:40px; -fx-font-weight:bold;");
            board[row][col] = Player.PLAYER_2;
            parentGUI.addHistory("TicTacToe", "O",
                    "(" + (row + 1) + ", " + (col + 1) + ")", oColor);
        }

        moves++;

        String winner = checkWinner();
        if (winner != null) {
            turnLabel.setText(winner + " Wins!");
            onGameCompletion();
            return;
        }

        if (moves == 9) {
            turnLabel.setText("Draw!");
            onGameCompletion();
            return;
        }

        xTurn = !xTurn;
        turnLabel.setText(xTurn ? "X's Turn" : "O's Turn");
    }

    /** After the human plays X in session mode, O picks a random empty cell (if enabled and game continues). */
    private void maybePlayRandomOpponent() {
        if (!randomOpponentEnabled || session == null) {
            return;
        }
        javafx.application.Platform.runLater(() -> {
            if (session == null || session.isGameOver()) {
                return;
            }
            if (session.getTurn() != Player.PLAYER_2) {
                return;
            }
            int[] cell = TicTacToeRandomMoveGenerator.pickRandomEmptySlot(session.getBoard());
            if (cell == null) {
                return;
            }
            if (!session.submitMove(cell[0], cell[1])) {
                return;
            }
            parentGUI.addHistory("TicTacToe", "O (AI)",
                    "(" + (cell[0] + 1) + ", " + (cell[1] + 1) + ")", oColor);
            refreshFromSession();
        });
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
        EloOverlayUtil.hide(eloResultOverlay);
        onlineEloApplied = false;
        if (session != null) {
            session.startNewGame();
            parentGUI.clearHistory();
            refreshFromSession();
            resetButton.setVisible(false);
            return;
        }
        board = new Player[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = Player.EMPTY;
            }
        }
        moves = 0;
        xTurn = true;
        turnLabel.setText("X's Turn");
        parentGUI.clearHistory();
        if (!randomOpponentEnabled && clientCore != null && clientCore.isConnected()) {
            clientCore.msgSend(new sysMSG("reset", ""));
        }

        for (Node node : ticTacToeGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.setText("");
                btn.setStyle("");
                btn.setDisable(false);
            }
        }
        resetButton.setVisible(false);
    }

    /** Updates grid and turn label from session state (only when session is set). */
    private void refreshFromSession() {
        if (session == null) return;
        ca.ucalgary.seng300.game.core.Player[][] b = session.getBoard();
        for (Node node : ticTacToeGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                Integer r = GridPane.getRowIndex(btn);
                Integer c = GridPane.getColumnIndex(btn);
                if (r == null) r = 0;
                if (c == null) c = 0;
                if (b[r][c] == Player.PLAYER_1) {
                    btn.setText("X");
                    btn.setStyle("-fx-text-fill:#3B82F6; -fx-font-size:40px; -fx-font-weight:bold;");
                } else if (b[r][c] == Player.PLAYER_2) {
                    btn.setText("O");
                    btn.setStyle("-fx-text-fill:#A855F7; -fx-font-size:40px; -fx-font-weight:bold;");
                } else {
                    btn.setText("");
                    btn.setStyle("");
                }
                btn.setDisable(session.isGameOver());
                resetButton.setVisible(session.isGameOver());
            }
        }
        State st = session.getState();
        if (st == State.PLAYER_1_WIN) turnLabel.setText("X Wins!");
        else if (st == State.PLAYER_2_WIN) turnLabel.setText("O Wins!");
        else if (st == State.TIE) turnLabel.setText("Draw!");
        else turnLabel.setText(session.getTurn() == Player.PLAYER_1 ? "X's Turn" : "O's Turn");
        refreshBackButtonVisibility();
    }

//    @Override
//    protected void refreshFromSession() {
//        super.refreshFromSession();
//        refreshBackButtonVisibility();
//    }

    public void setSession(TicTacToeSession s) {
        this.session = s;
        if (s != null) refreshFromSession();
        else refreshBackButtonVisibility();
    }

    /** Use when in online mode: refresh grid and label from server update. */
    public void refreshFromGameStateUpdate(TTTGameState update) {
        if (update == null || update.getBoard() == null) return;
        System.out.println("Ui update in TTTcontroller");
        State st = update.getState();
        if (State.IN_PROGRESS.equals(st)) {
            onlineEloApplied = false;
            EloOverlayUtil.hide(eloResultOverlay);
        }
        board = update.getBoard();
        for (Node node : ticTacToeGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                Integer r = GridPane.getRowIndex(btn);
                Integer c = GridPane.getColumnIndex(btn);

                if (r == null) r = 0;
                if (c == null) c = 0;
                // Don't know if there is a reason not to set the style for empty buttons

                btn.setText(getToken(board[r][c]));
                if (board[r][c].equals(Player.PLAYER_1)) {
                    btn.setStyle("-fx-text-fill:#3B82F6; -fx-font-size:40px; -fx-font-weight:bold;");
                } else if (board[r][c].equals(Player.PLAYER_2)) {
                    btn.setStyle("-fx-text-fill:#A855F7; -fx-font-size:40px; -fx-font-weight:bold;");
                } else {
                    btn.setStyle("");
                }
                /*
                if (r == null) r = 0;
                if (c == null) c = 0;
                Player cell = r < b.length && c < b[r].length ? b[r][c] : Player.EMPTY;

                if (Player.PLAYER_1.equals(cell)) {
                    btn.setText("X");
                    btn.setStyle("-fx-text-fill:#3B82F6; -fx-font-size:40px; -fx-font-weight:bold;");
                } else if ("PLAYER_2".equals(cell)) {
                    btn.setText("O");
                    btn.setStyle("-fx-text-fill:#A855F7; -fx-font-size:40px; -fx-font-weight:bold;");
                } else {
                    btn.setText("");
                    btn.setStyle("");
                }*/
                //btn.();
                boolean gameOver = !State.IN_PROGRESS.equals(st);
                resetButton.setVisible(gameOver);
                Player myRole = clientCore != null ? clientCore.getMyNetworkRole() : null;
                boolean onlineTurns = clientCore != null && clientCore.isConnected() && myRole != null;
                boolean myTurn = !onlineTurns || gameOver || myRole.equals(update.getPlayerTurn());
                btn.setDisable(gameOver || !myTurn);
            }
        }
        if (State.PLAYER_1_WIN.equals(st)) turnLabel.setText("X Wins!");
        else if (State.PLAYER_2_WIN.equals(st)) turnLabel.setText("O Wins!");
        else if (State.TIE.equals(st)) turnLabel.setText("Draw!");
        else {
            Player cur = update.getPlayerTurn();
            Player myRole = clientCore != null ? clientCore.getMyNetworkRole() : null;
            if (clientCore != null && clientCore.isConnected() && myRole != null && !myRole.equals(cur)) {
                turnLabel.setText(myRole == Player.PLAYER_1 ? "You are X — waiting for O" : "You are O — waiting for X");
            } else {
                turnLabel.setText(cur == Player.PLAYER_1 ? "X's Turn" : "O's Turn");
                rootController.getWindowManager().showNotification("Your Turn!");
            }
        }
        OnlineRankingHelper.applyIfOnlineGameFinished(clientCore, st, onlineEloApplied, GameType.TIC_TAC_TOE)
                .ifPresent(r -> {
                    onlineEloApplied = true;
                    EloOverlayUtil.show(eloResultOverlay, eloOutcomeTitle, eloDeltaLabel, eloNewRatingLabel, r);
                });
        refreshBackButtonVisibility();
        /* commented out depending on merging gamestate and gamestateupdate functions
        if (update.getMessage() != null && !update.getMessage().isEmpty()) {
            turnLabel.setText(turnLabel.getText() + " (" + update.getMessage() + ")");
        }*/
    }

//    @Override
//    public void refreshFromGameState(GameState gameState) {
//        super.refreshFromGameState(gameState);
//        refreshBackButtonVisibility();
//    }

    /*public void setClientCore(ClientCore c) {
        this.clientCore = c;
        if (c != null && c.getLastUpdate() != null) refreshFromGameStateUpdate(c.getLastUpdate());
    }*/

    public void setVisibility(Boolean status){
        boardPane.setVisible(status);
    }

    private void navigateBackFromGame() {
        CentralController.localAiMode = false;
        randomOpponentEnabled = false;
        EloOverlayUtil.hide(eloResultOverlay);
        if (CentralController.previousScreen != null) {
            CentralController.moveTo(CentralController.previousScreen);
        } else {
            CentralController.moveTo(Navigation.GAME_DASHBOARD);
        }
    }

    @FXML
    private void handleBackToMode(ActionEvent event) {
        if (isActiveOnlineMatch()) {
            new Alert(Alert.AlertType.INFORMATION,
                    "You cannot leave an online match in progress without using Forfeit in the sidebar.",
                    ButtonType.OK).showAndWait();
            return;
        }
        navigateBackFromGame();
    }

    /**
     * Called from {@link GameGUIController} sidebar forfeit. Online: server declares opponent wins;
     * local / AI: returns to the previous screen after confirm.
     */
    public void handleForfeitFromShell() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Forfeit");
        confirm.setHeaderText("Forfeit this match?");
        confirm.setContentText("Online: your opponent wins and ratings may update. Local / vs AI: you leave this game.");
        confirm.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
            EloOverlayUtil.hide(eloResultOverlay);
            boolean online = !randomOpponentEnabled && clientCore != null && clientCore.isConnected();
            if (!online) {
                navigateBackFromGame();
                return;
            }
            clientCore.msgSend(new sysMSG("FORFEIT", "", ""));
        });
    }

    @FXML
    private void handleDismissEloOverlay() {
        EloOverlayUtil.hide(eloResultOverlay);
    }

    @Override
    public Navigation getNavigation() {
        return Navigation.TTT;
    }

    @Override
    public void reset() {
        initialize();

        moves = 0;

        boolean vsAi = CentralController.localAiMode;
        setRandomOpponentEnabled(vsAi);
        if (vsAi) {
            setSession(new TicTacToeSession());
        } else {
            prepareForNetworkPlayInShell();
        }

        clientCore = rootController.clientCore;

        EloOverlayUtil.hide(eloResultOverlay);
        if (clientCore != null && clientCore.isConnected()) {
            session = null;
        }
        if (clientCore != null && clientCore.getLastUpdate() instanceof TTTGameState gs) {
            refreshFromGameStateUpdate(gs);
        }
        refreshBackButtonVisibility();
    }
}
