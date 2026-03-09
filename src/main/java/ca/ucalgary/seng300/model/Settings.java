package ca.ucalgary.seng300.model;

import ca.ucalgary.seng300.platform.store.UserStore; // TODO change if that is not the database

public class Settings {

    private final Player player;
    private final UserStore store; // TODO Whatever that ends up looking like

    public Settings(Player player, UserStore store) {
        this.player = player;
        this.store = store;
    }

    public boolean deleteAccounts(String password) {
        // TODO hook up with UI and PlatformCore
        return true;
    }

    public boolean changeUsername(String newUsername) {
        // TODO hook up to the UserStore
        if (store.findByUserName(newUsername))
            return false;

        player.updateUsername(player.getUsername(), newUsername);

        return true;
    }

    public boolean changeEmail(String newEmail) {
        // TODO hook up to the UserStore
        if (store.findByEmail(newEmail))
            return false;

        player.updateEmail(player.getUsername(), newEmail);

        return true;
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        // TODO the logic for the password lookup
        if (!player.getPassword().equals(oldPassword) || player.getPassword().equals(newPassword))
            return false;

        player.updatePassword(player.getUsername(), newPassword);

        return true;
    }

    public boolean logout() {
        // TODO hook up with UI and PlatformCore
        player.logout();

        return true;
    }


}
