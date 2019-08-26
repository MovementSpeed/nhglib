package io.github.movementspeed.nhglib.data.models.serialization

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.interfaces.JsonParseable

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
class TransformJson : JsonParseable<TransformJson> {
    var position: Vector3
    var rotation: Vector3
    var scale: Vector3

    init {
        position = Vector3()
        rotation = Vector3()
        scale = Vector3()
    }

    override fun parse(jsonValue: JsonValue) {
        val positionJson = jsonValue.get("position")
        val rotationJson = jsonValue.get("rotation")
        val scaleJson = jsonValue.get("scale")

        var xPosition = 0f
        var yPosition = 0f
        var zPosition = 0f

        var xRotation = 0f
        var yRotation = 0f
        var zRotation = 0f

        var xScale = 1f
        var yScale = 1f
        var zScale = 1f

        if (positionJson != null) {
            xPosition = positionJson.getFloat("x")
            yPosition = positionJson.getFloat("y")
            zPosition = positionJson.getFloat("z")
        }

        if (rotationJson != null) {
            xRotation = rotationJson.getFloat("x")
            yRotation = rotationJson.getFloat("y")
            zRotation = rotationJson.getFloat("z")
        }

        if (scaleJson != null) {
            xScale = scaleJson.getFloat("x")
            yScale = scaleJson.getFloat("y")
            zScale = scaleJson.getFloat("z")
        }

        position.set(xPosition, yPosition, zPosition)
        rotation.set(xRotation, yRotation, zRotation)
        scale.set(xScale, yScale, zScale)
    }

    override fun get(): TransformJson {
        return this
    }
}
