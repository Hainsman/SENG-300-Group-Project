package ca.ucalgary.seng300.platform.domain;
public class GameHistory extends Record{
    private int gameID;
    private String type;

    public GameHistory(int id, int gameID){
        super(id);
        this.gameID = gameID;
        this.type = "gameHistory";
    }
    public int getGameID(){return this.gameID;}
    public void setGameID(int gameID){this.gameID = gameID;}
}
