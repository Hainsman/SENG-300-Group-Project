```mermaid
sequenceDiagram
	UI --> Lobby: Add player
	Lobby --> Authentication: Signed in?
	Authentication --> Lobby: Yes/no.
	Lobby --> UI: Success/fail.
```
