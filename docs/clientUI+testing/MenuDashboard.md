# Home Page Abstraction – High-Level Design

## Purpose

This document defines the high-level architecture and abstraction for the **Home Page** UI.  
The Home Page serves as the initial landing view for the player, providing quick access to personal records, other players’ records, settings, and game selection, while remaining independent of any game-specific logic or backend processing.

---

## Design Goals


- Allow quick access to personal and global records
- Enable navigation to game selection and settings
- Remain fully decoupled from game logic and rules
- Support incremental UI updates when displayed data changes
- Follow object-oriented design principles suitable for Java

---

## High-Level Concept

The Home Page is a **UI-level abstraction** focused purely on presentation and navigation.  
It does **not** compute records, manage persistence, or enforce rules. Instead, it displays record summaries and routes user intent to other UI surfaces.

---

## Core Abstraction: HomePage

The `HomePage` abstraction represents the active landing view for a connected player.

### Responsibilities

- Initialize home page UI on client load
- Display player record summaries
- Display other players’ record summaries
- Provide navigation to game selection
- Provide navigation to settings
- Handle home page user actions
- Send incremental UI updates to the client

---

## Conceptual Interface (Java-oriented)

```text
HomePage
 ├── initialize(HomePageViewData data)
 ├── handleHomeAction(HomePageAction action)
 ├── getHomeState()
 └── shutdown()
```

---

## Home State

The home page maintains lightweight state describing what is currently visible to the user.

### HomeState (Conceptual)

- Player record summary
- Other players’ record summaries
- Available navigation options (Choose Game, Settings)

---

## Home Actions

All user interactions with the home page are represented as `HomePageAction` objects.

### HomePageAction (Conceptual)

- Player ID
- Action type (e.g., `VIEW_RECORDS`, `VIEW_OTHER_RECORDS`, `CHOOSE_GAME`, `OPEN_SETTINGS`)
- Optional action payload (e.g., selected player ID)

These actions represent **UI intent only** and do not directly affect game state.

---

## UI Features

The Home Page UI exposes the following core features:

- **View Records**
    - Displays the current player’s records
- **View Other Players’ Records**
    - Displays record summaries for other players
- **Choose Game**
    - Navigates to the Game Menu or game selection UI
- **Settings**
    - Navigates to the Settings UI

---

## Home Page Data Abstraction

All data rendered by the Home Page is provided in a display-ready format.

### HomePageViewData (Conceptual)

- Player record summary
- Other players’ record summaries
- Enabled navigation options

The Home Page does not transform or compute this data.

---

## Interaction Flow (High-Level)

1. Client navigates to the Home Page
2. `HomePage` initializes with home page view data
3. Client displays records and navigation options
4. Player selects a home page action
5. Home Page routes the action to the appropriate UI or platform handler
6. UI transitions to the selected destination

---

## Non-Goals

- No game-specific logic
- No record calculation or aggregation
- No matchmaking or session management
- No backend data fetching logic

---

## Summary

By isolating the Home Page as a pure UI abstraction, the platform provides a clean and intuitive entry point for players.  
This design supports easy UI iteration and extension while maintaining strict separation from game logic and backend systems.
