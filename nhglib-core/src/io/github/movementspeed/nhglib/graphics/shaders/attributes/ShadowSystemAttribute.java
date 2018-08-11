package io.github.movementspeed.nhglib.graphics.shaders.attributes;

import com.badlogic.gdx.graphics.g3d.Attribute;
import io.github.movementspeed.nhglib.graphics.shaders.shadows.system.ShadowSystem;

public class ShadowSystemAttribute extends Attribute {
    public final static String Alias = "shadowSystem";
    public final static long Type = register(Alias);

    public ShadowSystem shadowSystem;

    public ShadowSystemAttribute(ShadowSystem shadowSystem) {
        super(Type);
        this.shadowSystem = shadowSystem;
    }

    @Override
    public Attribute copy() {
        return new ShadowSystemAttribute(shadowSystem);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 991 * result + shadowSystem.hashCode();
        return result;
    }

    @Override
    public int compareTo(Attribute o) {
        if (type != o.type) return type < o.type ? -1 : 1;
        ShadowSystemAttribute other = (ShadowSystemAttribute) o;
        if (other.shadowSystem != shadowSystem)
            return 1;
        else
            return -1;
    }
}