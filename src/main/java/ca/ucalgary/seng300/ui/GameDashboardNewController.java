package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.domain.SessionHistory;
import ca.ucalgary.seng300.platform.store.SessionHistoryStore;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import java.util.List;

public class GameDashboardNewController {

    @FXML
    void handleLogout(MouseEvent event) {
        if (CentralController.getInstance().clientCore != null) {
            String cid = CentralController.getInstance().clientCore.CID;
            if (cid != null && !cid.isEmpty()) {
                try {
                    int userId = Integer.parseInt(cid);
                    SessionHistoryStore store = SessionHistoryStore.getInstance();
                    List<SessionHistory> sessions = store.findSessionsByUserID(userId);
                    for (SessionHistory session : sessions) {
                        // deleted the users UUID by calling the SessionHistoryStore.delete(new SessionHistory(exact param here))
                        store.delete(new SessionHistory(session.getID(), session.getUserId(), session.getGloballyUniqueSessionIdentifier()));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            CentralController.getInstance().clientCore.sendLogout();
        }
        CentralController.moveTo("Login.fxml");
    }

    @FXML
    void handleOpenSettings(MouseEvent event) {
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.moveTo("Settings.fxml");
    }

    @FXML
    void C4click(MouseEvent event) {
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.selectedGame = "Connect 4";
        CentralController.moveTo("chooseModeNew.fxml");
    }

    @FXML
    void TTTclicked(MouseEvent event) {
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.selectedGame = "TicTacToe";
        CentralController.moveTo("chooseModeNew.fxml");
    }
}
