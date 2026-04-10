package ca.ucalgary.seng300.platform.domain;

import ca.ucalgary.seng300.game.core.GameType;

public class PlayerStats extends Record {
    private GameType gameType;
    private int userID;
    private int counter = 0;
    private double score = 0;
    private int elo = 1200;

    public PlayerStats(int id, GameType gameType, int userID) {
        super(id, PlayerStats.class.getSimpleName());
        this.gameType = gameType;
        this.userID = userID;
    }

    //getters
    public GameType getGameType(){ return this.gameType; }
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
