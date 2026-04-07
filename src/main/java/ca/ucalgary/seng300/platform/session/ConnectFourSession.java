package ca.ucalgary.seng300.platform.session;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import ca.ucalgary.seng300.game.core.connect4.C4Game;
import ca.ucalgary.seng300.game.core.connect4.C4GameState;
import ca.ucalgary.seng300.game.core.connect4.C4PlayerAction;

/**
 * Local game session facade for Connect Four. Holds one {@link C4Game} and exposes
 * {@link #submitMove(int)} for a column drop and board/turn/state for the UI.
 */
public class ConnectFourSession implements Session {

    private final C4Game game = new C4Game();

    public ConnectFourSession() {
        startNewGame();
    }

    @Override
    public void startNewGame() {
        game.startGame(null, null);
    }

    /**
     * Drops a piece for the current player in {@code col} ({@code 0..6}).
     *
     * @return {@code true} if the move was valid and applied
     */
    @Override
    public boolean submitMove(int row, int col) {
        if (!State.IN_PROGRESS.equals(game.getGameState().getState())) {
            return false;
        }
        C4GameState gs = (C4GameState) game.getGameState();
        Player[][] b = gs.getBoard();
        if (col < 0 || col > 6 || b[0][col] != Player.EMPTY) {
            return false;
        }
        int landing = -1;
        for (int r = 5; r >= 0; r--) {
            if (b[r][col] == Player.EMPTY) {
                landing = r;
                break;
            }
        }
        if (landing < 0) {
            return false;
        }
        String playerId = gs.getPlayerTurn() == Player.PLAYER_1 ? "PLAYER_1" : "PLAYER_2";
        C4PlayerAction action = new C4PlayerAction(playerId, "MOVE", landing, col);
        game.handleAction(action);
        return gs.getBoard()[landing][col] != Player.EMPTY;
    }

    @Override
    public Player[][] getBoard() {
        return game.getGameState().getBoard();
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
}
