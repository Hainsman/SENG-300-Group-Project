package ca.ucalgary.seng300.platform.domain;

import java.util.UUID;

public class SessionHistory extends Record{
    private UUID globallyUniqueSessionIdentifier;
    private int userId;

    public SessionHistory(int id, int userId, UUID uuid){
        super(id, SessionHistory.class.getSimpleName());
        this.globallyUniqueSessionIdentifier = uuid;
        this.userId = userId;
    }
    public UUID getGloballyUniqueSessionIdentifier(){return this.globallyUniqueSessionIdentifier;}
    public int getUserId(){return this.userId;}
}
