package ca.ucalgary.seng300.game.core;

import ca.ucalgary.seng300.ui.util.Navigation;

public enum GameType {
    CHECKERS (0, "Checkers"),
    TIC_TAC_TOE (1, "Tic Tac Toe"),
    CONNECT4 (2, "Connect 4");

    private final int id;
    private final String string;

    GameType(int id, String string) {
        this.id = id;
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

    public int toId() {
        return id;
    }

    public static Navigation getNavigation(GameType type) {
        return type != null ?
            switch (type) {
                case CHECKERS -> Navigation.GAME_GUI;
                case CONNECT4 -> Navigation.C4;
                case TIC_TAC_TOE -> Navigation.TTT;
            }
            : null
        ;
    }
}
