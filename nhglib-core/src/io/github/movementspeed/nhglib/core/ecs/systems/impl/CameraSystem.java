package io.github.movementspeed.nhglib.core.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.core.ecs.components.graphics.CameraComponent;
import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent;
import io.github.movementspeed.nhglib.core.ecs.systems.base.NhgIteratingSystem;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class CameraSystem extends NhgIteratingSystem {
    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<CameraComponent> cameraMapper;

    Vector3 vec = new Vector3();

    public final Array<Camera> cameras;

    public CameraSystem() {
        super(Aspect.all(NodeComponent.class, CameraComponent.class));
        cameras = new Array<>();
    }

    @Override
    protected void process(int entityId) {
        CameraComponent cameraComponent = cameraMapper.get(entityId);
        NodeComponent nodeComponent = nodeMapper.get(entityId);

        Camera camera = cameraComponent.camera;
        camera.position.set(0, 0, 0);
        camera.direction.set(0, 0, -1);
        camera.up.set(0, 1, 0);
        camera.transform(nodeComponent.getTransform());

        camera.update();

        if (!cameras.contains(camera, true)) {
            cameras.add(camera);
        }
    }
}
