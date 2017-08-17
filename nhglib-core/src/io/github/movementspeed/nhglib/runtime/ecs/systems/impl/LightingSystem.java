package io.github.movementspeed.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import io.github.movementspeed.nhglib.runtime.ecs.components.graphics.LightComponent;
import io.github.movementspeed.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.movementspeed.nhglib.runtime.ecs.systems.base.ThreadedIteratingSystem;
import io.github.movementspeed.nhglib.runtime.threading.Threading;
import io.github.movementspeed.nhglib.utils.data.MatrixPool;
import io.github.movementspeed.nhglib.utils.data.VectorPool;

/**
 * Created by Fausto Napoli on 14/03/2017.
 */
public class LightingSystem extends ThreadedIteratingSystem {
    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<LightComponent> lightMapper;

    public LightingSystem(Threading threading) {
        super(Aspect.all(NodeComponent.class, LightComponent.class), threading);
    }

    @Override
    protected void process(int entityId) {
        NodeComponent node = nodeMapper.get(entityId);
        LightComponent light = lightMapper.get(entityId);

        light.light.position.set(node.getTranslation());
        light.light.setTransform(node.getTransform());

        switch (light.type) {
            case SPOT_LIGHT:
            case DIRECTIONAL_LIGHT:
                Matrix4 tempMatrix = MatrixPool.getMatrix4();
                tempMatrix.set(node.getTransform());
                tempMatrix.translate(0f, 1f, 0f);

                Vector3 direction = VectorPool.getVector3();
                Vector3 tempVec = VectorPool.getVector3();

                direction.set(light.light.position)
                        .sub(tempMatrix.getTranslation(tempVec));

                light.light.direction.set(direction);

                MatrixPool.freeMatrix4(tempMatrix);
                VectorPool.freeVector3(direction, tempVec);
                break;
        }
    }
}
