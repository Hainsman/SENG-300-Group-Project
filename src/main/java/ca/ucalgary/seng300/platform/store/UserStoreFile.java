package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.domain.User;

import jakarta.json.*;
import jakarta.json.stream.JsonGenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class UserStoreFile implements IUserStore {
//    public ArrayList<User> users = new ArrayList<User>(){{
//        new User(1, "Daniel", "123");
//        new User(2, "John", "456");
//        new User(3, "Alex", "789");
//    }};
    public static Map<Integer, User> userMap = new HashMap<Integer, User>();

    public UserStoreFile() {
        loadAll();
    }

    @Override
    public User find(String username) {
        for (int i = 0; i < userMap.size(); i++) {
            User userSearch = userMap.get(i);
            if (userSearch.getlogin().equals(username)){
                return userSearch;
            }
        }
        return null; //user not found
    }

    @Override
    public void create(User user) {
        userMap.put(user.getID(), user);
        System.out.println("Condition Reached");
        saveAll();
    }

    @Override
    public void delete(User user) {
        userMap.remove(user.getID(), user);
        saveAll();
    }

    @Override
    public void updater(User user) {
        userMap.put(user.getID(), user);
        saveAll();
    }

    private void loadAll() {

    }

    private void saveAll() {
        JsonObjectBuilder job = Json.createObjectBuilder();

        for (Map.Entry<Integer, User> entry : userMap.entrySet()){
            Integer id = entry.getKey();
            User user = entry.getValue();

            JsonObject userJson = Json.createObjectBuilder()
                    .add("Login", user.getlogin())
                    .add("Password", user.getPasswordHash())
                    .build();

            job.add(id.toString(), userJson);

        }
        JsonObject root = Json.createObjectBuilder()
                .add("users", job.build())
                .build();

        Map<String, Object> properties = new HashMap<>();
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(properties);

        try(FileWriter fw = new FileWriter("users.txt", false);
            JsonWriter jw = writerFactory.createWriter(fw)) {
            jw.writeObject(root);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}