package ca.ucalgary.seng300.platform.domain;

import ca.ucalgary.seng300.game.core.GameType;

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

    /** ELO for {@link GameType#TIC_TAC_TOE} or {@link GameType#CONNECT4}. */
    public int getEloForGame(GameType gameType) {
        if (gameType == GameType.CONNECT4) {
            return getEloConnectFour();
        }
        return getEloTicTacToe();
    }

    public int getGamesForGame(GameType gameType) {
        if (gameType == GameType.CONNECT4) {
            return gamesConnectFour;
        }
        return gamesTicTacToe;
    }

    public void setEloForGame(GameType gameType, int elo) {
        int v = Math.max(0, elo);
        if (gameType == GameType.CONNECT4) {
            this.eloConnectFour = v;
        } else {
            this.eloTicTacToe = v;
        }
    }

    public void adjustEloForGame(GameType gameType, int delta) {
        int next = getEloForGame(gameType) + delta;
        setEloForGame(gameType, Math.max(0, next));
    }

    public void incrementGamesForGame(GameType gameType) {
        if (gameType == GameType.CONNECT4) {
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