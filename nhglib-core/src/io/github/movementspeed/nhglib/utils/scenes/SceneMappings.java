package io.github.movementspeed.nhglib.utils.scenes;

import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson;

import java.util.HashMap;

/**
 * Created by Fausto Napoli on 20/12/2016.
 */
public class SceneMappings {
    private static HashMap<String, Class<?>> assetClassesMapping = new HashMap<>();
    private static HashMap<String, Class<? extends ComponentJson>> componentJsonClassesMapping = new HashMap<>();

    public static void addAssetClassMapping(String type, Class<?> assetClass) {
        assetClassesMapping.put(type, assetClass);
    }

    public static void addComponentJsonMapping(String type, Class<? extends ComponentJson> componentClass) {
        componentJsonClassesMapping.put(type, componentClass);
    }

    public static Class assetClassFromAlias(String alias) {
        return assetClassesMapping.get(alias);
    }

    public static ComponentJson componentJsonFromType(String type) {
        ComponentJson componentJson = null;

        try {
            Class componentJsonClass = componentJsonClassFromType(type);
            componentJson = (ComponentJson) componentJsonClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return componentJson;
    }

    private static Class<? extends ComponentJson> componentJsonClassFromType(String type) {
        return componentJsonClassesMapping.get(type);
    }
}
