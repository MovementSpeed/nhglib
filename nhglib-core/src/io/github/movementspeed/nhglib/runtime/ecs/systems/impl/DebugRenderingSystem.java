package io.github.movementspeed.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.runtime.ecs.systems.base.BaseRenderingSystem;
import io.github.movementspeed.nhglib.runtime.ecs.utils.Entities;

public class DebugRenderingSystem extends BaseRenderingSystem {
    // Injected references
    private PhysicsSystem physicsSystem;
    private DebugDrawer debugDrawer;

    public DebugRenderingSystem(Entities entities) {
        super(Aspect.all(), entities);
    }

    @Override
    protected void begin() {
        super.begin();

        if (physicsSystem.isPhysicsInitialized()) {
            if (debugDrawer == null) {
                debugDrawer = new DebugDrawer();
                debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);

                physicsSystem.setDebugDrawer(debugDrawer);
            }
        }
    }

    @Override
    protected void process(int entityId) {
    }

    @Override
    protected void end() {
        super.end();

        for (int i = 0; i < cameras.size; i++) {
            Camera camera = cameras.get(i);

            if (Nhg.debugDrawPhysics && debugDrawer != null) {
                debugDrawer.begin(camera);
                physicsSystem.debugDraw();
                debugDrawer.end();
            }
        }
    }

    public DebugDrawer getDebugDrawer() {
        return debugDrawer;
    }
}
