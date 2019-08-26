package io.github.movementspeed.nhglib.graphics.lights

import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.utils.Array

/**
 * Created by Fausto Napoli on 18/03/2017.
 */
class NhgLightsAttribute() : Attribute(Type) {

    val lights: Array<NhgLight>

    init {
        lights = Array(1)
    }

    constructor(copyFrom: NhgLightsAttribute) : this() {
        lights.addAll(copyFrom.lights)
    }

    override fun copy(): NhgLightsAttribute {
        return NhgLightsAttribute(this)
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        for (light in lights)
            result = 1237 * result + (light?.hashCode() ?: 0)
        return result
    }

    override fun compareTo(o: Attribute): Int {
        return if (type != o.type) if (type < o.type) -1 else 1 else 0
    }

    companion object {
        val Alias = "nhgLights"
        val Type = Attribute.register(Alias)

        fun `is`(mask: Long): Boolean {
            return mask and Type == mask
        }
    }
}
