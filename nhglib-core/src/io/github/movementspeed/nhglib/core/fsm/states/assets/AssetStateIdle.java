package io.github.movementspeed.nhglib.core.fsm.states.assets;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.utils.Timer;
import io.github.movementspeed.nhglib.assets.Assets;
import io.github.movementspeed.nhglib.core.fsm.base.AssetsStates;
import io.github.movementspeed.nhglib.utils.debug.NhgLogger;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class AssetStateIdle implements State<Assets> {
    public AssetStateIdle() {
        MessageManager.getInstance().addListener(new Telegraph() {
            @Override
            public boolean handleMessage(Telegram msg) {
                startGcTask();
                return true;
            }
        }, AssetsStates.ASSETS_GC);
    }

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

    private void startGcTask() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Runtime.getRuntime().gc();
                NhgLogger.log("Assets", "gc()");
            }
        }, 10, 10, 1);
    }
}
