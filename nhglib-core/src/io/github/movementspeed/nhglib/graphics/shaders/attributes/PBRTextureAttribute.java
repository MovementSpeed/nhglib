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
public class PBRTextureAttribute extends Attribute {
    public final static String AlbedoAlias = "PBRAlbedoTexture";
    public final static String NormalAlias = "PBRNormalTexture";
    public final static String RMAAlias = "RMATexture";
    public final static String EmissiveAlias = "PBREmissiveTexture";

    public final static long Albedo = register(AlbedoAlias);
    public final static long Normal = register(NormalAlias);
    public final static long RMA = register(RMAAlias);
    public final static long Emissive = register(EmissiveAlias);

    protected static long Mask = Albedo | Normal | RMA | Emissive;

    public final static boolean is(final long mask) {
        return (mask & Mask) != 0;
    }

    public static PBRTextureAttribute createAlbedo(final Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.setColor(color);
        pixmap.drawPixel(0, 0);
        Texture texture = new Texture(pixmap);
        return new PBRTextureAttribute(Albedo, texture);
    }

    public static PBRTextureAttribute createAlbedo(final Texture texture) {
        return new PBRTextureAttribute(Albedo, texture);
    }

    public static PBRTextureAttribute createAlbedo(final TextureRegion region) {
        return new PBRTextureAttribute(Albedo, region);
    }

    public static PBRTextureAttribute createAlbedo(final Texture texture, float offsetU, float offsetV, float tilesU, float tilesV) {
        TextureDescriptor textureDescriptor = new TextureDescriptor();
        textureDescriptor.texture = texture;

        return new PBRTextureAttribute(Albedo, textureDescriptor, offsetU, offsetV, tilesU, tilesV);
    }

    public static PBRTextureAttribute createRMA(final float roughness, final float metalness, final float ambientOcclusion) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.setColor(roughness, metalness, ambientOcclusion, 1.0f);
        pixmap.drawPixel(0, 0);
        Texture texture = new Texture(pixmap);
        return new PBRTextureAttribute(RMA, texture);
    }

    public static PBRTextureAttribute createRMA(final Texture texture) {
        return new PBRTextureAttribute(RMA, texture);
    }

    public static PBRTextureAttribute createRMA(final TextureRegion region) {
        return new PBRTextureAttribute(RMA, region);
    }

    public static PBRTextureAttribute createRMA(final Texture texture, float offsetU, float offsetV, float tilesU, float tilesV) {
        TextureDescriptor textureDescriptor = new TextureDescriptor();
        textureDescriptor.texture = texture;

        return new PBRTextureAttribute(RMA, textureDescriptor, offsetU, offsetV, tilesU, tilesV);
    }

    public static PBRTextureAttribute createEmissive(final Texture texture) {
        return new PBRTextureAttribute(Emissive, texture);
    }

    public static PBRTextureAttribute createEmissive(final TextureRegion region) {
        return new PBRTextureAttribute(Emissive, region);
    }

    public static PBRTextureAttribute createEmissive(final Texture texture, float offsetU, float offsetV, float tilesU, float tilesV) {
        TextureDescriptor textureDescriptor = new TextureDescriptor();
        textureDescriptor.texture = texture;

        return new PBRTextureAttribute(Emissive, textureDescriptor, offsetU, offsetV, tilesU, tilesV);
    }

    public static PBRTextureAttribute createNormal(final Texture texture) {
        return new PBRTextureAttribute(Normal, texture);
    }

    public static PBRTextureAttribute createNormal(final TextureRegion region) {
        return new PBRTextureAttribute(Normal, region);
    }

    public static PBRTextureAttribute createNormal(final Texture texture, float offsetU, float offsetV, float tilesU, float tilesV) {
        TextureDescriptor textureDescriptor = new TextureDescriptor();
        textureDescriptor.texture = texture;

        return new PBRTextureAttribute(Normal, textureDescriptor, offsetU, offsetV, tilesU, tilesV);
    }

    public final TextureDescriptor<Texture> textureDescription;

    public float offsetU = 0;
    public float offsetV = 0;
    public float tilesU = 1;
    public float tilesV = 1;
    /**
     * The index of the texture coordinate vertex attribute to use for this PBRTextureAttribute. Whether this value is used, depends
     * on the shader and {@link Attribute#type} value. For basic (model specific) types (e.g. {@link #Albedo},
     * etc.), this value is usually ignored and the first texture coordinate vertex attribute is used.
     */
    public int uvIndex = 0;

    public PBRTextureAttribute(final long type) {
        super(type);
        if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
        textureDescription = new TextureDescriptor<>();
    }

    public <T extends Texture> PBRTextureAttribute(final long type, final TextureDescriptor<T> textureDescription) {
        this(type);
        this.textureDescription.set(textureDescription);
    }

    public <T extends Texture> PBRTextureAttribute(final long type, final TextureDescriptor<T> textureDescription, float offsetU,
                                                   float offsetV, float tilesU, float tilesV, int uvIndex) {
        this(type, textureDescription);
        this.offsetU = offsetU;
        this.offsetV = offsetV;
        this.tilesU = tilesU;
        this.tilesV = tilesV;
        this.uvIndex = uvIndex;
    }

    public <T extends Texture> PBRTextureAttribute(final long type, final TextureDescriptor<T> textureDescription, float offsetU,
                                                   float offsetV, float tilesU, float tilesV) {
        this(type, textureDescription, offsetU, offsetV, tilesU, tilesV, 0);
    }

    public PBRTextureAttribute(final long type, final Texture texture) {
        this(type);
        textureDescription.texture = texture;
    }

    public PBRTextureAttribute(final long type, final TextureRegion region) {
        this(type);
        set(region);
    }

    public PBRTextureAttribute(final PBRTextureAttribute copyFrom) {
        this(copyFrom.type, copyFrom.textureDescription, copyFrom.offsetU, copyFrom.offsetV, copyFrom.tilesU, copyFrom.tilesV,
                copyFrom.uvIndex);
    }

    public void set(final TextureRegion region) {
        textureDescription.texture = region.getTexture();
        offsetU = region.getU();
        offsetV = region.getV();
        tilesU = region.getU2() - offsetU;
        tilesV = region.getV2() - offsetV;
    }

    @Override
    public Attribute copy() {
        return new PBRTextureAttribute(this);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 991 * result + textureDescription.hashCode();
        result = 991 * result + NumberUtils.floatToRawIntBits(offsetU);
        result = 991 * result + NumberUtils.floatToRawIntBits(offsetV);
        result = 991 * result + NumberUtils.floatToRawIntBits(tilesU);
        result = 991 * result + NumberUtils.floatToRawIntBits(tilesV);
        result = 991 * result + uvIndex;
        return result;
    }

    @Override
    public int compareTo(Attribute o) {
        if (type != o.type) return type < o.type ? -1 : 1;
        PBRTextureAttribute other = (PBRTextureAttribute) o;
        final int c = textureDescription.compareTo(other.textureDescription);
        if (c != 0) return c;
        if (uvIndex != other.uvIndex) return uvIndex - other.uvIndex;
        if (!MathUtils.isEqual(tilesU, other.tilesU)) return tilesU > other.tilesU ? 1 : -1;
        if (!MathUtils.isEqual(tilesV, other.tilesV)) return tilesV > other.tilesV ? 1 : -1;
        if (!MathUtils.isEqual(offsetU, other.offsetU)) return offsetU > other.offsetU ? 1 : -1;
        if (!MathUtils.isEqual(offsetV, other.offsetV)) return offsetV > other.offsetV ? 1 : -1;
        return 0;
    }
}
