package io.github.voidzombie.nhglib.runtime.fsm.base;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.voidzombie.nhglib.runtime.entry.NHGEntry;
import io.github.voidzombie.nhglib.runtime.fsm.states.engine.*;

/**
 * Created by Fausto Napoli on 19/10/2016.
 * Manages engine life cycle and handles boilerplate code in the mean time.
 */
public enum EngineStates implements State<NHGEntry> {
    START(new EngineStateStart()),
    NOT_INITIALIZED(new EngineStateNotInitialized()),
    INITIALIZED(new EngineStateInitialized()),
    RUNNING(new EngineStateRunning()),
    CLOSING(new EngineStateClosing());

    private State<NHGEntry> state;

    EngineStates(State<NHGEntry> state) {
        this.state = state;
    }

    @Override
    public void enter(NHGEntry nhgEntry) {
        state.enter(nhgEntry);
    }

    @Override
    public void update(NHGEntry nhgEntry) {
        state.update(nhgEntry);
    }

    @Override
    public void exit(NHGEntry nhgEntry) {
        state.exit(nhgEntry);
    }

    @Override
    public boolean onMessage(NHGEntry nhgEntry, Telegram telegram) {
        return state.onMessage(nhgEntry, telegram);
    }
}
