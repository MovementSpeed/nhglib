package io.github.movementspeed.nhglib.graphics.shaders.attributes;

import com.badlogic.gdx.graphics.g3d.Attribute;

/**
 * Created by Fausto Napoli on 01/04/2017.
 */
public class GammaCorrectionAttribute extends Attribute {
    public final static String Alias = "gammaCorrection";
    public final static long Type = register(Alias);

    public boolean gammaCorrection;

    public GammaCorrectionAttribute(boolean gammaCorrection) {
        super(Type);
        this.gammaCorrection = gammaCorrection;
    }

    @Override
    public Attribute copy() {
        return new GammaCorrectionAttribute(gammaCorrection);
    }

    @Override
    public int compareTo(Attribute o) {
        return 0;
    }
}
