package io.github.voidzombie.nhglib.runtime.ecs.components.scenes;

import com.artemis.PooledComponent;
import com.badlogic.gdx.graphics.g3d.model.Node;
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

    public NodeComponent() {
        node = new Node();
        translation = Vector3.Zero;
    }

    @Override
    protected void reset() {
        node.translation.set(TransformUtils.ZERO_VECTOR_3);
        node.rotation.set(TransformUtils.ZERO_QUATERNION);
        node.scale.set(TransformUtils.ONE_VECTOR_3);
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
     * @param x value
     * @param y value
     * @param z value
     * @param apply if true, transforms will be calculated immediately. It's not recommended, instead use
     * {@link #applyTransforms() applyTransforms()} after you've completed all transforms on the node.
     */
    public void translate(float x, float y, float z, boolean apply) {
        node.translation.add(x, y, z);

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

    public Vector3 getTranslation() {
        node.globalTransform.getTranslation(translation);
        return translation;
    }
}
