package ca.ucalgary.seng300.platform.domain;

public class Participants extends Record{
    private int gameHistoryID;
    private int userID;
    private int score;

    public Participants(int id, int gameHistoryID, int userID, int score){
        super(id);
        this.gameHistoryID = gameHistoryID;
        this.userID = userID;
        this.score = score;
    }

    //getters
    public int getGameHistoryID(){return this.gameHistoryID;}
    public int getUserID(){return this.userID;}
    public int getScore(){return this.score;}

    //setters
    public void setGameHistoryID(int gameHistoryID){this.gameHistoryID = gameHistoryID;}
    public void setUser(int userID){this.userID = userID;}
    public void setScore(int score){this.score = score;}
}
