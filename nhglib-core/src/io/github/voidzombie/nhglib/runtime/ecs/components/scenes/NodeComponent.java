package io.github.voidzombie.nhglib.runtime.ecs.components.scenes;

import com.artemis.PooledComponent;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import io.github.voidzombie.nhglib.utils.data.QuaternionPool;
import io.github.voidzombie.nhglib.utils.data.VectorPool;

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

    private Vector3 temp;

    private Vector3 translation;
    private Vector3 rotation;
    private Vector3 scale;

    private Vector3 translationDelta;
    private Vector3 rotationDelta;
    private Vector3 scaleDelta;

    private Quaternion rotationQuaternion;

    public NodeComponent() {
        node = new Node();

        temp = VectorPool.getVector3();

        translation = VectorPool.getVector3();
        rotation = VectorPool.getVector3();
        scale = VectorPool.getVector3().set(1, 1, 1);

        translationDelta = VectorPool.getVector3();
        rotationDelta = VectorPool.getVector3();
        scaleDelta = VectorPool.getVector3();

        rotationQuaternion = new Quaternion();
    }

    @Override
    protected void reset() {
        node.translation.set(VectorPool.getVector3());
        node.rotation.set(new Quaternion());
        node.scale.set(VectorPool.getVector3());

        translationDelta.set(Vector3.Zero);
        rotationDelta.set(Vector3.Zero);
        scaleDelta.set(Vector3.Zero);
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
        translationDelta.set(
                translation.x - x,
                translation.y - y,
                translation.z - z);

        node.translation.set(x, y, z);

        if (apply) {
            applyTransforms();
        }
    }

    public void setTranslationX(float x) {
        translationDelta.x = translation.x - x;
        node.translation.x = x;
    }

    public void setTranslationY(float y) {
        translationDelta.y = translation.y - y;
        node.translation.y = y;
    }

    public void setTranslationZ(float z) {
        translationDelta.z = translation.z - z;
        node.translation.z = z;
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
        translationDelta.set(
                translation.x - x,
                translation.y - y,
                translation.z - z);

        Vector3 translation = VectorPool.getVector3();
        translation.set(x, y, z);

        float len = translation.len();
        translation.rot(node.localTransform).nor().scl(len);

        node.translation.add(translation);

        if (apply) {
            applyTransforms();
        }

        VectorPool.freeVector3(translation);
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
        rotationDelta.set(
                rotation.x - x,
                rotation.y - y,
                rotation.z - z);

        node.rotation.setEulerAngles(y, x, z);

        if (apply) {
            applyTransforms();
        }
    }

    public void setRotation(Quaternion rotation) {
        rotationDelta.set(
                rotation.x - rotation.getPitch(),
                rotation.y - rotation.getYaw(),
                rotation.z - rotation.getRoll());

        node.rotation.set(rotation);
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
        rotationDelta.set(
                rotation.x - x,
                rotation.y - y,
                rotation.z - z);

        Quaternion quaternion = QuaternionPool.getQuaternion();
        quaternion.setEulerAngles(y, x, z);

        node.rotation.mul(quaternion);

        if (apply) {
            applyTransforms();
        }

        QuaternionPool.freeQuaternion(quaternion);
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
        scaleDelta.set(
                scale.x - x,
                scale.y - y,
                scale.z - z);

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
        scaleDelta.set(
                scale.x - x,
                scale.y - y,
                scale.z - z);

        node.scale.add(x, y, z);

        if (apply) {
            applyTransforms();
        }
    }

    public void setTransform(Matrix4 transform) {
        if (transform != null) {
            Vector3 translation = VectorPool.getVector3();
            Vector3 scale = VectorPool.getVector3();
            Quaternion rotation = QuaternionPool.getQuaternion();

            transform.getTranslation(translation);
            transform.getRotation(rotation);
            transform.getScale(scale);

            setTranslation(translation);
            setRotation(rotation);
            setScale(scale);

            VectorPool.freeVector3(translation, scale);
            QuaternionPool.freeQuaternion(rotation);
        }
    }

    public void setTransform(Vector3 translation, Vector3 rotation, Vector3 scale) {
        setTranslation(translation);
        setRotation(rotation);
        setScale(scale);
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

    public Float getXDelta() {
        Float res = translationDelta.x;
        translationDelta.x = 0;
        return res;
    }

    public Float getYDelta() {
        Float res = translationDelta.y;
        translationDelta.y = 0;
        return res;
    }

    public Float getZDelta() {
        Float res = translationDelta.z;
        translationDelta.z = 0;
        return res;
    }

    public Float getXRotationDelta() {
        Float res = rotationDelta.x;
        rotationDelta.x = 0;
        return res;
    }

    public Float getYRotationDelta() {
        Float res = rotationDelta.y;
        rotationDelta.y = 0;
        return res;
    }

    public Float getZRotationDelta() {
        Float res = rotationDelta.z;
        rotationDelta.z = 0;
        return res;
    }

    public Float getXScaleDelta() {
        Float res = scaleDelta.x;
        scaleDelta.x = 0;
        return res;
    }

    public Float getYScaleDelta() {
        Float res = scaleDelta.y;
        scaleDelta.y = 0;
        return res;
    }

    public Float getZScaleDelta() {
        Float res = scaleDelta.z;
        scaleDelta.z = 0;
        return res;
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

    public Vector3 getTranslationDelta() {
        Vector3 res = temp.set(translationDelta);
        translationDelta.set(Vector3.Zero);

        return res;
    }

    public Vector3 getRotationDelta() {
        Vector3 res = temp.set(rotationDelta);
        rotationDelta.set(Vector3.Zero);

        return res;
    }

    public Vector3 getScaleDelta() {
        Vector3 res = temp.set(scaleDelta);
        scaleDelta.set(Vector3.Zero);

        return res;
    }

    public Quaternion getRotationQuaternion() {
        getRotation();
        return rotationQuaternion;
    }
}
