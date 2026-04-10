package ca.ucalgary.seng300.platform.stubs;

import ca.ucalgary.seng300.model.Player;
import ca.ucalgary.seng300.model.Rank;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

// Stub: in-memory credentials store backed by a HashMap.
// saveDatabase/loadDatabase persist to a JSON file so the real DB can be
// swapped in at this boundary without structural changes.
public class CredentialsDatabase {

    private HashMap<String, Player> playerCredentials = new HashMap<>();

    public boolean usernameLookup(String username) {
        return playerCredentials.containsKey(username);
    }

    public boolean addNewPlayer(String username, String email, String password, Rank ranking) {
        if (username == null || playerCredentials.containsKey(username)) {
            return false;
        }
        playerCredentials.put(username, new Player(username, email, password, ranking));
        return true;
    }

    public boolean deleteExistingPlayer(String username) {
        if (!playerCredentials.containsKey(username)) {
            return false;
        }
        playerCredentials.remove(username);
        return true;
    }

    public Player findPlayerByUsername(String username) {
        return playerCredentials.get(username);
    }

    public void saveDatabase(String filename) {
        try (FileWriter fw = new FileWriter(filename)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(playerCredentials, fw);
        } catch (IOException e) {
            System.err.println("Failed to save credentials database: " + e.getMessage());
        }
    }

    public void loadDatabase(String filename) {
        try (FileReader fr = new FileReader(filename)) {
            HashMap<String, Player> loaded = new Gson().fromJson(
                    fr, new TypeToken<HashMap<String, Player>>(){}.getType());
            if (loaded != null) {
                playerCredentials = loaded;
            }
        } catch (FileNotFoundException e) {
            playerCredentials = new HashMap<>();
        } catch (IOException e) {
            System.err.println("Failed to load credentials database: " + e.getMessage());
        }
    }
}
