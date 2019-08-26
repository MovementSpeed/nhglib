package io.github.movementspeed.nhglib.core.fsm.states.assets

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.utils.Timer
import io.github.movementspeed.nhglib.assets.Assets
import io.github.movementspeed.nhglib.core.fsm.base.AssetsStates
import io.github.movementspeed.nhglib.utils.debug.NhgLogger

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
class AssetStateIdle : State<Assets> {
    init {
        MessageManager.getInstance().addListener({
            startGcTask()
            true
        }, AssetsStates.ASSETS_GC)
    }

    override fun enter(assets: Assets) {
        NhgLogger.log(this, "Asset manager is idle.")
    }

    override fun update(assets: Assets) {
        if (!assets.updateAssetManagers()) {
            assets.fsm.changeState(AssetsStates.LOADING)
        }
    }

    override fun exit(entity: Assets) {}

    override fun onMessage(entity: Assets, telegram: Telegram): Boolean {
        return false
    }

    private fun startGcTask() {
        Timer.schedule(object : Timer.Task() {
            override fun run() {
                Runtime.getRuntime().gc()
                NhgLogger.log("Assets", "gc()")
            }
        }, 10f, 10f, 1)
    }
}
