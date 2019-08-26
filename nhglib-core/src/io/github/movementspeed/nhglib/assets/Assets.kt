package io.github.movementspeed.nhglib.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetErrorListener
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.utils.*
import io.github.movementspeed.nhglib.Nhg
import io.github.movementspeed.nhglib.assets.loaders.*
import io.github.movementspeed.nhglib.core.fsm.base.AssetsStates
import io.github.movementspeed.nhglib.core.messaging.Message
import io.github.movementspeed.nhglib.files.HDRData
import io.github.movementspeed.nhglib.graphics.scenes.Scene
import io.github.movementspeed.nhglib.input.handler.InputProxy
import io.github.movementspeed.nhglib.interfaces.Updatable
import io.github.movementspeed.nhglib.utils.data.Bundle
import io.github.movementspeed.nhglib.utils.data.Strings
import io.github.movementspeed.nhglib.utils.debug.NhgLogger
import io.reactivex.functions.Consumer

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
class Assets : Updatable, AssetErrorListener {
    var fsm: DefaultStateMachine<Assets, AssetsStates>
    var assetManager: AssetManager? = null
    var syncAssetManager: AssetManager? = null

    private var nhg: Nhg? = null

    var assetQueue: Array<Asset>? = null
        private set
    private var assetCache: ArrayMap<String, Asset>? = null
    private var customAssetManagers: ArrayMap<String, AssetManager>? = null

    val cachedAssets: ArrayMap.Values<Asset>
        get() = assetCache!!.values()

    fun init(nhg: Nhg) {
        this.nhg = nhg
        fsm = DefaultStateMachine(this, AssetsStates.IDLE)

        assetQueue = Array<Asset>()
        assetCache = ArrayMap()
        customAssetManagers = ArrayMap()

        assetManager = AssetManager()
        syncAssetManager = AssetManager()

        setDefaultAssetLoaders(assetManager!!)
        setDefaultAssetLoaders(syncAssetManager!!)

        assetManager!!.setErrorListener(this)
        syncAssetManager!!.setErrorListener(this)

        Texture.setAssetManager(assetManager)
    }

    // Updatable
    override fun update() {
        fsm.update()
    }

    // AssetErrorListener
    override fun error(asset: AssetDescriptor<*>, throwable: Throwable) {
        try {
            throw throwable
        } catch (throwable1: Throwable) {
            throwable1.printStackTrace()
        }

    }

    fun addAssetManager(name: String, fileHandleResolver: FileHandleResolver): AssetManager {
        val assetManager = AssetManager(fileHandleResolver)
        setDefaultAssetLoaders(assetManager)
        customAssetManagers!!.put(name, assetManager)
        return assetManager
    }

    fun assetLoadingFinished() {
        nhg!!.messaging.send(Message(Strings.Events.assetLoadingFinished))
    }

    fun assetLoaded(asset: Asset?) {
        if (asset != null) {
            val bundle = Bundle()
            bundle[Strings.Defaults.assetKey] = asset

            nhg!!.messaging.send(Message(Strings.Events.assetLoaded, bundle))
        }
    }

    fun assetUnloaded(asset: Asset?) {
        if (asset != null) {
            val bundle = Bundle()
            bundle[Strings.Defaults.assetKey] = asset

            nhg!!.messaging.send(Message(Strings.Events.assetUnloaded, bundle))
        }
    }

    fun updateAssetManagers(): Boolean {
        var res = true

        for (entry in customAssetManagers!!) {
            if (entry.value != null && !entry.value.update()) {
                res = false
                break
            }
        }

        if (assetManager != null) {
            res = res && assetManager!!.update()
        }

        return res
    }

    fun isAssetLoaded(asset: Asset): Boolean {
        var res = false

        for (entry in customAssetManagers!!) {
            if (entry.value.isLoaded(asset.source)) {
                res = true
                break
            }
        }

        return assetManager!!.isLoaded(asset.source) || res
    }

    fun getAssetManager(name: String): AssetManager {
        return customAssetManagers!!.get(name)
    }

    operator fun <T> get(alias: String?): T? {
        var t: T? = null

        if (alias != null) {
            t = get<T>(assetCache!!.get(alias))
        }

        return t
    }

    operator fun <T> get(asset: Asset?): T? {
        var t: T? = null

        if (asset != null && isAssetLoaded(asset)) {
            if (assetManager!!.isLoaded(asset.source)) {
                t = assetManager!!.get<T>(asset.source)
            }

            if (t == null) {
                for (entry in customAssetManagers!!) {
                    if (entry.value.isLoaded(asset.source)) {
                        t = entry.value.get<T>(asset.source)
                    }

                    if (t != null) {
                        break
                    }
                }
            }
        }

        return t
    }

    fun getAsset(alias: String?): Asset? {
        return if (alias == null) null else assetCache!!.get(alias)
    }

    fun createAsset(alias: String, source: String, assetClass: Class<*>): Asset {
        val asset = Asset(alias, source, assetClass)
        assetCache!!.put(alias, asset)
        return asset
    }

    /**
     * Loads an asset with a direct callback.
     *
     * @param asset    the asset.
     * @param listener a listener for the asset loading.
     */
    fun loadAsset(asset: Asset?, listener: AssetListener?) {
        if (asset != null) {
            queueAsset(asset)

            nhg!!.messaging.get(Strings.Events.assetLoaded)
                    .subscribe { message ->
                        val loadedAsset = message.data!![Strings.Defaults.assetKey] as Asset

                        if (loadedAsset.`is`(asset)) {
                            listener?.onAssetLoaded(asset)
                        }
                    }
        }
    }

    /**
     * Loads an asset with a direct callback.
     *
     * @param asset    the asset.
     * @param listener a listener for the asset loading.
     */
    fun loadAsset(assetManagerName: String, asset: Asset?, listener: AssetListener?) {
        if (asset != null) {
            queueAsset(assetManagerName, asset)

            nhg!!.messaging.get(Strings.Events.assetLoaded)
                    .subscribe { message ->
                        val loadedAsset = message.data!![Strings.Defaults.assetKey] as Asset

                        if (loadedAsset.`is`(asset)) {
                            listener?.onAssetLoaded(asset)
                        }
                    }
        }
    }

    /**
     * Loads an asset in an asynchronous way.
     *
     * @param asset the asset.
     */
    fun queueAsset(asset: Asset?) {
        if (asset != null) {
            assetCache!!.put(asset.alias, asset)

            if (!assetManager!!.isLoaded(asset.source)) {
                val fileHandle = Gdx.files.internal(asset.source)

                if (fileHandle.exists()) {
                    buildAssetParameters(asset)

                    if (asset.parameters == null) {
                        assetManager!!.load(asset.source, asset.assetClass)
                    } else {
                        assetManager!!.load(asset.source, asset.assetClass, asset.parameters)
                    }

                    assetQueue!!.add(asset)
                } else {
                    NhgLogger.log(this, Strings.Messages.cannotQueueAssetFileNotFound, asset.source)
                }
            } else {
                assetLoaded(asset)
            }
        }
    }

    fun queueAsset(assetManagerName: String?, asset: Asset?) {
        if (asset != null && assetManagerName != null) {
            val assetManager = customAssetManagers!!.get(assetManagerName)

            if (assetManager != null) {
                assetCache!!.put(asset.alias, asset)

                if (!assetManager.isLoaded(asset.source)) {
                    val fileHandle = assetManager.fileHandleResolver.resolve(asset.source)

                    if (fileHandle.exists()) {
                        buildAssetParameters(asset)

                        if (asset.parameters == null) {
                            assetManager.load(asset.source, asset.assetClass)
                        } else {
                            assetManager.load(asset.source, asset.assetClass, asset.parameters)
                        }

                        assetQueue!!.add(asset)
                    } else {
                        NhgLogger.log(this, Strings.Messages.cannotQueueAssetFileNotFound, asset.source)
                    }
                } else {
                    assetLoaded(asset)
                }
            }
        }
    }

    fun queueAssets(assets: Array<Asset>?) {
        if (assets != null) {
            for (asset in assets) {
                queueAsset(asset)
            }
        }
    }

    fun queueAssets(assetManagerName: String, assets: Array<Asset>?) {
        if (assets != null) {
            for (asset in assets) {
                queueAsset(assetManagerName, asset)
            }
        }
    }

    fun queueAssetPackage(assetPackage: AssetPackage?) {
        if (assetPackage != null) {
            queueAssets(assetPackage.assets)
            nhg!!.messaging.get(Strings.Events.assetLoaded)
                    .subscribe { message ->
                        val asset = message.data!![Strings.Defaults.assetKey] as Asset
                        if (assetPackage.containsAsset(asset.alias)) {
                            if (assetPackage.decreaseAndCheckRemaining()) {
                                val bundle = Bundle()
                                bundle[Strings.Defaults.assetPackageKey] = assetPackage

                                nhg!!.messaging.send(Message(Strings.Events.assetPackageLoaded, bundle))
                            }
                        }
                    }
        }
    }

    fun queueAssetPackage(assetManagerName: String, assetPackage: AssetPackage?) {
        if (assetPackage != null) {
            queueAssets(assetManagerName, assetPackage.assets)

            nhg!!.messaging.get(Strings.Events.assetLoaded)
                    .subscribe { message ->
                        val asset = message.data!![Strings.Defaults.assetKey] as Asset

                        if (assetPackage.containsAsset(asset.alias)) {
                            if (assetPackage.decreaseAndCheckRemaining()) {
                                val bundle = Bundle()
                                bundle[Strings.Defaults.assetPackageKey] = assetPackage

                                nhg!!.messaging.send(Message(Strings.Events.assetPackageLoaded, bundle))
                            }
                        }
                    }
        }
    }

    fun <T> loadAssetSync(asset: Asset?): T? {
        var t: T? = null

        if (asset != null) {
            assetCache!!.put(asset.alias, asset)

            if (!syncAssetManager!!.isLoaded(asset.source)) {
                val fileHandle = Gdx.files.internal(asset.source)

                if (fileHandle.exists()) {
                    if (asset.parameters == null) {
                        syncAssetManager!!.load(asset.source, asset.assetClass)
                    } else {
                        syncAssetManager!!.load(asset.source, asset.assetClass, asset.parameters)
                    }

                    syncAssetManager!!.finishLoading()
                    t = syncAssetManager!!.get<T>(asset.source)
                } else {
                    NhgLogger.log(this, Strings.Messages.cannotQueueAssetFileNotFound, asset.source)
                }
            } else {
                t = syncAssetManager!!.get<T>(asset.source)
            }
        }

        return t
    }

    fun <T> loadAssetSync(assetManagerName: String?, asset: Asset?): T? {
        var t: T? = null

        if (asset != null && assetManagerName != null) {
            val syncAssetManager = customAssetManagers!!.get(assetManagerName)

            if (syncAssetManager != null) {
                assetCache!!.put(asset.alias, asset)

                if (!syncAssetManager.isLoaded(asset.source)) {
                    val fileHandle = syncAssetManager.fileHandleResolver.resolve(asset.source)

                    if (fileHandle.exists()) {
                        if (asset.parameters == null) {
                            syncAssetManager.load(asset.source, asset.assetClass)
                        } else {
                            syncAssetManager.load(asset.source, asset.assetClass, asset.parameters)
                        }

                        syncAssetManager.finishLoading()
                        t = syncAssetManager.get<T>(asset.source)
                    } else {
                        NhgLogger.log(this, Strings.Messages.cannotQueueAssetFileNotFound, asset.source)
                    }
                } else {
                    t = syncAssetManager.get<T>(asset.source)
                }
            }
        }

        return t
    }

    fun dequeueAsset(asset: Asset?) {
        if (asset != null) {
            assetQueue!!.removeValue(asset, true)
        }
    }

    fun unloadAsset(alias: String?) {
        if (alias != null && !alias.isEmpty()) {
            unloadAsset(getAsset(alias))
        }
    }

    fun unloadAsset(asset: Asset?) {
        if (asset != null) {
            if (assetManager!!.isLoaded(asset.source)) {
                assetManager!!.unload(asset.source)
                assetUnloaded(asset)
            }
        }
    }

    fun clearCompleted() {
        for (i in 0 until assetQueue!!.size) {
            val asset = assetQueue!![i]

            if (assetManager!!.isLoaded(asset.source)) {
                assetQueue!!.removeValue(asset, true)
            }
        }
    }

    fun clearQueue() {
        assetQueue!!.clear()
    }

    fun dispose() {
        if (assetManager != null) {
            assetManager!!.dispose()
            assetManager = null
        }

        if (syncAssetManager != null) {
            syncAssetManager!!.dispose()
            syncAssetManager = null
        }

        for (entry in customAssetManagers!!) {
            if (entry.value != null) {
                entry.value.dispose()
            }
        }

        customAssetManagers!!.clear()
    }

    fun assetInQueue(asset: Asset?): Boolean {
        return asset != null && assetQueue!!.contains(asset, true)
    }

    private fun buildAssetParameters(asset: Asset) {
        if (asset.parametersBundle != null) {
            if (asset.assetClass == Texture::class.java) {
                val textureParameter = TextureLoader.TextureParameter()

                textureParameter.genMipMaps = asset.parametersBundle!!.getBoolean("genMipMaps", false)
                textureParameter.magFilter = Texture.TextureFilter.Linear
                textureParameter.minFilter = Texture.TextureFilter.Linear
                textureParameter.wrapU = Texture.TextureWrap.Repeat
                textureParameter.wrapV = Texture.TextureWrap.Repeat

                asset.parameters = textureParameter
            }
        }
    }

    private fun setDefaultAssetLoaders(assetManager: AssetManager) {
        val resolver = assetManager.fileHandleResolver

        val sceneLoader = SceneLoader(nhg, resolver)
        val inputLoader = InputLoader(resolver)
        val jsonLoader = JsonLoader(resolver)
        val hdrLoader = HDRLoader(resolver)
        val nhgG3dModelLoader = NhgG3dModelLoader(this, UBJsonReader(), resolver)

        assetManager.setLoader<Scene, SceneParameter>(Scene::class.java, sceneLoader)
        assetManager.setLoader<InputProxy, InputProxyParameter>(InputProxy::class.java, inputLoader)
        assetManager.setLoader<JsonValue, JsonParameter>(JsonValue::class.java, jsonLoader)
        assetManager.setLoader<HDRData, HDRParams>(HDRData::class.java, hdrLoader)
        assetManager.setLoader<Model, ModelParameters>(Model::class.java, ".g3db", nhgG3dModelLoader)
    }

    interface AssetListener {
        fun onAssetLoaded(asset: Asset?)
    }
}
