package ca.ucalgary.seng300.platform.domain;
//gameHistory ID denotes a unique match between users
//TODO convert to Java class record?
public class GameHistory extends Record{
    private int gameID;
    private int userID;
    private int wins;
    private int losses;
    private int ties;

    //TODO add rank parameter
    public GameHistory(int gameHistoryID, int gameID, int userID, int wins, int losses, int ties){
        super(gameHistoryID);
        this.gameID = gameID;
        this.userID = userID;
        this.wins = wins;
        this.losses = losses;
        this.ties = ties;
    }

    //getters
    public int getGameID(){return this.gameID;}
    public int getUserID(){return this.userID;}
    public int getWins(){return this.wins;}
    public int getLosses(){return this.losses;}
    public int getTies(){return this.losses;}

    //setters
    public void setGameID(int gameID){this.gameID = gameID;}
    public void setUser(int userID){this.userID = userID;}
    public void setWins(int wins){this.wins = wins;}
}
