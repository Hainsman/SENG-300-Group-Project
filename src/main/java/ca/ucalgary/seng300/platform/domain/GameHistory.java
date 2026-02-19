package ca.ucalgary.seng300.platform.domain;

public class GameHistory extends Record{
    private int gameID;
    private int timeID;

    public GameHistory(int gameHistoryID, int gameID, int timeID){
        super(gameHistoryID);
        this.gameID = gameID;
        this.timeID = timeID;
    }

    //getters
    public int getGameID(){return this.gameID;}
    public int getTimeID(){return this.timeID;}

    //setters
    public void setGameID(int gameID){this.gameID = gameID;}
    public void setTimeID(int timeID){this.timeID = timeID;}
}
