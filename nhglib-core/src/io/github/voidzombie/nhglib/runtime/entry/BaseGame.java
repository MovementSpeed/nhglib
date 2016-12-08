package io.github.voidzombie.nhglib.runtime.entry;

import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import io.github.voidzombie.nhglib.interfaces.EngineConfigurationListener;
import io.github.voidzombie.nhglib.interfaces.EngineStateListener;
import io.github.voidzombie.nhglib.runtime.fsm.base.EngineStates;

/**
 * Created by Fausto Napoli on 02/11/2016.
 */
abstract class BaseGame implements
        ApplicationListener,
        EngineStateListener,
        EngineConfigurationListener {
    private DefaultStateMachine<NHGEntry, EngineStates> fsm;

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
    public void engineStarted() {
    }

    @Override
    public void engineInitialized() {
    }

    @Override
    public void engineUpdate() {
    }

    @Override
    public void engineClosing() {
    }

    @Override
    public void onConfigureEntitySystems(WorldConfigurationBuilder configurationBuilder) {
    }

    void init(NHGEntry nhgEntry) {
        fsm = new DefaultStateMachine<NHGEntry, EngineStates>(nhgEntry, EngineStates.START);
    }

    public final StateMachine<NHGEntry, EngineStates> getFsm() {
        return fsm;
    }
}
