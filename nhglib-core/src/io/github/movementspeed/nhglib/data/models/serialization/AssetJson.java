package io.github.movementspeed.nhglib.data.models.serialization;

import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.assets.Asset;
import io.github.movementspeed.nhglib.interfaces.JsonParseable;
import io.github.movementspeed.nhglib.utils.data.Bundle;
import io.github.movementspeed.nhglib.utils.scenes.SceneMappings;

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

        Class assetClass = SceneMappings.assetClassFromAlias(classString);

        JsonValue parameters = jsonValue.get("parameters");

        output = new Asset(alias, source, assetClass);
        output.dependenciesPath = dependenciesPath;

        if (parameters != null) {
            output.parametersBundle = new Bundle();

            for (JsonValue value : parameters) {
                if (value.isBoolean()) {
                    output.parametersBundle.put(value.name, value.asBoolean());
                } else if (value.isDouble()) {
                    output.parametersBundle.put(value.name, value.asDouble());
                } else if (value.isLong()) {
                    output.parametersBundle.put(value.name, value.asLong());
                } else if (value.isString()) {
                    output.parametersBundle.put(value.name, value.asString());
                }
            }
        }
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
