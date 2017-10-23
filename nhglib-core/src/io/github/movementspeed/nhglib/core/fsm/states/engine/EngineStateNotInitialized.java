package io.github.movementspeed.nhglib.core.fsm.states.engine;

import com.artemis.BaseSystem;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.core.ecs.systems.impl.*;
import io.github.movementspeed.nhglib.core.entry.NhgEntry;
import io.github.movementspeed.nhglib.core.fsm.base.EngineStates;
import io.github.movementspeed.nhglib.utils.debug.NhgLogger;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class EngineStateNotInitialized implements State<NhgEntry> {
    @Override
    public void enter(NhgEntry nhgEntry) {
        NhgLogger.log(this, "Engine is not initialized.");

        // Setup the ECS world.
        WorldConfigurationBuilder configurationBuilder = new WorldConfigurationBuilder();

        // Configure user entity systems
        Array<BaseSystem> entitySystems = nhgEntry.onConfigureEntitySystems();

        // Configure the most important systems last, especially RenderingSystem which
        // should be the last because it renders all the changes happened in all other
        // systems.
        if (!hasSystemClass(PhysicsSystem.class, entitySystems)) {
            entitySystems.add(new PhysicsSystem());
        } else {
            NhgLogger.log(this, "PhysicsSystem already registered, ignoring registration.");
        }

        if (!hasSystemClass(CameraSystem.class, entitySystems)) {
            entitySystems.add(new CameraSystem());
        } else {
            NhgLogger.log(this, "CameraSystem already registered, ignoring registration.");
        }

        if (!hasSystemClass(LightingSystem.class, entitySystems)) {
            entitySystems.add(new LightingSystem());
        } else {
            NhgLogger.log(this, "LightingSystem already registered, ignoring registration.");
        }

        if (!hasSystemClass(ParticleRenderingSystem.class, entitySystems)) {
            entitySystems.add(new ParticleRenderingSystem(nhgEntry.nhg.entities));
        } else {
            NhgLogger.log(this, "ParticleRenderingSystem already registered, ignoring registration.");
        }

        if (!hasSystemClass(ModelRenderingSystem.class, entitySystems)) {
            entitySystems.add(new ModelRenderingSystem(nhgEntry.nhg.entities, nhgEntry.nhg.messaging));
        } else {
            NhgLogger.log(this, "ModelRenderingSystem already registered, ignoring registration.");
        }

        if (!hasSystemClass(UiSystem.class, entitySystems)) {
            entitySystems.add(new UiSystem(nhgEntry.nhg.entities, nhgEntry.nhg.input));
        } else {
            NhgLogger.log(this, "ModelRenderingSystem already registered, ignoring registration.");
        }

        if (!hasSystemClass(DebugRenderingSystem.class, entitySystems)) {
            entitySystems.add(new DebugRenderingSystem(nhgEntry.nhg.entities));
        } else {
            NhgLogger.log(this, "DebugRenderingSystem already registered, ignoring registration.");
        }

        if (!hasSystemClass(RenderingSystem.class, entitySystems)) {
            entitySystems.add(new RenderingSystem());
        } else {
            NhgLogger.log(this, "RenderingSystem already registered, ignoring registration.");
        }

        for (BaseSystem bes : entitySystems) {
            configurationBuilder.with(bes);
        }

        nhgEntry.nhg.entities.setEntityWorld(new World(configurationBuilder.build()));
        nhgEntry.getFsm().changeState(EngineStates.INITIALIZED);
    }

    @Override
    public void update(NhgEntry entity) {

    }

    @Override
    public void exit(NhgEntry entity) {

    }

    @Override
    public boolean onMessage(NhgEntry entity, Telegram telegram) {
        return false;
    }

    private boolean hasSystemClass(Class<? extends BaseSystem> systemClass, Array<BaseSystem> entitySystems) {
        boolean res = false;

        for (BaseSystem es : entitySystems) {
            if (systemClass.isInstance(es)) {
                res = true;
            }
        }

        return res;
    }
}
