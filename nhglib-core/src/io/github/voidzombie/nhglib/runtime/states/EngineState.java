package io.github.voidzombie.nhglib.runtime.states;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.EntityMessageSystem;
import io.github.voidzombie.nhglib.runtime.entry.BaseGame;

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

            // Subscribe to asset loading events
            NHG.messaging.addListener(baseGame,
                    NHG.strings.events.assetLoaded,
                    NHG.strings.events.assetLoadingFinished);

            // EntityMessageSystem can addListener to Messaging events.
            EntityMessageSystem entityEventSystem = new EntityMessageSystem();
            NHG.messaging.addListener(entityEventSystem);

            // Setup the ECS' world.
            WorldConfigurationBuilder configurationBuilder = new WorldConfigurationBuilder();
            configurationBuilder.with(entityEventSystem);

            baseGame.onConfigureEntitySystems(configurationBuilder);

            baseGame.setEntityWorld(new World(configurationBuilder.build()));
            baseGame.engineStarted();

            baseGame.fsm.changeState(INITIALIZED);
        }
    },
    INITIALIZED() {
        @Override
        public void enter(BaseGame baseGame) {
            super.enter(baseGame);
            NHG.logger.log(this, "Engine is initialized.");
            baseGame.engineInitialized();
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

            baseGame.engineUpdate();
        }
    },
    PAUSED() {
        @Override
        public void enter(BaseGame baseGame) {
            super.enter(baseGame);
            NHG.logger.log(this, "Engine is paused.");
            baseGame.enginePaused();
        }
    },
    CLOSING() {
        @Override
        public void enter(BaseGame baseGame) {
            super.enter(baseGame);
            NHG.logger.log(this, "Engine is closing.");
            baseGame.engineClosing();
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
