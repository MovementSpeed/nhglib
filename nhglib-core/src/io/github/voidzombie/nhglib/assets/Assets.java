package io.github.voidzombie.nhglib.assets;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.assets.AssetManager;
import io.github.voidzombie.nhglib.enums.AssetsState;
import io.github.voidzombie.nhglib.interfaces.Updatable;

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
public class Assets implements Updatable {
    public final DefaultStateMachine<Assets, AssetsState> fsm;
    public final AssetManager assetManager;

    public Assets() {
        fsm = new DefaultStateMachine<Assets, AssetsState>(this, AssetsState.IDLE);
        assetManager = new AssetManager();
    }

    @Override
    public void update() {
        fsm.update();
    }
}
