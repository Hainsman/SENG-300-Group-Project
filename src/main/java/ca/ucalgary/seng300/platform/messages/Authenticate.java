package ca.ucalgary.seng300.platform.messages;

import java.util.UUID;

public class Authenticate extends Message{
    private String username;
    private String password;
    public Authenticate(UUID globallyUniqueSessionId, String username, String password) {
        super(globallyUniqueSessionId, Authenticate.class.getSimpleName());
        this.username = username;
        this.password = password;
    }
    public String getUsername(){return this.username;}
    public String getPassword(){return this.password;}
}

