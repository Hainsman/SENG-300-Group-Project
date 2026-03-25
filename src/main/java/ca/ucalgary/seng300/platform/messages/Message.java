package ca.ucalgary.seng300.platform.messages;
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
                .registerSubtype(CreateOrJoinSession.class, CreateOrJoinSession.class.getSimpleName())
                .registerSubtype(GameStateUpdate.class, GameStateUpdate.class.getSimpleName())
                .registerSubtype(SessionCreated.class, SessionCreated.class.getSimpleName())
                .registerSubtype(SubmitMove.class, SubmitMove.class.getSimpleName());

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
    public Message fromJson(String jsonObject){return GSON.fromJson(jsonObject, Message.class);}
}
