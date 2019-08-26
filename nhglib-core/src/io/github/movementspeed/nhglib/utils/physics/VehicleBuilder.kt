package io.github.movementspeed.nhglib.utils.physics

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape
import io.github.movementspeed.nhglib.assets.Asset
import io.github.movementspeed.nhglib.assets.Assets
import io.github.movementspeed.nhglib.core.ecs.components.graphics.ModelComponent
import io.github.movementspeed.nhglib.core.ecs.components.physics.VehicleComponent
import io.github.movementspeed.nhglib.core.ecs.components.physics.TyreComponent
import io.github.movementspeed.nhglib.core.ecs.systems.impl.PhysicsSystem
import io.github.movementspeed.nhglib.core.ecs.utils.Entities
import io.github.movementspeed.nhglib.graphics.scenes.Scene
import io.github.movementspeed.nhglib.graphics.scenes.SceneGraph

/**
 * Created by Fausto Napoli on 11/06/2017.
 */
class VehicleBuilder(private val entities: Entities, private val assets: Assets, scene: Scene) {
    private var builtWheels: Int = 0
    private var vehicleEntity: Int = 0

    private val physicsSystem = entities.getEntitySystem(PhysicsSystem::class.java)
    private val sceneGraph = scene.sceneGraph

    private var chassisModel: Model? = null
    private var wheelModel: Model? = null

    private var vehicleComponent: VehicleComponent? = null

    private var wheelEntities: IntArray? = null

    fun begin(entity: Int, wheels: Int): VehicleBuilder {
        wheelEntities = IntArray(wheels)
        vehicleEntity = entity

        wheelEntities?.apply {
            for (i in 0 until wheels) {
                set(i, sceneGraph.addSceneEntity(entity.toString() + "_wheel_" + i))
            }
        }

        return this
    }

    fun begin(name: String, wheels: Int): VehicleBuilder {
        wheelEntities = IntArray(wheels)

        // Create the main vehicle entity
        vehicleEntity = sceneGraph.addSceneEntity(name + "_chassis")

        // Create wheel entities
        wheelEntities?.apply {
            for (i in 0 until wheels) {
                set(i, sceneGraph.addSceneEntity(name + "_wheel_" + i))
            }
        }

        return this
    }

    fun setChassisAsset(chassisAsset: Asset): VehicleBuilder {
        assets.loadAsset(chassisAsset) { asset -> setChassisModel(assets.get<Any>(asset) as Model) }
        return this
    }

    fun setWheelAsset(wheelAsset: Asset): VehicleBuilder {
        assets.loadAsset(wheelAsset) { asset -> setWheelModel(assets.get<Any>(asset) as Model) }
        return this
    }

    fun setChassisModel(model: Model): VehicleBuilder {
        this.chassisModel = model

        val modelComponent = entities.createComponent(vehicleEntity, ModelComponent::class.java)
        modelComponent.buildWithModel(model)

        return this
    }

    fun setWheelModel(model: Model): VehicleBuilder {
        this.wheelModel = model

        wheelEntities?.forEach {
            val wheelModel = entities.createComponent(it, ModelComponent::class.java)
            wheelModel.buildWithModel(model)
        }

        return this
    }

    fun buildChassis(mass: Float): VehicleBuilder {
        val chassisHalfExtents = chassisModel!!
                .calculateBoundingBox(BoundingBox())
                .getDimensions(Vector3())
                .scl(0.5f)

        val boxShape = btBoxShape(chassisHalfExtents)
        return buildChassis(boxShape, mass)
    }

    fun buildChassis(vehicleShape: btCollisionShape, mass: Float): VehicleBuilder {
        vehicleComponent = entities.createComponent(vehicleEntity, VehicleComponent::class.java)
        vehicleComponent?.build(physicsSystem.bulletWorld!!, vehicleShape, mass)

        return this
    }

    fun buildWheel(point: Vector3, direction: Vector3, axis: Vector3, friction: Float,
                   frontWheel: Boolean): VehicleBuilder {
        val wheelHalfExtents = wheelModel
                ?.calculateBoundingBox(BoundingBox())
                ?.getDimensions(Vector3())
                ?.scl(0.5f)

        return buildWheel(point, direction, axis,
                wheelHalfExtents?.z ?: 0f,
                wheelHalfExtents?.z ?: 0f * 0.3f,
                friction, frontWheel)
    }

    fun buildWheel(point: Vector3, direction: Vector3, axis: Vector3, radius: Float,
                   suspensionRestLength: Float, friction: Float, frontWheel: Boolean): VehicleBuilder {
        if (builtWheels < wheelEntities?.size ?: 0) {
            vehicleComponent?.addTyre(point, direction, axis, radius, suspensionRestLength, friction, frontWheel)

            val tyreComponent = entities.createComponent(wheelEntities!![builtWheels], TyreComponent::class.java)
            tyreComponent.build()
            tyreComponent.index = builtWheels

            builtWheels++
        }

        return this
    }

    fun end(): VehicleComponent? {
        return vehicleComponent
    }
}
