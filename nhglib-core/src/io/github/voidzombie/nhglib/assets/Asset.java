package io.github.voidzombie.nhglib.assets;

import io.github.voidzombie.nhglib.NHG;

/**
 * Created by Fausto Napoli on 25/10/2016.
 */
public class Asset {
    public String alias;
    public String source;
    public Class assetClass;

    public Asset(Asset asset) {
        this(asset.alias, asset.source, asset.assetClass);
    }

    public Asset(String alias, String source, Class assetClass) {
        if (alias != null) {
            this.alias = alias;
        } else if (source != null) {
            this.alias = source;
        } else {
            throw new NullPointerException(NHG.strings.messages.nullAssetSource);
        }

        this.source = source;
        this.assetClass = assetClass;
    }

    public boolean is(String alias) {
        return alias != null && this.alias.contentEquals(alias);
    }

    public boolean isOfClass(Class assetClass) {
        return assetClass != null && this.assetClass == assetClass;
    }
}
