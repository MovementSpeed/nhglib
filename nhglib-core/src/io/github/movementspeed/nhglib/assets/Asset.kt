package io.github.movementspeed.nhglib.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.utils.GdxRuntimeException
import io.github.movementspeed.nhglib.utils.data.Bundle
import io.github.movementspeed.nhglib.utils.data.Strings

/**
 * Created by Fausto Napoli on 25/10/2016.
 */
class Asset(alias: String?, var source: String?, var assetClass: Class<*>) {
    val alias: String
    var dependenciesPath: String
    var parametersBundle: Bundle? = null
    var parameters: AssetLoaderParameters<*>

    constructor(asset: Asset) : this(asset.alias, asset.source, asset.assetClass) {}

    constructor(alias: String, source: String, assetClass: Class<*>, parameters: AssetLoaderParameters<*>) : this(alias, source, assetClass) {
        this.parameters = parameters
    }

    init {
        if (alias != null) {
            this.alias = alias
        } else if (source != null) {
            this.alias = source
        } else {
            throw NullPointerException(Strings.Messages.nullAssetSource)
        }

        if (source == null || source!!.isEmpty()) {
            throw GdxRuntimeException(Strings.Messages.nullOrEmptyAssetSource)
        }

        if (source!!.contains("/")) {
            this.dependenciesPath = source!!.substring(0, source!!.lastIndexOf("/") + 1)
        }
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) return true

        if (o !is Asset) {
            return false
        }

        val asset = o as Asset?
        return alias.contentEquals(asset!!.alias)
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 991 * result + alias.hashCode()
        result = 991 * result + source.hashCode()
        result = 991 * result + dependenciesPath.hashCode()
        return result
    }

    fun setDependenciesPath(dependenciesPath: String?) {
        if (dependenciesPath != null && !dependenciesPath.isEmpty()) {
            this.dependenciesPath = dependenciesPath
        }
    }

    fun `is`(alias: String?): Boolean {
        return alias != null && this.alias.contentEquals(alias)
    }

    fun `is`(asset: Asset?): Boolean {
        return asset != null && this.alias.contentEquals(asset.alias)
    }

    fun isType(assetClass: Class<*>?): Boolean {
        return assetClass != null && this.assetClass == assetClass
    }
}
