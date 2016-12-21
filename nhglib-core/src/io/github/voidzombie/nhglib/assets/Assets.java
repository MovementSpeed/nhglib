package io.github.voidzombie.nhglib.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.interfaces.Updatable;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.runtime.fsm.base.AssetsStates;
import io.github.voidzombie.nhglib.utils.data.Bundle;
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
        NHG.logger.log(this, throwable.getMessage());
    }

    public void assetLoadingFinished() {
        NHG.messaging.send(new Message(NHG.strings.events.assetLoadingFinished));
    }

    public void assetLoaded(Asset asset) {
        Bundle bundle = new Bundle();
        bundle.put(NHG.strings.defaults.assetKey, asset);

        NHG.messaging.send(new Message(NHG.strings.events.assetLoaded, bundle));
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
                NHG.logger.log(this, NHG.strings.messages.cannotQueueAssetFileNotFound, asset.source);
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

    public void clearQueue() {
        assetList.clear();
    }
}
