package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.domain.Record;
import ca.ucalgary.seng300.platform.domain.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Store<T extends Record> {
    public Map<Integer, T> records = new HashMap<Integer, T>();
    String filePath;

    public Store(String filePath){
        this.filePath = filePath;
        loadAll();
    }

    public void create(T record) {
        records.put(record.getID(), record);
        saveAll();
    };
    public void delete(T record) {
        records.remove(record.getID(), record);
        saveAll();
    };
    public void update(T record) {
        records.put(record.getID(), record);
        saveAll();
    };

    private void loadAll(){
        try (FileReader fr = new FileReader(filePath)) {
            records = new Gson().fromJson(fr,  new TypeToken<Map<Integer, User>>(){}.getType());
        }
        catch (FileNotFoundException e) {
            records = new HashMap<Integer, T>();
        }
        catch (IOException e) {
            System.err.println("Failed to load from: " + filePath);
        }
    }

    private void saveAll() {
        try (FileWriter fw = new FileWriter(filePath)){
            new GsonBuilder().setPrettyPrinting().create().toJson(records, fw);
        } catch (FileNotFoundException e) {
            records = new HashMap<Integer, T>();
        }
        catch (IOException e) {
            System.err.println("Failed to write to: " + filePath);
        }
    }
}
