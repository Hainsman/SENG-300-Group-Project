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
    // Creating new class for users in queues
    private class userInQueue {
        String userId;
        Double score;

        private userInQueue(String userId, Double score) {
            this.userId = userId;
            this.score = score;
        }

    }

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
    public static Map<String, List<userInQueue>> queues = new HashMap<>();

    /**
     * Add a player to machmaking queue for the given gameType
     *
     * @param userId
     * @param gameType eg. "TicTacToe", "ConnectFour"
     **/

    public void addPlayerToQueue(String userId, String gameType) {
        // retrieving userScore
        double userScore = 0.0;
        int gameId = validateGameId(gameType);
        List<PlayerStats> statsForGame = PlayerStatsStore.getInstance().findPlayersByGameID(gameId);

        for (PlayerStats stats : statsForGame) {
            if (String.valueOf(stats.getUserID()).equals(userId)) {
                userScore = stats.getScore();
            }
        }
        userInQueue newEntry = new userInQueue(userId, userScore);

        // if gameType queue exists, add to it. If not, create a new one
        if (queues.containsKey(gameType)) {
            queues.get(gameType).add(newEntry);
        } else {
            List<userInQueue> newEntryList = new ArrayList<>();
            newEntryList.add(newEntry);
            queues.put(gameType, newEntryList);
        }

    }


    public static void removePlayer(String userId) {
        // searching for user in hash map
        for (String gameType : queues.keySet()) {
            List<userInQueue> gameList = queues.get(gameType);
            for (int i = 0; i < gameList.size(); i++) {
                //removing use from queue
                if (gameList.get(i).userId.equals(userId)) {
                    gameList.remove(i);
                }
            }
        }
    }

    public List<String> matchPlayers(String gameType) {

        // get the queue for the given game type
        List<userInQueue> gameList = queues.get(gameType);

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
                double diff = Math.abs(gameList.get(i).score - gameList.get(j).score);

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
        List<String> matchedPlayers = new ArrayList<>();
        String player1 = gameList.get(bestI).userId;
        String player2 = gameList.get(bestJ).userId;
        matchedPlayers.add(player1);
        matchedPlayers.add(player2);

        // remove higher index first to avoid index shifting when removing from list
        removePlayer(player1);
        removePlayer(player2);

        return matchedPlayers;
    }
    public int getQueueSize(String gameType) {
        List<userInQueue> gameList = queues.get(gameType);
        if (gameList == null) return 0;
        return gameList.size();
    }
}
