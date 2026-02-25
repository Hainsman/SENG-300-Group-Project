# Platform Core — High-Level Abstraction

## What Is Platform Core?

Platform Core is the **backend infrastructure layer** of the OMG Platform. It is the connective tissue between the UI, game logic, networking, and persistence. It owns the player session lifecycle, routes events between subsystems, manages all services, and exposes APIs consumed by the UI and game engines.

The fundamental architectural rule Platform Core enforces is:

> **Game code never directly notifies players or updates the UI. It emits a `GameStateUpdate` event. PlatformCore consumes it and decides who to notify, and how — regardless of where the player currently is (lobby, dashboard, or in-game).**

---

## Component Overview

```
ClientUI
   │
ClientCore ──── TCP Network ──── ServerCore
                                      │
                    ┌─────────────────┼──────────────────┐
                    │                 │                   │
              PlatformCore         Chat              Database (stub)
                    │
       ┌────────────┼────────────────┬──────────────────┐
       │            │                │                  │
  EventBus   MatchMaking   GameSessionManager      Leaderboard
                    │                │
               PartySystem      GameEngine (interface)
                                     │
                              [Connect 4 / Tic Tac Toe impl]
```

### Component Responsibilities

| Component | Responsibility |
|---|---|
| `PlatformCore` | Central dispatcher; owns EventBus, MatchMaking, GameSessionManager, Leaderboard |
| `EventBus` | Pub/sub event routing between game logic and UI |
| `GameStateUpdate` | The key event: `sessionId`, `recipients`, `status`, `activePlayerId`, `payload` |
| `GameSessionManager` | Owns the move pipeline; emits `GameStateUpdate` on move accepted/rejected, turn change, game end |
| `MatchMaking` | Queue management by `gameType`; skill-based player matching |
| `PartySystem` | Lobby creation (`createLobby`), discovery (`getAvailableLobbies`), and membership (`addPlayerToLobby`) |
| `Leaderboard` | Rankings and stats persistence (`logGameResult`, `getTopPlayersByRank`) |
| `GameEngine` (interface) | Implemented per game: `createInitialState`, `validateMove`, `applyMove`, `evaluateEnd` |
| `Networking` (stub) | TCP connection handling, player sync, reconnection |
| `Database` (stub) | Generic persistence (`storeData`, `retrieveData`) |
| `Chat` | Per-lobby messaging; encapsulated player list for privacy |

---

## Player Session State Machine

```
LoggedOut
   │ submitCredentials
Authenticating
   │ authSuccess / authFail
LoggedIn → Browsing (Home)
              │                      │
        joinQueue (ranked)       joinLobby (unranked)
              │                      │
           InQueue               InLobby
              │ matchAssigned         │ everyone ready
           MatchFound            EnteringGame
              │ auto-join             │
           EnteringRoom ─────────────►│
                                      ▼
                                  InGame
                           ┌──────────────────────┐
                           │  WaitingForTurn       │
                           │       ↕ (via GameStateUpdate)
                           │  YourTurn             │
                           │       └──► GameEnded  │
                           └──────────────────────┘
                                      │ exitToHome
                                  Browsing
```

**Key constraints:**
- Logout is only allowed from `Browsing` (Home) — never mid-game
- Ranked matches: player is auto-joined, no option to decline
- Unranked matches: use the `Lobby` system; players wait and can chat
- `PlatformCore` can send notifications to a player regardless of their current state

---

## Key Data Structures

| Class | Purpose |
|---|---|
| `GameStateUpdate` | Event emitted by `GameSessionManager` carrying session ID, recipients, status, active player, and payload |
| `GameStatus` | Enum: `LOBBY / MATCHMAKING / STARTING / IN_PROGRESS / GAME_OVER` |
| `GameState` | Server-side snapshot: `sessionId`, `gameType`, `status`, `activePlayerId`, `board` |

---

## Integration API Surface

| Caller | Method | Target |
|---|---|---|
| UI | `joinLobby(playerId, gameType)` | PlatformCore |
| UI | `enqueue(playerId)` | PlatformCore |
| UI | `addPlayer(playerId)`, `sendMessage(msg)` | Lobby |
| PlatformCore | `matchUp(playerId)` | Rules/Validation (Matchmaking) |
| PlatformCore | `GameEngine.validateMove / applyMove` | Rules/Validation (GameEngine) |
| Rules/Validation | `filterMessage(msg)` *(optional)* | Platform Core (Chat) |

---

## Team Assignments

| Member | Area |
|---|---|
| Dylan | Chat system + Join Game flow |
| Abigail | Join Game Lobby |
| Daniel | Account creation + Login |
| Gwilym | Dashboard / game list view + broad architecture integration |

---

## Design Principles

1. **Event-driven decoupling** — `GameStateUpdate` is the contract between game logic and everything else; no direct calls from game to UI
2. **Single Responsibility** — each service owns one lane (matchmaking, sessions, lobby, leaderboard, chat)
3. **Privacy by design** — `Lobby.players` is encapsulated; external code cannot enumerate who is in a lobby
4. **Notification ownership** — `PlatformCore` decides when and how to notify players, regardless of their current UI state
5. **Stub boundaries** — `Networking` and `Database` are clearly marked stubs, enabling parallel development across teams