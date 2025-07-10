package me.icanttellyou.earsblockbenchhelper.model;

import java.awt.image.BufferedImage;

public class Texture {
    public String renderMode = "default";
    public BufferedImage source;

    public Texture(BufferedImage source) {
        this.source = source;
    }
}
