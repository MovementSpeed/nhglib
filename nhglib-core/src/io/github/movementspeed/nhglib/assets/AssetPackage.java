package io.github.movementspeed.nhglib.assets;

import com.badlogic.gdx.utils.Array;

public class AssetPackage {
    public final String alias;

    private int remainingAssets;
    private Array<Asset> assets;

    public AssetPackage(String alias) {
        this.alias = alias;
        remainingAssets = 0;
        assets = new Array<>();
    }

    public void addAsset(Asset asset) {
        assets.add(asset);
        remainingAssets++;
    }

    public void addAsset(String alias, String source, Class assetClass) {
        addAsset(new Asset(alias, source, assetClass));
    }

    public boolean decreaseAndCheckRemaining() {
        remainingAssets--;
        return remainingAssets == 0;
    }

    public boolean containsAsset(String alias) {
        boolean contained = false;

        for (Asset asset : assets) {
            if (asset.is(alias)) {
                contained = true;
                break;
            }
        }

        return contained;
    }

    public Array<Asset> getAssets() {
        return assets;
    }
}
