package ca.ucalgary.seng300.game.core.connect4;

import ca.ucalgary.seng300.game.core.ActionPayload;

public record Connect4ActionPayload(int row, int col) implements ActionPayload { }
