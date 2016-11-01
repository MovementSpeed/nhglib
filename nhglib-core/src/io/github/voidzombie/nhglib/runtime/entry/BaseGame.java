package io.github.voidzombie.nhglib.runtime.entry;

import com.artemis.World;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.assets.AssetLoadingListener;
import io.github.voidzombie.nhglib.enums.states.GameState;
import io.github.voidzombie.nhglib.interfaces.Notifiable;
import io.github.voidzombie.nhglib.interfaces.Updatable;
import io.github.voidzombie.nhglib.utils.data.Bundle;

/**
 * Created by Fausto Napoli on 19/10/2016.
 * Extend this class to manage your own game.
 */
public class BaseGame extends Game implements Updatable, Notifiable, AssetLoadingListener {
    public final DefaultStateMachine<BaseGame, GameState> fsm;

    private World world;

    public BaseGame() {
        fsm = new DefaultStateMachine<BaseGame, GameState>(this, GameState.NOT_INITIALIZED);
    }

    @Override
    public void create() {
    }

    @Override
    public void update() {
        fsm.update();
    }

    @Override
    public void render() {
        update();
        super.render();
    }

    @Override
    public void onLoadingCompleted() {}

    @Override
    public void onAssetLoaded(Asset asset) {}

    @Override
    public void onNotification(Bundle bundle) {}

    public void onEngineInitialized() {

    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        if (world != null) {
            this.world = world;
        }
    }
}
