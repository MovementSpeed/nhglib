package io.github.movementspeed.nhglib.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.movementspeed.nhglib.assets.Asset;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.PBRTextureAttribute;

import java.util.Iterator;

public abstract class NhgModelLoader<P extends NhgModelLoader.ModelParameters> extends AsynchronousAssetLoader<Model, P> {
    public NhgModelLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    protected Asset currentAsset;

    protected Array<ObjectMap.Entry<String, ModelData>> items = new Array<ObjectMap.Entry<String, ModelData>>();
    protected NhgModelLoader.ModelParameters defaultParameters = new NhgModelLoader.ModelParameters();

    private ArrayMap<ModelMaterial, Array<ModelTexture>> dependencies;

    /**
     * Directly load the raw model data on the calling thread.
     */
    public abstract ModelData loadModelData(final FileHandle fileHandle, P parameters);

    /**
     * Directly load the raw model data on the calling thread.
     */
    public ModelData loadModelData(final FileHandle fileHandle) {
        return loadModelData(fileHandle, null);
    }

    /**
     * Directly load the model on the calling thread. The model with not be managed by an {@link AssetManager}.
     */
    public Model loadModel(final FileHandle fileHandle, TextureProvider textureProvider, P parameters) {
        final ModelData data = loadModelData(fileHandle, parameters);
        return data == null ? null : new Model(data, textureProvider);
    }

    /**
     * Directly load the model on the calling thread. The model with not be managed by an {@link AssetManager}.
     */
    public Model loadModel(final FileHandle fileHandle, P parameters) {
        return loadModel(fileHandle, new TextureProvider.FileTextureProvider(), parameters);
    }

    /**
     * Directly load the model on the calling thread. The model with not be managed by an {@link AssetManager}.
     */
    public Model loadModel(final FileHandle fileHandle, TextureProvider textureProvider) {
        return loadModel(fileHandle, textureProvider, null);
    }

    /**
     * Directly load the model on the calling thread. The model with not be managed by an {@link AssetManager}.
     */
    public Model loadModel(final FileHandle fileHandle) {
        return loadModel(fileHandle, new TextureProvider.FileTextureProvider(), null);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, P parameters) {
        final Array<AssetDescriptor> deps = new Array();
        ModelData data = loadModelData(file, parameters);
        if (data == null) return deps;

        ObjectMap.Entry<String, ModelData> item = new ObjectMap.Entry<String, ModelData>();
        item.key = fileName;
        item.value = data;

        synchronized (items) {
            items.add(item);
        }

        TextureLoader.TextureParameter textureParameter = (parameters != null)
                ? parameters.textureParameter
                : defaultParameters.textureParameter;

        for (final ModelMaterial modelMaterial : data.materials) {
            if (modelMaterial.textures != null) {
                for (final ModelTexture modelTexture : modelMaterial.textures) {
                    String fName = modelTexture.fileName;

                    if (fName.contains("/")) {
                        fName = fName.substring(fName.lastIndexOf("/") + 1);
                    }

                    textureParameter.genMipMaps = true;
                    textureParameter.magFilter = Texture.TextureFilter.Linear;
                    textureParameter.minFilter = Texture.TextureFilter.MipMapLinearLinear;
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
    public Model loadSync(AssetManager manager, String fileName, FileHandle file, P parameters) {
        ModelData data = null;
        synchronized (items) {
            for (int i = 0; i < items.size; i++) {
                if (items.get(i).key.equals(fileName)) {
                    data = items.get(i).value;
                    items.removeIndex(i);
                }
            }
        }
        if (data == null) return null;
        final Model result = new Model(data, new TextureProvider.AssetTextureProvider(manager));

        convertModelMaterialsToPBR(result);

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

    private void convertModelMaterialsToPBR(Model model) {
        Array<Material> materials = model.materials;

        for (int i = 0; i < materials.size; i++) {
            Material material = materials.get(i);

            if (material.has(TextureAttribute.Diffuse)) {
                TextureAttribute ta = (TextureAttribute) material.get(TextureAttribute.Diffuse);
                Texture texture = ta.textureDescription.texture;
                material.set(PBRTextureAttribute.createAlbedo(texture, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV));
                material.remove(TextureAttribute.Diffuse);
            } else if (material.has(ColorAttribute.Diffuse)) {
                ColorAttribute ca = (ColorAttribute) material.get(ColorAttribute.Diffuse);
                Color color = ca.color;
                material.set(PBRTextureAttribute.createAlbedo(color));
                material.remove(TextureAttribute.Diffuse);
            }

            if (material.has(TextureAttribute.Bump)) {
                TextureAttribute ta = (TextureAttribute) material.get(TextureAttribute.Bump);
                Texture texture = ta.textureDescription.texture;
                material.set(PBRTextureAttribute.createNormal(texture, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV));
                material.remove(TextureAttribute.Bump);
            }

            if (material.has(TextureAttribute.Specular)) {
                TextureAttribute ta = (TextureAttribute) material.get(TextureAttribute.Specular);
                Texture texture = ta.textureDescription.texture;
                material.set(PBRTextureAttribute.createRMA(texture, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV));
                material.remove(TextureAttribute.Specular);
            }

            if (material.has(TextureAttribute.Emissive)) {
                TextureAttribute ta = (TextureAttribute) material.get(TextureAttribute.Emissive);
                Texture texture = ta.textureDescription.texture;
                material.set(PBRTextureAttribute.createEmissive(texture, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV));
                material.remove(TextureAttribute.Emissive);
            }
        }
    }

    static public class ModelParameters extends AssetLoaderParameters<Model> {
        public TextureLoader.TextureParameter textureParameter;

        public ModelParameters() {
            textureParameter = new TextureLoader.TextureParameter();
            textureParameter.minFilter = Texture.TextureFilter.MipMapLinearLinear;
            textureParameter.magFilter = Texture.TextureFilter.Linear;
            textureParameter.wrapU = textureParameter.wrapV = Texture.TextureWrap.Repeat;
            textureParameter.genMipMaps = true;
        }
    }
}