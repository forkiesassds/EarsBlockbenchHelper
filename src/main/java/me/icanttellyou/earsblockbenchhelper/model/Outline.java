package me.icanttellyou.earsblockbenchhelper.model;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import org.joml.Vector3f;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Outline {
    public String name;
    public Vector3f origin, rotation;
    public int color;
    public UUID uuid = UUID.randomUUID();
    public boolean mirrorUV;
    public boolean isOpen;
    public boolean locked;
    public boolean visibility;
    public int autoUV;
    @JsonAdapter(ChildrenSerializer.class)
    public List<Piece> children = new ArrayList<>();

    public Outline(String name, Piece... pieces) {
        this.name = name;
        children.addAll(Arrays.asList(pieces));
    }

    static class ChildrenSerializer implements JsonSerializer<List<Piece>> {
        @Override
        public JsonElement serialize(List<Piece> children, Type type, JsonSerializationContext context) {
            JsonArray arr = new JsonArray();

            for (Piece p : children) {
                arr.add(p.uuid.toString());
            };
            return arr;
        }
    }
}
