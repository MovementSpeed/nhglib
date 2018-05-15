package io.github.movementspeed.nhglib.core.fsm.states.engine;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.movementspeed.nhglib.core.entry.NhgEntry;
import io.github.movementspeed.nhglib.core.fsm.base.EngineStates;
import io.github.movementspeed.nhglib.core.messaging.Message;
import io.github.movementspeed.nhglib.utils.data.Strings;
import io.github.movementspeed.nhglib.utils.debug.NhgLogger;
import io.reactivex.functions.Consumer;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class EngineStatePaused implements State<NhgEntry> {
    @Override
    public void enter(final NhgEntry nhgEntry) {
        NhgLogger.log(this, "Engine is paused.");
        nhgEntry.onPause();

        nhgEntry.nhg.messaging.get(Strings.Events.engineDestroy, Strings.Events.engineResume)
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) {
                        if (message.is(Strings.Events.engineDestroy)) {
                            if (!nhgEntry.getFsm().isInState(EngineStates.CLOSING)) {
                                nhgEntry.getFsm().changeState(EngineStates.CLOSING);
                            }
                        } else if (message.is(Strings.Events.engineResume)) {
                            if (!nhgEntry.getFsm().isInState(EngineStates.RUNNING)) {
                                nhgEntry.getFsm().changeState(EngineStates.RUNNING);
                            }
                        }
                    }
                });
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
