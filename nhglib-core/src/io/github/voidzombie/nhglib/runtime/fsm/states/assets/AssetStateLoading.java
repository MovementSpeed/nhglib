package io.github.voidzombie.nhglib.runtime.fsm.states.assets;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.assets.Assets;
import io.github.voidzombie.nhglib.runtime.fsm.base.AssetsStates;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class AssetStateLoading implements State<Assets> {
    @Override
    public void enter(Assets assets) {
        NHG.logger.log(this, "Asset manager is loading.");
    }

    @Override
    public void update(Assets assets) {
        if (assets.assetManager.update()) {
            assets.fsm.changeState(AssetsStates.IDLE);

            assets.assetLoadingFinished();
            publishLoadedAssets(assets);
        }
    }

    @Override
    public void exit(Assets assets) {
        NHG.logger.log(this, "Asset manager has finished loading.");
    }

    @Override
    public boolean onMessage(Assets entity, Telegram telegram) {
        return false;
    }

    private void publishLoadedAssets(final Assets assets) {
        Array<Asset> assetsCopy = new Array<>(assets.getAssetList());

        Observable.fromIterable(assetsCopy)
                .filter(new Predicate<Asset>() {
                    @Override
                    public boolean test(Asset asset) throws Exception {
                        return assets.assetManager.isLoaded(asset.source);
                    }
                })
                .subscribe(new Consumer<Asset>() {
                    @Override
                    public void accept(Asset asset) throws Exception {
                        NHG.logger.log(
                                this,
                                NHG.strings.messages.assetLoaded,
                                asset.source);

                        assets.assetLoaded(asset);
                        assets.dequeueAsset(asset);
                    }
                });
    }
}
