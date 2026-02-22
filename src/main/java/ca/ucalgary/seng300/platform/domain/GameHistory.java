package ca.ucalgary.seng300.platform.domain;

public class GameHistory extends Record{
    private int gameID;
    private String time;

    public GameHistory(int gameHistoryID, int gameID, String time){
        super(gameHistoryID);
        this.gameID = gameID;
        this.time = time;
    }

    //getters
    public int getGameID(){return this.gameID;}
    public String getTime(){return this.time;}

    //setters
    public void setGameID(int gameID){this.gameID = gameID;}
    public void setTime(String time){this.time = time;}
}
