# Gaming Platform

This is a multiplayer gaming platform that allows users to play various games online with friends and other players. The platform provides a seamless experience for registering, finding games, and competing in real-time.

## Features

### User Management
- **Account Creation**: New users can easily create accounts to join the platform.
- **Secure Login**: Users can log in securely to access their personalized dashboard.
- **Password Recovery**: Forgot your password? No problem – recover it via email.

### Game Discovery
- **Browse Games**: View a list of available games, including descriptions and player requirements.
- **Game Dashboards**: Get detailed information about specific games, including stats and leaderboards.

### Lobby System
- **Join Lobbies**: Enter public or private lobbies to coordinate with other players before starting a game.
- **Create Lobbies**: Host your own lobbies, set privacy options, and invite friends.
- **Real-Time Chat**: Communicate with lobby participants through integrated chat functionality.

### Matchmaking
- **Queue for Games**: Join queues to be automatically matched with other players for quick gameplay.
- **Game Readiness Notifications**: Get notified when a game is ready to start.

### Gameplay
- **Multiple Games Supported**: Enjoy classics like Tic-Tac-Toe and Connect 4.
- **Real-Time Sessions**: Engage in live games with smooth, interactive experiences.

### Profiles and Competition
- **Player Profiles**: View and manage your personal profile, including game history and achievements.
- **Statistics Tracking**: Keep track of wins, losses, and other performance metrics.
- **Leaderboards**: Compete for top spots on global or game-specific leaderboards.

### Additional Features
- **Settings**: Customize your experience with user-friendly settings options.
- **Notifications**: Stay updated with badges, hints, and important alerts.

## Design Changes and Architecture

### Deviations from Original UML Diagrams

The initial UML diagrams in the `docs/` folder outlined a high-level architecture with stubs for networking and database components. However, the actual implementation introduced several key deviations to support a fully functional multiplayer platform:

- **Server Implementation**: While the original diagrams included placeholders for `ServerCore` and `ClientCore` with TCP communication, the full server-side logic (e.g., `PlatformCore` as the main server class) was not detailed. The application now features a robust server that handles client connections, request routing, and maintains the authoritative state for all game sessions, lobbies, and user data. This shift from a stubbed network layer to a complete server-client architecture enables real-time multiplayer interactions that were not fully specified in the early diagrams.

- **Database System**: The original persistence model relied on simple stubs like `CredentialsDatabase` using in-memory HashMaps or basic file I/O. The implemented system uses a file-based JSON storage with domain-driven design principles, providing thread-safe operations, data integrity, and scalability beyond the initial stubs.

- **Additional Components**: Components such as detailed matchmaking services, chat systems, and session management were expanded beyond the basic outlines in the diagrams, incorporating features like lobby creation, queuing, and real-time updates.

### Database System and Domain-Driven Architecture

The application employs a custom file-based database system using JSON serialization for persistence. Key aspects include:

- **Domain-Driven Design (DDD)**: Data models are organized in the `domain` package (e.g., `User`, `GameHistory`, `PlayerStats`), ensuring that business logic is closely aligned with data structures. This approach promotes maintainability and clear separation of concerns, with stores in the `store` package handling CRUD operations.

- **Java Reflections Usage**: The base `Store` class leverages Java reflections to dynamically resolve generic types at runtime (e.g., determining the record class for `UserStore` or `GameHistoryStore`). This enables polymorphic storage without hardcoding types, facilitating easy extension for new domain entities. Combined with Gson's type adapters, it supports complex object hierarchies and backward-compatible data loading.

- **Benefits**: 
  - **Thread Safety**: Read-write locks ensure concurrent access without data corruption.
  - **Persistence**: Data survives application restarts via JSON files in the `datafiles/` directory.
  - **Normalization**: Game history and participant data are normalized to minimize redundancy and optimize space usage, storing shared information efficiently without duplication.
  - **Flexibility**: Easy to add new stores or modify schemas without external dependencies.
  - **Performance**: In-memory caching with lazy loading from files balances speed and reliability.

### Server Request-Response System

The server (`PlatformCore`) acts as the central authority, separating database operations from client-side logic:

- **Request Handling**: Clients send structured requests (e.g., login, join lobby, make move) over TCP. The server parses, validates, and processes these, interacting with stores and services.

- **Response System**: The server responds with updated data or status, ensuring clients receive consistent, authoritative information.

- **Benefits**:
  - **Source of Truth**: All data mutations occur server-side, preventing inconsistencies from direct client access. This enforces security and data integrity.
  - **Separation of Concerns**: Clients focus on UI and user input, while the server manages business logic, persistence, and state.
  - **Scalability and Security**: Centralized control allows for easier auditing, rate limiting, and future expansions (e.g., multiple servers or cloud deployment).
  - **Real-Time Capabilities**: Enables features like live chat, matchmaking notifications, and synchronized game states across clients.

For a detailed list of design changes, see the project documentation in `docs/`. (Note: A PDF summary of changes is not available; refer to the UML diagrams and code comments for specifics.)

## UI and Game Logic Structure

The application follows a modular architecture to separate user interface concerns from core game mechanics, ensuring maintainability and extensibility.

### User Interface (UI) Layer
- **Framework**: Built using JavaFX with FXML for declarative UI design, allowing for rich, interactive graphical interfaces.
- **Structure**: Organized in the `ui` package, with controllers (e.g., `joinLobbyController`, `hostLobbyController`) handling user interactions and communicating with the platform core via client-side logic.
- **Navigation**: Supports multiple views such as login, dashboard, lobby, game boards, and profiles, with dynamic updates for real-time features like chat and matchmaking status.
- **Benefits**: Clean separation from business logic enables easy UI customization and testing, while JavaFX ensures cross-platform compatibility.

### Game Logic Layer
- **Abstraction**: Games implement a common `Game` interface, promoting polymorphism and easy addition of new games (e.g., Tic-Tac-Toe in `game.core.tictactoe`, Connect 4 in `game.core.connect4`).
- **Validation**: Rule validation is handled by `RuleValidator` implementations, ensuring game rules are enforced consistently and independently of UI.
- **Actions**: Player actions are modeled via `PlayerAction` classes, allowing for extensible move handling and state transitions.
- **Integration**: The platform core manages game sessions through `GameSessionManager`, coordinating between UI, server, and game logic for seamless multiplayer experiences.
- **Benefits**: Modular design allows for game-specific logic to be developed and tested in isolation, facilitating rapid prototyping and maintenance.

This structure adheres to the overall domain-driven architecture, with clear boundaries between UI presentation, business rules, and data persistence.

This platform is designed for fun, social gaming, bringing players together in an engaging and secure environment.