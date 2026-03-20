package ca.ucalgary.seng300.platform.services;

import ca.ucalgary.seng300.platform.constants.Rank;
import ca.ucalgary.seng300.platform.domain.Participant;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.store.PlayerStatsStore;
import ca.ucalgary.seng300.platform.utils.Helper;
import ca.ucalgary.seng300.platform.constants.Game;

import java.util.*;
import java.util.ArrayList;

import static java.lang.Math.abs;


public class MatchMakingService {

    // game names
    private static final Map<String, Integer> GAME_TYPE_IDS = Map.of(
            "TicTacToe", Game.TICK_TACK_TOE,
            "ConnectFour", Game.CONNECT_FOUR
    );

    //validating game type
    private int validateGameId(String gameType) {
        Integer id = GAME_TYPE_IDS.get(gameType);
        if (id == null) throw new IllegalArgumentException("Unknown gameType: " + gameType);
        return id;
    }


    //Key is gameType, value is player id and score
    public static Map<Integer, List<PlayerStats>> queues = new HashMap<>();

    /**
     * Add a player to matchmaking queue for the given gameType
     *
     * @param player the player's stats object containing their gameID, userID and score
     **/

    public void addPlayerToQueue(PlayerStats player) {
        // retrieving gameType, userId, and userScore
        int gameType = player.getGameID();
        int userId = player.getUserID();
        double userScore = player.getScore();
        List<PlayerStats> statsForGame = PlayerStatsStore.getInstance().findPlayersByGameID(gameType);

        // if gameType queue exists, add to it. If not, create a new one
        if (queues.containsKey(gameType)) {
            queues.get(gameType).add(player);
        } else {
            List<PlayerStats> newEntryList = new ArrayList<>();
            newEntryList.add(player);
            queues.put(gameType, newEntryList);
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
                //match by userId and gameId
                if (gameList.get(i).getUserID() == player.getUserID() && gameList.get(i).getGameID() == player.getGameID()) {
                    gameList.remove(i);
                }
            }
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

        // no valid pair found, or the closest pair exceeds the max allowed score difference
        if (bestI == -1 || bestDiff > 10) {
            return null;
        }
        // build the matched players list from the best pair
        List<PlayerStats> matchedPlayers = new ArrayList<>();
        PlayerStats player1 = queues.get(gameType).get(bestI);
        PlayerStats player2 = queues.get(gameType).get(bestJ);


        // remove higher index first to avoid index shifting when removing from list
        removePlayer(player1);
        removePlayer(player2);

        return matchedPlayers;
    }

}
