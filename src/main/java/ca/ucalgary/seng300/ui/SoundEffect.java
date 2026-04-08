package ca.ucalgary.seng300.ui;

public enum SoundEffect {
    YOUR_TURN_NOTIFICATION("your_turn_notification", 1),
    MOVE_SUBMITTED("move_submitted", 1),
    USER_LOGGED_IN("user_logged_in", 1),
    GAME_LOST("game_lost", 1),
    GAME_WON("game_won", 1),
    GAME_STARTED("game_started", 1),
    WAITING_LOBBY("waiting_lobby", 1),
    MOUSE_CLICKED("mouse_clicked", 0.5);

    private final String path;
    private final double volume;

    SoundEffect(String path, double volume) {
        this.path = path;
        this.volume = volume;
    }

    public String getPath() {
        return getClass().getResource(String.format("/ca.ucalgary.seng300.ui/sound/%s.mp3", path)).toExternalForm();
    }

    public double getVolume() {
        return volume;
    }
}
