package io.github.movementspeed.nhglib.graphics.shaders.attributes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.utils.GdxRuntimeException

class PBRColorAttribute(type: Long) : Attribute(type) {

    val color = Color()

    init {
        if (!`is`(type)) throw GdxRuntimeException("Invalid type specified")
    }

    constructor(type: Long, color: Color?) : this(type) {
        if (color != null) this.color.set(color)
    }

    constructor(type: Long, r: Float, g: Float, b: Float, a: Float) : this(type) {
        this.color.set(r, g, b, a)
    }

    constructor(copyFrom: PBRColorAttribute) : this(copyFrom.type, copyFrom.color) {}

    override fun copy(): Attribute {
        return PBRColorAttribute(this)
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 953 * result + color.toIntBits()
        return result
    }

    override fun compareTo(o: Attribute): Int {
        return if (type != o.type) (type - o.type).toInt() else (o as PBRColorAttribute).color.toIntBits() - color.toIntBits()
    }

    companion object {
        val AlbedoColorAlias = "albedoColor"
        val MetalnessValueAlias = "metalnessValue"
        val RoughnessValueAlias = "roughnessValue"

        val AlbedoColor = Attribute.register(AlbedoColorAlias)
        val MetalnessValue = Attribute.register(MetalnessValueAlias)
        val RoughnessValue = Attribute.register(RoughnessValueAlias)

        protected var Mask = AlbedoColor or MetalnessValue or RoughnessValue

        fun `is`(mask: Long): Boolean {
            return mask and Mask != 0L
        }

        fun createAlbedo(color: Color): PBRColorAttribute {
            return PBRColorAttribute(AlbedoColor, color)
        }

        fun createMetalness(metalness: Float): PBRColorAttribute {
            return PBRColorAttribute(MetalnessValue, Color(metalness, metalness, metalness, 1.0f))
        }

        fun createRoughness(roughness: Float): PBRColorAttribute {
            return PBRColorAttribute(RoughnessValue, Color(roughness, roughness, roughness, 1.0f))
        }
    }
}
