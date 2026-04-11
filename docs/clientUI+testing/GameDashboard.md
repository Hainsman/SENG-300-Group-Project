# Game Dashboard Abstraction – High-Level Design

## Purpose

This document defines the high-level architecture and abstraction for the **Game Dashboard** UI.  
The Game Menu serves as the primary navigation surface for players to browse available games, choose a game to play, view their profile, and access leaderboards, while remaining independent of any game-specific logic or backend processing.

---

## Design Goals

- Provide a clear and simple UI for navigating available games
- Allow players to select a game to play
- Provide access to profile and leaderboard views
- Remain fully decoupled from game logic and rules
- Support incremental UI updates when menu data changes
- Follow object-oriented design principles suitable for Java

---

## High-Level Concept

The Game Menu is a **UI-level abstraction** focused purely on presentation and user navigation.  
It does **not** handle matchmaking, session creation, or game rules. Instead, it presents menu options and routes user intent to other UI surfaces or platform systems.

---

## Core Abstraction: GameMenu

The `GameMenu` abstraction represents the active menu view for a connected player.

### Responsibilities

- Initialize game menu UI on client load
- Display list of available games
- Allow player to choose a game
- Provide navigation to Profile and Leaderboards views
- Handle menu-related user actions
- Send incremental UI updates to the client

---

## Conceptual Interface (Java-oriented)

```text
GameMenu
 ├── initialize(GameMenuViewData data)
 ├── handleMenuAction(GameMenuAction action)
 ├── getMenuState()
 └── shutdown()
```

---

## Menu State

The menu maintains lightweight state describing what is currently visible to the user.

### MenuState (Conceptual)

- Available games list
- Selected game (if any)
- Navigation options (Profile, Leaderboards)

---

## Menu Actions

All user interactions with the game menu are represented as `GameMenuAction` objects.

### GameMenuAction (Conceptual)

- Player ID
- Action type (e.g., `DISPLAY_GAMES`, `CHOOSE_GAME`, `VIEW_PROFILE`, `VIEW_LEADERBOARDS`)
- Optional action payload (e.g., selected game ID)

These actions represent **UI intent only** and do not directly affect game state.

---

## UI Features

The Game Menu UI exposes the following core features:

- **Display Games**
    - Shows all available games in a list or grid
- **Choose Game**
    - Allows the player to select a game to play
- **View Profile**
    - Navigates to the Profile Dashboard UI
- **Show Leaderboards**
    - Navigates to the Leaderboards UI

---

## Menu Data Abstraction

All data rendered by the Game Menu is provided in a display-ready format.

### GameMenuViewData (Conceptual)

- List of available games (ID, name, icon)
- Enabled navigation options
- Optional UI hints or badges

The Game Menu does not transform or compute this data.

---

## Interaction Flow (High-Level)

1. Client navigates to the Game Menu
2. `GameMenu` initializes with menu view data
3. Client displays available games and navigation options
4. Player selects a menu action
5. Game Menu routes the action to the appropriate UI or platform handler
6. UI transitions to the selected destination (game, profile, or leaderboard)

---

## Non-Goals

- No game-specific logic
- No matchmaking or session management
- No rule validation
- No stat calculation
- No backend data fetching logic

---

## Summary

By isolating the Game Menu as a pure UI abstraction, the platform enables a clean and extensible navigation experience.  
This approach allows new games and UI destinations to be added without impacting game logic or backend systems, while keeping player navigation simple and consistent.
