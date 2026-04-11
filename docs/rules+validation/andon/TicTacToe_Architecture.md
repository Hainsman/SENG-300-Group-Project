# Tic Tac Toe Architecture

## Rules

Tic-tac-toe is a two-player game on a 3x3 grid. Players take turns placing X or O in empty squares. First to line up three in a row, column, or diagonal wins. If the grid fills without a winner, it's a tie.

## Implementation Overview

Tic Tac Toe extends the shared game abstraction. `TicTacToeGame` extends `Game`, holds a `TicTacToeGameState`, and delegates validation to `TicTacToeRuleValidator`.

- `TicTacToeGame`: game engine. Processes moves and outputs `TicTacToeGameUpdate` objects.
- `TicTacToeGameState`: extends `GameState`. Stores the `board` (Symbol[3][3]), `currentPlayer`, `moveCount`, and `State`.
- `TicTacToeRuleValidator`: extends `RuleValidator`. Validates moves and returns a `ValidationResult` with failure reason and valid moves.
- `TicTacToePlayerAction`: extends `PlayerAction`. Contains a `Player` and `Position` (row, col).
- `TicTacToeGameUpdate`: contains `changedPosition`, `placedSymbol`, and resulting `State`.

## State Machine

States: `IN_PROGRESS`, `P1_WIN`, `P2_WIN`, `TIE`.

1. **Init**: `createInitialState(players)` populates the board with `BLANK` symbols. State is `IN_PROGRESS`.
2. **Loop**: `handleAction()` delegates to `validateMove()`. If valid, `applyAction()` places the symbol, increments `moveCount`, and switches player. If invalid, a `ValidationResult` with the failure reason is returned.
3. **End**: `isGameOver()` checks after each move. Three in a row → `P1_WIN`/`P2_WIN`. Board full with no winner → `TIE`.

## Game Sequence

1. `GameSessionManager` sends a `TicTacToePlayerAction` to `handleAction()`.
2. `TicTacToeRuleValidator.validateMove()` checks the move against the current state.
3. If valid, `applyAction()` updates the board. `isGameOver()` checks for termination.
4. A `TicTacToeGameUpdate` with the result is returned to the session manager.
