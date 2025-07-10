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
    public Matrix3fStack rotationMatrix = new Matrix3fStack(64);
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
        rotationMatrix.rotate((float) Math.toRadians(angle), x, y, z);
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

        translate(target.origin.x, target.origin.y, target.origin.z);

        rotate(-target.rotation.z, 0.0F, 0.0F, 1.0F);
        rotate(-target.rotation.y, 0.0F, 1.0F, 0.0F);
        rotate(-target.rotation.x, 1.0F, 0.0F, 0.0F);

        Vector3f dimensions = target.getDimensions();
        translate(-(dimensions.x / 2), -(dimensions.y / 2), -(dimensions.z / 2));
    }

    @Override
    public void push() {
        modelMatrix.pushMatrix();
        rotationMatrix.pushMatrix();
    }

    @Override
    public void pop() {
        modelMatrix.popMatrix();
        rotationMatrix.popMatrix();
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

        Matrix4f modelM = scale.mul(modelMatrix, new Matrix4f());

        Vector3f from = new Vector3f((float) -width / 2, (float) -height / 2, 0);
        Vector3f to = new Vector3f((float) width / 2, (float) height / 2, 0);

        modelM.transformPosition(origin);
        modelM.transformPosition(from);
        modelM.transformPosition(to);

        from.add(origin);
        to.add(origin);

        Vector3f rotation = rotationMatrix.getEulerAnglesZYX(new Vector3f());
        rotation.div((2.0F * (float) Math.PI) / 360.0F);

        Piece p = new Piece("ears" + earsPieces++, from, to, rotation, origin);
        Face dummy = new Face();
        dummy.uv = new float[]{0, 0, 0, 0};

        p.faces.put("north", f);
        p.faces.put("south", f);
        p.faces.put("west", dummy);
        p.faces.put("east", dummy);
        p.faces.put("up", dummy);
        p.faces.put("down", dummy);
        p.inflate = grow.grow;

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
