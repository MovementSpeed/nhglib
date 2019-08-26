package io.github.movementspeed.nhglib.core.fsm.states.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import io.github.movementspeed.nhglib.core.entry.NhgEntry
import io.github.movementspeed.nhglib.core.fsm.base.EngineStates
import io.github.movementspeed.nhglib.core.messaging.Message
import io.github.movementspeed.nhglib.utils.data.Strings
import io.github.movementspeed.nhglib.utils.debug.NhgLogger
import io.reactivex.functions.Consumer

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
class EngineStateRunning : State<NhgEntry> {
    override fun enter(nhgEntry: NhgEntry) {
        NhgLogger.log(this, "Engine is running.")

        nhgEntry.nhg.messaging.get(Strings.Events.engineDestroy)
                .subscribe { message ->
                    if (message.`is`(Strings.Events.engineDestroy)) {
                        if (!nhgEntry.fsm!!.isInState(EngineStates.CLOSING)) {
                            nhgEntry.fsm!!.changeState(EngineStates.CLOSING)
                        }
                    }
                }
    }

    override fun update(nhgEntry: NhgEntry) {
        val delta = Gdx.graphics.deltaTime

        MessageManager.getInstance().update()
        nhgEntry.nhg.assets.update()
        nhgEntry.nhg.entities.update(delta)

        nhgEntry.onUpdate(delta)
    }

    override fun exit(entity: NhgEntry) {}

    override fun onMessage(entity: NhgEntry, telegram: Telegram): Boolean {
        return false
    }
}
