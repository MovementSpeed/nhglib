package io.github.movementspeed.nhglib.core.ecs.systems.impl

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.movementspeed.nhglib.core.ecs.components.graphics.CameraComponent
import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent
import io.github.movementspeed.nhglib.core.ecs.systems.base.NhgIteratingSystem

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
class CameraSystem : NhgIteratingSystem(Aspect.all(NodeComponent::class.java, CameraComponent::class.java)) {
    private val nodeMapper: ComponentMapper<NodeComponent>? = null
    private val cameraMapper: ComponentMapper<CameraComponent>? = null

    val cameras: Array<Camera>
    //private Viewport viewport;

    init {
        cameras = Array()
        //viewport = new ScalingViewport(Scaling.stretch, 640, 360);
    }

    override fun process(entityId: Int) {
        val cameraComponent = cameraMapper!!.get(entityId)
        val nodeComponent = nodeMapper!!.get(entityId)

        val camera = cameraComponent.camera
        camera!!.position.set(0f, 0f, 0f)
        camera.direction.set(0f, 0f, -1f)
        camera.up.set(0f, 1f, 0f)
        camera.transform(nodeComponent.transform)

        /*viewport.setCamera(camera);
        viewport.update(640, 360);*/

        /*if (camera.viewportWidth != RenderingSystem.renderWidth ||
                camera.viewportHeight != RenderingSystem.renderHeight) {
            camera.viewportWidth = RenderingSystem.renderWidth;
            camera.viewportHeight = RenderingSystem.renderHeight;
        }*/

        /*camera.position.set(nodeComponent.getTranslation());

        Quaternion rotation = nodeComponent.getRotationQuaternion();

        camera.direction.rotate(camera.up, -rotation.getYaw());
        camera.up.rotate(camera.direction, rotation.getRoll());

        vec.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(vec, rotation.getPitch());*/

        camera.update()

        if (!cameras.contains(camera, true)) {
            cameras.add(camera)
        }
    }
}
