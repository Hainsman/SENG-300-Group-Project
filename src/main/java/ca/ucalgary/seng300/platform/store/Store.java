package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.domain.*;
import ca.ucalgary.seng300.platform.domain.Record;
import ca.ucalgary.seng300.platform.typeadapters.RuntimeTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.Collections;

class Store<T extends Record> {
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();
    private final ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();

    private static final Gson GSON = createGson(false);
    private static final Gson PRETTY_GSON = createGson(true);

    private final ConcurrentHashMap<Integer, T> records;
    private final String filePath;

    protected Store(String filePath) {
        this.records = new ConcurrentHashMap<>();
        this.filePath = filePath;
        loadAll();
    }

    protected List<T> find(Predicate<T> condition) {
        readLock.lock();
        try {
            return records.values().stream().filter(condition).toList();
        } finally {
            readLock.unlock();
        }
    }

    public T get(int id) {
        readLock.lock();
        try {
            return records.get(id);
        } finally {
            readLock.unlock();
        }
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
        } finally {
            writeLock.unlock();
        }
    }

    public void update(T record) {
        writeLock.lock();
        try {
            records.put(record.getID(), record);
            saveAll();
        } finally {
            writeLock.unlock();
        }
    }
    };
    public int assignNewID(){
        if (records.isEmpty())
            return 1;
        return Collections.max(records.keySet()) + 1;
    }

    private static Gson createGson(boolean pretty) {
        RuntimeTypeAdapterFactory<Record> entityAdapterFactory = RuntimeTypeAdapterFactory.of(Record.class, "type")
                .registerSubtype(User.class, "User")
                .registerSubtype(GameHistory.class, "GameHistory")
                .registerSubtype(Participant.class, "Participant")
                .registerSubtype(SessionHistory.class, "SessionHistory")
                .registerSubtype(PlayerStats.class, "PlayerStats");
        GsonBuilder b = new GsonBuilder().registerTypeAdapterFactory(entityAdapterFactory);
        if (pretty) {
            b.setPrettyPrinting();
        }
        return b.create();
    private static Gson createGson(){
        RuntimeTypeAdapterFactory<Record> entityAdapterFactory = RuntimeTypeAdapterFactory.of(Record.class,"type")
                .registerSubtype(User.class, User.class.getSimpleName())
                .registerSubtype(GameHistory.class, GameHistory.class.getSimpleName())
                .registerSubtype(Participant.class, Participant.class.getSimpleName())
                .registerSubtype(SessionHistory.class, SessionHistory.class.getSimpleName())
                .registerSubtype(PlayerStats.class, PlayerStats.class.getSimpleName());

        return new GsonBuilder()
                .registerTypeAdapterFactory(entityAdapterFactory)
                .create();
    }

    private void loadAll() {
        try (FileReader fr = new FileReader(filePath)) {
            ConcurrentHashMap<Integer, T> fileData = GSON.fromJson(fr, new TypeToken<ConcurrentHashMap<Integer, Record>>() {
            }.getType());
            records.clear();
            if (fileData != null && !fileData.isEmpty()) {
                records.putAll(fileData);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e);
            records.clear();
        } catch (JsonParseException e) {
            System.err.println("Invalid or legacy JSON store (resetting): " + filePath + " — " + e.getMessage());
            if (filePath.contains("playerStats")) {
                System.err.println("Tip: Gson expects a \"type\" discriminator per entry (e.g. PlayerStats). Old files are moved to *.invalid-* and the store starts empty.");
            }
            records.clear();
            quarantineCorruptStoreFile();
        } catch (IOException e) {
            System.err.println("Failed to load from: " + filePath);
            records.clear();
        }
    }

    private void quarantineCorruptStoreFile() {
        try {
            Path p = Path.of(filePath);
            if (Files.isRegularFile(p)) {
                Path dest = Path.of(filePath + ".invalid-" + System.currentTimeMillis());
                Files.move(p, dest);
            }
        } catch (IOException ignored) {
        }
    }

    private void saveAll() {
        try {
            Path path = Path.of(filePath);
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (FileWriter fw = new FileWriter(filePath)) {
                PRETTY_GSON.toJson(records, fw);
            }
        } catch (IOException e) {
            System.err.println("Failed to write to: " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
