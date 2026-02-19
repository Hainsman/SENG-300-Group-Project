package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.domain.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.GsonBuilder;

public class UserStoreFile implements IUserStore {

    public Map<Integer, User> userMap = new HashMap<Integer, User>();

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
        try (FileReader fr = new FileReader("users.txt")) {
            userMap = new Gson().fromJson(fr,  new TypeToken<Map<Integer, User>>(){}.getType());
        } catch (IOException e) {
            System.err.println("Failed to load from users file");
        }
    }

    private void saveAll() {
        try (FileWriter fw = new FileWriter("users.txt")){
            new GsonBuilder().setPrettyPrinting().create().toJson(userMap, fw);
        } catch (IOException e) {
            System.err.println("Failed to write to users file");
        }
    }
}