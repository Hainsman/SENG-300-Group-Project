package ca.ucalgary.seng300.platform.services;

import ca.ucalgary.seng300.platform.constants.Rank;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.utils.Helper;

import java.util.*;
import java.util.ArrayList;

public class MatchMakingService {

    private static final double MAX_SCORE_GAP = 10.0;
    private static final int MAX_RANK_GAP = 1;

    // Key is gameType, value is queued players for that game.
    public static Map<Integer, List<PlayerStats>> queues = new HashMap<>();

    /**
     * Add a player to matchmaking queue for the given gameType
     *
     * @param player the player's stats object containing their gameID, userID and score
     **/

    public void addPlayerToQueue(PlayerStats player) {
        int gameType = player.getGameID();
        List<PlayerStats> queue = queues.computeIfAbsent(gameType, ignored -> new ArrayList<>());
        for (PlayerStats queued : queue) {
            if (queued.getUserID() == player.getUserID()) {
                return;
            }
        }
        queue.add(player);
    }

    /**
     * Remove a player from all queues using their PlayerStats object
     *
     * @param player the player's stats object to remove
     */
    public static void removePlayer(PlayerStats player) {
        for (Integer gameType : queues.keySet()) {
            List<PlayerStats> gameList = queues.get(gameType);
            gameList.removeIf(queued ->
                    queued.getUserID() == player.getUserID() && queued.getGameID() == player.getGameID());
        }
    }

    /**
     * Match two players in the queue with the closest scores within a threshold of 10
     *
     * @param gameType
     * @return list of matched player IDs, or null if no match found
     */
    public List<PlayerStats> matchPlayers(Integer gameType) {

        // get the queue for the given game type
        List<PlayerStats> gameList = queues.get(gameType);

        // if the queue is empty or doesn't exist, no match is possible
        if (gameList == null || gameList.isEmpty()) {
            return null;
        }

        // track the best pair found so far
        int bestI = -1;
        int bestJ = -1;
        double bestDiff = Double.MAX_VALUE;

        // compare every unique pair of players in the queue
        for (int i = 0; i < gameList.size(); i++) {
            for (int j = i + 1; j < gameList.size(); j++) {
                if (!canMatchPair(gameList.get(i), gameList.get(j))) {
                    continue;
                }

                // calculate the score difference between the two players
                double diff = Math.abs(gameList.get(i).getScore() - gameList.get(j).getScore());

                // update the best pair if this difference is smaller than the current best
                if (diff < bestDiff) {
                    bestDiff = diff;
                    bestI = i;
                    bestJ = j;
                }
            }
        }

        // no valid pair found
        if (bestI == -1) {
            return null;
        }
        // build the matched players list from the best pair
        List<PlayerStats> matchedPlayers = new ArrayList<>();
        PlayerStats player1 = queues.get(gameType).get(bestI);
        PlayerStats player2 = queues.get(gameType).get(bestJ);

        // adding players to list
        matchedPlayers.add(player1);
        matchedPlayers.add(player2);


        // remove higher index first to avoid index shifting when removing from list
        removePlayer(player1);
        removePlayer(player2);

        return matchedPlayers;
    }

    public boolean canMatchPair(PlayerStats playerOne, PlayerStats playerTwo) {
        if (playerOne == null || playerTwo == null) {
            return false;
        }
        Rank rankOne = Helper.getRank(playerOne.getGameCounter(), playerOne.getScore());
        Rank rankTwo = Helper.getRank(playerTwo.getGameCounter(), playerTwo.getScore());
        int rankGap = Math.abs(rankOne.getRankValue() - rankTwo.getRankValue());
        if (rankGap > MAX_RANK_GAP) {
            return false;
        }
        double scoreGap = Math.abs(playerOne.getScore() - playerTwo.getScore());
        return scoreGap <= MAX_SCORE_GAP;
    }

    public int getQueueSize(Integer gameType) {
        List<PlayerStats> gameList = queues.get(gameType);
        if (gameList == null) return 0;
        return gameList.size();
    }

}
