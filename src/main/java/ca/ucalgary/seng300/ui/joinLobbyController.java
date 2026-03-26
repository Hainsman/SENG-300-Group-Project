package ca.ucalgary.seng300.ui;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class joinLobbyController {


    // FXML ELEMENTS


    @FXML private Label gameTitleLabel;

    @FXML private TextField codeField;
    @FXML private Button joinByCodeBtn;

    @FXML private TextField searchField;
    @FXML private ListView<String> lobbyList;



    @FXML
    public void initialize() {

        // Set game title from CentralController
        gameTitleLabel.setText(CentralController.selectedGame);

        // Dummy lobby list (you can replace later with real data)
        lobbyList.getItems().addAll(
                "Lobby 1 - ABC123",
                "Lobby 2 - XYZ789",
                "Lobby 3 - QWE456"
        );

        // Double-click to join lobby
        lobbyList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selected = lobbyList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    joinLobbyFromList(selected);
                }
            }
        });

        // Button click
        joinByCodeBtn.setOnAction(e -> handleJoinByCode());
    }


    private void handleJoinByCode() {

        String code = codeField.getText().trim();

        if (code.isEmpty()) {
            System.out.println("Please enter a lobby code");
            return;
        }

        System.out.println("Joining lobby with code: " + code);

        // Store data
        CentralController.lobbyCode = code;
        CentralController.maxPlayers = 4; // placeholder
        CentralController.privacy = "Public"; // placeholder

        // Track previous screen
        CentralController.previousScreen = "joinLobby.fxml";

        // Navigate
        CentralController.moveTo("mainLobby.fxml");
    }



    private void joinLobbyFromList(String lobby) {

        System.out.println("Joining: " + lobby);

        // Extract code (simple split)
        String code = lobby.split("-")[1].trim();

        CentralController.lobbyCode = code;
        CentralController.maxPlayers = 4; // placeholder
        CentralController.privacy = "Public";

        CentralController.previousScreen = "chooseMode.fxml";

        CentralController.moveTo("mainLobby.fxml");
    }



    @FXML
    private void handleBack() {
        CentralController.moveTo("chooseMode.fxml");
    }
}
