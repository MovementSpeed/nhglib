package io.github.movementspeed.nhglib.core.fsm.base

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import io.github.movementspeed.nhglib.core.entry.NhgEntry
import io.github.movementspeed.nhglib.core.fsm.states.engine.*

/**
 * Created by Fausto Napoli on 19/10/2016.
 * Manages engine life cycle and handles boilerplate code in the mean time.
 */
enum class EngineStates private constructor(private val state: State<NhgEntry>) : State<NhgEntry> {
    START(EngineStateStart()),
    NOT_INITIALIZED(EngineStateNotInitialized()),
    INITIALIZED(EngineStateInitialized()),
    RUNNING(EngineStateRunning()),
    PAUSED(EngineStatePaused()),
    CLOSING(EngineStateClosing());

    override fun enter(nhgEntry: NhgEntry) {
        state.enter(nhgEntry)
    }

    override fun update(nhgEntry: NhgEntry) {
        state.update(nhgEntry)
    }

    override fun exit(nhgEntry: NhgEntry) {
        state.exit(nhgEntry)
    }

    override fun onMessage(nhgEntry: NhgEntry, telegram: Telegram): Boolean {
        return state.onMessage(nhgEntry, telegram)
    }
}
