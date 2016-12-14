package io.github.voidzombie.nhglib.runtime.ecs.components.scenes;

import com.artemis.PooledComponent;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import io.github.voidzombie.nhglib.utils.graphics.TransformUtils;

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
        translation = TransformUtils.ZERO_VECTOR_3;
        rotation = TransformUtils.ZERO_VECTOR_3;
        scale = TransformUtils.ONE_VECTOR_3;
        rotationQuaternion = TransformUtils.ZERO_QUATERNION;
    }

    @Override
    protected void reset() {
        node.translation.set(TransformUtils.ZERO_VECTOR_3);
        node.rotation.set(TransformUtils.ZERO_QUATERNION);
        node.scale.set(TransformUtils.ONE_VECTOR_3);
    }

    public void setTranslation(Vector3 translation, boolean apply) {
        setTranslation(translation.x, translation.y, translation.z, apply);
    }

    public void setTranslation(float x, float y, float z, boolean apply) {
        node.translation.set(x, y, z);

        if (apply) {
            applyTransforms();
        }
    }

    public void translate(Vector3 axis, float value, boolean apply) {
        if (axis.x > 1) axis.x = 1;
        else if (axis.x < 0) axis.x = 0;
        
        if (axis.y > 1) axis.y = 1;
        else if (axis.y < 0) axis.y = 0;

        if (axis.z > 1) axis.z = 1;
        else if (axis.z < 0) axis.z = 0;

        translate(value * axis.x, value * axis.y, value * axis.z, apply);
    }

    public void translate(Vector3 translation, boolean apply) {
        translate(translation.x, translation.y, translation.z, apply);
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

    public void setRotation(Vector3 rotation, boolean apply) {
        setRotation(rotation.x, rotation.y, rotation.z, apply);
    }

    public void setRotation(float x, float y, float z, boolean apply) {
        node.rotation.setEulerAngles(y, x, z);

        if (apply) {
            applyTransforms();
        }
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

    public void rotate(Vector3 rotation, boolean apply) {
        rotate(rotation.x, rotation.y, rotation.z, apply);
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
        float pitch = node.rotation.getPitch();
        float yaw = node.rotation.getYaw();
        float roll = node.rotation.getRoll();

        node.rotation.setEulerAngles(yaw + y, pitch + x, roll + z);

        if (apply) {
            applyTransforms();
        }
    }

    public void setScale(Vector3 scale, boolean apply) {
        setScale(scale.x, scale.y, scale.z, apply);
    }

    public void setScale(float x, float y, float z, boolean apply) {
        node.scale.set(x, y, z);

        if (apply) {
            applyTransforms();
        }
    }

    public void scale(Vector3 scale, boolean apply) {
        scale(scale.x, scale.y, scale.y, apply);
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
