package ca.ucalgary.seng300.model;
import ca.ucalgary.seng300.platform.domain.Record;

    public class GeneralStats extends Record {

        private int gamesPlayed;
        private int wins;
        private int losses;
        private int ties;
        private double mmr;
        private Rank rank;

        // Minimum games before leaving Bronze regardless of score
        private static final int MIN_GAMES_FOR_RANK = 10;

        // Score thresholds for each rank tier
        private static final double SILVER_MMR      = 14.0;
        private static final double GOLD_MMR        = 29.0;
        private static final double PLATINUM_MMR    = 44.0;
        private static final double DIAMOND_MMR     = 59.0;
        private static final double MASTER_MMR      = 74.0;
        private static final double GRANDMASTER_MMR = 89.0;

        // Default deltas: win in (0, 1], loss in [-1, 0)
        private static final double MMR_WIN_DELTA  = 1.0;
        private static final double MMR_LOSS_DELTA = 1.0;

        private static final double BASE_MMR = 0.0;

        public GeneralStats(int id) {
            super(id);
            this.gamesPlayed = 0;
            this.wins = 0;
            this.losses = 0;
            this.ties = 0;
            this.mmr = BASE_MMR;
            this.rank = Rank.BRONZE;
        }

        // increment win counter, adjust MMR, and recalculate rank
        public void win() {
            gamesPlayed++;
            wins++;
            updateMMR(true);
            updateRank();
        }

        // Increment loss counter, adjust MMR, and recalculate rank
        public void lose() {
            gamesPlayed++;
            losses++;
            updateMMR(false);
            updateRank();
        }

        // Increment tie counter and recalculate rank (no MMR change for ties)
        public void tie() {
            gamesPlayed++;
            ties++;
            updateRank();
        }

        // Adjust score: +1.0 for a win, -1.0 for a loss
        public void updateMMR(boolean win) {
            if (win) {
                mmr += MMR_WIN_DELTA;
            } else {
                mmr -= MMR_LOSS_DELTA;
            }
        }

        // Recalculate rank from current score. Bronze if fewer than 10 games played
        public void updateRank() {
            if (gamesPlayed < MIN_GAMES_FOR_RANK || mmr < SILVER_MMR) {
                rank = Rank.BRONZE;
            } else if (mmr < GOLD_MMR) {
                rank = Rank.SILVER;
            } else if (mmr < PLATINUM_MMR) {
                rank = Rank.GOLD;
            } else if (mmr < DIAMOND_MMR) {
                rank = Rank.PLATINUM;
            } else if (mmr < MASTER_MMR) {
                rank = Rank.DIAMOND;
            } else if (mmr < GRANDMASTER_MMR) {
                rank = Rank.MASTER;
            } else {
                rank = Rank.GRANDMASTER;
            }
        }

        // Getters
        public int getGamesPlayed() { return gamesPlayed; }
        public int getWins()        { return wins; }
        public int getLosses()      { return losses; }
        public int getTies()        { return ties; }
        public double getMmr()      { return mmr; }
        public Rank getRank()       { return rank; }
    }