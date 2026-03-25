package ca.ucalgary.seng300.game.core.tictactoe;

public class TTTMultiplayerClient2 {
    static void main(String[] args) {
        TTTMultiplayerClient t = new TTTMultiplayerClient();
        t.run("Bob", "localhost", 9090);
    }
}