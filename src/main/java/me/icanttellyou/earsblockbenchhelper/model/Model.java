package me.icanttellyou.earsblockbenchhelper.model;

import java.util.ArrayList;
import java.util.List;

public class Model {
    public int width, height;
    public List<Piece> elements = new ArrayList<>();
    public List<Outline> outliner = new ArrayList<>();

    public Model(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
