package me.icanttellyou.earsblockbenchhelper.model;

import com.google.gson.annotations.SerializedName;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Piece {
    public String name;
    @SerializedName("box_uv")
    public boolean boxUV;
    @SerializedName("render_order")
    public String renderOrder = "default";
    public Vector3f from, to;
    public float inflate;
    public Vector3f rotation;
    public Vector3f origin;
    public Map<String, Face> faces = new HashMap<>();
    public String type = "cube";
    public UUID uuid = UUID.randomUUID();

    public Piece(String name, Vector3f from, Vector3f to) {
        this.name = name;
        this.from = from;
        this.to = to;
    }

    public Piece(String name, Vector3f from, Vector3f to, Vector3f rotation, Vector3f origin) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.rotation = rotation;
        this.origin = origin;
    }

    public Vector3f getDimensions() {
        float width = to.x - from.x;
        float height = to.y - from.y;
        float length = to.z - from.z;

        return new Vector3f(width + inflate * 2, height + inflate * 2, length + inflate * 2);
    }
}
