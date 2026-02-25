package ca.ucalgary.seng300.platform.domain;

public class GameHistory extends Record{
    private int gameID;

    public GameHistory(int gameHistoryID, int gameID){
        super(gameHistoryID);
        this.gameID = gameID;
    }

    //getters
    public int getGameID(){return this.gameID;}

    //setters
    public void setGameID(int gameID){this.gameID = gameID;}
}
