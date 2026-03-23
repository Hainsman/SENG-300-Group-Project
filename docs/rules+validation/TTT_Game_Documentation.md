# Tic-Tac-Toe Game Implementation (Java)

## Overview
This project implements a modular Tic-Tac-Toe (TTT) game using a structured game framework. It separates responsibilities into game state, rule validation, game control, and user interaction.

## File Structure
- `TTTGame.java` → Main game controller
- `TTTGameState.java` → Board + game logic
- `TTTRuleValidator.java` → Validates moves
- `TTTPlayerAction.java` → Represents player actions
- `TTTActionPayload.java` → Stores move coordinates
- `TTTActionType.java` → Defines action types
- `TTTCLI.java` → Command-line interface

## Game Engine (TTTGame)
The game engine manages the overall game flow.

Responsibilities:
- Initializes the game state
- Receives player actions
- Validates actions using `TTTRuleValidator`
- Applies valid actions to the game state
- Determines if the game is over

Key behavior:
- Calls `validateMove()` before applying any action
- Game ends when state is no longer `IN_PROGRESS`

## Game State (TTTGameState)
The game state maintains the board and game rules.

### Board Representation
~~~java
Player[][] board = new Player[3][3];
~~~

### Core Responsibilities
- Track current player turn
- Apply moves to the board
- Detect win conditions
- Detect tie conditions

### Move Application Logic
When a move is applied:
1. Place the player's symbol
2. Check if the move results in a win
3. If not, check if the board is full (tie)
4. Otherwise, switch turns

### Win Conditions
A player wins if they complete:
- A row
- A column
- The main diagonal
- The anti-diagonal

## Rule Validation (TTTRuleValidator)
This class ensures only valid moves are allowed.

### Validation Rules
A move is rejected if:
- The action is from the wrong player (if turn enforcement is enabled)
- The game is not in progress
- The row or column is outside the range `0` to `2`
- The selected cell is already occupied

### Valid Action Generation
The method `getValidActions()`:
- Iterates over the board
- Returns all empty positions as possible moves

## Player Actions

### TTTPlayerAction
Extends `PlayerAction` and provides:
- Strong typing for payload
- Strong typing for action type

### TTTActionPayload

Represents the coordinates of a move on the Tic-Tac-Toe board.  
It contains:
- a row index
- a column index

This information is used by the game logic to update the board state when a player makes a move.

### TTTActionType
~~~text
MOVE, RESIGN
~~~

## Command Line Interface (TTTCLI)
Provides a simple way to play the game.

### Flow
1. Prompt user for row and column
2. Create a `TTTPlayerAction`
3. Send action to the game engine
4. Print updated board

### Behavior
- Runs continuously in a loop
- Displays the board after every move

## Game Flow Summary
~~~text
User Input (CLI)
→ Create Action
→ Validate Move (TTTRuleValidator)
→ Apply Move (TTTGameState)
→ Update Board
→ Check Win/Tie
→ Next Turn or End Game
~~~

## Example Gameplay
~~~text
Enter row col: 0 0
[PLAYER_1, EMPTY, EMPTY]
[EMPTY, EMPTY, EMPTY]
[EMPTY, EMPTY, EMPTY]

Enter row col: 1 1
[PLAYER_1, EMPTY, EMPTY]
[EMPTY, PLAYER_2, EMPTY]
[EMPTY, EMPTY, EMPTY]
~~~

## Design Decisions

### Separation of Concerns
- Validation, logic, and UI are separated
- Improves readability and maintainability

### Type Safety
- Custom action and payload classes prevent casting issues

### Extensibility
- Easy to extend with:
    - AI player
    - GUI interface
    - Additional actions such as resign

## Future Improvements
- Add input validation in CLI
- Add game-over message display
- Implement `RESIGN` functionality
- Add an AI opponent
- Replace CLI with a GUI

## Conclusion
This project demonstrates a clean and modular implementation of Tic-Tac-Toe using Java. The architecture ensures scalability, maintainability, and clear separation between validation, state management, and interaction layers.
