package com.livingtechusa.reflexion.util.scopedStorageUtils;


import android.util.JsonWriter;

import com.livingtechusa.reflexion.data.entities.ReflexionItem;

import java.io.*;
import java.util.List;

// Reference: https://deevloper.android.com/reference/android/util/JsonWriter
public class ReflexionJsonWriter {
    public void writeJsonStream(OutputStream out, List<ReflexionItem> reflexionItems) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");
        writeReflexionItemArray(writer, reflexionItems);
        writer.close();
    }

    public void writeReflexionItemArray(JsonWriter writer, List<ReflexionItem> reflexionItems) throws IOException {
        writer.beginArray();
        for (ReflexionItem reflexionItem : reflexionItems) {
            writeReflexionItem(writer, reflexionItem);
        }
        writer.endArray();
    }

    public void writeReflexionItem(JsonWriter writer, ReflexionItem reflexionItem) throws IOException {
        writer.beginObject();
        writer.name("autogenPk").value(0L);
        writer.name("name").value(reflexionItem.getName());
        writer.name("description").value(reflexionItem.getDescription());
        writer.name("detailedDescription").value(reflexionItem.getDetailedDescription());
//        writer.name("imageStream").value(reflexionItem.getImage().toString());
//        writer.name("videoStream").value(reflexionItem.getVideoUri());
        writer.name("videoUrl").value(reflexionItem.getVideoUrl());
        writer.name("parent").value("");
        writer.endObject();
    }
}
