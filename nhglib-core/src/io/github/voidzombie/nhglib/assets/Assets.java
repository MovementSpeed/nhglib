package io.github.voidzombie.nhglib.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.interfaces.Updatable;
import io.github.voidzombie.nhglib.runtime.fsm.base.AssetsStates;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.utils.data.Bundle;
import io.github.voidzombie.nhglib.utils.files.JsonLoader;
import io.github.voidzombie.nhglib.utils.files.SceneLoader;

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
public class Assets implements Updatable, AssetErrorListener {
    public final DefaultStateMachine<Assets, AssetsStates> fsm;
    public final AssetManager assetManager;

    private Array<Asset> assetList;

    public Assets() {
        fsm = new DefaultStateMachine<>(this, AssetsStates.IDLE);

        assetManager = new AssetManager();
        assetManager.setLoader(Scene.class, new SceneLoader(assetManager.getFileHandleResolver()));
        assetManager.setLoader(JsonValue.class, new JsonLoader(assetManager.getFileHandleResolver()));
        assetManager.setErrorListener(this);

        assetList = new Array<>();
    }

    // Updatable
    @Override
    public void update() {
        fsm.update();
    }

    // AssetErrorListener
    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        Nhg.logger.log(this, throwable.toString());
    }

    public void assetLoadingFinished() {
        Nhg.messaging.send(new Message(Nhg.strings.events.assetLoadingFinished));
    }

    public void assetLoaded(Asset asset) {
        Bundle bundle = new Bundle();
        bundle.put(Nhg.strings.defaults.assetKey, asset);

        Nhg.messaging.send(new Message(Nhg.strings.events.assetLoaded, bundle));
    }

    public void assetUnloaded(Asset asset) {
        Bundle bundle = new Bundle();
        bundle.put(Nhg.strings.defaults.assetKey, asset);

        Nhg.messaging.send(new Message(Nhg.strings.events.assetUnloaded, bundle));
    }

    public Array<Asset> getAssetList() {
        return assetList;
    }

    public <T> T get(Asset asset) {
        return assetManager.get(asset.source);
    }

    @SuppressWarnings("unchecked")
    public void queueAsset(Asset asset) {
        if (!assetManager.isLoaded(asset.source)) {
            FileHandle fileHandle = Gdx.files.internal(asset.source);

            if (fileHandle.exists()) {
                assetManager.load(asset.source, asset.assetClass);
                assetList.add(asset);
            } else {
                Nhg.logger.log(this, Nhg.strings.messages.cannotQueueAssetFileNotFound, asset.source);
            }
        } else {
            assetLoaded(asset);
        }
    }

    public void queueAssets(Array<Asset> assets) {
        if (assets != null) {
            for (Asset asset : assets) {
                queueAsset(asset);
            }
        }
    }

    public void dequeueAsset(Asset asset) {
        assetList.removeValue(asset, true);
    }

    public void unloadAsset(Asset asset) {
        if (asset != null) {
            if (assetManager.isLoaded(asset.source)) {
                assetManager.unload(asset.source);
                assetUnloaded(asset);
            }
        }
    }

    public void clearCompleted() {
        for (int i = 0; i < assetList.size; i++) {
            Asset asset = assetList.get(i);

            if (Nhg.assets.assetManager.isLoaded(asset.source)) {
                assetList.removeValue(asset, true);
            }
        }
    }

    public void clearQueue() {
        assetList.clear();
    }
}
