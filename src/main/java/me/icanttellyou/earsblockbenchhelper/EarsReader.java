package me.icanttellyou.earsblockbenchhelper;

import com.unascribed.ears.api.features.AlfalfaData;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeaturesParser;
import com.unascribed.ears.common.legacy.AWTEarsImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class EarsReader {
    public static EarsFeatures read(BufferedImage image) {
        AWTEarsImage earsImage = new AWTEarsImage(image);
        AlfalfaData alfalfa = EarsCommon.preprocessSkin(earsImage);

        return EarsFeaturesParser.detect(earsImage, alfalfa, bytes -> {
            InputStream stream = new ByteArrayInputStream(bytes);
            BufferedImage bufferedImage = ImageIO.read(stream);
            return new AWTEarsImage(bufferedImage);
        });
    }
}
