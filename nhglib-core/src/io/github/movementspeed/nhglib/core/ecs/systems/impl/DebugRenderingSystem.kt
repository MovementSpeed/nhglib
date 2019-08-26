package io.github.movementspeed.nhglib.core.ecs.systems.impl

import com.artemis.Aspect
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.physics.bullet.DebugDrawer
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw
import io.github.movementspeed.nhglib.Nhg
import io.github.movementspeed.nhglib.core.ecs.systems.base.BaseRenderingSystem
import io.github.movementspeed.nhglib.core.ecs.utils.Entities

class DebugRenderingSystem(entities: Entities) : BaseRenderingSystem(Aspect.all(), entities) {
    // Injected references
    private val physicsSystem: PhysicsSystem? = null
    var debugDrawer: DebugDrawer? = null
        private set

    override fun begin() {
        super.begin()

        if (physicsSystem!!.isPhysicsInitialized) {
            if (debugDrawer == null) {
                debugDrawer = DebugDrawer()
                debugDrawer!!.debugMode = btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE

                physicsSystem.setDebugDrawer(debugDrawer)
            }
        }
    }

    override fun process(entityId: Int) {}

    override fun end() {
        super.end()

        for (i in 0 until cameras!!.size) {
            val camera = cameras!!.get(i)

            if (Nhg.debugDrawPhysics && debugDrawer != null) {
                debugDrawer!!.begin(camera)
                physicsSystem!!.debugDraw()
                debugDrawer!!.end()
            }
        }
    }
}
