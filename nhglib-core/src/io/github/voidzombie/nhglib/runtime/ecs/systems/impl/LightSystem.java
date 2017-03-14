package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.Vector3;
import io.github.voidzombie.nhglib.graphics.lights.NhgDirectionalLight;
import io.github.voidzombie.nhglib.graphics.lights.NhgPointLight;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.LightComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.ThreadedIteratingSystem;

/**
 * Created by Fausto Napoli on 14/03/2017.
 */
public class LightSystem extends ThreadedIteratingSystem {
    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<LightComponent> lightMapper;

    public LightSystem() {
        super(Aspect.all(NodeComponent.class, LightComponent.class));
    }

    @Override
    protected void process(int entityId) {
        NodeComponent node = nodeMapper.get(entityId);
        LightComponent light = lightMapper.get(entityId);

        switch (light.type) {
            case POINT_LIGHT:
                NhgPointLight pointLight = (NhgPointLight) light.light;
                pointLight.position.set(node.getTranslation());
                break;

            case SPOT_LIGHT:
                SpotLight spotLight = (SpotLight) light.light;
                spotLight.position.set(node.getTranslation());
                spotLight.direction.set(Vector3.Z).rot(node.getTransform());
                break;

            case DIRECTIONAL_LIGHT:
                NhgDirectionalLight directionalLight = (NhgDirectionalLight) light.light;
                directionalLight.direction.set(Vector3.Z).rot(node.getTransform());
                break;
        }
    }
}
