package ca.ucalgary.seng300.platform.lobby;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.services.LeaderBoardService;

import java.util.List;

public abstract class Lobby {

    public MatchMakingService matchMakingService;
    public LeaderBoardService leaderBoardService;
    public String state;

    public Lobby() {
        this.matchMakingService = new MatchMakingService();
        this.leaderBoardService = new LeaderBoardService();
        this.state = "Browsing";
    }

    // State
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    // Players Queue
    public void joinQueue(PlayerStats player) {
        matchMakingService.addPlayerToQueue(player);
        setState("InQueue");
    }

    public void leaveQueue(PlayerStats player) {
        matchMakingService.removePlayer(player);
        setState("Browsing");
    }

    // Matching Players
    public List<PlayerStats> findMatch() {
        List<PlayerStats> matched = matchMakingService.matchPlayers(getGameType());
        if (matched != null) setState("MatchFound");
        return matched;
    }

    // Viewing Leaderboard
    public List<PlayerStats> getLeaderboard(int count) {
        return leaderBoardService.getLeaderBoard(false, getGameType(), count);
    }

    // Methods Dependent on Game
    public abstract int getGameType();
    public abstract void startGame(List<PlayerStats> players);
    public abstract void submitMove(String playerId, String move);
    public abstract void endGame(String sessionId);
}
