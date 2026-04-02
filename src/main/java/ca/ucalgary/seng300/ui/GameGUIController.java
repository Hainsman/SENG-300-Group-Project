package ca.ucalgary.seng300.ui;
// Contributors: Kade Peters, Jarod Rideout

import ca.ucalgary.seng300.game.core.Game;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.server.ServerMain;
import ca.ucalgary.seng300.platform.session.TicTacToeSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;

public class GameGUIController {
    private CentralController control;

    public void setRoot(CentralController c){
        control = c;
        ticTacToeViewController.setBoss(control);
        ClientCore cc = c.getCC();
        if (cc != null && cc.getLastUpdate() instanceof TTTGameState gs) {
            ticTacToeViewController.refreshFromGameStateUpdate(gs);
        }
    }

    @FXML
    private SplitPane splitPane;
    @FXML
    private VBox leftSideBar;
    @FXML
    private VBox childView; // Injects the root of child.fxml
    @FXML
    private Connect4Controller connect4ViewController; // Injects the child's controller
    @FXML
    private TicTacToeController ticTacToeViewController; // Injects the child's controller

    // ELO Table Variables
    @FXML
    private Label eloValue; // Value of elo to be displayed
    @FXML
    private Label winValue; // Elo change based on win
    @FXML
    private Label lossValue; // Elo change based on loss
    @FXML
    private Label drawValue; // Elo change based on draw
    @FXML
    private Label rankValue; // Elo description string

    // History Box
    @FXML
    public VBox historyBox;

    // Chat box
    @FXML
    public VBox chatBox;
    @FXML private VBox matchChat;
    @FXML private TextField chatInput;

    // Accompanying Labels
    @FXML
    private Label connect4Label;
    @FXML
    private Label tttLabel;
    @FXML
    private Button infoButton;

    @FXML
    public void initialize() {
        GameGUI.setHistoryBox(historyBox);
        GameGUI.setChatBox(matchChat);
        GameGUI.setEloLabels(eloValue, winValue, lossValue, drawValue, rankValue);
        ticTacToeViewController.setSession(new TicTacToeSession());
        ticTacToeViewController.setRandomOpponentEnabled(true);
    }

    @FXML
    private void handleHostGame(ActionEvent event) {/*
        ClientCore client = new ClientCore("127.0.0.1", ServerMain.DEFAULT_PORT);   // Random IP address for now
        if (!client.connect()) {
            showAlert("Networking is currently a stub (matchmaking in progress). Platform core will implement connect() and session APIs later.");
            return;
        }
        if (client.createSession(null) == null || client.getSessionId() == null) {
            showAlert("Could not create session.");
            return;
        }
        hideGameScreens(new GameGUI.GameViewData(client.getMyPlayerId(), null, "TICTACTOE", 3, 3, new String[0]));
        ticTacToeViewController.setSession(null);
        ticTacToeViewController.setClientCore(client);
        showCopyableSessionIdAlert(client.getSessionId());*/
    }

    @FXML
    private void handleJoinGame(ActionEvent event) {/*
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog("");
        dialog.setTitle("Join game");
        dialog.setHeaderText("Enter session ID from the host:");
        dialog.showAndWait().ifPresent(sessionId -> {
            if (sessionId == null || sessionId.isBlank()) return;
            ClientCore client = new ClientCore("127.0.0.1", ServerMain.DEFAULT_PORT);
            if (!client.connect()) {
                showAlert("Networking is currently a stub (matchmaking in progress). Platform core will implement connect() and session APIs later.");
                return;
            }
            if (client.joinSession(sessionId.trim(), null) == null || client.getSessionId() == null) {
                showAlert("Could not join session. Check the ID.");
                return;
            }
            hideGameScreens(new GameGUI.GameViewData(client.getMyPlayerId(), null, "TICTACTOE", 3, 3, new String[0]));
            ticTacToeViewController.setSession(null);
            ticTacToeViewController.setClientCore(client);
        };*/
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    private void showCopyableSessionIdAlert(String sessionId) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Session created");
        alert.setHeaderText("Share this ID with player 2:");
        TextField field = new TextField(sessionId);
        field.setEditable(false);
        field.setMinWidth(280);
        field.selectAll();
        VBox box = new VBox(8, field);
        box.setPadding(new Insets(0, 0, 8, 0));
        alert.getDialogPane().setContent(box);
        alert.showAndWait();
    }

    @FXML
    private void handleInfoButton(ActionEvent event) {
        CentralController.moveTo("info.fxml");

    }

    @FXML
    private void handleChatSend(ActionEvent event) {
        String msg = chatInput.getText().trim();
        if (msg.isBlank()) return;
        String sender = "You"; // default
        if (ticTacToeViewController != null && ticTacToeViewController.currentPlayerID != null) {
            sender = ticTacToeViewController.currentPlayerID;
        } else if (connect4ViewController != null && connect4ViewController.currentPlayerID != null) {
            sender = connect4ViewController.currentPlayerID;
        }
        GameGUI.addChat(sender, msg, Color.web("#fafafa"));
        chatInput.clear();
    }

    @FXML
    private void handleExitButton(ActionEvent event) {
        Platform.exit();
    }

    public void hideGameScreens(GameGUI.GameViewData data){
        String gameType = data.gameType.toUpperCase();
/*
        if(gameType.equals("TICTACTOE")){
            ticTacToeViewController.setVisibility(true);
            connect4ViewController.setVisibility(false);
            tttLabel.setVisible(true);
            connect4Label.setVisible(false);

        } else if(gameType.equals("CONNECT4")) {
            ticTacToeViewController.setVisibility(false);
            connect4ViewController.setVisibility(true);
            tttLabel.setVisible(false);
            connect4Label.setVisible(true);
        }*/

    }
}
