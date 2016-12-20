package io.github.voidzombie.nhglib.data.models;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;

/**
 * Created by worse on 19/12/2016.
 */
public class AssetJson implements JsonParseable<Asset> {
    public String alias;
    public String source;
    public Class assetClass;

    private Asset output;

    @Override
    public void parse(JsonValue jsonValue) {

    }

    @Override
    public Asset get() {
        return output;
    }
}
