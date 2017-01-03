package io.github.voidzombie.nhglib.utils.scenes;

import io.github.voidzombie.nhglib.data.models.serialization.ComponentJson;

import java.util.HashMap;

/**
 * Created by Fausto Napoli on 20/12/2016.
 */
public class SceneUtils {
    private static SceneUtils instance;

    private HashMap<String, Class<?>> assetClassesMapping;
    private HashMap<String, Class<? extends ComponentJson>> componentJsonClassesMapping;

    public SceneUtils() {
        assetClassesMapping = new HashMap<>();
        componentJsonClassesMapping = new HashMap<>();
    }

    public static SceneUtils getInstance() {
        if (instance == null) {
            instance = new SceneUtils();
        }

        return instance;
    }

    public void addAssetClassMapping(String type, Class<?> componentClass) {
        assetClassesMapping.put(type, componentClass);
    }

    public void addComponentJsonMapping(String type, Class<? extends ComponentJson> componentClass) {
        componentJsonClassesMapping.put(type, componentClass);
    }

    public Class assetClassFromClassAlias(String alias) {
        return assetClassesMapping.get(alias);
    }

    public ComponentJson componentJsonFromType(String type) {
        ComponentJson componentJson = null;

        try {
            Class componentJsonClass = componentJsonClassFromType(type);
            componentJson = (ComponentJson) componentJsonClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return componentJson;
    }

    private Class<? extends ComponentJson> componentJsonClassFromType(String type) {
        return componentJsonClassesMapping.get(type);
    }
}
