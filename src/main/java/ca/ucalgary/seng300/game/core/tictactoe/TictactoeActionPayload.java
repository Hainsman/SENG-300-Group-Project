package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.ActionPayload;

public record TictactoeActionPayload(int row, int col) implements ActionPayload { }
