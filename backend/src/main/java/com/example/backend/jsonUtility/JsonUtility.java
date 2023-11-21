package com.example.backend.jsonUtility;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.reflect.Type;

/** A utility class for handling JSON-related data. */
public class JsonUtility<T> {
    private final Moshi moshi = new Moshi.Builder().build();

    /**
     * Converts a json string into an object of the specified class using Moshi's JsonAdapter.
     *
     * @param <T> the object to convert the json string into
     * @param json the json string
     * @param type the class of the object to which the json string is being converted
     * @return the object of class T
     * @throws IOException if an error occurs converting the json string into object of class T
     */
    public <T> T readJson(String json, Class<T> type) throws IOException {
      JsonAdapter<T> adapter = moshi.adapter(type);
      return adapter.fromJson(json);
    }

    /**
     * Converts a json string into an object of the specified type using Moshi's JsonAdapter.
     *
     * @param <T> the object to convert the json string into
     * @param json the json string
     * @param type the type of the object to which the json string is being converted
     * @return the object of type T
     * @throws IOException if an error occurs converting the json string into object of type T
     */
    public <T> T readJson(String json, Type type) throws IOException {
      JsonAdapter<T> adapter = moshi.adapter(type);
      return adapter.fromJson(json);
    }

    /**
     * Serializes an object of type T into a json string using Moshi's JsonAdapter.
     *
     * @param <T> the type of the object to be serialized
     * @param object the object of type T to be serialized
     * @param classOfT the class of type T
     * @return a json string representation of the specified object
     */
    public <T> String serialize(T object, Class<T> classOfT) {
      JsonAdapter<T> adapter = moshi.adapter(classOfT);
      return adapter.toJson(object);
    }

}
