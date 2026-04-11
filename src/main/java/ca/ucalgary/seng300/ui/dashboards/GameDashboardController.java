package ca.ucalgary.seng300.ui.dashboards;

import ca.ucalgary.seng300.ui.centralcontroller.CentralController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class GameDashboardController {

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
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.selectedGame = "Connect 4";
        CentralController.selectedGameId = 2;
        CentralController.moveTo("chooseModeNew.fxml");
    }

    @FXML
    void TTTclicked(MouseEvent event) {
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.selectedGame = "TicTacToe";
        CentralController.selectedGameId = 1;
        CentralController.moveTo("chooseModeNew.fxml");
    }



}
