
# Chess Implementation

## Purpose
This document describes how Chess will work with the shared game system used by the multiplayer game platform. Chess will use the Game interface to handle chess-specific stuff like the board, move rules, and detecting checkmate, while using the platform's existing networking and session management.

This implementation is owned by the **Rules and Validation sub-team**, but works with Platform Core and Client/UI.

## Design Goals
* Implement all methods required by the Game abstraction
* Handle all chess rules correctly
* Keep track of the board state
* Follow Java object-oriented design
* Keep chess-specific code separate from platform code

## High-Level Concept
Chess implements the shared Game abstraction so the platform can run chess games without knowing chess rules. The platform just sends actions to Chess and gets back updated game state.

Each Chess game has:
* Board state (piece positions, whose turn it is)
* Rule checking logic (valid moves, checkmate, etc.)
* Understanding of chess moves (from square to square)

## Core Abstraction: ChessGame
`ChessGame` implements the `Game` interface and represents one chess match.

### Responsibilities
* Set up the board at game start
* Receive and handle move actions
* Check if moves are legal before applying them
* Keep track of game state
* Know when the game is over

### Conceptual Interface
```
ChessGame implements Game
 ├── startGame()
 ├── handleAction(PlayerAction action)
 ├── validateAction(PlayerAction action)
 ├── getGameState()
 └── isGameOver()
```

## Player Actions
Chess moves are sent using the generic `PlayerAction` object.

### ChessAction Structure
* **Player ID** (white or black)
* **Action type** (MOVE)
* **Action payload** (where piece is moving from and to, promotion piece if needed)

The platform just passes these to Chess without reading the chess-specific parts.

## Rules & Validation Abstraction
Chess rules are checked using the `RuleValidator` abstraction.

### ChessRuleValidator Responsibilities
* Check if a move is legal
* Make sure players take turns correctly
* Verify pieces move according to chess rules
* Make sure king doesn't end up in check
* Prevent illegal game states

### Conceptual Structure
```
ChessRuleValidator implements RuleValidator
 └── validate(PlayerAction action, GameState state)
```

This validator handles all chess rule checking.

## Chess-Specific Implementation

### Components
* `ChessGame` (implements Game)
* `ChessRuleValidator` (implements RuleValidator)
* `ChessGameState` (implements GameState)

### Responsibilities

#### Board State
* Track piece positions on the board
* Track whose turn it is
* Remember past moves
* Track special conditions (can castle, en passant square)

#### Move Checking
* Make sure pieces move correctly for their type
* Check if other pieces are blocking the path
* Make sure king stays safe
* Handle special moves (castling, en passant, pawn promotion)

#### Game Ending
* Detect checkmate
* Detect stalemate
* Detect draw situations

## Interaction Flow
1. Client sends a chess move to the server
2. Platform sends it to the `ChessGame` instance
3. `ChessGame` asks `ChessRuleValidator` if the move is legal
4. If legal, update the board (move piece, switch turns, update special states)
5. Check if game is over
6. Send updated state back to platform to broadcast to players
