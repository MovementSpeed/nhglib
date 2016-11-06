package io.github.voidzombie.nhglib.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.states.AssetsState;
import io.github.voidzombie.nhglib.interfaces.Notifiable;
import io.github.voidzombie.nhglib.interfaces.Updatable;
import io.github.voidzombie.nhglib.utils.data.Bundle;

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
public class Assets implements Updatable, Notifiable, AssetErrorListener {
    public final DefaultStateMachine<Assets, AssetsState> fsm;
    public final AssetManager assetManager;

    private Array<Asset> assetList;

    public Assets() {
        fsm = new DefaultStateMachine<Assets, AssetsState>(this, AssetsState.IDLE);
        assetManager = new AssetManager();
        assetManager.setErrorListener(this);

        assetList = new Array<Asset>();
    }

    // Updatable
    @Override
    public void update() {
        fsm.update();
    }

    // Notifiable
    @Override
    public void onNotification(Bundle bundle) {
        Boolean finishedLoading = bundle.getBoolean(NHG.strings.events.assetLoadingFinished, false);
        Boolean assetLoaded = bundle.getBoolean(NHG.strings.events.assetLoaded, false);

        if (finishedLoading) {
            NHG.messaging.broadcastEvent(NHG.strings.events.assetLoadingFinished);
        }

        if (assetLoaded) {
            Asset asset = (Asset) bundle.get("asset");
            NHG.messaging.broadcastEvent(NHG.strings.events.assetLoaded, bundle);

            assetList.removeValue(asset, true);
        }
    }

    // AssetErrorListener
    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        NHG.logger.log(this, throwable.getMessage());
    }

    public Array<Asset> getAssetList() {
        return assetList;
    }

    public <T> T get(Asset asset) {
        return assetManager.get(asset.source);
    }

    public void queueAsset(Asset asset) {
        FileHandle fileHandle = Gdx.files.internal(asset.source);

        if (fileHandle.exists()) {
            assetManager.load(asset.source, asset.assetClass);
            assetList.add(asset);
        } else {
            NHG.logger.log(this, NHG.strings.messages.cannotQueueAssetFileNotFound, asset.source);
        }
    }

    public void queueAssets(Array<Asset> assets) {
        if (assets != null) {
            for (Asset asset : assets) {
                queueAsset(asset);
            }
        }
    }
}
