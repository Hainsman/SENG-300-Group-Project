package ca.ucalgary.seng300.ui;

public enum SoundEffect {
    YOUR_TURN_NOTIFICATION("your_turn_notification"),
    MOVE_SUBMITTED("move_submitted"),
    USER_LOGGED_IN("user_logged_in"),
    GAME_LOST("game_lost"),
    GAME_STARTED("game_started"),
    WAITING_LOBBY("waiting_lobby");

    private final String path;

    SoundEffect(String path) {
        this.path = path;
    }

    public String getPath() {
        return getClass().getResource(String.format("/ca.ucalgary.seng300.ui/sound/%s.mp3", path)).toExternalForm();
    }
}
