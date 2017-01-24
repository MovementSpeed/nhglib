package io.github.voidzombie.nhglib.runtime.fsm.states.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.runtime.entry.NhgEntry;
import io.github.voidzombie.nhglib.runtime.fsm.base.EngineStates;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.reactivex.functions.Consumer;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class EngineStateRunning implements State<NhgEntry> {
    @Override
    public void enter(final NhgEntry nhgEntry) {
        Nhg.logger.log(this, "Engine is running.");

        Nhg.messaging.get(Nhg.strings.events.engineDestroy)
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if (message.is(Nhg.strings.events.engineDestroy)) {
                            nhgEntry.getFsm().changeState(EngineStates.CLOSING);
                        }
                    }
                });
    }

    @Override
    public void update(NhgEntry nhgEntry) {
        float delta = Gdx.graphics.getDeltaTime();

        Nhg.input.update();
        Nhg.assets.update();
        Nhg.entitySystem.update(delta);

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
