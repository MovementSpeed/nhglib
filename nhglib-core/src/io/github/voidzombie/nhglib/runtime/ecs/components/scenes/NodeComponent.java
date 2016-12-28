package io.github.voidzombie.nhglib.runtime.ecs.components.scenes;

import com.artemis.PooledComponent;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Fausto Napoli on 08/12/2016.
 * The base component needed by the SceneGraph for entity positioning.
 * Should only be created by the SceneGraph together with an entity.
 */
public class NodeComponent extends PooledComponent {
    /** Unique NodeComponent ID, corresponds with entity itself */
    public Integer id;

    /** Node */
    public Node node;

    private Vector3 translation;
    private Vector3 rotation;
    private Vector3 scale;
    private Quaternion rotationQuaternion;

    public NodeComponent() {
        node = new Node();
        translation = new Vector3();
        rotation = new Vector3();
        scale = new Vector3(1, 1, 1);
        rotationQuaternion = new Quaternion();
    }

    @Override
    protected void reset() {
        node.translation.set(new Vector3());
        node.rotation.set(new Quaternion());
        node.scale.set(new Vector3());
    }

    public void setTranslation(Vector3 translation) {
        setTranslation(translation, false);
    }

    public void setTranslation(Vector3 translation, boolean apply) {
        setTranslation(translation.x, translation.y, translation.z, apply);
    }

    public void setTranslation(float x, float y, float z) {
        setTranslation(x, y, z, false);
    }

    public void setTranslation(float x, float y, float z, boolean apply) {
        node.translation.set(x, y, z);

        if (apply) {
            applyTransforms();
        }
    }

    public void translate(Vector3 translation) {
        translate(translation, false);
    }

    public void translate(Vector3 translation, boolean apply) {
        translate(translation.x, translation.y, translation.z, apply);
    }

    public void translate(float x, float y, float z) {
        translate(x, y, z, false);
    }

    /**
     * Translates the node.
     * @param x translation.
     * @param y translation.
     * @param z translation.
     * @param apply if true, transforms will be calculated immediately. It's not recommended, instead use
     * {@link #applyTransforms() applyTransforms()} after you've completed all transforms on the node.
     */
    public void translate(float x, float y, float z, boolean apply) {
        node.translation.add(x, y, z);

        if (apply) {
            applyTransforms();
        }
    }

    public void setRotation(Vector3 rotation) {
        setRotation(rotation, false);
    }

    public void setRotation(Vector3 rotation, boolean apply) {
        setRotation(rotation.x, rotation.y, rotation.z, apply);
    }

    public void setRotation(float x, float y, float z) {
        setRotation(x, y, z, false);
    }

    public void setRotation(float x, float y, float z, boolean apply) {
        node.rotation.setEulerAngles(y, x, z);

        if (apply) {
            applyTransforms();
        }
    }

    public void rotate(Quaternion quaternion) {
        rotate(quaternion, false);
    }

    /**
     * Rotates the node.
     * @param rotation the rotation quaternion added to the node's rotation.
     * @param apply if true, transforms will be calculated immediately. It's not recommended, instead use
     * {@link #applyTransforms() applyTransforms()} after you've completed all transforms on the node.
     */
    public void rotate(Quaternion rotation, boolean apply) {
        node.rotation.add(rotation);

        if (apply) {
            applyTransforms();
        }
    }

    public void rotate(Vector3 rotation) {
        rotate(rotation, false);
    }

    public void rotate(Vector3 rotation, boolean apply) {
        rotate(rotation.x, rotation.y, rotation.z, apply);
    }

    public void rotate(float x, float y, float z) {
        rotate(x, y, z, false);
    }

    /**
     * Rotates the node.
     * @param x rotation, also known as pitch.
     * @param y rotation, also known as yaw.
     * @param z rotation, also known as roll.
     * @param apply if true, transforms will be calculated immediately. It's not recommended, instead use
     * {@link #applyTransforms() applyTransforms()} after you've completed all transforms on the node.
     */
    public void rotate(float x, float y, float z, boolean apply) {
        Quaternion quaternion = new Quaternion();
        quaternion.setEulerAngles(y, x, z);

        node.rotation.mul(quaternion);

        if (apply) {
            applyTransforms();
        }
    }

    public void setScale(Vector3 scale) {
        setScale(scale, false);
    }

    public void setScale(Vector3 scale, boolean apply) {
        setScale(scale.x, scale.y, scale.z, apply);
    }

    public void setScale(float x, float y, float z) {
        setScale(x, y, z, false);
    }

    public void setScale(float x, float y, float z, boolean apply) {
        node.scale.set(x, y, z);

        if (apply) {
            applyTransforms();
        }
    }

    public void scale(Vector3 scale) {
        scale(scale, false);
    }

    public void scale(Vector3 scale, boolean apply) {
        scale(scale.x, scale.y, scale.y, apply);
    }

    public void scale(float x, float y, float z) {
        scale(x, y, z, false);
    }

    /**
     * Scales the node.
     * @param x scale.
     * @param y scale.
     * @param z scale.
     * @param apply if true, transforms will be calculated immediately. It's not recommended, instead use
     * {@link #applyTransforms() applyTransforms()} after you've completed all transforms on the node.
     */
    public void scale(float x, float y, float z, boolean apply) {
        node.scale.add(x, y, z);

        if (apply) {
            applyTransforms();
        }
    }

    public void applyTransforms() {
        node.calculateTransforms(true);
    }

    public Float getX() {
        return getTranslation().x;
    }

    public Float getY() {
        return getTranslation().y;
    }

    public Float getZ() {
        return getTranslation().z;
    }

    public Float getXRotation() {
        return getRotation().x;
    }

    public Float getYRotation() {
        return getRotation().y;
    }

    public Float getZRotation() {
        return getRotation().z;
    }

    public Float getXScale() {
        return getScale().x;
    }

    public Float getYScale() {
        return getScale().y;
    }

    public Float getZScale() {
        return getScale().z;
    }

    public Matrix4 getTransform() {
        return node.globalTransform;
    }

    public Vector3 getTranslation() {
        node.globalTransform.getTranslation(translation);
        return translation;
    }

    public Vector3 getRotation() {
        node.globalTransform.getRotation(rotationQuaternion);
        rotation.set(
                rotationQuaternion.getPitch(),
                rotationQuaternion.getYaw(),
                rotationQuaternion.getRoll());

        return rotation;
    }

    public Vector3 getScale() {
        node.globalTransform.getScale(scale);
        return scale;
    }
}
