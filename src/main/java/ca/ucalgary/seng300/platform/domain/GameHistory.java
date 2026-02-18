package ca.ucalgary.seng300.platform.domain;

public class GameHistory {
    private int gameID;
    private int gameHistoryID;
    private int timeID;

    public GameHistory(int gameID, int gameHistoryID, int timeID){
        this.gameID = gameID;
        this.gameHistoryID = gameHistoryID;
        this.timeID = timeID;
    }

    //getters
    public int getGameID(){return this.gameID;}
    public int getGameHistoryID(){return this.gameHistoryID;}
    public int getTimeID(){return this.timeID;}

    //setters
    public void setGameID(int gameID){this.gameID = gameID;}
    public void setGameHistoryID(int gameHistoryID){this.gameHistoryID = gameHistoryID;}
    public void setTimeID(int timeID){this.timeID = timeID;}
}
