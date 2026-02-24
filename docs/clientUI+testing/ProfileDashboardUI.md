# Profile Dashboard Abstraction – High-Level Design

## Purpose

This document defines the high-level architecture and abstraction for the **Profile Dashboard** UI.  
The Profile Dashboard provides players with a centralized view of their identity and game-related statistics, while remaining independent of any game-specific logic or backend computation.

---

## Design Goals

- Clearly display player identity information
- Present game statistics in a simple, readable format
- Remain decoupled from game logic and rules
- Support incremental UI updates when profile or stats change
- Follow object-oriented design principles suitable for Java

---

## High-Level Concept

The Profile Dashboard is a **UI-level abstraction** focused purely on presentation.  
It does **not** calculate statistics, manage persistence, or interpret game outcomes. Instead, it renders player information and statistics that are already prepared for display.

---

## Core Abstraction: ProfileDashboard

The `ProfileDashboard` abstraction represents the active profile view for a connected player.

### Responsibilities

- Initialize profile dashboard UI on client load
- Display player name and identity information
- Display aggregated game statistics
- Refresh displayed data when updates are received
- Send incremental UI updates to the client

---

## Conceptual Interface (Java-oriented)

```text
ProfileDashboard
 ├── initialize(ProfileViewData data)
 ├── refresh()
 ├── getDashboardState()
 └── shutdown()
```

---

## Dashboard State

The dashboard maintains lightweight state describing what is currently visible to the user.

### DashboardState (Conceptual)

- Player display name
- Total games played
- Total wins
- Total losses
- Per-game statistics summary??

---

## Profile Actions

User interactions with the profile dashboard are minimal and UI-focused.

### ProfileAction (Conceptual)

- Player ID
- Action type (e.g., `REFRESH_PROFILE`)

These actions do not affect game state or matchmaking.

---

## Profile Data Abstraction

All data rendered by the Profile Dashboard is provided in a display-ready format.

### ProfileViewData (Conceptual)

- Player ID
- Display name
- Aggregated statistics
- Per-game statistics list??

The Profile Dashboard does not transform or compute this data.

---

## Interaction Flow (High-Level)

1. Client navigates to the Profile Dashboard
2. `ProfileDashboard` initializes with profile view data
3. Client displays player name and statistics
4. Updated profile or statistics data is received
5. `ProfileDashboard` updates affected UI elements

---

## Non-Goals

- No game logic
- No stat calculation or aggregation
- No matchmaking or session management
- No backend data fetching logic

---

## Summary

By isolating profile display logic from game logic and data processing, the Profile Dashboard enables a clean, consistent UI for presenting player identity and game statistics.
