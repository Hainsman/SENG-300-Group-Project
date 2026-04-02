package ca.ucalgary.seng300.platform.domain;

import ca.ucalgary.seng300.platform.constants.Game;

public class User extends Record {
    private String login;
    private String email;
    private String passwordHash;
    private boolean isActive;

    /** Persisted per-game ELO (default 1200 for new accounts). */
    private int eloTicTacToe = 1200;
    private int eloConnectFour = 1200;
    private int gamesTicTacToe = 0;
    private int gamesConnectFour = 0;

    public User(int id, String login, String email, String passwordHash){
        super(id, User.class.getSimpleName());
        this.login = login;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isActive = true;
    }

    //getters
    public String getlogin(){return this.login;}
    public String getEmail(){return this.email;}
    public String getPasswordHash(){return this.passwordHash;}
    public boolean getIsActive(){return this.isActive;}

    public int getEloTicTacToe() {
        return eloTicTacToe <= 0 ? 1200 : eloTicTacToe;
    }

    public int getEloConnectFour() {
        return eloConnectFour <= 0 ? 1200 : eloConnectFour;
    }

    public int getGamesTicTacToe() {
        return gamesTicTacToe;
    }

    public int getGamesConnectFour() {
        return gamesConnectFour;
    }

    /** ELO for {@link Game#TICK_TACK_TOE} or {@link Game#CONNECT_FOUR}. */
    public int getEloForGame(int gameId) {
        if (gameId == Game.CONNECT_FOUR) {
            return getEloConnectFour();
        }
        return getEloTicTacToe();
    }

    public int getGamesForGame(int gameId) {
        if (gameId == Game.CONNECT_FOUR) {
            return gamesConnectFour;
        }
        return gamesTicTacToe;
    }

    public void setEloForGame(int gameId, int elo) {
        int v = Math.max(0, elo);
        if (gameId == Game.CONNECT_FOUR) {
            this.eloConnectFour = v;
        } else {
            this.eloTicTacToe = v;
        }
    }

    public void adjustEloForGame(int gameId, int delta) {
        int next = getEloForGame(gameId) + delta;
        setEloForGame(gameId, Math.max(0, next));
    }

    public void incrementGamesForGame(int gameId) {
        if (gameId == Game.CONNECT_FOUR) {
            gamesConnectFour++;
        } else {
            gamesTicTacToe++;
        }
    }

    //setters
    public void setlogin(String login){this.login = login;}
    public void setEmail(String email){this.email = email;}
    public void setPasswordHash(String passwordHash){this.passwordHash = passwordHash;}
    public void setIsActive(boolean isActive){this.isActive = isActive;}
}