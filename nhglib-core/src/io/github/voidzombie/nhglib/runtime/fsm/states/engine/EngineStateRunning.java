package io.github.voidzombie.nhglib.runtime.fsm.states.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.entry.NHGEntry;
import io.github.voidzombie.nhglib.runtime.fsm.base.EngineStates;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.reactivex.functions.Consumer;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class EngineStateRunning implements State<NHGEntry> {
    @Override
    public void enter(final NHGEntry nhgEntry) {
        NHG.logger.log(this, "Engine is running.");

        NHG.messaging.get(NHG.strings.events.engineDestroy)
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if (message.is(NHG.strings.events.engineDestroy)) {
                            nhgEntry.getFsm().changeState(EngineStates.CLOSING);
                        }
                    }
                });
    }

    @Override
    public void update(NHGEntry nhgEntry) {
        float delta = Gdx.graphics.getDeltaTime();

        NHG.input.update();
        NHG.assets.update();
        NHG.entitySystem.update(delta);

        nhgEntry.engineUpdate(delta);
    }

    @Override
    public void exit(NHGEntry entity) {}

    @Override
    public boolean onMessage(NHGEntry entity, Telegram telegram) {
        return false;
    }
}
