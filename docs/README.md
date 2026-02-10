# Starter Documentation Files #

## Design Diagrams Overview ##

The design diagrams in this directory are provided as starter references to help you reason about the system architecture and behavior. They are not guaranteed to be complete or optimal. You are expected to critically review, critique, and adapt these diagrams as part of your design work. If you change or replace any diagram, you should be able to explain why your version better represents the system.

### State Diagrams ###

State diagrams describe how the system or a major component transitions between states over time in response to events. They are especially useful for reasoning about game flow, turn handling, and session lifecycle (for example, waiting for players, active play, and game completion). The provided state diagrams focus on what states exist and when transitions occur, not on low-level implementation details. You may simplify or expand these diagrams as long as the overall behavior remains clear and consistent.

### Structure (Class/Architecture) Diagrams ###

Structure diagrams show the static organization of the system: key components, their responsibilities, and how they relate to one another. These diagrams emphasize separation of concerns, such as the distinction between platform services, game logic, persistence, and the GUI. The provided diagrams are intentionally high-level and may omit fields or methods. You are free to rename, split, or merge components as needed, provided your design still respects the intended architectural boundaries.

### Use Case Diagrams ###

Use case diagrams illustrate how a player interacts with the system through the GUI. They focus on user-visible actions and navigation, not internal algorithms. The provided use cases are simplified and represent typical or important flows rather than every possible edge case. You should treat them as examples of how to model user interaction and extend or refine them to match your final design.

### Sequence Diagrams ###

Sequence diagrams describe how components interact over time to complete a specific task, such as logging in, joining a game, making a move, or ending a game. They highlight the order of messages between the GUI, platform services, and game logic. These diagrams are particularly useful for validating responsibilities and ensuring that state changes flow through the correct components. The provided sequences are representative, not exhaustive.