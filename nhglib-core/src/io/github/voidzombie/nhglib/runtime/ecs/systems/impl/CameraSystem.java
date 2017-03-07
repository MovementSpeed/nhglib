package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.CameraComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.NhgIteratingSystem;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class CameraSystem extends NhgIteratingSystem {
    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<CameraComponent> cameraMapper;

    Quaternion quat = new Quaternion();
    Vector3 vec = new Vector3();
    Vector3 vec2 = new Vector3(0, 1, 0);

    public final Array<Camera> cameras;

    public CameraSystem() {
        super(Aspect.all(NodeComponent.class, CameraComponent.class));

        cameras = new Array<>();
    }

    float memZRotToApply;

    @Override
    protected void process(int entityId) {
        CameraComponent cameraComponent = cameraMapper.get(entityId);
        NodeComponent nodeComponent = nodeMapper.get(entityId);

        Camera camera = cameraComponent.camera;
        camera.position.set(nodeComponent.getTranslation());

        camera.direction.rotate(camera.up, nodeComponent.getYRotationDelta());
        camera.up.rotate(camera.direction, nodeComponent.getZRotationDelta());

        vec.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(vec, nodeComponent.getXRotationDelta());

        /*memZRotToApply += nodeComponent.getZRotationDelta();

        camera.up.set(vec2);
        camera.direction.rotate(camera.up, nodeComponent.getYRotationDelta());

        vec.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(vec, nodeComponent.getXRotationDelta());

        vec2.set(camera.up);
        camera.up.rotate(camera.direction, memZRotToApply);*/

        camera.update();

        if (!cameras.contains(camera, true)) {
            cameras.add(camera);
        }
    }
}