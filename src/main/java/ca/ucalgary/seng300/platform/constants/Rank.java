package ca.ucalgary.seng300.platform.constants;

public enum Rank {
    BRONZE(0, "Bronze", 0.0, 14.0),
    SILVER(1, "Silver", 14.0, 29.0),
    GOLD(2, "Gold", 29.0, 44.0),
    PLATINUM(3, "Platinum", 44.0, 59.0),
    DIAMOND(4, "Diamond", 59.0, 74.0),
    MASTER(5, "Master", 74.0, 89.0),
    GRAND_MASTER(6, "GrandMaster", 89.0, Double.MAX_VALUE);

    private final int rankValue;
    private final String displayName;
    private final double minScoreThreshold;
    private final double maxScoreThreshold;

    Rank(int rankValue, String displayName, double minScoreThreshold, double maxScoreThreshold){
        this.rankValue = rankValue;
        this.displayName = displayName;
        this.minScoreThreshold = minScoreThreshold;
        this.maxScoreThreshold = maxScoreThreshold;
    }

    public boolean isInRank(double score){
        return score >= minScoreThreshold && score < maxScoreThreshold;
    }

    public int getRankValue(){return rankValue;}
    public String getDisplayName(){return displayName;}
    public String getBadge() {
        return switch (this) {
            case BRONZE -> "Br";
            case SILVER -> "Si";
            case GOLD -> "Go";
            case PLATINUM -> "Pt";
            case DIAMOND -> "Di";
            case MASTER -> "Ma";
            case GRAND_MASTER -> "GM";
        };
    }
    public double getMinScoreThreshold(){return minScoreThreshold;}
    public double getMaxScoreThreshold(){return maxScoreThreshold;}

    public static Rank fromElo(int elo) {
        if (elo < 1200) {
            return BRONZE;
        }
        if (elo < 1400) {
            return SILVER;
        }
        if (elo < 1600) {
            return GOLD;
        }
        if (elo < 1800) {
            return PLATINUM;
        }
        if (elo < 2000) {
            return DIAMOND;
        }
        if (elo < 2200) {
            return MASTER;
        }
        return GRAND_MASTER;
    }
}
