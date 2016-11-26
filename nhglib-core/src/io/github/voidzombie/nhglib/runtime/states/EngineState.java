package io.github.voidzombie.nhglib.runtime.states;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.entry.NHGEntry;

/**
 * Created by Fausto Napoli on 19/10/2016.
 * Manages engine life cycle and handles boilerplate code in the mean time.
 */
public enum EngineState implements State<NHGEntry> {
    START() {
        @Override
        public void update(NHGEntry nhgEntry) {
            super.update(nhgEntry);
            nhgEntry.getFsm().changeState(NOT_INITIALIZED);
        }
    },
    NOT_INITIALIZED() {
        @Override
        public void enter(NHGEntry nhgEntry) {
            super.enter(nhgEntry);
            NHG.logger.log(this, "Engine is not initialized.");

            // Setup the ECS' world.
            WorldConfigurationBuilder configurationBuilder = new WorldConfigurationBuilder();
            nhgEntry.onConfigureEntitySystems(configurationBuilder);

            nhgEntry.setEntityWorld(new World(configurationBuilder.build()));
            nhgEntry.engineStarted();

            nhgEntry.getFsm().changeState(INITIALIZED);
        }
    },
    INITIALIZED() {
        @Override
        public void enter(NHGEntry nhgEntry) {
            super.enter(nhgEntry);
            NHG.logger.log(this, "Engine is initialized.");
            
            nhgEntry.engineInitialized();
            nhgEntry.getFsm().changeState(RUNNING);
        }
    },
    RUNNING() {
        @Override
        public void enter(NHGEntry nhgEntry) {
            super.enter(nhgEntry);
            NHG.logger.log(this, "Engine is running.");
        }

        @Override
        public void update(NHGEntry nhgEntry) {
            super.update(nhgEntry);
            NHG.assets.update();

            World world = nhgEntry.getEntityWorld();
            world.setDelta(Gdx.graphics.getDeltaTime());
            world.process();

            nhgEntry.engineUpdate();
        }
    },
    PAUSED() {
        @Override
        public void enter(NHGEntry nhgEntry) {
            super.enter(nhgEntry);
            NHG.logger.log(this, "Engine is paused.");
            nhgEntry.enginePaused();
        }
    },
    CLOSING() {
        @Override
        public void enter(NHGEntry nhgEntry) {
            super.enter(nhgEntry);
            NHG.logger.log(this, "Engine is closing.");
            nhgEntry.engineClosing();
        }
    };

    @Override
    public void enter(NHGEntry nhgEntry) {
    }

    @Override
    public void update(NHGEntry nhgEntry) {
    }

    @Override
    public void exit(NHGEntry nhgEntry) {
    }

    @Override
    public boolean onMessage(NHGEntry nhgEntry, Telegram telegram) {
        return false;
    }
}
