package io.github.movementspeed.nhglib.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.movementspeed.nhglib.assets.Asset;
import io.github.movementspeed.nhglib.graphics.geometry.NhgModel;
import io.github.movementspeed.nhglib.graphics.geometry.NhgModelData;
import io.github.movementspeed.nhglib.graphics.geometry.NhgModelMaterial;
import io.github.movementspeed.nhglib.graphics.geometry.NhgModelTexture;

import java.util.Iterator;

public abstract class NhgModelLoader<P extends NhgModelLoader.ModelParameters> extends AsynchronousAssetLoader<NhgModel, P> {
    public NhgModelLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    protected Asset currentAsset;

    protected Array<ObjectMap.Entry<String, NhgModelData>> items = new Array<ObjectMap.Entry<String, NhgModelData>>();
    protected NhgModelLoader.ModelParameters defaultParameters = new NhgModelLoader.ModelParameters();

    private ArrayMap<NhgModelMaterial, Array<NhgModelTexture>> dependencies;

    /**
     * Directly load the raw model data on the calling thread.
     */
    public abstract NhgModelData loadModelData(final FileHandle fileHandle, P parameters);

    /**
     * Directly load the raw model data on the calling thread.
     */
    public NhgModelData loadModelData(final FileHandle fileHandle) {
        return loadModelData(fileHandle, null);
    }

    /**
     * Directly load the model on the calling thread. The model with not be managed by an {@link AssetManager}.
     */
    public NhgModel loadModel(final FileHandle fileHandle, TextureProvider textureProvider, P parameters) {
        final NhgModelData data = loadModelData(fileHandle, parameters);
        return data == null ? null : new NhgModel(data, textureProvider);
    }

    /**
     * Directly load the model on the calling thread. The model with not be managed by an {@link AssetManager}.
     */
    public NhgModel loadModel(final FileHandle fileHandle, P parameters) {
        return loadModel(fileHandle, new TextureProvider.FileTextureProvider(), parameters);
    }

    /**
     * Directly load the model on the calling thread. The model with not be managed by an {@link AssetManager}.
     */
    public NhgModel loadModel(final FileHandle fileHandle, TextureProvider textureProvider) {
        return loadModel(fileHandle, textureProvider, null);
    }

    /**
     * Directly load the model on the calling thread. The model with not be managed by an {@link AssetManager}.
     */
    public NhgModel loadModel(final FileHandle fileHandle) {
        return loadModel(fileHandle, new TextureProvider.FileTextureProvider(), null);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, P parameters) {
        final Array<AssetDescriptor> deps = new Array();
        NhgModelData data = loadModelData(file, parameters);
        if (data == null) return deps;

        ObjectMap.Entry<String, NhgModelData> item = new ObjectMap.Entry<String, NhgModelData>();
        item.key = fileName;
        item.value = data;

        synchronized (items) {
            items.add(item);
        }

        TextureLoader.TextureParameter textureParameter = (parameters != null)
                ? parameters.textureParameter
                : defaultParameters.textureParameter;

        for (final NhgModelMaterial modelMaterial : data.materials) {
            if (modelMaterial.textures != null) {
                for (final NhgModelTexture modelTexture : modelMaterial.textures) {
                    String fName = modelTexture.fileName;

                    if (fName.contains("/")) {
                        fName = fName.substring(fName.lastIndexOf("/") + 1);
                    }

                    textureParameter.genMipMaps = false;
                    textureParameter.magFilter = Texture.TextureFilter.Linear;
                    textureParameter.minFilter = Texture.TextureFilter.Linear;
                    textureParameter.wrapU = Texture.TextureWrap.Repeat;
                    textureParameter.wrapV = Texture.TextureWrap.Repeat;

                    deps.add(new AssetDescriptor(currentAsset.dependenciesPath + fName, Texture.class, textureParameter));
                }
            }
        }

        return deps;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, P parameters) {
    }

    @Override
    public NhgModel loadSync(AssetManager manager, String fileName, FileHandle file, P parameters) {
        NhgModelData data = null;
        synchronized (items) {
            for (int i = 0; i < items.size; i++) {
                if (items.get(i).key.equals(fileName)) {
                    data = items.get(i).value;
                    items.removeIndex(i);
                }
            }
        }
        if (data == null) return null;
        final NhgModel result = new NhgModel(data, new TextureProvider.AssetTextureProvider(manager));
        // need to remove the textures from the managed disposables, or else ref counting
        // doesn't work!
        Iterator<Disposable> disposables = result.getManagedDisposables().iterator();
        while (disposables.hasNext()) {
            Disposable disposable = disposables.next();
            if (disposable instanceof Texture) {
                disposables.remove();
            }
        }

        data = null;
        return result;
    }

    public void setCurrentAsset(Asset asset) {
        this.currentAsset = asset;
    }

    static public class ModelParameters extends AssetLoaderParameters<NhgModel> {
        public TextureLoader.TextureParameter textureParameter;

        public ModelParameters() {
            textureParameter = new TextureLoader.TextureParameter();
            textureParameter.minFilter = textureParameter.magFilter = Texture.TextureFilter.Linear;
            textureParameter.wrapU = textureParameter.wrapV = Texture.TextureWrap.Repeat;
        }
    }
}