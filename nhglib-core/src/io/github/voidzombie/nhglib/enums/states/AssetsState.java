package io.github.voidzombie.nhglib.enums.states;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.assets.Assets;
import io.github.voidzombie.nhglib.utils.data.Bundle;

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
public enum AssetsState implements State<Assets> {
    IDLE() {
        @Override
        public void enter(Assets entity) {
            super.enter(entity);
            NHG.logger.log(this, "Asset manager is idle.");
        }

        @Override
        public void update(Assets entity) {
            super.update(entity);

            if (!entity.assetManager.update()) {
                entity.fsm.changeState(LOADING);
            }
        }
    },
    LOADING() {
        @Override
        public void enter(Assets entity) {
            super.enter(entity);
            NHG.logger.log(this, "Asset manager is loading.");
        }

        @Override
        public void update(Assets entity) {
            super.update(entity);

            if (entity.assetManager.update()) {
                entity.fsm.changeState(IDLE);

                Bundle bundle = new Bundle();
                bundle.put(NHG.strings.notifications.assetLoadingFinished, true);

                checkLoadedAsset(entity);

                entity.onNotification(bundle);
            }
        }

        @Override
        public void exit(Assets entity) {
            super.exit(entity);
            NHG.logger.log(this, "Asset manager has finished loading.");
        }

        private void checkLoadedAsset(Assets entity) {
            Array<Asset> assets = entity.getAssetList();

            for (Asset asset : assets) {
                Boolean isLoaded = entity.assetManager.isLoaded(asset.source);

                if (isLoaded) {
                    NHG.logger.log(this, NHG.strings.messages.assetLoaded, asset.source);

                    Bundle bundle = new Bundle();
                    bundle.put(NHG.strings.notifications.assetLoaded, true);
                    bundle.put("asset", asset);

                    entity.onNotification(bundle);
                    break;
                }
            }
        }
    };

    @Override
    public void enter(Assets entity) {}

    @Override
    public void update(Assets entity) {}

    @Override
    public void exit(Assets entity) {}

    @Override
    public boolean onMessage(Assets entity, Telegram telegram) {
        return false;
    }
}
