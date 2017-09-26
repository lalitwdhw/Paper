package io.paperdb;

import android.content.Context;

import com.esotericsoftware.kryo.Serializer;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public class Book {

    private final DbStoragePlainFile mStorage;

    protected Book(Context context, String dbName, HashMap<Class, Serializer> serializers) {
        mStorage = new DbStoragePlainFile(context.getApplicationContext(), dbName, serializers);
    }

    /**
     * Destroys all data saved in Book.
     */
    public void destroy() {
        mStorage.destroy();
    }

    /**
     * Saves any types of POJOs or collections in Book storage.
     *
     * @param key   object key is used as part of object's file name
     * @param value object to save, must have no-arg constructor, can't be null.
     * @param <T>   object type
     * @return this Book instance
     */
    public <T> Book write(String key, T value) {
        if (value == null) {
            throw new PaperDbException("Paper doesn't support writing null root values");
        } else {
            mStorage.insert(key, value);
        }
        return this;
    }

    /**
     * Instantiates saved object using original object class (e.g. LinkedList). Support limited
     * backward and forward compatibility: removed fields are ignored, new fields have their
     * default values.
     * <p/>
     * All instantiated objects must have no-arg constructors.
     *
     * @param key object key to read
     * @return the saved object instance or null
     */
    public <T> T read(String key) {
        return read(key, null);
    }

    /**
     * Instantiates saved object using original object class (e.g. LinkedList). Support limited
     * backward and forward compatibility: removed fields are ignored, new fields have their
     * default values.
     * <p/>
     * All instantiated objects must have no-arg constructors.
     *
     * @param key          object key to read
     * @param defaultValue will be returned if key doesn't exist
     * @return the saved object instance or null
     */
    public <T> T read(String key, T defaultValue) {
        T value = mStorage.select(key);
        return value == null ? defaultValue : value;
    }

    /**
     * Checks if an object with the given key is saved in Book storage.
     *
     * @param key object key
     * @return true if object with given key exists in Book storage, false otherwise
     */
    public boolean exists(String key) {
        return mStorage.exists(key);
    }

    /**
     * Checks if an object with the given key is saved in Book storage.
     *
     * @param key object key
     * @return true if object with given key exists in Book storage, false otherwise
     * @deprecated As of release 2.6, replaced by {@link #exists(String)}}
     */
    public boolean exist(String key) {
        return mStorage.exists(key);
    }

    /**
     * Returns lastModified timestamp of last write in ms.
     * NOTE: only granularity in seconds is guaranteed. Some file systems keep
     * file modification time only in seconds.
     *
     * @param key object key
     * @return timestamp of last write for given key in ms if it exists, otherwise -1
     */
    public long lastModified(String key) {
        return mStorage.lastModified(key);
    }

    /**
     * Delete saved object for given key if it is exist.
     *
     * @param key object key
     */
    public void delete(String key) {
        mStorage.deleteIfExists(key);
    }

    /**
     * Returns all keys for objects in book.
     *
     * @return all keys
     */
    public List<String> getAllKeys() {
        return mStorage.getAllKeys();
    }

    /**
     * Sets log level for internal Kryo serializer
     *
     * @param level one of levels from {@link com.esotericsoftware.minlog.Log }
     */
    public void setLogLevel(int level) {
        mStorage.setLogLevel(level);
    }

    /**
     * Returns path to a folder containing *.pt files for all keys kept
     * in the current Book. Handy for Book export/import purposes.
     * The returned path may not exist if the method has been called prior
     * saving any data in the current Book.
     * To get path for a particular key use {@link #getFilePath(String)} instead.
     *
     * @return path to a folder locating data files for the current Book
     */
    public String getPath() {
        return mStorage.getRootFolderPath();
    }

    public String getFilePath(String key) {
        return mStorage.getOriginalFilePath(key);
    }
}
