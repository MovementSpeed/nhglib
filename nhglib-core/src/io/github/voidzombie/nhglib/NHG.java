package io.github.voidzombie.nhglib;

import io.github.voidzombie.nhglib.assets.Assets;
import io.github.voidzombie.nhglib.utils.data.Strings;

/**
 * Created by Fausto Napoli on 17/10/2016.
 * Entry point for NHG, where the various parts of the engine will be exposed.
 */
public class NHG {
    public final static Strings strings;
    public final static Assets assets;

    static {
        strings = new Strings();
        assets = new Assets();
    }
}
