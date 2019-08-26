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
        output.parametersBundle = new Bundle();

        if (parameters != null) {
            for (JsonValue value : parameters) {
                JsonValue internalValue = value.get(0);

                if (internalValue.isBoolean()) {
                    output.parametersBundle.put(internalValue.name, internalValue.asBoolean());
                } else if (internalValue.isDouble()) {
                    output.parametersBundle.put(internalValue.name, internalValue.asDouble());
                } else if (internalValue.isLong()) {
                    output.parametersBundle.put(internalValue.name, internalValue.asLong());
                } else if (internalValue.isString()) {
                    output.parametersBundle.put(internalValue.name, internalValue.asString());
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
