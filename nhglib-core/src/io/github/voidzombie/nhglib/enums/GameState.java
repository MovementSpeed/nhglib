package io.github.voidzombie.nhglib.enums;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.voidzombie.nhglib.runtime.NHGGame;
import io.github.voidzombie.nhglib.utils.Logger;

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
public enum GameState implements State<NHGGame> {
    NOT_INITIALIZED() {
        @Override
        public void enter(NHGGame entity) {
            super.enter(entity);
            Logger.log(this, "Engine is not initialized.");
        }

        @Override
        public void update(NHGGame entity) {
            super.update(entity);

            // Create all NHGGame specific objects here, then go to the INITIALIZED state
            entity.fsm.changeState(INITIALIZED);
        }
    },
    INITIALIZED() {
        @Override
        public void enter(NHGGame entity) {
            super.enter(entity);
            Logger.log(this, "Engine is initialized.");
        }

        @Override
        public void update(NHGGame entity) {
            super.update(entity);
        }
    },
    RUNNING() {
        @Override
        public void enter(NHGGame entity) {
            super.enter(entity);
            Logger.log(this, "Engine is running.");
        }

        @Override
        public void update(NHGGame entity) {
            super.update(entity);
        }
    },
    PAUSED() {
        @Override
        public void enter(NHGGame entity) {
            super.enter(entity);
            Logger.log(this, "Engine is paused.");
        }

        @Override
        public void update(NHGGame entity) {
            super.update(entity);
        }
    };

    @Override
    public void enter(NHGGame entity) {

    }

    @Override
    public void update(NHGGame entity) {

    }

    @Override
    public void exit(NHGGame entity) {

    }

    @Override
    public boolean onMessage(NHGGame entity, Telegram telegram) {
        return false;
    }
}
