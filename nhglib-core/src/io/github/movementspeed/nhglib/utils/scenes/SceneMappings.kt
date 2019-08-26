package io.github.movementspeed.nhglib.utils.scenes

import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson

import kotlin.collections.HashMap
import kotlin.reflect.KClass

/**
 * Created by Fausto Napoli on 20/12/2016.
 */
object SceneMappings {
    private val assetClassesMapping = HashMap<String, KClass<*>>()
    private val componentJsonClassesMapping = HashMap<String, KClass<out ComponentJson>>()

    fun addAssetClassMapping(type: String, assetClass: KClass<*>) {
        assetClassesMapping[type] = assetClass
    }

    fun addComponentJsonMapping(type: String, componentClass: KClass<out ComponentJson>) {
        componentJsonClassesMapping[type] = componentClass
    }

    fun assetClassFromAlias(alias: String): KClass<*>? {
        return assetClassesMapping[alias]
    }

    fun componentJsonFromType(type: String): ComponentJson? {
        var componentJson: ComponentJson? = null

        try {
            val componentJsonClass = componentJsonClassFromType(type)
            componentJson = componentJsonClass?.objectInstance
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        return componentJson
    }

    private fun componentJsonClassFromType(type: String): KClass<out ComponentJson>? {
        return componentJsonClassesMapping[type]
    }
}
