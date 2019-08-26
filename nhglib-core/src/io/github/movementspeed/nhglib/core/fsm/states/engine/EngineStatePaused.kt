package io.github.movementspeed.nhglib.core.fsm.states.engine

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import io.github.movementspeed.nhglib.core.entry.NhgEntry
import io.github.movementspeed.nhglib.utils.debug.NhgLogger

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
class EngineStatePaused : State<NhgEntry> {
    override fun enter(nhgEntry: NhgEntry) {
        NhgLogger.log(this, "Engine is paused.")
        nhgEntry.onPause()
    }

    override fun update(nhgEntry: NhgEntry) {

    }

    override fun exit(entity: NhgEntry) {}

    override fun onMessage(entity: NhgEntry, telegram: Telegram): Boolean {
        return false
    }
}
