package io.github.movementspeed.nhglib.core.ecs.systems.impl

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.artemis.utils.IntBag
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.DebugDrawer
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import io.github.movementspeed.nhglib.core.ecs.components.physics.RigidBodyComponent
import io.github.movementspeed.nhglib.core.ecs.components.physics.VehicleComponent
import io.github.movementspeed.nhglib.core.ecs.components.physics.TyreComponent
import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent
import io.github.movementspeed.nhglib.core.ecs.systems.base.NhgIteratingSystem

/**
 * Created by Fausto Napoli on 04/05/2017.
 */
class PhysicsSystem : NhgIteratingSystem(Aspect
        .all(NodeComponent::class.java)
        .one(RigidBodyComponent::class.java, VehicleComponent::class.java, TyreComponent::class.java)) {

    var isPhysicsInitialized: Boolean = false
        private set

    var bulletWorld: btDynamicsWorld? = null
        private set
    private var constraintSolver: btConstraintSolver? = null
    private var collisionConfiguration: btDefaultCollisionConfiguration? = null
    private var collisionDispatcher: btCollisionDispatcher? = null
    private var dbvtBroadphase: btDbvtBroadphase? = null

    private val nodeMapper: ComponentMapper<NodeComponent>? = null
    private val rigidBodyMapper: ComponentMapper<RigidBodyComponent>? = null
    private val vehicleMapper: ComponentMapper<VehicleComponent>? = null
    private val wheelMapper: ComponentMapper<TyreComponent>? = null

    init {

        initPhysics()
    }

    public override fun dispose() {
        isPhysicsInitialized = false

        val entityIds = entityIds
        for (entity in entityIds.data) {
            val bodyComponent = rigidBodyMapper!!.get(entity)

            bodyComponent?.dispose()
        }

        bulletWorld!!.dispose()
        constraintSolver!!.dispose()
        collisionConfiguration!!.dispose()
        collisionDispatcher!!.dispose()
        dbvtBroadphase!!.dispose()
    }

    override fun begin() {
        super.begin()

        bulletWorld!!.stepSimulation(
                Gdx.graphics.deltaTime,
                5,
                TIME_STEP)
    }

    override fun process(entityId: Int) {
        var bodyComponent: RigidBodyComponent? = null
        var vehicleComponent: VehicleComponent? = null
        var tyreComponent: TyreComponent? = null

        val nodeComponent = nodeMapper!!.get(entityId)

        if (rigidBodyMapper!!.has(entityId)) {
            bodyComponent = rigidBodyMapper.get(entityId)
        } else if (wheelMapper!!.has(entityId)) {
            tyreComponent = wheelMapper.get(entityId)
        } else if (vehicleMapper!!.has(entityId)) {
            vehicleComponent = vehicleMapper.get(entityId)
        }

        if (bodyComponent != null) {
            processBodyComponent(bodyComponent, nodeComponent)
        } else if (tyreComponent != null) {
            processWheelComponent(tyreComponent, nodeComponent)
        } else if (vehicleComponent != null) {
            processVehicleComponent(vehicleComponent, nodeComponent)
        }
    }

    override fun end() {
        super.end()
    }

    fun setGravity(gravity: Vector3) {
        bulletWorld!!.gravity = gravity
    }

    fun setDebugDrawer(debugDrawer: DebugDrawer) {
        bulletWorld!!.debugDrawer = debugDrawer
    }

    fun debugDraw() {
        bulletWorld!!.debugDrawWorld()
    }

    private fun initPhysics() {
        Bullet.init()

        collisionConfiguration = btDefaultCollisionConfiguration()
        collisionDispatcher = btCollisionDispatcher(collisionConfiguration)
        dbvtBroadphase = btDbvtBroadphase()
        constraintSolver = btSequentialImpulseConstraintSolver()

        bulletWorld = btDiscreteDynamicsWorld(collisionDispatcher, dbvtBroadphase, constraintSolver, collisionConfiguration)
        bulletWorld!!.gravity = Vector3(0f, -1f, 0f)

        isPhysicsInitialized = true
    }

    private fun processBodyComponent(bodyComponent: RigidBodyComponent, nodeComponent: NodeComponent) {
        if (bodyComponent.state == RigidBodyComponent.State.READY) {
            if (!bodyComponent.isAdded) {
                val initialTransform = Matrix4()

                val translation = nodeComponent.node.translation
                val scale = Vector3(1f, 1f, 1f)
                val rotation = nodeComponent.node.rotation

                initialTransform.set(translation, rotation, scale)
                bodyComponent.addToWorld(bulletWorld, initialTransform)
            } else {
                if (bodyComponent.kinematic) {
                    bodyComponent.transform = nodeComponent.transform
                } else {
                    nodeComponent.translation = bodyComponent.translation
                    nodeComponent.setRotation(bodyComponent.rotation)
                    nodeComponent.applyTransforms()
                }
            }
        }
    }

    private fun processVehicleComponent(vehicleComponent: VehicleComponent, nodeComponent: NodeComponent) {
        if (vehicleComponent.state == RigidBodyComponent.State.READY) {
            if (!vehicleComponent.isAdded) {
                val initialTransform = Matrix4()

                val trn = nodeComponent.translation
                val scl = Vector3(1f, 1f, 1f)
                val rtn = nodeComponent.rotationQuaternion

                initialTransform.set(trn, rtn, scl)

                vehicleComponent.addToWorld(bulletWorld!!, initialTransform)
            } else {
                nodeComponent.translation = vehicleComponent.translation
                nodeComponent.setRotation(vehicleComponent.rotation)
                nodeComponent.applyTransforms()
            }
        }
    }

    private fun processWheelComponent(tyreComponent: TyreComponent, nodeComponent: NodeComponent) {
        if (tyreComponent.state == TyreComponent.State.READY) {
            val rotation = tyreComponent.getRotation() * MathUtils.radiansToDegrees % 360
            val steering = tyreComponent.steering * MathUtils.radiansToDegrees

            nodeComponent.setRotation(rotation, steering, 0f)
            nodeComponent.applyTransforms()
        }
    }

    companion object {
        var TIME_STEP = 1f / 60f
    }
}
