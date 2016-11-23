package io.github.voidzombie.nhglib.runtime.states;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.EventSystem;
import io.github.voidzombie.nhglib.runtime.entry.BaseGame;
import io.github.voidzombie.nhglib.runtime.messaging.EventAdapter;

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
public enum EngineState implements State<BaseGame> {
    START() {
        @Override
        public void update(BaseGame baseGame) {
            super.update(baseGame);
            baseGame.fsm.changeState(NOT_INITIALIZED);
        }
    },
    NOT_INITIALIZED() {
        @Override
        public void enter(BaseGame baseGame) {
            super.enter(baseGame);
            NHG.logger.log(this, "Engine is not initialized.");

            NHG.messaging.subscribe(NHG.strings.events.assetLoaded, baseGame);
            NHG.messaging.subscribe(NHG.strings.events.assetLoadingFinished, baseGame);

            // EventSystem can listen to Messaging events.
            EventSystem eventSystem = new EventSystem();
            NHG.messaging.subscribe(eventSystem);

            // Setup the ECS' world.
            WorldConfigurationBuilder configurationBuilder = new WorldConfigurationBuilder();
            configurationBuilder.with(eventSystem);

            baseGame.onConfigureEntitySystems(configurationBuilder);

            baseGame.setEntityWorld(new World(configurationBuilder.build()));
            baseGame.onEngineStart();

            baseGame.fsm.changeState(INITIALIZED);
        }
    },
    INITIALIZED() {
        @Override
        public void enter(BaseGame baseGame) {
            super.enter(baseGame);
            NHG.logger.log(this, "Engine is initialized.");
            baseGame.onEngineInitialized();
            baseGame.fsm.changeState(RUNNING);
        }
    },
    RUNNING() {
        @Override
        public void enter(BaseGame baseGame) {
            super.enter(baseGame);
            NHG.logger.log(this, "Engine is running.");
        }

        @Override
        public void update(BaseGame baseGame) {
            super.update(baseGame);
            NHG.assets.update();

            World world = baseGame.getEntityWorld();
            world.setDelta(Gdx.graphics.getDeltaTime());
            world.process();

            baseGame.onEngineRunning();
        }
    },
    PAUSED() {
        @Override
        public void enter(BaseGame baseGame) {
            super.enter(baseGame);
            NHG.logger.log(this, "Engine is paused.");
            baseGame.onEnginePaused();
        }
    },
    CLOSING() {
        @Override
        public void enter(BaseGame baseGame) {
            super.enter(baseGame);
        }
    };

    @Override
    public void enter(BaseGame baseGame) {
    }

    @Override
    public void update(BaseGame baseGame) {
    }

    @Override
    public void exit(BaseGame baseGame) {
    }

    @Override
    public boolean onMessage(BaseGame baseGame, Telegram telegram) {
        return false;
    }
}
