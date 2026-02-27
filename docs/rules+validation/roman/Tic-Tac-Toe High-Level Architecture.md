# Tic Tac Toe Architecture

The purpose of this document is to give an overview of how the game Tic Tac Toe was designed, as well as how it will be implemented.

## Rules

Tic-tac-toe is a two-player game played on a 3x3 grid. Players are assigned either X or O and take turns placing their symbol in an empty square of the grid. The first to line up three symbols (in a row, column or diagonally), wins. If the grid is filled without a winner, the game comes to a draw.

## Implementation overview

The system was designed as a modular, object-oriented game engine. `Game` controls the flow, while `GameState` stores the board data and current status, and `RuleValidator` enforces the rules of the game. 

- TicTacToeGame: implements the interface `Game` and works as the game engine. It holds the current game state and delegates validation to the `RuleValidator`. 

