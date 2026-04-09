package ca.ucalgary.seng300.platform.domain;

public class PlayerStats extends Record{
    private int gameID;
    private int userID;
    private int counter = 0;
    private double score = 0;

    public PlayerStats(int id, int gameID, int userID) {
        super(id, PlayerStats.class.getSimpleName());
        this.gameID = gameID;
        this.userID = userID;
    }

    //getters
    public int getGameID(){return this.gameID;}
    public int getUserID(){return this.userID;}
    public int getGameCounter(){return this.counter;}
    public double getScore(){return this.score;}

    public void updateStatsWin(double score) {
        this.score += score;
        this.counter ++;
    }

    public void updateStatsLose(double score) {
        this.score -= score;
        if (this.score < 0)
            this.score = 0;
        this.counter ++;
    }
}
