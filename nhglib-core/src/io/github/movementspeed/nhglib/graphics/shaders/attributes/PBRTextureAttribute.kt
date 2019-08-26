package io.github.movementspeed.nhglib.graphics.shaders.attributes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.NumberUtils

/**
 * Created by Fausto Napoli on 23/03/2017.
 */
class PBRTextureAttribute(type: Long) : Attribute(type) {

    /*public static PBRTextureAttribute createAmbientOcclusion(final Texture texture) {
        return new PBRTextureAttribute(AmbientOcclusion, texture);
    }

    public static PBRTextureAttribute createAmbientOcclusion(final TextureRegion region) {
        return new PBRTextureAttribute(AmbientOcclusion, region);
    }

    public static PBRTextureAttribute createAmbientOcclusion(final Texture texture, float offsetU, float offsetV, float tilesU, float tilesV) {
        TextureDescriptor textureDescriptor = new TextureDescriptor();
        textureDescriptor.texture = texture;

        return new PBRTextureAttribute(AmbientOcclusion, textureDescriptor, offsetU, offsetV, tilesU, tilesV);
    }*/

    val textureDescription: TextureDescriptor<Texture>

    var offsetU = 0f
    var offsetV = 0f
    var tilesU = 1f
    var tilesV = 1f
    /**
     * The index of the texture coordinate vertex attribute to use for this PBRTextureAttribute. Whether this value is used, depends
     * on the shader and [Attribute.type] value. For basic (model specific) types (e.g. [.Albedo],
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

    constructor(type: Long, textureDescription: TextureDescriptor<T>, offsetU: Float,
                offsetV: Float, tilesU: Float, tilesV: Float, uvIndex: Int) : this<T>(type, textureDescription)
    {
        this.offsetU = offsetU
        this.offsetV = offsetV
        this.tilesU = tilesU
        this.tilesV = tilesV
        this.uvIndex = uvIndex
    }

    constructor(type: Long, textureDescription: TextureDescriptor<T>, offsetU: Float,
                offsetV: Float, tilesU: Float, tilesV: Float) : this<T>(type, textureDescription, offsetU, offsetV, tilesU, tilesV, 0)
    {}

    constructor(type: Long, texture: Texture) : this(type) {
        textureDescription.texture = texture
    }

    constructor(type: Long, region: TextureRegion) : this(type) {
        set(region)
    }

    constructor(copyFrom: PBRTextureAttribute) : this<Texture>(copyFrom.type, copyFrom.textureDescription, copyFrom.offsetU, copyFrom.offsetV, copyFrom.tilesU, copyFrom.tilesV,
    copyFrom.uvIndex)
    {}

    fun set(region: TextureRegion) {
        textureDescription.texture = region.texture
        offsetU = region.u
        offsetV = region.v
        tilesU = region.u2 - offsetU
        tilesV = region.v2 - offsetV
    }

    override fun copy(): Attribute {
        return PBRTextureAttribute(this)
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 991 * result + textureDescription.hashCode()
        result = 991 * result + NumberUtils.floatToRawIntBits(offsetU)
        result = 991 * result + NumberUtils.floatToRawIntBits(offsetV)
        result = 991 * result + NumberUtils.floatToRawIntBits(tilesU)
        result = 991 * result + NumberUtils.floatToRawIntBits(tilesV)
        result = 991 * result + uvIndex
        return result
    }

    override fun compareTo(o: Attribute): Int {
        if (type != o.type) return if (type < o.type) -1 else 1
        val other = o as PBRTextureAttribute
        val c = textureDescription.compareTo(other.textureDescription)
        if (c != 0) return c
        if (uvIndex != other.uvIndex) return uvIndex - other.uvIndex
        if (!MathUtils.isEqual(tilesU, other.tilesU)) return if (tilesU > other.tilesU) 1 else -1
        if (!MathUtils.isEqual(tilesV, other.tilesV)) return if (tilesV > other.tilesV) 1 else -1
        if (!MathUtils.isEqual(offsetU, other.offsetU)) return if (offsetU > other.offsetU) 1 else -1
        return if (!MathUtils.isEqual(offsetV, other.offsetV)) if (offsetV > other.offsetV) 1 else -1 else 0
    }

    companion object {
        val AlbedoAlias = "PBRAlbedoTexture"
        val NormalAlias = "PBRNormalTexture"
        val RMAAlias = "RMATexture"
        val EmissiveAlias = "PBREmissiveTexture"

        val Albedo = Attribute.register(AlbedoAlias)
        val Normal = Attribute.register(NormalAlias)
        val RMA = Attribute.register(RMAAlias)
        val Emissive = Attribute.register(EmissiveAlias)

        protected var Mask = Albedo or Normal or RMA or Emissive

        fun `is`(mask: Long): Boolean {
            return mask and Mask != 0L
        }

        fun createAlbedo(color: Color): PBRTextureAttribute {
            val pixmap = Pixmap(1, 1, Pixmap.Format.RGB888)
            pixmap.setColor(color)
            pixmap.drawPixel(0, 0)
            val texture = Texture(pixmap)
            return PBRTextureAttribute(Albedo, texture)
        }

        fun createAlbedo(texture: Texture): PBRTextureAttribute {
            return PBRTextureAttribute(Albedo, texture)
        }

        fun createAlbedo(region: TextureRegion): PBRTextureAttribute {
            return PBRTextureAttribute(Albedo, region)
        }

        fun createAlbedo(texture: Texture, offsetU: Float, offsetV: Float, tilesU: Float, tilesV: Float): PBRTextureAttribute {
            val textureDescriptor = TextureDescriptor()
            textureDescriptor.texture = texture

            return PBRTextureAttribute(Albedo, textureDescriptor, offsetU, offsetV, tilesU, tilesV)
        }

        fun createRMA(roughness: Float, metalness: Float, ambientOcclusion: Float): PBRTextureAttribute {
            val pixmap = Pixmap(1, 1, Pixmap.Format.RGB888)
            pixmap.setColor(roughness, metalness, ambientOcclusion, 1.0f)
            pixmap.drawPixel(0, 0)
            val texture = Texture(pixmap)
            return PBRTextureAttribute(RMA, texture)
        }

        fun createRMA(texture: Texture): PBRTextureAttribute {
            return PBRTextureAttribute(RMA, texture)
        }

        fun createRMA(region: TextureRegion): PBRTextureAttribute {
            return PBRTextureAttribute(RMA, region)
        }

        fun createRMA(texture: Texture, offsetU: Float, offsetV: Float, tilesU: Float, tilesV: Float): PBRTextureAttribute {
            val textureDescriptor = TextureDescriptor()
            textureDescriptor.texture = texture

            return PBRTextureAttribute(RMA, textureDescriptor, offsetU, offsetV, tilesU, tilesV)
        }

        fun createEmissive(texture: Texture): PBRTextureAttribute {
            return PBRTextureAttribute(Emissive, texture)
        }

        fun createEmissive(region: TextureRegion): PBRTextureAttribute {
            return PBRTextureAttribute(Emissive, region)
        }

        fun createEmissive(texture: Texture, offsetU: Float, offsetV: Float, tilesU: Float, tilesV: Float): PBRTextureAttribute {
            val textureDescriptor = TextureDescriptor()
            textureDescriptor.texture = texture

            return PBRTextureAttribute(Emissive, textureDescriptor, offsetU, offsetV, tilesU, tilesV)
        }

        /*public static PBRTextureAttribute createMetalness(final float metalness) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.setColor(metalness, metalness, metalness, 1.0f);
        pixmap.drawPixel(0, 0);
        Texture texture = new Texture(pixmap);
        return new PBRTextureAttribute(Metalness, texture);
    }

    public static PBRTextureAttribute createMetalness(final Texture texture) {
        return new PBRTextureAttribute(Metalness, texture);
    }

    public static PBRTextureAttribute createMetalness(final TextureRegion region) {
        return new PBRTextureAttribute(Metalness, region);
    }

    public static PBRTextureAttribute createMetalness(final Texture texture, float offsetU, float offsetV, float tilesU, float tilesV) {
        TextureDescriptor textureDescriptor = new TextureDescriptor();
        textureDescriptor.texture = texture;

        return new PBRTextureAttribute(Metalness, textureDescriptor, offsetU, offsetV, tilesU, tilesV);
    }

    public static PBRTextureAttribute createRoughness(final float roughness) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.setColor(roughness, roughness, roughness, 1.0f);
        pixmap.drawPixel(0, 0);
        Texture texture = new Texture(pixmap);
        return new PBRTextureAttribute(Roughness, texture);
    }

    public static PBRTextureAttribute createRoughness(final Texture texture) {
        return new PBRTextureAttribute(Roughness, texture);
    }

    public static PBRTextureAttribute createRoughness(final TextureRegion region) {
        return new PBRTextureAttribute(Roughness, region);
    }

    public static PBRTextureAttribute createRoughness(final Texture texture, float offsetU, float offsetV, float tilesU, float tilesV) {
        TextureDescriptor textureDescriptor = new TextureDescriptor();
        textureDescriptor.texture = texture;

        return new PBRTextureAttribute(Roughness, textureDescriptor, offsetU, offsetV, tilesU, tilesV);
    }*/

        fun createNormal(texture: Texture): PBRTextureAttribute {
            return PBRTextureAttribute(Normal, texture)
        }

        fun createNormal(region: TextureRegion): PBRTextureAttribute {
            return PBRTextureAttribute(Normal, region)
        }

        fun createNormal(texture: Texture, offsetU: Float, offsetV: Float, tilesU: Float, tilesV: Float): PBRTextureAttribute {
            val textureDescriptor = TextureDescriptor()
            textureDescriptor.texture = texture

            return PBRTextureAttribute(Normal, textureDescriptor, offsetU, offsetV, tilesU, tilesV)
        }
    }
}
