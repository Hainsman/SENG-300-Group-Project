package ca.ucalgary.seng300.platform.domain;

import java.util.UUID;

public class SessionHistory extends Record{
    private UUID globallyUniqueSessionIdentifier;
    private String type;
    public SessionHistory(int id){
        super(id);
        this.globallyUniqueSessionIdentifier = UUID.randomUUID();
        this.type = "sessionHistory";
    }
    public UUID getGloballyUniqueSessionIdentifier(){return this.globallyUniqueSessionIdentifier;}
}
