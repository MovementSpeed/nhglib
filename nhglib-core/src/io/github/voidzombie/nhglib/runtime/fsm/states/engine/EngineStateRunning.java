package io.github.voidzombie.nhglib.runtime.fsm.states.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.voidzombie.nhglib.runtime.entry.NhgEntry;
import io.github.voidzombie.nhglib.runtime.fsm.base.EngineStates;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.utils.data.Strings;
import io.github.voidzombie.nhglib.utils.debug.Logger;
import io.reactivex.functions.Consumer;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class EngineStateRunning implements State<NhgEntry> {
    @Override
    public void enter(final NhgEntry nhgEntry) {
        Logger.log(this, "Engine is running.");

        nhgEntry.nhg.messaging.get(Strings.Events.engineDestroy)
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if (message.is(Strings.Events.engineDestroy)) {
                            nhgEntry.getFsm().changeState(EngineStates.CLOSING);
                        }
                    }
                });
    }

    @Override
    public void update(NhgEntry nhgEntry) {
        float delta = Gdx.graphics.getDeltaTime();

        nhgEntry.nhg.input.update();
        nhgEntry.nhg.assets.update();
        nhgEntry.nhg.entities.update(delta);

        nhgEntry.engineUpdate(delta);
    }

    @Override
    public void exit(NhgEntry entity) {
    }

    @Override
    public boolean onMessage(NhgEntry entity, Telegram telegram) {
        return false;
    }
}
