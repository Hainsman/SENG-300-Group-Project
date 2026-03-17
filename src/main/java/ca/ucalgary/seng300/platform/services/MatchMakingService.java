package ca.ucalgary.seng300.platform.services;

import ca.ucalgary.seng300.platform.constants.Rank;
import ca.ucalgary.seng300.platform.domain.Participant;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.store.PlayerStatsStore;
import ca.ucalgary.seng300.platform.utils.Helper;
import ca.ucalgary.seng300.platform.constants.Game;

import java.util.*;
import java.util.ArrayList;


public class MatchMakingService {
    // Creating new class for users in queues
    private class userInQueue{
        String userId;
        Double score;
        private userInQueue(String userId,Double score) {
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
     Map<String, List<userInQueue>> queues = new HashMap<>();
    /**
     Add a player to machmaking queue for the given gameType
     @param userId
     @param gameType eg. "TicTacToe", "ConnectFour"

     **/

    void addPlayerToQueue(String userId, String gameType ){
        // retrieving userScore
        double userScore = 0.0;
        int gameId = validateGameId(gameType);
        List<PlayerStats> statsForGame = PlayerStatsStore.findPlayersByGameID(gameId);

        for (PlayerStats stats : statsForGame) {
            if (String.valueOf(stats.getUserID()).equals(userId)){
                userScore = stats.getScore();
            }
        }
        userInQueue newEntry = new userInQueue(userId,userScore);

        // if gameType queue exists, add to it. If not, create a new one
        if (queues.containsKey(gameType)) {
            queues.get(gameType).add(newEntry);
        } else {
            List<userInQueue> newEntryList = new ArrayList<>();
            newEntryList.add(newEntry);
            queues.put(gameType, newEntryList);
        }

    }


    void removePlayer( String userId){}
    List<String> matchPlayers (String gameType){

        List <String> matchedPlayers = List.of("Player 1", "Player 2");
        return matchedPlayers;
    }



}
