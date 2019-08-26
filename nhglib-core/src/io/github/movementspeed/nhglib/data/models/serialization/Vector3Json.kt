package io.github.movementspeed.nhglib.data.models.serialization

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.interfaces.JsonParseable

/**
 * Created by Fausto Napoli on 11/06/2017.
 */
class Vector3Json : JsonParseable<Vector3> {
    private var vec: Vector3? = null

    override fun parse(jsonValue: JsonValue) {
        vec = Vector3(
                jsonValue.getFloat("x"),
                jsonValue.getFloat("y"),
                jsonValue.getFloat("z"))
    }

    override fun get(): Vector3? {
        return vec
    }
}
