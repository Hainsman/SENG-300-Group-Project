package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.domain.*;
import ca.ucalgary.seng300.platform.domain.Record;
import ca.ucalgary.seng300.platform.typeadapters.RuntimeTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.Collections;

abstract class Store<T extends Record> {
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();
    private final ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();

    private static final Gson GSON = createGson(false);
    private static final Gson PRETTY_GSON = createGson(true);
    /** Plain Gson for files written before {@code "type"} discriminators were enforced. */
    private static final Gson LEGACY_GSON = new GsonBuilder().create();

    private final ConcurrentHashMap<Integer, T> records;
    private final String filePath;

    protected abstract Class<T> recordType();

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
    }

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

    public int assignNewID() {
        if (records.isEmpty()) {
            return 1;
        }
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
    }

    private void loadAll() {
        Path path = Path.of(filePath);
        try {
            if (!Files.isRegularFile(path)) {
                records.clear();
                return;
            }
            String json = Files.readString(path, StandardCharsets.UTF_8);
            if (json.isBlank()) {
                records.clear();
                return;
            }
            ConcurrentHashMap<Integer, T> loaded = tryLoadWithTypeDiscriminator(json);
            if (loaded == null) {
                loaded = tryLoadLegacyShape(json);
            }
            if (loaded == null) {
                System.err.println("Invalid JSON store (resetting): " + filePath);
                records.clear();
                quarantineCorruptStoreFile();
                return;
            }
            records.clear();
            records.putAll(loaded);
        } catch (IOException e) {
            System.err.println("Failed to load from: " + filePath);
            records.clear();
        }
    }

    /**
     * Normal path: each entry includes {@code "type"} (e.g. {@code "User"}) for polymorphic {@link Record} values.
     */
    private ConcurrentHashMap<Integer, T> tryLoadWithTypeDiscriminator(String json) {
        try {
            ConcurrentHashMap<Integer, Record> fileData = GSON.fromJson(json,
                    new TypeToken<ConcurrentHashMap<Integer, Record>>() {}.getType());
            if (fileData == null) {
                return null;
            }
            ConcurrentHashMap<Integer, T> out = new ConcurrentHashMap<>();
            for (var e : fileData.entrySet()) {
                out.put(e.getKey(), recordType().cast(e.getValue()));
            }
            return out;
        } catch (JsonParseException | ClassCastException e) {
            return null;
        }
    }

    /**
     * Older files omit {@code "type"}; deserialize values as this store's concrete {@link #recordType()} instead.
     */
    private ConcurrentHashMap<Integer, T> tryLoadLegacyShape(String json) {
        try {
            Type mapType = TypeToken.getParameterized(ConcurrentHashMap.class, Integer.class, recordType()).getType();
            ConcurrentHashMap<Integer, T> fileData = LEGACY_GSON.fromJson(json, mapType);
            return fileData != null ? fileData : new ConcurrentHashMap<>();
        } catch (JsonParseException e) {
            return null;
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
            try (var fw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                PRETTY_GSON.toJson(records, fw);
            }
        } catch (IOException e) {
            System.err.println("Failed to write to: " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
