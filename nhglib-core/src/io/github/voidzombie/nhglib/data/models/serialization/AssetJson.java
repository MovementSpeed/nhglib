package io.github.voidzombie.nhglib.data.models.serialization;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;
import io.github.voidzombie.nhglib.utils.scenes.SceneUtils;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class AssetJson implements JsonParseable<Asset> {
    private Asset output;

    @Override
    public void parse(JsonValue jsonValue) {
        String alias = jsonValue.getString("alias");
        String source = jsonValue.getString("source");
        String classString = jsonValue.getString("classAlias");

        String dependenciesPath = jsonValue.getString(
                "dependenciesPath",
                getDefaultDependenciesPath(source));

        if (!dependenciesPath.endsWith("/")) {
            dependenciesPath += "/";
        }

        Class assetClass = SceneUtils.assetClassFromAlias(classString);

        output = new Asset(alias, source, assetClass);
        output.dependenciesPath = dependenciesPath;
    }

    @Override
    public Asset get() {
        return output;
    }

    private String getDefaultDependenciesPath(String sourcePath) {
        String res = sourcePath;

        if (sourcePath.contains("/")) {
            res = sourcePath.substring(0, sourcePath.lastIndexOf("/"));
        }

        return res;
    }
}
