package io.github.movementspeed.nhglib.core.fsm.states.engine

import com.artemis.BaseSystem
import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.core.ecs.systems.impl.*
import io.github.movementspeed.nhglib.core.entry.NhgEntry
import io.github.movementspeed.nhglib.core.fsm.base.EngineStates
import io.github.movementspeed.nhglib.utils.debug.NhgLogger

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
class EngineStateNotInitialized : State<NhgEntry> {
    override fun enter(nhgEntry: NhgEntry) {
        NhgLogger.log(this, "Engine is not initialized.")

        // Setup the ECS world.
        val configurationBuilder = WorldConfigurationBuilder()

        // Configure user entity systems
        val entitySystems = nhgEntry.onConfigureEntitySystems()

        // Configure the most important systems last, especially RenderingSystem which
        // should be the last because it renders all the changes happened in all other
        // systems.
        if (!hasSystemClass(InputSystem::class.java, entitySystems)) {
            entitySystems.add(InputSystem(nhgEntry.nhg.assets))
        } else {
            NhgLogger.log(this, "InputSystem already registered, ignoring registration.")
        }

        if (!hasSystemClass(PhysicsSystem::class.java, entitySystems)) {
            entitySystems.add(PhysicsSystem())
        } else {
            NhgLogger.log(this, "PhysicsSystem already registered, ignoring registration.")
        }

        if (!hasSystemClass(CameraSystem::class.java, entitySystems)) {
            entitySystems.add(CameraSystem())
        } else {
            NhgLogger.log(this, "CameraSystem already registered, ignoring registration.")
        }

        if (!hasSystemClass(LightingSystem::class.java, entitySystems)) {
            entitySystems.add(LightingSystem())
        } else {
            NhgLogger.log(this, "LightingSystem already registered, ignoring registration.")
        }

        if (!hasSystemClass(ParticleRenderingSystem::class.java, entitySystems)) {
            entitySystems.add(ParticleRenderingSystem(nhgEntry.nhg.entities))
        } else {
            NhgLogger.log(this, "ParticleRenderingSystem already registered, ignoring registration.")
        }

        if (!hasSystemClass(ModelRenderingSystem::class.java, entitySystems)) {
            entitySystems.add(ModelRenderingSystem(nhgEntry.nhg.entities, nhgEntry.nhg.messaging, nhgEntry.nhg.assets))
        } else {
            NhgLogger.log(this, "ModelRenderingSystem already registered, ignoring registration.")
        }

        if (!hasSystemClass(UiSystem::class.java, entitySystems)) {
            entitySystems.add(UiSystem(nhgEntry.nhg.entities))
        } else {
            NhgLogger.log(this, "ModelRenderingSystem already registered, ignoring registration.")
        }

        if (!hasSystemClass(RenderingSystem::class.java, entitySystems)) {
            entitySystems.add(RenderingSystem())
        } else {
            NhgLogger.log(this, "RenderingSystem already registered, ignoring registration.")
        }

        if (!hasSystemClass(DebugRenderingSystem::class.java, entitySystems)) {
            entitySystems.add(DebugRenderingSystem(nhgEntry.nhg.entities))
        } else {
            NhgLogger.log(this, "DebugRenderingSystem already registered, ignoring registration.")
        }

        for (bes in entitySystems) {
            configurationBuilder.with(bes)
        }

        nhgEntry.nhg.entities.setEntityWorld(World(configurationBuilder.build()))
        nhgEntry.fsm!!.changeState(EngineStates.INITIALIZED)
    }

    override fun update(entity: NhgEntry) {

    }

    override fun exit(entity: NhgEntry) {

    }

    override fun onMessage(entity: NhgEntry, telegram: Telegram): Boolean {
        return false
    }

    private fun hasSystemClass(systemClass: Class<out BaseSystem>, entitySystems: Array<BaseSystem>): Boolean {
        var res = false

        for (es in entitySystems) {
            if (systemClass.isInstance(es)) {
                res = true
            }
        }

        return res
    }
}
