package ca.ucalgary.seng300.platform.domain;

public class PlayerStats extends Record{
    private int gameID;
    private int userID;
    private int counter = 0;
    private double score = 0;
    private int elo = 1200;

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
    public int getElo() {
        return elo <= 0 ? 1200 : elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public void adjustElo(int delta) {
        int next = getElo() + delta;
        this.elo = Math.max(0, next);
    }

    public void updateStats(double score) {
        this.score += score;
        this.counter ++;
    }
}
