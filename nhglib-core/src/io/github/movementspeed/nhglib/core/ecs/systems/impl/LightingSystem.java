package io.github.movementspeed.nhglib.core.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import io.github.movementspeed.nhglib.core.ecs.components.graphics.LightComponent;
import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent;

/**
 * Created by Fausto Napoli on 14/03/2017.
 */
public class LightingSystem extends IteratingSystem {
    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<LightComponent> lightMapper;

    private Matrix4 tempMat;
    private Vector3 tempVec1;
    private Vector3 tempVec2;

    public LightingSystem() {
        super(Aspect.all(NodeComponent.class, LightComponent.class));

        tempMat = new Matrix4();
        tempVec1 = new Vector3();
        tempVec2 = new Vector3();
    }

    @Override
    protected void process(int entityId) {
        LightComponent light = lightMapper.get(entityId);

        if (light.light.enabled) {
            NodeComponent node = nodeMapper.get(entityId);

            light.light.position.set(node.getTranslation());
            light.light.setTransform(node.getTransform());

            switch (light.type) {
                case SPOT_LIGHT:
                case DIRECTIONAL_LIGHT:
                    tempMat.set(node.getTransform());
                    tempMat.translate(0f, 1f, 0f);

                    tempVec1.set(light.light.position)
                            .sub(tempMat.getTranslation(tempVec2));

                    light.light.direction.set(tempVec1);
                    break;
            }
        }
    }
}
