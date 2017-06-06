package io.github.voidzombie.nhglib.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.UBJsonReader;
import io.github.voidzombie.nhglib.assets.loaders.JsonLoader;
import io.github.voidzombie.nhglib.assets.loaders.NhgG3dModelLoader;
import io.github.voidzombie.nhglib.assets.loaders.SceneLoader;
import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.interfaces.Updatable;
import io.github.voidzombie.nhglib.runtime.ecs.utils.Entities;
import io.github.voidzombie.nhglib.runtime.fsm.base.AssetsStates;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.runtime.messaging.Messaging;
import io.github.voidzombie.nhglib.utils.data.Bundle;
import io.github.voidzombie.nhglib.utils.data.Strings;
import io.github.voidzombie.nhglib.utils.debug.Logger;

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
public class Assets implements Updatable, AssetErrorListener {
    public final DefaultStateMachine<Assets, AssetsStates> fsm;
    public AssetManager assetManager;

    private Messaging messaging;
    private ArrayMap<String, Asset> assetCache;
    private Array<Asset> assetQueue;

    public Assets(Messaging messaging, Entities entities) {
        this.messaging = messaging;
        fsm = new DefaultStateMachine<>(this, AssetsStates.IDLE);

        assetManager = new AssetManager();
        assetManager.setLoader(Scene.class, new SceneLoader(entities, assetManager.getFileHandleResolver()));
        assetManager.setLoader(JsonValue.class, new JsonLoader(assetManager.getFileHandleResolver()));

        assetManager.setLoader(Model.class, ".g3db", new NhgG3dModelLoader(this,
                new UBJsonReader(), assetManager.getFileHandleResolver()));

        assetManager.setErrorListener(this);

        assetQueue = new Array<>();
        assetCache = new ArrayMap<>();

        Texture.setAssetManager(assetManager);
    }

    // Updatable
    @Override
    public void update() {
        fsm.update();
    }

    // AssetErrorListener
    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        throw new RuntimeException(throwable);
    }

    public void assetLoadingFinished() {
        messaging.send(new Message(Strings.Events.assetLoadingFinished));
    }

    public void assetLoaded(Asset asset) {
        Bundle bundle = new Bundle();
        bundle.put(Strings.Defaults.assetKey, asset);

        messaging.send(new Message(Strings.Events.assetLoaded, bundle));
    }

    public void assetUnloaded(Asset asset) {
        Bundle bundle = new Bundle();
        bundle.put(Strings.Defaults.assetKey, asset);

        messaging.send(new Message(Strings.Events.assetUnloaded, bundle));
    }

    public Array<Asset> getAssetQueue() {
        return assetQueue;
    }

    public <T> T get(String alias) {
        return get(assetCache.get(alias));
    }

    public <T> T get(Asset asset) {
        return assetManager.get(asset.source);
    }

    public Asset getAsset(String alias) {
        return assetCache.get(alias);
    }

    public ArrayMap.Values<Asset> getCachedAssets() {
        return assetCache.values();
    }

    /**
     * Loads an asset in an asynchronous way.
     *
     * @param asset
     */
    public void queueAsset(Asset asset) {
        queueAsset(asset, true);
    }

    /**
     * Loads an asset in a synchronized way.
     *
     * @param asset the asset.
     */
    public void loadAsset(Asset asset) {
        queueAsset(asset, false);
    }

    @SuppressWarnings("unchecked")
    private void queueAsset(Asset asset, boolean async) {
        assetCache.put(asset.alias, asset);

        if (!assetManager.isLoaded(asset.source)) {
            FileHandle fileHandle = Gdx.files.internal(asset.source);

            if (fileHandle.exists()) {
                if (asset.parameters == null) {
                    assetManager.load(asset.source, asset.assetClass);
                } else {
                    assetManager.load(asset.source, asset.assetClass, asset.parameters);
                }

                if (async) {
                    assetQueue.add(asset);
                } else {
                    assetManager.finishLoadingAsset(asset.source);
                    assetLoaded(asset);
                }
            } else {
                Logger.log(this, Strings.Messages.cannotQueueAssetFileNotFound, asset.source);
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
        assetQueue.removeValue(asset, true);
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
        for (int i = 0; i < assetQueue.size; i++) {
            Asset asset = assetQueue.get(i);

            if (assetManager.isLoaded(asset.source)) {
                assetQueue.removeValue(asset, true);
            }
        }
    }

    public void clearQueue() {
        assetQueue.clear();
    }

    public void dispose() {
        if (assetManager != null) {
            assetManager.dispose();
            assetManager = null;
        }
    }
}
