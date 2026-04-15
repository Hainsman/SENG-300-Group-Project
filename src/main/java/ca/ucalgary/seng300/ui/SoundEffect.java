package ca.ucalgary.seng300.ui;

import java.net.URL;

public enum SoundEffect {
    YOUR_TURN_NOTIFICATION("your_turn_notification", 1),
    MOVE_SUBMITTED("move_submitted", 1),
    USER_LOGGED_IN("user_logged_in", 1),
    GAME_LOST("game_lost", 1),
    GAME_WON("game_won", 1),
    GAME_STARTED("game_started", 1),
    WAITING_LOBBY("waiting_lobby", 1),
    MOUSE_CLICKED("mouse_clicked", 0.5),
    TIE("vineboom", 1),
    APP_STARTUP("app_startup", 1);

    private final String path;
    private final double volume;

    SoundEffect(String path, double volume) {
        this.path = path;
        this.volume = volume;
    }

    /** Absolute URL string for JavaFX {@link javafx.scene.media.AudioClip}. */
    public String getPath() {
        URL url = SoundEffect.class.getResource(String.format("/ca.ucalgary.seng300.ui/sound/%s.mp3", path));
        if (url == null) {
            throw new IllegalStateException("Missing sound resource: /ca.ucalgary.seng300.ui/sound/" + path + ".mp3");
        }
        return url.toExternalForm();
    }

    public double getVolume() {
        return volume;
    }
}
