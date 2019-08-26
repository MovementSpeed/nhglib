package io.github.movementspeed.nhglib.graphics.shaders.attributes

import com.badlogic.gdx.graphics.g3d.Attribute

/**
 * Created by Fausto Napoli on 01/04/2017.
 */
class GammaCorrectionAttribute(var gammaCorrection: Boolean) : Attribute(Type) {

    override fun copy(): Attribute {
        return GammaCorrectionAttribute(gammaCorrection)
    }

    override fun compareTo(o: Attribute): Int {
        return 0
    }

    companion object {
        val Alias = "gammaCorrection"
        val Type = Attribute.register(Alias)
    }
}
