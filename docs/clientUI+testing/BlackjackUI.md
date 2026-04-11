# Blackjack UI Design

## Purpose

Defines the visual structure and interaction design for the Blackjack game UI.

## Overall Layout

| Region             | Purpose                |
| ------------------ | ---------------------- |
| Table Area         | Shows cards in play    |
| Player Panel       | Player hand and chips  |
| Dealer Panel       | Dealer hand            |
| Action Controls    | Hit, Stand, Split, Bet |
| Status/Result Area | Win/Loss/Push messages |

## Core UI Components

(Reminder: Components and workings are always up for tweaks and changes)
Card component
Represents playing a card
| Property | Description |
| -------- | -------------------- |
| suit | Hearts, Spades, etc. |
| rank | A, 2–10, J, Q, K |
| faceUp | Visible or hidden |

## Hand Component

Container for a set of cards
| Hand Type | Purpose |
| ----------- | ---------------------- |
| Player Hand | User’s cards |
| Dealer Hand | Dealer’s cards |
| Split Hand | Additional player hand |
Displays total value

## Betting Panel

Allows:

- Placing bet
- Adjusting chips
- Displaying balance

## Action Button Panel

| Button | Function   |
| ------ | ---------- |
| Hit    | Draw card  |
| Stand  | End turn   |
| Split  | Split pair |
| Double | Double bet |

Buttons enable/disable based on the current game state

## Result Display

Shows:

- Bust
- Blackjack
- Dealer Bust
- Win/Loss/Push

## Player Info Panel

Positioned:

- Below the player’s hand or
- On the side of the table in a stacked player list (multiplayer)

## Displayed Info

| Element                    | Description                         |
| -------------------------- | ----------------------------------- |
| **Player Name**            | Display name / ID                   |
| **Avatar/Icon (optional)** | Visual identifier                   |
| **Chip Balance**           | Total chips remaining               |
| **Current Bet**            | Chips committed this round          |
| **Hand Value**             | Current total (e.g., 18)            |
| **Status Label**           | Active, Standing, Busted, Blackjack |
| **Turn Indicator**         | Highlight or glow                   |

## Visual States

| State         | UI Effect            |
| ------------- | -------------------- |
| Player’s turn | Panel highlighted    |
| Standing      | Greyed “STAND” label |
| Busted        | Red label / icon     |
| Blackjack     | Gold highlight       |
| Waiting       | Dimmed               |

## Behavior

Updates whenever:

- Bet changes
- Card dealt
- Turn changes
- Status changes
- Animate subtle changes (chip count tick, value update; optional)\

## Component Structure

PlayerInfoPanel
├── Avatar
├── PlayerNameLabel
├── ChipBalanceDisplay
├── CurrentBetDisplay
├── HandValueDisplay
├── StatusIndicator
└── TurnHighlight

## Interaction Rules

| User Type     | Interaction                                             |
| ------------- | ------------------------------------------------------- |
| Local player  | Panel may show extra controls (bet adjust before round) |
| Other players | Display only, not interactive                           |

## Design Principles

Some things to keep in mind:

- Always readable at a glance
- No rule logic displayed (only results/state)
- Changes should be visually noticeable
- Consistent across all players

## User Interaction Flow

1. Player places bet
2. Cards dealt (animation)
3. Player chooses actions
4. Dealer plays
5. Result displayed

## Feedback and Visual Effects

| Event      | UI Effect         |
| ---------- | ----------------- |
| Card dealt | Slide animation   |
| Bust       | Hand glows red    |
| Blackjack  | Highlight + sound |
| Win        | Chips animation   |
