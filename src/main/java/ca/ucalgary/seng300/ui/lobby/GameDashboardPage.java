package ca.ucalgary.seng300.ui.lobby;

import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.ui.CentralController;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class GameDashboardPage extends AnchorPane implements Navigable {

    private CentralController cent;
    public GameDashboardPage(CentralController c) {
        try {
            cent = c;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ca.ucalgary.seng300.ui/" + getNavigation().getFxml()));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleProfileDashboard(ActionEvent event) {
        CentralController.moveTo(Navigation.PROFILE_DASHBOARD);
    }

    @FXML
    void handleTeamSettings(ActionEvent event) {
        CentralController.moveTo(Navigation.SETTINGS);
    }

    @FXML
    void handleLogout(ActionEvent event) {
        CentralController.logout();
    }

    @FXML
    void C4click(MouseEvent event) {
        cent.clientCore.gameType = GameType.CONNECT4;
        CentralController.selectedGame = GameType.CONNECT4;
        CentralController.moveTo(Navigation.CHOOSE_MODE);
    }

    @FXML
    void TTTclicked(MouseEvent event) {
        cent.clientCore.gameType = GameType.TIC_TAC_TOE;
        CentralController.selectedGame = GameType.TIC_TAC_TOE;
        CentralController.moveTo(Navigation.CHOOSE_MODE);
    }

    @Override
    public Navigation getNavigation() {
        return Navigation.GAME_DASHBOARD;
    }

    @Override
    public void reset() {

    }
}
