package ca.ucalgary.seng300.ui.lobby;

import ca.ucalgary.seng300.ui.CentralController;
import javafx.application.Platform;

import java.util.function.Consumer;

/**
 * Cross-thread callbacks for lobby create/join (signalled from {@link ca.ucalgary.seng300.platform.client.ClientCore}).
 */
public final class LobbyBridge {

    private LobbyBridge() {}

    public static Runnable onHostOk;
    public static Runnable onJoinOk;
    public static Consumer<String> onLobbyFail;
    /** Host updates player list when guest joins.
    public static Runnable onPartnerJoined;
    /** First board sync after pairing (enables Start Game for host). */
    public static Runnable onGameState;

    public static void notifyLobbyOk(String role, String code, CentralController rootController) {
        Platform.runLater(() -> {
            if (code != null) {
                rootController.lobbyCode = code;
            }
            if ("HOST".equals(role) && onHostOk != null) {
                //CentralController.lobbyPaired = false;
                Runnable r = onHostOk;
                onHostOk = null;
                r.run();
            } else if ("JOIN".equals(role) && onJoinOk != null) {
                //CentralController.lobbyPaired = true;
                Runnable r = onJoinOk;
                onJoinOk = null;
                r.run();
            }
        });
    }

    public static void notifyLobbyFail(String detail) {
        Platform.runLater(() -> {
            Consumer<String> fail = onLobbyFail;
            onLobbyFail = null;
            onHostOk = null;
            onJoinOk = null;
            if (fail != null) {
                fail.accept(detail != null ? detail : "Lobby error");
            }
        });
    }

    public static void notifyPartnerJoined() {
        Platform.runLater(() -> {
            //CentralController.lobbyPaired = true;
            //if (onPartnerJoined != null) {
            //    onPartnerJoined.run();
            //}
        });
    }

    public static void notifyGameState() {
        Platform.runLater(() -> {
            if (onGameState != null) {
                onGameState.run();
            }
        });
    }

    /** Call when leaving the lobby screen so callbacks do not target disposed controllers.*/
    public static void clearLobbyUiCallbacks() {
        //onPartnerJoined = null;
        onGameState = null;
    }
}
