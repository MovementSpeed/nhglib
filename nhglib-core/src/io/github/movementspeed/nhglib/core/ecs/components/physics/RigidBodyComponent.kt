package io.github.movementspeed.nhglib.core.ecs.components.physics

import com.artemis.Component
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.utils.Disposable
import io.github.movementspeed.nhglib.assets.Assets
import io.github.movementspeed.nhglib.physics.MotionState
import io.github.movementspeed.nhglib.physics.enums.RigidBodyType
import io.github.movementspeed.nhglib.physics.models.*

import com.badlogic.gdx.physics.bullet.collision.CollisionConstants.DISABLE_DEACTIVATION
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT

/**
 * Created by Fausto Napoli on 03/05/2017.
 */
open class RigidBodyComponent : Component(), Disposable {
    var isAdded: Boolean = false
    var collisionFiltering: Boolean = false
    var kinematic: Boolean = false

    var group: Short = 0
    var mask: Short = 0

    var mass: Float = 0.toFloat()
    var friction: Float = 0.toFloat()
    var restitution: Float = 0.toFloat()

    var state: State

    var body: btRigidBody? = null
    var motionState: MotionState? = null
    var rigidBodyShape: RigidBodyShape? = null
    var collisionShape: btCollisionShape? = null
    var constructionInfo: btRigidBody.btRigidBodyConstructionInfo? = null

    private val translation: Vector3
    private val scale: Vector3
    private val rotation: Quaternion
    private val initialTransform: Matrix4

    var transform: Matrix4
        get() = motionState!!.transform
        set(transform) {
            motionState!!.transform.set(transform)
        }

    init {
        state = State.NOT_INITIALIZED

        translation = Vector3()
        scale = Vector3()
        rotation = Quaternion()
        initialTransform = Matrix4()
    }

    override fun dispose() {
        if (body != null) {
            body!!.dispose()
        }

        if (motionState != null) {
            motionState!!.dispose()
        }

        if (collisionShape != null) {
            collisionShape!!.dispose()
        }

        if (constructionInfo != null) {
            constructionInfo!!.dispose()
        }
    }

    @JvmOverloads
    fun build(collisionShape: btCollisionShape, mass: Float, friction: Float = 0.5f, restitution: Float = 0f, group: Short = (-1).toShort(), masks: ShortArray = shortArrayOf()) {
        this.collisionShape = collisionShape
        buildBody(mass, friction, restitution)
    }

    fun build(assets: Assets) {
        buildCollisionShape(assets)
        buildBody(mass, friction, restitution)
    }

    open fun addToWorld(world: btDynamicsWorld, transform: Matrix4) {
        if (body != null && !body!!.isInWorld) {
            initialTransform.set(transform)
            transform = transform
            body!!.motionState = motionState

            if (collisionFiltering) {
                world.addRigidBody(body, group.toInt(), mask.toInt())
            } else {
                world.addRigidBody(body)
            }

            isAdded = true
        }
    }

    fun setWorldTransform(transform: Matrix4) {
        body!!.worldTransform = transform
    }

    fun reset() {
        setWorldTransform(initialTransform)
    }

    fun getTranslation(): Vector3 {
        return motionState!!.transform.getTranslation(translation)
    }

    fun getScale(): Vector3 {
        return motionState!!.transform.getScale(scale)
    }

    fun getRotation(): Quaternion {
        return motionState!!.transform.getRotation(rotation)
    }

    private fun buildBody(mass: Float, friction: Float, restitution: Float) {
        constructionInfo = getConstructionInfo(collisionShape, mass)

        if (constructionInfo != null) {
            motionState = MotionState()

            body = btRigidBody(constructionInfo!!)

            if (kinematic) {
                body!!.collisionFlags = body!!.collisionFlags or CF_KINEMATIC_OBJECT
                body!!.activationState = DISABLE_DEACTIVATION
            } else {
                body!!.setSleepingThresholds(1f / 1000f, 1f / 1000f)
                body!!.friction = friction
                body!!.restitution = restitution

                if (rigidBodyShape!!.type == RigidBodyType.BVH_TRIANGLE_MESH) {
                    body!!.collisionFlags = body!!.collisionFlags or CF_CUSTOM_MATERIAL_CALLBACK
                }
            }
        }
    }

    private fun buildCollisionShape(assets: Assets) {
        when (rigidBodyShape!!.type) {
            RigidBodyType.BOX -> {
                val boxRigidBodyShape = rigidBodyShape as BoxRigidBodyShape?
                collisionShape = btBoxShape(Vector3(boxRigidBodyShape!!.width, boxRigidBodyShape.height, boxRigidBodyShape.depth))
            }

            RigidBodyType.CONE -> {
                val coneRigidBodyShape = rigidBodyShape as ConeRigidBodyShape?
                collisionShape = btConeShape(coneRigidBodyShape!!.radius, coneRigidBodyShape.height)
            }

            RigidBodyType.SPHERE -> {
                val sphereRigidBodyShape = rigidBodyShape as SphereRigidBodyShape?
                collisionShape = btSphereShape(sphereRigidBodyShape!!.radius)
            }

            RigidBodyType.CAPSULE -> {
                val capsuleRigidBodyShape = rigidBodyShape as CapsuleRigidBodyShape?
                collisionShape = btCapsuleShape(capsuleRigidBodyShape!!.radius, capsuleRigidBodyShape.height)
            }

            RigidBodyType.CYLINDER -> {
                val cylinderRigidBodyShape = rigidBodyShape as CylinderRigidBodyShape?
                collisionShape = btCylinderShape(Vector3(cylinderRigidBodyShape!!.width, cylinderRigidBodyShape.height, cylinderRigidBodyShape.depth))
            }

            RigidBodyType.CONVEX_HULL -> {
                val convexHullRigidBodyShape = rigidBodyShape as ConvexHullRigidBodyShape?

                val convexHull = assets.get<Model>(convexHullRigidBodyShape!!.asset)
                val convexHullMesh = convexHull!!.meshes.first()

                collisionShape = btConvexHullShape(convexHullMesh.verticesBuffer, convexHullMesh.numVertices, convexHullMesh.vertexSize)

                if (convexHullRigidBodyShape.optimize) {
                    (collisionShape as btConvexHullShape).optimizeConvexHull()
                }
            }

            RigidBodyType.BVH_TRIANGLE_MESH -> {
                val bvhTriangleMeshRigidBodyShape = rigidBodyShape as BvhTriangleMeshRigidBodyShape?
                val bvhTriangleModel = assets.get<Model>(bvhTriangleMeshRigidBodyShape!!.asset)
                collisionShape = btBvhTriangleMeshShape(bvhTriangleModel!!.meshParts, bvhTriangleMeshRigidBodyShape.quantization, bvhTriangleMeshRigidBodyShape.buildBvh)

                val triangleInfoMap = btTriangleInfoMap()
                Collision.btGenerateInternalEdgeInfo(collisionShape as btBvhTriangleMeshShape?, triangleInfoMap)
                triangleInfoMap.dispose()
            }

            RigidBodyType.CONVEX_TRIANGLE_MESH -> {
                val convexTriangleMeshRigidBodyShape = rigidBodyShape as ConvexTriangleMeshRigidBodyShape?
                val convexTriangleModel = assets.get<Model>(convexTriangleMeshRigidBodyShape!!.asset)
                collisionShape = btConvexTriangleMeshShape(btTriangleIndexVertexArray.obtain<MeshPart>(convexTriangleModel!!.meshParts), convexTriangleMeshRigidBodyShape.calcAabb)
            }
        }
    }

    private fun getConstructionInfo(shape: btCollisionShape?, mass: Float): btRigidBody.btRigidBodyConstructionInfo? {
        var info: btRigidBody.btRigidBodyConstructionInfo? = null

        if (shape != null && mass >= 0) {
            val localInertia = Vector3()

            if (mass > 0f) {
                shape.calculateLocalInertia(mass, localInertia)
            } else {
                localInertia.set(Vector3.Zero)
            }

            info = btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia)
        }

        return info
    }

    enum class State {
        NOT_INITIALIZED,
        READY
    }
}
