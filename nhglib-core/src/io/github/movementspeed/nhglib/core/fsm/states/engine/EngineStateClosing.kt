package io.github.movementspeed.nhglib.core.fsm.states.engine

import com.artemis.BaseSystem
import com.artemis.utils.ImmutableBag
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.utils.Disposable
import io.github.movementspeed.nhglib.core.entry.NhgEntry
import io.github.movementspeed.nhglib.utils.debug.NhgLogger

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
class EngineStateClosing : State<NhgEntry> {
    override fun enter(nhgEntry: NhgEntry) {
        NhgLogger.log(this, "Engine is closing.")
        nhgEntry.onClose()

        val systems = nhgEntry.nhg.entities.entitySystems
        for (bs in systems) {
            if (bs is Disposable) {
                (bs as Disposable).dispose()
            }
        }

        nhgEntry.nhg.assets.dispose()
        nhgEntry.nhg.threading.terminate()
        Gdx.app.exit()
    }

    override fun update(entity: NhgEntry) {

    }

    override fun exit(entity: NhgEntry) {

    }

    override fun onMessage(entity: NhgEntry, telegram: Telegram): Boolean {
        return false
    }
}
