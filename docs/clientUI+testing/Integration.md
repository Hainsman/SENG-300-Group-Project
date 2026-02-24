# Client-Game Integration Abstraction High-Level Design

## Purpose

This document defines the shared integration abstraction between the Client/UI layer and the platform’s Game abstraction.

The goal is to ensure that any game (e.g., Blackjack, Chess) can communicate with the client through a consistent structure without the client depending on game-specific logic.

## Primary Goals

- Client UI works correctly with the backend and game logic
- Game rules and enforced correctly
- Decouple UI from game-specific implementations
- Standardize how game state is delivered to the client
- Standardize how player actions are sent to the server
- Support incremental updates
- Provide integration hooks useful for testing and debugging

## High-Level Concept

The integration layer acts as a translation boundary:
| Server Side | Integration Layer | Client Side |
| ------------------ | -------------------------- | -------------------- |
| `Game` abstraction | **Game Integration Model** | UI rendering & input |

The client never sees ChessGame, BlackjackGame, or any rule validators.
It only interacts with integration objects.

## Core Integration Abstraction: GameSession

Represents the data contract between the server and client.

## Responsibilities

- Carry a UI-safe representation of game state
- Hide internal rule and validation structures
- Provide enough information for rendering and interaction

## Conceptual Structure

GameSession
├── sessionId
├── players
├── currentTurnPlayerId
├── gamePhase
├── gameStatePayload
└── validActions

## State Payload Abstraction

Different games have different internal state models, so integration uses a generic payload container.

GameStatePayload
├── elementList
├── metadata
└── statusFlags

Examples:
| Game | elementList Contains |
| --------- | --------------------- |
| Chess | Pieces with positions |
| Blackjack | Cards, hands, bets |

## Action Integration Abstraction: ActionRequest

Represents a player action traveling from client to server.

## Responsibilities

- Encapsulate user input in a standard format
- Be interpretable by any Game implementation
- Allow logging and testing of actions

## Conceptual Structure

ActionRequest
├── sessionId
├── playerId
├── actionType
└── actionPayload

## Update Integration: GameUpdate

Used for incremental synchronization.

## Responsibilities

- Send only changes instead of full state
- Reduce bandwidth
- Allow efficient UI refresh
  GameUpdate
  ├── changedElements
  ├── removedElements
  ├── newElements
  ├── turnChange
  └── gameOverInfo

## Validation Feedback Integration

Instead of returning only success/failure, integration supports structured feedback.
ValidationResult
├── success
├── message
└── updatedValidActions

This supports:

- UI error display
- Testing invalid inputs
- Synchronizing valid action lists

## Integration Responsibilities by Layer

| Layer             | Responsibility                        |
| ----------------- | ------------------------------------- |
| Client UI         | Display, input capture                |
| Integration Layer | Data formatting, transport structures |
| Game Abstraction  | Rules, validation, state authority    |

## Interaction Flow (Integration View)

1. Client sends ActionRequest
2. Platform routes to Game
3. Game processes action
4. Server returns:

- GameUpdate
- Updated validActions
- (Optional) ValidationResult

5. Client updates UI
