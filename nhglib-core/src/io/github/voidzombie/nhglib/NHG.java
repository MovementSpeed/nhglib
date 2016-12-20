package io.github.voidzombie.nhglib;

import io.github.voidzombie.nhglib.assets.Assets;
import io.github.voidzombie.nhglib.graphics.scenes.SceneManager;
import io.github.voidzombie.nhglib.runtime.ecs.utils.EntitySystem;
import io.github.voidzombie.nhglib.runtime.messaging.Messaging;
import io.github.voidzombie.nhglib.runtime.threading.Threading;
import io.github.voidzombie.nhglib.utils.Utils;
import io.github.voidzombie.nhglib.utils.data.Strings;
import io.github.voidzombie.nhglib.utils.debug.Logger;

/**
 * Created by Fausto Napoli on 17/10/2016.
 * Entry point for NHG, where the various parts of the engine will be exposed.
 */
public class NHG {
    public final static Strings strings;
    public final static Assets assets;
    public final static Logger logger;
    public final static Messaging messaging;
    public final static Threading threading;
    public final static EntitySystem entitySystem;
    public final static SceneManager sceneManager;
    public final static Utils utils;

    public static Boolean debugLogs = false;

    static {
        strings = new Strings();
        assets = new Assets();
        logger = new Logger();
        messaging = new Messaging();
        threading = new Threading();
        entitySystem = new EntitySystem();
        sceneManager = new SceneManager();
        utils = new Utils();
    }
}
