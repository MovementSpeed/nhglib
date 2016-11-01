package io.github.voidzombie.nhglib.enums.states;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.ecs.systems.BroadcastSystem;
import io.github.voidzombie.nhglib.runtime.entry.BaseGame;

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
public enum GameState implements State<BaseGame> {
    NOT_INITIALIZED() {
        @Override
        public void enter(BaseGame entity) {
            super.enter(entity);
            NHG.logger.log(this, "Engine is not initialized.");
        }

        @Override
        public void update(BaseGame entity) {
            super.update(entity);

            // BaseGame can listen to asset loading events.
            NHG.assets.addAssetLoadingListener(entity);

            // BroadcastSystem can listen to Broadcaster events.
            BroadcastSystem broadcastSystem = new BroadcastSystem();
            NHG.broadcaster.addListener(broadcastSystem);

            // Setup the ECS' world.
            WorldConfiguration worldConfiguration = new WorldConfigurationBuilder()
                    .with(broadcastSystem)
                    .build();

            World world = new World(worldConfiguration);
            entity.setWorld(world);

            entity.fsm.changeState(INITIALIZED);
        }
    },
    INITIALIZED() {
        @Override
        public void enter(BaseGame entity) {
            super.enter(entity);
            NHG.logger.log(this, "Engine is initialized.");
            entity.onEngineInitialized();
        }

        @Override
        public void update(BaseGame entity) {
            super.update(entity);
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

            World world = entity.getWorld();
            world.setDelta(Gdx.graphics.getDeltaTime());
            world.process();
        }
    },
    PAUSED() {
        @Override
        public void enter(BaseGame entity) {
            super.enter(entity);
            NHG.logger.log(this, "Engine is paused.");
        }

        @Override
        public void update(BaseGame entity) {
            super.update(entity);
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
