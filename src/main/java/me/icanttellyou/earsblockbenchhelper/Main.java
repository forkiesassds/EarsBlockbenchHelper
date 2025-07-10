package me.icanttellyou.earsblockbenchhelper;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsCommon;
import me.icanttellyou.earsblockbenchhelper.model.Model;
import me.icanttellyou.earsblockbenchhelper.model.Texture;
import me.icanttellyou.earsblockbenchhelper.model.VanillaModelHelper;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        EarsFeatures features;

        try {
            BufferedImage image = ImageIO.read(new File(String.join(" ", args)));
            TextureManager.textures.put(0, new Texture(image));

            features = EarsReader.read(image);
        } catch (IOException e) {
            System.out.println("Failed to read image!");
            e.printStackTrace();
            return;
        }

        Model model = VanillaModelHelper.makeModel(true);
        EarsCommon.render(features, new BlockbenchEarsRenderDelegate(features, model, true, true));

        JsonObject element = getGSON().toJsonTree(model).getAsJsonObject();
        JsonObject obj = new JsonObject();

        JsonObject meta = new JsonObject();
        meta.addProperty("format_version", "4.10");
        meta.addProperty("model_format", "free");
        meta.addProperty("box_uv", true);

        obj.add("meta", meta);

        JsonObject res = new JsonObject();
        res.addProperty("width", model.width);
        res.addProperty("height", model.height);
        obj.add("resolution", res);

        for (Map.Entry<String, JsonElement> e : element.asMap().entrySet()) {
            obj.add(e.getKey(), e.getValue());
        }

        System.out.println(obj);
    }

    public static Gson getGSON() {
        return new GsonBuilder()
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        for (Annotation annotation : f.getAnnotations()) {
                            if (annotation instanceof Expose expose) {
                                return !expose.serialize();
                            }
                        }

                        return false;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        for (Annotation annotation : clazz.getAnnotations()) {
                            if (annotation instanceof Expose expose) {
                                return !expose.serialize();
                            }
                        }

                        return false;
                    }
                })
                .registerTypeAdapter(Vector2f.class,
                        (JsonSerializer<Vector2f>)(vec2f, type, context) -> {
                            JsonArray arr = new JsonArray();
                            arr.add(vec2f.x);
                            arr.add(vec2f.y);
                            return arr;
                        })
                .registerTypeAdapter(Vector3f.class,
                        (JsonSerializer<Vector3f>)(vec3f, type, context) -> {
                            JsonArray arr = new JsonArray();
                            arr.add(vec3f.x);
                            arr.add(vec3f.y);
                            arr.add(vec3f.z);
                            return arr;
                        })
                .create();
    }
}
