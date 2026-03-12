package ca.ucalgary.seng300.platform.services;

import ca.ucalgary.seng300.platform.constants.Rank;
import ca.ucalgary.seng300.platform.domain.Participant;
import ca.ucalgary.seng300.platform.utils.Helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;





public class MatchMakingService {
    // Creating new class for users in Queue
    private class userInQueue{
        String userId;
        Integer score;
        private userInQueue(String userId,Integer score) {
            this.userId = userId;
            this.score = score;
        }

    }

    //Key is gameType, value is player id and score
    static Map<String, List<userInQueue>> Queue = new HashMap<>();
    /**
     Add a player to machmaking queue for the given gameType
     @param userId
     @param gameType eg. "TicTacToe", "ConnectFour"

     **/
    void addPlayerToQueue(String userId, String gameType ){

    }
    void removePlayer( String userId){}
    List<String> matchPlayers (String gameType){

        List <String> matchedPlayers = List.of("Player 1", "Player 2");
        return matchedPlayers;
    }



}
