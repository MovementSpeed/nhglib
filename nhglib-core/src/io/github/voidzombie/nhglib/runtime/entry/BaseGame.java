package io.github.voidzombie.nhglib.runtime.entry;

import com.artemis.BaseEntitySystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.runtime.fsm.base.EngineStates;
import io.github.voidzombie.nhglib.runtime.fsm.interfaces.EngineConfigurationListener;
import io.github.voidzombie.nhglib.runtime.fsm.interfaces.EngineStateListener;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.utils.data.Strings;

/**
 * Created by Fausto Napoli on 02/11/2016.
 */
abstract class BaseGame implements
        ApplicationListener,
        EngineStateListener,
        EngineConfigurationListener {

    public Nhg nhg;
    private DefaultStateMachine<NhgEntry, EngineStates> fsm;

    @Override
    public final void create() {
        nhg = new Nhg();
        nhg.init();
    }

    @Override
    public final void resize(int width, int height) {
    }

    @Override
    public final void render() {
        fsm.update();
    }

    @Override
    public final void pause() {
        fsm.changeState(EngineStates.PAUSED);
    }

    @Override
    public final void resume() {
        fsm.changeState(EngineStates.RUNNING);
    }

    @Override
    public final void dispose() {
        nhg.messaging.send(new Message(Strings.Events.engineDestroy));
    }

    @Override
    public void engineStarted() {
    }

    @Override
    public void engineInitialized() {
    }

    @Override
    public void engineUpdate(float delta) {
    }

    @Override
    public void enginePaused() {

    }

    @Override
    public void engineClosing() {
    }

    @Override
    public Array<BaseEntitySystem> onConfigureEntitySystems() {
        return new Array<>();
    }

    void init(NhgEntry nhgEntry) {
        fsm = new DefaultStateMachine<>(nhgEntry, EngineStates.START);
    }

    public final StateMachine<NhgEntry, EngineStates> getFsm() {
        return fsm;
    }
}
