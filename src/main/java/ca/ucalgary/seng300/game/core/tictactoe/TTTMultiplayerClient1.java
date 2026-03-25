package ca.ucalgary.seng300.game.core.tictactoe;

public class TTTMultiplayerClient1 {
    static void main(String[] args) {
        TTTMultiplayerClient t = new TTTMultiplayerClient();
        t.run("Alice", "localhost", 9090);
    }
}