package io.github.movementspeed.nhglib.core.fsm.states.engine

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import io.github.movementspeed.nhglib.core.entry.NhgEntry
import io.github.movementspeed.nhglib.core.fsm.base.EngineStates

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
class EngineStateStart : State<NhgEntry> {
    override fun enter(entity: NhgEntry) {}

    override fun update(nhgEntry: NhgEntry) {
        nhgEntry.onStart()
        nhgEntry.fsm!!.changeState(EngineStates.NOT_INITIALIZED)
    }

    override fun exit(entity: NhgEntry) {}

    override fun onMessage(entity: NhgEntry, telegram: Telegram): Boolean {
        return false
    }
}
