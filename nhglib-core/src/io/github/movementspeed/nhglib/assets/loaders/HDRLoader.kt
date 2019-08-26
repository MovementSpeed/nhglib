package io.github.movementspeed.nhglib.assets.loaders

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.files.HDRData

import java.io.IOException

/**
 * Created by Fausto Napoli on 20/08/2017.
 */
class HDRLoader(fileHandleResolver: FileHandleResolver) : AsynchronousAssetLoader<HDRData, HDRLoader.HDRParams>(fileHandleResolver) {

    override fun loadAsync(manager: AssetManager, fileName: String, file: FileHandle, parameter: HDRParams) {

    }

    override fun loadSync(manager: AssetManager, fileName: String, file: FileHandle, parameter: HDRParams): HDRData? {
        return loadHDR(file)
    }

    override fun getDependencies(fileName: String, file: FileHandle, parameter: HDRParams): Array<AssetDescriptor<*>>? {
        return null
    }

    fun loadHDR(file: FileHandle): HDRData? {
        var hdrData: HDRData? = null

        try {
            hdrData = HDRData(file.readBytes())
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return hdrData
    }

    class HDRParams : AssetLoaderParameters<HDRData>()
}
