package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.domain.User;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonWriter;

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
//        for (int i = 0; i < users.size(); i++) {
//            User userSearch = users.get(i);
//            if (userSearch.getlogin().equals(username)){
//                return userSearch; // if user found return user object
//            }
//        }
        return null; //user not found
    }

    @Override
    public void create(User user) {
        userMap.put(user.getID(), user);
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
        JsonObject json = job.build();

        try(JsonWriter jw = Json.createWriter(new FileWriter("Users.txt"))){
            jw.writeObject(json);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}