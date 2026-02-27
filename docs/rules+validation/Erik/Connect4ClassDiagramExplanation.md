# Connect 4 Class Diagram Explanation #


### PlayerAction ###

I made the PlayerAction move an integer because the only input needed for connect 4 is which column a piece is being placed in, as well as which player is doing it. Full coordinates are unneeded as it is very simple to find where a piece ends up based on the column it is placed in. 

### Player Enumeration ###

The Player enumeration is used both for determining what spaces are occupied by which player, if a space is empty it should clearly be EMPTY enum, as well as to determine which player won based on which colour connected 4. 

### State Enumeration ###

The State enumeration is used so that the state of the game is easily communicated. Either the game is still in progress, or it has ended. If it has ended, then it must be a tie, or one of the players wins. 

### GameState ###

In GameState, the playerTurn variable is to track whose turn it is, which is why it is of the Player enumeration type. In GameState the board variable is a 2 dimensional array of the Player enumeration, with 6 rows and 7 columns as that is the size of a Connect 4 board. 
