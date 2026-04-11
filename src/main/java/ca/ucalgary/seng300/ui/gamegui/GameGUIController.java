package ca.ucalgary.seng300.ui.gamegui;
// Contributors: Kade Peters, Jarod Rideout

import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.ui.gamecontrollers.TicTacToeController;
import ca.ucalgary.seng300.ui.centralcontroller.CentralController;
import javafx.fxml.FXML;
import javafx.scene.layout.*;

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
    private TicTacToeController ticTacToeViewController; // Injects the child's controller
    // History Box
    @FXML
    public VBox historyBox;
}
