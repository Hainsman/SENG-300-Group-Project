package ca.ucalgary.seng300.platform.domain;

enum Type {
    USER("User"),
    GAME_HISTORY("GameHistory"),
    PARTICIPANT("Participant"),
    PLAYER_STATS("PlayerStats"),
    SESSION_History("SessionHistory"),
    MESSAGE_HISTORY("MessageHistory"); //For adding type to MessageHistory

    private final String type;

    Type(String type){this.type = type;}
    public String getTypeToString(){return this.type;}
}
