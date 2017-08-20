package io.github.movementspeed.nhglib.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.files.HDRData;

import java.io.File;
import java.io.IOException;

/**
 * Created by Fausto Napoli on 20/08/2017.
 */
public class HDRLoader extends AsynchronousAssetLoader<HDRData, HDRLoader.HDRParams> {
    public HDRLoader(FileHandleResolver fileHandleResolver) {
        super(fileHandleResolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, HDRParams parameter) {

    }

    @Override
    public HDRData loadSync(AssetManager manager, String fileName, FileHandle file, HDRParams parameter) {
        return loadHDR(file.file());
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, HDRParams parameter) {
        return null;
    }

    public HDRData loadHDR(File file) {
        HDRData hdrData = null;

        try {
            hdrData = new HDRData(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return hdrData;
    }

    public static class HDRParams extends AssetLoaderParameters<HDRData> {
    }
}
