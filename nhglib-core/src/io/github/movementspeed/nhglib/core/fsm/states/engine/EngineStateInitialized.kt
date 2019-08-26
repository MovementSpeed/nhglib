package io.github.movementspeed.nhglib.core.fsm.states.engine

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import io.github.movementspeed.nhglib.core.entry.NhgEntry
import io.github.movementspeed.nhglib.core.fsm.base.EngineStates
import io.github.movementspeed.nhglib.utils.debug.NhgLogger

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
class EngineStateInitialized : State<NhgEntry> {
    override fun enter(nhgEntry: NhgEntry) {
        NhgLogger.log(this, "Engine is initialized.")

        nhgEntry.onInitialized()
        nhgEntry.fsm!!.changeState(EngineStates.RUNNING)
    }

    override fun update(entity: NhgEntry) {

    }

    override fun exit(entity: NhgEntry) {

    }

    override fun onMessage(entity: NhgEntry, telegram: Telegram): Boolean {
        return false
    }
}
