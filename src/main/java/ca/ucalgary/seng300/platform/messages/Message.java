package ca.ucalgary.seng300.platform.messages;

import ca.ucalgary.seng300.platform.domain.*;
import ca.ucalgary.seng300.platform.domain.Record;
import ca.ucalgary.seng300.platform.typeadapters.RuntimeTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.UUID;

public abstract class Message {
    private UUID globallyUniqueSessionId;
    private String type;
    private static final Gson GSON = createGson();

    protected Message (UUID globallyUniqueSessionId, String type){
        this.globallyUniqueSessionId = globallyUniqueSessionId;
        this.type = type;
    }

    private static Gson createGson(){
        RuntimeTypeAdapterFactory<Message> entityAdapterFactory = RuntimeTypeAdapterFactory.of(Message.class,"type")
                .registerSubtype(CreateOrJoinSession.class, CreateOrJoinSession.class.getSimpleName())
                .registerSubtype(GameStateUpdate.class, GameStateUpdate.class.getSimpleName())
                .registerSubtype(SessionCreated.class, SessionCreated.class.getSimpleName())
                .registerSubtype(SubmitMove.class, SubmitMove.class.getSimpleName());

        return new GsonBuilder()
                .registerTypeAdapterFactory(entityAdapterFactory)
                .create();
    }

    public UUID getGloballyUniqueSessionId(){return globallyUniqueSessionId;}
    public String toJson(Message message){return GSON.toJson(message);}
    public Message fromJson(String jsonObject){return GSON.fromJson(jsonObject, Message.class);}
}
