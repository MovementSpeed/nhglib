package io.github.voidzombie.nhglib;

import io.github.voidzombie.nhglib.assets.Assets;
import io.github.voidzombie.nhglib.input.handler.InputHandler;
import io.github.voidzombie.nhglib.runtime.ecs.utils.EntitySystem;
import io.github.voidzombie.nhglib.runtime.messaging.Messaging;
import io.github.voidzombie.nhglib.runtime.threading.Threading;
import io.github.voidzombie.nhglib.utils.data.Strings;
import io.github.voidzombie.nhglib.utils.debug.Logger;

// FIXME :
// FIXME : assets that are not stored inside folders don't get loaded

// TODO :
// TODO : Implement saturation post-processing filter
// TODO : Implement real time shadow maps
// TODO : Implement image based lighting
// TODO : Implements physics and easy collision system
// TODO : Re-implement lights parsing from JSON and fix LightSystem

/**
 * Created by Fausto Napoli on 17/10/2016.
 * Entry point for Nhg, where the various parts of the engine will be exposed.
 */
public class Nhg {
    public final static Strings strings;
    public final static Assets assets;
    public final static Logger logger;
    public final static Messaging messaging;
    public final static InputHandler input;
    public final static Threading threading;
    public final static EntitySystem entitySystem;

    public static Boolean debugLogs = false;

    static {
        strings = new Strings();
        assets = new Assets();
        logger = new Logger();
        messaging = new Messaging();
        input = new InputHandler();
        threading = new Threading();
        entitySystem = new EntitySystem();
    }
}