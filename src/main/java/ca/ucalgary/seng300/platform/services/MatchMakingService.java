package ca.ucalgary.seng300.platform.services;

import ca.ucalgary.seng300.platform.constants.Rank;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.game.core.GameType;

import java.util.*;
import java.util.ArrayList;

public class MatchMakingService {

    // game names
    private static final Map<String, Integer> GAME_TYPE_IDS = Map.of(
            "TicTacToe", GameType.TIC_TAC_TOE.toId(),
            "ConnectFour", GameType.CONNECT4.toId());

    // validating game type
    private int validateGameId(String gameType) {
        Integer id = GAME_TYPE_IDS.get(gameType);
        if (id == null)
            throw new IllegalArgumentException("Unknown gameType: " + gameType);
        return id;
    }

    // Key is gameType, value is player queue
    public static Map<Integer, List<PlayerStats>> queues = new HashMap<>();
    private static final int MAX_RANK_GAP = 1;

    /**
     * Add a player to matchmaking queue for the given gameType
     *
     * @param player the player's stats object containing their gameID, userID and
     *               score
     **/

    public void addPlayerToQueue(PlayerStats player) {
        // retrieving gameType from player stats
        GameType gameType = player.getGameType();

        // if gameType queue exists, add to it. If not, create a new one
        if (queues.containsKey(gameType.toId())) {
            queues.get(gameType.toId()).add(player);
        } else {
            List<PlayerStats> newEntryList = new ArrayList<>();
            newEntryList.add(player);
            queues.put(gameType.toId(), newEntryList);
        }

    }

    /**
     * Remove a player from all queues using their PlayerStats object
     *
     * @param player the player's stats object to remove
     */
    public static void removePlayer(PlayerStats player) {
        // searching for user in hash map
        for (Integer gameType : queues.keySet()) {
            List<PlayerStats> gameList = queues.get(gameType);
            for (int i = 0; i < gameList.size(); i++) {
                // match by userId and gameId
                if (gameList.get(i).getUserID() == player.getUserID()
                        && gameList.get(i).getGameType() == player.getGameType()) {
                    gameList.remove(i);
                }
            }
        }
    }

    /**
     * Match two players in the queue with the closest scores within a threshold of
     * 10
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
        int bestRankDiff = Integer.MAX_VALUE;
        double bestScoreDiff = Double.MAX_VALUE;

        // compare every unique pair of players in the queue
        for (int i = 0; i < gameList.size(); i++) {
            for (int j = i + 1; j < gameList.size(); j++) {

                Rank rankI = rankForMatchmaking(gameList.get(i).getScore());
                Rank rankJ = rankForMatchmaking(gameList.get(j).getScore());
                int rankDiff = Math.abs(rankI.getRankValue() - rankJ.getRankValue());
                double scoreDiff = Math.abs(gameList.get(i).getScore() - gameList.get(j).getScore());

                // Primary sort key: rank proximity. Secondary key: score proximity.
                if (rankDiff < bestRankDiff || (rankDiff == bestRankDiff && scoreDiff < bestScoreDiff)) {
                    bestRankDiff = rankDiff;
                    bestScoreDiff = scoreDiff;
                    bestI = i;
                    bestJ = j;
                }
            }
        }

        // no valid pair found, or the closest pair exceeds allowed rank distance
        if (bestI == -1 || bestRankDiff > MAX_RANK_GAP) {
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

    public int getQueueSize(Integer gameType) {
        List<PlayerStats> gameList = queues.get(gameType);
        if (gameList == null)
            return 0;
        return gameList.size();
    }

    private static Rank rankForMatchmaking(double score) {
        for (Rank rank : Rank.values()) {
            if (rank.isInRank(score)) {
                return rank;
            }
        }
        return Rank.GRAND_MASTER;
    }

}
