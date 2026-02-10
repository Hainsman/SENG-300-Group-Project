# Game Abstraction – High-Level Design

## Purpose

This document defines the shared game abstraction used by the multiplayer game platform. The goal of this abstraction is to allow multiple games (e.g., Blackjack and Chess) to run on the same platform infrastructure while sharing networking, session management, and validation logic.

This abstraction is owned primarily by the **Rules and Validation** sub-team, but is designed to integrate cleanly with Platform Core and Client/UI.

---

## Design Goals

* Support multiple games without modifying platform-level code
* Enforce consistent validation and turn-handling logic
* Follow object-oriented design principles suitable for Java

---

## High-Level Concept

All games on the platform implement a shared **Game abstraction**. The platform interacts only with this abstraction and does not depend on any game-specific logic.

Each concrete game (e.g., Blackjack, Chess) provides:

* Its own game state
* Its own rule validation logic
* Its own interpretation of player actions
---

## Core Abstraction: `Game`

The `Game` abstraction represents a single running match of any game type.

### Responsibilities

* Initialize and start a game session
* Accept player actions
* Validate actions before applying them
* Maintain and expose game state
* Send just the changes(updates) instead of whole game state to the client
* Determine when the game has ended

### Conceptual Interface (Java-oriented)

```text
Game
 ├── startGame()
 ├── handleAction(PlayerAction action)
 ├── validateAction(PlayerAction action)
 ├── getGameState()
 ├── getGameUpdate()
 └── isGameOver()
```

> Note: This is a conceptual interface. Exact method signatures are intentionally omitted at this stage.

---

## Player Actions

All player inputs are represented as a **generic action object** that is interpreted by the active game.

### PlayerAction (Conceptual)

* Player ID
* Action type (e.g., MOVE, HIT, STAND)
* Action payload (game-specific data)

The format of the action payload will necessarily be quite different between games so this object will be an abstraction implemented separately for each game.

The platform does not interpret the contents of actions; it only routes them to the correct game instance.

---

## Rules & Validation Abstraction

Validation logic is separated from game flow logic using a shared **RuleValidator** abstraction.

### RuleValidator Responsibilities

* Determine whether a proposed action is legal
* Enforce turn order and game-specific constraints
* Prevent invalid state transitions
* Upon failure of validation a reason should be sent of why it failed instead of the mere boolean
* Provide a list of valid moves to the client since they should be known anyways for validating client input

### Conceptual Structure

```text
RuleValidator
 ├── validate(PlayerAction action, GameState state)
 └── getValidActions()
```

Each game provides its own implementation of this validator.

---

## Game-Specific Implementations

### Blackjack Implementation

**Components**:

* `BlackjackGame` (implements Game)
* `BlackjackPlayerAction` (contains whether to hit or stand, as well as splitting and possibly the initial bet)
* `BlackjackRuleValidator`
* `BlackjackGameState` (Deck, player and house hands, current bets)

**Responsibilities**:

* Card dealing and deck management
* Hit / Stand logic
* Win/loss determination

---

### Chess Implementation

**Components**:

* `ChessGame` (implements Game)
* `ChessPlayerAction` (contains move information, promotion, etc.)
* `ChessRuleValidator`
* `ChessGameState` (Board positions, captures, checks)

**Responsibilities**:

* Board state management
* Piece movement validation
* Check / checkmate detection

---

## Interaction Flow (High-Level)

1. Client sends an action to the server
2. Platform routes the action to the active `Game` instance
3. `Game` requests validation from its `RuleValidator`
4. If valid, the game state is updated
5. Updated state is returned to the platform for broadcasting

---
