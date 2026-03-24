# Data Base Info
## Table of Contents
    Objects
    Store for each object
    Helper Methods
    Files For your convenience

## Objects
The current objects are used by the database (all children of Record (not to be confused with java record class type))
### 
    User
    GameHistory
    Participant
    PlayerStats
    Session History

### What each object does and how to create them

#### Record (Abstract class)
The objects listed above are extensions of record 

##### Functions (available in all other objects)
```
public Record(int id)
public int getID()
public long getTimeCreated()
public void setID(int id)
public static String getTimeCreatedReadableFormat(Long timeInMilLis)

```
#### User
User object contains information regarding users and there attributes: unique Id, login, password, email, isActive (set to true by default), timeCreated (miliseconds since epoch)
```
User paul =  new User(4, "Paul", "Paul.C@ucalgary.ca", Helper.hashPassword("jdshakjdbskj", Constants.HASH_ALGORITHM));
```
##### Functions
```
public User(int id, String login, String email, String passwordHash)
public String getlogin()
public String getEmail()
public String getPasswordHash()
public boolean getIsActive()
public void setlogin(String login)
public void setEmail(String email)
public void setPasswordHash(String passwordHash)
public void setIsActive(boolean isActive)
```
#### GameHistory
Game History object contains information about a specific game event that occured it contains: unique Id, gameId, timeCreated
```
GameHistory gameHistory1 = new GameHistory(4, Game.TICK_TACK_TOE);
```

##### Functions
```
public GameHistory(int id, int gameID)
public int getGameID()
public void setGameID(int gameID)

```
#### Participants 
    Participant object is really an extension of game history but in normalized form it contains: unique id, userId 
    (who particpated in a game), score (how much they scored), gameHistoryId (unique id each gameHistory has), timecreated
```
Participant participant = new Participant(11, 11, 3, 2);
```
##### Functions
```
public Participant(int id, int gameHistoryID, int userID, int score)
public int getGameHistoryID()
public int getUserID()
public int getScore()
public void setGameHistoryID(int gameHistoryID)
public void setUser(int userID)
public void setScore(int score)
```
#### PlayerStats
    PlayerStats contains information about a users rank (notice we determine rank off of score) it contains:
    unique Id, gameId, userId, counter (number of games played), score (cumulative score for a given game), timeCreated
```
PlayerStats playerFour = new PlayerStats(4, Game.TICK_TACK_TOE, 4);
```
##### Functions
```
public PlayerStats(int id, int gameID, int userID)
public int getGameID()
public int getUserID()
public int getGameCounter()
public void updateStats(double score) //Updates game counter too

```

#### SessionHistory
contains a unique UUID for each session that is created it contains: unique id, UUID (unique UUID)
```
SessionHistory sessionHistory = new SessionHistory(2);
```
##### Functions
```
public SessionHistory(int id)
public UUID getGloballyUniqueSessionIdentifier()
```
### Each of the object above has its own associated store 
### (IMPORTANT!: (please note that reading to a file is done when the constructor is called and is writing is done by the Create, update, delete functions hence you do not have to worry about them)

#### UserStore 
##### How to create an instance
```
UserStore userStorage = UserStore.getInstance();
```
##### Functions
    get(int id) returns user that matches that id
    create(User user) creates user and stores in it a hashmap, writes the user to file
    delete(User user) deletes user from the hashmap, rewrites the file to update info
    update(User user) updates user according to their id (any of the attributes that make up user)
    findByUserName(String username) returns list of users with the same username
    findbyUserEmail(String email) returns a list of users according to their email
#### GameHistoryStore
##### how to create an instance
```
GameHistoryStore gameHistoryStore = GameHistoryStore.getInstance();
```
###### Functions
    get(int id) returns gameHistory that matches that id
    create(GameHistory gameHistory) creates gameHistory Instance and stores it  in a hashmap, writes GameHistory to a file
    delete(GameHistory gameHistory) deletes gameHistory from the hashmap, rewrites the file to update info
    update(GameHistory gameHistory) updates gameHistory according to their id (any of the attributes that make up gameHistory)
    findByGameID(int id) returns a list of game histories with an associated game Id (tick tack toe or Connect Four)

#### Participants
##### how to create an instance
```
ParticipantStore participantStore = ParticipantStore.getInstance();
```
##### Functions
    get(int id) returns Participant that matches that id
    create(Participant participant) creates Participant Instance and stores it in a hashmap, writes participant to a file
    delete(Participant participant) deletes Participant from the hashmap, rewrites the file to update info
    update(Participant participant) updates Participant according to their id (any of the attributes that make up Participant)
    findParticipationInGameEvents(int id) returns a list of all games a user has ever played 

#### PlayerStats
##### how to create an instance
```
PlayerStatsStore playerStatsStore = PlayerStatsStore.getInstance();
```
###### Functions
    get(int id) returns playerStats that matches that id
    create(PlayerStats playerStats) creates PlayerStats Instance and stores it in a hashmap, writes PlayerStats to a file
    delete(PlayerStats playerStats) deletes PlayerStats from the hashmap, rewrites the file to update info
    update(PlayerStats playerStats) updates PlayerStats according to their id (any of the attributes that make up PlayerStats)
    findPlayersByGameID(int id) returns all players that have played a given game (GameId)
#### SessionHistoryStore
##### how to create an instance
```
SessionHistoryStore sessionHistoryStore = SessionHistoryStore.getInstance();
```
##### Functions
    get(int id) returns a UUID session that matches that id
    create(SessionHistory sessionHistory) creates sessionHistory Instance and stores it in a hashmap, writes sessionHistory to a file
    delete(SessionHistory sessionHistory) deletes sessionHistory from the hashmap, rewrites the file to update info
    update(SessionHistory sessionHistory) updates sessionHistory according to their id (any of the attributes that make up sessionHistory)
    findSessionByUUID(UUID sessionIdent) returns a session if it exists in the database

### Helper Functions (in Utils.Helper)
```
public static String hashPassword(String password, String algorithm)
public static boolean isEmailValid(String email, String emailRegex)
public static Rank getRank(int totalRoundsPlayed, double score)
```

### Files for your convenience
    Rank contains info for how a rank is determined
    Game contains game id's (1 for tick tac toe) and (2 for connect four)
    Constants contain Email regular expression and the Hashing algorithm string

### Additional Info
    If you are confused on how to create objects/stores and what methods to call I would highly recommond taking a look at the MainAppTest function