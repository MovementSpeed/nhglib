package io.github.voidzombie.nhglib;

import io.github.voidzombie.nhglib.assets.Assets;
import io.github.voidzombie.nhglib.input.handler.InputHandler;
import io.github.voidzombie.nhglib.runtime.ecs.utils.Entities;
import io.github.voidzombie.nhglib.runtime.messaging.Messaging;
import io.github.voidzombie.nhglib.runtime.threading.Threading;

// FIXME :
// FIXME : model assets that are not stored inside folders don't get loaded

// TODO :
// TODO : Implement projective textures
// TODO : Implement asset loading from URL
// TODO : Implement chromatic abberration post-processing filter
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
    public Assets assets;
    public Messaging messaging;
    public InputHandler input;
    public Threading threading;
    public Entities entities;

    public static Boolean debugLogs = false;

    public Nhg() {
        messaging = new Messaging();
        entities = new Entities();
        assets = new Assets(messaging, entities);
        input = new InputHandler();
        threading = new Threading();
    }
}