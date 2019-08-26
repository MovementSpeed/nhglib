package io.github.movementspeed.nhglib.assets.loaders

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.model.data.ModelData
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ObjectMap
import io.github.movementspeed.nhglib.assets.Asset
import io.github.movementspeed.nhglib.graphics.shaders.attributes.PBRTextureAttribute

abstract class NhgModelLoader<P : NhgModelLoader.ModelParameters>(resolver: FileHandleResolver) : AsynchronousAssetLoader<Model, P>(resolver) {

    protected var currentAsset: Asset

    protected var items = Array<ObjectMap.Entry<String, ModelData>>()
    protected var defaultParameters = NhgModelLoader.ModelParameters()

    private val dependencies: ArrayMap<ModelMaterial, Array<ModelTexture>>? = null

    /**
     * Directly load the raw model data on the calling thread.
     */
    abstract fun loadModelData(fileHandle: FileHandle, parameters: P?): ModelData?

    /**
     * Directly load the raw model data on the calling thread.
     */
    fun loadModelData(fileHandle: FileHandle): ModelData? {
        return loadModelData(fileHandle, null)
    }

    /**
     * Directly load the model on the calling thread. The model with not be managed by an [AssetManager].
     */
    @JvmOverloads
    fun loadModel(fileHandle: FileHandle, textureProvider: TextureProvider = TextureProvider.FileTextureProvider(), parameters: P? = null): Model? {
        val data = loadModelData(fileHandle, parameters)
        return if (data == null) null else Model(data, textureProvider)
    }

    /**
     * Directly load the model on the calling thread. The model with not be managed by an [AssetManager].
     */
    fun loadModel(fileHandle: FileHandle, parameters: P): Model? {
        return loadModel(fileHandle, TextureProvider.FileTextureProvider(), parameters)
    }

    override fun getDependencies(fileName: String, file: FileHandle, parameters: P?): Array<AssetDescriptor<*>> {
        val deps = Array()
        val data = loadModelData(file, parameters) ?: return deps

        val item = ObjectMap.Entry<String, ModelData>()
        item.key = fileName
        item.value = data

        synchronized(items) {
            items.add(item)
        }

        val textureParameter = parameters?.textureParameter ?: defaultParameters.textureParameter

        for (modelMaterial in data.materials) {
            if (modelMaterial.textures != null) {
                for (modelTexture in modelMaterial.textures) {
                    var fName = modelTexture.fileName

                    if (fName.contains("/")) {
                        fName = fName.substring(fName.lastIndexOf("/") + 1)
                    }

                    textureParameter.genMipMaps = false
                    textureParameter.magFilter = Texture.TextureFilter.Linear
                    textureParameter.minFilter = Texture.TextureFilter.Linear
                    textureParameter.wrapU = Texture.TextureWrap.Repeat
                    textureParameter.wrapV = Texture.TextureWrap.Repeat

                    deps.add(AssetDescriptor(currentAsset.dependenciesPath + fName, Texture::class.java, textureParameter))
                }
            }
        }

        return deps
    }

    override fun loadAsync(manager: AssetManager, fileName: String, file: FileHandle, parameters: P) {}

    override fun loadSync(manager: AssetManager, fileName: String, file: FileHandle, parameters: P): Model? {
        var data: ModelData? = null
        synchronized(items) {
            for (i in 0 until items.size) {
                if (items.get(i).key == fileName) {
                    data = items.get(i).value
                    items.removeIndex(i)
                }
            }
        }
        if (data == null) return null
        val result = Model(data, TextureProvider.AssetTextureProvider(manager))

        convertModelMaterialsToPBR(result)

        // need to remove the textures from the managed disposables, or else ref counting
        // doesn't work!
        val disposables = result.managedDisposables.iterator()
        while (disposables.hasNext()) {
            val disposable = disposables.next()
            if (disposable is Texture) {
                disposables.remove()
            }
        }

        data = null
        return result
    }

    fun setCurrentAsset(asset: Asset) {
        this.currentAsset = asset
    }

    private fun convertModelMaterialsToPBR(model: Model) {
        val materials = model.materials

        for (i in 0 until materials.size) {
            val material = materials.get(i)

            if (material.has(TextureAttribute.Diffuse)) {
                val ta = material.get(TextureAttribute.Diffuse) as TextureAttribute
                val texture = ta.textureDescription.texture
                material.set(PBRTextureAttribute.createAlbedo(texture, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV))
                material.remove(TextureAttribute.Diffuse)
            } else if (material.has(ColorAttribute.Diffuse)) {
                val ca = material.get(ColorAttribute.Diffuse) as ColorAttribute
                val color = ca.color
                material.set(PBRTextureAttribute.createAlbedo(color))
                material.remove(TextureAttribute.Diffuse)
            }

            if (material.has(TextureAttribute.Bump)) {
                val ta = material.get(TextureAttribute.Bump) as TextureAttribute
                val texture = ta.textureDescription.texture
                material.set(PBRTextureAttribute.createNormal(texture, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV))
                material.remove(TextureAttribute.Normal)
                material.remove(TextureAttribute.Bump)
            }

            if (material.has(TextureAttribute.Specular)) {
                val ta = material.get(TextureAttribute.Specular) as TextureAttribute
                val texture = ta.textureDescription.texture
                material.set(PBRTextureAttribute.createRMA(texture, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV))
                material.remove(TextureAttribute.Specular)
            }

            if (material.has(TextureAttribute.Emissive)) {
                val ta = material.get(TextureAttribute.Emissive) as TextureAttribute
                val texture = ta.textureDescription.texture
                material.set(PBRTextureAttribute.createEmissive(texture, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV))
                material.remove(TextureAttribute.Emissive)
            }
        }
    }

    class ModelParameters : AssetLoaderParameters<Model>() {
        var textureParameter: TextureLoader.TextureParameter

        init {
            textureParameter = TextureLoader.TextureParameter()
            textureParameter.minFilter = Texture.TextureFilter.Linear
            textureParameter.magFilter = Texture.TextureFilter.Linear
            textureParameter.wrapV = Texture.TextureWrap.Repeat
            textureParameter.wrapU = textureParameter.wrapV
            textureParameter.genMipMaps = false
        }
    }
}
/**
 * Directly load the model on the calling thread. The model with not be managed by an [AssetManager].
 */
/**
 * Directly load the model on the calling thread. The model with not be managed by an [AssetManager].
 */