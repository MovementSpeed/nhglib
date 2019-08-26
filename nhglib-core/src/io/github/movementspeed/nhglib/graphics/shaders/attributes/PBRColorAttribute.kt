package io.github.movementspeed.nhglib.graphics.shaders.attributes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class PBRColorAttribute extends Attribute {
    public final static String AlbedoColorAlias = "albedoColor";
    public final static String MetalnessValueAlias = "metalnessValue";
    public final static String RoughnessValueAlias = "roughnessValue";

    public final static long AlbedoColor = register(AlbedoColorAlias);
    public final static long MetalnessValue = register(MetalnessValueAlias);
    public final static long RoughnessValue = register(RoughnessValueAlias);

    protected static long Mask = AlbedoColor | MetalnessValue | RoughnessValue;

    public final static boolean is (final long mask) {
        return (mask & Mask) != 0;
    }

    public final static PBRColorAttribute createAlbedo(final Color color) {
        return new PBRColorAttribute(AlbedoColor, color);
    }

    public final static PBRColorAttribute createMetalness(final float metalness) {
        return new PBRColorAttribute(MetalnessValue, new Color(metalness, metalness, metalness, 1.0f));
    }

    public final static PBRColorAttribute createRoughness(final float roughness) {
        return new PBRColorAttribute(RoughnessValue, new Color(roughness, roughness, roughness, 1.0f));
    }

    public final Color color = new Color();

    public PBRColorAttribute(final long type) {
        super(type);
        if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
    }

    public PBRColorAttribute(final long type, final Color color) {
        this(type);
        if (color != null) this.color.set(color);
    }

    public PBRColorAttribute(final long type, float r, float g, float b, float a) {
        this(type);
        this.color.set(r, g, b, a);
    }

    public PBRColorAttribute(final PBRColorAttribute copyFrom) {
        this(copyFrom.type, copyFrom.color);
    }

    @Override
    public Attribute copy () {
        return new PBRColorAttribute(this);
    }

    @Override
    public int hashCode () {
        int result = super.hashCode();
        result = 953 * result + color.toIntBits();
        return result;
    }

    @Override
    public int compareTo (Attribute o) {
        if (type != o.type) return (int)(type - o.type);
        return ((PBRColorAttribute)o).color.toIntBits() - color.toIntBits();
    }
}
