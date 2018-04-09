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

    public void addAssets(Asset ... assets) {
        for (Asset asset : assets) {
            addAsset(asset);
        }
    }

    public void addAssets(Array<Asset> assets) {
        for (Asset asset : assets) {
            addAsset(asset);
        }
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

    public boolean is(String alias) {
        return alias != null && this.alias.contentEquals(alias);
    }

    public boolean is(AssetPackage assetPackage) {
        return assetPackage != null && this.alias.contentEquals(assetPackage.alias);
    }

    public Array<Asset> getAssets() {
        return assets;
    }
}
