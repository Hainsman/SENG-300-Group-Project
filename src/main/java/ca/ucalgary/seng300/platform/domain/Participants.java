package ca.ucalgary.seng300.platform.domain;

public class Participants extends Record{
    private int gameHistoryID;
    private int gameID;
    private User user;


    public Participants(int id, int userID, ){
        super(id);
        this.gameHistoryID = gameHistoryID;
        this.gameID = gameID;
        this.user = user;
    }

    //getters
    public int getGameHistoryID(){return this.gameHistoryID;}
    public int getGameID(){return this.gameID;}
    public User getUser(){return this.user;}

    //setters
    public void setGameHistoryID(int gameHistoryID){this.gameHistoryID = gameHistoryID;}
    public void setGameID(int gameID){this.gameID = gameID;}
    public void setUser(User user){this.user = user;}
}
