package me.icanttellyou.earsblockbenchhelper.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Model {
    @Expose(serialize = false)
    public int width;
    @Expose(serialize = false)
    public int height;
    public List<Piece> elements = new ArrayList<>();
    public List<Outline> outliner = new ArrayList<>();

    public Model(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
