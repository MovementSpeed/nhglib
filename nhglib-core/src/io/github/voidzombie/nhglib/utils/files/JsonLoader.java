package io.github.voidzombie.nhglib.utils.files;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class JsonLoader extends AsynchronousAssetLoader<JsonValue, JsonLoader.JsonParameter> {
    public JsonLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, JsonParameter parameter) {
    }

    @Override
    public JsonValue loadSync(AssetManager manager, String fileName, FileHandle file, JsonParameter parameter) {
        return new JsonReader().parse(file);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, JsonParameter parameter) {
        return null;
    }

    public static class JsonParameter extends AssetLoaderParameters<JsonValue> {
    }
}
