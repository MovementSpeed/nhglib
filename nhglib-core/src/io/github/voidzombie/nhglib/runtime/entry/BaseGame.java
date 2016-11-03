package io.github.voidzombie.nhglib.runtime.entry;

import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.interfaces.AssetLoadingListener;
import io.github.voidzombie.nhglib.enums.states.EngineState;
import io.github.voidzombie.nhglib.interfaces.EngineConfigurationListener;
import io.github.voidzombie.nhglib.interfaces.EngineStateListener;

/**
 * Created by Fausto Napoli on 02/11/2016.
 */
public abstract class BaseGame implements
        ApplicationListener,
        AssetLoadingListener,
        EngineStateListener,
        EngineConfigurationListener {
    public final DefaultStateMachine<BaseGame, EngineState> fsm;
    private World entityWorld;

    @SuppressWarnings("unchecked")
    public BaseGame() {
        fsm = new DefaultStateMachine<BaseGame, EngineState>(this, EngineState.START);
    }

    @Override
    public final void create() {}

    @Override
    public final void resize(int width, int height) {}

    @Override
    public final void render() {
        fsm.update();
    }

    @Override
    public final void pause() {}

    @Override
    public final void resume() {}

    @Override
    public final void dispose() {}

    @Override
    public void onEngineStart() {}

    @Override
    public void onEngineInitialized() {}

    @Override
    public void onEngineRunning() {}

    @Override
    public void onEnginePaused() {}

    @Override
    public void onLoadingCompleted() {}

    @Override
    public void onAssetLoaded(Asset asset) {}

    @Override
    public void onConfigureEntitySystems(WorldConfigurationBuilder configurationBuilder) {}

    protected final int createEntity() {
        return entityWorld.create();
    }

    protected final <T extends Component> T createComponent(int entity, Class<T> type) {
        return entityWorld.getMapper(type).create(entity);
    }

    public final World getEntityWorld() {
        return entityWorld;
    }

    public final void setEntityWorld(World entityWorld) {
        this.entityWorld = entityWorld;
    }
}
