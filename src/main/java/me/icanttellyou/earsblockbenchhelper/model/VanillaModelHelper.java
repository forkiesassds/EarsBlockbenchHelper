package me.icanttellyou.earsblockbenchhelper.model;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class VanillaModelHelper {
    public static Model makeModel(boolean slim) {
        Model m = new Model(64, 64);

        Piece head = bodyPart("head", -4, 24, -4, 8, 8, 8, 0, 0, false, 0, 0, 0);
        Piece headOverlay = bodyPart("head2", -4, 24, -4, 8, 8, 8, 32, 0, true, 0, 0, 0);
        Piece torso = bodyPart("torso", -4, 12, -2, 8, 12, 4, 16, 16, false, 0, 0, 0);
        Piece torsoOverlay = bodyPart("torso2", -4, 12, -2, 8, 12, 4, 16, 32, true, 0, 0, 0);

        Piece rightArm, rightArmOverlay;
        Piece leftArm, leftArmOverlay;

        if (slim) {
            rightArm = bodyPart("right_arm", 4, 12, -2, 3, 12, 4, 40, 16, false, 0, 0, 0);
            rightArmOverlay = bodyPart("right_arm2", 4, 12, -2, 3, 12, 4, 40, 32, true, 0, 0, 0);
            leftArm = bodyPart("left_arm", -7, 12, -2, 3, 12, 4, 32, 48, false, 0, 0, 0);
            leftArmOverlay = bodyPart("left_arm2", -7, 12, -2, 3, 12, 4, 48, 48, true, 0, 0, 0);
        } else {
            rightArm = bodyPart("right_arm", -6, 12, 1, 4, 12, 4, 40, 16, false, 0, 0, 0);
            rightArmOverlay = bodyPart("right_arm2", -6, 12, 1, 4, 12, 4, 40, 32, true, 0, 0, 0);
            leftArm = bodyPart("left_arm", 6, 12, -1, 4, 12, 4, 32, 48, false, 0, 0, 0);
            leftArmOverlay = bodyPart("left_arm2", 6, 12, -1, 4, 12, 4, 48, 48, true, 0, 0, 0);
        }

        Piece rightLeg = bodyPart("right_leg", 0, 0, -2, 4, 12, 4, 0, 16, false, 0, 0, 0);
        Piece rightLegOverlay = bodyPart("right_leg2", 0, 0, -2, 4, 12, 4, 0, 32, true, 0, 0, 0);
        Piece leftLeg = bodyPart("left_leg", -4, 0, -2, 4, 12, 4, 16, 48, false, 0, 0, 0);
        Piece leftLegOverlay = bodyPart("left_leg2", -4, 0, -2, 4, 12, 4, 0, 48, true, 0, 0, 0);

        head.origin = headOverlay.origin = new Vector3f(0.0F, 0.0F, 0.0F);
        torso.origin = torsoOverlay.origin = new Vector3f(0.0F, 0.0F, 0.0F);
        rightArm.origin = rightArmOverlay.origin = new Vector3f(-5.0F, 2.0F, 0.0F);
        leftArm.origin = leftArmOverlay.origin = new Vector3f(5.0F, 2.0F, 0.0F);
        rightLeg.origin = rightLegOverlay.origin = new Vector3f(-1.9F, 12.0F, 0.0F);
        leftLeg.origin = leftLegOverlay.origin = new Vector3f(1.9F, 12.0F, 0.0F);

        Outline headO = new Outline("head", head, headOverlay);
        Outline torsoO = new Outline("torso", torso, torsoOverlay);
        Outline rightArmO = new Outline("right_arm", rightArm, rightArmOverlay);
        Outline leftArmO = new Outline("left_arm", leftArm, leftArmOverlay);
        Outline rightLegO = new Outline("right_leg", rightLeg, rightLegOverlay);
        Outline leftLegO = new Outline("left_leg", leftLeg, leftLegOverlay);

        m.elements.add(head);
        m.elements.add(headOverlay);
        m.elements.add(torso);
        m.elements.add(torsoOverlay);
        m.elements.add(rightArm);
        m.elements.add(rightArmOverlay);
        m.elements.add(leftArm);
        m.elements.add(leftArmOverlay);
        m.elements.add(rightLeg);
        m.elements.add(rightLegOverlay);
        m.elements.add(leftLeg);
        m.elements.add(leftLegOverlay);

        m.outliner.add(headO);
        m.outliner.add(torsoO);
        m.outliner.add(rightArmO);
        m.outliner.add(leftArmO);
        m.outliner.add(rightLegO);
        m.outliner.add(leftLegO);

        return m;
    }

    public static Piece bodyPart(String name,
                                float x, float y, float z,
                                float w, float h, float l,
                                float u, float v,
                                boolean overlay,
                                float rotX, float rotY, float rotZ) {
        float inflate = overlay ? (name.equals("head2") ? 0.5F : 0.25F) : 0F;

        Face north = face(u + l, v + l, u + l + w, v + l + h);
        Face south = face(u + l + w + l, v + l, u + l + w + l + w, v + l + h);
        Face west = face(u + l + w, v + l, u + l + w + l, v + l + h);
        Face east = face(u, v + l, u + l, v + l + h);
        Face up = face(u + l + w, v + l, u + l, v);
        Face down = face(u + l + w + w, v, u + l + w, v + l);

        Vector3f from = new Vector3f(x, y, z);
        Vector3f to = new Vector3f(x + w, y + h, z + l);

        Vector3f rotation = new Vector3f(rotX, rotY, rotZ);

        Piece p = new Piece(name, from, to, rotation, new Vector3f(from));

        p.faces.put("north", north);
        p.faces.put("south", south);
        p.faces.put("west", west);
        p.faces.put("east", east);
        p.faces.put("up", up);
        p.faces.put("down", down);

        p.uvOffset = new Vector2f(u, v);
        p.inflate = inflate;

        return p;
    }

    private static Face face(float minU, float minV, float maxU, float maxV) {
        Face f = new Face();
        f.uv = new float[] {minU, minV, maxU, maxV};
        return f;
    }
}
