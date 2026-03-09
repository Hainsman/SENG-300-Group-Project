package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.domain.*;
import ca.ucalgary.seng300.platform.domain.Record;
import ca.ucalgary.seng300.platform.typeadapters.RuntimeTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

class Store<T extends Record> {
    private Map<Integer, T> records = new HashMap<Integer, T>();
    String filePath;

    public Store(String filePath){
        this.filePath = filePath;
        loadAll();
    }

    protected List<T> find(Predicate<T> condition) {
        var res = records
                .values()
                .stream()
                .filter(condition);

        return res.toList();
    }
    public T get(int id) {
        return records.get(id);
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
            RuntimeTypeAdapterFactory<Record> entityAdapterFactory = RuntimeTypeAdapterFactory.of(Record.class,"type")
                    .registerSubtype(User.class, "user")
                    .registerSubtype(GameHistory.class, "gameHistory")
                    .registerSubtype(Participant.class, "participant")
                    .registerSubtype(SessionHistory.class, "sessionHistory")
                    .registerSubtype(PlayerStats.class, "playerStats");

            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(entityAdapterFactory)
                    .create();

            Map<Integer, T> fileData = gson.fromJson(fr, new TypeToken<Map<Integer, Record>>(){}.getType());

            if (fileData != null)
                records = fileData;
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
