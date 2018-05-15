package io.github.movementspeed.nhglib.core.fsm.states.engine;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.movementspeed.nhglib.core.entry.NhgEntry;
import io.github.movementspeed.nhglib.core.fsm.base.EngineStates;
import io.github.movementspeed.nhglib.utils.debug.NhgLogger;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class EngineStateInitialized implements State<NhgEntry> {
    @Override
    public void enter(final NhgEntry nhgEntry) {
        NhgLogger.log(this, "Engine is initialized.");

        nhgEntry.onInitialized();
        nhgEntry.getFsm().changeState(EngineStates.RUNNING);
    }

    @Override
    public void update(NhgEntry entity) {

    }

    @Override
    public void exit(NhgEntry entity) {

    }

    @Override
    public boolean onMessage(NhgEntry entity, Telegram telegram) {
        return false;
    }
}
