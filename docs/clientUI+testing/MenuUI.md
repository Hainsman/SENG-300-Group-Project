# Main Menu Abstraction – High-Level Design

## Purpose

This document defines the high-level architecture and abstraction for the **Main Menu** UI  
The Main Menu acts as the primary entry point into the platform and is responsible for routing players into game sessions, matchmaking flows, and social features while remaining independent of any specific game logic.

---

## Design Goals

- Provide a single, consistent entry experience for all games
- Decouple menu flow from game-specific logic
- Support adding new games without modifying menu core logic
- Support live updates (available games, player status)
- Follow object-oriented design principles suitable for Java

---

## High-Level Concept

The Main Menu is a **platform-level abstraction** that does navigation, matchmaking initiation, and session creation.  
It does **not** contain any game rules or game state. Instead, it is a menu to join games from.

---

## Core Abstraction: MainMenu

The `MainMenu` abstraction represents the active menu experience for a connected player.

### Responsibilities

- Initialize menu state on client connect
- Expose available games and modes
- Handle user menu actions
- Coordinate matchmaking and lobby creation
- Route the player into an active game session
- Send incremental menu updates to the client

---

## Conceptual Interface (Java-oriented)

```text
MainMenu
 ├── initialize(PlayerContext context)
 ├── handleMenuAction(MenuAction action)
 ├── getMenuState()
 ├── getMenuUpdate()
 └── shutdown()
```
## Menu State

The menu maintains lightweight state describing what the user can see and do.

### MenuState (Conceptual)

- Player profile summary
- Available games list
- Connection and matchmaking status??

---

## Menu Actions

All user interactions with the menu are represented as `MenuAction` objects.

### MenuAction (Conceptual)

- Player ID
- Action type (e.g., `SELECT_GAME`, `START_MATCHMAKING`)

The platform interprets menu actions but does not interpret game-specific rules.

---

## Game Registration Abstraction

Each game integrates with the platform by registering menu-visible metadata.

### GameDescriptor (Conceptual)

- Game ID
- Display name
- Player count constraints

The Main Menu uses this abstraction to dynamically render available options.

## Interaction Flow (High-Level)

1. Client connects to the platform
2. `MainMenu` initializes and fetches menu state
3. Client displays available games and options
4. Player selects a menu action
5. `MainMenu` validates and routes the action
6. Platform services perform matchmaking or lobby creation
7. `SessionManager` creates a game session
8. Player is transitioned from Main Menu to active `Game` instance

---

## Non-Goals

- No game-specific logic
- No game state persistence
- No rule validation

---

## Summary
 
By isolating menu logic from game logic, the platform enables rapid addition of new games, and consistent player experience.

