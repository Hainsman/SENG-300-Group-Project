# UI Mockup Overview
## SENG300 | Online Multiplayer Game Platform — Project Iteration 1

This document provides a brief overview of the UI mockups designed for the platform. Screens are organized by functional area.

---

## Screen 1 — Login

**File:** `LoginUIMockUp.PNG`
![LoginUIMockUp.PNG](All%20Images/UI%20Mockups/LoginUIMockUp.PNG)

A clean, minimal login form on a light blue background. Contains username and password fields with inline icons, a "Remember Me" checkbox, a **Submit** button, a **Forgot Password** link, and a **Create New Account** button for new users. Directly maps to UC-03 (Log In), UC-02 (Create Account), and UC-09 (Recover Password).

---

## Screen 2 — Main Dashboard (Home Page)

**File:** `menu_dashboard.png`
![menu_dashboard.png](All%20Images/UI%20Mockups/menu_dashboard.png)

The authenticated landing page. Displays:
- **Your Record Summary** card — total games, wins, losses, win rate, and current rank
- **Top Players** leaderboard snippet with win rates
- Three navigation tiles: **Choose Game**, **View Records**, **Settings**
- Top bar shows username, rank badge, and a settings shortcut

Maps to UC-04 (View Dashboard) and `HomePage` from the UI class diagram.

---

## Screen 3 — Game Menu / Game Select

**File:** `game_dashboard_ui_mockup.png`
![game dashboard ui mockup.png](All%20Images/UI%20Mockups/game%20dashboard%20ui%20mockup.png)

A left-panel navigation layout with the platform logo (**Bogo Solutions**), **Games** and **Profile** links, and bottom-anchored **Settings**, **Info**, and **Exit** actions. The main area shows a **Multiplayer Games** header and game cards (Checkers, Connect 4). A right panel shows the current player's username and a **Friends** list. Maps to UC-01 (View Game List) and `GameMenu` from the UI class diagram.

---

## Screen 4 — Connect Four In-Game

**File:** `connect4_ui_in_game.png`
![connect4 ui in game.png](All%20Images/UI%20Mockups/connect4%20ui%20in%20game.png)

Dark navy game view with three zones:
- **Left panel** — game name, live ELO stats (Win: +20, Lose: -18, Draw: 0), and action buttons (Forfeit, Settings, Info, Exit)
- **Centre** — 6×7 Connect Four board with coloured tokens (yellow / red); "Your turn" status header
- **Right panel** — scrollable **History** log showing move coordinates with colour-coded tokens
- **Bottom** — two player name badges each showing their token colour

Maps to UC-GS-02 (View Game Board), UC-GS-03 (Submit Move), and UC-GS-04 (Receive Turn Notification).

---

## Screen 5 — Tic-Tac-Toe In-Game

**File:** `TicTacToeUIMockUp.png`
![TicTacToeUIMockUp.png](All%20Images/UI%20Mockups/TicTacToeUIMockUp.png)

Green-themed game view matching the Connect Four layout pattern:
- **Left panel** — game name, ELO stats (Win: +10, Lose: -6, Draw: 0), Forfeit / Settings / Info / Exit actions
- **Centre** — 3×3 grid with X and O symbols; "Waiting For Turn" status header
- **Right panel** — **History** log showing move coordinates with player symbol labels (X/O)
- **Bottom** — two player name badges with their assigned symbol

Maps to the same game session use cases as Connect Four; consistent platform UI conventions across both games.

---

## Screen 6 — Settings (Multi-Tab)

The Settings screen uses a persistent left sidebar with 7 tabs. All tabs share the same dark theme layout.

| Tab | File | Key Content |
|---|---|---|
| **Profile** | `profile_settings.png` | Avatar upload, username field, display title dropdown, bio text area |
| **Security** | `security-pw_settings.png` | Change password, change email, enable 2FA, active session management with revoke |
| **Gameplay** | `game_settings.png` | Default control scheme, in-game toggles (tutorials, spectator mode, performance stats), language, colorblind mode |
| **Matchmaking** | `matchmaking_settings.png` | Preferred server region, default game mode (Casual / Ranked / Private Match), fill party toggle, private match defaults (visibility, max players) |
| **Social** | `social-friend_settings.png` | Who can send friend requests / invite to games, online status visibility, leaderboard visibility, rank badge display, direct messages toggle |
| **Notifications** | `notification_settings.png` | Match & game alerts (match found, your turn, results, tournaments), social alerts (friend online, friend requests, game invites, messages) |
| **Privacy** | `privacy_settings.png` | Profile visibility (Public / Friends / Private), match history visibility, show online status, show current activity |
| **Competitive** | `leaderboard_settings.png` | Current season rank card (Diamond III, MMR, wins, win rate), rank visibility toggles, statistics sharing toggles |

Maps to the `Settings` class (Diagram 02) and UC-PS-04 through UC-PS-07.
