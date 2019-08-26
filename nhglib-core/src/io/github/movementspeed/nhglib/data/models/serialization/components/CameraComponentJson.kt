package io.github.movementspeed.nhglib.data.models.serialization.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.core.ecs.components.graphics.CameraComponent
import io.github.movementspeed.nhglib.core.ecs.systems.impl.RenderingSystem
import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
class CameraComponentJson : ComponentJson() {
    override fun parse(jsonValue: JsonValue) {
        val cameraComponent = nhg!!.entities.createComponent(entity, CameraComponent::class.java)

        val camera: Camera

        val nearPlane = jsonValue.getFloat("nearPlane")
        val farPlane = jsonValue.getFloat("farPlane")

        val type = CameraComponent.Type.fromString(
                jsonValue.getString("cameraType"))

        when (type) {
            CameraComponent.Type.PERSPECTIVE -> {
                val fieldOfView = jsonValue.getFloat("fieldOfView")

                camera = PerspectiveCamera(
                        fieldOfView,
                        Gdx.graphics.width.toFloat(),
                        Gdx.graphics.height.toFloat())
            }

            CameraComponent.Type.ORTHOGRAPHIC -> camera = OrthographicCamera()
            else -> {
                val fieldOfView = jsonValue.getFloat("fieldOfView")
                camera = PerspectiveCamera(fieldOfView, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
            }
        }

        camera.near = nearPlane
        camera.far = farPlane

        cameraComponent.camera = camera
        cameraComponent.type = type

        output = cameraComponent
    }
}
