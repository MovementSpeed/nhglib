package io.github.voidzombie.nhglib.graphics.lights.tiled;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Fausto Napoli on 18/03/2017.
 */
public class NhgLightsAttribute extends Attribute {
    public final static String Alias = "nhgLights";
    public final static long Type = register(Alias);

    public final static boolean is(final long mask) {
        return (mask & Type) == mask;
    }

    public final Array<NhgLight> lights;

    public NhgLightsAttribute() {
        super(Type);
        lights = new Array<>(1);
    }

    public NhgLightsAttribute(final NhgLightsAttribute copyFrom) {
        this();
        lights.addAll(copyFrom.lights);
    }

    @Override
    public NhgLightsAttribute copy() {
        return new NhgLightsAttribute(this);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        for (NhgLight light : lights)
            result = 1237 * result + (light == null ? 0 : light.hashCode());
        return result;
    }

    @Override
    public int compareTo(Attribute o) {
        if (type != o.type) return type < o.type ? -1 : 1;
        return 0; // FIXME implement comparing
    }
}
