package io.github.voidzombie.nhglib.enums.states;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.ecs.systems.EventSystem;
import io.github.voidzombie.nhglib.runtime.entry.BaseGame;

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
public enum EngineState implements State<BaseGame> {
    START() {
        @Override
        public void update(BaseGame entity) {
            super.update(entity);
            entity.fsm.changeState(NOT_INITIALIZED);
        }
    },
    NOT_INITIALIZED() {
        @Override
        public void enter(BaseGame entity) {
            super.enter(entity);
            NHG.logger.log(this, "Engine is not initialized.");

            // BaseGameOld can listen to asset loading events.
            NHG.assets.addAssetLoadingListener(entity);

            // EventSystem can listen to Messaging events.
            EventSystem eventSystem = new EventSystem();
            NHG.messaging.addListener(eventSystem);

            // Setup the ECS' world.
            WorldConfigurationBuilder configurationBuilder = new WorldConfigurationBuilder();
            configurationBuilder.with(eventSystem);

            entity.onConfigureEntitySystems(configurationBuilder);

            entity.setEntityWorld(new World(configurationBuilder.build()));
            entity.onEngineStart();

            entity.fsm.changeState(INITIALIZED);
        }
    },
    INITIALIZED() {
        @Override
        public void enter(BaseGame entity) {
            super.enter(entity);
            NHG.logger.log(this, "Engine is initialized.");
            entity.onEngineInitialized();
            entity.fsm.changeState(RUNNING);
        }
    },
    RUNNING() {
        @Override
        public void enter(BaseGame entity) {
            super.enter(entity);
            NHG.logger.log(this, "Engine is running.");
        }

        @Override
        public void update(BaseGame entity) {
            super.update(entity);
            NHG.assets.update();

            World world = entity.getEntityWorld();
            world.setDelta(Gdx.graphics.getDeltaTime());
            world.process();

            entity.onEngineRunning();
        }
    },
    PAUSED() {
        @Override
        public void enter(BaseGame entity) {
            super.enter(entity);
            NHG.logger.log(this, "Engine is paused.");
            entity.onEnginePaused();
        }
    };

    @Override
    public void enter(BaseGame entity) {
    }

    @Override
    public void update(BaseGame entity) {
    }

    @Override
    public void exit(BaseGame entity) {
    }

    @Override
    public boolean onMessage(BaseGame entity, Telegram telegram) {
        return false;
    }
}
