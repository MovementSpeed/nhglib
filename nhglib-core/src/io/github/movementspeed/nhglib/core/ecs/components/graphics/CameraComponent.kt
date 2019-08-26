package io.github.movementspeed.nhglib.core.ecs.components.graphics

import com.artemis.Component
import com.badlogic.gdx.graphics.Camera

/**
 * Created by Fausto Napoli on 03/03/2017.
 */
class CameraComponent : Component() {
    var camera: Camera? = null
    var type: Type? = null

    enum class Type {
        PERSPECTIVE,
        ORTHOGRAPHIC;


        companion object {

            fun fromString(value: String): CameraComponent.Type? {
                var type: CameraComponent.Type? = null

                if (value.contentEquals("perspective")) {
                    type = PERSPECTIVE
                } else if (value.contentEquals("orthographic")) {
                    type = ORTHOGRAPHIC
                }

                return type
            }
        }
    }
}
