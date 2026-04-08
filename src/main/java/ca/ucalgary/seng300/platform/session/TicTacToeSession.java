package ca.ucalgary.seng300.platform.session;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGame;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.game.core.tictactoe.TTTPlayerAction;
import ca.ucalgary.seng300.game.core.tictactoe.TTTActionType;

import java.util.HashMap;
import java.util.Map;

/**
 * Local game session facade for Tic-Tac-Toe. Holds one TTTGame and exposes
 * submitMove(row, col) and getBoard/getTurn/getState for the UI. When no
 * playerId mapping is set, uses "P1"/"P2" for local two-player play.
 */
public class TicTacToeSession implements Session {

    private final TTTGame game = new TTTGame();
    /** Maps playerId (e.g. "P1", "P2") to Player enum for turn enforcement. */
    private Map<String, Player> playerIdToPlayer = new HashMap<>();

    public TicTacToeSession() {
        startNewGame();
    }

    /** Starts a new game (fresh board, PLAYER_1 to move). */
    public void startNewGame() {
        game.startGame("P1", "P2");/*
        playerIdToPlayer = new HashMap<>();
        playerIdToPlayer.put("P1", Player.PLAYER_1);
        playerIdToPlayer.put("P2", Player.PLAYER_2);
        game.setPlayerIdToPlayer(playerIdToPlayer);*/
    }

    /**
     * Submits a move for the current turn. Uses "P1" for PLAYER_1 and "P2" for PLAYER_2.
     * @return true if the move was valid and applied, false otherwise
     */
    @Override
    public boolean submitMove(int row, int col) {
        if (!game.getGameState().getState().equals(State.IN_PROGRESS)) {
            return false;
        }
        TTTGameState gs = (TTTGameState) game.getGameState();
        //String playerId = gs.getPlayerTurn() == Player.PLAYER_1 ? "P1" : "P2";
        TTTPlayerAction action = new TTTPlayerAction(gs.getPlayerTurn(), "MOVE", row, col);
        game.handleAction(action);
        // Move was applied only if validator accepted it (cell is now filled)
        return game.getGameState().getBoard()[row][col] != Player.EMPTY;
    }

    /** Returns the current board (3x3). Do not modify the returned array. */
    @Override
    public Player[][] getBoard() {
        return game.getGameState().getBoard();
    }

    public TTTGameState getGameState() {
        return (TTTGameState) game.getGameState();
    }

    @Override
    public Player getTurn() {
        return game.getGameState().getPlayerTurn();
    }

    @Override
    public State getState() {
        return game.getGameState().getState();
    }

    @Override
    public boolean isGameOver() {
        return game.isGameOver();
    }

    /** Optional: set custom playerId -> Player mapping (e.g. for multiplayer).
    public void setPlayerIdToPlayer(Map<String, Player> map) {
        this.playerIdToPlayer = map != null ? new HashMap<>(map) : new HashMap<>();
        game.setPlayerIdToPlayer(playerIdToPlayer.isEmpty() ? null : playerIdToPlayer);
    }*/
}
