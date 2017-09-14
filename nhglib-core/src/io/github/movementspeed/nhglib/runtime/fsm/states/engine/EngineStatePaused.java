package io.github.movementspeed.nhglib.runtime.fsm.states.engine;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.movementspeed.nhglib.runtime.entry.NhgEntry;
import io.github.movementspeed.nhglib.utils.debug.NhgLogger;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class EngineStatePaused implements State<NhgEntry> {
    @Override
    public void enter(final NhgEntry nhgEntry) {
        NhgLogger.log(this, "Engine is paused.");
        nhgEntry.onPause();
    }

    @Override
    public void update(NhgEntry nhgEntry) {

    }

    @Override
    public void exit(NhgEntry entity) {
    }

    @Override
    public boolean onMessage(NhgEntry entity, Telegram telegram) {
        return false;
    }
}
