package ca.ucalgary.seng300.ui.userdata;

import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.ui.CentralController;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class InfoPage extends AnchorPane implements Navigable {

    @FXML private Button backButton;
    @FXML private Label gameTitleLabel;
    @FXML private Label taglineLabel;
    @FXML private Label bodyLabel;

    public InfoPage() {
        try {
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
    private void handleBack(ActionEvent event) {
        if (CentralController.previousScreen != null) {
            CentralController.moveTo(CentralController.previousScreen);
        } else {
            CentralController.moveTo(Navigation.GAME_DASHBOARD);
        }
    }

    /**
     * Resolves {@link GameType} from {@link CentralController#selectedGame}, or from
     * {@link CentralController#previousScreen} when Info is opened from in-game (TTT / C4).
     */
    private static GameType resolveGameType() {
        GameType g = CentralController.selectedGame;
        if (g != null) {
            return g;
        }
        Navigation prev = CentralController.previousScreen;
        if (prev == Navigation.TTT) {
            return GameType.TIC_TAC_TOE;
        }
        if (prev == Navigation.C4) {
            return GameType.CONNECT4;
        }
        return GameType.TIC_TAC_TOE;
    }

    private static String bodyFor(GameType g) {
        return switch (g) {
            case TIC_TAC_TOE -> """
                    Two players take turns on a 3×3 grid. You play as X or O depending on the match.

                    Place your mark on an empty cell each turn. The first player to get three in a row — horizontally, vertically, or diagonally — wins the game.

                    If every cell is filled and no one has three in a row, the game is a draw.

                    Online: create or join a lobby from the game hub, then play in real time. While a ranked online match is in progress, use Forfeit in the sidebar if you must leave; ratings may update when the game ends.""";
            case CONNECT4 -> """
                    Two players take turns dropping a piece into one of seven columns. The board is seven columns wide and six rows tall; pieces fall to the lowest empty slot in that column.

                    The first player to connect four pieces in a row — horizontally, vertically, or diagonally — wins.

                    If the board is full and neither player has four in a row, the game is a draw.

                    Online: host or join a lobby, then take turns when it is your move. Use Forfeit in the sidebar to resign an online match; ELO may change when the result is recorded.""";
            case CHECKERS -> """
                    Rules for this mode are not shown here. Use Back to return to the game hub and choose Tic-Tac-Toe or Connect 4 for full online play in this app.""";
        };
    }

    private void applyContent(GameType g) {
        String title = switch (g) {
            case TIC_TAC_TOE -> "Tic-Tac-Toe";
            case CONNECT4 -> "Connect 4";
            case CHECKERS -> "Checkers";
        };
        String tag = switch (g) {
            case TIC_TAC_TOE -> "Classic 3×3 — get three in a row";
            case CONNECT4 -> "Drop pieces — connect four to win";
            case CHECKERS -> "Game information";
        };
        gameTitleLabel.setText(title);
        taglineLabel.setText(tag);
        bodyLabel.setText(bodyFor(g));
    }

    @Override
    public Navigation getNavigation() {
        return Navigation.INFO;
    }

    @Override
    public void reset() {
        applyContent(resolveGameType());
    }
}
