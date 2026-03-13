package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.ActionPayload;

public record TTTActionPayload(int row, int col) implements ActionPayload { }
