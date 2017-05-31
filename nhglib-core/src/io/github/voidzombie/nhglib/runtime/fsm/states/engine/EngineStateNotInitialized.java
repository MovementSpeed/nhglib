package io.github.voidzombie.nhglib.runtime.fsm.states.engine;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
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
        configurationBuilder
                .with(new PhysicsSystem())
                .with(new GraphicsSystem(nhgEntry.nhg.entities, nhgEntry.nhg.messaging))
                .with(new LightingSystem(nhgEntry.nhg.threading))
                .with(new CameraSystem());

        nhgEntry.onConfigureEntitySystems(configurationBuilder);

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
}
