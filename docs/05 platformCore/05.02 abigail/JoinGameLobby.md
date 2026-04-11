# Functional Intent
Allow authenticated user to enter a lobby for a specific game

# Process 
Sequence diagram:
```uml
@startuml
title Sequence - Join Game Lobby

actor Player
participant "GUI (Dashboard)" as GUI
participant "PlatformCore" as Core
participant "PartySystem" as PS
participant "EventBus" as Bus
participant "Networking (stub)" as Net

Player -> GUI : select gameType\nclick "Join Lobby"
GUI -> Core : joinLobby(playerId, gameType)

Core -> PS : getAvailableLobbies(gameType)
PS --> Core : lobbyList

alt No lobby exists
Core -> PS : createLobby(gameType)
PS --> Core : lobbyId
else Lobby exists
Core -> PS : selectLobby(lobbyId)
end

Core -> PS : addPlayerToLobby(lobbyId, playerId)

PS --> Core : lobbyUpdated

Core -> Bus : publish(LobbyUpdated)
Bus --> GUI : notifyLobbyUpdate(lobbyId)

Core -> Net : syncPlayers(lobbyId)
Net --> Core : syncConfirmed

Core --> GUI : openLobbyPage(lobbyId)

GUI -> GUI : display participants\nshow lobby chat
@enduml
```
# Overall  
**1. Player selects Join Lobby**
    1. UI - PlatformCore.joinLobby(playerId, gameId)

**2. Lobby Discovery**
   1. PlatformCore queries:
   2. PartySystem.getAvailableLobbies(gameId)
   3. If none exist: PartySystem.createLobby(gameId)

**3. Join Lobby** 
   1. PlatformCore → PartySystem.addPlayerToLobby(lobbyId, playerId)
   2. Networking:syncPlayers(lobbyId)

**4. Notify Participants**
   1. PlatformCore → EventBus.publish(LobbyUpdated)
   2. notifyPlayer(playerId, "Joined Lobby")
   3. Other players receive:
      1. LobbyUpdated event 
      2. Updated participant list

**Postconditions**

Player is registered in PartySystem as lobby member
Lobby membership is synchronized via Networking

UI displays:
1. Participant list
2. Lobby chat
3. Lobby state