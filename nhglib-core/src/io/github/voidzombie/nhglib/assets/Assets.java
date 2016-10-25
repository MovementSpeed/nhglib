package io.github.voidzombie.nhglib.assets;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.enums.states.AssetsState;
import io.github.voidzombie.nhglib.interfaces.Notifiable;
import io.github.voidzombie.nhglib.interfaces.Updatable;
import io.github.voidzombie.nhglib.utils.data.Bundle;
import io.github.voidzombie.nhglib.utils.debug.Logger;

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
public class Assets implements Updatable, Notifiable, AssetErrorListener {
    public final DefaultStateMachine<Assets, AssetsState> fsm;
    public final AssetManager assetManager;

    private String modelsPath;

    private Array<Asset> assetList;
    private Array<AssetLoadingListener> listeners;

    public Assets() {
        fsm = new DefaultStateMachine<Assets, AssetsState>(this, AssetsState.IDLE);
        assetManager = new AssetManager();
        assetManager.setErrorListener(this);

        modelsPath = NHG.strings.nhgDefaultModelPath;

        assetList = new Array<Asset>();
        listeners = new Array<AssetLoadingListener>();
    }

    // Updatable
    @Override
    public void update() {
        fsm.update();
    }

    // Notifiable
    @Override
    public void onNotify(Bundle bundle) {
        Boolean finishedLoading = bundle.getBoolean(NHG.strings.nhgNotificationAssetLoadingFinished, false);

        if (finishedLoading) {
            notifyLoadingCompleted();
        }

        Boolean assetLoaded = bundle.getBoolean(NHG.strings.nhgNotificationAssetLoaded, false);

        if (assetLoaded) {
            Asset asset = (Asset) bundle.get("asset");
            notifyAssetLoaded(new Asset(asset));

            assetList.removeValue(asset, true);
        }
    }

    // AssetErrorListener
    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        Logger.log(this, throwable.getMessage());
    }

    public Array<Asset> getAssetList() {
        return assetList;
    }

    public void addAssetLoadingListener(AssetLoadingListener loadingListener) {
        if (loadingListener != null) {
            listeners.add(loadingListener);
        } else {
            Logger.log(this, NHG.strings.nhgMessageNullLoadingListener);
        }
    }

    public <T> T get(Asset asset) {
        return assetManager.get(asset.source);
    }

    public <T> T get(Asset asset, Class<T> type) {
        return assetManager.get(asset.source, type);
    }

    public void queueAsset(Asset asset) {
        FileHandle fileHandle = new FileHandle(asset.source);

        if (fileHandle.exists()) {
            assetManager.load(asset.source, Model.class);
            assetList.add(asset);
        } else {
            Logger.log(this, NHG.strings.nhgMessageCannotQueueAssetFileNotFound, asset.source);
        }
    }

    private void notifyLoadingCompleted() {
        for (AssetLoadingListener all : listeners) {
            all.onLoadingCompleted();
        }
    }

    private void notifyAssetLoaded(Asset asset) {
        for (AssetLoadingListener all : listeners) {
            all.onAssetLoaded(asset);
        }
    }
}
