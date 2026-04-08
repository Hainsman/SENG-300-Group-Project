package ca.ucalgary.seng300.ui;

public enum SoundEffect {
    YOUR_TURN_NOTIFICATION("your_turn_notification");

    private final String path;

    SoundEffect(String path) {
        this.path = path;
    }

    public String getPath() {
        return getClass().getResource(String.format("/ca.ucalgary.seng300.ui/sound/%s.mp3", path)).toExternalForm();
    }
}
