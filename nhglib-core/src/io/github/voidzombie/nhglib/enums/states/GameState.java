package io.github.voidzombie.nhglib.enums.states;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.BaseGame;
import io.github.voidzombie.nhglib.utils.debug.Logger;

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

            // Create all BaseGame specific objects here, then go to the INITIALIZED state
            NHG.assets.addAssetLoadingListener(entity);
            entity.fsm.changeState(INITIALIZED);
        }
    },
    INITIALIZED() {
        @Override
        public void enter(BaseGame entity) {
            super.enter(entity);
            NHG.logger.log(this, "Engine is initialized.");
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
