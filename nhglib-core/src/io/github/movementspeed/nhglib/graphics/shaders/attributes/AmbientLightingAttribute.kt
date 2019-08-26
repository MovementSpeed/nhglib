package io.github.movementspeed.nhglib.graphics.shaders.attributes;

import com.badlogic.gdx.graphics.g3d.Attribute;

/**
 * Created by Fausto Napoli on 01/04/2017.
 */
public class AmbientLightingAttribute extends Attribute {
    public final static String Alias = "ambientLighting";
    public final static long Type = register(Alias);

    public float ambient;

    public AmbientLightingAttribute(float ambient) {
        super(Type);
        this.ambient = ambient;
    }

    @Override
    public Attribute copy() {
        return new AmbientLightingAttribute(ambient);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 991 * result + (int) ambient;
        return result;
    }

    @Override
    public int compareTo(Attribute o) {
        if (type != o.type) return type < o.type ? -1 : 1;
        AmbientLightingAttribute other = (AmbientLightingAttribute) o;
        if (other.ambient != ambient) return other.ambient < ambient ? 1 : -1;
        return 0;
    }
}