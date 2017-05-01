package io.github.voidzombie.nhglib;

import io.github.voidzombie.nhglib.assets.Assets;
import io.github.voidzombie.nhglib.input.handler.InputHandler;
import io.github.voidzombie.nhglib.runtime.ecs.utils.Entities;
import io.github.voidzombie.nhglib.runtime.messaging.Messaging;
import io.github.voidzombie.nhglib.runtime.threading.Threading;

// FIXME :
// FIXME : Normal mapping has issues when camera rotates certain angles

// TODO :
// TODO : Implement video playback on iOS and Android
// TODO : Manage global configuration files
// TODO : Implement support for transparent textures
// TODO : Implement projective textures
// TODO : Implement asset loading from URL
// TODO : Implement chromatic abberration post-processing filter
// TODO : Implement saturation post-processing filter
// TODO : Implement real time shadow maps
// TODO : Implement image based lighting
// TODO : Implement physics and easy collision system
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