package io.github.movementspeed.nhglib.graphics.shaders.attributes

import com.badlogic.gdx.graphics.Cubemap
import com.badlogic.gdx.graphics.GLTexture
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor
import com.badlogic.gdx.utils.GdxRuntimeException

/**
 * Created by Fausto Napoli on 16/08/2017.
 */
class IBLAttribute(type: Long) : Attribute(type) {

    val textureDescription: TextureDescriptor<GLTexture>
    /**
     * The index of the texture coordinate vertex attribute to use for this IBLAttribute. Whether this value is used, depends
     * on the shader and [Attribute.type] value. For basic (model specific) types (e.g. [.IrradianceType],
     * etc.), this value is usually ignored and the first texture coordinate vertex attribute is used.
     */
    var uvIndex = 0

    init {
        if (!`is`(type)) throw GdxRuntimeException("Invalid type specified")
        textureDescription = TextureDescriptor()
    }

    constructor(type: Long, textureDescription: TextureDescriptor<T>) : this(type) {
        this.textureDescription.set<T>(textureDescription)
    }

    constructor(type: Long, textureDescription: TextureDescriptor<T>, uvIndex: Int) : this<T>(type, textureDescription)
    {
        this.uvIndex = uvIndex
    }

    constructor(type: Long, texture: Cubemap) : this(type) {
        textureDescription.texture = texture
    }

    constructor(type: Long, texture: Texture) : this(type) {
        textureDescription.texture = texture
    }

    fun set(cubemap: Cubemap) {
        textureDescription.texture = cubemap
    }

    override fun copy(): Attribute? {
        return null
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 991 * result + textureDescription.hashCode()
        result = 991 * result + uvIndex
        return result
    }

    override fun compareTo(o: Attribute): Int {
        if (type != o.type) return if (type < o.type) -1 else 1
        val other = o as IBLAttribute
        val c = textureDescription.compareTo(other.textureDescription)
        if (c != 0) return c
        return if (uvIndex != other.uvIndex) uvIndex - other.uvIndex else 0
    }

    companion object {
        val IrradianceAlias = "irradianceTexture"
        val PrefilterAlias = "prefilterTexture"
        val BrdfAlias = "brdfTexture"

        val IrradianceType = Attribute.register(IrradianceAlias)
        val PrefilterType = Attribute.register(PrefilterAlias)
        val BrdfType = Attribute.register(BrdfAlias)

        protected var Mask = IrradianceType or PrefilterType or BrdfType

        fun `is`(mask: Long): Boolean {
            return mask and Mask != 0L
        }

        fun createIrradiance(cubemap: Cubemap): IBLAttribute {
            return IBLAttribute(IrradianceType, cubemap)
        }

        fun createPrefilter(cubemap: Cubemap): IBLAttribute {
            return IBLAttribute(PrefilterType, cubemap)
        }

        fun createBrdf(texture: Texture): IBLAttribute {
            return IBLAttribute(BrdfType, texture)
        }
    }
}
