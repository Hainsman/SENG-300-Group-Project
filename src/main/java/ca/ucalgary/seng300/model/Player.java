package ca.ucalgary.seng300.model;

public class Player {
    private String username;
    private String email;
    private String password;
    private Rank ranking;

    public Player(String username, String email, String password, Rank ranking) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.ranking = ranking;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Rank getRanking() { return ranking; }
}
