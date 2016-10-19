package io.github.voidzombie.nhglib.enums;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.voidzombie.nhglib.assets.Assets;
import io.github.voidzombie.nhglib.utils.Logger;

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
public enum AssetsState implements State<Assets> {
    IDLE() {
        @Override
        public void enter(Assets entity) {
            super.enter(entity);
            Logger.log(this, "Asset manager is idle.");
        }

        @Override
        public void update(Assets entity) {
            super.update(entity);

            if (!entity.assetManager.update()) {
                entity.fsm.changeState(LOADING);
            }
        }
    },
    LOADING() {
        @Override
        public void enter(Assets entity) {
            super.enter(entity);
            Logger.log(this, "Asset manager is loading.");
        }

        @Override
        public void update(Assets entity) {
            super.update(entity);

            if (entity.assetManager.update()) {
                entity.fsm.changeState(IDLE);
            }
        }

        @Override
        public void exit(Assets entity) {
            super.exit(entity);
            Logger.log(this, "Asset manager has finished loading.");
        }
    };

    @Override
    public void enter(Assets entity) {

    }

    @Override
    public void update(Assets entity) {

    }

    @Override
    public void exit(Assets entity) {

    }

    @Override
    public boolean onMessage(Assets entity, Telegram telegram) {
        return false;
    }
}
