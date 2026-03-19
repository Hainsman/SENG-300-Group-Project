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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

class Store<T extends Record> {
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();
    private final ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();
    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Gson GSON = createGson();
    private final ConcurrentHashMap<Integer, T> records;
    private final String filePath;

    protected Store(String filePath){
        this.records = new ConcurrentHashMap<>();
        this.filePath = filePath;
        loadAll();
    }

    protected List<T> find(Predicate<T> condition) {
        readLock.lock();
        try {
            return records
                    .values()
                    .stream()
                    .filter(condition)
                    .toList();
        } finally{
            readLock.unlock();
        }
    }
    public T get(int id) {
        readLock.lock();
        try {
            return records.get(id);
        } finally {readLock.unlock();}
    }

    public void create(T record) {
        writeLock.lock();
        try {
            records.put(record.getID(), record);
            saveAll();
        } finally {
            writeLock.unlock();
        }
    };
    public void delete(T record) {
        writeLock.lock();
        try {
            records.remove(record.getID(), record);
            saveAll();
        } finally{
            writeLock.unlock();
        }
    };
    public void update(T record) {
        writeLock.lock();
        try {
            records.put(record.getID(), record);
            saveAll();
        } finally{
            writeLock.unlock();
        }
    };

    private static Gson createGson(){
        RuntimeTypeAdapterFactory<Record> entityAdapterFactory = RuntimeTypeAdapterFactory.of(Record.class,"type")
                .registerSubtype(User.class, "user")
                .registerSubtype(GameHistory.class, "gameHistory")
                .registerSubtype(Participant.class, "participant")
                .registerSubtype(SessionHistory.class, "sessionHistory")
                .registerSubtype(PlayerStats.class, "playerStats");

        return new GsonBuilder()
                .registerTypeAdapterFactory(entityAdapterFactory)
                .create();
    }

    private void loadAll(){
        try (FileReader fr = new FileReader(filePath)) {
            ConcurrentHashMap<Integer, T> fileData = GSON.fromJson(fr, new TypeToken<ConcurrentHashMap<Integer, Record>>(){}.getType());
            records.clear(); // clean records to ensure that if data exist we put in only to empty record
            if (fileData != null && !fileData.isEmpty())
                records.putAll(fileData);
        } catch (FileNotFoundException e) {
            records.clear();
        } catch (IOException e) {
            System.err.println("Failed to load from: " + filePath);
        }
    }

    private void saveAll() {
        try (FileWriter fw = new FileWriter(filePath)){
            PRETTY_GSON.toJson(records, fw);
        } catch (IOException e) {
            System.err.println("Failed to write to: " + filePath);
        }
    }
}
