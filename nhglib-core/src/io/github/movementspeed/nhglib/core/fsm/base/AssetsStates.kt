package io.github.movementspeed.nhglib.core.fsm.base

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import io.github.movementspeed.nhglib.assets.Assets
import io.github.movementspeed.nhglib.core.fsm.states.assets.AssetStateIdle
import io.github.movementspeed.nhglib.core.fsm.states.assets.AssetStateLoading

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
enum class AssetsStates private constructor(private val state: State<Assets>) : State<Assets> {
    IDLE(AssetStateIdle()),
    LOADING(AssetStateLoading());

    override fun enter(assets: Assets) {
        state.enter(assets)
    }

    override fun update(assets: Assets) {
        state.update(assets)
    }

    override fun exit(assets: Assets) {
        state.exit(assets)
    }

    override fun onMessage(assets: Assets, telegram: Telegram): Boolean {
        return state.onMessage(assets, telegram)
    }

    companion object {

        val ASSETS_GC = 0
    }
}
