package ca.ucalgary.seng300.ui;

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
    /** Host updates player list when guest joins. */
    public static Runnable onPartnerJoined;
    /** First board sync after pairing (enables Start Game for host). */
    public static Runnable onGameState;
    /** Roster info received with player names and ELO. */
    public static Consumer<String> onLobbyRoster;
    /** Player stats updated after game ends (score/rank changes). */
    public static Consumer<String> onPlayerStatsUpdated;
    /** Arbitrary system notifications. */
    public static Consumer<String> onChatNotification;

    public static void notifyLobbyOk(String role, String code) {
        Platform.runLater(() -> {
            if (code != null) {
                CentralController.lobbyCode = code;
            }
            if ("HOST".equals(role) && onHostOk != null) {
                CentralController.lobbyPaired = false;
                Runnable r = onHostOk;
                onHostOk = null;
                r.run();
            } else if ("JOIN".equals(role) && onJoinOk != null) {
                CentralController.lobbyPaired = true;
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
            CentralController.lobbyPaired = true;
            if (onPartnerJoined != null) {
                onPartnerJoined.run();
            }
        });
    }

    public static void notifyGameState() {
        Platform.runLater(() -> {
            if (onGameState != null) {
                onGameState.run();
            }
        });
    }

    public static void notifyLobbyRoster(String rosterData) {
        Platform.runLater(() -> {
            if (onLobbyRoster != null) {
                onLobbyRoster.accept(rosterData);
            }
        });
    }

    public static void notifyPlayerStatsUpdated(String statsData) {
        Platform.runLater(() -> {
            if (onPlayerStatsUpdated != null) {
                onPlayerStatsUpdated.accept(statsData);
            }
        });
    }

    public static void notifyChatNotification(String message) {
        Platform.runLater(() -> {
            if (onChatNotification != null) {
                onChatNotification.accept(message);
            }
        });
    }

    /** Call when leaving the lobby screen so callbacks do not target disposed controllers. */
    public static void clearLobbyUiCallbacks() {
        onPartnerJoined = null;
        onGameState = null;
        onLobbyRoster = null;
        onPlayerStatsUpdated = null;
        onChatNotification = null;
    }
}
