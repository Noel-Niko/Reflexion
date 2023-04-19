package com.livingtechusa.reflexion.util.json;

import android.util.JsonReader;

import com.livingtechusa.reflexion.data.entities.ReflexionItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ReflexionJsonReader {
    public List<ReflexionItem> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readReflexionItemsArray(reader);
        } finally {
            reader.close();
        }
    }

    public List<ReflexionItem> readReflexionItemsArray(JsonReader reader) throws IOException {
        List<ReflexionItem> ReflexionItems = new ArrayList<ReflexionItem>();

        reader.beginArray();
        while (reader.hasNext()) {
            ReflexionItems.add(readReflexionItem(reader));
        }
        reader.endArray();
        return ReflexionItems;
    }

    public ReflexionItem readReflexionItem(JsonReader reader) throws IOException {
        reader.beginObject();
        Long autogenPk = 0L;
        String name;
        String description;
        String detailedDescription;
        String videoUrl;
        String parent;

        while (reader.hasNext()) {
            String key = reader.nextName();
            if (key.equals("autogenPk")) {
                autogenPk = reader.nextLong();
            } else if (key.equals("name")) {
                name = reader.nextString();
            } else if (key.equals("description")) {
                description = reader.nextString();
            } else if (key.equals("detailedDescription")) {
                detailedDescription = reader.nextString();
            } else if (key.equals("videoUrl")) {
                videoUrl = reader.nextString();
            } else if (key.equals("parent")) {
                parent = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new ReflexionItem();
    }

}
