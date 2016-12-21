package io.github.voidzombie.nhglib.data.models;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;
import io.github.voidzombie.nhglib.utils.scenes.SceneUtils;

/**
 * Created by worse on 19/12/2016.
 */
public class AssetJson implements JsonParseable<Asset> {
    private Asset output;

    @Override
    public void parse(JsonValue jsonValue) {
        String alias = jsonValue.getString("alias");
        String source = jsonValue.getString("source");
        String classString = jsonValue.getString("classAlias");

        Class assetClass = SceneUtils.getInstance().assetClassFromClassAlias(classString);
        output = new Asset(alias, source, assetClass);
    }

    @Override
    public Asset get() {
        return output;
    }
}
