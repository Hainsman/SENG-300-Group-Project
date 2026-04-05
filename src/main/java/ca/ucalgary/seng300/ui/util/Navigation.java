package ca.ucalgary.seng300.ui.util;

public enum Navigation {
    TTT ("TTT"),
    C4 ("C4"),
    CHOOSE_MODE ("ChooseMode"),
    GAME_DASHBOARD ("GameDashboard"),
    GAME_GUI ("GameGUI"),
    HOST_LOBBY ("HostLobby"),
    INFO ("Info"),
    JOIN_LOBBY ("JoinLobby"),
    LOGIN ("Login"),
    MAIN_LOBBY ("MainLobby"),
    NEW_USER ("NewUser"),
    PROFILE_DASHBOARD ("ProfileDashboard"),
    SETTINGS ("Settings");

    Navigation(String fxmlForm) {
        this.fxmlForm = fxmlForm + ".fxml";
    }

    private final String fxmlForm;

    public String getFxml() {
        return fxmlForm;
    }

    public static Navigation getFromFxml(String fxml) {
        if (fxml != null && !fxml.isBlank())
            for (Navigation nav : Navigation.values())
                if (nav.getFxml().equalsIgnoreCase(fxml))
                    return nav;

        return null;
    }
}
