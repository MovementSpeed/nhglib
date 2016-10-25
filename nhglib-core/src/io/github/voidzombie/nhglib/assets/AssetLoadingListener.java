package io.github.voidzombie.nhglib.assets;

/**
 * Created by Fausto Napoli on 25/10/2016.
 */
public interface AssetLoadingListener {
    void onLoadingCompleted();
    void onAssetLoaded(Asset asset);
}
