package io.github.voidzombie.nhglib.assets;

/**
 * Created by Fausto Napoli on 25/10/2016.
 */
public class Asset {
    public String alias;
    public String source;

    public Asset(Asset asset) {
        this(asset.alias, asset.source);
    }

    public Asset(String alias, String source) {
        this.alias = alias;
        this.source = source;
    }

    public boolean is(String alias) {
        return this.alias.contentEquals(alias);
    }
}
