package ca.ucalgary.seng300.platform.domain;

import java.util.UUID;

public class SessionHistory extends Record{
    private UUID globallyUniqueSessionIdentifier;
    public SessionHistory(int id){
        super(id);
        this.globallyUniqueSessionIdentifier = UUID.randomUUID();
    }
    public UUID getGloballyUniqueSessionIdentifier(){return this.globallyUniqueSessionIdentifier;}
}
