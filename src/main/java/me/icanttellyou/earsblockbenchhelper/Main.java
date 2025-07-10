package me.icanttellyou.earsblockbenchhelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonSerializer;
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
        EarsCommon.render(features, new BlockbenchEarsRenderDelegate(features, model, false, true));

        System.out.println(getGSON().toJson(model));
    }

    public static Gson getGSON() {
        return new GsonBuilder()
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
