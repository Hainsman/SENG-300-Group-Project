package ca.ucalgary.seng300.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;


import java.io.IOException;

public class hostLobbyController {




    @FXML private Label gameLabel;
    @FXML private TextField lobbyNameField;
    @FXML private Slider playerSlider;

    @FXML private ToggleButton privateLobby;
    @FXML private ToggleButton publicLobby;
    @FXML private ToggleGroup privacyOptions;

    @FXML private Label previewGameLabel;
    @FXML private Label previewPlayersLabel;
    @FXML private Label previewPrivacyLabel;

    @FXML
    private Button createLobbyBtn;


    @FXML
    private void initialize() {

        // 🔥 Set game from CentralController
        String game = CentralController.selectedGame;
        gameLabel.setText(game);
        previewGameLabel.setText(game);

        // Default values
        previewPlayersLabel.setText(String.valueOf((int) playerSlider.getValue()));
        previewPrivacyLabel.setText("Public");

        // Update preview when slider changes
        playerSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            previewPlayersLabel.setText(String.valueOf(newVal.intValue()));
        });

        // Update preview when privacy changes
        privacyOptions.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == publicLobby) {
                previewPrivacyLabel.setText("Public");
            } else if (newVal == privateLobby) {
                previewPrivacyLabel.setText("Private");
            }
        });
    }


    @FXML
    void handleCreateLobby(ActionEvent event) {

        String lobbyName = lobbyNameField.getText();
        int maxPlayers = (int) playerSlider.getValue();

        String privacy = "Public";
        if (privacyOptions.getSelectedToggle() == privateLobby) {
            privacy = "Private";
        }

        // 🔥 STORE DATA GLOBALLY
        CentralController.maxPlayers = maxPlayers;
        CentralController.privacy = privacy;

        // Optional
        CentralController.lobbyCode = "ABC123"; // placeholder

        System.out.println("Creating Lobby:");
        System.out.println("Game: " + CentralController.selectedGame);
        System.out.println("Name: " + lobbyName);
        System.out.println("Players: " + maxPlayers);
        System.out.println("Privacy: " + privacy);

        // 🔥 Track previous screen
        CentralController.previousScreen = "hostLobby.fxml";
        CentralController.moveTo("mainLobby.fxml");
    }


    @FXML
    void handleBack(ActionEvent event) {
        CentralController.moveTo("chooseMode.fxml");
    }


}
