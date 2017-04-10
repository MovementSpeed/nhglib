package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.LightComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.ThreadedIteratingSystem;
import io.github.voidzombie.nhglib.runtime.threading.Threading;
import io.github.voidzombie.nhglib.utils.data.VectorPool;

/**
 * Created by Fausto Napoli on 14/03/2017.
 */
public class LightSystem extends ThreadedIteratingSystem {
    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<LightComponent> lightMapper;

    public LightSystem(Threading threading) {
        super(Aspect.all(NodeComponent.class, LightComponent.class), threading);
    }

    @Override
    protected void process(int entityId) {
        NodeComponent node = nodeMapper.get(entityId);
        LightComponent light = lightMapper.get(entityId);

        light.light.position.set(node.getTranslation());

        switch (light.type) {
            case SPOT_LIGHT:
            case DIRECTIONAL_LIGHT:
                Matrix4 tempMatrix = new Matrix4(node.getTransform());
                tempMatrix.translate(0f, 0f, 1f);

                Vector3 direction = VectorPool.getVector3();
                direction.set(light.light.position)
                        .sub(tempMatrix.getTranslation(VectorPool.getVector3()));

                light.light.direction.set(direction);
                break;
        }
    }
}
