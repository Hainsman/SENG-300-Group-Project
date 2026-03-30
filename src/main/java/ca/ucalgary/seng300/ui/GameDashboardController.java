package ca.ucalgary.seng300.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.awt.*;
import java.util.ArrayList;

public class GameDashboardController {

    @FXML
    private Label C4Label;

    @FXML
    private Button ExitButton;

    @FXML
    private Button ProfileButton;

    @FXML
    private Button SettingsButton;

    @FXML
    private Label TTTLabel;

    @FXML
    private Button infoButton;

    @FXML
    private AnchorPane friendsListPane;

    private ArrayList<Label> FriendList = new ArrayList<>();

    @FXML
    void handleExitButton(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void handleInfoButton(ActionEvent event) {
        CentralController.moveTo("info.fxml");

    }
    public void setFriendList(String f1,String f2, String f3){
        addToFriendlist(f1,1);
        addToFriendlist(f2,2);
        addToFriendlist(f3,3);
    }

    private void addToFriendlist(String username,int num){
        Label newFriend = new Label();
        newFriend.setText(username);
        newFriend.setVisible(true);
        newFriend.setLayoutX(28);
        newFriend.setLayoutY(34*num);
        newFriend.setTextFill(Color.WHITE);
        newFriend.setStyle("-fx-font-size: 20");
        friendsListPane.getChildren().add(newFriend);
        FriendList.add(newFriend);
    }
    @FXML
    public void initialize() {
        setFriendList("User1","User2","User3");

    }

    @FXML
    void C4click(MouseEvent event) {
        CentralController.selectedGame = "Connect 4";
        CentralController.moveTo("chooseMode.fxml");
    }

    @FXML
    void TTTclicked(MouseEvent event) {
        CentralController.selectedGame = "TicTacToe";
        CentralController.moveTo("chooseMode.fxml");
    }



}
