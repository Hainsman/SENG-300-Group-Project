package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.domain.*;
import ca.ucalgary.seng300.platform.domain.Record;
import ca.ucalgary.seng300.platform.typeadapters.RuntimeTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
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
    private final Class<T> recordClass;

    protected Store(String filePath){
        this.records = new ConcurrentHashMap<>();
        this.filePath = filePath;
        this.recordClass = resolveRecordClass();
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

    public int assignNewId(){
        if (records.isEmpty()) {
            return 1;
        }
        return Collections.max(records.keySet()) + 1;
    }

    private static Gson createGson(boolean pretty){
        RuntimeTypeAdapterFactory<Record> entityAdapterFactory = RuntimeTypeAdapterFactory.of(Record.class,"type")
                .registerSubtype(User.class, User.class.getSimpleName())
                .registerSubtype(GameHistory.class, GameHistory.class.getSimpleName())
                .registerSubtype(Participant.class, Participant.class.getSimpleName())
                .registerSubtype(SessionHistory.class, SessionHistory.class.getSimpleName())
                .registerSubtype(PlayerStats.class, PlayerStats.class.getSimpleName());

        GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapterFactory(entityAdapterFactory);
        if (pretty) {
            builder.setPrettyPrinting();
        }
        return builder.create();
    }

    private void loadAll(){
        try (FileReader fr = new FileReader(filePath)) {
            ConcurrentHashMap<Integer, T> fileData = GSON.fromJson(fr, new TypeToken<ConcurrentHashMap<Integer, Record>>(){}.getType());
            records.clear(); // clean records to ensure that if data exist we put in only to empty record
            if (fileData != null && !fileData.isEmpty())
                records.putAll(fileData);
        } catch (JsonParseException e) {
            loadLegacyDataWithoutType();
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

    @SuppressWarnings("unchecked")
    private Class<T> resolveRecordClass() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            Type actualType = parameterizedType.getActualTypeArguments()[0];
            if (actualType instanceof Class<?> clazz) {
                return (Class<T>) clazz;
            }
        }
        throw new IllegalStateException("Could not resolve record type for store " + getClass().getName());
    }

    private void loadLegacyDataWithoutType() {
        try (FileReader fr = new FileReader(filePath)) {
            JsonObject root = GSON.fromJson(fr, JsonObject.class);
            records.clear();
            if (root == null || root.isEmpty()) {
                return;
            }

            String recordTypeName = recordClass.getSimpleName();
            for (String key : root.keySet()) {
                JsonElement element = root.get(key);
                if (!(element instanceof JsonObject recordObject)) {
                    continue;
                }
                if (!recordObject.has("type")) {
                    recordObject.addProperty("type", recordTypeName);
                }
            }

            ConcurrentHashMap<Integer, T> fileData =
                    GSON.fromJson(root, new TypeToken<ConcurrentHashMap<Integer, Record>>(){}.getType());
            if (fileData != null && !fileData.isEmpty()) {
                records.putAll(fileData);
            }
        } catch (IOException | JsonParseException e) {
            System.err.println("Failed to load from: " + filePath);
        }
    }
}
