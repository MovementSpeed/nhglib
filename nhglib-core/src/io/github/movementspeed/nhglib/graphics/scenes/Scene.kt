package io.github.movementspeed.nhglib.graphics.scenes

import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.Nhg
import io.github.movementspeed.nhglib.assets.Asset

/**
 * Created by Fausto Napoli on 15/12/2016.
 */
class Scene(nhg: Nhg, rootId: String) {
    var name: String? = null
    var sceneGraph: SceneGraph

    var assets: Array<Asset>

    init {
        sceneGraph = SceneGraph(nhg, rootId)
        assets = Array()
    }
}
