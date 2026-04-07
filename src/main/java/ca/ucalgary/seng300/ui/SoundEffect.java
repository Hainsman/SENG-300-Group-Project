package ca.ucalgary.seng300.ui;

public enum SoundEffect {
    YOUR_TURN_NOTIFICATION;

    public String getPath() {
        String path = switch (this) {
            case YOUR_TURN_NOTIFICATION -> "your_turn_notification";
        };

        return getClass().getResource(String.format("/ca.ucalgary.seng300.ui/sound/%s.mp3", path)).toExternalForm();
    }
}
