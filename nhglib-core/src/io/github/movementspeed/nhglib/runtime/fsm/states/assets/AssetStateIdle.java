package io.github.movementspeed.nhglib.runtime.fsm.states.assets;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.movementspeed.nhglib.assets.Assets;
import io.github.movementspeed.nhglib.runtime.fsm.base.AssetsStates;
import io.github.movementspeed.nhglib.utils.debug.NhgLogger;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class AssetStateIdle implements State<Assets> {
    @Override
    public void enter(Assets assets) {
        NhgLogger.log(this, "Asset manager is idle.");
    }

    @Override
    public void update(Assets assets) {
        if (!assets.assetManager.update()) {
            assets.fsm.changeState(AssetsStates.LOADING);
        }
    }

    @Override
    public void exit(Assets entity) {
    }

    @Override
    public boolean onMessage(Assets entity, Telegram telegram) {
        return false;
    }
}
