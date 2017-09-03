package io.github.movementspeed.nhglib.runtime.fsm.states.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.movementspeed.nhglib.graphics.shaders.tiledForward.PBRShader;
import io.github.movementspeed.nhglib.runtime.entry.NhgEntry;
import io.github.movementspeed.nhglib.runtime.fsm.base.EngineStates;
import io.github.movementspeed.nhglib.runtime.messaging.Message;
import io.github.movementspeed.nhglib.utils.data.Strings;
import io.github.movementspeed.nhglib.utils.debug.NhgLogger;
import io.reactivex.functions.Consumer;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class EngineStateRunning implements State<NhgEntry> {
    @Override
    public void enter(final NhgEntry nhgEntry) {
        NhgLogger.log(this, "Engine is running.");

        nhgEntry.nhg.messaging.get(Strings.Events.engineDestroy)
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if (message.is(Strings.Events.engineDestroy)) {
                            if (!nhgEntry.getFsm().isInState(EngineStates.CLOSING)) {
                                nhgEntry.getFsm().changeState(EngineStates.CLOSING);
                            }
                        }
                    }
                });
    }

    @Override
    public void update(NhgEntry nhgEntry) {
        float delta = Gdx.graphics.getDeltaTime();

        if (PBRShader.counters != null)
            PBRShader.counters.tick(delta);

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
