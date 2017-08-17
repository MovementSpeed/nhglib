package io.github.movementspeed.nhglib.assets;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import io.github.movementspeed.nhglib.utils.data.Strings;

/**
 * Created by Fausto Napoli on 25/10/2016.
 */
public class Asset {
    public String alias;
    public String source;
    public String dependenciesPath;

    public Class assetClass;
    public AssetLoaderParameters parameters;

    public Asset(Asset asset) {
        this(asset.alias, asset.source, asset.assetClass);
    }

    public Asset(String alias, String source, Class assetClass, AssetLoaderParameters parameters) {
        this(alias, source, assetClass);
        this.parameters = parameters;
    }

    public Asset(String alias, String source, Class assetClass) {
        if (alias != null) {
            this.alias = alias;
        } else if (source != null) {
            this.alias = source;
        } else {
            throw new NullPointerException(Strings.Messages.nullAssetSource);
        }

        this.source = source;
        this.assetClass = assetClass;

        if (source.contains("/")) {
            this.dependenciesPath = source.substring(0, source.lastIndexOf("/") + 1);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (!(o instanceof Asset)) {
            return false;
        }

        Asset asset = (Asset) o;
        return alias.contentEquals(asset.alias);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 991 * result + alias.hashCode();
        result = 991 * result + source.hashCode();
        result = 991 * result + dependenciesPath.hashCode();
        return result;
    }

    public void setDependenciesPath(String dependenciesPath) {
        if (dependenciesPath != null && !dependenciesPath.isEmpty()) {
            this.dependenciesPath = dependenciesPath;
        }
    }

    public boolean is(String alias) {
        return alias != null && this.alias.contentEquals(alias);
    }

    public boolean is(Asset asset) {
        return asset != null && this.alias.contentEquals(asset.alias);
    }

    public boolean isType(Class assetClass) {
        return assetClass != null && this.assetClass == assetClass;
    }
}
