package io.github.movementspeed.nhglib;

import io.github.movementspeed.nhglib.assets.Assets;
import io.github.movementspeed.nhglib.input.handler.InputHandler;
import io.github.movementspeed.nhglib.runtime.ecs.utils.Entities;
import io.github.movementspeed.nhglib.runtime.messaging.Messaging;
import io.github.movementspeed.nhglib.runtime.threading.Threading;

// FIXME :
// FIXME : Normal mapping has rendering issues when camera rotates certain angles, they kind of "flip".

// TODO :
// TODO : Have a look at distance field fonts integrated with LibGDX
// TODO : Implement physics material system
// TODO : Implement video playback on iOS and Android
// TODO : Manage global configuration files
// TODO : Implement support for transparent textures
// TODO : Implement projective textures
// TODO : Implement asset loading from URL
// TODO : Implement chromatic abberration post-processing filter
// TODO : Implement saturation post-processing filter
// TODO : Implement real time shadow maps
// TODO : Implement image based lighting
// TODO : Implement Bullet based occlusion culling (https://github.com/libgdx/libgdx/blob/master/tests/gdx-tests/src/com/badlogic/gdx/tests/bullet/OcclusionCullingTest.java)

/**
 * Created by Fausto Napoli on 17/10/2016.
 * Entry point for Nhg, where various parts of the engine will be exposed.
 */
public class Nhg {
    public Assets assets;
    public Messaging messaging;
    public InputHandler input;
    public Threading threading;
    public Entities entities;

    public static Boolean debugLogs = false;
    public static Boolean debugDrawPhysics = false;
    public static Boolean debugFpsLogs = false;

    public Nhg() {
        messaging = new Messaging();
        entities = new Entities();
        assets = new Assets();
        input = new InputHandler();
        threading = new Threading();
    }

    public void init() {
        assets.init(this);
    }
}