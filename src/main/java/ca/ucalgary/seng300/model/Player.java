package ca.ucalgary.seng300.model;

public class Player {


    private String playerId;
    private String username;
    private String email;
    private String password;

    //the player's current rank
    //defaults to BRONZE on account creation
    //updated by GeneralStats via updateRank()
    private Rank ranking;

    public Player(String playerId, String username, String email, String password) {
        this.playerId= playerId;
        this.username= username;
        this.email= email;
        this.password= password;
        this.ranking= Rank.BRONZE;
    }

    // Authentication (to be handled by LoginPage / PlatformCore) 
    public void login() {
        // Bring up with UI and PlatformCore
    }

    public void logout() {
        // Bring up with UI and Settings
    }

    // Stats 
    public GeneralStats getStats(String gameType) {
        // Wait for Andon (GeneralStats)
        return null;
    }

    //  Updates
    public void updateUsername(String oldUsername, String newUsername) {
        if (this.username.equals(oldUsername)) {
            this.username= newUsername;
        }
    }

    public void updateEmail(String username, String email) {
        if (this.username.equals(username)) {
            this.email= email;
        }
    }

    public void updatePassword(String username, String password) {
        if (this.username.equals(username)) {
            this.password=password;
        }
    }

    // Getters & Setters
    public String getPlayerId() { return playerId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Rank getRanking() { return ranking; }
    public void setRanking(Rank ranking) { this.ranking = ranking; }
}
