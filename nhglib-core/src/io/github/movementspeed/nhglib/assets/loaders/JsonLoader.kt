package io.github.movementspeed.nhglib.assets.loaders

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
class JsonLoader(resolver: FileHandleResolver) : AsynchronousAssetLoader<JsonValue, JsonLoader.JsonParameter>(resolver) {

    override fun loadAsync(manager: AssetManager, fileName: String, file: FileHandle, parameter: JsonParameter) {}

    override fun loadSync(manager: AssetManager, fileName: String, file: FileHandle, parameter: JsonParameter): JsonValue {
        return JsonReader().parse(file)
    }

    override fun getDependencies(fileName: String, file: FileHandle, parameter: JsonParameter): Array<AssetDescriptor<*>>? {
        return null
    }

    class JsonParameter : AssetLoaderParameters<JsonValue>()
}
