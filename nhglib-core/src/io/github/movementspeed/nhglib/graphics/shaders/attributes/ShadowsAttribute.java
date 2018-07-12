package io.github.movementspeed.nhglib.graphics.shaders.attributes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;

public class ShadowsAttribute extends Attribute {
    public final static String Alias = "shadows";
    public final static long Type = register(Alias);

    public Texture shadows;

    public ShadowsAttribute(Texture shadows) {
        super(Type);
        this.shadows = shadows;
    }

    @Override
    public Attribute copy() {
        return new ShadowsAttribute(shadows);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 991 * result + Alias.hashCode();
        return result;
    }

    @Override
    public int compareTo(Attribute o) {
        if (type != o.type) return type < o.type ? -1 : 1;
        ShadowsAttribute other = (ShadowsAttribute) o;
        if (other.shadows != shadows) return 1;
        return 0;
    }
}