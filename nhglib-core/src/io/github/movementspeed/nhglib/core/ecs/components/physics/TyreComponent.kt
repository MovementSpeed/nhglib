package io.github.movementspeed.nhglib.core.ecs.components.physics

import com.artemis.Component
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.dynamics.btRaycastVehicle
import com.badlogic.gdx.physics.bullet.dynamics.btWheelInfo

/**
 * Created by Fausto Napoli on 05/06/2017.
 */
class TyreComponent : Component() {
    var frontTyre: Boolean = false
    var index: Int = 0

    var radius: Float = 0.toFloat()
    var suspensionRestLength: Float = 0.toFloat()
    var wheelFriction: Float = 0.toFloat()

    var state: State

    var attachmentPoint: Vector3? = null
    var direction: Vector3? = null
    var axis: Vector3? = null
    var translation: Vector3
    var rotation: Quaternion

    var vehicleComponent: VehicleComponent? = null

    val steering: Float
        get() {
            val info = vehicleComponent!!.vehicle.getWheelInfo(index)
            return info.steering
        }

    val rotationQuaternion: Quaternion
        get() {
            val mat = vehicleComponent!!.vehicle.getWheelTransformWS(index)
            return mat.getRotation(rotation)
        }

    val vehicle: btRaycastVehicle
        get() = vehicleComponent!!.vehicle

    init {
        state = State.NOT_INITIALIZED
    }

    fun build() {
        translation = Vector3()
        rotation = Quaternion()

        vehicleComponent!!.addTyre(attachmentPoint, direction, axis, radius,
                suspensionRestLength, wheelFriction, frontTyre)
    }

    fun getRotation(): Float {
        val info = vehicleComponent!!.vehicle.getWheelInfo(index)
        return info.rotation
    }

    fun getTranslation(): Vector3 {
        val mat = vehicleComponent!!.vehicle.getWheelTransformWS(index)
        return mat.getTranslation(translation)
    }

    enum class State {
        NOT_INITIALIZED,
        READY
    }
}
