package io.github.voidzombie.nhglib.runtime.fsm.states.engine;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.voidzombie.nhglib.runtime.entry.NHGEntry;
import io.github.voidzombie.nhglib.runtime.fsm.base.EngineStates;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class EngineStateStart implements State<NHGEntry> {
    @Override
    public void enter(NHGEntry entity) {}

    @Override
    public void update(NHGEntry nhgEntry) {
        nhgEntry.getFsm().changeState(EngineStates.NOT_INITIALIZED);
    }

    @Override
    public void exit(NHGEntry entity) {}

    @Override
    public boolean onMessage(NHGEntry entity, Telegram telegram) {
        return false;
    }
}
