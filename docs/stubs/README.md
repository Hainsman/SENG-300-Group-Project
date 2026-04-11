# Sprint 1 Stubs — Overview & Test Guide

**Branch:** `QA-01/stubs-and-network-tests`
**Sub-Team:** Quality, Testing & Integration (QA-01)
**Sprint:** 1 — Foundation & Core Abstractions

---

## Why These Stubs Exist

The Sprint 1 plan (see `docs/Iteration 1 Submission/Project Plan.md`) assigns `STUBS-01` with building the `NETWORK` stub and `CredentialsDatabase` stub. These are boundary classes that other sub-teams depend on but that do not require a real database or TCP socket to test against.

Because those stubs were not yet available, they have been implemented here so that QA-01's unit tests can be written and reviewed. The goal is for these implementations to be swapped out for the real versions when `STUBS-01` delivers theirs — the test suite should pass unchanged against either.

---

## What Was Added

```
src/main/java/ca/ucalgary/seng300/
├── model/
│   ├── Player.java              — Player domain model (username, email, password, rank)
│   └── Rank.java                — Rank enum (BRONZE → GRANDMASTER)
└── platform/
    └── stubs/
        ├── CredentialsDatabase.java   — In-memory credentials store
        └── Network.java               — Simulated TCP channel pair

src/test/java/ca/ucalgary/seng300/
└── platform/
    └── stubs/
        ├── CredentialsDatabaseTest.java   — 12 unit tests
        └── NetworkTest.java               — 10 unit tests
```

---

## Stubs

### `Network`

**Package:** `ca.ucalgary.seng300.platform.stubs`

Simulates the TCP boundary between `ClientCore` and `ServerCore`. In the real architecture, messages cross a TCP socket; here they cross a pair of in-memory `LinkedBlockingQueue`s. The queues are cross-wired so that:

- `clientSide.send(msg)` → appears in `serverSide.receive()`
- `serverSide.send(msg)` → appears in `clientSide.receive()`

This faithfully models the one-way nature of each TCP stream (a sender's outbound does not loop back to itself) and the FIFO ordering guarantee of TCP.

**API:**

```java
// Create a connected pair — pair[0] is the client side, pair[1] is the server side.
Network[] pair = Network.createConnectedPair();

pair[0].send("login request");       // client sends to server
String msg = pair[1].receive();      // server receives "login request"

pair[1].send("login ok");            // server sends to client
String reply = pair[0].receive();    // client receives "login ok"
```

`receive()` is non-blocking and returns `null` when no message is pending. `send(null)` is silently ignored (null messages are not queued).

**Swap-in point:** Replace `LinkedBlockingQueue` queues with a real `java.net.Socket` pair to upgrade to actual TCP.

---

### `CredentialsDatabase`

**Package:** `ca.ucalgary.seng300.platform.stubs`

In-memory credential store backed by a `HashMap<String, Player>`. Provides the full interface specified in the architecture diagram (`docs/02 structure/02.02 - Player Profiles Stats Persistence.md`). Data can optionally be persisted to a JSON file via `saveDatabase`/`loadDatabase`.

**API:**

```java
CredentialsDatabase db = new CredentialsDatabase();

// Register a new player
db.addNewPlayer("alice", "alice@example.com", "password", Rank.BRONZE); // true

// Check if a username is taken
db.usernameLookup("alice");  // true
db.usernameLookup("ghost");  // false

// Retrieve a player
Player p = db.findPlayerByUsername("alice");

// Delete a player
db.deleteExistingPlayer("alice"); // true

// Persist and restore
db.saveDatabase("credentials.json");
db.loadDatabase("credentials.json");
```

`addNewPlayer` returns `false` if the username is null or already taken, leaving the existing record unchanged. `deleteExistingPlayer` returns `false` if the username does not exist.

**Swap-in point:** Replace `HashMap` with a real database call in `addNewPlayer`, `deleteExistingPlayer`, `findPlayerByUsername`, and `usernameLookup`.

---

### `Player` and `Rank`

**Package:** `ca.ucalgary.seng300.model`

Minimal stubs for the player domain model, matching the architecture spec in `docs/02 structure/02.02`. These are provided here only because `MODEL-01` has not yet been merged. Once `MODEL-01/player-domain` lands, these should be replaced with that branch's implementations.

```java
Player p = new Player("alice", "alice@example.com", "password", Rank.GOLD);
p.getUsername();  // "alice"
p.getRanking();   // Rank.GOLD
```

`Rank` values in order: `BRONZE`, `SILVER`, `GOLD`, `PLATINUM`, `DIAMOND`, `MASTER`, `GRANDMASTER`.

---

## Tests

### Running the Tests

**IntelliJ:** Right-click either test class → Run, or run the whole module via the Maven tool window → `test`.

**Terminal:**
```
./mvnw test
# or on Windows:
.\mvnw.cmd test
```

---

### `NetworkTest` — 10 tests

| Test | What it verifies |
|---|---|
| `clientSend_messageIsReceivedByServer` | Basic client → server delivery |
| `serverSend_messageIsReceivedByClient` | Basic server → client delivery |
| `receive_withNoMessagesPending_returnsNull` | Non-blocking receive returns null |
| `send_nullMessage_isNotQueued` | Null is silently dropped |
| `send_emptyString_isDelivered` | Empty string is a valid message |
| `multipleMessages_areReceivedInFIFOOrder` | Queue preserves ordering |
| `bidirectional_bothSidesCanSendAndReceiveSimultaneously` | Full duplex works |
| `clientMessages_doNotAppearInClientReceive` | No loopback on client side |
| `serverMessages_doNotAppearInServerReceive` | No loopback on server side |
| `createConnectedPair_twoPairs_areIsolatedFromEachOther` | Independent pairs don't share queues |

---

### `CredentialsDatabaseTest` — 12 tests

| Test | What it verifies |
|---|---|
| `addNewPlayer_withValidData_returnsTrue` | Successful registration returns true |
| `addNewPlayer_withValidData_playerIsStored` | Player retrievable after add |
| `addNewPlayer_withDuplicateUsername_returnsFalse` | Duplicate rejected |
| `addNewPlayer_withDuplicateUsername_doesNotOverwriteExistingPlayer` | Original record preserved |
| `addNewPlayer_withNullUsername_returnsFalse` | Null username rejected |
| `usernameLookup_forExistingUsername_returnsTrue` | Known username found |
| `usernameLookup_forNonExistentUsername_returnsFalse` | Unknown username not found |
| `findPlayerByUsername_forExistingPlayer_returnsCorrectPlayer` | Correct fields on returned Player |
| `findPlayerByUsername_forNonExistentPlayer_returnsNull` | Missing username returns null |
| `deleteExistingPlayer_forExistingPlayer_returnsTrueAndIsRemoved` | Delete succeeds and removes |
| `deleteExistingPlayer_forExistingPlayer_usernameLookupReturnsFalse` | Lookup reflects deletion |
| `deleteExistingPlayer_forNonExistentPlayer_returnsFalse` | Delete on unknown returns false |
| `saveDatabase_andLoadDatabase_restoresAllPlayers` | Roundtrip preserves all records |
| `saveDatabase_andLoadDatabase_preservesPlayerDetails` | Roundtrip preserves field values |
| `loadDatabase_withNonExistentFile_leavesDbEmpty` | Missing file results in empty DB |

---

## Merge Plan

This branch should be reviewed and merged into `QA-01/sprint1-tests`. Once `STUBS-01/network-db-stubs` and `MODEL-01/player-domain` are merged into `develop`, the stub classes added here (`Network`, `CredentialsDatabase`, `Player`, `Rank`) should be removed and the test imports updated to point to the canonical implementations.
