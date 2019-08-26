package io.github.movementspeed.nhglib.assets.loaders

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import io.github.movementspeed.nhglib.Nhg
import io.github.movementspeed.nhglib.data.models.serialization.SceneJson
import io.github.movementspeed.nhglib.graphics.scenes.Scene

import java.io.UnsupportedEncodingException

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
class SceneLoader(private val nhg: Nhg, resolver: FileHandleResolver) : AsynchronousAssetLoader<Scene, SceneLoader.SceneParameter>(resolver) {

    override fun loadAsync(manager: AssetManager, fileName: String, file: FileHandle, parameter: SceneParameter) {}

    override fun loadSync(manager: AssetManager, fileName: String, file: FileHandle, parameter: SceneParameter): Scene? {
        return getScene(file.readBytes())
    }

    override fun getDependencies(fileName: String, file: FileHandle, parameter: SceneParameter): Array<AssetDescriptor<*>>? {
        return null
    }

    private fun getScene(bytes: ByteArray): Scene? {
        var scene: Scene? = null

        try {
            val json = String(bytes, "UTF-8")

            val sceneJson = SceneJson(nhg)
            sceneJson.parse(JsonReader().parse(json).get("scene"))

            scene = sceneJson.get()
        } catch (e: UnsupportedEncodingException) {
            if (Nhg.debugLogs) e.printStackTrace()
        }

        return scene
    }

    class SceneParameter : AssetLoaderParameters<Scene>()
}
