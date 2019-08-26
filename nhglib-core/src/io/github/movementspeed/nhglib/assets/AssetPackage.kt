package io.github.movementspeed.nhglib.assets

import com.badlogic.gdx.utils.Array

class AssetPackage(val alias: String) {

    private var remainingAssets: Int = 0
    val assets: Array<Asset>

    init {
        remainingAssets = 0
        assets = Array()
    }

    fun addAsset(asset: Asset) {
        assets.add(asset)
        remainingAssets++
    }

    fun addAsset(alias: String, source: String, assetClass: Class<*>) {
        addAsset(Asset(alias, source, assetClass))
    }

    fun addAssets(vararg assets: Asset) {
        for (asset in assets) {
            addAsset(asset)
        }
    }

    fun addAssets(assets: Array<Asset>) {
        for (asset in assets) {
            addAsset(asset)
        }
    }

    fun decreaseAndCheckRemaining(): Boolean {
        remainingAssets--
        return remainingAssets == 0
    }

    fun containsAsset(alias: String): Boolean {
        var contained = false

        for (asset in assets) {
            if (asset.`is`(alias)) {
                contained = true
                break
            }
        }

        return contained
    }

    fun `is`(alias: String?): Boolean {
        return alias != null && this.alias.contentEquals(alias)
    }

    fun `is`(assetPackage: AssetPackage?): Boolean {
        return assetPackage != null && this.alias.contentEquals(assetPackage.alias)
    }
}
