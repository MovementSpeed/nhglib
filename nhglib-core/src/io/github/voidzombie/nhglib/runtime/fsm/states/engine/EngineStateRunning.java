package io.github.voidzombie.nhglib.runtime.fsm.states.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.entry.NHGEntry;
import io.github.voidzombie.nhglib.runtime.fsm.base.EngineStates;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class EngineStateRunning implements State<NHGEntry> {
    @Override
    public void enter(NHGEntry nhgEntry) {
        NHG.logger.log(this, "Engine is running.");

        NHG.messaging.get(NHG.strings.events.engineDestroy)
                .subscribe(message -> {
                    if (message.is(NHG.strings.events.engineDestroy)) {
                        nhgEntry.getFsm().changeState(EngineStates.CLOSING);
                    }
                });
    }

    @Override
    public void update(NHGEntry nhgEntry) {
        NHG.assets.update();
        NHG.entitySystem.update(Gdx.graphics.getDeltaTime());

        nhgEntry.engineUpdate();
    }

    @Override
    public void exit(NHGEntry entity) {}

    @Override
    public boolean onMessage(NHGEntry entity, Telegram telegram) {
        return false;
    }
}
