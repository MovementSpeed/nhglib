package io.github.voidzombie.nhglib.graphics.shaders.attributes;

import com.badlogic.gdx.graphics.g3d.Attribute;

/**
 * Created by Fausto Napoli on 01/04/2017.
 */
public class GammaCorrectionAttribute extends Attribute {
    public final static String Alias = "gammaCorrection";
    public final static long Type = register(Alias);

    public boolean gammaCorrection;

    public GammaCorrectionAttribute() {
        super(Type);
        gammaCorrection = true;
    }

    @Override
    public Attribute copy() {
        return new GammaCorrectionAttribute();
    }

    @Override
    public int compareTo(Attribute o) {
        return 0;
    }
}
