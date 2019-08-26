package io.github.movementspeed.nhglib.core.ecs.components.physics

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape
import com.badlogic.gdx.physics.bullet.dynamics.btDefaultVehicleRaycaster
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRaycastVehicle
import com.badlogic.gdx.physics.bullet.dynamics.btVehicleRaycaster
import com.badlogic.gdx.utils.Disposable
import io.github.movementspeed.nhglib.assets.Assets

/**
 * Created by Fausto Napoli on 11/06/2017.
 */
class VehicleComponent : RigidBodyComponent(), Disposable {
    var tyreNumber: Int = 0
        private set

    var vehicleTuning: btRaycastVehicle.btVehicleTuning
    var vehicleRaycaster: btVehicleRaycaster
    var vehicle: btRaycastVehicle

    override fun addToWorld(world: btDynamicsWorld, transform: Matrix4) {
        world.addVehicle(vehicle)
        super.addToWorld(world, transform)
    }

    override fun dispose() {
        vehicle.dispose()
        vehicleTuning.dispose()
        vehicleRaycaster.dispose()
    }

    fun setSteeringValue(steering: Float, wheel: Int) {
        vehicle.setSteeringValue(steering, wheel)
    }

    fun applyEngineForce(force: Float, wheel: Int) {
        vehicle.applyEngineForce(force, wheel)
    }

    fun setBrake(brake: Float, wheelIndex: Int) {
        vehicle.setBrake(brake, wheelIndex)
    }

    fun build(world: btDynamicsWorld, chassisShape: btCollisionShape, mass: Float): VehicleComponent {
        return build(world, chassisShape, btRaycastVehicle.btVehicleTuning(), mass)
    }

    @JvmOverloads
    fun build(world: btDynamicsWorld, chassisShape: btCollisionShape,
              vehicleTuning: btRaycastVehicle.btVehicleTuning, mass: Float, friction: Float = 1f, restitution: Float = 0f, group: Short = (-1).toShort(), masks: ShortArray = shortArrayOf()): VehicleComponent {
        this.vehicleTuning = vehicleTuning

        build(chassisShape, mass, friction, restitution, group, masks)
        body!!.activationState = Collision.DISABLE_DEACTIVATION

        vehicleRaycaster = btDefaultVehicleRaycaster(world)

        vehicle = btRaycastVehicle(vehicleTuning, body, vehicleRaycaster)
        vehicle.setCoordinateSystem(0, 1, 2)

        return this
    }

    fun build(assets: Assets, world: btDynamicsWorld): VehicleComponent {
        super.build(assets)

        body!!.activationState = Collision.DISABLE_DEACTIVATION
        vehicleRaycaster = btDefaultVehicleRaycaster(world)

        vehicle = btRaycastVehicle(vehicleTuning, body, vehicleRaycaster)
        vehicle.setCoordinateSystem(0, 1, 2)

        return this
    }

    fun addTyre(attachPoint: Vector3, radius: Float, frontWheel: Boolean): VehicleComponent {
        return addTyre(attachPoint, Vector3(0f, -1f, 0f), Vector3(-1f, 0f, 0f), radius,
                radius * 0.3f, 10f, frontWheel)
    }

    fun addTyre(attachPoint: Vector3, direction: Vector3, axis: Vector3, radius: Float,
                suspensionRestLength: Float, friction: Float, frontWheel: Boolean): VehicleComponent {
        tyreNumber++

        vehicle.addWheel(attachPoint, direction, axis, suspensionRestLength, radius, vehicleTuning, frontWheel).frictionSlip = friction

        return this
    }
}
