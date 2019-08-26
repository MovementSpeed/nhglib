package io.github.movementspeed.nhglib.graphics.shaders.attributes

import com.badlogic.gdx.graphics.g3d.Attribute

/**
 * Created by Fausto Napoli on 01/04/2017.
 */
class AmbientLightingAttribute(var ambient: Float) : Attribute(Type) {

    override fun copy(): Attribute {
        return AmbientLightingAttribute(ambient)
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 991 * result + ambient.toInt()
        return result
    }

    override fun compareTo(o: Attribute): Int {
        if (type != o.type) return if (type < o.type) -1 else 1
        val other = o as AmbientLightingAttribute
        return if (other.ambient != ambient) if (other.ambient < ambient) 1 else -1 else 0
    }

    companion object {
        val Alias = "ambientLighting"
        val Type = Attribute.register(Alias)
    }
}