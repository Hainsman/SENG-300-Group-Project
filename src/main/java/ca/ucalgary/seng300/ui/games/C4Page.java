package ca.ucalgary.seng300.ui.games;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.platform.session.ConnectFourSession;
import ca.ucalgary.seng300.ui.*;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.io.IOException;

public class C4Page extends CommonGameLogic implements Navigable {

    private static final String player1Color = "#dd3030";
    private static final String player2Color = "#fad44a";

    public C4Page(CentralController rootController, GameGUIPage parentGUI) {
        super(
                rootController,
                parentGUI,
                6,
                7,
                GameType.CONNECT4,
                "c4-cell",
                Color.web(player1Color),
                Color.web(player2Color),
                "Red",
                "Yellow"
        );

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

    @Override
    public Navigation getNavigation() {
        return Navigation.C4;
    }

    @Override
    protected void updateButton(Button button, Player player) {
        button.setDisable(player != Player.EMPTY);
        if (player == Player.PLAYER_1) {
            button.setStyle("-fx-background-color: " + player1Color + "; -fx-border-radius: 1000em; -fx-background-radius: 1000em;");
        }
        else if (player == Player.PLAYER_2) {
            button.setStyle("-fx-background-color: " + player2Color + "; -fx-border-radius: 1000em; -fx-background-radius: 1000em;");
        }
        else {
            button.setStyle("-fx-background-color: #19244C; -fx-border-radius: 1000em; -fx-background-radius: 1000em;");
        }
    }

    @Override
    protected void setNewSession() {
        session = new ConnectFourSession();
    }
}
