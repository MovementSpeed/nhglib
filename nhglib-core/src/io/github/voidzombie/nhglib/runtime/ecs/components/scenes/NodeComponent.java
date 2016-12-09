package io.github.voidzombie.nhglib.runtime.ecs.components.scenes;

import com.artemis.PooledComponent;
import com.badlogic.gdx.graphics.g3d.model.Node;
import io.github.voidzombie.nhglib.utils.graphics.TransformUtils;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class NodeComponent extends PooledComponent {
    public Node node;

    @Override
    protected void reset() {
        node.translation.set(TransformUtils.ZERO_VECTOR_3);
        node.rotation.set(TransformUtils.ZERO_QUATERNION);
        node.scale.set(TransformUtils.ONE_VECTOR_3);
    }
}
