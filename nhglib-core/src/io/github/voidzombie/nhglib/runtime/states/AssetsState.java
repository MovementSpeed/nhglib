package io.github.voidzombie.nhglib.runtime.states;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.assets.Assets;

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
public enum AssetsState implements State<Assets> {
    IDLE() {
        @Override
        public void enter(Assets assets) {
            super.enter(assets);
            NHG.logger.log(this, "Asset manager is idle.");
        }

        @Override
        public void update(Assets assets) {
            super.update(assets);

            if (!assets.assetManager.update()) {
                assets.fsm.changeState(LOADING);
            }
        }
    },
    LOADING() {
        @Override
        public void enter(Assets assets) {
            super.enter(assets);
            NHG.logger.log(this, "Asset manager is loading.");
        }

        @Override
        public void update(Assets assets) {
            super.update(assets);

            if (assets.assetManager.update()) {
                assets.fsm.changeState(IDLE);
                
                assets.assetLoadingFinished();
                publishLoadedAssets(assets);
            }
        }

        @Override
        public void exit(Assets assets) {
            super.exit(assets);
            NHG.logger.log(this, "Asset manager has finished loading.");
        }

        private void publishLoadedAssets(Assets assets) {
            Array<Asset> assetsList = assets.getAssetList();

            for (Asset asset : assetsList) {
                Boolean isLoaded = assets.assetManager.isLoaded(asset.source);

                if (isLoaded) {
                    NHG.logger.log(this, NHG.strings.messages.assetLoaded, asset.source);
                    assets.assetLoaded(asset);
                }
            }
        }
    };

    @Override
    public void enter(Assets assets) {
    }

    @Override
    public void update(Assets assets) {
    }

    @Override
    public void exit(Assets assets) {
    }

    @Override
    public boolean onMessage(Assets assets, Telegram telegram) {
        return false;
    }
}
