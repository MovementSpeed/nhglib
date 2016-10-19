package io.github.voidzombie.nhglib;

import io.github.voidzombie.nhglib.assets.Assets;

/**
 * Created by Fausto Napoli on 17/10/2016.
 * Entry point for NHG, where the various parts of the engine will be exposed.
 */
public class NHG {
    public final static Assets assets;

    static {
        assets = new Assets();
    }
}
