package ca.ucalgary.seng300.platform.messages;
/*
import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.platform.typeadapters.RuntimeTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.UUID;

public abstract class Message {
    private UUID globallyUniqueSessionId;
    private String type;
    private static final Gson GSON = createGson();

    public Message (UUID globallyUniqueSessionId, String type){
        this.globallyUniqueSessionId = globallyUniqueSessionId;
        this.type = type;
    }

    private static Gson createGson(){
        RuntimeTypeAdapterFactory<Message> entityAdapterFactory = RuntimeTypeAdapterFactory.of(Message.class,"type")
//                .registerSubtype(GameStateUpdate.class, GameStateUpdate.class.getSimpleName())
//                .registerSubtype(SubmitMove.class, SubmitMove.class.getSimpleName())
                .registerSubtype(CreateAccount.class, CreateAccount.class.getSimpleName())
                .registerSubtype(Authenticate.class, Authenticate.class.getSimpleName())
                .registerSubtype(JoinGameRoom.class, JoinGameRoom.class.getSimpleName())
                .registerSubtype(CreateGameRoom.class, CreateGameRoom.class.getSimpleName());

        RuntimeTypeAdapterFactory<GameState> gameStateAdapterFactory = RuntimeTypeAdapterFactory.of(GameState.class, "type")
                .registerSubtype(TTTGameState.class, TTTGameState.class.getSimpleName());
        //this is where we will register the Connect Four game state

        return new GsonBuilder()
                .registerTypeAdapterFactory(entityAdapterFactory)
                .registerTypeAdapterFactory(gameStateAdapterFactory)
                .create();
    }

    public UUID getGloballyUniqueSessionId(){return globallyUniqueSessionId;}
    public static String toJson(Message message){return GSON.toJson(message);}
    public static Message fromJson(String jsonObject){return GSON.fromJson(jsonObject, Message.class);}
}*/