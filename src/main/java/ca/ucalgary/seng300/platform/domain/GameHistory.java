package ca.ucalgary.seng300.platform.domain;

import ca.ucalgary.seng300.game.core.GameType;

public class GameHistory extends Record {
    private GameType gameType;

    public GameHistory(int id, GameType gameType){
        super(id, GameHistory.class.getSimpleName());
        this.gameType = gameType;
    }

    public GameType getGameType(){return this.gameType;}
    public void setGameType(GameType gameType){this.gameType = gameType;}
}
