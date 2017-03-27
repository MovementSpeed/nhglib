package io.github.voidzombie.nhglib.assets;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import io.github.voidzombie.nhglib.Nhg;

import java.util.Objects;

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

    public Asset(String alias, String source, Class assetClass) {
        if (alias != null) {
            this.alias = alias;
        } else if (source != null) {
            this.alias = source;
        } else {
            throw new NullPointerException(Nhg.strings.messages.nullAssetSource);
        }

        this.source = source;
        this.assetClass = assetClass;
    }

    public Asset(String alias, String source, Class assetClass, AssetLoaderParameters parameters) {
        if (alias != null) {
            this.alias = alias;
        } else if (source != null) {
            this.alias = source;
        } else {
            throw new NullPointerException(Nhg.strings.messages.nullAssetSource);
        }

        this.source = source;
        this.assetClass = assetClass;
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (!(o instanceof Asset)) {
            return false;
        }

        Asset asset = (Asset) o;
        return Objects.equals(alias, asset.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(alias);
    }

    public boolean is(String alias) {
        return alias != null && this.alias.contentEquals(alias);
    }

    public boolean isType(Class assetClass) {
        return assetClass != null && this.assetClass == assetClass;
    }
}
