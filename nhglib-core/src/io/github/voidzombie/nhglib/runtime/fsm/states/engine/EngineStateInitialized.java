package io.github.voidzombie.nhglib.runtime.fsm.states.engine;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.entry.NHGEntry;
import io.github.voidzombie.nhglib.runtime.fsm.base.EngineStates;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class EngineStateInitialized implements State<NHGEntry> {
    @Override
    public void enter(NHGEntry nhgEntry) {
        NHG.logger.log(this, "Engine is initialized.");

        nhgEntry.engineInitialized();
        nhgEntry.getFsm().changeState(EngineStates.RUNNING);
    }

    @Override
    public void update(NHGEntry entity) {

    }

    @Override
    public void exit(NHGEntry entity) {

    }

    @Override
    public boolean onMessage(NHGEntry entity, Telegram telegram) {
        return false;
    }
}
