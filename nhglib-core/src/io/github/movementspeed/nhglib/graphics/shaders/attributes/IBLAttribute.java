package io.github.movementspeed.nhglib.graphics.shaders.attributes;

import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by Fausto Napoli on 16/08/2017.
 */
public class IBLAttribute extends Attribute {
    public final static String IrradianceAlias = "irradianceTexture";
    public final static String PrefilterAlias = "prefilterTexture";
    public final static String BrdfAlias = "brdfTexture";

    public final static long IrradianceType = register(IrradianceAlias);
    public final static long PrefilterType = register(PrefilterAlias);
    public final static long BrdfType = register(BrdfAlias);

    protected static long Mask = IrradianceType | PrefilterType | BrdfType;

    public final static boolean is(final long mask) {
        return (mask & Mask) != 0;
    }

    public static IBLAttribute createIrradiance(final Cubemap cubemap) {
        return new IBLAttribute(IrradianceType, cubemap);
    }

    public static IBLAttribute createPrefilter(final Cubemap cubemap) {
        return new IBLAttribute(PrefilterType, cubemap);
    }

    public static IBLAttribute createBrdf(final Texture texture) {
        return new IBLAttribute(BrdfType, texture);
    }

    public final TextureDescriptor<GLTexture> textureDescription;
    /**
     * The index of the texture coordinate vertex attribute to use for this IBLAttribute. Whether this value is used, depends
     * on the shader and {@link Attribute#type} value. For basic (model specific) types (e.g. {@link #IrradianceType},
     * etc.), this value is usually ignored and the first texture coordinate vertex attribute is used.
     */
    public int uvIndex = 0;

    public IBLAttribute(final long type) {
        super(type);
        if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
        textureDescription = new TextureDescriptor<>();
    }

    public <T extends Cubemap> IBLAttribute(final long type, final TextureDescriptor<T> textureDescription) {
        this(type);
        this.textureDescription.set(textureDescription);
    }

    public <T extends Cubemap> IBLAttribute(final long type, final TextureDescriptor<T> textureDescription, int uvIndex) {
        this(type, textureDescription);
        this.uvIndex = uvIndex;
    }

    public IBLAttribute(final long type, final Cubemap texture) {
        this(type);
        textureDescription.texture = texture;
    }

    public IBLAttribute(final long type, final Texture texture) {
        this(type);
        textureDescription.texture = texture;
    }

    public void set(final Cubemap cubemap) {
        textureDescription.texture = cubemap;
    }

    @Override
    public Attribute copy() {
        return null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 991 * result + textureDescription.hashCode();
        result = 991 * result + uvIndex;
        return result;
    }

    @Override
    public int compareTo(Attribute o) {
        if (type != o.type) return type < o.type ? -1 : 1;
        IBLAttribute other = (IBLAttribute) o;
        final int c = textureDescription.compareTo(other.textureDescription);
        if (c != 0) return c;
        if (uvIndex != other.uvIndex) return uvIndex - other.uvIndex;
        return 0;
    }
}
