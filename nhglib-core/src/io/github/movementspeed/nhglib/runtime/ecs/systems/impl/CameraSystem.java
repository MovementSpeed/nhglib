package io.github.movementspeed.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.runtime.ecs.components.graphics.CameraComponent;
import io.github.movementspeed.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.movementspeed.nhglib.runtime.ecs.systems.base.NhgIteratingSystem;

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

        /*camera.position.set(nodeComponent.getTranslation());

        Quaternion rotation = nodeComponent.getRotationQuaternion();

        camera.direction.rotate(camera.up, -rotation.getYaw());
        camera.up.rotate(camera.direction, rotation.getRoll());

        vec.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(vec, rotation.getPitch());*/

        camera.update();

        if (!cameras.contains(camera, true)) {
            cameras.add(camera);
        }
    }
}
