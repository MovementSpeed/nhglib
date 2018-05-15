package io.github.movementspeed.nhglib.core.fsm.states.engine;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.movementspeed.nhglib.core.entry.NhgEntry;
import io.github.movementspeed.nhglib.core.fsm.base.EngineStates;
import io.github.movementspeed.nhglib.core.messaging.Message;
import io.github.movementspeed.nhglib.utils.data.Strings;
import io.reactivex.functions.Consumer;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class EngineStateStart implements State<NhgEntry> {
    @Override
    public void enter(final NhgEntry entity) {
        entity.nhg.messaging.get(Strings.Events.enginePause, Strings.Events.engineResume)
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) {
                        if (message.is(Strings.Events.enginePause)) {
                            if (!entity.getFsm().isInState(EngineStates.PAUSED)) {
                                entity.getFsm().changeState(EngineStates.PAUSED);
                            }
                        } else if (message.is(Strings.Events.engineResume)) {
                            if (!entity.getFsm().isInState(EngineStates.RUNNING)) {
                                entity.getFsm().changeState(EngineStates.RUNNING);
                            }
                        }
                    }
                });
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
