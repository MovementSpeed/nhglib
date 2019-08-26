package io.github.movementspeed.nhglib.core.fsm.states.assets

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.assets.Asset
import io.github.movementspeed.nhglib.assets.Assets
import io.github.movementspeed.nhglib.core.fsm.base.AssetsStates
import io.github.movementspeed.nhglib.utils.data.Strings
import io.github.movementspeed.nhglib.utils.debug.NhgLogger
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Predicate

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
class AssetStateLoading : State<Assets> {
    override fun enter(assets: Assets) {
        NhgLogger.log(this, "Asset manager is loading.")
    }

    override fun update(assets: Assets) {
        if (assets.updateAssetManagers()) {
            assets.fsm.changeState(AssetsStates.IDLE)
            MessageManager.getInstance().dispatchMessage(AssetsStates.ASSETS_GC)

            assets.assetLoadingFinished()
            publishLoadedAssets(assets)
        }
    }

    override fun exit(assets: Assets) {
        NhgLogger.log(this, "Asset manager has finished loading.")
    }

    override fun onMessage(entity: Assets, telegram: Telegram): Boolean {
        return false
    }

    private fun publishLoadedAssets(assets: Assets) {
        val assetsCopy = Array(assets.assetQueue!!)

        Observable.fromIterable(assetsCopy)
                .filter { asset -> assets.isAssetLoaded(asset) }
                .subscribe(object : Consumer<Asset> {
                    override fun accept(asset: Asset) {
                        NhgLogger.log(
                                this,
                                Strings.Messages.assetLoaded,
                                asset.source)

                        assets.assetLoaded(asset)
                        assets.dequeueAsset(asset)
                    }
                })
    }
}
