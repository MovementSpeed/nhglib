package io.github.movementspeed.nhglib.core.ecs.components.graphics

import com.artemis.Component
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.graphics.g3d.model.NodePart
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.graphics.utils.PBRMaterial

/**
 * Created by Fausto Napoli on 28/03/2017.
 */
class ModelComponent : Component() {
    var enabled: Boolean = false
    var nodeAdded: Boolean = false
    var cached: Boolean = false

    var radius: Float = 0.toFloat()

    var type: Type
    var state: State
    var asset: String
    var model: ModelInstance
    var boundingBox: BoundingBox
    var animationController: AnimationController

    var pbrMaterials: Array<PBRMaterial>

    private val translationBefore: Vector3
    private val translationAfter: Vector3
    private val scaleBefore: Vector3
    private val scaleAfter: Vector3
    private val rotationBefore: Vector3
    private val rotationAfter: Vector3
    private val rotationQuaternionBefore: Quaternion
    private val rotationQuaternionAfter: Quaternion

    init {
        pbrMaterials = Array()
        enabled = true
        nodeAdded = false
        state = State.NOT_INITIALIZED
        type = Type.DYNAMIC

        translationBefore = Vector3()
        translationAfter = Vector3()
        scaleBefore = Vector3()
        scaleAfter = Vector3()
        rotationBefore = Vector3()
        rotationAfter = Vector3()
        rotationQuaternionBefore = Quaternion()
        rotationQuaternionAfter = Quaternion()
    }

    fun buildWithModel(m: Model) {
        buildWithModel(ModelInstance(m))
    }

    fun buildWithModel(nodeScale: Vector3, m: Model) {
        buildWithModel(nodeScale, ModelInstance(m))
    }

    fun buildWithModel(m: ModelInstance) {
        buildWithModel(Vector3(1f, 1f, 1f), m)
    }

    fun buildWithModel(nodeScale: Vector3, m: ModelInstance) {
        model = m
        boundingBox = BoundingBox()
        model.calculateBoundingBox(boundingBox)

        val dimensions = boundingBox.getDimensions(Vector3())
        dimensions.scl(nodeScale)
        radius = dimensions.len() / 2f

        state = ModelComponent.State.READY

        if (m.animations.size > 0) {
            animationController = AnimationController(model)
        }
    }

    fun calculateTransforms() {
        if (type == Type.STATIC) {
            model.transform.getTranslation(translationBefore)
            model.transform.getScale(scaleBefore)
            model.transform.getRotation(rotationQuaternionBefore)
            rotationBefore.set(rotationQuaternionBefore.pitch, rotationQuaternionBefore.yaw, rotationQuaternionBefore.roll)
        }

        model.calculateTransforms()

        if (type == Type.STATIC) {
            model.transform.getTranslation(translationAfter)
            model.transform.getScale(scaleAfter)
            model.transform.getRotation(rotationQuaternionAfter)
            rotationAfter.set(rotationQuaternionAfter.pitch, rotationQuaternionAfter.yaw, rotationQuaternionAfter.roll)

            translationBefore.sub(translationAfter)
            scaleBefore.sub(scaleAfter)
            rotationBefore.sub(rotationAfter)

            if (translationBefore.x != 0f || translationBefore.y != 0f || translationBefore.z != 0f ||
                    scaleBefore.x != 0f || scaleBefore.y != 0f || scaleBefore.z != 0f ||
                    rotationBefore.x != 0f || rotationBefore.y != 0f || rotationBefore.z != 0f) {
                cached = false
            }
        }
    }

    fun buildWithAsset(asset: String) {
        this.asset = asset
        this.state = State.NOT_INITIALIZED
    }

    fun setPBRMaterial(material: PBRMaterial) {
        for (m in model.materials) {
            m.set(material)
        }
    }

    fun setPBRMaterial(index: Int, material: PBRMaterial) {
        model.materials.get(index).set(material)
    }

    fun setPBRMaterial(nodeId: String, material: PBRMaterial) {
        val targetNode = model.getNode(nodeId)

        for (nodePart in targetNode.parts) {
            nodePart.material.set(material)
        }
    }

    enum class State {
        NOT_INITIALIZED,
        READY
    }

    enum class Type {
        STATIC,
        DYNAMIC;


        companion object {

            fun fromString(value: String): Type? {
                var type: Type? = null

                if (value.contentEquals("dynamic")) {
                    type = DYNAMIC
                } else if (value.contentEquals("static")) {
                    type = STATIC
                }

                return type
            }
        }
    }
}
