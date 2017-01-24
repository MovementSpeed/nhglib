package io.github.voidzombie.nhglib.runtime.fsm.states.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.runtime.entry.NhgEntry;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class EngineStateClosing implements State<NhgEntry> {
    @Override
    public void enter(NhgEntry nhgEntry) {
        Nhg.logger.log(this, "Engine is closing.");
        nhgEntry.engineClosing();

        Nhg.threading.terminate();
        Gdx.app.exit();
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
