# Gaming Platform

This is a multiplayer gaming platform that allows users to play various games online with friends and other players. The platform provides a seamless experience for registering, finding games, and competing in real-time.

## How to Run the Project

To run the project, you need to start the server first, then launch the client.

### Running the Server
- **Using IntelliJ IDEA**: Go to Run > Edit Configurations. Add a new Application configuration. Set the Main class to `ca.ucalgary.seng300.platform.server.PlatformCore`. Run this configuration to start the server.
- **Using Maven Wrapper (Command Line)**: Execute `sh mvnw exec:java` (on Windows, use `mvnw.cmd exec:java`). This uses the configured main class from `pom.xml`.

### Running the Client
- **Using IntelliJ IDEA**: Ensure the server is running. Then, go to Run > Edit Configurations. Add or use the JavaFX configuration. Run it to launch the client UI.
- **Using Maven Wrapper (Command Line)**: Execute `sh mvnw javafx:run` (on Windows, use `mvnw.cmd javafx:run`). This starts the JavaFX application.

### Populating Sample Data
To populate the stores with sample data (users, game history, participants, sessions, player stats, and friends), run the `PopulateStores` class:
- **Using IntelliJ IDEA**: Add a new Application configuration with Main class `ca.ucalgary.seng300.tools.PopulateStores` and run it.
- **Using Maven Wrapper (Command Line)**: Execute `sh mvnw exec:java -Dexec.mainClass=ca.ucalgary.seng300.tools.PopulateStores` (on Windows, use `mvnw.cmd exec:java -Dexec.mainClass=ca.ucalgary.seng300.tools.PopulateStores`).

This will initialize the data files in the `datafiles/` directory with sample users, completed games with participants, and other related data without starting the server. Note: If the user store is empty, it will be populated upon first access via `UserStore.getInstance()`, which loads data from the file or initializes an empty store if the file doesn't exist.

## Testing

The project includes comprehensive JUnit 5 tests to ensure functionality and reliability. Tests cover key components such as persistence, server operations, client interactions, and game logic.

### Running Tests
- **Using IntelliJ IDEA**: Right-click on the `src/test/java` directory or individual test classes and select "Run Tests".
- **Using Maven Wrapper (Command Line)**: Execute `sh mvnw test` (on Windows, use `mvnw.cmd test`). This runs all tests and generates reports in `target/surefire-reports`.

### What is Currently Tested
- **Persistence Layer**: Tests for stores (e.g., `UserStore`, `GameHistoryStore`, `ParticipantStore`, `PlayerStatsStore`) to verify data loading, saving, and CRUD operations.
- **Server Functionality**: Tests for `PlatformCore` (including behaviours covered by `ServerCoreTest`), such as lobby/game requests, profile updates, and related server-side handling.
- **Client Operations**: Tests for `ClientCore` to ensure proper communication with the server.
- **Game History and Sessions**: Tests for game history persistence and session records.
- **Main Application**: Basic tests for `MainApp` to verify application startup.
- **Domain and Utilities**: Tests for domain objects and helper utilities, such as hashing and validation.

Tests use temporary files and in-memory setups where possible to avoid interfering with production data. Run `mvn test` regularly to catch regressions during development.

## Features

### User Management
- **Account Creation**: New users can easily create accounts to join the platform, with built-in email validation to ensure proper formatting.
- **Secure Login**: Users can log in securely to access their personalized dashboard.
- **Password Hashing**: Passwords are securely hashed to protect user credentials and prevent unauthorized access.
- **Password Recovery**: The UI offers a “Forgot password?” action; it directs users to contact an administrator (there is no automated email reset in this prototype).

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

### User Interface and Game Logic
The application follows a modular architecture to separate user interface concerns from core game mechanics, ensuring maintainability and extensibility.

#### User Interface (UI) Layer
- **Framework**: Built using JavaFX with FXML for declarative UI design, allowing for rich, interactive graphical interfaces.
- **Structure**: Organized in the `ui` package, with controllers (e.g., `joinLobbyController`, `hostLobbyController`) handling user interactions and communicating with the platform core via client-side logic.
- **Navigation**: Supports multiple views such as login, dashboard, lobby, game boards, and profiles, with dynamic updates for real-time features like chat and matchmaking status.
- **Benefits**: Clean separation from business logic enables easy UI customization and testing, while JavaFX ensures cross-platform compatibility.

#### Game Logic Layer
- **Abstraction**: Games implement a common `Game` interface, promoting polymorphism and easy addition of new games (e.g., Tic-Tac-Toe in `game.core.tictactoe`, Connect 4 in `game.core.connect4`).
- **Validation**: Rule validation is handled by `RuleValidator` implementations, ensuring game rules are enforced consistently and independently of UI.
- **Actions**: Player actions are modeled via `PlayerAction` classes, allowing for extensible move handling and state transitions.
- **Integration**: The platform core (`PlatformCore` on the server, session types such as `TicTacToeSession` / `Connect4Session`, and `ClientCore` on the client) coordinates lobbies, moves, and state updates between the UI and game logic. (Early design docs used the name `GameSessionManager`; that role is implemented across these types rather than as a single class.)
- **Benefits**: Modular design allows for game-specific logic to be developed and tested in isolation, facilitating rapid prototyping and maintenance.

This structure adheres to the overall domain-driven architecture, with clear boundaries between UI presentation, business rules, and data persistence.

## Design Changes and Architecture

### Deviations from Original UML Diagrams

The initial UML diagrams in the `docs/` folder outlined a high-level architecture with stubs for networking and database components. However, the actual implementation introduced several key deviations to support a fully functional multiplayer platform:

- **Server Implementation**: While the original diagrams included placeholders for `ServerCore` and `ClientCore` with TCP communication, the full server-side logic (e.g., `PlatformCore` as the main server class) was not detailed. The application now features a robust server that handles client connections, request routing, and maintains the authoritative state for all game sessions, lobbies, and user data. This shift from a stubbed network layer to a complete server-client architecture enables real-time multiplayer interactions that were not fully specified in the early diagrams.

- **Database System**: The original persistence model relied on simple stubs like `CredentialsDatabase` using in-memory HashMaps or basic file I/O. The implemented system uses a file-based JSON storage with domain-driven design principles, providing thread-safe operations, data integrity, and scalability beyond the initial stubs.

- **Additional Components**: Components such as detailed matchmaking services, chat systems, and session management were expanded beyond the basic outlines in the diagrams, incorporating features like lobby creation, queuing, and real-time updates. Additionally, user ranks are determined by a game-agnostic score system, allowing consistent ranking across different games. Session management ensures only one active session per user to prevent hijacking and maintain security.

- **Game Implementation**: While the original diagrams outlined an abstract `Game` interface, the implementation includes fully functional games (Tic-Tac-Toe and Connect 4) with concrete rule validators and action models. Each game implements `RuleValidator` to enforce game-specific rules independently of the UI, and uses `PlayerAction` classes to model moves and state transitions. This design allows for easy extension with new games and ensures consistent game logic validation across all titles.

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

For a detailed **as-built vs Iteration 1** comparison, see [`summary.md`](summary.md) in the repository root (source for the Iteration 3 **`summary.pdf`** submission). Historical UML and sequence diagrams under `docs/` remain useful but do not always match class names in the final code.

This platform is designed for fun, social gaming, bringing players together in an engaging and secure environment.