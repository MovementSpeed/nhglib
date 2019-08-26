package io.github.movementspeed.nhglib.core.ecs.components.scenes

import com.artemis.PooledComponent
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3

/**
 * Created by Fausto Napoli on 08/12/2016.
 * The base component needed by the SceneGraph for entity positioning.
 * Should only be created by the SceneGraph together with an entity.
 */
class NodeComponent : PooledComponent() {
    /**
     * Unique NodeComponent ID, corresponds with entity itself
     */
    var id: Int = 0
        set(value) {
            field = value
            node.id = "node_$value"
        }

    /**
     * Node
     */
    var node = Node()

    /**
     * Parent NodeComponent, can be null.
     */
    var parentNodeComponent: NodeComponent? = null

    var parentInternalNodeId: String? = null

    private val tempVec = Vector3()
    private val tempVec2 = Vector3()
    private val tempQuat = Quaternion()

    private val translation = Vector3()
    private val rotation = Vector3()
    private val scale = Vector3(1f, 1f, 1f)

    private val localTranslation = Vector3()
    private val localRotation = Vector3()
    private val localScale = Vector3(1f, 1f, 1f)

    private val translationDelta = Vector3()
    private val rotationDelta = Vector3()
    private val scaleDelta = Vector3()

    private val localRotationQuaternion = Quaternion()
    private val rotationQuaternion = Quaternion()

    val localX: Float
        get() = getLocalTranslation().x

    val localY: Float
        get() = getLocalTranslation().y

    val localZ: Float
        get() = getLocalTranslation().z

    val localXRotation: Float
        get() = getLocalRotation().x

    val localYRotation: Float
        get() = getLocalRotation().y

    val localZRotation: Float
        get() = getLocalRotation().z

    val localXScale: Float
        get() = getLocalScale().x

    val localYScale: Float
        get() = getLocalScale().y

    val localZScale: Float
        get() = getLocalScale().z

    val x: Float
        get() = getTranslation().x

    val y: Float
        get() = getTranslation().y

    val z: Float
        get() = getTranslation().z

    val xRotation: Float
        get() = getRotation().x

    val yRotation: Float
        get() = getRotation().y

    val zRotation: Float
        get() = getRotation().z

    val xScale: Float
        get() = getScale().x

    val yScale: Float
        get() = getScale().y

    val zScale: Float
        get() = getScale().z

    val xDelta: Float
        get() {
            val res = translationDelta.x
            translationDelta.x = 0f
            return res
        }

    val yDelta: Float
        get() {
            val res = translationDelta.y
            translationDelta.y = 0f
            return res
        }

    val zDelta: Float
        get() {
            val res = translationDelta.z
            translationDelta.z = 0f
            return res
        }

    val xRotationDelta: Float
        get() {
            val res = rotationDelta.x
            rotationDelta.x = 0f
            return res
        }

    val yRotationDelta: Float
        get() {
            val res = rotationDelta.y
            rotationDelta.y = 0f
            return res
        }

    val zRotationDelta: Float
        get() {
            val res = rotationDelta.z
            rotationDelta.z = 0f
            return res
        }

    val xScaleDelta: Float
        get() {
            val res = scaleDelta.x
            scaleDelta.x = 0f
            return res
        }

    val yScaleDelta: Float
        get() {
            val res = scaleDelta.y
            scaleDelta.y = 0f
            return res
        }

    val zScaleDelta: Float
        get() {
            val res = scaleDelta.z
            scaleDelta.z = 0f
            return res
        }

    val localTransform: Matrix4
        get() = node.localTransform

    var transform: Matrix4?
        get() = node.globalTransform
        set(transform) {
            if (transform != null) {
                transform.getTranslation(tempVec)
                transform.getRotation(tempQuat)
                transform.getScale(tempVec2)

                setTranslation(tempVec)
                setRotation(tempQuat)
                setScale(tempVec2)
            }
        }

    override fun reset() {
        node.translation.set(Vector3())
        node.rotation.set(Quaternion())
        node.scale.set(Vector3())

        translationDelta.set(Vector3.Zero)
        rotationDelta.set(Vector3.Zero)
        scaleDelta.set(Vector3.Zero)
    }

    @JvmOverloads
    fun setTranslation(translation: Vector3, apply: Boolean = false) {
        setTranslation(translation.x, translation.y, translation.z, apply)
    }

    @JvmOverloads
    fun setTranslation(x: Float, y: Float, z: Float, apply: Boolean = false) {
        translationDelta.set(
                translation.x - x,
                translation.y - y,
                translation.z - z)

        node.translation.set(x, y, z)

        if (apply) {
            applyTransforms()
        }
    }

    fun setTranslationX(x: Float) {
        translationDelta.x = translation.x - x
        node.translation.x = x
    }

    fun setTranslationY(y: Float) {
        translationDelta.y = translation.y - y
        node.translation.y = y
    }

    fun setTranslationZ(z: Float) {
        translationDelta.z = translation.z - z
        node.translation.z = z
    }

    @JvmOverloads
    fun translate(translation: Vector3, apply: Boolean = false) {
        translate(translation.x, translation.y, translation.z, apply)
    }

    /**
     * Translates the node.
     *
     * @param x     translation.
     * @param y     translation.
     * @param z     translation.
     * @param apply if true, transforms will be calculated immediately. It's not recommended, instead use
     * [applyTransforms()][.applyTransforms] after you've completed all transforms on the node.
     */
    @JvmOverloads
    fun translate(x: Float, y: Float, z: Float, apply: Boolean = false) {
        translationDelta.set(
                translation.x - x,
                translation.y - y,
                translation.z - z)

        tempVec.set(x, y, z)

        val len = tempVec.len()
        tempVec.rot(node.localTransform).nor().scl(len)

        node.translation.add(tempVec)

        if (apply) {
            applyTransforms()
        }
    }

    @JvmOverloads
    fun setRotation(rotation: Vector3, apply: Boolean = false) {
        setRotation(rotation.x, rotation.y, rotation.z, apply)
    }

    @JvmOverloads
    fun setRotation(x: Float, y: Float, z: Float, apply: Boolean = false) {
        rotationDelta.set(
                rotation.x - x,
                rotation.y - y,
                rotation.z - z)

        node.rotation.setEulerAngles(y, x, z)

        if (apply) {
            applyTransforms()
        }
    }

    fun setRotation(rotation: Quaternion) {
        rotationDelta.set(
                rotation.x - rotation.pitch,
                rotation.y - rotation.yaw,
                rotation.z - rotation.roll)

        node.rotation.set(rotation)
    }

    fun rotate(quaternion: Quaternion) {
        rotate(quaternion, false)
    }

    /**
     * Rotates the node.
     *
     * @param rotation the rotation quaternion added to the node's rotation.
     * @param apply    if true, transforms will be calculated immediately. It's not recommended, instead use
     * [applyTransforms()][.applyTransforms] after you've completed all transforms on the node.
     */
    fun rotate(rotation: Quaternion, apply: Boolean) {
        node.rotation.add(rotation)

        if (apply) {
            applyTransforms()
        }
    }

    @JvmOverloads
    fun rotate(rotation: Vector3, apply: Boolean = false) {
        rotate(rotation.x, rotation.y, rotation.z, apply)
    }

    /**
     * Rotates the node.
     *
     * @param x     rotation, also known as pitch.
     * @param y     rotation, also known as yaw.
     * @param z     rotation, also known as roll.
     * @param apply if true, transforms will be calculated immediately. It's not recommended, instead use
     * [applyTransforms()][.applyTransforms] after you've completed all transforms on the node.
     */
    @JvmOverloads
    fun rotate(x: Float, y: Float, z: Float, apply: Boolean = false) {
        rotationDelta.set(
                rotation.x - x,
                rotation.y - y,
                rotation.z - z)

        tempQuat.setEulerAngles(y, x, z)
        node.rotation.mul(tempQuat)

        if (apply) {
            applyTransforms()
        }
    }

    @JvmOverloads
    fun setScale(scale: Vector3, apply: Boolean = false) {
        setScale(scale.x, scale.y, scale.z, apply)
    }

    @JvmOverloads
    fun setScale(x: Float, y: Float, z: Float, apply: Boolean = false) {
        scaleDelta.set(
                scale.x - x,
                scale.y - y,
                scale.z - z)

        node.scale.set(x, y, z)

        if (apply) {
            applyTransforms()
        }
    }

    @JvmOverloads
    fun scale(scale: Vector3, apply: Boolean = false) {
        scale(scale.x, scale.y, scale.y, apply)
    }

    /**
     * Scales the node.
     *
     * @param x     scale.
     * @param y     scale.
     * @param z     scale.
     * @param apply if true, transforms will be calculated immediately. It's not recommended, instead use
     * [applyTransforms()][.applyTransforms] after you've completed all transforms on the node.
     */
    @JvmOverloads
    fun scale(x: Float, y: Float, z: Float, apply: Boolean = false) {
        scaleDelta.set(
                scale.x - x,
                scale.y - y,
                scale.z - z)

        node.scale.add(x, y, z)

        if (apply) {
            applyTransforms()
        }
    }

    fun setTransform(translation: Vector3, rotation: Vector3, scale: Vector3) {
        setTranslation(translation)
        setRotation(rotation)
        setScale(scale)
    }

    @JvmOverloads
    fun applyTransforms(recursive: Boolean = true) {
        node.calculateTransforms(recursive)
    }

    fun getLocalTranslation(): Vector3 {
        node.localTransform.getTranslation(localTranslation)
        return localTranslation
    }

    fun getTranslation(): Vector3 {
        node.globalTransform.getTranslation(translation)
        return translation
    }

    fun getLocalRotation(): Vector3 {
        node.localTransform.getRotation(localRotationQuaternion)
        localRotation.set(
                localRotationQuaternion.pitch,
                localRotationQuaternion.yaw,
                localRotationQuaternion.roll)

        return localRotation
    }

    fun getRotation(): Vector3 {
        node.globalTransform.getRotation(rotationQuaternion)
        rotation.set(
                rotationQuaternion.pitch,
                rotationQuaternion.yaw,
                rotationQuaternion.roll)

        return rotation
    }

    fun getLocalScale(): Vector3 {
        node.localTransform.getScale(localScale)
        return localScale
    }

    fun getScale(): Vector3 {
        node.globalTransform.getScale(scale)
        return scale
    }

    fun getTranslationDelta(): Vector3 {
        val res = tempVec.set(translationDelta)
        translationDelta.set(Vector3.Zero)

        return res
    }

    fun getRotationDelta(): Vector3 {
        val res = tempVec.set(rotationDelta)
        rotationDelta.set(Vector3.Zero)

        return res
    }

    fun getScaleDelta(): Vector3 {
        val res = tempVec.set(scaleDelta)
        scaleDelta.set(Vector3.Zero)

        return res
    }

    fun getLocalRotationQuaternion(): Quaternion {
        getLocalRotation()
        return localRotationQuaternion
    }

    fun getRotationQuaternion(): Quaternion {
        getRotation()
        return rotationQuaternion
    }
}
