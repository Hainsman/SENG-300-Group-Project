# Tic Tac Toe Architecture

The purpose of this document is to give an overview of how the game Tic Tac Toe was designed, as well as how it will be implemented.

## Rules

Tic-tac-toe is a two-player game played on a 3x3 grid. Players are assigned either X or O and take turns placing their symbol in an empty square of the grid. The first to line up three symbols (in a row, column or diagonally), wins. If the grid is filled without a winner, the game comes to a draw.

## Implementation overview

The system was designed as a modular, object-oriented game engine. `Game` controls the flow, while `GameState` stores the board data and current status, and `RuleValidator` enforces the rules of the game. 

- `TicTacToeGame`: implements the interface `Game` and works as the game engine. It holds the current game state and delegates validation to the `RuleValidator`. It also processes moves and ouputs `GameUpdate` objects.
- `TicTacToeGameState`: is the container that implements `GameState`. It stores the `board` (the 3x3 grid), the `activePlayerId`, `moveCount`, and the current `GameStatus` (ACTIVE, DRAW OR WIN).
- `TicTacToeRuleValidator`: implements `RuleValidator` and essentially runs the logic. It takes a `PlayerAction` (a move), checks against the `GameState` and returns a `ValidationResult` that indicates whether the move is legal or not.

## State Machine

The primary states are `ACTIVE`, `WIN` and `DRAW`.

1. Initialization: the grame is created, the board (2D array) is populated with blank symbols and the state is set to `ACTIVE`.
2. Game loop: the game receives a `handleAction()`, and if `validadeAction` passes, the player's symbol is placed, `moveCount` is incremented and we switch the active player. If validation fails, the move is rejected and the state remains `ACTIVE`.
3. Termination: if `moveCount` reaches 9 without a winner the game comes to a `DRAW`, otherwise the player who managed to line up three symbols wins, in which case the state is changed to `WIN`. `getGameUpdate()` broadcasts the final result and the state machine terminates.

## Game Sequence overview

1. Input: `GameSessionManager` sends a `TicTacToePlayerAction` to `TicTacToeGame.handleAction()`.
2. Validation: `TicTacToeGame` passes the action and current `TicTacToeGameState` to `TicTacToeRuleValidator`.
3. Execution: if `ValidationResult` returns true, the engine updates the board, increments the move counter and checks the game state (WIN/DRAW).
4. Output: the engine generates a `GameUpdate` that contains the new board and `GameStatus`, which is then passed to the session manager.

