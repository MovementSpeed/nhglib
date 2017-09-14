package io.github.movementspeed.nhglib.runtime.fsm.states.engine;

import com.artemis.BaseSystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.utils.Disposable;
import io.github.movementspeed.nhglib.runtime.entry.NhgEntry;
import io.github.movementspeed.nhglib.utils.debug.NhgLogger;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class EngineStateClosing implements State<NhgEntry> {
    @Override
    public void enter(NhgEntry nhgEntry) {
        NhgLogger.log(this, "Engine is closing.");
        nhgEntry.onClose();

        ImmutableBag<BaseSystem> systems = nhgEntry.nhg.entities.getEntitySystems();
        for (BaseSystem bs : systems) {
            if (bs instanceof Disposable) {
                ((Disposable) bs).dispose();
            }
        }

        nhgEntry.nhg.assets.dispose();
        nhgEntry.nhg.threading.terminate();
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
