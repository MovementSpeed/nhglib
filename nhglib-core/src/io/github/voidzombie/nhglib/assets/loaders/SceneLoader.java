package io.github.voidzombie.nhglib.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.data.models.serialization.SceneJson;
import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.runtime.ecs.utils.Entities;

import java.io.UnsupportedEncodingException;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class SceneLoader extends AsynchronousAssetLoader<Scene, SceneLoader.SceneParameter> {
    private Entities entities;

    public SceneLoader(Entities entities, FileHandleResolver resolver) {
        super(resolver);
        this.entities = entities;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, SceneParameter parameter) {
    }

    @Override
    public Scene loadSync(AssetManager manager, String fileName, FileHandle file, SceneParameter parameter) {
        return getScene(file.readBytes());
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, SceneParameter parameter) {
        return null;
    }

    public static class SceneParameter extends AssetLoaderParameters<Scene> {
    }

    private Scene getScene(byte[] bytes) {
        Scene scene = null;

        try {
            String json = new String(bytes, "UTF-8");

            SceneJson sceneJson = new SceneJson(entities);
            sceneJson.parse(new JsonReader().parse(json).get("scene"));

            scene = sceneJson.get();
        } catch (UnsupportedEncodingException e) {
            if (Nhg.debugLogs) e.printStackTrace();
        }

        return scene;
    }
}
