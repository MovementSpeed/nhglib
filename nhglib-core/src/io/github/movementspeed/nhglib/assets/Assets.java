package io.github.movementspeed.nhglib.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.*;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.assets.loaders.*;
import io.github.movementspeed.nhglib.core.fsm.base.AssetsStates;
import io.github.movementspeed.nhglib.core.messaging.Message;
import io.github.movementspeed.nhglib.files.HDRData;
import io.github.movementspeed.nhglib.graphics.scenes.Scene;
import io.github.movementspeed.nhglib.input.handler.InputProxy;
import io.github.movementspeed.nhglib.interfaces.Updatable;
import io.github.movementspeed.nhglib.utils.data.Bundle;
import io.github.movementspeed.nhglib.utils.data.Strings;
import io.github.movementspeed.nhglib.utils.debug.NhgLogger;
import io.reactivex.functions.Consumer;

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
public class Assets implements Updatable, AssetErrorListener {
    public DefaultStateMachine<Assets, AssetsStates> fsm;
    public AssetManager assetManager;
    public AssetManager syncAssetManager;

    private Nhg nhg;

    private Array<Asset> assetQueue;
    private ArrayMap<String, Asset> assetCache;
    private ArrayMap<String, AssetManager> customAssetManagers;

    public void init(Nhg nhg) {
        this.nhg = nhg;
        fsm = new DefaultStateMachine<>(this, AssetsStates.IDLE);

        assetQueue = new Array<>();
        assetCache = new ArrayMap<>();
        customAssetManagers = new ArrayMap<>();

        assetManager = new AssetManager();
        syncAssetManager = new AssetManager();

        setDefaultAssetLoaders(assetManager);
        setDefaultAssetLoaders(syncAssetManager);

        assetManager.setErrorListener(this);
        syncAssetManager.setErrorListener(this);

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
        try {
            throw throwable;
        } catch (Throwable throwable1) {
            throwable1.printStackTrace();
        }
    }

    public AssetManager addAssetManager(String name, FileHandleResolver fileHandleResolver) {
        AssetManager assetManager = new AssetManager(fileHandleResolver);
        setDefaultAssetLoaders(assetManager);
        customAssetManagers.put(name, assetManager);
        return assetManager;
    }

    public void assetLoadingFinished() {
        nhg.messaging.send(new Message(Strings.Events.assetLoadingFinished));
    }

    public void assetLoaded(Asset asset) {
        if (asset != null) {
            Bundle bundle = new Bundle();
            bundle.put(Strings.Defaults.assetKey, asset);

            nhg.messaging.send(new Message(Strings.Events.assetLoaded, bundle));
        }
    }

    public void assetUnloaded(Asset asset) {
        if (asset != null) {
            Bundle bundle = new Bundle();
            bundle.put(Strings.Defaults.assetKey, asset);

            nhg.messaging.send(new Message(Strings.Events.assetUnloaded, bundle));
        }
    }

    public boolean updateAssetManagers() {
        boolean res = true;

        for (ObjectMap.Entry<String, AssetManager> entry : customAssetManagers) {
            if (!entry.value.update()) {
                res = false;
                break;
            }
        }

        return assetManager.update() && res;
    }

    public boolean isAssetLoaded(Asset asset) {
        boolean res = false;

        for (ObjectMap.Entry<String, AssetManager> entry : customAssetManagers) {
            if (entry.value.isLoaded(asset.source)) {
                res = true;
                break;
            }
        }

        return assetManager.isLoaded(asset.source) || res;
    }

    public AssetManager getAssetManager(String name) {
        return customAssetManagers.get(name);
    }

    public Array<Asset> getAssetQueue() {
        return assetQueue;
    }

    public <T> T get(String alias) {
        T t = null;

        if (alias != null) {
            t = get(assetCache.get(alias));
        }

        return t;
    }

    public <T> T get(Asset asset) {
        T t = null;

        if (asset != null && isAssetLoaded(asset)) {
            if (assetManager.isLoaded(asset.source)) {
                t = assetManager.get(asset.source);
            }

            if (t == null) {
                for (ObjectMap.Entry<String, AssetManager> entry : customAssetManagers) {
                    if (entry.value.isLoaded(asset.source)) {
                        t = entry.value.get(asset.source);
                    }

                    if (t != null) {
                        break;
                    }
                }
            }
        }

        return t;
    }

    public Asset getAsset(String alias) {
        return alias == null ? null : assetCache.get(alias);
    }

    public Asset createAsset(String alias, String source, Class assetClass) {
        Asset asset = new Asset(alias, source, assetClass);
        assetCache.put(alias, asset);
        return asset;
    }

    public ArrayMap.Values<Asset> getCachedAssets() {
        return assetCache.values();
    }

    /**
     * Loads an asset with a direct callback.
     *
     * @param asset    the asset.
     * @param listener a listener for the asset loading.
     */
    public void loadAsset(final Asset asset, final AssetListener listener) {
        if (asset != null) {
            queueAsset(asset);

            nhg.messaging.get(Strings.Events.assetLoaded)
                    .subscribe(new Consumer<Message>() {
                        @Override
                        public void accept(Message message) throws Exception {
                            Asset loadedAsset = (Asset) message.data.get(Strings.Defaults.assetKey);

                            if (loadedAsset.is(asset)) {
                                if (listener != null) {
                                    listener.onAssetLoaded(asset);
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Loads an asset with a direct callback.
     *
     * @param asset    the asset.
     * @param listener a listener for the asset loading.
     */
    public void loadAsset(String assetManagerName, final Asset asset, final AssetListener listener) {
        if (asset != null) {
            queueAsset(assetManagerName, asset);

            nhg.messaging.get(Strings.Events.assetLoaded)
                    .subscribe(new Consumer<Message>() {
                        @Override
                        public void accept(Message message) {
                            Asset loadedAsset = (Asset) message.data.get(Strings.Defaults.assetKey);

                            if (loadedAsset.is(asset)) {
                                if (listener != null) {
                                    listener.onAssetLoaded(asset);
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Loads an asset in an asynchronous way.
     *
     * @param asset the asset.
     */
    @SuppressWarnings("unchecked")
    public void queueAsset(Asset asset) {
        if (asset != null) {
            assetCache.put(asset.alias, asset);

            if (!assetManager.isLoaded(asset.source)) {
                FileHandle fileHandle = Gdx.files.internal(asset.source);

                if (fileHandle.exists()) {
                    if (asset.parameters == null) {
                        assetManager.load(asset.source, asset.assetClass);
                    } else {
                        assetManager.load(asset.source, asset.assetClass, asset.parameters);
                    }

                    assetQueue.add(asset);
                } else {
                    NhgLogger.log(this, Strings.Messages.cannotQueueAssetFileNotFound, asset.source);
                }
            } else {
                assetLoaded(asset);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void queueAsset(String assetManagerName, Asset asset) {
        if (asset != null && assetManagerName != null) {
            AssetManager assetManager = customAssetManagers.get(assetManagerName);

            if (assetManager != null) {
                assetCache.put(asset.alias, asset);

                if (!assetManager.isLoaded(asset.source)) {
                    FileHandle fileHandle = assetManager.getFileHandleResolver().resolve(asset.source);

                    if (fileHandle.exists()) {
                        if (asset.parameters == null) {
                            assetManager.load(asset.source, asset.assetClass);
                        } else {
                            assetManager.load(asset.source, asset.assetClass, asset.parameters);
                        }

                        assetQueue.add(asset);
                    } else {
                        NhgLogger.log(this, Strings.Messages.cannotQueueAssetFileNotFound, asset.source);
                    }
                } else {
                    assetLoaded(asset);
                }
            }
        }
    }

    public void queueAssets(Array<Asset> assets) {
        if (assets != null) {
            for (Asset asset : assets) {
                queueAsset(asset);
            }
        }
    }

    public void queueAssets(String assetManagerName, Array<Asset> assets) {
        if (assets != null) {
            for (Asset asset : assets) {
                queueAsset(assetManagerName, asset);
            }
        }
    }

    public void queueAssetPackage(final AssetPackage assetPackage) {
        if (assetPackage != null) {
            queueAssets(assetPackage.getAssets());
            nhg.messaging.get(Strings.Events.assetLoaded)
                    .subscribe(new Consumer<Message>() {
                        @Override
                        public void accept(Message message) {
                            Asset asset = (Asset) message.data.get(Strings.Defaults.assetKey);
                            if (assetPackage.containsAsset(asset.alias)) {
                                if (assetPackage.decreaseAndCheckRemaining()) {
                                    Bundle bundle = new Bundle();
                                    bundle.put(Strings.Defaults.assetPackageKey, assetPackage);

                                    nhg.messaging.send(new Message(Strings.Events.assetPackageLoaded, bundle));
                                }
                            }
                        }
                    });
        }
    }

    public void queueAssetPackage(String assetManagerName, final AssetPackage assetPackage) {
        if (assetPackage != null) {
            queueAssets(assetManagerName, assetPackage.getAssets());

            nhg.messaging.get(Strings.Events.assetLoaded)
                    .subscribe(new Consumer<Message>() {
                        @Override
                        public void accept(Message message) {
                            Asset asset = (Asset) message.data.get(Strings.Defaults.assetKey);

                            if (assetPackage.containsAsset(asset.alias)) {
                                if (assetPackage.decreaseAndCheckRemaining()) {
                                    Bundle bundle = new Bundle();
                                    bundle.put(Strings.Defaults.assetPackageKey, assetPackage);

                                    nhg.messaging.send(new Message(Strings.Events.assetPackageLoaded, bundle));
                                }
                            }
                        }
                    });
        }
    }

    public <T> T loadAssetSync(Asset asset) {
        T t = null;

        if (asset != null) {
            assetCache.put(asset.alias, asset);

            if (!syncAssetManager.isLoaded(asset.source)) {
                FileHandle fileHandle = Gdx.files.internal(asset.source);

                if (fileHandle.exists()) {
                    if (asset.parameters == null) {
                        syncAssetManager.load(asset.source, asset.assetClass);
                    } else {
                        syncAssetManager.load(asset.source, asset.assetClass, asset.parameters);
                    }

                    syncAssetManager.finishLoading();
                    t = syncAssetManager.get(asset.source);
                } else {
                    NhgLogger.log(this, Strings.Messages.cannotQueueAssetFileNotFound, asset.source);
                }
            } else {
                t = syncAssetManager.get(asset.source);
            }
        }

        return t;
    }

    public <T> T loadAssetSync(String assetManagerName, Asset asset) {
        T t = null;

        if (asset != null && assetManagerName != null) {
            AssetManager syncAssetManager = customAssetManagers.get(assetManagerName);

            if (syncAssetManager != null) {
                assetCache.put(asset.alias, asset);

                if (!syncAssetManager.isLoaded(asset.source)) {
                    FileHandle fileHandle = syncAssetManager.getFileHandleResolver().resolve(asset.source);

                    if (fileHandle.exists()) {
                        if (asset.parameters == null) {
                            syncAssetManager.load(asset.source, asset.assetClass);
                        } else {
                            syncAssetManager.load(asset.source, asset.assetClass, asset.parameters);
                        }

                        syncAssetManager.finishLoading();
                        t = syncAssetManager.get(asset.source);
                    } else {
                        NhgLogger.log(this, Strings.Messages.cannotQueueAssetFileNotFound, asset.source);
                    }
                } else {
                    t = syncAssetManager.get(asset.source);
                }
            }
        }

        return t;
    }

    public void dequeueAsset(Asset asset) {
        if (asset != null) {
            assetQueue.removeValue(asset, true);
        }
    }

    public void unloadAsset(String alias) {
        if (alias != null && !alias.isEmpty()) {
            unloadAsset(getAsset(alias));
        }
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

        if (syncAssetManager != null) {
            syncAssetManager.dispose();
            syncAssetManager = null;
        }

        for (ObjectMap.Entry<String, AssetManager> entry : customAssetManagers) {
            if (entry.value != null) {
                entry.value.dispose();
            }
        }

        customAssetManagers.clear();
    }

    public boolean assetInQueue(Asset asset) {
        return asset != null && assetQueue.contains(asset, true);
    }

    private void setDefaultAssetLoaders(AssetManager assetManager) {
        FileHandleResolver resolver = assetManager.getFileHandleResolver();

        SceneLoader sceneLoader = new SceneLoader(nhg, resolver);
        InputLoader inputLoader = new InputLoader(resolver);
        JsonLoader jsonLoader = new JsonLoader(resolver);
        HDRLoader hdrLoader = new HDRLoader(resolver);
        NhgG3dModelLoader nhgG3dModelLoader = new NhgG3dModelLoader(this, new UBJsonReader(), resolver);

        assetManager.setLoader(Scene.class, sceneLoader);
        assetManager.setLoader(InputProxy.class, inputLoader);
        assetManager.setLoader(JsonValue.class, jsonLoader);
        assetManager.setLoader(HDRData.class, hdrLoader);
        assetManager.setLoader(Model.class, ".g3db", nhgG3dModelLoader);
    }

    public interface AssetListener {
        void onAssetLoaded(Asset asset);
    }
}
