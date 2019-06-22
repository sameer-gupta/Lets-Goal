package com.example.habit.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonHelper {

    public static <R> String convertToStringJsonPro(R req) {

        // Gson gObj = new Gson();
        // String reqString = gObj.toJson(req);
        ObjectMapper mapper = new ObjectMapper();
        String responseJson = "";
        try {
            responseJson = mapper.writeValueAsString(req);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return responseJson;
    }

    public static <R> String convertToString(R req) {

        Gson gObj = new Gson();
        String reqString = gObj.toJson(req);
        return reqString;
    }

    public static Map<String, Object> toMap(JsonObject object) {
        Map<String, Object> map = new HashMap<String, Object>();

        Set<Entry<String, JsonElement>> entries = object.entrySet();
        for (Entry<String, JsonElement> entry : entries) {
            String key = entry.getKey();
            Object value = object.get(key);

            if (value instanceof JsonArray) {
                value = toList((JsonArray) value);
            } else if (value instanceof JsonObject) {
                value = toMap((JsonObject) value);
            } else if (value instanceof JsonElement) {
                if (((JsonElement) value).isJsonPrimitive()) {
                    map.put(key, ((JsonElement) value).getAsString());
                    continue;
                }
            }
            map.put(key, value);
        }
        return map;
    }

    public static <R> R fromJson(String jsonString, Class<R> clazz) {
        Gson gObj = new Gson();
        return gObj.fromJson(jsonString, clazz);
    }

    public static <R> R fromJson(String jsonString, Type type) {
        Gson gObj = new Gson();
        return gObj.fromJson(jsonString, type);
    }

    private static List<Object> toList(JsonArray array) {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.size(); i++) {
            Object value = array.get(i);
            if (value instanceof JsonArray) {
                value = toList((JsonArray) value);
            }

            else if (value instanceof JsonObject) {
                value = toMap((JsonObject) value);
            } else if (value instanceof JsonElement) {
                if (((JsonElement) value).isJsonPrimitive()) {
                    list.add(((JsonElement) value).getAsString());
                    continue;
                }
            }
            list.add(value);
        }
        return list;
    }

    public static JsonArray fromStringToJsonArray(String jsonArrayString) {
        JsonParser parser = new JsonParser();
        JsonElement elem = parser.parse(jsonArrayString);

        return elem.getAsJsonArray();
    }

    public static <J> String toJson(J obj) {

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(obj);

        return json;
    }

    public static <J> J toObject(String data, Class<J> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(data, clazz);
    }
}
