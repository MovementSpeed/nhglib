package io.github.voidzombie.nhglib.runtime.fsm.states.engine;

import com.artemis.BaseEntitySystem;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.CameraSystem;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.GraphicsSystem;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.LightingSystem;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.PhysicsSystem;
import io.github.voidzombie.nhglib.runtime.entry.NhgEntry;
import io.github.voidzombie.nhglib.runtime.fsm.base.EngineStates;
import io.github.voidzombie.nhglib.utils.debug.Logger;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class EngineStateNotInitialized implements State<NhgEntry> {
    @Override
    public void enter(NhgEntry nhgEntry) {
        Logger.log(this, "Engine is not initialized.");

        // Setup the ECS world.
        WorldConfigurationBuilder configurationBuilder = new WorldConfigurationBuilder();

        // Configure user entity systems
        Array<BaseEntitySystem> entitySystems = nhgEntry.onConfigureEntitySystems();

        // Configure the most important systems last, especially GraphicsSystem which
        // should be the last because it renders all the changes happened in all other
        // systems.
        if (!hasSystemClass(PhysicsSystem.class, entitySystems)) {
            entitySystems.add(new PhysicsSystem());
        } else {
            Logger.log(this, "PhysicsSystem already registered, ignoring registration.");
        }

        if (!hasSystemClass(CameraSystem.class, entitySystems)) {
            entitySystems.add(new CameraSystem());
        } else {
            Logger.log(this, "CameraSystem already registered, ignoring registration.");
        }

        if (!hasSystemClass(LightingSystem.class, entitySystems)) {
            entitySystems.add(new LightingSystem(nhgEntry.nhg.threading));
        } else {
            Logger.log(this, "LightingSystem already registered, ignoring registration.");
        }

        if (!hasSystemClass(GraphicsSystem.class, entitySystems)) {
            entitySystems.add(new GraphicsSystem(nhgEntry.nhg.entities, nhgEntry.nhg.messaging));
        } else {
            Logger.log(this, "GraphicsSystem already registered, ignoring registration.");
        }

        for (BaseEntitySystem bes : entitySystems) {
            configurationBuilder.with(bes);
        }

        nhgEntry.nhg.entities.setEntityWorld(new World(configurationBuilder.build()));
        nhgEntry.engineStarted();

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

    private boolean hasSystemClass(Class<? extends BaseEntitySystem> systemClass, Array<BaseEntitySystem> entitySystems) {
        boolean res = false;

        for (BaseEntitySystem es : entitySystems) {
            if (systemClass.isInstance(es)) {
                res = true;
            }
        }

        return res;
    }
}
