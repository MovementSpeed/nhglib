package io.github.movementspeed.nhglib.graphics.shaders.attributes

import com.badlogic.gdx.graphics.g3d.Attribute
import io.github.movementspeed.nhglib.graphics.shaders.shadows.system.ShadowSystem

class ShadowSystemAttribute(var shadowSystem: ShadowSystem) : Attribute(Type) {

    override fun copy(): Attribute {
        return ShadowSystemAttribute(shadowSystem)
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 991 * result + shadowSystem.hashCode()
        return result
    }

    override fun compareTo(o: Attribute): Int {
        if (type != o.type) return if (type < o.type) -1 else 1
        val other = o as ShadowSystemAttribute
        return if (other.shadowSystem !== shadowSystem)
            1
        else
            -1
    }

    companion object {
        val Alias = "shadowSystem"
        val Type = Attribute.register(Alias)
    }
}