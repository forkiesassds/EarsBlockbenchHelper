package me.icanttellyou.earsblockbenchhelper;

import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.render.AbstractDetachedEarsRenderDelegate;
import me.icanttellyou.earsblockbenchhelper.model.Face;
import me.icanttellyou.earsblockbenchhelper.model.Model;
import me.icanttellyou.earsblockbenchhelper.model.Outline;
import me.icanttellyou.earsblockbenchhelper.model.Piece;
import org.joml.*;

import java.lang.Math;

public class BlockbenchEarsRenderDelegate extends AbstractDetachedEarsRenderDelegate {
    public Matrix4fStack modelMatrix = new Matrix4fStack(64);
    public EarsFeatures features;
    public boolean enableSlim, enableLayers;
    public Model model;

    private Outline earsOutline = new Outline("ears");
    private int earsPieces = 0;

    public BlockbenchEarsRenderDelegate(EarsFeatures features, Model model, boolean enableSlim, boolean enableLayers) {
        this.features = features;
        this.enableSlim = enableSlim;
        this.enableLayers = enableLayers;
        this.model = model;

        this.model.outliner.add(earsOutline);
    }

    @Override
    public void translate(float x, float y, float z) {
        modelMatrix.translate(x, -y, z);
    }

    @Override
    public void rotate(float angle, float x, float y, float z) {
        modelMatrix.rotate((float) Math.toRadians(angle), x, y, z);
    }

    @Override
    public void scale(float x, float y, float z) {
        modelMatrix.scale(x, y, z);
    }

    @Override
    public void anchorTo(BodyPart bodyPart) {
        Piece target = null;
        for (Piece piece : model.elements) {
            if (piece.name.equals(bodyPart.name().toLowerCase())) {
                target = piece;
                break;
            }
        }

        if (target == null)
            return;

        modelMatrix.translate(target.origin);

        modelMatrix.rotate((float) Math.toRadians(-target.rotation.z), 0.0F, 0.0F, 1.0F);
        modelMatrix.rotate((float) Math.toRadians(-target.rotation.y), 0.0F, 1.0F, 0.0F);
        modelMatrix.rotate((float) Math.toRadians(-target.rotation.x), 1.0F, 0.0F, 0.0F);

        Vector3f dimensions = target.getDimensions();
        modelMatrix.translate(-(dimensions.x / 2), -(dimensions.y / 2), -(dimensions.z / 2));
    }

    @Override
    public void push() {
        modelMatrix.pushMatrix();
    }

    @Override
    public void pop() {
        modelMatrix.popMatrix();
    }

    @Override
    public void bind(TexSource texSource) {
        TextureManager.bind(features, texSource);
    }

    @Override
    public void renderFront(int u, int v, int width, int height, TexRotation rot, TexFlip flip, QuadGrow grow) {
        renderQuad(u, v, width, height, rot, flip, grow, false);
    }

    @Override
    public void renderBack(int u, int v, int width, int height, TexRotation rot, TexFlip flip, QuadGrow grow) {
        renderQuad(u, v, width, height, rot, flip, grow, true);
    }

    private void renderQuad(int u, int v, int width, int height, TexRotation rot, TexFlip flip, QuadGrow grow, boolean back) {
        if (grow.grow > 0) {
            push();
            translate(-grow.grow, -grow.grow, 0);
            pop();
        }

        if (back)
            flip = flip.flipHorizontally();

        float minU = u;
        float minV = v;
        float maxU = u + (rot.transpose ? height : width);
        float maxV = v + (rot.transpose ? width : height);
        if (rot.transpose) {
            if (flip == TexFlip.HORIZONTAL) {
                flip = TexFlip.VERTICAL;
            } else if (flip == TexFlip.VERTICAL) {
                flip = TexFlip.HORIZONTAL;
            }
        }

        if (flip == TexFlip.HORIZONTAL || flip == TexFlip.BOTH) {
            float swap = maxU;
            maxU = minU;
            minU = swap;
        }

        if (flip == TexFlip.VERTICAL || flip == TexFlip.BOTH) {
            float swap = maxV;
            maxV = minV;
            minV = swap;
        }

        Face f = new Face();
        switch (rot) {
            case CW -> f.rotation = 90;
            case CCW -> f.rotation = 270;
            case UPSIDE_DOWN -> f.rotation = 180;
        }
        f.uv = new float[]{minU, minV, maxU, maxV};

        Vector3f origin = new Vector3f();
        origin.add((float) width / 2, (float) -height / 2, 0);

        Matrix4f scale = new Matrix4f(
                1, 0, 0, 0,
                0, 1, 0, 1,
                0, 0, -1, 0,
                0, 0, 0, 0
        );
        scale.mul(modelMatrix);
        Quaternionf quart = scale.getNormalizedRotation(new Quaternionf());
        scale.rotation(quart.invert(new Quaternionf()));
        scale.transformPosition(origin);

        Vector3f from = new Vector3f(origin.x - (float) width / 2, origin.y - (float) height / 2, origin.z);
        Vector3f to = new Vector3f(origin.x + (float) width / 2, origin.y + (float) height / 2, origin.z);
        Piece p = new Piece("ears" + earsPieces++, from, to, new Vector3f(0, 0, 0), origin);
        Face dummy = new Face();
        dummy.uv = new float[]{0, 0, 0, 0};

        p.uvOffset = new Vector2f(u, v);
        p.faces.put("north", f);
        p.faces.put("south", f);
        p.faces.put("west", dummy);
        p.faces.put("east", dummy);
        p.faces.put("up", dummy);
        p.faces.put("down", dummy);
        p.inflate = grow.grow;

        p.rotation = quart.transform(new Vector3f(1, 1, 1));
//        p.rotation = quart.getEulerAnglesXYZ(new Vector3f());
        p.rotation.div((2.0F * (float) Math.PI) / 360.0F);
        System.out.println(p.rotation);

        model.elements.add(p);
        earsOutline.children.add(p);
    }

    @Override
    public void renderDoubleSided(int u, int v, int width, int height, TexRotation rot, TexFlip flip, QuadGrow grow) {
        this.renderFront(u, v, width, height, rot, flip, grow);
//        this.renderBack(u, v, width, height, rot, flip, grow);
    }

    @Override
    public void renderDebugDot(float v, float v1, float v2, float v3) {
    }

    @Override
    public boolean isSlim() {
        return enableSlim;
    }

    @Override
    public boolean isJacketEnabled() {
        return enableLayers;
    }

    @Override
    public void setEmissive(boolean b) {
        TextureManager.textures.get(TextureManager.boundTexture).renderMode = b ? "emissive" : "default";
    }
}
