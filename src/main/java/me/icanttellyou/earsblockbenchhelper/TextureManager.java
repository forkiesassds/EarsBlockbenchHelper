package me.icanttellyou.earsblockbenchhelper;

import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import me.icanttellyou.earsblockbenchhelper.model.Texture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TextureManager {
    public static Map<Integer, Texture> textures = new HashMap<>();
    public static Map<EarsRenderDelegate.TexSource, Integer> sourceToTexMap = new HashMap<>();
    public static int boundTexture = 0;
    private static int textureCount = 1;

    public static void bind(EarsFeatures features, EarsRenderDelegate.TexSource texSource) {
        if (!texSource.isBuiltIn()) {
            if (sourceToTexMap.containsKey(texSource)) {
                boundTexture = sourceToTexMap.get(texSource);
                return;
            }

            try {
                byte[] bytes = texSource.getPNGData(features);

                InputStream stream = new ByteArrayInputStream(bytes);
                BufferedImage bufferedImage = ImageIO.read(stream);
                Texture texture = new Texture(bufferedImage);

                textures.put(textureCount, texture);
                sourceToTexMap.put(texSource, textureCount);
                boundTexture = textureCount;

                textureCount++;
            } catch (IOException e) {
                System.out.println("Failed to read image!");
            }

            return;
        }

        boundTexture = 0;
    }
}
