package io.github.movementspeed.nhglib.core.fsm.states.engine;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.movementspeed.nhglib.core.entry.NhgEntry;
import io.github.movementspeed.nhglib.core.fsm.base.EngineStates;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class EngineStateStart implements State<NhgEntry> {
    @Override
    public void enter(final NhgEntry entity) {
    }

    @Override
    public void update(NhgEntry nhgEntry) {
        nhgEntry.onStart();
        nhgEntry.getFsm().changeState(EngineStates.NOT_INITIALIZED);
    }

    @Override
    public void exit(NhgEntry entity) {
    }

    @Override
    public boolean onMessage(NhgEntry entity, Telegram telegram) {
        return false;
    }
}
