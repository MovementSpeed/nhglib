package io.github.movementspeed.nhglib.graphics.shaders.attributes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;

/**
 * Created by Fausto Napoli on 23/03/2017.
 */
public class PbrTextureAttribute extends Attribute {
    public final static String AlbedoAlias = "albedoTexture";
    public final static String MetalnessAlias = "metalnessTexture";
    public final static String RoughnessAlias = "roughnessTexture";
    public final static String NormalAlias = "normalTexture";
    public final static String AmbientOcclusionAlias = "ambientOcclusionTexture";

    public final static long Albedo = register(AlbedoAlias);
    public final static long Metalness = register(MetalnessAlias);
    public final static long Roughness = register(RoughnessAlias);
    public final static long Normal = register(NormalAlias);
    public final static long AmbientOcclusion = register(AmbientOcclusionAlias);

    protected static long Mask = Albedo | Metalness | Roughness | Normal | AmbientOcclusion;

    public final static boolean is(final long mask) {
        return (mask & Mask) != 0;
    }

    public static PbrTextureAttribute createAlbedo(final Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.setColor(color);
        pixmap.drawPixel(0, 0);
        Texture texture = new Texture(pixmap);
        return new PbrTextureAttribute(Albedo, texture);
    }

    public static PbrTextureAttribute createAlbedo(final Texture texture) {
        return new PbrTextureAttribute(Albedo, texture);
    }

    public static PbrTextureAttribute createAlbedo(final TextureRegion region) {
        return new PbrTextureAttribute(Albedo, region);
    }

    public static PbrTextureAttribute createMetalness(final float metalness) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.setColor(metalness, metalness, metalness, 1.0f);
        pixmap.drawPixel(0, 0);
        Texture texture = new Texture(pixmap);
        return new PbrTextureAttribute(Metalness, texture);
    }

    public static PbrTextureAttribute createMetalness(final Texture texture) {
        return new PbrTextureAttribute(Metalness, texture);
    }

    public static PbrTextureAttribute createMetalness(final TextureRegion region) {
        return new PbrTextureAttribute(Metalness, region);
    }

    public static PbrTextureAttribute createRoughness(final float roughness) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.setColor(roughness, roughness, roughness, 1.0f);
        pixmap.drawPixel(0, 0);
        Texture texture = new Texture(pixmap);
        return new PbrTextureAttribute(Roughness, texture);
    }

    public static PbrTextureAttribute createRoughness(final Texture texture) {
        return new PbrTextureAttribute(Roughness, texture);
    }

    public static PbrTextureAttribute createRoughness(final TextureRegion region) {
        return new PbrTextureAttribute(Roughness, region);
    }

    public static PbrTextureAttribute createNormal(final Texture texture) {
        return new PbrTextureAttribute(Normal, texture);
    }

    public static PbrTextureAttribute createNormal(final TextureRegion region) {
        return new PbrTextureAttribute(Normal, region);
    }

    public static PbrTextureAttribute createAmbientOcclusion(final Texture texture) {
        return new PbrTextureAttribute(AmbientOcclusion, texture);
    }

    public static PbrTextureAttribute createAmbientOcclusion(final TextureRegion region) {
        return new PbrTextureAttribute(AmbientOcclusion, region);
    }

    public final TextureDescriptor<Texture> textureDescription;

    public float offsetU = 0;
    public float offsetV = 0;
    public float scaleU = 1;
    public float scaleV = 1;
    /**
     * The index of the texture coordinate vertex attribute to use for this PbrTextureAttribute. Whether this value is used, depends
     * on the shader and {@link Attribute#type} value. For basic (model specific) types (e.g. {@link #Albedo},
     * etc.), this value is usually ignored and the first texture coordinate vertex attribute is used.
     */
    public int uvIndex = 0;

    public PbrTextureAttribute(final long type) {
        super(type);
        if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
        textureDescription = new TextureDescriptor<>();
    }

    public <T extends Texture> PbrTextureAttribute(final long type, final TextureDescriptor<T> textureDescription) {
        this(type);
        this.textureDescription.set(textureDescription);
    }

    public <T extends Texture> PbrTextureAttribute(final long type, final TextureDescriptor<T> textureDescription, float offsetU,
                                                   float offsetV, float scaleU, float scaleV, int uvIndex) {
        this(type, textureDescription);
        this.offsetU = offsetU;
        this.offsetV = offsetV;
        this.scaleU = scaleU;
        this.scaleV = scaleV;
        this.uvIndex = uvIndex;
    }

    public <T extends Texture> PbrTextureAttribute(final long type, final TextureDescriptor<T> textureDescription, float offsetU,
                                                   float offsetV, float scaleU, float scaleV) {
        this(type, textureDescription, offsetU, offsetV, scaleU, scaleV, 0);
    }

    public PbrTextureAttribute(final long type, final Texture texture) {
        this(type);
        textureDescription.texture = texture;
    }

    public PbrTextureAttribute(final long type, final TextureRegion region) {
        this(type);
        set(region);
    }

    public PbrTextureAttribute(final PbrTextureAttribute copyFrom) {
        this(copyFrom.type, copyFrom.textureDescription, copyFrom.offsetU, copyFrom.offsetV, copyFrom.scaleU, copyFrom.scaleV,
                copyFrom.uvIndex);
    }

    public void set(final TextureRegion region) {
        textureDescription.texture = region.getTexture();
        offsetU = region.getU();
        offsetV = region.getV();
        scaleU = region.getU2() - offsetU;
        scaleV = region.getV2() - offsetV;
    }

    @Override
    public Attribute copy() {
        return new PbrTextureAttribute(this);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 991 * result + textureDescription.hashCode();
        result = 991 * result + NumberUtils.floatToRawIntBits(offsetU);
        result = 991 * result + NumberUtils.floatToRawIntBits(offsetV);
        result = 991 * result + NumberUtils.floatToRawIntBits(scaleU);
        result = 991 * result + NumberUtils.floatToRawIntBits(scaleV);
        result = 991 * result + uvIndex;
        return result;
    }

    @Override
    public int compareTo(Attribute o) {
        if (type != o.type) return type < o.type ? -1 : 1;
        PbrTextureAttribute other = (PbrTextureAttribute) o;
        final int c = textureDescription.compareTo(other.textureDescription);
        if (c != 0) return c;
        if (uvIndex != other.uvIndex) return uvIndex - other.uvIndex;
        if (!MathUtils.isEqual(scaleU, other.scaleU)) return scaleU > other.scaleU ? 1 : -1;
        if (!MathUtils.isEqual(scaleV, other.scaleV)) return scaleV > other.scaleV ? 1 : -1;
        if (!MathUtils.isEqual(offsetU, other.offsetU)) return offsetU > other.offsetU ? 1 : -1;
        if (!MathUtils.isEqual(offsetV, other.offsetV)) return offsetV > other.offsetV ? 1 : -1;
        return 0;
    }
}
