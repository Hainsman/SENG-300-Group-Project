package ca.ucalgary.seng300.platform.services;

import ca.ucalgary.seng300.platform.domain.Client;

import java.util.ArrayList;

public class ChatService {
    public ArrayList<Client> users;

    public ChatService() {
        users = new ArrayList<>();
    }
}
