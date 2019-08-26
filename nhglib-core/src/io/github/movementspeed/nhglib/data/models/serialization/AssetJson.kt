package io.github.movementspeed.nhglib.data.models.serialization

import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.assets.Asset
import io.github.movementspeed.nhglib.interfaces.JsonParseable
import io.github.movementspeed.nhglib.utils.data.Bundle
import io.github.movementspeed.nhglib.utils.scenes.SceneMappings

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
class AssetJson : JsonParseable<Asset> {
    private var output: Asset? = null

    override fun parse(jsonValue: JsonValue) {
        val alias = jsonValue.getString("alias")
        val source = jsonValue.getString("source")
        val classString = jsonValue.getString("classAlias")

        var dependenciesPath = jsonValue.getString(
                "dependenciesPath",
                getDefaultDependenciesPath(source))

        if (!dependenciesPath.endsWith("/")) {
            dependenciesPath += "/"
        }

        val assetClass = SceneMappings.assetClassFromAlias(classString)

        val parameters = jsonValue.get("parameters")

        output = Asset(alias, source, assetClass)
        output!!.dependenciesPath = dependenciesPath
        output!!.parametersBundle = Bundle()

        if (parameters != null) {
            for (value in parameters) {
                val internalValue = value.get(0)

                if (internalValue.isBoolean) {
                    output!!.parametersBundle!![internalValue.name] = internalValue.asBoolean()
                } else if (internalValue.isDouble) {
                    output!!.parametersBundle!![internalValue.name] = internalValue.asDouble()
                } else if (internalValue.isLong) {
                    output!!.parametersBundle!![internalValue.name] = internalValue.asLong()
                } else if (internalValue.isString) {
                    output!!.parametersBundle!![internalValue.name] = internalValue.asString()
                }
            }
        }
    }

    override fun get(): Asset? {
        return output
    }

    private fun getDefaultDependenciesPath(sourcePath: String): String {
        var res = sourcePath

        if (sourcePath.contains("/")) {
            res = sourcePath.substring(0, sourcePath.lastIndexOf("/"))
        }

        return res
    }
}
